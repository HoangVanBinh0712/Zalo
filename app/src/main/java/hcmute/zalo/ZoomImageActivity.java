package hcmute.zalo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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
        //ánh xạ các view
        zoomImage = findViewById(R.id.zoomImage);
        imgBack = findViewById(R.id.imgBack);
        btnDownload = findViewById(R.id.btnDownload);

        //Lấy đường dẫn của hình ảnh từ sharePreferences
        Uri uri = Uri.parse(getIntent().getStringExtra("uri"));
        //Nếu không có đường dẫn
        if (uri.equals("")) {
            //để default
            zoomImage.setImageResource(R.drawable.thanhpho);
            return;
        }
        //Đưa ảnh vào trong imageview
        Picasso.get().load(uri).into(zoomImage);

        //Bấm nút back để kết thúc activity
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //Bấm nút tải xuống để tải hình ảnh về máy
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ZoomImageActivity.this, "Starting download....", Toast.LENGTH_SHORT).show();
                //tạo bất kỳ tên cho hình ảnh
                String name = createRandomString();
                //Bắt đầu tải hình ảnh xuống
                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setVisibleInDownloadsUi(true);
                //gắn đường dẫn để lưu hình ảnh
                request.setDestinationInExternalFilesDir(ZoomImageActivity.this, Environment.DIRECTORY_DOWNLOADS,name+".jpg");
                downloadManager.enqueue(request);
            }
        });
    }
    //Tạo 1 chuỗi bất kỳ
    private String createRandomString() {
        String upperAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerAlphabet = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";

        String alphaNumeric = upperAlphabet + lowerAlphabet + numbers;

        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        //Chuỗi có 6 ký tự
        int length = 6;
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(alphaNumeric.length());
            char randomChar = alphaNumeric.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString();
    }
}