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
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hcmute.zalo.Pattern.User_SingeTon;
import hcmute.zalo.adapter.UserAdapter;
import hcmute.zalo.model.Message;
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
    ListView listviewMessage;
    UserAdapter adapter;
    User found_user;
    ArrayList<User> users = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_list_message, container, false);
        searchView = view.findViewById(R.id.searchView);

        listviewMessage = (ListView) view.findViewById(R.id.listviewMessage);
        adapter = new UserAdapter(getActivity(),R.layout.user_row,users);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
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
                }else{
                    users.clear();
                    adapter.notifyDataSetChanged();
                }
                return false;
            }
        });


        listviewMessage.setAdapter(adapter);
        listviewMessage.setClickable(true);
        listviewMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //User hiện tại nhấn vào nhắn tin với user nào đó
                User user = users.get(i);
                //Kiểm tra có cuộc trò chuyện ? chưa có thì tạo mới và chuyển qua giao diện nhắn tin.
                String phone = user.getPhone();
                User main_user = User_SingeTon.getInstance().getUser();
                if(phone.equals(main_user.getPhone()))
                {
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
                        if(snapshot.child(message_id_1).exists())
                        {

                            //Đã có cuộc hội thoại giữa 2 người.
                            Log.d("TAG",  "Đã có" + message_id_1);
                            sharedPreferences.edit().putString("message_id", message_id_1).commit();
                        }else if(snapshot.child(message_id_2).exists())
                        {
                            Log.d("TAG",  "Đã có" + message_id_2);
                            sharedPreferences.edit().putString("message_id", message_id_2).commit();

                        }
                        else{
                            //Chưa có hội thoại giữa 2 người
                            Log.d("TAG", "Chưa có và tiến hành tạo");
                            //Tiến hành thêm hội thoại.
                            //Tạo một message
                            Message message = new Message(message_id_1, user.getFullname());
                            myRef.child(message_id_1).setValue(message);
                            //Thêm vào bảng participants cho cả 2 người
                            DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("participants");
                            newRef.child(main_user.getPhone()).child("message").setValue(phone);
                            newRef.child(phone).child("message").setValue(main_user.getPhone());
                            sharedPreferences.edit().putString("message_id", message_id_1).commit();

                        }
//                        //Đá qua chat fragment
//                        ChatFragment chatFragment = new ChatFragment();
//                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, chatFragment).commit();
                        startActivity(new Intent(getActivity(),ChatActivity.class));
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