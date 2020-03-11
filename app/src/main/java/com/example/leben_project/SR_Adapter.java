package com.example.leben_project;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SR_Adapter extends RecyclerView.Adapter<SR_Adapter.ViewHolder> {

    Context context;
    ArrayList<String> list;

    public SR_Adapter(Context context, ArrayList<String> services) {
        this.context=context;
        this.list=services;


    }

    @NonNull
    @Override
    public SR_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.sd_rv_cell,parent,false);
        return new SR_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SR_Adapter.ViewHolder holder, int position) {
        holder.names.setText(list.get(position));
        Log.e("TAG", "onBindViewHolder: "+list.get(position) );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView names;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            names=itemView.findViewById(R.id.sd_name);
        }
    }
}
