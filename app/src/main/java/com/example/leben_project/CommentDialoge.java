package com.example.leben_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentDialoge extends BottomSheetDialogFragment {


    private static String uid;
    @BindView(R.id.AddComment)
    Button addComment;

    @BindView(R.id.ratingBarComment)
    RatingBar ratcom;

    @BindView(R.id.comment)
    EditText comment;

    FirebaseFirestore ref;

    public static CommentDialoge newInstance(String ID) {
        uid = ID;
        return new CommentDialoge();
    }

    RVCell listData;

    public CommentDialoge() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comment_dialoge, container, false);

        ButterKnife.bind(this, view);

        if(!SaveSharedPreference.getLoggedStatus(getContext())){
            new AccountFragment().show(getActivity().getSupportFragmentManager(),"tag");
        }

        ref = FirebaseFirestore.getInstance();

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (comment.getText().toString().equals("") || ratcom.getRating() == 0.0) {
                    Toast.makeText(getContext(), "Please fill all the text", Toast.LENGTH_SHORT).show();
                } else {

                    String Comment = comment.getText().toString();
                    float Rating = ratcom.getRating();
                    HashMap<String, String> CommentSection = new HashMap<>();
                    CommentSection.put("Comment", Comment);
                    CommentSection.put("Rating", Rating + "");
                    CommentSection.put("uid", uid);
                    CommentSection.put("Name",SaveSharedPreference.getAccount(getContext()));
                    CommentSection.put("Image",SaveSharedPreference.getAccountImage(getContext()));
                    ref.collection("Comment").add(CommentSection).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            dismiss();
                            Toast.makeText(getContext(), "Added Successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

        return view;
    }
}
