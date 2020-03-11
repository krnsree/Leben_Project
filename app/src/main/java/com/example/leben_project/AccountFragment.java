package com.example.leben_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AccountFragment extends BottomSheetDialogFragment {

    @BindView(R.id.gSignIn)
    SignInButton gogleSignIn;

    @BindView(R.id.ButtonShow)
    LinearLayout button;

    @BindView(R.id.detailsShow)
    CardView details;

    @BindView(R.id.userName)
    TextView username;

    @BindView(R.id.userMail)
    TextView userMail;

    @BindView(R.id.signOut)
    Button signOut;

    @BindView(R.id.profile_image)
    ImageView profileImage;

    @BindView(R.id.nameShimmer)
    ShimmerFrameLayout nameShimmer;

    @BindView(R.id.emailShimmer)
    ShimmerFrameLayout emailShimmer;

    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 1;
    private FirebaseAuth auth;
    static GoogleSignInAccount account;

    public AccountFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_account, container, false);

        ButterKnife.bind(this,view);


        auth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        if(SaveSharedPreference.getLoggedStatus(getActivity().getApplicationContext())){
            button.setVisibility(View.GONE);
            details.setVisibility(View.VISIBLE);
            emailShimmer.startShimmerAnimation();
            nameShimmer.startShimmerAnimation();
            setDetails(account);
        }

        else{
            button.setVisibility(View.VISIBLE);
            details.setVisibility(View.GONE);
        }

        gogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                details.setVisibility(View.VISIBLE);
                button.setVisibility(View.GONE);
                AccountFragment.this.signIn();
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveSharedPreference.setLoggedIn(getActivity().getApplicationContext(), false);
                SaveSharedPreference.setAccount(getActivity().getApplicationContext(), null);
                SaveSharedPreference.setAccountImage(getActivity().getApplicationContext(), null);
                signout();
            }
        });

        return view;
    }

    private void signout() {

        if(account!=null)
        {
            googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    button.setVisibility(View.VISIBLE);
                    details.setVisibility(View.GONE);
                    account=null;
                }
            });
        }

    }

    private void signIn() {

        nameShimmer.startShimmerAnimation();
        emailShimmer.startShimmerAnimation();
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {

        try {
            GoogleSignInAccount acc = task.getResult(ApiException.class);
            fbgogAuth(acc);
        } catch (ApiException e) {
            fbgogAuth(null);
        }

    }

    private void fbgogAuth(GoogleSignInAccount acc) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
        auth.signInWithCredential(authCredential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Succesfull", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = auth.getCurrentUser();
                    updateUI(user);
                } else {
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        account = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());
        if(account!=null)
        {
            SaveSharedPreference.setLoggedIn(getActivity().getApplicationContext(), true);
            SaveSharedPreference.setAccount(getActivity().getApplicationContext(),account.getDisplayName());
            SaveSharedPreference.setAccountImage(getActivity().getApplicationContext(),account.getPhotoUrl().toString());
            setDetails(account);
        }
        else
        {
            Toast.makeText(getContext(), "Null", Toast.LENGTH_SHORT).show();
        }
    }

    private void setDetails(GoogleSignInAccount account) {
        details.setVisibility(View.VISIBLE);
        if(account!=null){
            emailShimmer.stopShimmerAnimation();
            nameShimmer.stopShimmerAnimation();
            emailShimmer.setVisibility(View.GONE);
            nameShimmer.setVisibility(View.GONE);
            username.setText(account.getDisplayName());
            userMail.setText(account.getEmail());
            Glide.with(getContext()).load(account.getPhotoUrl()).into(profileImage);
        }
        else{
            signIn();
        }
    }
}
