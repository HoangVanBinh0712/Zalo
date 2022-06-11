package hcmute.zalo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import hcmute.zalo.Pattern.User_SingeTon;
import hcmute.zalo.model.MessageDetails;
import hcmute.zalo.model.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
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
    AppCompatImageView sendMessageButton;
    EditText inputMessage;
    User main_user = User_SingeTon.getInstance().getUser();
    ArrayList<MessageDetails> messageDetails;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        inputMessage = view.findViewById(R.id.inputMessage);
        sendMessageButton = view.findViewById(R.id.sendMessageButton);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("dataCookie", Context.MODE_MULTI_PROCESS);
        String message_id = sharedPreferences.getString("message_id","");
        Log.d("TAG", "onCreateView: "+ message_id);
        if(message_id.equals("") == false ){
            //Lấy hết tin nhắn của 2 đứa lên
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("message_details");
            myRef.child(message_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        messageDetails = new ArrayList<>();
                        //Có tin nhắn lấy hết lên
                        Log.d("TAG", "-------------Load-----------------");
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            messageDetails.add(dataSnapshot.getValue(MessageDetails.class));
                            Log.d("TAG", dataSnapshot.getValue(MessageDetails.class).toString());
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = inputMessage.getText().toString();
                if(message.equals("") == false){
                    String message_detail_id = UUID.randomUUID().toString();
                    String viewer="";
                    MessageDetails messageDetails = new MessageDetails(message_id,main_user.getPhone(),new Date(),message, viewer);
                    DatabaseReference sendMessRef = FirebaseDatabase.getInstance().getReference("message_details");
                    sendMessRef.child(message_id).child(message_detail_id).setValue(messageDetails);
                }
            }
        });

        return view;
    }
}