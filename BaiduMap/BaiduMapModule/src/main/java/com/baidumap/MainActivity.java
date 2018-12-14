package com.baidumap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;


public class MainActivity extends AppCompatActivity {
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private LocationInstance mLocationInstance = null;
    private BDLocation mLastLocation;//最新定位信息
    private SensorInstance mSensorInstance;//传感器
    private boolean mIsFirstLocation = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();//显示平面地图
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15f));
        mBaiduMap.setMyLocationEnabled(true);
        init();//实现定位功能
    }

    public void init() {//实现定位功能

        initLocationDectect();
        initSensorDetect();//传感器定位指向

    }

    private void initLocationDectect() {//图标实现定位功能
        mLocationInstance = new LocationInstance(this,
                new LocationInstance.MyLocationListener() {
                    @Override
                    public void onReceiveLocation(BDLocation location) {
                        super.onReceiveLocation(location);
                        mLastLocation = location;//最新信息
                        Log.d("location", "我在" + location.getAddrStr());
                    }
                });
    }


    private void initSensorDetect() {
        mSensorInstance = new SensorInstance(getApplicationContext());
        mSensorInstance.setOnOrientationChangedListener(
                new SensorInstance.OnOrientationChangedListener() {
                    @Override
                    public void onOrientation(float x) {
                        // 设置定位图标；
                        if (mLastLocation == null) {
                            return;
                        }

                        // 构造定位数据
                        MyLocationData locData = new MyLocationData.Builder()
                                .accuracy(mLastLocation.getRadius())
                                // 此处设置开发者获取到的方向信息，顺时针0-360
                                .direction(x).latitude(mLastLocation.getLatitude())
                                .longitude(mLastLocation.getLongitude()).build();

                        // 设置定位数据
                        mBaiduMap.setMyLocationData(locData);

                        // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
//                BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
//                        .fromResource(R.drawable.navi_map_gps_locked);
                        MyLocationConfiguration config = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,
                                true, null);
                        mBaiduMap.setMyLocationConfiguration(config);


                        if (mIsFirstLocation) {
                            mIsFirstLocation = false;
                            LatLng point = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(point));
                        }


                        // 当不需要定位图层时关闭定位图层
//                mMap.setMyLocationEnabled(false);

                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        mLocationInstance.start();//开启定位
        mSensorInstance.start();//传感器定位
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocationInstance.stop();//关闭定位
        mSensorInstance.stop();//传感器定位
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    public static final int Item_ID_NORMAL_MAP = 101;
    public static final int Item_ID_SATELITE_MAP = 102;
    public static final int ITEM_LOCATION = 103;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//实现卫星和平面地图的切换菜单
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        menu.add(Menu.NONE, Item_ID_NORMAL_MAP, 0, "切换为普通地图");
        menu.add(Menu.NONE, Item_ID_SATELITE_MAP, 0, "切换为卫星地图");
        menu.add(Menu.NONE, ITEM_LOCATION, 0, "定位到我的位置");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Item_ID_NORMAL_MAP:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
            case Item_ID_SATELITE_MAP:
                mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                break;
            case ITEM_LOCATION://传感器根据手机的方向改变图标方向
                mBaiduMap.clear();
                //定义Maker坐标点
                LatLng point = new LatLng(mLastLocation.getLatitude(),
                        mLastLocation.getLongitude());
                //构建Marker图标
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.navi_map_gps_locked);
                //构建MarkerOption，用于在地图上添加Marker
                if (bitmap != null) {
                    OverlayOptions option = new MarkerOptions()
                            .position(point)
                            .icon(bitmap);
                }
                //在地图上添加Marker，并显示
                // mBaiduMap.addOverlay(option);
//手势改变地图中心点
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(point));
                break;


        }
        return super.onOptionsItemSelected(item);
    }
}