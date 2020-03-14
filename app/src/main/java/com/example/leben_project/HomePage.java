package com.example.leben_project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomePage extends AppCompatActivity {

    @BindView(R.id.hospitalCard)
    CardView HospitalCard;

    @BindView(R.id.clinicCard)
    CardView ClinicCard;

    @BindView(R.id.pharmacyCard)
    CardView pharmacyCard;

    @BindView(R.id.laboratoryCard)
    CardView laboratoryCard;

    @BindView(R.id.accountCard)
    CardView accountCard;

    @BindView(R.id.home)
    LinearLayout home;

    @BindView(R.id.emergencyNumber)
    FloatingActionButton fab;

    FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment homeFragment = new HospitalFragment();
    Fragment clinincFragment = new ClinicFragment();
    AccountFragment acc = new AccountFragment();
    Fragment activeFragment = homeFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        ButterKnife.bind(this);

        home.setVisibility(View.VISIBLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:102"));
                startActivity(intent);
            }
        });

        HospitalCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomePage.this.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout, homeFragment)
                        .addToBackStack("HospitalList")
                        .commitAllowingStateLoss();
            }
        });

        ClinicCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*HomePage.this.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout, clinincFragment)
                        .addToBackStack("ClinicList")
                        .commitAllowingStateLoss();*/

                Toast.makeText(HomePage.this, "Please Wait for this Feature ", Toast.LENGTH_SHORT).show();
            }
        });

        pharmacyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomePage.this, "Please Wait for this Feature ", Toast.LENGTH_SHORT).show();
            }
        });

        laboratoryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomePage.this, "Please Wait for this Feature ", Toast.LENGTH_SHORT).show();
            }
        });
        accountCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acc.show(HomePage.this.getSupportFragmentManager(), acc.getTag());
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //do something here like

                int backStackEntryCount
                        =getSupportFragmentManager().getBackStackEntryCount();

                if (backStackEntryCount > 0) {

                    getSupportFragmentManager().popBackStack();

                }

                return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        home.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        home.setVisibility(View.GONE);
    }

}
