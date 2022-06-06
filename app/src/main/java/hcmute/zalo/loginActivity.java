package hcmute.zalo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import hcmute.zalo.Pattern.UserImageBitmap_SingleTon;
import hcmute.zalo.Pattern.User_SingeTon;
import hcmute.zalo.model.LoginHistory;
import hcmute.zalo.model.User;

public class loginActivity extends AppCompatActivity {

    private TextView textView3;
    private EditText edtPhonenum, edtPassword1;
    private Button btnLogin;
    private CheckBox checkbox_showPassword;
    ProgressDialog progressDialog;
    //Khai báo biến lấy tên thiết bị
    private String deviceName = Build.MODEL;

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
                                User_SingeTon user_singeTon = User_SingeTon.getInstance();
                                user_singeTon.setUser(user);

                                //Lấy ảnh bìa / ảnh đại diện đưa vào bitmap để load lên tốn ít thời gian hơn.
                                //Gọi lớp singleTon
                                UserImageBitmap_SingleTon userImageBitmap_singleTon = UserImageBitmap_SingleTon.getInstance();
                                //Truy xuất storage

                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageReference = storage.getReference(user.getAvatar());
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        //Lấy được Uri thành công. Lấy bitmap của ảnh đại diện thông qua thread
                                        Thread thread = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try  {
                                                    URL url = new URL(uri.toString());
                                                    Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                                    userImageBitmap_singleTon.setAnhdaidien(image);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                        thread.start();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Thất bại thì sẽ in ra lỗi
                                        Toast.makeText(loginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });
                                // lấy ảnh bìa
                                storageReference = storage.getReference(user.getBackground());
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        //Lấy được Uri thành công. Lấy bitmap của ảnh bìa thông qua thread
                                            Thread thread = new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try  {
                                                        URL url = new URL(uri.toString());
                                                        Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                                        userImageBitmap_singleTon.setAnhbia(image);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                            thread.start();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Thất bại thì sẽ in ra lỗi
                                        Toast.makeText(loginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });
                                //Tất cả hoàn tất.
                                progressDialog.dismiss();

                                //Lưu lịch sử đăng nhập
                                //Lấy thời gian đăng nhập
                                Date currentTime = Calendar.getInstance().getTime();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                                String loginDate = dateFormat.format(currentTime);

                                //Lưu các thông tin đăng nhập vào model
                                LoginHistory loginHistory = new LoginHistory(phone,loginDate,deviceName);

                                //Lưu lịch sử đăng nhập vào database
                                DatabaseReference myLoginHistoryRef = database.getReference("loginHistory");
                                myLoginHistoryRef.child(phone).child(loginDate).setValue(loginHistory);

                                //Chuyển sang MainActivity
                                startActivity(new Intent(loginActivity.this, MainActivity.class));

                            }else{
                                //Sai mật khẩu. Thông báo
                                progressDialog.dismiss();
                                Toast.makeText(loginActivity.this, "Sai tài khoản hoặc mật khẩu !", Toast.LENGTH_SHORT).show();
                                finish();
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