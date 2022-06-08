package hcmute.zalo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import hcmute.zalo.Pattern.UserImageBitmap_SingleTon;
import hcmute.zalo.Pattern.User_SingeTon;
import hcmute.zalo.model.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PasswordFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PasswordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PasswordFragment newInstance(String param1, String param2) {
        PasswordFragment fragment = new PasswordFragment();
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
    ImageView btnBackChangePassword;
    TextView txtShowPassword;
    EditText edittextCurrentPassword,edittextNewPassword,edittextConfirmNewPassword;
    Button btnConfirmChangePassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = (View)inflater.inflate(R.layout.fragment_password, container, false);
        //Ánh xạ các view
        btnBackChangePassword = (ImageView) view.findViewById(R.id.btnBackChangePassword);
        txtShowPassword = (TextView) view.findViewById(R.id.txtShowPassword);
        edittextCurrentPassword = (EditText) view.findViewById(R.id.edittextCurrentPassword);
        edittextNewPassword = (EditText) view.findViewById(R.id.edittextNewPassword);
        edittextConfirmNewPassword = (EditText) view.findViewById(R.id.edittextConfirmNewPassword);
        btnConfirmChangePassword = (Button) view.findViewById(R.id.btnConfirmChangePassword);

        //Bấm nút back để quay lại Account setting
        btnBackChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccountFragment accountFragment = new AccountFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, accountFragment).commit();
            }
        });
        //Bấm nút show để hiện hoặc ẩn tất cả các password
        txtShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Nếu người dùng muốn hiện mật khẩu
                if(txtShowPassword.getText().toString().equals("SHOW")) {
                    //Chuyển các edittext sang kiểu text
                    edittextCurrentPassword.setInputType(1);
                    edittextNewPassword.setInputType(1);
                    edittextConfirmNewPassword.setInputType(1);
                    txtShowPassword.setText("HIDE");
                }
                //Nếu người dùng muốn ẩn mật khẩu
                else{
                    //Chuyển các edittext sang kiểu password
                    edittextCurrentPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                    edittextNewPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                    edittextConfirmNewPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                    txtShowPassword.setText("SHOW");
                }
            }
        });
        //Bấm nút Update để thực hiện thay đổi password
        btnConfirmChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Tạo dialog process đang đăng nhập
                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setTitle("Please wait...");
                progressDialog.show();
                //Lấy mật khẩu bên view
                String currentPassword = edittextCurrentPassword.getText().toString();
                String newPassword = edittextNewPassword.getText().toString();
                String confirmNewPassword = edittextConfirmNewPassword.getText().toString();

                //Dùng mẫu thiết kế singleTon để lưu lại user sau khi login
                User_SingeTon user_singeTon = User_SingeTon.getInstance();
                User user = user_singeTon.getUser();

                //Kiểm tra mật khẩu hiện tại người dùng nhập có đúng không
                if(!currentPassword.equals(user.getPassword())){
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Incorrect current password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Kiểm tra mật khẩu mới không được trống và phải có ít nhất 6 ký tự
                if(newPassword.isEmpty() || newPassword.length()<6){
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Invalid password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Kiểm tra mật khẩu mới và mật khẩu xác nhận
                //Không giống nhau
                if(!newPassword.equals(confirmNewPassword)){
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "The confirm password does not match!!", Toast.LENGTH_SHORT).show();
                }
                //Giống nhau
                else{
                    //Cập nhật thay đổi lên FirebaseDatabase và user
                    //Cập nhật cho user
                    user.setPassword(newPassword);
                    //Cập nhật cho database
                    //Gọi kết nối đến FirebaseDatabase vào bảng users
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("users");
                    //Truy cập đến nhánh có id là số điện thoại và cập nhật lại thông tin
                    myRef.child(user.getPhone()).setValue(user);
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Change password successful.", Toast.LENGTH_SHORT).show();
                    logout();
                }
            }
        });

        return view;
    }
    private void logout(){
        //Gán User là null và trả về trang login
        User_SingeTon user_singeTon = User_SingeTon.getInstance();
        user_singeTon.setUser(null);
        user_singeTon = null;
        UserImageBitmap_SingleTon userImageBitmap_singleTon = UserImageBitmap_SingleTon.getInstance();
        userImageBitmap_singleTon.setAnhbia(null);
        userImageBitmap_singleTon.setAnhdaidien(null);
        userImageBitmap_singleTon = null;

        //Trả về trang login
        startActivity(new Intent(getActivity(),loginActivity.class));
        getActivity().finish();
    }
}