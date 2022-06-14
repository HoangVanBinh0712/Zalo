package hcmute.zalo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import hcmute.zalo.adapter.UserAdapter;
import hcmute.zalo.model.Friends;
import hcmute.zalo.model.Message;
import hcmute.zalo.model.Participants;
import hcmute.zalo.model.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ContactsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactsFragment newInstance(String param1, String param2) {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    //Khai báo các view sử dụng
    View view;
    ListView lstFriends;
    TextView txtNofriend;
    //Khai báo adapter
    UserAdapter userAdapter;
    //Khai báo biến
    ArrayList<User> arrUser;
    User main_user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Ánh xạ
        view = inflater.inflate(R.layout.fragment_contacts, container, false);
        txtNofriend = view.findViewById(R.id.txtNofriend);
        lstFriends = view.findViewById(R.id.lstFriends);
        //Khởi tạo mảng
        arrUser = new ArrayList<>();
        //Khởi tạo adapter
        userAdapter = new UserAdapter(getActivity(),R.layout.user_row,arrUser);
        //Set adapter cho listview
        lstFriends.setAdapter(userAdapter);
        //Bắt sự kiện click vào listview Item
        lstFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Mở trang cá nhân của đối tượng lên
                //Lưu user_id vào sharedPreferences
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("dataCookie", Context.MODE_MULTI_PROCESS);
                sharedPreferences.edit().putString("user_id", arrUser.get(i).getPhone()).commit();
                //Chạy activity
                startActivity(new Intent(getActivity(), ViewUserPageActivity.class));
            }
        });
        //Lấy danh sách bạn bè
        //Sử dụng Mẫu SingleTon lấy user hiện tại ra
        main_user = User_SingeTon.getInstance().getUser();
        //Kết nối đến Database
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        //Tạo sự kiện cho nhánh con trong bảng friends/ addListenerForSingleValueEvent chỉ chạy 1 lần
        myRef.child("friends").child(main_user.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Xóa hết phần tử trong mảng arrUser
                arrUser.clear();
                //Vòng lặp để lấy danh sách bạn bè
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //Ánh xạ qua bảng users để lấy thông tin
                    String user_phone = dataSnapshot.getValue(Friends.class).getFriendPhone();
                    //Tạo kết nối bảng users
                    DatabaseReference myRef1 = FirebaseDatabase.getInstance().getReference("users");
                    //Add sự kiện để đọc dữ liệu.
                    myRef1.child(user_phone).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //Đưa user vào trong Arraylist
                            arrUser.add(snapshot.getValue(User.class));
                            lstFriends.setVisibility(View.VISIBLE);
                            txtNofriend.setVisibility(View.INVISIBLE);
                            userAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if(arrUser.size() == 0)
                {
                    txtNofriend.setVisibility(View.VISIBLE);
                    lstFriends.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        lstFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = arrUser.get(position);
                //Kiểm tra có cuộc trò chuyện ? chưa có thì tạo mới và chuyển qua giao diện nhắn tin.
                String phone = user.getPhone();
                if (phone.equals(main_user.getPhone())) {
                    Toast.makeText(getActivity(), "Cant Message to yourself !", Toast.LENGTH_SHORT).show();
                    return;

                }
                //Kiểm tra xem có cuộc hội thoại này chưa.
                //Tạo kết nối điến bảng messages
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("messages");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //Đọc dữ liệu từ cơ sở dữ liệu
                        //Lấy id tin nhắn ta có 2 trướng hợp là phone1_phone2 hoặc phone2_phone1.
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("dataCookie", Context.MODE_MULTI_PROCESS);
                        String message_id_1 = main_user.getPhone() + "_" + phone;
                        String message_id_2 = phone + "_" + main_user.getPhone();
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
                            Participants participants1 = new Participants(message_id_1, phone);
                            newRef.child(main_user.getPhone()).child(message_id_1).setValue(participants1);
                            Participants participants2 = new Participants(message_id_1, main_user.getPhone());
                            newRef.child(phone).child(message_id_1).setValue(participants2);
                            sharedPreferences.edit().putString("message_id", message_id_1).commit();

                        }
                        startActivity(new Intent(getActivity(), ChatActivity.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        return view;
    }
}