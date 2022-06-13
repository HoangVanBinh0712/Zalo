package hcmute.zalo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.zalo.Pattern.UserImageBitmap_SingleTon;
import hcmute.zalo.Pattern.User_SingeTon;
import hcmute.zalo.adapter.MessageDetailsAdapter;
import hcmute.zalo.model.Message;
import hcmute.zalo.model.MessageDetails;
import hcmute.zalo.model.User;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ChatActivity extends AppCompatActivity {

    //Khai báo các view và các biến được sử dụng
    ImageView sendMessageButton;
    TextView txtUserChatName;
    EditText inputMessage;
    User main_user = User_SingeTon.getInstance().getUser();
    ArrayList<MessageDetails> messageDetails;
    RecyclerView rcvChat;
    MessageDetailsAdapter messageDetailsAdapter;
    NestedScrollView idNestedSV;
    Chronometer recordTimer;
    CircleImageView imageProfileChat;
    int count = 0;
    ProgressBar progressBar;
    ImageView iconMedia, iconMicro,imageBack;
    String message_id, viewer;
    ImageView iconCamera;
    //Các biến dùng cho việc chọn hình ảnh từ gallery
    private int PICK_IMAGE_REQUEST = 22;
    private Uri filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chat);
        //Ánh xạ các view
        iconMicro = findViewById(R.id.iconMicro);
        iconMedia = findViewById(R.id.iconMedia);
        idNestedSV = findViewById(R.id.idNestedSV);
        progressBar = findViewById(R.id.progressBar);
        inputMessage = findViewById(R.id.inputMessage);
        sendMessageButton = findViewById(R.id.sendMessageButton);
        txtUserChatName = findViewById(R.id.txtUserChatName);
        rcvChat = findViewById(R.id.rcvChat);
        imageBack = findViewById(R.id.imageBack);
        recordTimer = findViewById(R.id.recordTimer);
        imageProfileChat = findViewById(R.id.imageProfileChat);
        iconCamera = findViewById(R.id.iconCamera);
        //Cài đặt cho RecyclerView
        rcvChat.setLayoutManager(new LinearLayoutManager(this));
        //Khởi tạo messageDetails
        messageDetails = new ArrayList<>();
        //Khởi tạo messageDetailsAdapter
        messageDetailsAdapter = new MessageDetailsAdapter(ChatActivity.this,messageDetails);
        //Set adapter cho RecyclerView
        rcvChat.setAdapter(messageDetailsAdapter);
        //Bắt sự kiện onClick
        imageProfileChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Khi click vào nút thì sẽ mở một activity hiện trang cá nhân của đối tượng đang nhắn tin
                //Đưa dữ liệu vào trong sharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("dataCookie",Context.MODE_MULTI_PROCESS);
                sharedPreferences.edit().putString("user_id", viewer).commit();
                //Mở activity ViewUserPageActivity
                startActivity(new Intent(ChatActivity.this, ViewUserPageActivity.class));
            }
        });
        //Sự kiện khi nhập thông tin trong Edittext
        inputMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Nếu không nhập sẽ hiện các icon như micro để gửi record, picture để gửi ảnh, camera để chụp ảnh
                String text = inputMessage.getText().toString();
                if(text.length() == 0)
                {
                    //Hiện các nút camera, voice, picture
                    iconCamera.setVisibility(View.VISIBLE);
                    iconMedia.setVisibility(View.VISIBLE);
                    iconMicro.setVisibility(View.VISIBLE);

                }else
                {
                    //Ẩn các nút đấy đi
                    iconCamera.setVisibility(View.INVISIBLE);
                    iconMedia.setVisibility(View.INVISIBLE);
                    iconMicro.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //Bắt sự kiện onclick
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Trả về mainActivity
                startActivity(new Intent(ChatActivity.this,MainActivity.class));
                //Kết thúc activity này đi
                finish();
            }
        });
        //lấy id của phòng chat trong SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("dataCookie", Context.MODE_MULTI_PROCESS);
        message_id = sharedPreferences.getString("message_id","");
        //lấy id của người nhận
        if(main_user.getPhone().equals(message_id.substring(0,10))){
            viewer = message_id.substring(11);
        }
        else {
            viewer = message_id.substring(0,10);
        }
        if(message_id.equals("") == false ){
            //Lấy tên chat box
            //Tạo liên kết đến bảng users
            DatabaseReference myUserRef = FirebaseDatabase.getInstance().getReference("users");
            //Tạo sự kiện onDatachange cho nhánh con
            myUserRef.child(viewer).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //Lấy user ra
                    User user = snapshot.getValue(User.class);
                    //Đưa tên của user lên thanh tiêu đề đoạn chat
                    txtUserChatName.setText(user.getFullname());
                    //Nếu user có avatar
                    if(!user.getAvatar().equals("")) {
                        //Đưa dữ liệu cho ảnh đại diện dùng Firebase Storage
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageReference = storage.getReference(user.getAvatar());
                        //Tạo kết nối đến Firebase Storage và add sự kiện lấy hình ảnh thành công
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //Lấy được Uri thành công. Dùng picasso để đưa hình vào Circle View ảnh đại diện
                                Picasso.get().load(uri).fit().centerCrop().into(imageProfileChat);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Thất bại thì sẽ in ra lỗi
                                Log.d("TAG", "onFailure: " + e.getMessage());
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            //Lấy hết tin nhắn của 2 người lên đồng thời kèm phân trang
            //Tạo liên kết đến bảng message_details
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("message_details");
            //Đi vào nhánh message_id và lấy 1 tin nhắn đầu tiên
            //Sự kiên addValueEventListener sẽ được lặp lại. khi 1 trong 2 người gửi 1 tin nhắn thì sự kiện sẽ được chạy
            myRef.child(message_id).limitToFirst(1).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //Lấy một tin nhắn mới nhất
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        //Đưa tin nhắn vào Arraylist
                        messageDetails.add(dataSnapshot.getValue(MessageDetails.class));
                    }
                    //Thông báo cho adapter
                    messageDetailsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            //Lấy tiếp 9 ti nhắn tiếp theo, sự kiện addListenerForSingleValueEvent chỉ chạy 1 lần kẻ cả khi có sự thay đổi trong Database
            myRef.child(message_id).limitToFirst(10).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        //Load 9 tin nhắn đầu tin khi bật lên
                        int i = 0;
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if(i == 0) {
                                i++;
                                continue;
                            }
                            //Đưa tin nhắn vào Arraylist
                            messageDetails.add(0,dataSnapshot.getValue(MessageDetails.class));
                        }
                        //Thông báo cho adapter
                        messageDetailsAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        //Sự kiện vuốt màn hình xuống
        idNestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                //Nếu kéo xuống hết cỡ thì scrolly == 0
                if (scrollY == 0) {
                    //Tăng count lên
                    count++;
                    //Số lượng tin nhắn lấy lên
                    int start_index = count*10 + 10;
                    //Lấy thêm 10 tin mỗi lần vuốt
                    //Tạo kết nối đến bảng message_details
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("message_details");
                    //Tạo sự kiện lấy tin nhắn.  limitToFirst lấy đúng số lượng cần lấy và
                    // addListenerForSingleValueEvent chỉ chạy một lần
                    myRef.child(message_id).limitToFirst(start_index).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //Hiện progressBar cho biết hệ thống đang tải
                            progressBar.setVisibility(View.VISIBLE);
                            messageDetails.clear();
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                //Đưa tin nhắn vào arraylist
                                messageDetails.add(0,dataSnapshot.getValue(MessageDetails.class));
                            }
                            //Thông báo adapter
                            messageDetailsAdapter.notifyDataSetChanged();
                            //Bỏ hiện progressBar khi tải xong
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }
        });
        //Bấm nút gửi đẻ gửi tin nhắn (dạng text)
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = inputMessage.getText().toString();
                //Nếu tin nhắn không phải rỗng
                if(message.equals("") == false){
                    //Đặt lại cho input
                    inputMessage.setText("");
                    //Tạo id cho tin nhắn
                    String message_detail_id = Long.toString(Long.MAX_VALUE - new Date().getTime());
                    //Tạo object lưu lại tin nhắn
                    MessageDetails messageDetails = new MessageDetails(message_id,main_user.getPhone(),new Date(),message, viewer);
                    //Tạo liên kết đén bảng message_details
                    DatabaseReference sendMessRef = FirebaseDatabase.getInstance().getReference("message_details");
                    //Viết vào cơ sở dữ liệu
                    sendMessRef.child(message_id).child(message_detail_id).setValue(messageDetails);
                }
            }
        });
        //Bấm vào imageview hình ảnh để tiến hành chọn ảnh và gửi
        iconMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Chọn hình ảnh trong máy
                SelectImage();
            }
        });
        //Khi nhấn giữ vào trong icon Micro thì hệ thống sẽ record
        iconMicro.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Chuẩn bị cho record
                //Đếm thời gian
                recordTimer.setBase(SystemClock.elapsedRealtime());
                recordTimer.start();
                //Ẩn các view trong thanh nhập tin nhắn
                iconCamera.setVisibility(View.INVISIBLE);
                iconMedia.setVisibility(View.INVISIBLE);
                inputMessage.setVisibility(View.INVISIBLE);
                //Hiện timer đếm thời gian
                recordTimer.setVisibility(View.VISIBLE);
                //Bắt đầu record
                startRecording();
                Toast.makeText(ChatActivity.this, "Start Recording...", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        //Bắt sự kiện click vào icon Mcro
        iconMicro.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Khi Micro được giữ lâu và đang ghi âm thì sẽ bỏ qua lệnh này
                if(mRecorder == null)
                    return false;
                //Sự kiện chính
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                    {
                        //Dừng record
                        mRecorder.stop();
                        //Hiện các view đã tắt
                        recordTimer.setVisibility(View.INVISIBLE);
                        iconMedia.setVisibility(View.VISIBLE);
                        inputMessage.setVisibility(View.VISIBLE);
                        iconCamera.setVisibility(View.VISIBLE);

                        // giải hposng cho Recorder
                        mRecorder.release();
                        mRecorder = null;
                        //Thông báo người dùng
                        Toast.makeText(ChatActivity.this, "Stop Recording...", Toast.LENGTH_SHORT).show();
                        //Tạo yêu cầu xác nhận gửi bằng AlertDialog
                        AlertDialog.Builder dialogCheck = new AlertDialog.Builder(ChatActivity.this);
                        dialogCheck.setMessage("Do you want to send this record ?");
                        //Sự kiện thi nhấn vào nút Yes
                        dialogCheck.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Đưa record lên Database
                                UploadRecord();
                            }
                        });
                        //Sự kiện khi nhấn vào nút No
                        dialogCheck.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        //Hiện dialog lên
                        dialogCheck.show();
                        return true;
                    }
                }
                return false;
            }
        });
        //Sự kiện khi nhấn vào IconCamera
        iconCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Kiểm tra quyền sử dụng máy ảnh
                if(CheckPermissions())
                {
                    //Nếu được quyền thì chạy Activity chụp ảnh
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    //Request code = 0 .
                    startActivityForResult(takePicture, 0);
                }else
                {
                    RequestPermissions();
                }
            }
        });
    }
    //lưu file record lên database
    private void UploadRecord(){
        //Lấy Uri của file
        filePath = Uri.fromFile(new File(mFileName));
        //Kiểm tra đường dẫn file
        if (filePath != null) {
            //Lấy ngày hiện tại
            Date today = new Date();
            //Lấy thời gian dạng Long
            String pic_id = Long.toString(today.getTime());
            //Tạo object lưu dữ lueeyj của tin nhắn
            MessageDetails mes = new MessageDetails(message_id, main_user.getPhone(),today,"message_records/" + message_id + "/" + pic_id,viewer);
            // Hiện ProgressDialog trong khi đang tải lên
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            //Khai báo FirebaseStorage
            StorageReference storageReference= FirebaseStorage.getInstance().getReference();
            // Đi vào nhánh con
            StorageReference ref;
            ref = storageReference.child("message_records/" + message_id).child(pic_id);
            // Tạo sự kiên cho việc upload file cả khi thành công hay thất bại và hiện thanh progress theo %
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Tải ảnh lên thành công
                                    // Tắt dialog progress đi
                                    progressDialog.dismiss();
                                    //Cập nhật lại cho bảng user về địa chỉ của avatar
                                    //Tạo id cho tin nhắn
                                    String message_detail_id = Long.toString(Long.MAX_VALUE - today.getTime());;
                                    //Kết nối đến bảng message_details
                                    DatabaseReference sendMessRef = FirebaseDatabase.getInstance().getReference("message_details");
                                    //Đưa dữ liệu lên
                                    sendMessRef.child(message_id).child(message_detail_id).setValue(mes);
                                }
                            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Lỗi, không tải lên thành công
                            // Tắt progress đi và in ra lỗi
                            progressDialog.dismiss();
                            Log.d("TAG", "onFailure: " + e.getMessage());
                            Toast.makeText(ChatActivity.this, "Update failed. Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        // Sự kiện cho Progress
                        // Hiển thị % hoàn thành
                        @Override
                        public void onProgress(
                                UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }
    // Hàm để chọn hình ảnh
    private void SelectImage() {
        // Xác định mở thư mục ảnh
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //Chạy Activity chọn ảnh
        startActivityForResult(Intent.createChooser(intent,"Select image..."), PICK_IMAGE_REQUEST);
    }
    //Sau khi chọn ảnh , chụp ảnh xong chạy vào hàm này
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Chọn ảnh
        if (requestCode == PICK_IMAGE_REQUEST    && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Lấy được uri của ảnh
            filePath = data.getData();
            //Sau đó gửi hình ảnh
            //Đưa ảnh lên Firebase Storage
            uploadImage();

        }else if (requestCode == 0 && resultCode == RESULT_OK && data != null)
        {
            //Chụp ảnh
            //Lấy bitmap của ảnh
            Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
            //Đổi bitmap thành byte array
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.PNG,100,stream);
            byte[] byteArray = stream.toByteArray();
            //Lấy ngày hiện tại để lấy dạng Long của ngày làm id cho tin nhắn
            Date today = new Date();
            String pic_id = Long.toString(today.getTime());
            //Tạo object lưu dữ liệu tin nhắn
            MessageDetails mes = new MessageDetails(message_id, main_user.getPhone(),today,"message_images/" + message_id + "/" + pic_id,viewer);
            // Hiện ProgressDialog trong khi đang tải lên
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            //Khai báo FirebaseStorage
            StorageReference storageReference= FirebaseStorage.getInstance().getReference();
            // Đi vào nhánh con
            StorageReference ref;
            ref = storageReference.child("message_images/" + message_id).child(pic_id);
            // Tạo sự kiên cho việc upload file cả khi thành công hay thất bại và hiện thanh progress theo %
            ref.putBytes(byteArray).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Tải ảnh lên thành công
                    // Tắt dialog progress đi
                    progressDialog.dismiss();
                    //Tạo id cho tin nhắn
                    String message_detail_id = Long.toString(Long.MAX_VALUE - today.getTime());
                    //Tạo kết nối đến bảng message_details
                    DatabaseReference sendMessRef = FirebaseDatabase.getInstance().getReference("message_details");
                    //Đưa dữ liệu lên
                    sendMessRef.child(message_id).child(message_detail_id).setValue(mes);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Lỗi, không tải lên thành công
                    // Tắt progress đi và in ra lỗi
                    progressDialog.dismiss();
                    Log.d("TAG", "onFailure: " + e.getMessage());
                    Toast.makeText(ChatActivity.this, "Update failed. Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    //Hiện % hoàn thành việc đưa ảnh lên
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                }
            });
        }

    }
    //Hàm này dùng để đưa ảnh lên trên firebase storage
    private void uploadImage() {
        //Kiểm tra đường dẫn file
        if (filePath != null) {
            //Lấy ngày hiện tại -> lấy dạng số của ngày làm id cho tin nhắn
            Date today = new Date();
            String pic_id = Long.toString(today.getTime());
            //Tạo object lưu thông tin tin nhắn
            MessageDetails mes = new MessageDetails(message_id, main_user.getPhone(),today,"message_images/" + message_id + "/" + pic_id,viewer);
            // Hiện ProgressDialog trong khi đang tải lên
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            //Khai báo FirebaseStorage
            StorageReference storageReference= FirebaseStorage.getInstance().getReference();
            // Đi vào nhánh con
            StorageReference ref;
            ref = storageReference.child("message_images/" + message_id).child(pic_id);
            // Tạo sự kiên cho việc upload file cả khi thành công hay thất bại và hiện thanh progress theo %
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Tải ảnh lên thành công
                                    // Tắt dialog progress đi
                                    progressDialog.dismiss();
                                    //Cập nhật lại cho bảng user về địa chỉ của avatar
                                    //Tạo id cho tin nhắn
                                    String message_detail_id = Long.toString(Long.MAX_VALUE - today.getTime());;
                                    //Tạo kết nối đến bảng message_details
                                    DatabaseReference sendMessRef = FirebaseDatabase.getInstance().getReference("message_details");
                                    //Đưa dữ liệu lên
                                    sendMessRef.child(message_id).child(message_detail_id).setValue(mes);
                                }
                            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Lỗi, không tải lên thành công
                            // Tắt progress đi và in ra lỗi
                            progressDialog.dismiss();
                            Log.d("TAG", "onFailure: " + e.getMessage());
                            Toast.makeText(ChatActivity.this, "Update failed. Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        // Sự kiện cho Progress
                        // Hiển thị % hoàn thành
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    public boolean CheckPermissions() {
        // Kiểm tra các quyền có được cấp chưa
        //Quyền viết dữ liệu vào bộ nhớ
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        //Quền sử dụng micro ghi âm
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        //Quyền dùng máy nhả
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }
    private void RequestPermissions() {
        //Xin cấp quyền từ hệ thống
        ActivityCompat.requestPermissions(ChatActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE, CAMERA}, REQUEST_AUDIO_PERMISSION_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // Sau khi ta chọn chấp nhận hay từ chối sẽ trả kết quả vào hàm này
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToCamera = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    //Nếu được chấp nhận hết
                    if (permissionToRecord && permissionToStore && permissionToCamera) {
                        //Thông báo
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        //Ngược lại thông báo denied
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
    //Khai báo biến dùng cho việc ghi âm
    private static String mFileName = null;
    private MediaRecorder mRecorder;

    private void startRecording() {
        //Kiểm tra quyền
        if (CheckPermissions()) {
            //Tạo địa chỉ file lưu
            mFileName =  Environment.getExternalStorageDirectory() + File.separator
                    + Environment.DIRECTORY_DCIM + File.separator + "AudioRecording.3gp";
            //Khởi tạo
            mRecorder = new MediaRecorder();

            //Sử dụng micro
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile(mFileName);
            try {
                // Chuẩn bị micro cho việc ghi âm
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e("TAG", "prepare() failed");
            }
            //Bắt đầu ghi âm
            mRecorder.start();
        } else {
            // Tiến hành xin cấp quyền
            RequestPermissions();
        }
    }

}