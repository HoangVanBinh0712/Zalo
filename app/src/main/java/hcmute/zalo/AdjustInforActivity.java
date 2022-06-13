package hcmute.zalo;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

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
                //Mở AdjustInforActivity
                startActivity(new Intent(AdjustInforActivity.this,ChangeInformationActivity.class));
            }
        });
        //Cho textview đổi ảnh đại diện
        txtChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Tạo 1 dialog
                final Dialog dialog = new Dialog(AdjustInforActivity.this);
                dialog.setContentView(R.layout.dialog_anhdaidien);
                //Hiện dialog lên
                dialog.show();
                //Trong dialog ảnh đại diện nhấn vào dòng chọn ảnh trong điện thoại sẽ hiện lên hình ảnh trong điện thoại cho người dùng chọn
                //Ánh xạ
                LinearLayout linearChooseImageAvatar = dialog.findViewById(R.id.linearChooseImageAvatar);
                //Bắt sự kiện click
                linearChooseImageAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Hàm dùng để hiện lên hình ảnh
                        //Type = 1 la anh anh dai dien
                        SelectImage(1);
                    }
                });
                //Ánh xạ
                LinearLayout linearViewAvatar = dialog.findViewById(R.id.linearViewAvatar);
                //Bắt sự kiện click
                linearViewAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Tắt dialog hiện tại
                        dialog.dismiss();
                        //Tạo 1 dialog mới
                        final Dialog viewPictureDialog = new Dialog(AdjustInforActivity.this);
                        viewPictureDialog.setContentView(R.layout.dialog_zoom);
                        //Hiện dialog mới lên
                        viewPictureDialog.show();
                        //Ánh xạ
                        ImageView mainpicture = viewPictureDialog.findViewById(R.id.mainpicture);
                        //Đưa hình bảnh vào trong imageview mainpicture bằng bitmap
                        mainpicture.setImageBitmap(UserImageBitmap_SingleTon.getInstance().getAnhdaidien());
                    }
                });
                //Trong dialog ảnh đại diện nhấn vào dòng chụp ảnh sẽ cho phép người dùng chụp ảnh
                LinearLayout linearTakeNewImageAvatar = dialog.findViewById(R.id.linearTakeNewImageAvatar);
                linearTakeNewImageAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(CheckPermissions())
                        {
                            type = 1;
                            Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(takePicture, 0);
                        }else
                        {
                            RequestPermissions();
                        }
                    }
                });
            }
        });
        //Cho textview đổi ảnh bìa
        txtChangeBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Tạo 1 dialog
                final Dialog dialog = new Dialog(AdjustInforActivity.this);
                dialog.setContentView(R.layout.dialog_anhbia);
                //Hiện dialog lên
                dialog.show();
                //Ánh xạ
                LinearLayout linearChooseBackgroundImage = dialog.findViewById(R.id.linearChooseBackgroundImage);
                //Bắt sự kiện click
                linearChooseBackgroundImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Type = 0 la anh bia
                        SelectImage(0);
                    }
                });
                //Ánh xạ
                LinearLayout linearViewBackground = dialog.findViewById(R.id.linearViewBackground);
                //Bắt sự kiện click
                linearViewBackground.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Tắt dialog đi
                        dialog.dismiss();
                        //Tạo 1 dialog mới
                        final Dialog viewPictureDialog = new Dialog(AdjustInforActivity.this);
                        viewPictureDialog.setContentView(R.layout.dialog_zoom);
                        //Hiện dialog mới lên
                        viewPictureDialog.show();
                        //Ánh xạ imageView
                        ImageView mainpicture = viewPictureDialog.findViewById(R.id.mainpicture);
                        //Đưa ảnh vào trong imageview
                        mainpicture.setImageBitmap(UserImageBitmap_SingleTon.getInstance().getAnhbia());
                    }
                });
                LinearLayout linearTakeNewImageBackground = dialog.findViewById(R.id.linearTakeNewImageBackground);
                linearTakeNewImageBackground.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(CheckPermissions())
                        {
                            type = 0;
                            Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(takePicture, 0);
                        }else
                        {
                            RequestPermissions();
                        }
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
    //Khai báo các biến cục bộ
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
        //Mở hoạt động chọn ảnh
        startActivityForResult(Intent.createChooser(intent,"Select image..."), PICK_IMAGE_REQUEST);
    }
    //Sau khi chọn ảnh xong chạy vào hàm này
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Nếu kết quả chọn ảnh thành công
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            // Lấy được uri của ảnh
            filePath = data.getData();
            try {
                //Đưa ảnh lên Firebase Storage
                uploadImage();
                // Chuyển thành bitmap và đưa vào ảnh đại diện
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                // Đưa bitmap vào UserImageBitmap_SingleTon để tiện cho việc load hình - Nhanh hơn
                UserImageBitmap_SingleTon userImageBitmap_singleTon = UserImageBitmap_SingleTon.getInstance();
                //type là 1 là ảnh đại diện, 0 là ảnh bìa
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
        else if(requestCode == 0
                && resultCode == RESULT_OK
                && data != null)
        {
            Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.PNG,100,stream);
            byte[] byteArray = stream.toByteArray();
            //Từ camera
            Date today = new Date();
            String pic_id = Long.toString(today.getTime());
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
            ref.putBytes(byteArray)
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
            // Chuyển thành bitmap và đưa vào ảnh đại diện
            //byte -> bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
            UserImageBitmap_SingleTon userImageBitmap_singleTon = UserImageBitmap_SingleTon.getInstance();

            if(this.type == 1) {
                userImageBitmap_singleTon.setAnhdaidien(bitmap);
            }
            else {
                userImageBitmap_singleTon.setAnhbia(bitmap);
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
            //type là 1 là ảnh đại diện, 0 là ảnh bìa
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
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference myRef = database.getReference("users");
                                    //type là 1 là ảnh đại diện, 0 là ảnh bìa
                                    if(type == 1) {
                                        user.setAvatar("images/" + user.getPhone() + "_avatar");
                                        myRef.child(user.getPhone()).setValue(user);
                                    }else {
                                        user.setBackground("images/" + user.getPhone() + "_background");
                                        myRef.child(user.getPhone()).setValue(user);
                                    }
                                    //Cập nhật lại cho cả user_singleTon
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
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    public boolean CheckPermissions() {
        // this method is used to check permission
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    private void RequestPermissions() {
        // this method is used to request the
        // permission for audio recording and storage.
        ActivityCompat.requestPermissions(AdjustInforActivity.this, new String[]{CAMERA}, REQUEST_AUDIO_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToCamera = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToCamera) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
}