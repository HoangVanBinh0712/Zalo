package hcmute.zalo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog progressDialog = new ProgressDialog(loginActivity.this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();
                String phone = edtPhonenum.getText().toString();
                String pass = edtPassword1.getText().toString();
                if(phone.equals("") || pass.equals("") || phone.length() != 10)
                {
                    Toast.makeText(loginActivity.this, "Wrong phonenumber or password !", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

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
                            progressDialog.dismiss();

                            return;
                        }
                        String validPass = dataSnapshot.getValue(User.class).getPassword();

                        if(validPass.equals(pass))
                        {

                            SharedPreferences sharedPreferences = getSharedPreferences("dataCookie", MODE_MULTI_PROCESS);
                            sharedPreferences.edit().putString("userphone",phone);
                            sharedPreferences.edit().commit();
                            progressDialog.dismiss();
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
                        progressDialog.dismiss();

                    }
                });
                progressDialog.dismiss();
            }
        });
    }
}