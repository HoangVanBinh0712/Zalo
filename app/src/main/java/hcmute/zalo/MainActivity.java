package hcmute.zalo;

import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.navchats);
    }
    MoreFragment moreFragment = new MoreFragment();
    ListMessageFragment listMessageFragment = new ListMessageFragment();
    PhoneBookFragment phoneBookFragment = new PhoneBookFragment();
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.navMore:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, moreFragment).commit();
                return true;
            case R.id.navchats:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, listMessageFragment).commit();
                return true;
            case R.id.navcontacts:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, phoneBookFragment).commit();
                return true;
        }
        return false;
    }

    int counter = 0;
    @Override
    public void onBackPressed() {
        counter++;
        if(counter==2)
        {
            counter=0;
            super.onBackPressed();
        }else
        {
            Toast.makeText(this, "Press again to Exit", Toast.LENGTH_SHORT).show();
        }
    }
}