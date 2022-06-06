package hcmute.zalo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

import hcmute.zalo.Pattern.User_SingeTon;
import hcmute.zalo.model.User;

public class loginActivity extends AppCompatActivity {

    private TextView textView3;
    private EditText edtPhonenum, edtPassword1;
    private Button btnLogin;
    private CheckBox checkbox_showPassword;
    ProgressDialog progressDialog;
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
                progressDialog = new ProgressDialog(loginActivity.this);
                progressDialog.setTitle("Loging in...");
                progressDialog.setMessage("Please wait");
                progressDialog.show();
                String phone = edtPhonenum.getText().toString();
                String pass = edtPassword1.getText().toString();
                if(phone.equals("") || pass.equals("") || phone.length() != 10 || pass.length() < 6)
                {
                    progressDialog.dismiss();
                    Toast.makeText(loginActivity.this, "Wrong phonenumber or password !", Toast.LENGTH_SHORT).show();
                }else
                {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("users");
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            DataSnapshot dataSnapshot = snapshot.child(phone);
                            if(dataSnapshot.exists() == false)
                            {
                                progressDialog.dismiss();
                                Toast.makeText(loginActivity.this, "Wrong phonenumber or password !", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            User user = dataSnapshot.getValue(User.class);
                            String validPass = user.getPassword();

                            if(validPass.equals(pass))
                            {
                                progressDialog.dismiss();
                                User_SingeTon user_singeTon = User_SingeTon.getInstance();
                                user_singeTon.setUser(user);
                                //Dang nhap thanh cong
                                //Toast.makeText(loginActivity.this, "Login Successfully !", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(loginActivity.this, MainActivity.class));

                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(loginActivity.this, "Wrong phonenumber or password !", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                //progressDialog.dismiss();
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