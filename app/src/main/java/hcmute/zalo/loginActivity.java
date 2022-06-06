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
        // Ánh xạ các View và thêm sự kiện
        textView3 = findViewById(R.id.textView3);
        // Sự kiện click chuyển sang trang đăng ký
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
        // Sự kiện click nút đăng nhập
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Tạo dialog process đang đăng nhập
                progressDialog = new ProgressDialog(loginActivity.this);
                progressDialog.setTitle("Đang đăng nhập...");
                progressDialog.setMessage("Vui lòng chờ");
                progressDialog.show();
                //Lấy số điện thoại và mật khẩu trong edittext
                String phone = edtPhonenum.getText().toString();
                String pass = edtPassword1.getText().toString();
                //Kiểm tra cơ bản
                if(phone.equals("") || pass.equals("") || phone.length() != 10 || pass.length() < 6)
                {
                    progressDialog.dismiss();
                    Toast.makeText(loginActivity.this, "Sai tài khoản hoặc mật khẩu !", Toast.LENGTH_SHORT).show();
                }else
                {
                    //Tiến hành tìm kiếm trên FirebaseDatabase
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("users");
                    //Tìm kiếm trong bảng users
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            DataSnapshot dataSnapshot = snapshot.child(phone);
                            //Nếu không có số điện thoại này (Số điện thoại chưa được đăng ký).
                            if(dataSnapshot.exists() == false)
                            {
                                progressDialog.dismiss();
                                Toast.makeText(loginActivity.this, "Sai tài khoản hoặc mật khẩu !", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            //Số điện thoại đã được đăng ký
                            User user = dataSnapshot.getValue(User.class);
                            String validPass = user.getPassword();
                            //Kiểm tra mật khẩu
                            if(validPass.equals(pass))
                            {
                                //Mật khẩu đúng, đưa user vào mẫu SingeTon cho việc sử dụng lúc sau và mở Activity main
                                progressDialog.dismiss();
                                User_SingeTon user_singeTon = User_SingeTon.getInstance();
                                user_singeTon.setUser(user);
                                startActivity(new Intent(loginActivity.this, MainActivity.class));

                            }else{
                                //Sai mật khẩu. Thông báo
                                progressDialog.dismiss();
                                Toast.makeText(loginActivity.this, "Sai tài khoản hoặc mật khẩu !", Toast.LENGTH_SHORT).show();
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
        //Checkbox hiện mật khẩu thay vì hiện dấu "*"
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