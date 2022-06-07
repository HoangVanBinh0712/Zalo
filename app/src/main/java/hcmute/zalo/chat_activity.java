package hcmute.zalo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import hcmute.zalo.Pattern.User_SingeTon;
import hcmute.zalo.model.Message;
import hcmute.zalo.model.MessageDetails;
import hcmute.zalo.model.User;

public class chat_activity extends AppCompatActivity {

    private Button btnTaoMessage, btnGetMessage,btnSendMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        btnTaoMessage = findViewById(R.id.btnTaoMessage);
        btnGetMessage = findViewById(R.id.btnGetMessage);
        btnSendMessage = findViewById(R.id.btnSendMessage);

        btnTaoMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Phone: 0337445596
                String phone = "0337445596";
                User user = User_SingeTon.getInstance().getUser();

                //Kiểm tra xem có cuộc hội thoại này chưa.
                //Tạo kết nối điến bảng messages
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("messages");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //Đọc dữ liệu từ cơ sở dữ liệu
                        String message_id_1 = user.getPhone() + "_" + phone;
                        String message_id_2 = phone + "_" + user.getPhone();
                        if(snapshot.child(message_id_1).exists() ||snapshot.child(message_id_2).exists())
                        {
                            //Đã có cuộc hội thoại giữa 2 người.
                            Log.d("TAG", "onDataChange: " + "Đã có");
                        }else{
                            //Chưa có hội thoại giữa 2 người
                            Log.d("TAG", "onDataChange: " + "Chưa có");
                            //Tiến hành thêm hội thoại.
                            //Tạo một message
                            Message message = new Message(message_id_1, "");
                            myRef.child(message_id_1).setValue(message);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        btnGetMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Phone: 0337445596
                String phone = "0337445596";
                User user = User_SingeTon.getInstance().getUser();

                //Kiểm tra xem có cuộc hội thoại này chưa.
                //Tạo kết nối điến bảng messages
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("messages");
                //Lấy id tin nhắn ta có 2 trướng hợp là phone1_phone2 hoặc phone2_phone1.
                String message_id_1 = user.getPhone() + "_" + phone;
                String message_id_2 = phone + "_" + user.getPhone();
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String message_id;
                        //Kiểm tra nếu có child message_id_1 thì chèn với child message_id_1
                        if(snapshot.child(message_id_1).exists()){
                            message_id = message_id_1;
                        }else{
                            //Tới đây thì message id là child message_id_2
                            message_id = message_id_2;
                        }
                        //Tach ra message_id
                        ArrayList<MessageDetails> messages = new ArrayList<>();
                        DatabaseReference sendMessRef = FirebaseDatabase.getInstance().getReference("message_details");
                        sendMessRef.child(message_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                //Load hết tin nhắn 2 đứa lên vafl logd
                                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                                {
                                    messages.add(dataSnapshot.getValue(MessageDetails.class));
                                }
                                for(int i = 0; i < messages.size();i++)
                                {
                                    Log.d("TAG", "Message: " + messages.get(i).toString());
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Phone: 0337445596
                String phone = "0337445596";
                User user = User_SingeTon.getInstance().getUser();

                //Kiểm tra xem có cuộc hội thoại này chưa.
                //Tạo kết nối điến bảng messages
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("messages");
                //Lấy id tin nhắn ta có 2 trướng hợp là phone1_phone2 hoặc phone2_phone1.
                String message_id_1 = user.getPhone() + "_" + phone;
                String message_id_2 = phone + "_" + user.getPhone();
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //Kiểm tra nếu có child message_id_1 thì chèn với child message_id_1
                        if(snapshot.child(message_id_1).exists()){
                            String message_detail_id = UUID.randomUUID().toString();
                            ArrayList<String> viewer = new ArrayList<>();
                            MessageDetails messageDetails = new MessageDetails(message_id_1,user.getPhone(),new Date(),"Xin chào kiểu 1", viewer);
                            DatabaseReference sendMessRef = FirebaseDatabase.getInstance().getReference("message_details");
                            sendMessRef.child(message_id_1).child(message_detail_id).setValue(messageDetails);
                        }else{
                            //Tới đây thì message id là child message_id_2
                            String message_detail_id = UUID.randomUUID().toString();
                            ArrayList<String> viewer = new ArrayList<>();
                            MessageDetails messageDetails = new MessageDetails(message_id_2,user.getPhone(),new Date(),"Xin chào kiểu 2", viewer);
                            DatabaseReference sendMessRef = FirebaseDatabase.getInstance().getReference("message_details");
                            sendMessRef.child(message_id_2).child(message_detail_id).setValue(messageDetails);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
}