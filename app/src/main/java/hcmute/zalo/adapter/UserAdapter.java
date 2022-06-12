package hcmute.zalo.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import hcmute.zalo.ChatFragment;
import hcmute.zalo.MainActivity;
import hcmute.zalo.Pattern.User_SingeTon;
import hcmute.zalo.R;
import hcmute.zalo.model.FriendRequest;
import hcmute.zalo.model.LoginHistory;
import hcmute.zalo.model.Message;
import hcmute.zalo.model.MessageDetails;
import hcmute.zalo.model.User;

public class UserAdapter extends BaseAdapter {

    public UserAdapter(Context context, int layout, ArrayList<User> arrUser) {
        this.context = context;
        this.layout = layout;
        this.arrUser = arrUser;
    }

    private Context context;
    private int layout;
    private ArrayList<User> arrUser;
    @Override
    public int getCount() {
        return arrUser.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
    private class ViewHolder{
        TextView txtUserPhone;
        ImageView btnAddFriend;
        ShapeableImageView imageBoxChat;
        ConstraintLayout constraintRowUser;
    }
    User main_user;

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        main_user = User_SingeTon.getInstance().getUser();
        UserAdapter.ViewHolder holder;
        if(view == null) {
            holder = new UserAdapter.ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout,null);
            holder.constraintRowUser = (ConstraintLayout) view.findViewById(R.id.constraintRowUser);
            holder.txtUserPhone = (TextView) view.findViewById(R.id.txtUserPhone);
            holder.btnAddFriend = (ImageView) view.findViewById(R.id.btnAddFriend);
            holder.imageBoxChat = (ShapeableImageView) view.findViewById(R.id.imageBoxChat);
            view.setTag(holder);
        }
        else{
            holder = (UserAdapter.ViewHolder) view.getTag();
        }
        User user = arrUser.get(i);
        holder.txtUserPhone.setText(user.getFullname());
        //Kiểm tra xem kết bạn chưa nếu chưa thì hiện nút kết bạn
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child("friends").child(main_user.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(user.getPhone()).exists())
                {
                    holder.btnAddFriend.setVisibility(View.INVISIBLE);
                }else
                {
                    //Kiểm tra có gửi lời mời kết bạn chưa
                    DatabaseReference myRef1 = FirebaseDatabase.getInstance().getReference("friend_requests");
                    myRef1.orderByChild("senderPhone").startAt(main_user.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                FriendRequest friendRequest = dataSnapshot.getValue(FriendRequest.class);
                                if(!friendRequest.getSenderPhone().equals(main_user.getPhone()))
                                    break;
                                else {
                                    if(friendRequest.getSenderPhone().equals(main_user.getPhone()) && friendRequest.getReceiverPhone().equals(user.getPhone()))
                                    {
                                        //Đã gửi rồi
                                        holder.btnAddFriend.setVisibility(View.INVISIBLE);
                                        break;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Tạo lời mời kết bạn
                String req_id = UUID.randomUUID().toString();
                FriendRequest fq = new FriendRequest(main_user.getPhone(),main_user.getFullname(),user.getPhone(),user.getFullname(),"Become friend with me");
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("friend_requests");
                myRef.child(req_id).setValue(fq);
                holder.btnAddFriend.setVisibility(View.INVISIBLE);
                Toast.makeText(context, "Send Request Successfully to " + user.getFullname(), Toast.LENGTH_SHORT).show();
            }
        });

        //Kiểm tra nếu đã có ảnh mới thực hiện lấy ảnh đại diện
        if(!user.getAvatar().equals("")) {
            //Đưa dữ liệu cho ảnh đại diện dùng Firebase Storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference(user.getAvatar());
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    //Lấy được Uri thành công. Dùng picasso để đưa hình vào Circle View ảnh đại diện
                    Picasso.get().load(uri).fit().centerCrop().into(holder.imageBoxChat);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Thất bại thì sẽ in ra lỗi
                    Log.d("TAG", "onFailure: " + e.getMessage());
                }
            });
        }

        return view;
    }
}
