package hcmute.zalo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import hcmute.zalo.Pattern.User_SingeTon;
import hcmute.zalo.adapter.MessageDetailsAdapter;
import hcmute.zalo.model.MessageDetails;
import hcmute.zalo.model.User;

public class ChatActivity extends AppCompatActivity {
    AppCompatImageView sendMessageButton;
    EditText inputMessage;
    User main_user = User_SingeTon.getInstance().getUser();
    ArrayList<MessageDetails> messageDetails;
    RecyclerView rcvChat;
    MessageDetailsAdapter messageDetailsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chat);
        inputMessage = findViewById(R.id.inputMessage);
        sendMessageButton = findViewById(R.id.sendMessageButton);
        rcvChat = findViewById(R.id.rcvChat);
        rcvChat.setLayoutManager(new LinearLayoutManager(this));
        messageDetails = new ArrayList<>();
        messageDetailsAdapter = new MessageDetailsAdapter(ChatActivity.this,messageDetails);
        rcvChat.setAdapter(messageDetailsAdapter);

        SharedPreferences sharedPreferences = getSharedPreferences("dataCookie", Context.MODE_MULTI_PROCESS);
        String message_id = sharedPreferences.getString("message_id","");
        if(message_id.equals("") == false ){
            //Lấy hết tin nhắn của 2 đứa lên
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("message_details");
            myRef.child(message_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        //Có tin nhắn lấy hết lên
                        messageDetails.clear();
                        //rcvChat.setAdapter(adapter);
                        Log.d("TAG", "-------------Load-----------------");
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            messageDetails.add(dataSnapshot.getValue(MessageDetails.class));
                        }
                        messageDetailsAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = inputMessage.getText().toString();
                if(message.equals("") == false){
                    inputMessage.setText("");
                    String message_detail_id = Long.toString(new Date().getTime());
                    ArrayList<String> viewer = new ArrayList<>();
                    MessageDetails messageDetails = new MessageDetails(message_id,main_user.getPhone(),new Date(),message, viewer);
                    DatabaseReference sendMessRef = FirebaseDatabase.getInstance().getReference("message_details");
                    sendMessRef.child(message_id).child(message_detail_id).setValue(messageDetails);
                }
            }
        });

    }
}