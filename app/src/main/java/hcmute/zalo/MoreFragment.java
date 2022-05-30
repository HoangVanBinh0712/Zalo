package hcmute.zalo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoreFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MoreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MoreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MoreFragment newInstance(String param1, String param2) {
        MoreFragment fragment = new MoreFragment();
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
    LinearLayout linearAccount,lineartop,linearPrivacy;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = (View) inflater.inflate(R.layout.fragment_more, container, false);

        linearAccount = (LinearLayout) view.findViewById(R.id.linearAccount);
        linearAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccountFragment accountFragment = new AccountFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, accountFragment).commit();
            }
        });
        lineartop = (LinearLayout) view.findViewById(R.id.lineartop);
        lineartop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccountInformationFragment accountInformationFragment = new AccountInformationFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, accountInformationFragment).commit();
            }
        });
        linearPrivacy = (LinearLayout) view.findViewById(R.id.linearPrivacy);
        linearPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrivacyFragment privacyFragment = new PrivacyFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, privacyFragment).commit();
            }
        });
        return view;
    }
}