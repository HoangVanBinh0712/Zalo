package hcmute.zalo;

import static android.Manifest.permission.CAMERA;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.zalo.Pattern.UserImageBitmap_SingleTon;
import hcmute.zalo.Pattern.User_SingeTon;
import hcmute.zalo.model.User;

public class ChangeInformationActivity extends AppCompatActivity {
    //Các text view
    private ImageView btnBackProfile,imgBackgroundProfile;
    private CircleImageView imgAvatarProfile;
    private EditText edttextFullName,edttextBirthday;
    private RadioButton radioMale,radioFemale;
    private Button btnUpdateProfile;
    private TextView textviewMobile2,textviewName;

    FirebaseStorage storage;
    StorageReference storageReference;
    //Gọi mẫu single ton lấy ra user
    User_SingeTon user_singeTon = User_SingeTon.getInstance();
    User user = user_singeTon.getUser();

    private final int PICK_IMAGE_REQUEST = 22;
    private int type;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_information);
        //Ánh xạ các view
        btnBackProfile = (ImageView) findViewById(R.id.btnBackProfile);
        imgBackgroundProfile = (ImageView) findViewById(R.id.imgBackgroundProfile);
        imgAvatarProfile = (CircleImageView) findViewById(R.id.imgAvatarProfile);
        edttextFullName = (EditText) findViewById(R.id.edttextFullName);
        edttextBirthday = (EditText) findViewById(R.id.edttextBirthday);
        radioMale = (RadioButton) findViewById(R.id.radioMale);
        radioFemale = (RadioButton) findViewById(R.id.radioFemale);
        btnUpdateProfile = (Button) findViewById(R.id.btnUpdateProfile);
        textviewMobile2 = (TextView) findViewById(R.id.textviewMobile2);
        textviewName = (TextView) findViewById(R.id.textviewName);

        //Đưa dữ liệu vào các view
        putDatatoView();

        //Bấm nút Back để quay lại Activity trước
        btnBackProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Bấm vào ảnh đại diện để đổi ảnh
        imgAvatarProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(ChangeInformationActivity.this);
                dialog.setContentView(R.layout.dialog_anhdaidien);
                dialog.show();
                //Trong dialog ảnh đại diện nhấn vào dòng chọn ảnh trong điện thoại sẽ hiện lên hình ảnh trong điện thoại cho người dùng chọn
                LinearLayout linearChooseImageAvatar = dialog.findViewById(R.id.linearChooseImageAvatar);
                linearChooseImageAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Hàm dùng để hiện lên hình ảnh
                        //Type = 1 la ảnh dai dien
                        SelectImage(1);
                    }
                });
                //Trong dialog ảnh đại diện nhấn vào dòng chụp ảnh sẽ cho phép người dùng chụp ảnh
                LinearLayout linearTakeNewImageAvatar = dialog.findViewById(R.id.linearTakeNewImageAvatar);
                linearTakeNewImageAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Kiểm tra quyền sử dụng máy ảnh
                        if(CheckPermissions()) {
                            //Gắn loại ảnh là ảnh đại diện
                            type = 1;
                            //Nếu được quyền thì chạy Activity chụp ảnh
                            Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(takePicture, 0);
                        }else {
                            RequestPermissions();
                        }
                    }
                });
                //Trong dialog ảnh đại diện nhấn vào xem ảnh dể xem ảnh
                LinearLayout linearViewAvatar = dialog.findViewById(R.id.linearViewAvatar);
                linearViewAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        //Mở dialog để có thể xem full hình ảnh
                        final Dialog viewPictureDialog = new Dialog(ChangeInformationActivity.this);
                        viewPictureDialog.setContentView(R.layout.dialog_zoom);
                        viewPictureDialog.show();
                        //Đưa ảnh vào trong imageview
                        ImageView mainpicture = viewPictureDialog.findViewById(R.id.mainpicture);
                        mainpicture.setImageBitmap(UserImageBitmap_SingleTon.getInstance().getAnhdaidien());
                    }
                });
            }
        });

        //Bấm vào ảnh bìa để đổi ảnh
        imgBackgroundProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(ChangeInformationActivity.this);
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
                //Trong dialog ảnh đại diện nhấn vào dòng chụp ảnh sẽ cho phép người dùng chụp ảnh
                LinearLayout linearTakeNewImageBackground = dialog.findViewById(R.id.linearTakeNewImageBackground);
                linearTakeNewImageBackground.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Kiểm tra quyền sử dụng máy ảnh
                        if(CheckPermissions()) {
                            //Gắn loại ảnh là ảnh bìa
                            type = 0;
                            //Nếu được quyền thì chạy Activity chụp ảnh
                            Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(takePicture, 0);
                        }else
                        {
                            RequestPermissions();
                        }
                    }
                });
                //Trong dialog ảnh đại diện nhấn vào xem ảnh dể xem ảnh
                LinearLayout linearViewBackground = dialog.findViewById(R.id.linearViewBackground);
                linearViewBackground.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        //Mở dialog để có thể xem full hình ảnh
                        final Dialog viewPictureDialog = new Dialog(ChangeInformationActivity.this);
                        viewPictureDialog.setContentView(R.layout.dialog_zoom);
                        viewPictureDialog.show();
                        //Đưa ảnh vào trong imageview
                        ImageView mainpicture = viewPictureDialog.findViewById(R.id.mainpicture);
                        mainpicture.setImageBitmap(UserImageBitmap_SingleTon.getInstance().getAnhbia());
                    }
                });
            }
        });

        //Bắt sự kiện khi bấm vào RadioButton (giới tính nam)
        radioMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Thay đổi trạng thái của RadioButton (giới tính nữ)
                radioFemale.setChecked(false);
            }
        });
        //Bắt sự kiện khi bấm vào RadioButton (giới tính nữ)
        radioFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Thay đổi trạng thái của RadioButton (giới tính nam)
                radioMale.setChecked(false);
            }
        });

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Đặt kiểu định dạng năm-tháng-ngày
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                //Khai báo biến để lấy ngày sinh
                Date birth = null;

                //Lấy dữ liệu mà người dùng đã nhập để tiến hành cập nhập
                String fullName = edttextFullName.getText().toString().trim();
                String sbirth = edttextBirthday.getText().toString().trim();
                //Mặc định giới tính là nam
                boolean sex = true;
                //Nếu người dùng chọn giới tính nữ
                if(radioFemale.isChecked())
                    sex = false;
                //Kiểm tra nếu người dùng không nhập đầy đủ thông tin
                if(fullName.isEmpty() || sbirth.isEmpty())
                {
                    //Thông báo phải nhập đầy đủ thông tin
                    Toast.makeText(ChangeInformationActivity.this, "Invalid input !", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Ngày sinh nhập hợp lệ
                try {
                    //Chuyển ngày sinh sang dạng date
                    birth = format.parse(sbirth);

                } catch (ParseException e) {  //Ngày sinh nhập không hợp lệ
                    e.printStackTrace();
                    //Thông báo ngày sinh không hợp lệ
                    Toast.makeText(ChangeInformationActivity.this, "Invalid Birthday !", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Chuyền birthday sang string
                String birthday = format.format(birth);

                //Cập nhật thay đổi lên FirebaseDatabase và user
                //Cập nhật cho user
                user.setFullname(fullName);
                user.setBirthday(birthday);
                user.setSex(sex);
                //Cập nhật cho database
                //Gọi kết nối đến FirebaseDatabase vào bảng users
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("users");
                //Truy cập đến nhánh có id là số điện thoại và cập nhật lại thông tin
                myRef.child(user.getPhone()).setValue(user);
                Toast.makeText(ChangeInformationActivity.this, "Update successful.", Toast.LENGTH_SHORT).show();
                putDatatoView();
            }
        });

    }

    // Hàm để chọn hình ảnh
    private void SelectImage(int type) {
        this.type = type;
        // Xác định mở thư mục ảnh
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select image..."), PICK_IMAGE_REQUEST);
    }

    //Sau khi chọn ảnh xong chạy vào hàm này
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
                                    Toast.makeText(ChangeInformationActivity.this, "Update successful!!", Toast.LENGTH_SHORT).show();
                                }
                            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            // Lỗi, không tải lên thành công
                            // Tắt progress đi và in ra lỗi
                            progressDialog.dismiss();
                            Toast.makeText(ChangeInformationActivity.this,"Update failed. Error: " + e.getMessage(),Toast.LENGTH_SHORT).show();
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
    private void uploadImage() {
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
                                    //Cập nhập lại dữ liệu sau khi sửa đổi
                                    //putDatatoView();
                                    Toast.makeText(ChangeInformationActivity.this, "Update successful!!", Toast.LENGTH_SHORT).show();
                                }
                            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            // Lỗi, không tải lên thành công
                            // Tắt progress đi và in ra lỗi
                            progressDialog.dismiss();
                            Toast.makeText(ChangeInformationActivity.this,"Update failed. Error: " + e.getMessage(),Toast.LENGTH_SHORT).show();
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

    private void putDatatoView() {
        //Nếu không có user trả về trang login
        if(user == null)
        {
            startActivity(new Intent(ChangeInformationActivity.this, loginActivity.class));
            finish();
        }
        //Có user và bắt đầu đưa dữ liệu cho các view
        textviewName.setText(user.getFullname());
        edttextFullName.setText(user.getFullname());
        edttextBirthday.setText(user.getBirthday());
        //Lấy số điện thoại và chuyển nó về kiểu +84
        String getPhone = user.getPhone();
        String phone = "+84" + getPhone.substring(1);
        textviewMobile2.setText(phone);
        if(user.getSex()){
            radioMale.setChecked(true);
        }
        else{
            radioFemale.setChecked(true);
        }
        // Lấy ảnh bìa , ảnh đại diện
        //Gọi lớp singleTon
        UserImageBitmap_SingleTon userImageBitmap_singleTon = UserImageBitmap_SingleTon.getInstance();
        //Nếu chưa có ảnh thì dùng local storage tải lên - Tốn thời gian xử lý
        if(userImageBitmap_singleTon.getAnhbia() == null || userImageBitmap_singleTon.getAnhdaidien() == null)
        {
            //Kiểm tra nếu đã có ảnh mới thực hiện lấy ảnh đại diện
            if(!user.getAvatar().equals("")) {
                Toast.makeText(ChangeInformationActivity.this, "Load Storage", Toast.LENGTH_SHORT).show();
                //Đưa dữ liệu cho ảnh đại diện dùng Firebase Storage
                storage = FirebaseStorage.getInstance();
                storageReference = storage.getReference(user.getAvatar());
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //Lấy được Uri thành công. Dùng picasso để đưa hình vào Circle View ảnh đại diện
                        Picasso.get().load(uri).fit().centerCrop().into(imgAvatarProfile);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Thất bại thì sẽ in ra lỗi
                        Toast.makeText(ChangeInformationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            //Đưa dữ liệu cho ảnh bìa dùng Firebase Storage
            //Kiểm tra nếu đã có ảnh mới thực hiện lấy ảnh bìa
            if(!user.getBackground().equals("")) {
                storage = FirebaseStorage.getInstance();
                storageReference = storage.getReference(user.getBackground());
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //Lấy được Uri thành công. Dùng picasso để đưa hình vào Circle View ảnh đại diện
                        Picasso.get().load(uri).fit().centerCrop().into(imgBackgroundProfile);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Thất bại thì sẽ in ra lỗi
                        Toast.makeText(ChangeInformationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }else
        {
            //Nếu đã có ảnh thì set vào luôn
            imgAvatarProfile.setImageBitmap(userImageBitmap_singleTon.getAnhdaidien());
            imgBackgroundProfile.setImageBitmap(userImageBitmap_singleTon.getAnhbia());
        }

    }
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    // Kiểm tra các quyền có được cấp chưa
    public boolean CheckPermissions() {
        //Quyền dùng máy ảnh
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    private void RequestPermissions() {
        //Xin cấp quyền từ hệ thống
        ActivityCompat.requestPermissions(ChangeInformationActivity.this, new String[]{CAMERA}, REQUEST_AUDIO_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Sau khi ta chọn chấp nhận hay từ chối sẽ trả kết quả vào hàm này
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToCamera = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    //Nếu được chấp nhận hết
                    if (permissionToCamera) {
                        //Thông báo thành công
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        //Thông báo từ chối
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
    //Khi trở lại Activity cập nhật lại cho các view
    @Override
    protected void onResume() {
        super.onResume();
        putDatatoView();
    }
}