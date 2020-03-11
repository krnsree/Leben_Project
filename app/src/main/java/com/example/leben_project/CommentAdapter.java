package com.example.leben_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    ArrayList<CommentCell> list;
    Context context;

    public CommentAdapter(@NonNull Context context, ArrayList<CommentCell> list) {
        this.list=list;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.commenttile,parent,false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.rating.setText(list.get(position).getRating());
        holder.name.setText(list.get(position).getName());
        holder.comment.setText(list.get(position).getComment());
        Glide.with(context).load(list.get(position).getImage()).into(holder.profileImage);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView rating;
        TextView comment;
        ImageView profileImage;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            name=itemView.findViewById(R.id.nametil);
            rating=itemView.findViewById(R.id.rattile);
            comment=itemView.findViewById(R.id.comtile);
            profileImage=itemView.findViewById(R.id.profile_image_com);
        }

    }
}
