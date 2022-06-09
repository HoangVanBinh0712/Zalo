package hcmute.zalo.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.zalo.Pattern.User_SingeTon;
import hcmute.zalo.R;
import hcmute.zalo.model.MessageDetails;
import hcmute.zalo.model.User;

public class MessageDetailsAdapter extends RecyclerView.Adapter {
    public MessageDetailsAdapter(Context context, ArrayList<MessageDetails> arrMessDetails) {
        this.context = context;
        this.arrMessDetails = arrMessDetails;
    }

    Context context;
    ArrayList<MessageDetails> arrMessDetails;
    int ITEM_SEND = 1;
    int ITEM_RECIVE=2;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_SEND)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.message_adapter_sent,parent,false);
            return new SenderViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.message_adapter_received,parent,false);
            return new ReciverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageDetails messageDetails = arrMessDetails.get(position);
        if(holder.getClass() == SenderViewHolder.class){

            SenderViewHolder senderViewHolder = (SenderViewHolder) holder;
            //message_records 1 trường hợp để tải record lên
            if(messageDetails.getContent().startsWith("message_images/"+ messageDetails.getMessageId())){
                //Picture
                StorageReference storageReference = FirebaseStorage.getInstance().getReference(messageDetails.getContent());
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).fit().centerCrop().into(senderViewHolder.chatPicture);
                    }
                });
                senderViewHolder.sendMessage.setVisibility(View.INVISIBLE);
                senderViewHolder.chatPicture.setVisibility(View.VISIBLE);

            }else
            {
                senderViewHolder.sendMessage.setText(messageDetails.getContent());
                senderViewHolder.sendMessage.setVisibility(View.VISIBLE);
                senderViewHolder.chatPicture.setVisibility(View.INVISIBLE);

            }
            SimpleDateFormat simpleDateFormat;

            Date today = new Date();
            if(today.getYear() == messageDetails.getTimeSended().getYear()
            && today.getMonth() == messageDetails.getTimeSended().getMonth()
                    && today.getDate() == messageDetails.getTimeSended().getDate())
            {
                //Chỉ hiện thời gian bỏ đi ngày
                simpleDateFormat = new SimpleDateFormat("hh:mm");;
            }else
            {
                simpleDateFormat = new SimpleDateFormat("dd-M hh:mm");
            }
            senderViewHolder.txtTimeSent.setText(simpleDateFormat.format(messageDetails.getTimeSended()));
        }else{
            ReciverViewHolder reciverViewHolder = (ReciverViewHolder) holder;
            if(messageDetails.getContent().startsWith("message_images/"+ messageDetails.getMessageId())){
                //Picture
                StorageReference storageReference = FirebaseStorage.getInstance().getReference(messageDetails.getContent());
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).fit().centerCrop().into(reciverViewHolder.chatPicture);
                    }
                });
                reciverViewHolder.receiveMessage.setVisibility(View.INVISIBLE);
                reciverViewHolder.chatPicture.setVisibility(View.VISIBLE);

            }else
            {
                reciverViewHolder.receiveMessage.setText(messageDetails.getContent());
                reciverViewHolder.receiveMessage.setVisibility(View.VISIBLE);
                reciverViewHolder.chatPicture.setVisibility(View.INVISIBLE);

            }
            reciverViewHolder.receiveMessage.setText(messageDetails.getContent());
           // Picasso.get().load(uri).fit().centerCrop().into(holder.imageBoxChat);
            SimpleDateFormat simpleDateFormat;

            Date today = new Date();
            if(today.getYear() == messageDetails.getTimeSended().getYear()
                    && today.getMonth() == messageDetails.getTimeSended().getMonth()
                    && today.getDate() == messageDetails.getTimeSended().getDate())
            {
                //Chỉ hiện thời gian bỏ đi ngày
                simpleDateFormat = new SimpleDateFormat("hh:mm");;
            }else
            {
                simpleDateFormat = new SimpleDateFormat("dd-M hh:mm");
            }
            reciverViewHolder.receiveTime.setText(simpleDateFormat.format(messageDetails.getTimeSended()));
        }
    }

    @Override
    public int getItemCount() {
        return arrMessDetails.size();
    }

    @Override
    public int getItemViewType(int position) {
        User user = User_SingeTon.getInstance().getUser();
        MessageDetails messageDetails = arrMessDetails.get(position);
        if(messageDetails.getSenderPhone().equals(user.getPhone()))
        {
            return ITEM_SEND;
        }else
        {
            return ITEM_RECIVE;
        }
    }

    class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView sendMessage;
        TextView txtTimeSent;
        ImageView chatPicture;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            sendMessage = itemView.findViewById(R.id.sendMessage);
            txtTimeSent = itemView.findViewById(R.id.txtTimeSent);
            chatPicture = itemView.findViewById(R.id.chatPicture);
        }
    }

    class ReciverViewHolder extends RecyclerView.ViewHolder {
        TextView receiveMessage;
        CircleImageView imgUser;
        TextView receiveTime;
        ImageView chatPicture;
        public ReciverViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imgUser);
            receiveMessage = itemView.findViewById(R.id.receiveMessage);
            receiveTime = itemView.findViewById(R.id.receiveTime);
            chatPicture = itemView.findViewById(R.id.chatPicture);
        }
    }
}
