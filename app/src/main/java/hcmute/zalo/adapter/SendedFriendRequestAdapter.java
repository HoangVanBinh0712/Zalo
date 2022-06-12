package hcmute.zalo.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.zalo.Pattern.User_SingeTon;
import hcmute.zalo.R;
import hcmute.zalo.model.FriendRequest;
import hcmute.zalo.model.Friends;
import hcmute.zalo.model.User;

public class SendedFriendRequestAdapter extends BaseAdapter {
    public SendedFriendRequestAdapter(ArrayList<FriendRequest> arrFriendRequest, Context context, int layout) {
        this.arrFriendRequest = arrFriendRequest;
        this.context = context;
        this.layout = layout;
    }

    private ArrayList<FriendRequest> arrFriendRequest;
    private Context context;
    private int layout ;

    @Override
    public int getCount() {
        return arrFriendRequest.size();
    }

    @Override
    public Object getItem(int i) {
        return arrFriendRequest.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
    class Holder {
        CircleImageView senderRequestImageView;
        TextView txtSenderRequestName,txtRequestDay;
        Button btnCancel;
    }
    User main_user;
    DatabaseReference myRef;
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder holder;
        if(view == null) {
            holder = new Holder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout,null);
            holder.senderRequestImageView = (CircleImageView) view.findViewById(R.id.senderRequestImageView);
            holder.txtSenderRequestName = (TextView) view.findViewById(R.id.txtSenderRequestName);
            holder.txtRequestDay = (TextView)view.findViewById(R.id.txtRequestDay);
            holder.btnCancel = (Button)view.findViewById(R.id.btnCancel);
            view.setTag(holder);
        }
        else{
            holder = (Holder) view.getTag();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-mm-yyyy");;
        main_user = User_SingeTon.getInstance().getUser();

        FriendRequest friendRequest = arrFriendRequest.get(i);
        holder.txtSenderRequestName.setText(friendRequest.getReceiverName());
        holder.txtRequestDay.setText(simpleDateFormat.format(friendRequest.getDateRequest()));
        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("friend_requests");
                myRef.orderByChild("senderPhone").startAt(main_user.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren())
                        {
                            if(dataSnapshot.getValue(FriendRequest.class).getReceiverPhone().equals(friendRequest.getReceiverPhone()))
                            {
                                String req_id = dataSnapshot.getKey();
                                myRef.child(req_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        arrFriendRequest.remove(i);
                                        notifyDataSetChanged();
                                        Toast.makeText(context, "Cancel Friend Request Successfully !", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            }
                        }
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
