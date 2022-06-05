package hcmute.zalo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import hcmute.zalo.model.User;

public class RegisterActivity extends AppCompatActivity {

    private TextView txtRegisterToLogin;
    private Button btnRegister;
    private EditText phonenumber,fullname,password, birthday;
    private RadioButton rmale,rfemale;
    private boolean isAvailable = true ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        anhxa();

    }
    private void anhxa(){
        txtRegisterToLogin = findViewById(R.id.txtRegisterToLogin);
        txtRegisterToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,loginActivity.class));

            }
        });
        btnRegister = findViewById(R.id.btnLogin);
        phonenumber = findViewById(R.id.edtPhonenum);
        fullname = findViewById(R.id.edtName);
        password = findViewById(R.id.edtPassword1);
        birthday = findViewById(R.id.edtBirthday1);
        rmale = (RadioButton) findViewById(R.id.checkMale);
        rfemale = (RadioButton) findViewById(R.id.checkFemale);
        rmale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rfemale.setChecked(false);
            }
        });
        rfemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rmale.setChecked(false);
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAvailable = true;
                String phone,name,pass,sbirth;
                boolean sex = true;
                phone = phonenumber.getText().toString();
                name = fullname.getText().toString();
                sbirth = birthday.getText().toString();
                pass = password.getText().toString();

                if(rfemale.isChecked())
                    sex = false;


                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date birth = null;

                //Check if data ís valid
                if(phone == "" || name == "" || pass == "" || sbirth == "")
                {
                    Toast.makeText(RegisterActivity.this, "Invalid input !", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(pass.length() < 6)
                {
                    Toast.makeText(RegisterActivity.this, "Password too weak !", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    birth = format.parse(sbirth);

                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this, "Invalid Birthday !", Toast.LENGTH_SHORT).show();
                    return;
                }

                User user = new User(name,phone,pass,birth,"",sex,"","");
                Log.d("TAGG", user.toString());
                //All good now check if the phonenumber is already taken.

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("users");
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(user.getPhone()))
                        {
                            Toast.makeText(RegisterActivity.this, "Phone number is already Taken !", Toast.LENGTH_SHORT).show();
                            isAvailable = false;
                        }
                        if(isAvailable == true) {
                            myRef.child(user.getPhone()).setValue(user);
                            Toast.makeText(RegisterActivity.this, "Register Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
}