package hcmute.zalo.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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
            senderViewHolder.sendMessage.setText(messageDetails.getContent());
        }else{
            ReciverViewHolder reciverViewHolder = (ReciverViewHolder) holder;
            reciverViewHolder.receiveMessage.setText(messageDetails.getContent());
           // Picasso.get().load(uri).fit().centerCrop().into(holder.imageBoxChat);
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
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            sendMessage = itemView.findViewById(R.id.sendMessage);

        }
    }

    class ReciverViewHolder extends RecyclerView.ViewHolder {
        TextView receiveMessage;
        CircleImageView imgUser;
        public ReciverViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imgUser);
            receiveMessage = itemView.findViewById(R.id.receiveMessage);
        }
    }
}
