package com.example.hp.locationdemo;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.example.hp.locationdemo.overlay.WalkRouteOverlay;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TextWatcher, AMap.OnMarkerClickListener {
    private TextView bottomOne;
    private TextView bottomTwo;
    private TextView bottomThree;
    private EditText fromText;
    private EditText toText;
    private ImageView exchangeImage;

    private LinearLayout hindLayout;
    private RecyclerView hindRecyclerView;

    private MapView mMapView = null;
    private AMap aMap;
    private RouteSearch routeSearch;

    private LatLonPoint mStartPoint = new LatLonPoint(39.996678,116.479271);//起点，39.996678,116.479271
    private LatLonPoint mEndPoint = new LatLonPoint(39.997796,116.468939);//终点，39.997796,116.468939
    private WalkRouteOverlay walkRouteOverlay;

    private ArrayList<String> list = new ArrayList<>();
    private HindAdapter hindAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);

        if (aMap == null) {
            aMap = mMapView.getMap();
            aMap.setOnMarkerClickListener(this);
        }

        initView();
        initLocation();
    }


    private void searchRoute() {
        if(routeSearch == null){
            routeSearch = new RouteSearch(this);
            routeSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {
                @Override
                public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

                }

                @Override
                public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

                }

                @Override
                public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {
                    aMap.clear();// 清理地图上的所有覆盖物
                    final WalkPath walkPath = walkRouteResult.getPaths().get(0);
                    WalkRouteResult mWalkRouteResult = walkRouteResult;

                    if (walkRouteOverlay != null){
                        walkRouteOverlay.removeFromMap();
                    }
                    walkRouteOverlay = new WalkRouteOverlay(
                            getApplicationContext(), aMap, walkPath,
                            mWalkRouteResult.getStartPos(),
                            mWalkRouteResult.getTargetPos());
                    walkRouteOverlay.addToMap();
                    walkRouteOverlay.zoomToSpan();
                }

                @Override
                public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

                }
            });
        }

        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(mStartPoint,mEndPoint);
        RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, RouteSearch.WALK_DEFAULT);
        routeSearch.calculateWalkRouteAsyn(query);//开始算路
    }

    private void initLocation() {
        aMap.moveCamera(CameraUpdateFactory.zoomTo(21));
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationEnabled(true);
    }

    private void initView() {

        fromText = findViewById(R.id.from_text);
        toText = findViewById(R.id.to_text);
        fromText.addTextChangedListener(this);
        toText.addTextChangedListener(this);

        bottomOne = findViewById(R.id.bottom_one);
        bottomTwo = findViewById(R.id.bottom_two);
        bottomThree = findViewById(R.id.bottom_three);
        exchangeImage = findViewById(R.id.exchange_image);
        exchangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchRoute();
            }
        });

        hindLayout = findViewById(R.id.hind_layout);
        hindRecyclerView = findViewById(R.id.hind_recycler_view);
        initRecyclerView();
    }

    private void initRecyclerView() {
        hindAdapter = new HindAdapter(list);
        hindRecyclerView.setAdapter(hindAdapter);
        hindRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(charSequence.length()==0)hindLayout.setVisibility(View.INVISIBLE);
        else if(hindLayout.getVisibility() != View.VISIBLE)hindLayout.setVisibility(View.VISIBLE);
        list.clear();
        for (int j = 0; j < charSequence.length(); j++) {
            list.add("8号教学");
        }
        hindAdapter.notifyDataSetChanged();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        double latidute = marker.getPosition().latitude;
        double longitude = marker.getPosition().longitude;
        //然后请求数据`````````
        return false;
    }
}
