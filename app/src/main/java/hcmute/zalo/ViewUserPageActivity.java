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

    //Khai báo SharedPreferences
    SharedPreferences sharedPreferences;
    //Khai báo các view
    ImageView btnBack, btnAddFriend, btnChat;
    ImageView background;
    CircleImageView avatar;
    TextView txtFullName, txtDescription;
    //Khai báo các biến
    User user, main_user;
    Uri uriAvatar, uriBackground;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_user_page);
        //Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences("dataCookie",MODE_MULTI_PROCESS);
        //Lấy dữ liêu từ SharedPreferences
        String user_id = sharedPreferences.getString("user_id","");
        //Lấy dữ liệu từ singleTon
        main_user = User_SingeTon.getInstance().getUser();
        //Ánh xạ
        btnBack = findViewById(R.id.btnBack);
        btnAddFriend = findViewById(R.id.btnAddFriend);
        btnChat = findViewById(R.id.btnChat);
        background = findViewById(R.id.background);
        avatar = findViewById(R.id.avatar);
        txtFullName = findViewById(R.id.txtFullName);
        txtDescription = findViewById(R.id.txtDescription);

        //Bắt sự kiện onclick
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Tắt activity hiện tại
                finish();
            }
        });
        //Bắt sự kiện onclick
        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Tạo lời mời kết bạn
                if(user == null) return;
                //Tạo id ngẫu nhiên
                String req_id = UUID.randomUUID().toString();
                //Tạo đối tượng  FriendRequest lưu thông tin
                FriendRequest fq = new FriendRequest(main_user.getPhone(),main_user.getFullname(),user.getPhone(),user.getFullname(),"Become friend with me");
                //Tạo kết nối đến bảng friend_requests
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("friend_requests");
                //Đưa dữ liệu vào
                myRef.child(req_id).setValue(fq);
                //Tắt button btnAddFriend đi khi đã gửi lời mời kết bạn
                btnAddFriend.setVisibility(View.INVISIBLE);
                //Thông báo người dùng
                Toast.makeText(ViewUserPageActivity.this, "Send Request Successfully to " + user.getFullname(), Toast.LENGTH_SHORT).show();
            }
        });
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user == null) return;
                //Kiểm tra xem có cuộc hội thoại này chưa.
                //Tạo kết nối điến bảng messages
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("messages");
                //Tạo sự kiện chạy 1 lần
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //Kiểm tra nếu người dùng nhấn vào chính mình
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
                            Log.d("TAG", "Đã có" + message_id_1);
                            sharedPreferences.edit().putString("message_id", message_id_1).commit();
                        } else if (snapshot.child(message_id_2).exists()) {
                            Log.d("TAG", "Đã có" + message_id_2);
                            sharedPreferences.edit().putString("message_id", message_id_2).commit();

                        } else {
                            //Chưa có hội thoại giữa 2 người
                            Log.d("TAG", "Chưa có và tiến hành tạo");
                            //Tiến hành thêm hội thoại.
                            //Tạo một message
                            Message message = new Message(message_id_1, user.getFullname());
                            //Thêm vào bảng message
                            myRef.child(message_id_1).setValue(message);
                            //Thêm vào bảng participants cho cả 2 người
                            DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("participants");
                            //Thêm cho người thứ nhất
                            //Tạo đối tượng Participants
                            Participants participants1 = new Participants(message_id_1, user_id);
                            newRef.child(main_user.getPhone()).child(message_id_1).setValue(participants1);
                            //Thêm cho người thứ 2
                            //Tạo đối tượng Participants
                            Participants participants2 = new Participants(message_id_1, main_user.getPhone());
                            newRef.child(user_id).child(message_id_1).setValue(participants2);
                            //Đưa dữ liệu về message_id_1 lên sharedPreferences để sử dụng cho việc lấy tin nhắn của 2 người
                            sharedPreferences.edit().putString("message_id", message_id_1).commit();

                        }
                        //Mở activity nhắn tin
                        startActivity(new Intent(ViewUserPageActivity.this, ChatActivity.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        //Khi nhấn vào ảnh avatar thì ảnh sẽ hiện to lên
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Mở activity ZoomImageActivity và truyền Uri của ảnh vào để lấy từ trên FirebaseStorage xuống
                Intent zoomImageIntent = new Intent(ViewUserPageActivity.this,ZoomImageActivity.class);
                //Nếu không có ảnh thì lấy mặc định
                if(uriAvatar!=null) zoomImageIntent.putExtra("uri",uriAvatar.toString());
                else
                    zoomImageIntent.putExtra("uri","https://firebasestorage.googleapis.com/v0/b/zalo-b0715.appspot.com/o/man.png?alt=media&token=f5d3a7fb-3863-4aac-9fad-c8b7791f7331");
                startActivity(zoomImageIntent);
            }
        });
        //Khi nhấn vào ảnh background thì ảnh sẽ hiện to lên
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Mở activity ZoomImageActivity và truyền Uri của ảnh vào để lấy từ trên FirebaseStorage xuống
                Intent zoomImageIntent = new Intent(ViewUserPageActivity.this,ZoomImageActivity.class);
                //Nếu không có ảnh thì lấy mặc định
                if(uriBackground != null)
                    zoomImageIntent.putExtra("uri",uriBackground.toString());
                else
                    zoomImageIntent.putExtra("uri","https://firebasestorage.googleapis.com/v0/b/zalo-b0715.appspot.com/o/thanhpho.jpg?alt=media&token=0819ef67-3c4a-48a1-8666-08703cef74b3");
                startActivity(zoomImageIntent);
            }
        });
        //Tạo kết nối đến bảng users để đọc
        //addListenerForSingleValueEvent hàm này chạy 1 lần duy nhất không bị lặp khi dữ liệu thay đổi
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child("users").child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Nếu có nhánh thì mới thực hiện tránh trường hợp không có users
                if(snapshot.exists()) {
                    //Lấy user xuống
                    user = snapshot.getValue(User.class);
                    //Đưa tên và mô tả và trong textview
                    txtFullName.setText(user.getFullname());
                    txtDescription.setText(user.getDescription());
                    //Nêu có hình ảnh
                    if (!user.getAvatar().equals("")) {
                        //Tạo kết nối đến StorageReference
                        StorageReference myStorageAvatar = FirebaseStorage.getInstance().getReference(user.getAvatar());
                        //Sự kiện khi tải xuống thành công
                        myStorageAvatar.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                uriAvatar = uri;
                                Picasso.get().load(uri).into(avatar);
                            }
                        });
                    }
                    if (!user.getBackground().equals("")) {
                        //Tạo kết nối đến StorageReference
                        StorageReference myStorageBackground = FirebaseStorage.getInstance().getReference(user.getBackground());
                        //Sự kiện khi tải xuống thành công
                        myStorageBackground.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                uriBackground = uri;
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
        //Kết nối FirebaseDatabase
        DatabaseReference myRef1 = FirebaseDatabase.getInstance().getReference();
        //Đọc dữ liệu từ nhánh friends -> main_user.getPhone() ->
        myRef1.child("friends").child(main_user.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(user_id).exists())
                {
                    //Nếu đã kết bạn thì ẩn nút kết bạn đi
                    btnAddFriend.setVisibility(View.INVISIBLE);
                }else
                {
                    //Kiểm tra có gửi lời mời kết bạn chưa nếu rồi thì cũng ẩn nút kết bạn đi
                    //Kết nối bảng friend_requests
                    DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("friend_requests");
                    //Đọc dữ liệu, xoay theo nhánh senderphone
                    //Thay vì duyệt một lần hết tất cả các lời mời kết bạn. Tiến hành xoay nhánh để nhóm những lời mời kết bạn do người dùng gửi liên tục và bắt đầu từ đó
                    //Tiết kiệm thời gian, dung lượng, khối lượng công việc
                    //addListenerForSingleValueEvent : chạy 1 lần
                    myRef2.orderByChild("senderPhone").startAt(main_user.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //Vòng lặp for để lấy ra
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                FriendRequest friendRequest = dataSnapshot.getValue(FriendRequest.class);
                                //Nếu khác thì dừng việc lặp lại vì từ đó về sau không còn do người dùng hiện tại gửi nữa
                                if(!friendRequest.getSenderPhone().equals(main_user.getPhone()))
                                    break;
                                else {
                                    if(friendRequest.getSenderPhone().equals(main_user.getPhone()) && friendRequest.getReceiverPhone().equals(user_id))
                                    {
                                        //Đã gửi rồi
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