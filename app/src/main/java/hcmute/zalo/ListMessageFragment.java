package hcmute.zalo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hcmute.zalo.Pattern.User_SingeTon;
import hcmute.zalo.adapter.MessageListAdapter;
import hcmute.zalo.adapter.UserAdapter;
import hcmute.zalo.model.LoginHistory;
import hcmute.zalo.model.Message;
import hcmute.zalo.model.MessageDetails;
import hcmute.zalo.model.Participants;
import hcmute.zalo.model.PhoneBook;
import hcmute.zalo.model.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListMessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListMessageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ListMessageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListMessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListMessageFragment newInstance(String param1, String param2) {
        ListMessageFragment fragment = new ListMessageFragment();
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

    View view;
    SearchView searchView;
    ImageView btn_more;
    ListView listviewMessage;
    UserAdapter adapter;
    MessageListAdapter messageListAdapter;
    User found_user;
    User main_user;
    ArrayList<User> users = new ArrayList<>();
    ArrayList<Participants> participantsList = new ArrayList<>();

    @Override
    public void onStart() {
        super.onStart();
        users = new ArrayList<>();
        messageListAdapter = new MessageListAdapter(getActivity(),R.layout.message_row,participantsList);
        listviewMessage.setAdapter(messageListAdapter);

        getListParticipant();
        messageListAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_list_message, container, false);

        //Nếu không có user trả về trang login
        if(User_SingeTon.getInstance().getUser() == null) {
            startActivity(new Intent(getActivity(), loginActivity.class));
            getActivity().finish();
        }

        //ánh xạ
        searchView = view.findViewById(R.id.searchView);
        btn_more = (ImageView) view.findViewById(R.id.btn_more);
        listviewMessage = (ListView) view.findViewById(R.id.listviewMessage);
        btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getListParticipant();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter = new UserAdapter(getActivity(),R.layout.user_row,users);
                listviewMessage.setAdapter(adapter);
                String search_phone = newText;
                if(search_phone.length() == 10){
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users");
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //Tìm trong bảng user có người dùng đó không
                            if(snapshot.child(search_phone).exists()){
                                //Có user với số điện thoại thì hiện ra.
                                found_user = snapshot.child(search_phone).getValue(User.class);
                                users.clear();
                                users.add(found_user);
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }else if(search_phone.length() == 0) {
                    getListParticipant();
                }else{
                    users.clear();
                    adapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        listviewMessage.setClickable(true);
        listviewMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("dataCookie", Context.MODE_MULTI_PROCESS);
                //User hiện tại nhấn vào nhắn tin với user nào đó
                if(users.isEmpty()){
                    sharedPreferences.edit().putString("message_id", participantsList.get(i).getMessageid()).commit();
                    startActivity(new Intent(getActivity(),ChatActivity.class));
                }
                else {
                    User user = users.get(i);
                    //Kiểm tra có cuộc trò chuyện ? chưa có thì tạo mới và chuyển qua giao diện nhắn tin.
                    String phone = user.getPhone();
                    main_user = User_SingeTon.getInstance().getUser();
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

            }
        });
        return view;
    }
    //lấy danh sách những người mà đã nhắn tin
    private void getListParticipant(){
        //Kết nối cơ sở dữ liệu
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myParticipantRef = database.getReference("participants");

        //Dùng mẫu thiết kế singleTon để lưu lại user sau khi login
        main_user = User_SingeTon.getInstance().getUser();
        //Tìm trong bảng participants
        myParticipantRef.child(main_user.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Xóa mảng cũ
                participantsList.clear();
                //lấy danh sách những người đã nhắn tin
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Participants participants = dataSnapshot.getValue(Participants.class);
                    //System.out.println(participants.getUserPhone());
                    participantsList.add(participants);
                }
                List<Date> arrDate = new ArrayList<>();
                int len = participantsList.size();
                for(int i = 0; i < len; i++)
                {
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("message_details");
                    int finalI = i;
                    myRef.child(participantsList.get(i).getMessageid()).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                MessageDetails messageDetails = dataSnapshot.getValue(MessageDetails.class);
                                arrDate.add(messageDetails.getTimeSended());
                            }
                            if(snapshot.exists() == false)
                            {
                                arrDate.add(new Date(0,0,0));

                            }
                            if(finalI == len - 1 )
                            {
                                //Tai vong cuoi cung tien hanh sap xep
                                //Sap xep lai participantsList theo arrDate
                                //Ban đầu thứ tự là đúng
                                int len = participantsList.size();
                                for(int i = 0; i < len; i++)
                                    for(int j = 0 ; j < len-1 ; j++)
                                    {
                                        long num1 = arrDate.get(j).getTime();
                                        long num2 = arrDate.get(j+1).getTime();
                                        if(num1 < num2)
                                        {
                                            //Swap j cho j+1
                                            Participants part1 = participantsList.get(j);
                                            Participants part2 = participantsList.get(j+1);
                                            participantsList.set(j,part2);
                                            participantsList.set(j+1,part1);

                                            Date date1 = arrDate.get(j);
                                            Date date2 = arrDate.get(j+1);
                                            arrDate.set(j,date2);
                                            arrDate.set(j+1,date1);
                                        }
                                    }
                                listviewMessage.setAdapter(messageListAdapter);
                                messageListAdapter = new MessageListAdapter(getActivity(),R.layout.message_row,participantsList);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }});


            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}