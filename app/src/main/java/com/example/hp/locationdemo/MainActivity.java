package com.example.hp.locationdemo;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.example.hp.locationdemo.overlay.WalkRouteOverlay;
import com.example.hp.locationdemo.util.AMapServicesUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PoiSearch.OnPoiSearchListener,TextWatcher, AMap.OnMarkerClickListener ,View.OnFocusChangeListener{
    private TextView bottomOne;
    private TextView bottomTwo;
    private TextView bottomThree;

    private EditText fromText;
    private EditText toText;
    private ImageView exchangeImage;
    private EditText searchText;
    private ImageView searchImage;
    private LinearLayout searchLayout;
    private LinearLayout routeLayout;
    private ImageView backImage;

    private LinearLayout hindLayout;
    private RecyclerView hindRecyclerView;
    private TextView exchangeText;

    private MapView mMapView = null;
    private AMap aMap;
    private RouteSearch routeSearch;

    private LatLonPoint mStartPoint;//起点，39.996678,116.479271
    private LatLonPoint mEndPoint;//终点，39.997796,116.468939
    private LatLonPoint mSearchPoint;
    private WalkRouteOverlay walkRouteOverlay;

    private ArrayList<PoiItem> list = new ArrayList<>();
    private HindAdapter hindAdapter;

    private static final int SEARCH_MODE = 0;
    private static final int ROUTE_MODE = 1;
    private int mode = SEARCH_MODE;

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
        initListener();
        initLocation();
    }

    private void initListener() {
        exchangeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exchangeRouteMode();
            }
        });
        bottomThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetailActivity.start(getApplicationContext(),"from_main_activity");
            }
        });
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comeback();
            }
        });
        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchPlace();
            }
        });
        exchangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchRoute();
            }
        });
    }


    private void searchPlace() {

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
        fromText.setOnFocusChangeListener(this);
        toText.setOnFocusChangeListener(this);

        searchText = findViewById(R.id.search_text);
        searchText.setOnFocusChangeListener(this);
        searchText.addTextChangedListener(this);
        searchImage = findViewById(R.id.search_image);
        searchLayout = findViewById(R.id.search_layout);
        routeLayout = findViewById(R.id.route_layout);

        backImage = findViewById(R.id.back_image);
        exchangeText = findViewById(R.id.exchange_text);

        bottomOne = findViewById(R.id.bottom_one);
        bottomTwo = findViewById(R.id.bottom_two);
        bottomThree = findViewById(R.id.bottom_three);
        exchangeImage = findViewById(R.id.exchange_image);

        hindLayout = findViewById(R.id.hind_layout);
        hindRecyclerView = findViewById(R.id.hind_recycler_view);
        initRecyclerView();
    }

    private void comeback() {
        if(mode == ROUTE_MODE){
            mode = SEARCH_MODE;
            searchLayout.setVisibility(View.VISIBLE);
            routeLayout.setVisibility(View.GONE);
            exchangeText.setVisibility(View.VISIBLE);
        }else if(mode == SEARCH_MODE){
            finish();
        }
    }

    private void exchangeRouteMode() {
        mode = ROUTE_MODE;
        routeLayout.setVisibility(View.VISIBLE);
        exchangeText.setVisibility(View.GONE);
        searchLayout.setVisibility(View.GONE);
    }

    private void initRecyclerView() {
        ChangeText changeText = new ChangeText() {
            @Override
            public void changeEditText(String s,LatLonPoint l) {
                if(fromText.hasFocus()){fromText.setText(s);mStartPoint=l;fromText.clearFocus();}
                else if(toText.hasFocus()) {toText.setText(s);mEndPoint=l;toText.clearFocus();}
                else {searchText.setText(s);mSearchPoint=l;searchText.clearFocus();}
                hideKeyboard();
            }
        };
        hindAdapter = new HindAdapter(list,changeText);
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
        else if (hindLayout.getVisibility() != View.VISIBLE){
            hindLayout.setVisibility(View.VISIBLE);
        }
        searchHelp(charSequence.toString());

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

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if(hasFocus){


        }else {
            hindLayout.setVisibility(View.INVISIBLE);
        }
    }

    public void searchHelp(String locate){
        PoiSearch.Query query=new PoiSearch.Query(locate,"","武汉");
        PoiSearch poiSearch=new PoiSearch(this,query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        list.clear();
        list.addAll(poiResult.getPois());
        hindAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    private void hideKeyboard() {
        View viewFocus = this.getCurrentFocus();
        if (viewFocus != null) {
            InputMethodManager imManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imManager.hideSoftInputFromWindow(viewFocus.getWindowToken(), 0);
        }
    }
}
