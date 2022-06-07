package hcmute.zalo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.zalo.Pattern.UserImageBitmap_SingleTon;
import hcmute.zalo.Pattern.User_SingeTon;

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
    private CircleImageView profile_image;
    private TextView txtUserPhone,txtUserName;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = (View) inflater.inflate(R.layout.fragment_more, container, false);
        profile_image = view.findViewById(R.id.profile_image);
        txtUserPhone = view.findViewById(R.id.txtUserPhone);
        txtUserName = view.findViewById(R.id.txtUserName);
        linearAccount = (LinearLayout) view.findViewById(R.id.linearAccount);
        lineartop = (LinearLayout) view.findViewById(R.id.lineartop);
        linearPrivacy = (LinearLayout) view.findViewById(R.id.linearPrivacy);
        UserImageBitmap_SingleTon userImageBitmap_singleTon = UserImageBitmap_SingleTon.getInstance();
        User_SingeTon user_singeTon = User_SingeTon.getInstance();

        linearAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccountFragment accountFragment = new AccountFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, accountFragment).commit();
            }
        });

        lineartop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AccountInformationFragment accountInformationFragment = new AccountInformationFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, accountInformationFragment).commit();
            }
        });

        linearPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrivacyFragment privacyFragment = new PrivacyFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, privacyFragment).commit();
            }
        });
        //Kiểm tra nếu đã có ảnh mới thực hiện lấy ảnh đại diện
        if(!user_singeTon.getUser().getAvatar().equals("")) {
            Bitmap avatar = userImageBitmap_singleTon.getAnhdaidien();
            if (avatar == null) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReference(user_singeTon.getUser().getAvatar());
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //Lấy được Uri thành công. Dùng picasso để đưa hình vào Circle View ảnh đại diện
                        Picasso.get().load(uri).fit().centerCrop().into(profile_image);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Thất bại thì sẽ in ra lỗi
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                profile_image.setImageBitmap(avatar);
            }
        }

        txtUserName.setText(user_singeTon.getUser().getFullname());
        txtUserPhone.setText(user_singeTon.getUser().getPhone());


        return view;
    }
}