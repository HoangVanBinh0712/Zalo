package hcmute.zalo;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import hcmute.zalo.Pattern.User_SingeTon;
import hcmute.zalo.adapter.PhonebookAdapter;
import hcmute.zalo.model.LoginHistory;
import hcmute.zalo.model.PhoneBook;
import hcmute.zalo.model.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PhoneBookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhoneBookFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PhoneBookFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PhoneBookFragment newInstance(String param1, String param2) {
        PhoneBookFragment fragment = new PhoneBookFragment();
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
    private ImageView btn_addFriend,btn_updatePhonebook;
    private TextView textviewTimeUpdate;
    private ListView listviewPhonebook;
    PhonebookAdapter adapter;
    ArrayList<PhoneBook> phoneBookList;
    String id, name, phone,timeUpdate;
    //Gọi mẫu singleton lấy ra user
    User_SingeTon user_singeTon = User_SingeTon.getInstance();
    User user = user_singeTon.getUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_phone_book, container, false);
        //ánh xạ các view
        btn_addFriend = (ImageView) view.findViewById(R.id.btn_addFriend);
        btn_updatePhonebook = (ImageView) view.findViewById(R.id.btn_updatePhonebook);
        textviewTimeUpdate = (TextView) view.findViewById(R.id.textviewTimeUpdate);
        listviewPhonebook = (ListView) view.findViewById(R.id.listviewPhonebook);

        //Khai báo mảng để lưu danh bạ
        phoneBookList = new ArrayList<>();
        //Load thông tin vào listview
        adapter = new PhonebookAdapter(getActivity(),R.layout.phonebook_row,phoneBookList);
        listviewPhonebook.setAdapter(adapter);

        getListPhoneBook();
        textviewTimeUpdate.setText(timeUpdate);

        //Bấm vào nút update để lấy tất cả số điện thoại từ danh bạ
        btn_updatePhonebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Lấy thời gian cập nhập
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                timeUpdate = dateFormat.format(currentTime);
                //Lấy đường dẫn truy cập danh bạ
                Uri uri = ContactsContract.Contacts.CONTENT_URI;
                //Sắp xếp danh bạ theo thứ tự tăng dần
                String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+"ASC";
                //Lấy tất cả số điện thoại
                Cursor cursor = null;
                try {
                    cursor = getActivity().getContentResolver().query(uri,null,null,null,sort);
                }catch (Exception e){
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                //Nếu số lượng lấy ra lớn hơn 0
                if(cursor.getCount()>0){
                    while (cursor.moveToNext()){
                        //Lấy tên của số điện thoại
                        id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        //Lấy tên của số điện thoại
                        name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

                        //Lấy số điện thoại
                        Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                        String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?";
                        Cursor phoneCursor = getActivity().getContentResolver().query(
                                uriPhone,null,selection,new String[]{id},null
                        );
                        //Kiểm tra có số điện thoại
                        if(phoneCursor.moveToNext()){
                            phone = phoneCursor.getString(
                                    phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        }
                        //Tiến hành tìm kiếm trên FirebaseDatabase
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("users");
                        //Đọc và lắng nghe các thay đổi của dữ liệu
                        myRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                //Nếu số điện thoại trong danh bạ đã đăng ký tài khoản
                                if(snapshot.hasChild(phone)) {
                                    //Thêm vào database
                                    PhoneBook phoneBook = new PhoneBook(user.getPhone(),name,phone);
                                    DatabaseReference myPhoneBookRef = database.getReference("PhoneBook");
                                    myPhoneBookRef.child(user.getPhone()).child(phone).setValue(phoneBook);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        
                    }
                }
                getListPhoneBook();
            }

        });


        return view;
    }
    private void getListPhoneBook(){
        //Kết nối cơ sở dữ liệu và truy xuất vào bảng lịch sử đăng nhập
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("PhoneBook");
        //Dùng mẫu thiết kế singleTon để lưu lại user sau khi login
        user_singeTon = User_SingeTon.getInstance();
        user = user_singeTon.getUser();
        //Tìm lịch sử đăng nhập
        myRef.child(user.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Xóa mảng cũ
                phoneBookList.clear();
                //Lấy danh bạ của người dùng từ database và thêm vào mảng
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    PhoneBook phoneBook = dataSnapshot.getValue(PhoneBook.class);
                    phoneBookList.add(phoneBook);
                }
                adapter.notifyDataSetChanged();
            }
            //Không lấy được dữ liệu và thông báo
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Get phonebook failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}