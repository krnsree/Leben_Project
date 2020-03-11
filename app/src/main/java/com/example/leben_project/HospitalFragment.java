package com.example.leben_project;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class HospitalFragment extends Fragment {

    @BindView(R.id.hospitalList)
    RecyclerView hospitalList;

    @BindView(R.id.hrshimmer)
    ShimmerFrameLayout hrshimmer;

    @BindView(R.id.locationClick)
    LinearLayout lc;

    @BindView(R.id.searchplate)
    LinearLayout sp;

    @BindView(R.id.locText)
    TextView locText;

    @BindView(R.id.noneHos)
    TextView none;

    @BindView(R.id.placesearch)
    Button search;

    @BindView(R.id.district)
    EditText TDistrict;

    @BindView(R.id.setLocation)
    Button setLocation;

    @BindView(R.id.hostoolbar)
    Toolbar toolbar;

    FirebaseFirestore ref;

    static ArrayList<RVCell> hospitalLists = new ArrayList<>();

    MCAdapter hospitalAdapter;
    private String TAG = "TAG";

    static boolean isDataAvailable;
    boolean isVisible;
    boolean hasSearched;

    Location currentLocation;
    static FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;

    public HospitalFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hospital, container, false);
        ButterKnife.bind(this, view);
        getActivity().findViewById(R.id.home).setVisibility(View.GONE);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        isDataAvailable = false;

        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
            }
        }

        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);

        hospitalList.setHasFixedSize(true);
        hospitalAdapter = new MCAdapter(hospitalLists, getContext(), getActivity().getSupportFragmentManager(), getActivity());
        hospitalList.setAdapter(hospitalAdapter);

        lc.setOnClickListener(v -> {
            if (isVisible) {
                sp.setVisibility(View.GONE);
                isVisible = false;
            } else {
                sp.setVisibility(View.VISIBLE);
                isVisible = true;
            }
        });

        search.setOnClickListener(v -> {
            if (!TDistrict.getText().toString().isEmpty()) {
                hasSearched = true;
                hrshimmer.startShimmerAnimation();
                hrshimmer.setVisibility(View.VISIBLE);
                locText.setText(TDistrict.getText().toString());
                sp.setVisibility(View.GONE);
                getItems(TDistrict.getText().toString());
            } else {
                Toast.makeText(getContext(), "Please enter the field", Toast.LENGTH_SHORT).show();
            }
        });

        setLocation.setOnClickListener(v -> fetchLastLocation());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        fetchLastLocation();
        return view;
    }


    private void fetchLastLocation() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();

        task.addOnSuccessListener(location -> {

            if (task != null) {
                currentLocation = location;
                Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = gcd.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                    if (addresses.size() > 0) {
                        hrshimmer.startShimmerAnimation();
                        hrshimmer.setVisibility(View.VISIBLE);
                        sp.setVisibility(View.GONE);
                        locText.setText((CharSequence) addresses.get(0).getSubAdminArea());
                        getItems(addresses.get(0).getSubAdminArea());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void getItems(String District) {

        hrshimmer.startShimmerAnimation();
        if (!hasSearched) {
            if (hospitalLists != null && hospitalLists.size() > 0) {
                Log.e(TAG, "onItems: 1");
                // hospitalAdapter.notifyDataSetChanged();
                isDataAvailable = true;
                hrshimmer.stopShimmerAnimation();
                hrshimmer.setVisibility(View.GONE);
                hospitalList.setAdapter(hospitalAdapter);
                return;
            }
        }

        Log.e(TAG, "onItems: " + District);

        ref = FirebaseFirestore.getInstance();

        ref.collection("MedicalCenters")
                .whereEqualTo("location", District.trim())
                .whereEqualTo("type", "hospital")
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        if (!task.getResult().isEmpty()) {

                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                if (documentSnapshot.exists()) {
                                    RVCell rvcell = new RVCell();
                                    rvcell.setName(String.valueOf(documentSnapshot.getData().get("name")));
                                    rvcell.setAddress(String.valueOf(documentSnapshot.getData().get("address")));
                                    rvcell.setLocation(String.valueOf(documentSnapshot.getData().get("location")));
                                    rvcell.setTime(String.valueOf(documentSnapshot.getData().get("time")));
                                    rvcell.setPhno(String.valueOf(documentSnapshot.getData().get("phno")));
                                    rvcell.setHomeurl(String.valueOf(documentSnapshot.getData().get("homeurl")));
                                    rvcell.setUid(String.valueOf(documentSnapshot.getData().get("uid")));
                                    Log.e(TAG, "UID: " + rvcell.getUid());
                                    if (documentSnapshot.getData().get("services") != null)
                                        rvcell.setServices((ArrayList<String>) documentSnapshot.getData().get("services"));
                                    if (documentSnapshot.getData().get("dept") != null)
                                        rvcell.setDept((ArrayList<String>) documentSnapshot.getData().get("dept"));
                                    hospitalLists.add(rvcell);
                                    rvcell.setLatitude(String.valueOf(documentSnapshot.getData().get("lat")));
                                    rvcell.setLongitude(String.valueOf(documentSnapshot.getData().get("lon")));
                                }

                            }
                            hospitalAdapter.notifyDataSetChanged();
                            hrshimmer.stopShimmerAnimation();
                            hrshimmer.setVisibility(View.GONE);
                            hospitalList.setVisibility(View.VISIBLE);
                            none.setVisibility(View.GONE);

                        } else {
                            none.setVisibility(View.VISIBLE);
                            hospitalList.setVisibility(View.GONE);
                            hospitalAdapter.notifyDataSetChanged();
                            hrshimmer.stopShimmerAnimation();
                            hrshimmer.setVisibility(View.GONE);
                        }
                    }
                });
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        isVisible = false;
        hasSearched = false;
    }

    @Override
    public void onStart() {
        super.onStart();

        getActivity().findViewById(R.id.home).setVisibility(View.GONE);

        if (isDataAvailable) {
            hrshimmer.stopShimmerAnimation();
            hrshimmer.setVisibility(View.GONE);
        } else {
            hrshimmer.setVisibility(View.VISIBLE);
            hrshimmer.startShimmerAnimation();
        }

        Log.e(TAG, "onStart: 1");

    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().findViewById(R.id.home).setVisibility(View.GONE);

        if (isDataAvailable) {
            hrshimmer.stopShimmerAnimation();
            hrshimmer.setVisibility(View.GONE);
        } else {
            hrshimmer.setVisibility(View.VISIBLE);
            hrshimmer.startShimmerAnimation();
        }
        fetchLastLocation();

    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().findViewById(R.id.home).setVisibility(View.VISIBLE);
    }


}
