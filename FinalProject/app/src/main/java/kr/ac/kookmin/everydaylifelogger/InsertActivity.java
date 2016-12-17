package kr.ac.kookmin.everydaylifelogger;

import android.app.Activity;
import android.R.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class InsertActivity extends Activity {

    GoogleMap map;
    int check = 0;
    String category; // DB에 저장 될 category값
    String juso; // DB에 저장 될 address값
    Spinner sp;

    TextView logView;
    TextView address;

    double lng; // 경도
    double lat; // 위도

    DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_insert);
        Log.d("Main", "onCreate");

        dbManager = new DBManager(getApplicationContext(), "List.db", null, 1); // DB를 선언해준다.

        // 지도와 좌표
        logView = (TextView) findViewById(R.id.log);
        logView.setText("GPS 가 잡혀야 좌표가 구해짐");

        address = (TextView) findViewById(R.id.text_address);

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // GPS 프로바이더 사용가능여부
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 네트워크 프로바이더 사용가능여부
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Log.d("Main", "isGPSEnabled=" + isGPSEnabled);
        Log.d("Main", "isNetworkEnabled=" + isNetworkEnabled);

        LocationListener locationListener = new LocationListener() {
            Marker MyLocation;//

            public void onLocationChanged(Location location) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();

                if (lat > 0 && lng > 0 && check == 0) {
                    check = 1;
                    LatLng SEOUL = new LatLng(lat, lng);

                    map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
                    MyLocation = map.addMarker(new MarkerOptions().position(SEOUL).title("내 위치"));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 15));
                    map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                } else if (check == 1) {
                    MyLocation.remove();
                    LatLng SEOUL = new LatLng(lat, lng);

                    map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
                    MyLocation = map.addMarker(new MarkerOptions().position(SEOUL).title("내 위치"));
                    map.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));

                }
                logView.setText("latitude: " + lat + ", longitude: " + lng);
                address.setText(getAddress(lat, lng));
                juso = getAddress(lat, lng);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                logView.setText("onStatusChanged");
            }

            public void onProviderEnabled(String provider) {
                logView.setText("onProviderEnabled");
            }

            public void onProviderDisabled(String provider) {
                logView.setText("onProviderDisabled");
            }
        };

        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        // 수동으로 위치 구하기
        String locationProvider = LocationManager.GPS_PROVIDER;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        if (lastKnownLocation != null) {
            //double lng = lastKnownLocation.getLatitude();
            //double lat = lastKnownLocation.getLatitude();
            lng = lastKnownLocation.getLatitude();
            lat = lastKnownLocation.getLatitude();
            Log.d("Main", "longtitude=" + lng + ", latitude=" + lat);
            logView.setText("longtitude=" + lng + ", latitude=" + lat);
            address.setText(getAddress(lat, lng));
            juso = getAddress(lat, lng);
        }

        // spinner구현
        /*ArrayList<String> arraylist;
        arraylist = new ArrayList<String>();

        // 선택항목
        arraylist.add("공부");arraylist.add("운동");arraylist.add("약속");
        arraylist.add("쇼핑");arraylist.add("독서");arraylist.add("여행");arraylist.add("기타");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, arraylist);

        Spinner sp = (Spinner) this.findViewById(R.id.spinner);
        sp.setAdapter(adapter);*/

        sp = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.whatToDo,
                android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
    }

    // geo code를 이용한 주소출력 함수
    private String getAddress(double lat, double lng) {
        Geocoder gcK = new Geocoder(getApplicationContext(), Locale.KOREA);
        String res = "정보없음";
        try {
            List<Address> addresses = gcK.getFromLocation(lat, lng, 1);
            StringBuilder sb = new StringBuilder();

            if (null != addresses && addresses.size() > 0) {
                /*Address address = addresses.get(0);
                 sb.append(address.getCountryName()).append(" ");
                 sb.append(address.getPostalCode()).append(" ");
                sb.append(address.getLocality()).append(" "); // 시
                sb.append(address.getThoroughfare()).append(" "); // 동
                sb.append(address.getFeatureName()); // 번지
                res = sb.toString();*/
                res = addresses.get(0).getAddressLine(0).toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public void onClick(View view) {
        switch (view.getId()){
            // InsertActivity 으로 돌아감
            case R.id.bt_back:
                finish();
                break;

            case R.id.bt_save:
                EditText doing = (EditText) findViewById(R.id.text_doing);

                sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View arg1, int position, long id) {
                        category = (String) sp.getSelectedItem(); // 선택하면 선택된 값이 저장됨
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // TODO Auto-generated method stub
                    }
                });

                if ( doing.getText().toString().length() != 0 ) { // 내용이 입력되면 DB에 저장
                    Toast.makeText(getApplicationContext(), "저장되었습니다.", Toast.LENGTH_LONG).show();

                    if(category == null)    category = "학습"; // category 선택이 안돼있으면 "학습"
                    dbManager.insert("insert into SCHEDULE_LIST values('"+ juso
                            + "','" + category + "', '"
                            + doing.getText().toString() + "');");
                }
                else { // 아무것도 입력받지 못하면 토스트메세지를 띄워준다.
                    Toast.makeText(getApplicationContext(), "내용을 입력해주세요", Toast.LENGTH_LONG).show();
                }

                String str = dbManager.PrintData(); // DB의 데이터들을 str에 저장
                Intent list_intent = new Intent(InsertActivity.this, CheckActivity.class); // CheckActivity로 이동
                list_intent.putExtra("List", str); // 조회리스트로 str전달
                startActivity(list_intent);
                break;
        }
    }
}
