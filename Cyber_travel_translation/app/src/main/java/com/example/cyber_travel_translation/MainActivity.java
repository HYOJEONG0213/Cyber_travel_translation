package com.example.cyber_travel_translation;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import android.webkit.WebView;
import android.widget.Button;
import android.view.View;
import android.content.Intent;
import android.net.Uri;
import android.content.pm.PackageManager;
import android.widget.Toast;
import com.google.android.gms.maps.model.MarkerOptions;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private MapView mapView;    //MapView: 구글맵 띄우기
    private GoogleMap googleMap;
    private Button streetViewButton;
    private LatLng clickedLatLng;   //클릭한 지점의 좌표

    private WebView streetViewWebView;  //streetViewWebView: 거리뷰 웹뷰로 띄우기
    //해당 앱안에서 거리뷰 띄우는건 구글이 금지해서 안됌 ㅠㅡㅠ

    @Override
    //앱이 처음 생성될때, activity_maps.xml로드, 초기화..
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps); //앱 실행시 activity_maps로 가지게끔

        mapView = findViewById(R.id.mapView);   //mapView 초기화
        mapView.onCreate(savedInstanceState);

        // 버튼 초기화
        streetViewButton = findViewById(R.id.streetViewButton);
        streetViewWebView = new WebView(this);
        streetViewWebView.getSettings().setJavaScriptEnabled(true); //streeView 초기화
        streetViewWebView.setWebViewClient(new WebViewClient());
        streetViewButton.setOnClickListener(new View.OnClickListener() {    //streetView 버튼 누르면 showStreetView 실행
            @Override
            public void onClick(View v) {
                showStreetView();
            }
        });
        mapView.getMapAsync(this);  //구글맵 객체 가져오기
    }


    @Override
    //지도가 사용가능하면 시작
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


    // 클릭한 지점의 로드뷰로 이동하는 함수
    private void showStreetView() {
        if (clickedLatLng != null) {
            //위도와 경도로 해당 지점의 로드뷰로 이동
            String streetViewUrl = "https://www.google.com/maps/@?api=1&map_action=pano&viewpoint="
                    + clickedLatLng.latitude + "," + clickedLatLng.longitude;
            streetViewWebView.loadUrl(streetViewUrl);
            setContentView(streetViewWebView);  //화면에 streetViewWebView가 보이게끔 표시
        } else {
            Toast.makeText(this, "지점을 클릭해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

}


/*
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        LatLng SEOUL = new LatLng(37.56, 126.97);

        MarkerOptions markerOptions = new MarkerOptions();         // 마커 생성
        markerOptions.position(SEOUL);
        markerOptions.title("서울");                         // 마커 제목
        markerOptions.snippet("한국의 수도");         // 마커 설명
        mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));                 // 초기 위치
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));                         // 줌의 정도
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);                           // 지도 유형 설정

    }

}
*/
