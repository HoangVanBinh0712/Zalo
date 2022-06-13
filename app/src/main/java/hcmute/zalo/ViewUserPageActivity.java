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

    SharedPreferences sharedPreferences;
    ImageView btnBack, btnAddFriend, btnChat;
    ImageView background;
    CircleImageView avatar;
    TextView txtFullName, txtDescription;
    User user, main_user;
    Uri uriAvatar, uriBackground;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_user_page);
        sharedPreferences = getSharedPreferences("dataCookie",MODE_MULTI_PROCESS);
        String user_id = sharedPreferences.getString("user_id","");
        main_user = User_SingeTon.getInstance().getUser();
        btnBack = findViewById(R.id.btnBack);
        btnAddFriend = findViewById(R.id.btnAddFriend);
        btnChat = findViewById(R.id.btnChat);
        background = findViewById(R.id.background);
        avatar = findViewById(R.id.avatar);
        txtFullName = findViewById(R.id.txtFullName);
        txtDescription = findViewById(R.id.txtDescription);

        //
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Tạo lời mời kết bạn
                if(user == null) return;
                String req_id = UUID.randomUUID().toString();
                FriendRequest fq = new FriendRequest(main_user.getPhone(),main_user.getFullname(),user.getPhone(),user.getFullname(),"Become friend with me");
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("friend_requests");
                myRef.child(req_id).setValue(fq);
                btnAddFriend.setVisibility(View.INVISIBLE);
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
                            myRef.child(message_id_1).setValue(message);
                            //Thêm vào bảng participants cho cả 2 người

                            DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("participants");
                            Participants participants1 = new Participants(message_id_1, user_id);
                            newRef.child(main_user.getPhone()).child(message_id_1).setValue(participants1);
                            Participants participants2 = new Participants(message_id_1, main_user.getPhone());
                            newRef.child(user_id).child(message_id_1).setValue(participants2);
                            sharedPreferences.edit().putString("message_id", message_id_1).commit();

                        }
                        startActivity(new Intent(ViewUserPageActivity.this, ChatActivity.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent zoomImageIntent = new Intent(ViewUserPageActivity.this,ZoomImageActivity.class);
                if(uriAvatar!=null) zoomImageIntent.putExtra("uri",uriAvatar.toString());
                else
                    zoomImageIntent.putExtra("uri","https://firebasestorage.googleapis.com/v0/b/zalo-b0715.appspot.com/o/man.png?alt=media&token=f5d3a7fb-3863-4aac-9fad-c8b7791f7331");
                startActivity(zoomImageIntent);
            }
        });
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent zoomImageIntent = new Intent(ViewUserPageActivity.this,ZoomImageActivity.class);
                if(uriBackground != null)
                    zoomImageIntent.putExtra("uri",uriBackground.toString());
                else
                    zoomImageIntent.putExtra("uri","https://firebasestorage.googleapis.com/v0/b/zalo-b0715.appspot.com/o/thanhpho.jpg?alt=media&token=0819ef67-3c4a-48a1-8666-08703cef74b3");
                startActivity(zoomImageIntent);
            }
        });
        //

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child("users").child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    user = snapshot.getValue(User.class);
                    txtFullName.setText(user.getFullname());
                    txtDescription.setText(user.getDescription());
                    if (!user.getAvatar().equals("")) {
                        StorageReference myStorageAvatar = FirebaseStorage.getInstance().getReference(user.getAvatar());
                        myStorageAvatar.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                uriAvatar = uri;
                                Picasso.get().load(uri).into(avatar);
                            }
                        });
                    }
                    if (!user.getBackground().equals("")) {

                        StorageReference myStorageBackground = FirebaseStorage.getInstance().getReference(user.getBackground());
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
        DatabaseReference myRef1 = FirebaseDatabase.getInstance().getReference();
        myRef1.child("friends").child(main_user.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(user_id).exists())
                {
                    btnAddFriend.setVisibility(View.INVISIBLE);
                }else
                {
                    //Kiểm tra có gửi lời mời kết bạn chưa
                    DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("friend_requests");
                    myRef2.orderByChild("senderPhone").startAt(main_user.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                FriendRequest friendRequest = dataSnapshot.getValue(FriendRequest.class);
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