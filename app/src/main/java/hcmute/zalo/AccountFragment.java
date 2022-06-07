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
    TextView txtChangePassword, txtLoginHistory,txtLogOut;
    LinearLayout linearChangeNumber;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = (View)inflater.inflate(R.layout.fragment_account, container, false);
        btnBack = (ImageView) view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoreFragment moreFragment = new MoreFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, moreFragment).commit();
            }
        });
        txtChangePassword = (TextView) view.findViewById(R.id.txtChangePassword);
        txtChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PasswordFragment passwordFragment = new PasswordFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, passwordFragment).commit();
            }
        });
        linearChangeNumber = (LinearLayout) view.findViewById(R.id.linearChangeNumber);
        linearChangeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneNumberFragment phoneNumberFragment = new PhoneNumberFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, phoneNumberFragment).commit();
            }
        });

        txtLoginHistory = (TextView) view.findViewById(R.id.txtLoginHistory);
        txtLoginHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginHistoryFragment loginHistoryFragment = new LoginHistoryFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,loginHistoryFragment).commit();
            }
        });
        txtLogOut = view.findViewById(R.id.txtLogOut);
        txtLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });
        return view;
    }
}