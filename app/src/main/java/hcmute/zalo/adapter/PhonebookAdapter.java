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

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.zalo.R;
import hcmute.zalo.model.LoginHistory;
import hcmute.zalo.model.PhoneBook;
import hcmute.zalo.model.User;

public class PhonebookAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<PhoneBook> lstPhonebook;

    public PhonebookAdapter(Context context, int layout, List<PhoneBook> lstPhonebook) {
        this.context = context;
        this.layout = layout;
        this.lstPhonebook = lstPhonebook;
    }

    @Override
    public int getCount() {
        return lstPhonebook.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    private class ViewHolder{
        CircleImageView phonebook_image;
        TextView phonebookName, phonebookNumber;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        //Kiểm tra xem đã có view chưa
        //Nếu chưa thì thiết lập các object view
        if(view == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout,null);

            //Ánh xạ các view
            holder.phonebook_image = (CircleImageView) view.findViewById(R.id.phonebook_image);
            holder.phonebookName = (TextView) view.findViewById(R.id.phonebookName);
            holder.phonebookNumber = (TextView) view.findViewById(R.id.phonebookNumber);

            view.setTag(holder);
        }
        else{
            holder = (ViewHolder) view.getTag();
        }
        //lấy dữ liệu từ các list đã được truyền vào
        final PhoneBook phoneBook = lstPhonebook.get(position);
        holder.phonebookName.setText(phoneBook.getPhonebookName());
        holder.phonebookNumber.setText(phoneBook.getPhonebookNumber());

        //Lấy ảnh đại diện
        //Tiến hành tìm kiếm trên FirebaseDatabase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        //Tìm kiếm trong bảng usersv
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //lấy user trong database
                DataSnapshot dataSnapshot = snapshot.child(phoneBook.getPhonebookNumber());
                User user = dataSnapshot.getValue(User.class);
                //Kiểm tra nếu đã có ảnh mới thực hiện lấy ảnh đại diện
                if(!user.getAvatar().equals("")) {
                    //Đưa dữ liệu cho ảnh đại diện dùng Firebase Storage
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReference(user.getAvatar());
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //Lấy được Uri thành công. Dùng picasso để đưa hình vào Circle View ảnh đại diện
                            Picasso.get().load(uri).fit().centerCrop().into(holder.phonebook_image);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Thất bại thì sẽ in ra lỗi
                            Log.d("TAG", "onFailure: " + e.getMessage());
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}
