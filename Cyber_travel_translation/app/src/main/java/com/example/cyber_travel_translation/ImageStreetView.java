package com.example.cyber_travel_translation;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class ImageStreetView extends AppCompatActivity {

    private Button web_streetViewButton;
    private int webStreetViewButtonClickCount = 0;
    private Button image_streetViewButton;
    private ImageView imageView;
    private Button transformButton;
    private LatLng clickedLatLng;   //클릭한 지점의 좌표
    private WebView webStreetView;  //webStreetView: 거리뷰 웹뷰로 띄우기
    private FrameLayout imageViewControl;   //이미지뷰 컨트롤러
    private Button backButton;

    private float fov=360;
    private float heading=235;
    private float pitch=10;

    private static final int REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_roadview);

        webStreetView = findViewById(R.id.webStreetView);
        webStreetView.getSettings().setJavaScriptEnabled(true);
        webStreetView.setWebViewClient(new WebViewClient());

        imageViewControl = findViewById(R.id.imageViewControl);

        // 버튼 초기화
        web_streetViewButton = findViewById(R.id.web_streetViewButton);
        image_streetViewButton = findViewById(R.id.image_streetViewButton);
        transformButton = findViewById(R.id.transformButton);
        backButton = findViewById(R.id.backButton);


        // Intent에서 전달된 데이터 받기
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            double latitude = extras.getDouble("latitude", 0.0);
            double longitude = extras.getDouble("longitude", 0.0);
            clickedLatLng = new LatLng(latitude, longitude);
        }

        image_showStreetView();


        image_streetViewButton.setOnClickListener(new View.OnClickListener() {    //streetView 버튼 누르면 showStreetView 실행
            @Override
            public void onClick(View v) {

            }
        });


        transformButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String streetViewUrl = "https://maps.googleapis.com/maps/api/streetview?" +
                        "size=600x1500" +
                        "&location=" + clickedLatLng.latitude + "," + clickedLatLng.longitude +
                        "&fov="+ fov +
                        "&heading="+ heading +
                        "&pitch=" + pitch +
                        "&key="+BuildConfig.GOOGLE_API;

                downloadAndSaveImage(streetViewUrl);

            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    //https://developers.google.com/maps/documentation/streetview/request-streetview?hl=ko
    //https://wooiljeong.github.io/python/google-streetview-api/
    private void image_showStreetView() {
        if (clickedLatLng != null) {
            String streetViewUrl = "https://maps.googleapis.com/maps/api/streetview?" +
                    "size=600x1500" +
                    "&location=" + clickedLatLng.latitude + "," + clickedLatLng.longitude +
                    "&fov="+ fov +
                    "&heading="+ heading +
                    "&pitch=" + pitch +
                    "&key="+BuildConfig.GOOGLE_API;

            // 생성한 URL을 WebView에 로드
            webStreetView.loadUrl(streetViewUrl);
            //setContentView(webStreetView);  // 화면에 webStreetView가 보이게끔 표시
            webStreetView.setVisibility(View.VISIBLE);

            imageViewControl.setVisibility(View.VISIBLE);

            setButtonListeners();

            //downloadAndSaveImage(streetViewUrl);
        } else {
            Toast.makeText(this, "지점을 클릭해주세요.", Toast.LENGTH_SHORT).show();
        }

    }


    private void setButtonListeners() {
        // 좌측 버튼: 서쪽으로 회전
        findViewById(R.id.leftButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                heading -= 10; // 10도씩 왼쪽으로 회전
                image_showStreetView();
            }
        });

        // 우측 버튼: 동쪽으로 회전
        findViewById(R.id.rightButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                heading += 10; // 10도씩 오른쪽으로 회전
                image_showStreetView();
            }
        });

        // 위 버튼: 위를 바라보게끔
        findViewById(R.id.upButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pitch += 10; // 10도씩 위를 바라보게끔
                image_showStreetView();
            }
        });

        // 아래 버튼: 아래를 바라보게끔
        findViewById(R.id.downButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pitch -= 10; // 10도씩 아래를 바라보게끔
                image_showStreetView();
            }
        });
    }


    //현재화면 캡쳐하기
    ////https://ghj1001020.tistory.com/6

    private void downloadAndSaveImage(String imageUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(imageUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(input);

                    // 이제 bitmap을 갤러리에 저장.
                    saveBitmap(bitmap);

                    //transformImage(bitmap);
                    transformImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //갤러리에 저장

    public void saveBitmap(Bitmap bitmap) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "source_" + timeStamp;

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, imageFileName);
        values.put(MediaStore.Images.Media.DESCRIPTION, imageFileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        try (OutputStream outstream = getContentResolver().openOutputStream(uri)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outstream);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "갤러리에 저장되었습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void transformImage() {
        Intent intent = new Intent(ImageStreetView.this, TranslateView.class);
        startActivity(intent);
    }

}
