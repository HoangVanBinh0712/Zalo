package hcmute.zalo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import hcmute.zalo.Pattern.UserImageBitmap_SingleTon;
import hcmute.zalo.Pattern.User_SingeTon;
import hcmute.zalo.model.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
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
    ImageView btnBack;
    TextView txtChangePassword, txtLoginHistory,txtLogOut,txtAddFriendRequest,txtPhonenumber;
    LinearLayout linearChangeNumber;
    User main_user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //Anh xạ các view
        main_user = User_SingeTon.getInstance().getUser();
        view = (View)inflater.inflate(R.layout.fragment_account, container, false);
        btnBack = (ImageView) view.findViewById(R.id.btnBack);
        txtChangePassword = (TextView) view.findViewById(R.id.txtChangePassword);
        linearChangeNumber = (LinearLayout) view.findViewById(R.id.linearChangeNumber);
        txtLoginHistory = (TextView) view.findViewById(R.id.txtLoginHistory);
        txtLogOut = view.findViewById(R.id.txtLogOut);
        txtAddFriendRequest = view.findViewById(R.id.txtAddFriendRequest);
        txtPhonenumber = view.findViewById(R.id.txtPhonenumber);
        txtPhonenumber.setText("(+84) " + main_user.getPhone());
        //bắt sự kiện onclick cho nút back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Trả về MoreFragment
                MoreFragment moreFragment = new MoreFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, moreFragment).commit();
            }
        });
        //bắt sự kiện onclick khi nhấn vào change password
        txtChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Mở PasswordFragment lên thay thế vào FrameLayout trong activity_main
                PasswordFragment passwordFragment = new PasswordFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, passwordFragment).commit();
            }
        });
        //Bắt sự kiện onClick của textview linearChangeNumber
        linearChangeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Mở PhoneNumberFragment lên thay thế vào FrameLayout trong activity_main
                PhoneNumberFragment phoneNumberFragment = new PhoneNumberFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, phoneNumberFragment).commit();
            }
        });
        //Bắt sự kiện onClick của textview txtLoginHistory
        txtLoginHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Mở LoginHistoryFragment lên thay thế vào FrameLayout trong activity_main
                LoginHistoryFragment loginHistoryFragment = new LoginHistoryFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,loginHistoryFragment).commit();
            }
        });
        //Bắt sự kiện onClick của textview txtLogout
        txtLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Gán User là null và trả về trang login
                //Gán user trong singleTon là null
                User_SingeTon user_singeTon = User_SingeTon.getInstance();
                user_singeTon.setUser(null);
                user_singeTon = null;
                //Gán hình ảnh lưu thành null
                UserImageBitmap_SingleTon userImageBitmap_singleTon = UserImageBitmap_SingleTon.getInstance();
                userImageBitmap_singleTon.setAnhbia(null);
                userImageBitmap_singleTon.setAnhdaidien(null);
                userImageBitmap_singleTon = null;

                //Trả về trang login
                startActivity(new Intent(getActivity(),loginActivity.class));
                getActivity().finish();

            }
        });
        //Bắt sự kiện onClick của textview txtAddFriendRequest
        txtAddFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Mở activity FriendRequestActivity
                startActivity(new Intent(getActivity(), FriendRequestActivity.class));
            }
        });
        return view;
    }
}