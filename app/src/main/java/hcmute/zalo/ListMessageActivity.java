package hcmute.zalo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

public class ListMessageActivity extends AppCompatActivity {
    ListView listviewMessage;
    MessageListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_mesage);

        listviewMessage = (ListView) findViewById(R.id.listviewMessage);

        adapter = new MessageListAdapter(this,R.layout.message_row);
        listviewMessage.setAdapter(adapter);


    }
}