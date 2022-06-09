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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import hcmute.zalo.Pattern.UserImageBitmap_SingleTon;
import hcmute.zalo.Pattern.User_SingeTon;
import hcmute.zalo.adapter.MessageDetailsAdapter;
import hcmute.zalo.model.MessageDetails;
import hcmute.zalo.model.User;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ChatActivity extends AppCompatActivity {

    AppCompatImageView sendMessageButton;
    EditText inputMessage;
    User main_user = User_SingeTon.getInstance().getUser();
    ArrayList<MessageDetails> messageDetails;
    RecyclerView rcvChat;
    MessageDetailsAdapter messageDetailsAdapter;
    NestedScrollView idNestedSV;
    int count = 0;
    ProgressBar progressBar;
    ImageView iconMedia, iconMicro;
    private int PICK_IMAGE_REQUEST = 22;
    private Uri filePath;
    String message_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chat);
        iconMicro = findViewById(R.id.iconMicro);
        iconMedia = findViewById(R.id.iconMedia);
        idNestedSV = findViewById(R.id.idNestedSV);
        progressBar = findViewById(R.id.progressBar);
        inputMessage = findViewById(R.id.inputMessage);
        sendMessageButton = findViewById(R.id.sendMessageButton);
        rcvChat = findViewById(R.id.rcvChat);
        rcvChat.setLayoutManager(new LinearLayoutManager(this));
        messageDetails = new ArrayList<>();
        messageDetailsAdapter = new MessageDetailsAdapter(ChatActivity.this,messageDetails);
        rcvChat.setAdapter(messageDetailsAdapter);

        SharedPreferences sharedPreferences = getSharedPreferences("dataCookie", Context.MODE_MULTI_PROCESS);
        message_id = sharedPreferences.getString("message_id","");
        if(message_id.equals("") == false ){
            //Lấy hết tin nhắn của 2 đứa lên đồng thời kèm phân trang
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("message_details");
            myRef.child(message_id).limitToFirst(1).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("TAG", "-------------Load 0 -----------------");
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        messageDetails.add(dataSnapshot.getValue(MessageDetails.class));
                    }
                    messageDetailsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            myRef.child(message_id).limitToFirst(10).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        //Load 9 tin nhắn đầu tin khi bật lên
                        Log.d("TAG", "-------------Load 1 - 9 -----------------");
                        int i = 0;
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if(i == 0) {
                                i++;
                                continue;
                            }
                            messageDetails.add(0,dataSnapshot.getValue(MessageDetails.class));
                        }

                        messageDetailsAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        //Sự kiện vuốt màn hình
        idNestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY == 0) {

                    count++;
                    int start_index = count*10 + 10;
                    //Load more 10 lines message
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("message_details");
                    myRef.child(message_id).limitToFirst(start_index).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            progressBar.setVisibility(View.VISIBLE);
                            messageDetails.clear();
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                messageDetails.add(0,dataSnapshot.getValue(MessageDetails.class));
                            }
                            Log.d("TAG", "Pull Down Screen Load more " );
                            messageDetailsAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }
        });

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = inputMessage.getText().toString();
                if(message.equals("") == false){
                    inputMessage.setText("");
                    String message_detail_id = Long.toString(Long.MAX_VALUE - new Date().getTime());
                    ArrayList<String> viewer = new ArrayList<>();
                    MessageDetails messageDetails = new MessageDetails(message_id,main_user.getPhone(),new Date(),message, viewer);
                    DatabaseReference sendMessRef = FirebaseDatabase.getInstance().getReference("message_details");
                    sendMessRef.child(message_id).child(message_detail_id).setValue(messageDetails);
                }
            }
        });
        iconMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });
        iconMicro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(stateAudio == false){
                    startRecording();

                }else
                {
                    Log.d("TAG", "startRecording: False");

                    mRecorder.stop();
                    // below method will release
                    // the media recorder class.
                    mRecorder.release();
                    mRecorder = null;

                    stateAudio = false;
                    UploadRecord();
                }
            }
        });
    }
    private boolean stateAudio = false;
    private void UploadRecord(){
        //mFileName
        Log.d("TAG", "UploadRecord: " + mFileName);
        filePath = Uri.fromFile(new File(mFileName));
        //Kiểm tra đường dẫn file
        if (filePath != null) {
            Date today = new Date();
            String pic_id = Long.toString(today.getTime());
            ArrayList<String> arrString = new ArrayList<>();
            MessageDetails mes = new MessageDetails(message_id, main_user.getPhone(),today,"message_records/" + message_id + "/" + pic_id,arrString);
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
                                    //Cập nhật lại cho cả user_singleTon
                                    String message_detail_id = Long.toString(Long.MAX_VALUE - today.getTime());;
                                    DatabaseReference sendMessRef = FirebaseDatabase.getInstance().getReference("message_details");
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
    private void SelectImage()
    {
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
            //Sau đó gửi hình ảnh
            //Đưa ảnh lên Firebase Storage
            uploadImage();

        }
    }
    //Hàm này dùng để đưa ảnh lên trên firebase storage
    private void uploadImage() {
        //Kiểm tra đường dẫn file
        if (filePath != null) {
            Date today = new Date();
            String pic_id = Long.toString(today.getTime());
            ArrayList<String> arrString = new ArrayList<>();
            MessageDetails mes = new MessageDetails(message_id, main_user.getPhone(),today,"message_images/" + message_id + "/" + pic_id,arrString);
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
                                    //Cập nhật lại cho cả user_singleTon
                                    String message_detail_id = Long.toString(Long.MAX_VALUE - today.getTime());;
                                    DatabaseReference sendMessRef = FirebaseDatabase.getInstance().getReference("message_details");
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
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    public boolean CheckPermissions() {
        // this method is used to check permission
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }
    private void RequestPermissions() {
        // this method is used to request the
        // permission for audio recording and storage.
        ActivityCompat.requestPermissions(ChatActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // this method is called when user will
        // grant the permission for audio recording.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
    private static String mFileName = null;
    private MediaRecorder mRecorder;

    private void startRecording() {
        // check permission method is used to check
        // that the user has granted permission
        // to record nd store the audio.
        if (CheckPermissions()) {
            stateAudio = true;

            // we are here initializing our filename variable
            // with the path of the recorded audio file.
            mFileName =  Environment.getExternalStorageDirectory() + File.separator
                    + Environment.DIRECTORY_DCIM + File.separator + "AudioRecording.3gp";

            // below method is used to initialize
            // the media recorder class
            mRecorder = new MediaRecorder();

            // below method is used to set the audio
            // source which we are using a mic.
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

            // below method is used to set
            // the output format of the audio.
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

            // below method is used to set the
            // audio encoder for our recorded audio.
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            // below method is used to set the
            // output file location for our recorded audio
            mRecorder.setOutputFile(mFileName);
            try {
                // below method will prepare
                // our audio recorder class
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e("TAG", "prepare() failed");
            }
            // start method will start
            // the audio recording.
            mRecorder.start();
            Log.d("TAG", "startRecording: True");
        } else {
            // if audio recording permissions are
            // not granted by user below method will
            // ask for runtime permission for mic and storage.
            RequestPermissions();
        }
    }
}