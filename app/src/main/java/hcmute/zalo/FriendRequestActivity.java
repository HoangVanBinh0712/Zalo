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
import android.widget.Toast;

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

    //Khởi tạo các View
    ImageView btnBack;
    //List view hiển thị danh sách cái lời mời kết bạn, đã gửi
    ListView lstFriendRequest;
    //Textview để hiển thị cho người dùng biết mình đang chọn chức năng xem lời mời nhận hay lời mời đã gửi
    TextView txtReceived,txtSended;
    //Nếu không có lời mời nào thì hiện txtNoRequest: không có lời mời
    TextView txtNoRequest;
    //Khở tạo các biến sử dụng
    //mảng lưu các lời mời
    ArrayList<FriendRequest> arrFriendRequest;
    //Biến lưu thông tin người dùng
    User main_user;
    //Firebase: Biến kết nối và viết, đọc dữ liệu
    FriendRequestAdapter friendRequestAdapter;
    SendedFriendRequestAdapter sendedFriendRequestAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_request_activity);

        //Ánh xạ
        btnBack = findViewById(R.id.btnBack);
        lstFriendRequest = findViewById(R.id.lstFriendRequest);
        txtReceived = findViewById(R.id.txtReceived);
        txtSended = findViewById(R.id.txtSended);
        txtNoRequest = findViewById(R.id.txtNoRequest);
        //Bắt sự kiện onclick. Nhấn vào hiện danh sách các lời mời được nhận
        txtReceived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Kết nối đến bảng friend_request
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("friend_requests");
                //Đọc dữ liệu, xoay nhánh theo receiverPhone
                //Thay vì duyệt một lần hết tất cả các lời mời kết bạn. Tiến hành xoay nhánh để nhóm những lời mời kết bạn cho người dùng liên tục và bắt đầu từ đó
                //Tiết kiệm thời gian, dung lượng, khối lượng công việc
                myRef.orderByChild("receiverPhone").startAt(main_user.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //Làm mới arrayList
                        arrFriendRequest.clear();
                        //Lặp vòng for để lấy
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            FriendRequest friendRequest = dataSnapshot.getValue(FriendRequest.class);
                            //Nếu là lời mời cho người dùng thì lấy. khi không phải cho người dùng thì hủy vòng lặp
                            if(friendRequest.getReceiverPhone().equals(main_user.getPhone()))
                                arrFriendRequest.add(friendRequest);
                            else break;
                        }
                        if (arrFriendRequest.size() == 0){
                            //Nếu không có lời mời hiển thị ra
                            txtNoRequest.setText("No received request");
                            txtNoRequest.setVisibility(View.VISIBLE);
                            lstFriendRequest.setVisibility(View.INVISIBLE);
                        } else {
                            txtNoRequest.setVisibility(View.INVISIBLE);
                            lstFriendRequest.setVisibility(View.VISIBLE);
                        }
                        //Set adapter cho listview và thông báo dữ liệu thay đổi cho adapter
                        lstFriendRequest.setAdapter(friendRequestAdapter);
                        friendRequestAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                //Làm cho các textview được gạch chân khi click vào
                txtReceived.setBackgroundResource(R.drawable.top_bottom_border);
                txtReceived.setTextColor(getResources().getColor(R.color.black));
                txtSended.setBackground(null);
                txtSended.setTextColor(Color.parseColor("#808080"));
            }
        });
        //Bắt sự kiên onclick.Nhấn vào hiện danh sách các lời mời đã gửi
        txtSended.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Kết nối đến bảng friend_request
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("friend_requests");
                //Đọc dữ liệu, xoay nhánh theo senderPhone
                //Thay vì duyệt một lần hết tất cả các lời mời kết bạn. Tiến hành xoay nhánh để nhóm những lời mời kết bạn người dùng đã gửi liên tục và bắt đầu từ đó
                //Tiết kiệm thời gian, dung lượng, khối lượng công việc
                myRef.orderByChild("senderPhone").startAt(main_user.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //Làm mới arraylist
                        arrFriendRequest.clear();
                        //Lặp vòng for để lấy
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            FriendRequest friendRequest = dataSnapshot.getValue(FriendRequest.class);
                            //Nếu là lời mời cho người dùng thì lấy. khi không phải cho người dùng thì hủy vòng lặp
                            if(friendRequest.getSenderPhone().equals(main_user.getPhone()))
                                arrFriendRequest.add(friendRequest);
                            else break;
                        }
                        if (arrFriendRequest.size() == 0){
                            //Nếu không có lời mời hiển thị ra
                            txtNoRequest.setText("No sended request");
                            txtNoRequest.setVisibility(View.VISIBLE);
                            lstFriendRequest.setVisibility(View.INVISIBLE);
                        } else {
                            txtNoRequest.setVisibility(View.INVISIBLE);
                            lstFriendRequest.setVisibility(View.VISIBLE);
                        }
                        //Set adapter cho listview và thông báo dữ liệu thay đổi cho adapter

                        lstFriendRequest.setAdapter(sendedFriendRequestAdapter);
                        sendedFriendRequestAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                //Làm cho các textview được gạch chân khi click vào
                txtSended.setBackgroundResource(R.drawable.top_bottom_border);
                txtSended.setTextColor(getResources().getColor(R.color.black));
                txtReceived.setBackground(null);
                txtReceived.setTextColor(Color.parseColor("#808080"));
            }
        });
        //Bắt sự kiện cho nút back. Tắt activity hiện tại đi
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Đóng activity
                finish();
            }
        });
        //Lấy Người sử dụng ứng dụng đã đăng nhập
        main_user = User_SingeTon.getInstance().getUser();
        //Khởi tạo ArrayList
        arrFriendRequest = new ArrayList<>();
        //Khởi tạo adapter
        friendRequestAdapter = new FriendRequestAdapter(arrFriendRequest,this,R.layout.row_friend_request);
        sendedFriendRequestAdapter = new SendedFriendRequestAdapter(arrFriendRequest,this,R.layout.row_sended_request);
        //Set adapter cho listview mặc định là lời mời kết bạn gửi đến
        lstFriendRequest.setAdapter(friendRequestAdapter);
        //Kết nối bảng friend_requests
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("friend_requests");
        //Đọc dữ liệu, xoay nhánh theo receiverPhone
        //Thay vì duyệt một lần hết tất cả các lời mời kết bạn. Tiến hành xoay nhánh để nhóm những lời mời kết bạn cho người dùng liên tục và bắt đầu từ đó
        //Tiết kiệm thời gian, dung lượng, khối lượng công việc
        //Tương tự như hàm onclick ở trên
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
                if (arrFriendRequest.size() == 0){
                    //Nếu không có lời mời hiển thị
                    txtNoRequest.setText("No received request");
                    txtNoRequest.setVisibility(View.VISIBLE);
                    lstFriendRequest.setVisibility(View.INVISIBLE);
                } else {
                    txtNoRequest.setVisibility(View.INVISIBLE);
                    lstFriendRequest.setVisibility(View.VISIBLE);
                }
                friendRequestAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}