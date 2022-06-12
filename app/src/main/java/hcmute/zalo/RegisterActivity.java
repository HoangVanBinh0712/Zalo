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
import android.widget.ProgressBar;
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
    //Gọi các view object để có thể thực hiện các chức năng cần thiết
    //Textview để chuyển từ hoạt động đăng ký về hoạt động đăng nhập
    private TextView txtRegisterToLogin;
    //Button đăng ký
    private Button btnRegister;
    //Edittext điền thông tin cần thiết
    private EditText phonenumber,fullname,password, birthday,password2;
    //RadioButton chọn giới tính
    private RadioButton rmale,rfemale;
    //Biến kiểm tra đăng nhập có thành công hay không?
    private boolean isAvailable = true ;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //Ánh xạ với các view object
        anhxa();
        //Bắt sự kiện khi bấm vào Textview
        txtRegisterToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Chuyển sang LoginActitvity
                startActivity(new Intent(RegisterActivity.this,loginActivity.class));
            }
        });
        //Bắt sự kiện khi bấm vào RadioButton (giới tính nam)
        rmale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Thay đổi trạng thái của RadioButton (giới tính nữ)
                rfemale.setChecked(false);
            }
        });
        //Bắt sự kiện khi bấm vào RadioButton (giới tính nữ)
        rfemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Thay đổi trạng thái của RadioButton (giới tính nam)
                rmale.setChecked(false);
            }
        });
        //Bắt sự kiện khi bấm vào Button đăng ký
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                isAvailable = true;
                //Gọi các biến để lấy thông tin từ các view object
                String phone,name,pass,pass2,sbirth;
                boolean sex = true;
                //Lấy thông tin từ các view object
                phone = phonenumber.getText().toString();
                name = fullname.getText().toString();
                sbirth = birthday.getText().toString();
                pass = password.getText().toString();
                pass2 = password2.getText().toString();
                //Nếu trạng thái của RadioButton (giới tính nữ) bằng true thì biến sex bằng false
                if(rfemale.isChecked())
                    sex = false;
                //Đặt kiểu định dạng năm-tháng-ngày
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                //Khai báo biến để lấy ngày sinh
                Date birth = null;

                //Kiểm tra nếu người dùng không nhập đầy đủ thông tin
                if(phone == "" || name == "" || pass == "" || sbirth == "")
                {
                    //Thông báo phải nhập đầy đủ thông tin
                    Toast.makeText(RegisterActivity.this, "Invalid input !", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Kiểm tra mật khẩu không đủ 6 kí tự
                if(pass.length() < 6)
                {
                    //Thông báo phải nhập đủ 6 kí tự cho mật khẩu
                    Toast.makeText(RegisterActivity.this, "Password too weak !", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Kiểm tra 2 mật khẩu nhập vào có giống nhau không
                if(!pass.equals(pass2)){        //Không giống nhau
                    Toast.makeText(RegisterActivity.this, "The two passwords are not the same !", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Ngày sinh nhập hợp lệ
                try {
                    //Chuyển ngày sinh sang dạng date
                    birth = format.parse(sbirth);

                } catch (ParseException e) {  //Ngày sinh nhập không hợp lệ
                    e.printStackTrace();
                    //Thông báo ngày sinh không hợp lệ
                    Toast.makeText(RegisterActivity.this, "Invalid Birthday !", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Chuyền birthday sang string
                String birthday = format.format(birth);
                User user = new User(name,phone,pass,birthday,"",sex,"","");
                //CheLog.d("TAGG", user.toString());
                //All good now check if the phonenumber is already taken.
                progressDialog = new ProgressDialog(RegisterActivity.this);
                progressDialog.setTitle("Checking...");
                progressDialog.setMessage("Please wait");
                progressDialog.show();
                //Kết nối cơ sở dữ liệu
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                //Gọi root (bảng) User
                DatabaseReference myRef = database.getReference("users");
                //Đọc và lắng nghe các thay đổi của dữ liệu
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    //Mỗi khí dữ liệu thay đổi
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //Nếu số điện thoại của người dùng nhập vào đã có trong database
                        if(snapshot.hasChild(user.getPhone()))
                        {
                            //Thông báo số điện thoại đã tồn tại
                            Toast.makeText(RegisterActivity.this, "Phone number is already Taken !", Toast.LENGTH_SHORT).show();
                            //Đặt trạng thái là false để có thể kiểm tra lại dữ liệu
                            isAvailable = false;
                            progressDialog.dismiss();
                        }
                        //Nếu dự liệu hợp lệ
                        if(isAvailable == true) {
                            //Thêm dữ liệu người dùng đã nhập vào database
                            myRef.child(user.getPhone()).setValue(user);
                            progressDialog.dismiss();
                            //Thông báo đăng ký thành công
                            Toast.makeText(RegisterActivity.this, "Register Successfully", Toast.LENGTH_SHORT).show();
                            //Kết thúc RegisterActivity
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
    //Ánh xạ với các view object
    private void anhxa() {
        txtRegisterToLogin = findViewById(R.id.txtRegisterToLogin);
        btnRegister = findViewById(R.id.btnLogin);
        phonenumber = findViewById(R.id.edtPhonenum);
        fullname = findViewById(R.id.edtName);
        password = findViewById(R.id.edtPassword1);
        password2 = findViewById(R.id.edtPassword2);
        birthday = findViewById(R.id.edtBirthday1);
        rmale = (RadioButton) findViewById(R.id.checkMale);
        rfemale = (RadioButton) findViewById(R.id.checkFemale);
    }
}