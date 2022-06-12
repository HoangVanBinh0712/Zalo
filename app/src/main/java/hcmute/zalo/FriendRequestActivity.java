package hcmute.zalo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hcmute.zalo.Pattern.User_SingeTon;
import hcmute.zalo.adapter.FriendRequestAdapter;
import hcmute.zalo.adapter.SendedFriendRequestAdapter;
import hcmute.zalo.model.FriendRequest;
import hcmute.zalo.model.User;

public class FriendRequestActivity extends AppCompatActivity {

    ImageView btnBack;
    ListView lstFriendRequest;
    ArrayList<FriendRequest> arrFriendRequest;
    User main_user;
    TextView txtReceived,txtSended;
    FriendRequestAdapter friendRequestAdapter;
    SendedFriendRequestAdapter sendedFriendRequestAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_request_activity);
        btnBack = findViewById(R.id.btnBack);
        lstFriendRequest = findViewById(R.id.lstFriendRequest);
        txtReceived = findViewById(R.id.txtReceived);
        txtSended = findViewById(R.id.txtSended);

        txtReceived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("friend_requests");
                myRef.orderByChild("receiverPhone").startAt(main_user.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        arrFriendRequest.clear();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            FriendRequest friendRequest = dataSnapshot.getValue(FriendRequest.class);
                            if(friendRequest.getReceiverPhone().equals(main_user.getPhone()))
                                arrFriendRequest.add(friendRequest);
                            else break;
                        }
                        lstFriendRequest.setAdapter(friendRequestAdapter);
                        friendRequestAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                txtReceived.setBackgroundResource(R.drawable.top_bottom_border);
                txtReceived.setTextColor(getResources().getColor(R.color.black));
                txtSended.setBackground(null);
                txtSended.setTextColor(Color.parseColor("#808080"));
            }
        });
        txtSended.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("friend_requests");
                myRef.orderByChild("senderPhone").startAt(main_user.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        arrFriendRequest.clear();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            FriendRequest friendRequest = dataSnapshot.getValue(FriendRequest.class);
                            if(friendRequest.getSenderPhone().equals(main_user.getPhone()))
                                arrFriendRequest.add(friendRequest);
                            else break;
                        }
                        lstFriendRequest.setAdapter(sendedFriendRequestAdapter);
                        sendedFriendRequestAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                txtSended.setBackgroundResource(R.drawable.top_bottom_border);
                txtSended.setTextColor(getResources().getColor(R.color.black));
                txtReceived.setBackground(null);
                txtReceived.setTextColor(Color.parseColor("#808080"));
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        main_user = User_SingeTon.getInstance().getUser();
        arrFriendRequest = new ArrayList<>();
        friendRequestAdapter = new FriendRequestAdapter(arrFriendRequest,this,R.layout.row_friend_request);
        sendedFriendRequestAdapter = new SendedFriendRequestAdapter(arrFriendRequest,this,R.layout.row_sended_request);

        lstFriendRequest.setAdapter(friendRequestAdapter);
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("friend_requests");
        myRef.orderByChild("receiverPhone").startAt(main_user.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrFriendRequest.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    FriendRequest friendRequest = dataSnapshot.getValue(FriendRequest.class);
                    if(friendRequest.getReceiverPhone().equals(main_user.getPhone()))
                        arrFriendRequest.add(friendRequest);
                    else break;
                }
                friendRequestAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}