package com.example.cyber_travel_translation;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

//구글맵 가져오기용 import
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.google.android.gms.maps.StreetViewPanoramaView;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.SupportMapFragment;


import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.webkit.WebViewClient;

//구글맵 rode view 정보 가져오기용
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.google.android.gms.maps.model.StreetViewPanoramaLink;






//파파고 가져오기용 import
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
//import okhttp3.Request;
import okhttp3.RequestBody;
//import okhttp3.Response;

//Volley 가져오기
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private MapView mapView;    //MapView: 구글맵 띄우기
    private GoogleMap googleMap;
    private Button web_streetViewButton;
    private int webStreetViewButtonClickCount = 0;
    private Button image_streetViewButton;
    private ImageView imageView;
    private Button transformButton;
    private LatLng clickedLatLng;   //클릭한 지점의 좌표
    private WebView webStreetView;  //webStreetView: 거리뷰 웹뷰로 띄우기
    private FrameLayout imageViewControl;   //이미지뷰 컨트롤러
    private Button searchButton;

    private StreetViewPanorama streetViewPanorama;   //streetViewPanorama: 스트리트 뷰에서 각도 추출하기 위한 것!
    //해당 앱안에서 거리뷰 띄우는건 구글이 금지해서 안됌 ㅠㅡㅠ


    private static final int REQUEST_PERMISSIONS = 1;
    private static final String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };


    private float fov=360;
    private float heading=235;
    private float pitch=10;
    @Override
    //앱이 처음 생성될때, activity_maps.xml로드, 초기화..
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps); //앱 실행시 activity_maps로 가지게끔


        mapView = findViewById(R.id.mapView);   //mapView 초기화
        mapView.onCreate(savedInstanceState);

        webStreetView = findViewById(R.id.webStreetView);
        webStreetView.getSettings().setJavaScriptEnabled(true);
        webStreetView.setWebViewClient(new WebViewClient());


        // 버튼 초기화
        web_streetViewButton = findViewById(R.id.web_streetViewButton);
        image_streetViewButton = findViewById(R.id.image_streetViewButton);
        transformButton = findViewById(R.id.transformButton);
        searchButton = findViewById(R.id.searchButton);

        checkAndRequestPermissions();

        // WebView에 JavaScript 인터페이스 추가


        web_streetViewButton.setOnClickListener(new View.OnClickListener() {    //streetView 버튼 누르면 showStreetView 실행
            @Override
            public void onClick(View v) {
                web_showStreetView();
            }
        });


        image_streetViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fetchWebStreetViewData();
                Intent intent = new Intent(MainActivity.this, ImageStreetView.class);
                intent.putExtra("latitude", clickedLatLng.latitude);    //INTENT때 위도와 경도 넘겨주기
                intent.putExtra("longitude", clickedLatLng.longitude);
                startActivity(intent);
                //image_showStreetView();
            }
        });

        transformButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 지금 상태의 이미지를 번역하는 함수 호출
                //translateCurrentImage();
            }
        });

        //검색하면 해당지역에 마커 추가
        //https://developers.google.com/maps/documentation/javascript/geocoding?hl=ko
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchField = (EditText) findViewById(R.id.searchField);   //입력한거 가져오고
                String location = searchField.getText().toString();
                List<android.location.Address> addressList = null;

                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(MainActivity.this);    //해당위치 주소가져옴
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);    //검색리스트 첫번째꺼 선택
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    
                    //검색리스트 1개 이상 있다면
                    if (!addressList.isEmpty()) {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());  //위도 경도 가져와서
                        clickedLatLng = latLng;
                        googleMap.clear();  //기존 마커 지우고
                        googleMap.addMarker(new MarkerOptions().position(latLng).title(location));  //지도에 마커 표시
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    }
                    else{
                        Toast.makeText(MainActivity.this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                //기존 키보드 창 사라지게끔 하기
                //https://caliou.tistory.com/193
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
            }


        });

        mapView.getMapAsync(this);  //구글맵 객체 가져오기


        //Toast.makeText(this, BuildConfig.GOOGLE_API, Toast.LENGTH_SHORT).show();
    }


    @Override
    //지도가 사용가능하면 시작
    //https://duzi077.tistory.com/121
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        LatLng seoul = new LatLng(37.5665, 126.9780);   //서울좌표로 ㄱㄱ
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));

        googleMap.getUiSettings().setZoomControlsEnabled(true); //확대, 축소 버튼

        googleMap.setOnMapClickListener(this);
    }

    public void onMapClick(LatLng latLng) {
        // 클릭한 지점의 좌표를 가져와서 저장
        clickedLatLng = latLng; // 클릭한 지점을 표시
        googleMap.clear(); // 기존 클릭한 지점을 제거
        //마커 추가
        //https://developers.google.com/maps/documentation/android-sdk/marker?hl=ko
        googleMap.addMarker(new MarkerOptions().position(clickedLatLng).title("클릭한 지점"));
    }


    @Override
    //activity가 화면에 나타남
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    //activity가 다른 act로 이동
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    //act 종료
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    //시스템이 낮은 메모리상태에 있을 때
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    //https://developers.google.com/maps/documentation/streetview/request-streetview?hl=ko
    //https://wooiljeong.github.io/python/google-streetview-api/
    // 클릭한 지점의 로드뷰로 이동하는 함수
    private void web_showStreetView() {
        if (clickedLatLng != null) {
            if (webStreetViewButtonClickCount %2 == 0){
            //위도와 경도로 해당 지점의 로드뷰로 이동
                String streetViewUrl = "https://www.google.com/maps/@?api=1&map_action=pano&viewpoint="
                        + clickedLatLng.latitude + "," + clickedLatLng.longitude;
                webStreetView.loadUrl(streetViewUrl);
                //setContentView(webStreetView);  //화면에 webStreetView가 보이게끔 표시
                webStreetView.setVisibility(View.VISIBLE);  // 웹뷰를 표시
                mapView.setVisibility(View.VISIBLE);  // 맵뷰를 숨김
                webStreetViewButtonClickCount++;
                //fetchWebStreetViewData();
            }
            else{
                //fetchWebStreetViewData();

                webStreetView.setVisibility(View.GONE);
                mapView.setVisibility(View.VISIBLE);
                webStreetViewButtonClickCount++;
            }
        } else {
            Toast.makeText(this, "지점을 클릭해주세요.", Toast.LENGTH_SHORT).show();
        }
    }



    // 로드뷰에서 실시간 데이터 받아오기
// 최종적인 로드뷰의 데이터를 받아오는 함수
    //https://developers.google.com/maps/documentation/urls/get-started?hl=ko
    //실패. 폐기함.


    // 권한을 확인하고 필요한 권한이 없으면 요청하기
    // https://valuenetwork.tistory.com/54
    private void checkAndRequestPermissions() {
        List<String> requiredPermissions = new ArrayList<>();
        //모든 권한 보기
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                requiredPermissions.add(permission);
            }
        }

        //필요한 권한이 없다면 요청하기
        if (!requiredPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, requiredPermissions.toArray(new String[0]), REQUEST_PERMISSIONS);
        }
    }

    // 권한 요청을 처리한 후 호출됌
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 승인되었을 때 수행할 작업
                //translateCurrentImage();
            } else {
                // 권한이 거절되었을 때 수행할 작업
                Toast.makeText(this, "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }



    //https://api.ncloud-docs.com/docs/ai-naver-imagetoimageapi
    //현재화면 캡쳐


    //구글맵 캡쳐: https://velog.io/@jibmin/%EC%BD%94%ED%8B%80%EB%A6%B0-%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%EA%B5%AC%EA%B8%80-%EB%A7%B5-%EC%BA%A1%EC%B3%90-%EB%B2%84%ED%8A%BC-%EB%A7%8C%EB%93%A4%EA%B8%B0
    //폰 캡쳐는 구글맵 api가 구글맵 캡쳐 못하게 막음..

}


