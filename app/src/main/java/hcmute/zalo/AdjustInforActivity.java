package hcmute.zalo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import hcmute.zalo.Pattern.UserImageBitmap_SingleTon;
import hcmute.zalo.Pattern.User_SingeTon;
import hcmute.zalo.model.User;

public class AdjustInforActivity extends AppCompatActivity {

    //Các text view
    private TextView txtUserName,txtInformation,txtChangeAvatar,txtChangeBackground,txtChangeDescription;
    private ImageView btnBack;
    //Gọi mẫu single ton lấy ra user
    User_SingeTon user_singeTon = User_SingeTon.getInstance();
    User user = user_singeTon.getUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust_infor);
        //Ánh xạ các view
        txtUserName = findViewById(R.id.txtUserName);
        txtChangeAvatar = findViewById(R.id.txtChangeAvatar);
        txtInformation = findViewById(R.id.txtInformation);
        txtChangeBackground = findViewById(R.id.txtChangeBackground);
        txtChangeDescription = findViewById(R.id.txtChangeDescription);
        btnBack = findViewById(R.id.btnBack);
        //Hiện tên của user trên txtUsername
        txtUserName.setText(user.getFullname());
        //Bắt sự kiện cho các textview khi click vào
        //Cho textview thông tin
        txtInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdjustInforActivity.this,ChangeInformationActivity.class));
            }
        });
        //Cho textview đổi ảnh đại diện
        txtChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(AdjustInforActivity.this);
                dialog.setContentView(R.layout.dialog_anhdaidien);
                dialog.show();
                //Trong dialog ảnh đại diện nhấn vào dòng chọn ảnh trong điện thoại sẽ hiện lên hình ảnh trong điện thoại cho người dùng chọn
                LinearLayout linearChooseImageAvatar = dialog.findViewById(R.id.linearChooseImageAvatar);
                linearChooseImageAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Hàm dùng để hiện lên hình ảnh
                        //Type = 1 la anh anh dai dien
                        SelectImage(1);
                    }
                });
            }
        });
        //Cho textview đổi ảnh bìa
        txtChangeBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(AdjustInforActivity.this);
                dialog.setContentView(R.layout.dialog_anhbia);
                dialog.show();
                LinearLayout linearChooseBackgroundImage = dialog.findViewById(R.id.linearChooseBackgroundImage);
                linearChooseBackgroundImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Type = 0 la anh bia
                        SelectImage(0);
                    }
                });
            }
        });
        //Cho textview đổi giới thiệu bản thân
        txtChangeDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Mở dialog lên
                DialogChangeDescription();
            }
        });
        // Cho nút Back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    //Các view cho DialogChangeDescription
    private EditText txtDescription;
    //Các button
    private Button btnConfirmEditDescription,btnCancelEditDescription;
    public void DialogChangeDescription() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_description);
        //Ánh xạ các View và button
        btnCancelEditDescription = dialog.findViewById(R.id.btnCancelEditDescription);
        btnConfirmEditDescription = dialog.findViewById(R.id.btnConfirmEditDescription);
        txtDescription = dialog.findViewById(R.id.txtDescription);

        //Đưa giới thiệu của người dùng vào edittext
        txtDescription.setText(user.getDescription());
        //Tạo sự kiện click cho nút hủy
        btnCancelEditDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Bấm hủy thì tắt dialog
                dialog.dismiss();
            }
        });
        //Tạo sự kiện cho nút xác nhận
        btnConfirmEditDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Cập nhật thay đổi lên FirebaseDatabase và user
                //Cập nhật cho user
                user.setDescription(txtDescription.getText().toString());
                //Gọi kết nối đến FirebaseDatabase vào bảng users
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("users");
                //Truy cập đến nhánh có id là số điện thoại và cập nhật lại thông tin
                myRef.child(user.getPhone()).setValue(user);
                //Cập nhật xong thì tắt dialog
                dialog.dismiss();
                Toast.makeText(AdjustInforActivity.this, "Update description successful.", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }
    private final int PICK_IMAGE_REQUEST = 22;
    private int type;
    private Uri filePath;

    // Hàm để chọn hình ảnh
    private void SelectImage(int type)
    {
        this.type = type;
        // Xác định mở thư mục ảnh
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select image..."), PICK_IMAGE_REQUEST);
    }
    //Sau khi chọn ảnh xong chạy vào hàm này
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Lấy được uri của ảnh
            filePath = data.getData();
            try {
                //Đưa ảnh lên Firebase Storage
                uploadImage();
                // Chuyển thành bitmap và đưa vào ảnh đại diện
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                UserImageBitmap_SingleTon userImageBitmap_singleTon = UserImageBitmap_SingleTon.getInstance();

                if(this.type == 1) {
                    userImageBitmap_singleTon.setAnhdaidien(bitmap);
                }
                else {
                    userImageBitmap_singleTon.setAnhbia(bitmap);
                }
            }
            catch (IOException e) {
                // In ra lỗi
                e.printStackTrace();
            }
        }
    }
    //Hàm này dùng để đưa ảnh lên trên firebase storage
    private void uploadImage()
    {
        //Kiểm tra đường dẫn file
        if (filePath != null) {

            // Hiện ProgressDialog trong khi đang tải lên
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");

            progressDialog.show();
            //Khai báo FirebaseStorage
            FirebaseStorage storage;
            StorageReference storageReference;

            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
            // Đi vào nhánh con
            StorageReference ref;
            if(type == 1){
                ref = storageReference.child("images/" + user.getPhone() + "_avatar");
            }else
                ref = storageReference.child("images/" + user.getPhone() + "_background");
            // Tạo sự kiên cho việc upload file cả khi thành công hay thất bại và hiện thanh progress theo %
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    // Tải ảnh lên thành công
                                    // Tắt dialog progress đi
                                    progressDialog.dismiss();
                                    //Cập nhật lại cho bảng user về địa chỉ của avatar
                                    //Cập nhật lại cho cả user_singleTon
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference myRef = database.getReference("users");
                                    if(type == 1) {
                                        user.setAvatar("images/" + user.getPhone() + "_avatar");
                                        myRef.child(user.getPhone()).setValue(user);
                                    }else {
                                        user.setBackground("images/" + user.getPhone() + "_background");
                                        myRef.child(user.getPhone()).setValue(user);
                                    }
                                    user_singeTon.setUser(user);
                                    Toast.makeText(AdjustInforActivity.this, "Update successful!!", Toast.LENGTH_SHORT).show();
                                }
                            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            // Lỗi, không tải lên thành công
                            // Tắt progress đi và in ra lỗi
                            progressDialog.dismiss();
                            Toast.makeText(AdjustInforActivity.this,"Update failed. Error: " + e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        // Sự kiện cho Progress
                        // Hiển thị % hoàn thành
                        @Override
                        public void onProgress(
                                UploadTask.TaskSnapshot taskSnapshot)
                        {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()/ taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Downloaded " + (int)progress + "%");
                        }
                    });
        }
    }

}