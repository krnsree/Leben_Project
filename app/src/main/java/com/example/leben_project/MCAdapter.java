package com.example.leben_project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MCAdapter extends RecyclerView.Adapter<MCAdapter.ViewHolder> {

    private final Context context;
    ArrayList<RVCell> MClists;
    FragmentManager fm;
    Activity activity;

    public MCAdapter(ArrayList<RVCell> MClists, Context context, FragmentManager factivity, Activity activity) {
        Log.e("Tag", "HospitalAdapter: here");
        this.context = context;
        this.MClists = MClists;
        this.fm = factivity;
        this.activity = activity;
        Log.e("Tag", "HospitalAdapter: " + MClists.size());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_cell, parent, false);
        return new MCAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(MClists.get(position).getName());
        holder.location.setText(MClists.get(position).getLocation());
        holder.time.setText(MClists.get(position).getTime());
        holder.phone.setText(MClists.get(position).getPhno());
        // Log.e("tag", "onBindViewHolder: "+MClists.get(position).getName() );

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String list = gson.toJson(MClists.get(position));
                Bundle bundle = new Bundle();
                bundle.putString("MD_Details", list);
                details_fragment df = new details_fragment();
                df.setArguments(bundle);
                fm.beginTransaction().replace(R.id.frameLayout
                        , df).addToBackStack("MCDetails").commitAllowingStateLoss();
            }
        });

        holder.locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.findViewById(R.id.home).setVisibility(View.GONE);
                String query = null;
                try {
                    query= URLEncoder.encode(MClists.get(position).getName(),"utf-8");
                    Log.e("TAG", "onMaps: "+ query );
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Uri locuri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination="+query);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, locuri);
                mapIntent.setPackage("com.google.android.apps.maps");
                try {
                    if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(mapIntent);
                    }
                } catch (NullPointerException e) {
                    Log.e("TAG", "onClick: NullPointerException: Couldn't open map." + e.getMessage());
                    Toast.makeText(context, "Couldn't open map", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return MClists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, location, time, phone;
        CardView card;
        Button locationButton;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            location = itemView.findViewById(R.id.location);
            time = itemView.findViewById(R.id.time);
            phone = itemView.findViewById(R.id.phno);
            card = itemView.findViewById(R.id.card);
            locationButton = itemView.findViewById(R.id.button);
        }
    }

}