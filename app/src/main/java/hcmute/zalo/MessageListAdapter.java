package hcmute.zalo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MessageListAdapter extends BaseAdapter {
    private Context context;
    private int layout;

    public MessageListAdapter(Context context, int layout) {
        this.context = context;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return 3;
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
        TextView nameBoxChat, lastMessage;
        ImageView imageBoxChat;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout,null);

            holder.imageBoxChat = (ImageView) convertView.findViewById(R.id.imageBoxChat);
            holder.nameBoxChat = (TextView) convertView.findViewById(R.id.nameBoxChat);
            holder.lastMessage = (TextView) convertView.findViewById(R.id.lastMessage);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }
}
