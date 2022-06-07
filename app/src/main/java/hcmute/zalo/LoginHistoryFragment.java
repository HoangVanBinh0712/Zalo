package hcmute.zalo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import hcmute.zalo.Pattern.User_SingeTon;
import hcmute.zalo.adapter.LoginHistoryAdapter;
import hcmute.zalo.model.LoginHistory;
import hcmute.zalo.model.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginHistoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginHistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginHistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginHistoryFragment newInstance(String param1, String param2) {
        LoginHistoryFragment fragment = new LoginHistoryFragment();
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

    private View view;
    private ImageView btnBackPageHistory;
    private ListView listviewHistory;
    LoginHistoryAdapter adapter;
    ArrayList<LoginHistory> historyList;
    User user;
    User_SingeTon user_singeTon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_login_history, container, false);
        //Ánh xạ các object view
        btnBackPageHistory = (ImageView) view.findViewById(R.id.btnBackPageHistory);
        listviewHistory = (ListView) view.findViewById(R.id.listviewHistory);

        //Khai báo mảng để lưu lịch sử đăng nhập
        historyList = new ArrayList<>();
        //Load lịch sử đăng nhập vào listview
        adapter = new LoginHistoryAdapter(getActivity(),R.layout.history_row,historyList);
        listviewHistory.setAdapter(adapter);

        //Bắt sự kiện khi bấm vào nút back
        btnBackPageHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Chuyển sang page Account
                AccountFragment accountFragment = new AccountFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,accountFragment).commit();

            }
        });

        //Lấy danh sách lịch sử đăng nhập từ database
        getListHistory();

        return view;
    }

    private void getListHistory() {
        //Kết nối cơ sở dữ liệu và truy xuất vào bảng lịch sử đăng nhập
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("loginHistory");

        //Dùng mẫu thiết kế singleTon để lưu lại user sau khi login
        user_singeTon = User_SingeTon.getInstance();
        user = user_singeTon.getUser();

        //Tìm trong bảng lịch sử đăng nhập
        myRef.child(user.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Xóa mảng cũ
                historyList.clear();
                //Lấy lịch sử đảng nhâp của người dùng từ database và thêm vào mảng
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    LoginHistory history = dataSnapshot.getValue(LoginHistory.class);
                    historyList.add(history);
                }
                adapter.notifyDataSetChanged();
            }
            //Không lấy được dữ liệu và thông báo
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Get login history failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}