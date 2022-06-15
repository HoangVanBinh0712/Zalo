package hcmute.zalo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.zalo.Pattern.User_SingeTon;
import hcmute.zalo.model.FriendRequest;
import hcmute.zalo.model.Message;
import hcmute.zalo.model.Participants;
import hcmute.zalo.model.User;

public class ViewUserPageActivity extends AppCompatActivity {

    //Khai báo
    //sharedPreferences để lấy số điện thoại được truyền qua
    SharedPreferences sharedPreferences;
    //btnBack là nút trở lại, btnAddFriend để kết bạn, btnChat để trò chuyện
    ImageView btnBack, btnAddFriend, btnChat;
    // background Hiện ảnh bìa
    ImageView background;
    //avatar Hiện ảnh đại diện
    CircleImageView avatar;
    //txtFullName, txtDescription hiện tên và mô tả
    TextView txtFullName, txtDescription;
    //Lưu thông tin của người dùng hiện tại (main_user) và người dùng đang xem trang cá nhân
    User user, main_user;
    //Lưu uri của ảnh bìa, ảnh đại diện
    Uri uriAvatar, uriBackground;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_user_page);
        //Gọi getSharedPreferences lấy user_id
        sharedPreferences = getSharedPreferences("dataCookie",MODE_MULTI_PROCESS);
        String user_id = sharedPreferences.getString("user_id","");
        //Gọi SingleTon lấy thông tin người dùng hiện tại
        main_user = User_SingeTon.getInstance().getUser();
        //Ánh xạ các view
        btnBack = findViewById(R.id.btnBack);
        btnAddFriend = findViewById(R.id.btnAddFriend);
        btnChat = findViewById(R.id.btnChat);
        background = findViewById(R.id.background);
        avatar = findViewById(R.id.avatar);
        txtFullName = findViewById(R.id.txtFullName);
        txtDescription = findViewById(R.id.txtDescription);

        // Sự kiên nhấn vào nút BACK. Trở về trang trước
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // Sự kiện nhấn vào nút kết bạn. Gửi lời mời kết bạn
        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Tạo lời mời kết bạn
                if(user == null) return;
                //Tạo id lời mời
                String req_id = UUID.randomUUID().toString();
                //Tạo object lưu thông tin lời mời
                FriendRequest fq = new FriendRequest(main_user.getPhone(),main_user.getFullname(),user.getPhone(),user.getFullname(),"Become friend with me");
                //Tạo kết nối firebase database
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("friend_requests");
                //Lưu dữ liệu
                myRef.child(req_id).setValue(fq);
                //Ẩn nút kết bạn đi
                btnAddFriend.setVisibility(View.INVISIBLE);
                //Thông báo thành công
                Toast.makeText(ViewUserPageActivity.this, "Send Request Successfully to " + user.getFullname(), Toast.LENGTH_SHORT).show();
            }
        });
        // Sự kiên nhấn vào nút Chat. Chuyển sang trang trò chuyện
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user == null) return;
                //Kiểm tra xem có cuộc hội thoại này chưa.
                //Tạo kết nối điến bảng messages
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("messages");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (user_id.equals(main_user.getPhone())) {
                            Toast.makeText(ViewUserPageActivity.this, "Cant Message to yourself !", Toast.LENGTH_SHORT).show();
                            return;

                        }
                        //Đọc dữ liệu từ cơ sở dữ liệu
                        //Lấy id tin nhắn ta có 2 trướng hợp là phone1_phone2 hoặc phone2_phone1.
                        SharedPreferences sharedPreferences = getSharedPreferences("dataCookie", Context.MODE_MULTI_PROCESS);
                        String message_id_1 = main_user.getPhone() + "_" + user_id;
                        String message_id_2 = user_id + "_" + main_user.getPhone();
                        if (snapshot.child(message_id_1).exists()) {

                            //Đã có cuộc hội thoại giữa 2 người.
                            sharedPreferences.edit().putString("message_id", message_id_1).commit();
                        } else if (snapshot.child(message_id_2).exists()) {
                            sharedPreferences.edit().putString("message_id", message_id_2).commit();

                        } else {
                            //Chưa có hội thoại giữa 2 người
                            //Tiến hành thêm hội thoại.
                            //Tạo một message
                            Message message = new Message(message_id_1, user.getFullname());
                            myRef.child(message_id_1).setValue(message);
                            //Thêm vào bảng participants cho cả 2 người

                            DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("participants");
                            Participants participants1 = new Participants(message_id_1, user_id);
                            newRef.child(main_user.getPhone()).child(message_id_1).setValue(participants1);
                            Participants participants2 = new Participants(message_id_1, main_user.getPhone());
                            newRef.child(user_id).child(message_id_1).setValue(participants2);
                            sharedPreferences.edit().putString("message_id", message_id_1).commit();

                        }
                        //Chuyển sang trang trò chuyện
                        startActivity(new Intent(ViewUserPageActivity.this, ChatActivity.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        //Nhấn vào ảnh đại diện để xem phóng to
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Tạo intent
                Intent zoomImageIntent = new Intent(ViewUserPageActivity.this,ZoomImageActivity.class);
                //Đưa dữ liệu ảnh vào extra nếu có và mặc định nếu không có
                if(uriAvatar!=null) zoomImageIntent.putExtra("uri",uriAvatar.toString());
                else
                    zoomImageIntent.putExtra("uri","https://firebasestorage.googleapis.com/v0/b/zalo-b0715.appspot.com/o/man.png?alt=media&token=f5d3a7fb-3863-4aac-9fad-c8b7791f7331");
                //Mở activity ZoomImageActivity lên
                startActivity(zoomImageIntent);
            }
        });
        //Nhấn vào ảnh bìa để xem phóng to
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Tạo intent

                Intent zoomImageIntent = new Intent(ViewUserPageActivity.this,ZoomImageActivity.class);
                //Đưa dữ liệu ảnh vào extra nếu có và mặc định nếu không có
                if(uriBackground != null)
                    zoomImageIntent.putExtra("uri",uriBackground.toString());
                else
                    zoomImageIntent.putExtra("uri","https://firebasestorage.googleapis.com/v0/b/zalo-b0715.appspot.com/o/thanhpho.jpg?alt=media&token=0819ef67-3c4a-48a1-8666-08703cef74b3");
                //Mở activity ZoomImageActivity lên

                startActivity(zoomImageIntent);
            }
        });
        //Kết nối cơ sở dữ liệu
        //Đọc để lấy thông tin về người dùng được xem trang cá nhân
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child("users").child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    //Nếu tồn tại nhánh đó. tức là số điện thoại của người được xem trang cá nhân có được đăng ký
                    //Lấy dữ liệu đưa vào user
                    user = snapshot.getValue(User.class);
                    //Đưa dữ liệu lên các view để hiển thị
                    txtFullName.setText(user.getFullname());
                    txtDescription.setText(user.getDescription());
                    if (!user.getAvatar().equals("")) {
                        //Nếu có ảnh đại diện
                        //Kết nối storage và lấy về
                        StorageReference myStorageAvatar = FirebaseStorage.getInstance().getReference(user.getAvatar());
                        myStorageAvatar.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                uriAvatar = uri;
                                //Đưa ảnh vào avatar
                                Picasso.get().load(uri).into(avatar);
                            }
                        });
                    }
                    if (!user.getBackground().equals("")) {
                        //Nếu có ảnh bìa
                        //Kết nối storage và lấy về
                        StorageReference myStorageBackground = FirebaseStorage.getInstance().getReference(user.getBackground());
                        myStorageBackground.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                uriBackground = uri;
                                //Đưa ảnh vào background
                                Picasso.get().load(uri).into(background);
                            }
                        });
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //Kiểm tra xem kết bạn chưa nếu chưa thì hiện nút kết bạn
        //Kết nói đến bảng friends và kiểm tra
        DatabaseReference myRef1 = FirebaseDatabase.getInstance().getReference();
        myRef1.child("friends").child(main_user.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(user_id).exists())
                {
                    //Nếu đã là bạn
                    btnAddFriend.setVisibility(View.INVISIBLE);
                }else
                {
                    //Kiểm tra có gửi lời mời kết bạn chưa
                    //Kết nối bảng friend_requests
                    DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("friend_requests");
                    //Xoay nhánh theo senderphone.
                    //Việc sử dụng id random ta không biết đâu là của người dùng a, đâu là của người dùng b gửi. Nên xoay theo nhánh senderPhone thì các
                    //Lời mời của người dùng a gửi sẽ được hiện liên tiếp nhau
                    myRef2.orderByChild("senderPhone").startAt(main_user.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //Kiểm tra xem trong các lời mời kết bạn mà người dùng đã gửi có của người dùng đang xem trang cá nhân không
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                FriendRequest friendRequest = dataSnapshot.getValue(FriendRequest.class);
                                //Nếu số điện thoại người gửi không phải của người dùng thì dừng vòng lặp
                                if(!friendRequest.getSenderPhone().equals(main_user.getPhone()))
                                    break;
                                else {
                                    //Kiểm tra có gửi chưa
                                    if(friendRequest.getSenderPhone().equals(main_user.getPhone()) && friendRequest.getReceiverPhone().equals(user_id))
                                    {
                                        //Đã gửi rồi thì ẩn nút đi
                                        btnAddFriend.setVisibility(View.INVISIBLE);
                                        break;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}