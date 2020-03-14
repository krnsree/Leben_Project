package com.example.leben_project;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.WindowManager.LayoutParams;
import static androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;


public class details_fragment extends Fragment {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;

    @BindView(R.id.hospitalName)
    TextView hospitalName;

    @BindView(R.id.address)
    TextView hospitalAddress;

    @BindView(R.id.ratingBar)
    RatingBar ratingBar;

    @BindView(R.id.rating)
    TextView hospitalRating;

    @BindView(R.id.dp_time)
    TextView hospitalTime;

    @BindView(R.id.phone_num)
    TextView hospitalPhnonum;

    @BindView(R.id.url)
    TextView hospitalLink;

    @BindView(R.id.dp_location)
    TextView hospitalLocation;

    @BindView(R.id.services_rv)
    RecyclerView services_rv;

    @BindView(R.id.dept_rv)
    RecyclerView dept_rv;

    @BindView(R.id.comments_rv)
    RecyclerView commentss_rv;

    @BindView(R.id.deptPro)
    ProgressBar deptPro;

    @BindView(R.id.serPro)
    ProgressBar serPro;

    @BindView(R.id.noComments)
    TextView noComments;

    @BindView(R.id.noServices)
    TextView noServices;

    @BindView(R.id.noDepartments)
    TextView noDepartments;

    @BindView(R.id.locationButton)
    Button locationButton;

    @BindView(R.id.commentButton)
    Button commentButton;

    CommentDialoge commentDialoge;

    FirebaseFirestore ref;

    SR_Adapter sr_adapter;
    SR_Adapter dr_adapter;

    ArrayList<CommentCell> list = new ArrayList<>();

    String name, address, homeurl, contact, location, time;

    RVCell listData = new RVCell();
    static ArrayList<String> services = new ArrayList<>();
    static ArrayList<String> departments = new ArrayList<>();

    CommentAdapter adapter;

    public details_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details_fragment, container, false);

        ButterKnife.bind(this, view);

        noServices.setVisibility(View.GONE);
        noDepartments.setVisibility(View.GONE);
        getActivity().getWindow().setFlags(LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        collapsingToolbar.setContentScrimColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        collapsingToolbar.setTitle("Leben");
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedToolbar);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedToolbar);
        collapsingToolbar.setTitleEnabled(true);
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
            }
        }

        if (getArguments() != null) {
            String list = getArguments().getString("MD_Details");
            Gson gson = new Gson();
            listData = gson.fromJson(list, RVCell.class);
        }

        ref = FirebaseFirestore.getInstance();
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        commentss_rv.setHasFixedSize(true);
        commentss_rv.setLayoutManager(layoutManager);
        adapter = new CommentAdapter(getContext(), list);
        commentss_rv.setAdapter(adapter);

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentDialoge = CommentDialoge.newInstance(listData.getUid());
                commentDialoge.show(details_fragment.this.getActivity().getSupportFragmentManager(), "comment");
                adapter.notifyDataSetChanged();
            }
        });

        services_rv.setHasFixedSize(true);
        RecyclerView.LayoutManager slayoutManager = new GridLayoutManager(getContext(), 2);
        services_rv.setLayoutManager(slayoutManager);
        sr_adapter = new SR_Adapter(getContext(), services);
        services_rv.setAdapter(sr_adapter);

        dept_rv.setHasFixedSize(true);
        RecyclerView.LayoutManager dlayoutManager = new GridLayoutManager(getContext(), 2);
        dept_rv.setLayoutManager(dlayoutManager);
        dr_adapter = new SR_Adapter(getContext(), departments);
        dept_rv.setAdapter(dr_adapter);

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String query = null;
                try {
                    query = URLEncoder.encode(listData.getName(), "utf-8");
                    Log.e("TAG", "onMaps: " + query);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Uri locuri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + query);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, locuri);
                mapIntent.setPackage("com.google.android.apps.maps");
                try {
                    if (mapIntent.resolveActivity(getContext().getPackageManager()) != null) {
                        getContext().startActivity(mapIntent);
                    }
                } catch (NullPointerException e) {
                    Log.e("TAG", "onClick: NullPointerException: Couldn't open map." + e.getMessage());
                    Toast.makeText(getContext(), "Couldn't open map", Toast.LENGTH_SHORT).show();
                }
            }
        });

        getServiceItems();
        getDeptItems();
        getComments();
        setItems();

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //do something with your id
        return super.onOptionsItemSelected(item);
    }

    private void getComments() {

        ref.collection("Re").whereEqualTo("uid", listData.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            CommentCell cell = new CommentCell();
                            cell.setComment((String) documentSnapshot.getData().get("Comment"));
                            cell.setName((String) documentSnapshot.getData().get("Name"));
                            cell.setRating((String) documentSnapshot.getData().get("Rating"));
                            cell.setImage((String) documentSnapshot.getData().get("Image"));
                            list.add(cell);
                        }
                        adapter.notifyDataSetChanged();
                        noComments.setVisibility(View.GONE);
                    } else {
                        noComments.setVisibility(View.VISIBLE);
                    }

                } else {
                    noComments.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void getDeptItems() {
        ref = FirebaseFirestore.getInstance();

        if (departments != null && departments.size() > 0) {
            dept_rv.setAdapter(dr_adapter);
            dr_adapter.notifyDataSetChanged();
            serPro.setVisibility(View.GONE);
            return;
        }

        if (listData.getDept() != null) {

            for (int i = 0; i < listData.getDept().size(); i++) {
                Log.e("tag", "getDeptItems: " + listData.getDept().get(i));
                ref.collection("Dept")
                        .whereEqualTo("uid", listData.getDept().get(i))
                        .get()
                        .addOnCompleteListener(task -> {

                            Log.e("TAG", "getDeptItems: " + task.isSuccessful());
                            Log.e("TAG", "getDeptItems: " + task.getResult().size());

                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    departments.add(String.valueOf(documentSnapshot.getData().get("name")));
                                    Log.e("TAG", "getItems:" + documentSnapshot.getData().get("name"));
                                }
                            }
                            dr_adapter.notifyDataSetChanged();

                        });
            }
            dr_adapter.notifyDataSetChanged();

        } else {
            noDepartments.setVisibility(View.VISIBLE);
        }
        serPro.setVisibility(View.GONE);


    }

    private void getServiceItems() {


        if (services != null && services.size() > 0) {
            services_rv.setAdapter(sr_adapter);
            sr_adapter.notifyDataSetChanged();
            deptPro.setVisibility(View.GONE);
            return;
        } else {
            if (listData.getServices() != null) {
                for (int i = 0; i < listData.getServices().size(); i++) {
                    ref.collection("Services")
                            .whereEqualTo("uid", listData.getServices().get(i))
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                        services.add(String.valueOf(documentSnapshot.get("name")));
                                    }
                                }
                                sr_adapter.notifyDataSetChanged();
                            });
                }
            } else {
                noServices.setVisibility(View.VISIBLE);
            }
        }


        serPro.setVisibility(View.GONE);
        deptPro.setVisibility(View.GONE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (getFragmentManager().getBackStackEntryCount() > 0) {
                        getFragmentManager().popBackStackImmediate("MCLists", POP_BACK_STACK_INCLUSIVE);
                    }
                }

                return false;
            }
        });


        /*ChipNavigationBar bottomNavigationView = getActivity().findViewById(R.id.navBar);
        bottomNavigationView.setVisibility(View.GONE);*/

    }

    private void setItems() {

        hospitalName.setText(listData.getName());
        hospitalAddress.setText(listData.getAddress());
        hospitalLink.setText(listData.getHomeurl());
        hospitalLocation.setText(listData.getLocation());
        hospitalTime.setText(listData.getTime());
        hospitalPhnonum.setText(listData.getPhno());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().findViewById(R.id.home).setVisibility(View.GONE);
    }
}
