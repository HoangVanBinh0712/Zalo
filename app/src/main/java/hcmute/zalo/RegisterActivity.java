package hcmute.zalo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
                //Ánh xạ các View
                String phone,name,pass,sbirth;
                boolean sex = true;
                phone = phonenumber.getText().toString();
                name = fullname.getText().toString();
                sbirth = birthday.getText().toString();
                pass = password.getText().toString();
                //Mặc định sex là true (đàn ông). Nếu click và radio female thì chuyển sex là false
                if(rfemale.isChecked())
                    sex = false;
                //Dùng SimpleDateFormat để định dạng ngày sinh
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date birth = null;

                //Kiểm tra dữ liệu dầu vào
                if(phone == "" || name == "" || pass == "" || sbirth == "")
                {
                    Toast.makeText(RegisterActivity.this, "Thông tin không hợp lệ !", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Kiểm tra mật khẩu
                if(pass.length() < 6)
                {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu yếu !", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Kiểm tra ngày sinh
                try {
                    //Nếu parse được thì hợp lệ
                    birth = format.parse(sbirth);

                } catch (ParseException e) {
                    //In lỗi
                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this, "Ngày sinh không đúng. (YYYY-MM-DD) !", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Tạo user với  thông tin nhập vào
                User user;
                if(sex == true)
                {
                    //Đàn ông
                    user = new User(name,phone,pass,birth,"",sex,"man.png","thanhpho.jpg");
                }else{
                    user = new User(name,phone,pass,birth,"",sex,"woman.jpg","thanhpho.jpg");
                }
                Log.d("TAGG", user.toString());
                //Tất cả đã được kiểm tra trừ số điện thoại. Kiểm tra xem số điện thoại đã được đăng ký ?
                //Hiện thanh tiến trình đang xử lý
                ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
                progressDialog.setTitle("Đang đăng ký...");
                progressDialog.setMessage("Vui lòng chờ");
                progressDialog.show();
                //Kiểm tra FirebaseDatabase
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("users");
                //Trong bảng user
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //Id của user cũng chính là số điện thoại. Nếu đã có nhánh mang số điện thoại tức số điện thoại đã được đăng ký
                        if(snapshot.hasChild(user.getPhone()) == false)
                        {
                            // SĐT chưa được đăng ký thì tiến hành setvalue (thêm giá trị cho nhánh đó)
                            // Hệ thống sẽ lấy những getter của object làm đầu vào thay vì phải truyền từng thuộc tính.
                            myRef.child(user.getPhone()).setValue(user);
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                            finish();

                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Số điện thoại đã được đăng ký !", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}