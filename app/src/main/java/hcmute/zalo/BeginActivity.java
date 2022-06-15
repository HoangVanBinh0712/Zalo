package hcmute.zalo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BeginActivity extends AppCompatActivity {
    //Gọi Button để có thể thực hiện các chức năng cần thiết
    private Button btn_start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hello_screen);
        //Ánh xạ Button "btn_start" bên file xml
        btn_start = findViewById(R.id.btn_start);
        //Bắt sự kiện khi bấm vào Button. Nhấn vào chuyển sang trang login
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Chuyển sang LoginActitvity
                startActivity(new Intent(BeginActivity.this,loginActivity.class));
                //Kết thúc BeginActivity
                finish();
            }
        });
    }
}