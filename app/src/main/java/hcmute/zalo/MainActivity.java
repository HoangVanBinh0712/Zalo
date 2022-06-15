package hcmute.zalo;

import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    //Khai báo BottomNavigationView. Hiện thanh ở dưới màn hình
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        //Thiết lập bottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        //Mặc định là navchats
        bottomNavigationView.setSelectedItemId(R.id.navchats);
    }
    //Khởi tạo các Fragment
    MoreFragment moreFragment = new MoreFragment();
    ListMessageFragment listMessageFragment = new ListMessageFragment();
    PhoneBookFragment phoneBookFragment = new PhoneBookFragment();
    ContactsFragment contactsFragment = new ContactsFragment();
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        //Khi click vào các button, icon ở trên bottomNavigation thì sẽ thay thế các fragment tương ứng vào FrameLayout
        switch (item.getItemId()) {
            case R.id.navMore:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, moreFragment).commit();
                return true;
            case R.id.navchats:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, listMessageFragment).commit();
                return true;
            case R.id.navcontacts:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, contactsFragment).commit();
                return true;
            case R.id.navphones:
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, phoneBookFragment).commit();
                return true;
        }
        return false;
    }
    //Biến để đếm số lượng lần bấm nút back của người dùng, Nếu là 2 thì thoát
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