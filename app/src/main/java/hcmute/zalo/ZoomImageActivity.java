package hcmute.zalo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Random;

public class ZoomImageActivity extends AppCompatActivity {
    ImageView zoomImage,imgBack,btnDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image);
        zoomImage = findViewById(R.id.zoomImage);
        imgBack = findViewById(R.id.imgBack);
        btnDownload = findViewById(R.id.btnDownload);

        Uri uri = Uri.parse(getIntent().getStringExtra("uri"));
        Picasso.get().load(uri).into(zoomImage);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ZoomImageActivity.this, "Starting download....", Toast.LENGTH_SHORT).show();
                String name = createRandomString();
                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setVisibleInDownloadsUi(true);
                request.setDestinationInExternalFilesDir(ZoomImageActivity.this, Environment.DIRECTORY_DOWNLOADS,name+".jpg");
                downloadManager.enqueue(request);
            }
        });
    }
    private String createRandomString() {
        String upperAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerAlphabet = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";

        String alphaNumeric = upperAlphabet + lowerAlphabet + numbers;

        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        int length = 6;
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(alphaNumeric.length());
            char randomChar = alphaNumeric.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString();
    }
}