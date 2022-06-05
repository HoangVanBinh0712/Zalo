package hcmute.zalo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import hcmute.zalo.model.User;

public class loginActivity extends AppCompatActivity {

    private TextView textView3;
    private EditText edtPhonenum, edtPassword1;
    private Button btnLogin;
    private CheckBox checkbox_showPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        textView3 = findViewById(R.id.textView3);
        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(loginActivity.this,RegisterActivity.class));
            }
        });
        edtPassword1 = findViewById(R.id.edtPassword1);
        edtPhonenum = findViewById(R.id.edtPhonenum);
        btnLogin = findViewById(R.id.btnLogin);
        checkbox_showPassword = findViewById(R.id.checkbox_showPassword);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = edtPhonenum.getText().toString();
                String pass = edtPassword1.getText().toString();
                if(phone.equals("") || pass.equals("") || phone.length() != 10)
                {
                    Toast.makeText(loginActivity.this, "Wrong phonenumber or password !", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("users");
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DataSnapshot dataSnapshot = snapshot.child(phone);
                        if(dataSnapshot.exists() == false)
                        {
                            Toast.makeText(loginActivity.this, "Wrong phonenumber or password !", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String validPass = dataSnapshot.getValue(User.class).getPassword();

                        if(validPass.equals(pass))
                        {
                            //Dang nhap thanh cong
                            //Toast.makeText(loginActivity.this, "Login Successfully !", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(loginActivity.this, MainActivity.class));

                        }else{
                            Toast.makeText(loginActivity.this, "Wrong phonenumber or password !", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        checkbox_showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkbox_showPassword.isChecked()){
                    edtPassword1.setInputType(1);
                }
                else {
                    edtPassword1.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                }
            }
        });

    }
}