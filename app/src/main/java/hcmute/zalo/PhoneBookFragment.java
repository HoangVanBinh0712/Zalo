package hcmute.zalo;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    //Dùng để lưu thời gian lần gần nhất cập nhập từ danh bạ
    SharedPreferences sharedPreferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_phone_book, container, false);
        //ánh xạ các view
        btn_updatePhonebook = (ImageView) view.findViewById(R.id.btn_updatePhonebook);
        textviewTimeUpdate = (TextView) view.findViewById(R.id.textviewTimeUpdate);
        listviewPhonebook = (ListView) view.findViewById(R.id.listviewPhonebook);

        //Nếu không có user trả về trang login
        if(user == null) {
            startActivity(new Intent(getActivity(), loginActivity.class));
            getActivity().finish();
        }

        //Khai báo mảng để lưu danh bạ
        phoneBookList = new ArrayList<>();
        //Load thông tin vào listview
        adapter = new PhonebookAdapter(getActivity(),R.layout.phonebook_row,phoneBookList);
        listviewPhonebook.setAdapter(adapter);
        getListPhoneBook();

        listviewPhonebook.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("TAG", "onItemClick: "+ phoneBookList.get(i).getPhonebookNumber());
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("dataCookie", Context.MODE_MULTI_PROCESS);
                sharedPreferences.edit().putString("user_id", phoneBookList.get(i).getPhonebookNumber()).commit();
                startActivity(new Intent(getActivity(), ViewUserPageActivity.class));
            }
        });
        //Lấy thời gian update gần nhất để hiển thị
        sharedPreferences = getActivity().getSharedPreferences("dataTimePhonebook",MODE_PRIVATE);
        timeUpdate = sharedPreferences.getString("timeUpdate","");
        textviewTimeUpdate.setText(timeUpdate);

        //Bấm vào nút update để lấy tất cả số điện thoại từ danh bạ
        btn_updatePhonebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Lấy thời gian cập nhập
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                timeUpdate = dateFormat.format(currentTime);
                //Lưu thời gian cập nhập lên sharedPreferences
                sharedPreferences = getActivity().getSharedPreferences("dataTimePhonebook",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("timeUpdate",timeUpdate);
                editor.commit();
                textviewTimeUpdate.setText(timeUpdate);

                //Tiến hành tìm kiếm trên FirebaseDatabase
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myPhoneBookRef = database.getReference("PhoneBook");
                myPhoneBookRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //Nếu đã có danh bạ cũ thì xóa đi
                        if(snapshot.child(user.getPhone()).exists()){
                            //Xóa danh bạ cũ
                            myPhoneBookRef.child(user.getPhone()).removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                //Xin quyền truy cập
                checkPermission();
                //getContactListFromPhone();
                //Hiển thị người dùng được lưu trong danh bạ trên database
                getListPhoneBook();
                Toast.makeText(getActivity(), "Update success!!", Toast.LENGTH_SHORT).show();

            }

        });

        return view;
    }

    private void checkPermission(){
        //Xin quyền truy cập
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)
        != PackageManager.PERMISSION_GRANTED){
            //khi không được quyển truy cập
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_CONTACTS},1);
        }
        else {
            getContactListFromPhone();
        }
    }
    private void getContactListFromPhone(){
        //Lấy đường dẫn truy cập danh bạ
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        //Lấy tất cả số điện thoại
        Cursor cursor = getActivity().getContentResolver().query(uri,null,null,null,null);
        //Nếu số lượng lấy ra lớn hơn 0

//        if(cursor.moveToFirst()){
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
                if(phoneCursor.moveToNext()) {
                    phone = phoneCursor.getString(
                            phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    //Bỏ hết khoảng trắng trong sdt lấy được
                    String phone_number = phone.replace(" ", "");
                    String phone_name = name.toString();
                    //Tiến hành tìm kiếm trên FirebaseDatabase
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("users");
                    //Đọc dữ liệu
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //Nếu số điện thoại trong danh bạ đã đăng ký tài khoản
                            if (snapshot.child(phone_number).exists()) {
                                //Thêm vào database
                                PhoneBook phoneBook = new PhoneBook(user.getPhone(), phone_name, phone_number);
                                DatabaseReference myPhoneBookRef = database.getReference("PhoneBook");
                                myPhoneBookRef.child(user.getPhone()).child(phone_number).setValue(phoneBook);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        cursor.close();
    }
    //hiển thị những sdt trong database
    private void getListPhoneBook(){
        //Kết nối cơ sở dữ liệu và truy xuất vào bảng lịch sử đăng nhập
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef1 = database.getReference("PhoneBook");
        //Dùng mẫu thiết kế singleTon để lưu lại user sau khi login
        user_singeTon = User_SingeTon.getInstance();
        user = user_singeTon.getUser();
        myRef1.child(user.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Khi được quyền truy cập
        if(requestCode == 1 && grantResults.length > 0 && grantResults[0]
        == PackageManager.PERMISSION_GRANTED){
            getContactListFromPhone();
        }
        else{
            //Không được quyền truy cập -thông báo
            Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
            checkPermission();
        }
    }

}