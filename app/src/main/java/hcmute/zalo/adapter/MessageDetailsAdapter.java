package hcmute.zalo.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

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

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.zalo.Pattern.User_SingeTon;
import hcmute.zalo.R;
import hcmute.zalo.ZoomImageActivity;
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
    int ITEM_RECEIVE=2;
    ScheduledExecutorService timer;
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
                        senderViewHolder.sendImageUri = uri;
                        Picasso.get().load(uri).fit().centerCrop().into(senderViewHolder.chatPicture);
                    }
                });
                senderViewHolder.sendMessage.setVisibility(View.INVISIBLE);
                senderViewHolder.chatPicture.setVisibility(View.VISIBLE);
                senderViewHolder.linearAudioSend.setVisibility(View.INVISIBLE);

                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(senderViewHolder.sendParent_layout);
                constraintSet.connect(R.id.chatPicture,ConstraintSet.RIGHT,R.id.txtTimeSent,ConstraintSet.RIGHT,0);
                constraintSet.connect(R.id.txtTimeSent,ConstraintSet.TOP,R.id.chatPicture,ConstraintSet.BOTTOM,0);
                constraintSet.applyTo(senderViewHolder.sendParent_layout);

            }
            else if(messageDetails.getContent().startsWith("message_records/"+ messageDetails.getMessageId())){
                StorageReference storageReference = FirebaseStorage.getInstance().getReference(messageDetails.getContent());
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        senderViewHolder.sendMediaPlayer = new MediaPlayer();
                        senderViewHolder.sendMediaPlayer.setAudioAttributes(
                                new AudioAttributes.Builder()
                                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                        .setUsage(AudioAttributes.USAGE_MEDIA)
                                        .build()
                                );
                        try{
                            senderViewHolder.sendMediaPlayer.setDataSource(context.getApplicationContext(),uri);
                            senderViewHolder.sendMediaPlayer.prepare();

                            int millis = senderViewHolder.sendMediaPlayer.getDuration();
                            senderViewHolder.seekbarSend.setMax(millis);

                            senderViewHolder.sendMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    timer.shutdown();
                                    senderViewHolder.seekbarSend.setProgress(0);
                                    senderViewHolder.btn_actionAudioSend.setImageResource(R.drawable.ic_play);
                                }
                            });

                        }catch (IOException e){
                            Toast.makeText(context, "Error "+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                senderViewHolder.linearAudioSend.setVisibility(View.VISIBLE);
                senderViewHolder.chatPicture.setVisibility(View.INVISIBLE);
                senderViewHolder.sendMessage.setVisibility(View.INVISIBLE);
            }
            else{
                senderViewHolder.sendMessage.setText(messageDetails.getContent());
                senderViewHolder.sendMessage.setVisibility(View.VISIBLE);
                senderViewHolder.chatPicture.setVisibility(View.INVISIBLE);
                senderViewHolder.linearAudioSend.setVisibility(View.INVISIBLE);

            }
            senderViewHolder.btn_actionAudioSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(senderViewHolder.sendMediaPlayer!=null){
                        if(senderViewHolder.sendMediaPlayer.isPlaying()) {
                            senderViewHolder.sendMediaPlayer.pause();
                            senderViewHolder.btn_actionAudioSend.setImageResource(R.drawable.ic_play);
                            timer.shutdown();
                        }
                        else {
                            senderViewHolder.sendMediaPlayer.start();
                            senderViewHolder.btn_actionAudioSend.setImageResource(R.drawable.ic_pause);

                            timer = Executors.newScheduledThreadPool(1);
                            timer.scheduleAtFixedRate(new Runnable() {
                                @Override
                                public void run() {
                                    senderViewHolder.seekbarSend.setProgress(senderViewHolder.sendMediaPlayer.getCurrentPosition());
                                }
                            }, 10, 10, TimeUnit.MILLISECONDS);
                        }
                    }
                }
            });

            SimpleDateFormat simpleDateFormat;

            Date today = new Date();
            if(today.getYear() == messageDetails.getTimeSended().getYear()
            && today.getMonth() == messageDetails.getTimeSended().getMonth()
                    && today.getDate() == messageDetails.getTimeSended().getDate())
            {
                //Chỉ hiện thời gian bỏ đi ngày
                simpleDateFormat = new SimpleDateFormat("hh:mm");
            }else
            {
                simpleDateFormat = new SimpleDateFormat("dd-M hh:mm");
            }
            senderViewHolder.txtTimeSent.setText(simpleDateFormat.format(messageDetails.getTimeSended()));
            senderViewHolder.chatPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent zoomImageIntent = new Intent(context, ZoomImageActivity.class);
                    zoomImageIntent.putExtra("uri",senderViewHolder.sendImageUri.toString());
                    context.startActivity(zoomImageIntent);
                }
            });

        }else{
            ReciverViewHolder reciverViewHolder = (ReciverViewHolder) holder;
            if(messageDetails.getContent().startsWith("message_images/"+ messageDetails.getMessageId())){
                //Picture
                StorageReference storageReference = FirebaseStorage.getInstance().getReference(messageDetails.getContent());
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        reciverViewHolder.receiveImageUri = uri;
                        Picasso.get().load(uri).fit().centerCrop().into(reciverViewHolder.chatPicture);
                    }
                });
                reciverViewHolder.receiveMessage.setVisibility(View.INVISIBLE);
                reciverViewHolder.linearAudioReceive.setVisibility(View.INVISIBLE);
                reciverViewHolder.chatPicture.setVisibility(View.VISIBLE);

                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(reciverViewHolder.receiveParent_layout);
                constraintSet.connect(R.id.chatPicture,ConstraintSet.START,R.id.receiveTime,ConstraintSet.START,0);
                constraintSet.connect(R.id.receiveTime,ConstraintSet.TOP,R.id.chatPicture,ConstraintSet.BOTTOM,0);
                constraintSet.applyTo(reciverViewHolder.receiveParent_layout);

            }else if(messageDetails.getContent().startsWith("message_records/"+ messageDetails.getMessageId())){
                StorageReference storageReference = FirebaseStorage.getInstance().getReference(messageDetails.getContent());
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        reciverViewHolder.receiveMediaPlayer = new MediaPlayer();
                        reciverViewHolder.receiveMediaPlayer.setAudioAttributes(
                                new AudioAttributes.Builder()
                                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                        .setUsage(AudioAttributes.USAGE_MEDIA)
                                        .build()
                        );
                        try{
                            reciverViewHolder.receiveMediaPlayer.setDataSource(context.getApplicationContext(),uri);
                            reciverViewHolder.receiveMediaPlayer.prepare();

                            int millis = reciverViewHolder.receiveMediaPlayer.getDuration();
                            reciverViewHolder.seekbarReceive.setMax(millis);

                            reciverViewHolder.receiveMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    timer.shutdown();
                                    reciverViewHolder.seekbarReceive.setProgress(0);
                                    reciverViewHolder.btn_actionAudioReceive.setImageResource(R.drawable.ic_play);
                                }
                            });

                        }catch (IOException e){
                            Toast.makeText(context, "Error "+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                reciverViewHolder.linearAudioReceive.setVisibility(View.VISIBLE);
                reciverViewHolder.receiveMessage.setVisibility(View.INVISIBLE);
                reciverViewHolder.chatPicture.setVisibility(View.INVISIBLE);
            }
            else {
                reciverViewHolder.receiveMessage.setText(messageDetails.getContent());
                reciverViewHolder.receiveMessage.setVisibility(View.VISIBLE);
                reciverViewHolder.chatPicture.setVisibility(View.INVISIBLE);
                reciverViewHolder.linearAudioReceive.setVisibility(View.INVISIBLE);

            }
            //lấy ảnh đại diện của người nhận
            //Tiến hành tìm kiếm trên FirebaseDatabase
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("users");
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //lấy user trong database
                    DataSnapshot dataSnapshot = snapshot.child(messageDetails.getSenderPhone());
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
                                Picasso.get().load(uri).fit().centerCrop().into(reciverViewHolder.imgUser);

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
            //reciverViewHolder.receiveMessage.setText(messageDetails.getContent());
           // Picasso.get().load(uri).fit().centerCrop().into(holder.imageBoxChat);
            reciverViewHolder.btn_actionAudioReceive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(reciverViewHolder.receiveMediaPlayer != null){
                        if(reciverViewHolder.receiveMediaPlayer.isPlaying()) {
                            reciverViewHolder.receiveMediaPlayer.pause();
                            reciverViewHolder.btn_actionAudioReceive.setImageResource(R.drawable.ic_play);
                            timer.shutdown();
                        }
                        else {
                            reciverViewHolder.receiveMediaPlayer.start();
                            reciverViewHolder.btn_actionAudioReceive.setImageResource(R.drawable.ic_pause);

                            timer = Executors.newScheduledThreadPool(1);
                            timer.scheduleAtFixedRate(new Runnable() {
                                @Override
                                public void run() {
                                    reciverViewHolder.seekbarReceive.setProgress(reciverViewHolder.receiveMediaPlayer.getCurrentPosition());
                                }
                            }, 10, 10, TimeUnit.MILLISECONDS);
                        }
                    }
                }
            });
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
            reciverViewHolder.chatPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent zoomImageIntent = new Intent(context, ZoomImageActivity.class);
                    zoomImageIntent.putExtra("uri",reciverViewHolder.receiveImageUri.toString());
                    context.startActivity(zoomImageIntent);
                }
            });
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
            return ITEM_RECEIVE;
        }
    }

    class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView sendMessage;
        TextView txtTimeSent;
        LinearLayout linearAudioSend;
        ConstraintLayout sendParent_layout;
        ImageView chatPicture,btn_actionAudioSend;
        SeekBar seekbarSend;
        MediaPlayer sendMediaPlayer;
        Uri sendImageUri;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            sendMessage = itemView.findViewById(R.id.sendMessage);
            txtTimeSent = itemView.findViewById(R.id.txtTimeSent);
            chatPicture = itemView.findViewById(R.id.chatPicture);
            btn_actionAudioSend = itemView.findViewById(R.id.btn_actionAudioSend);
            seekbarSend = itemView.findViewById(R.id.seekbarSend);
            linearAudioSend = itemView.findViewById(R.id.linearAudioSend);
            sendParent_layout = itemView.findViewById(R.id.sendParent_layout);
        }
    }

    class ReciverViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearAudioReceive;
        ConstraintLayout receiveParent_layout;
        TextView receiveMessage;
        CircleImageView imgUser;
        TextView receiveTime;
        SeekBar seekbarReceive;
        ImageView chatPicture,btn_actionAudioReceive;
        MediaPlayer receiveMediaPlayer;
        Uri receiveImageUri;
        public ReciverViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imgUser);
            receiveMessage = itemView.findViewById(R.id.receiveMessage);
            receiveTime = itemView.findViewById(R.id.receiveTime);
            chatPicture = itemView.findViewById(R.id.chatPicture);
            btn_actionAudioReceive = itemView.findViewById(R.id.btn_actionAudioReceive);
            seekbarReceive = itemView.findViewById(R.id.seekbarReceive);
            linearAudioReceive = itemView.findViewById(R.id.linearAudioReceive);
            receiveParent_layout = itemView.findViewById(R.id.receiveParent_layout);
        }
    }

}
