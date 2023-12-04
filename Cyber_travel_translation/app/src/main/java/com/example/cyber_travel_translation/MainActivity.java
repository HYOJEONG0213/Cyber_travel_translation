package com.example.cyber_travel_translation;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    private MapView mapView;
    private GoogleMap googleMap;

    @Override
    //앱이 처음 생성될때, activity_maps.xml로드, 초기화..
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps); //앱 실행시 activity_maps로 가지게끔

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);
    }

    @Override
    //지도가 사용가능하면 시작
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        LatLng seoul = new LatLng(37.5665, 126.9780);   //서울좌표로 ㄱㄱ
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));

        googleMap.getUiSettings().setZoomControlsEnabled(true); //확대, 축소 버튼
        Log.d("MapsActivity", "Map is ready.");
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
