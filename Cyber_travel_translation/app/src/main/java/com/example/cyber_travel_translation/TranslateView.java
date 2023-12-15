package com.example.cyber_travel_translation;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

//import com.android.volley.Request;
import com.android.volley.RequestQueue;
//import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.ContentType;
//import org.apache.http.entity.mime.HttpMultipartMode;
//import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.Call;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Callback;
import okhttp3.Response;

public class TranslateView extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Button web_streetViewButton;
    private int webStreetViewButtonClickCount = 0;
    private Button image_streetViewButton;
    private ImageView imageView;
    private Button transformButton;
    private LatLng clickedLatLng;   //클릭한 지점의 좌표
    private WebView webStreetView;  //webStreetView: 거리뷰 웹뷰로 띄우기
    private FrameLayout imageViewControl;   //이미지뷰 컨트롤러

    private static final String API_KEY_ID = BuildConfig.PAPAGO_ID;
    private static final String API_KEY = BuildConfig.PAPAGO_API;
    private static final String API_URL = "https://naveropenapi.apigw.ntruss.com/image-to-image/v1/translate";

    private Button transform_button;
    private Button back_button;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translate_view);
        ImageView imageView = (ImageView) findViewById(R.id.translatedImageView);
        imageView.setVisibility(View.GONE);

        //갤러리의 가장 최근 사진 가져오기
        Bitmap imageBitmap = getLatestImage();

        transform_button = findViewById(R.id.transform_button);
        back_button = findViewById(R.id.back);

        RadioGroup sourceLanguageGroup = findViewById(R.id.source_language_group);
        RadioGroup targetLanguageGroup = findViewById(R.id.target_language_group);
        final AtomicReference<String> sourceLanguage = new AtomicReference<>("ko");
        final AtomicReference<String> targetLanguage = new AtomicReference<>("ko");
        sourceLanguageGroup.setOnCheckedChangeListener((group, checkedId) -> {
                    if (checkedId == R.id.source_korean) {
                        sourceLanguage.set("ko");
                    } else if (checkedId == R.id.source_english) {
                        sourceLanguage.set("en");
                    } else if (checkedId == R.id.source_japanese) {
                        sourceLanguage.set("ja");
                    } else {
                        sourceLanguage.set(null);
                    }
                });
        targetLanguageGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.target_korean) {
                targetLanguage.set("ko");
            } else if (checkedId == R.id.target_english) {
                targetLanguage.set("en");
            } else if (checkedId == R.id.target_japanese) {
                targetLanguage.set("ja");
            } else {
                targetLanguage.set(null);  // or set a default language
            }
        });

        transform_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setVisibility(View.VISIBLE);
                //Toast.makeText(v.getContext(), "test1", Toast.LENGTH_SHORT).show();
                translateImage(imageBitmap, sourceLanguage.get(), targetLanguage.get());
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });





        //if (imageBitmap != null) {
        //    imageView.setImageBitmap(imageBitmap);
        //} else {
        //    Toast.makeText(this, "헐 없대", Toast.LENGTH_SHORT).show();
        //}


    }


    //Pictures 앨범에서 가장 마지막 사진 가져오기
    //https://velog.io/@k4hee/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%EC%9E%90%EB%B0%94-%EA%B0%9C%EB%B0%9C-FileObserving
    public Bitmap getLatestImage() {
        String directoryPath = "/storage/emulated/0/Pictures";
        File dir = new File(directoryPath);

        File[] files = dir.listFiles();
        int last_file = files.length;

        // 가장 최신 파일을 Bitmap으로 변환
        Bitmap latestImageBitmap = BitmapFactory.decodeFile(files[last_file-1].getAbsolutePath());

        return latestImageBitmap;
    }


    //https://api.ncloud-docs.com/docs/ai-naver-imagetoimageapi
    //https://soeun-87.tistory.com/23
    private void translateImage(Bitmap image, String sourceLanguage, String targetLanguage) {
        // Convert Bitmap to byte[]
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        //Toast.makeText(this, "test1"+sourceLanguage+targetLanguage, Toast.LENGTH_SHORT).show();

        // 요청 보낼 때 미디어 파일이 가도록 미리 생성, 바이트 배열로 이루어져 있음
        RequestBody imageBody = RequestBody.create(byteArray, okhttp3.MediaType.parse("application/octet-stream"));

        // 요청보낼때 요청 Body
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("source", sourceLanguage)
                .addFormDataPart("target", targetLanguage)
                .addFormDataPart("image", "a.png", imageBody)
                .build();

        // Request 생성
        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("X-NCP-APIGW-API-KEY-ID", API_KEY_ID)
                .addHeader("X-NCP-APIGW-API-KEY", API_KEY)
                .post(requestBody)
                .build();

        // 클라이언트 개체를 받음
        OkHttpClient client = new OkHttpClient();

        //새로운 요청 하기
        client.newCall(request).enqueue(new Callback() {
            @Override
            //실패
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            //요청 성공시
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseString = response.body().string();   //응답내용 string형태로 가져오기
                        JSONObject responseObject = new JSONObject(responseString); //해당 내용 JSON 객체로 변환
                        String translatedImageBase64 = responseObject.getJSONObject("data").getString("renderedImage"); //해당 객체에서 필요한 데이터 추출
                        byte[] decodedImage = Base64.decode(translatedImageBase64, Base64.DEFAULT); //이미지를 바이트단위로 디코드
                        final Bitmap translatedBitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);    //바이트배열을 비트맵으로 변환

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Display translated image in ImageView
                                ImageView imageView = findViewById(R.id.translatedImageView);   //imageView 찾고
                                imageView.setImageBitmap(translatedBitmap); //해당 imageView에 변역사진 보여줌
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle error
                    Log.e(TAG, "Error status code : " + response.code());
                }
            }
        });
    }



}