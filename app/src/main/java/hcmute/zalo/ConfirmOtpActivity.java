package hcmute.zalo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ConfirmOtpActivity extends AppCompatActivity {
    Button btn_confirmOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_otp);
        btn_confirmOtp = (Button) findViewById(R.id.btn_confirmOtp);

        btn_confirmOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ConfirmOtpActivity.this,ListMessageActivity.class));
                finish();
            }
        });
    }
}