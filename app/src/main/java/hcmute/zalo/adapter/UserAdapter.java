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

import hcmute.zalo.Pattern.User_SingeTon;
import hcmute.zalo.R;
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
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
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
        holder.txtUserPhone.setText(user.getDescription());
        holder.btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Gui loi moi ket ban
                Log.d("TAG", "onClick: Gui loi moi ket ban den "+ user.getPhone());
            }
        });
        holder.constraintRowUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Kiểm tra có cuộc trò chuyện ? chưa có thì tạo mới và chuyển qua giao diện nhắn tin.
                String phone = user.getPhone();
                User main_user = User_SingeTon.getInstance().getUser();
                if(phone.equals(main_user.getPhone()))
                    return;
                //Kiểm tra xem có cuộc hội thoại này chưa.
                //Tạo kết nối điến bảng messages
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("messages");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //Đọc dữ liệu từ cơ sở dữ liệu
                        //Lấy id tin nhắn ta có 2 trướng hợp là phone1_phone2 hoặc phone2_phone1.
                        String message_id_1 = main_user.getPhone() + "_" + phone;
                        String message_id_2 = phone + "_" + main_user.getPhone();
                        if(snapshot.child(message_id_1).exists() ||snapshot.child(message_id_2).exists())
                        {
                            //Đã có cuộc hội thoại giữa 2 người.
                            Log.d("TAG", "onDataChange: " + "Đã có");
                        }else{
                            //Chưa có hội thoại giữa 2 người
                            Log.d("TAG", "onDataChange: " + "Chưa có");
                            //Tiến hành thêm hội thoại.
                            //Tạo một message
                            Message message = new Message(message_id_1, "");
                            myRef.child(message_id_1).setValue(message);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
