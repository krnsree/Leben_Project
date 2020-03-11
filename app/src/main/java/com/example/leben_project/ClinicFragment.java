package com.example.leben_project;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ClinicFragment extends Fragment {

    @BindView(R.id.clinicList)
    RecyclerView clinicList;

    @BindView(R.id.crshimmer)
    ShimmerFrameLayout crshimmer;


    FirebaseFirestore ref;

    static ArrayList<RVCell> clinicLists= new ArrayList<>();

    MCAdapter clinicAdapter;

    private String TAG="TAG";
    private boolean isDataAvailable;

    public ClinicFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_clinic, container, false);
        ButterKnife.bind(this,view);

        getActivity().findViewById(R.id.home).setVisibility(View.GONE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        clinicList.setHasFixedSize(true);
        clinicAdapter=new MCAdapter(clinicLists,getContext(),getActivity().getSupportFragmentManager(),getActivity());
        clinicList.setAdapter(clinicAdapter);

        getItems();
    }

    private void getItems() {

        crshimmer.startShimmerAnimation();
        if (clinicLists != null && clinicLists.size() > 0) {
            isDataAvailable=false;
            crshimmer.stopShimmerAnimation();
            crshimmer.setVisibility(View.GONE);
            return;
        }

        ref=FirebaseFirestore.getInstance();

        ref.collection("MedicalCenters")
                .get()
                .addOnCompleteListener(task -> {

                    if(task.isSuccessful())
                    {
                        for(QueryDocumentSnapshot documentSnapshot : task.getResult())
                        {
                            if (documentSnapshot.exists()) {
                                RVCell rvcell = new RVCell();
                                rvcell.setName(String.valueOf(documentSnapshot.getData().get("name")));
                                rvcell.setAddress(String.valueOf(documentSnapshot.getData().get("address")));
                                rvcell.setLocation(String.valueOf(documentSnapshot.getData().get("location")));
                                rvcell.setTime(String.valueOf(documentSnapshot.getData().get("time")));
                                rvcell.setPhno(String.valueOf(documentSnapshot.getData().get("phno")));
                                clinicLists.add(rvcell);
                            }

                        }
                        clinicAdapter.notifyDataSetChanged();
                        crshimmer.stopShimmerAnimation();
                        crshimmer.setVisibility(View.GONE);

                    }
                    else
                        Toast.makeText(getContext(), "No clinics available", Toast.LENGTH_SHORT).show();
                });
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        isDataAvailable = true;

    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isDataAvailable) {
            crshimmer.stopShimmerAnimation();
            crshimmer.setVisibility(View.GONE);
        }
        else {
            crshimmer.setVisibility(View.VISIBLE);
            crshimmer.startShimmerAnimation();
        }

        getActivity().findViewById(R.id.home).setVisibility(View.GONE);
        getItems();

    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isDataAvailable) {
            crshimmer.stopShimmerAnimation();
            crshimmer.setVisibility(View.GONE);
        }
        else {
            crshimmer.setVisibility(View.VISIBLE);
            crshimmer.startShimmerAnimation();
        }

        getActivity().findViewById(R.id.home).setVisibility(View.GONE);
        getItems();

    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().findViewById(R.id.home).setVisibility(View.VISIBLE);
    }
}