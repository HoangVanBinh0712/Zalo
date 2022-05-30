package hcmute.zalo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SendOtpActivity extends AppCompatActivity {
    Button btn_sendOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_otp);

        btn_sendOtp = (Button) findViewById(R.id.btn_sendOtp);

        btn_sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SendOtpActivity.this,ConfirmOtpActivity.class));
                finish();
            }
        });
    }
}