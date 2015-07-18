package com.playfun.lineonline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.lbsapi.BMapManager;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapDoubleClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapLongClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;

import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.baidu.nplatform.comapi.map.MapController;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.BDLocationListener;
import com.baidu.lbsapi.BMapManager;
import com.baidu.lbsapi.MKGeneralListener;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.nplatform.comapi.map.MapController;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.CommonParams.Const.ModelName;
import com.baidu.navisdk.CommonParams.NL_Net_Mode;
import com.baidu.navisdk.comapi.mapcontrol.BNMapController;
import com.baidu.navisdk.comapi.mapcontrol.MapParams.Const.LayerMode;
import com.baidu.navisdk.comapi.routeguide.RouteGuideParams.RGLocationMode;
import com.baidu.navisdk.comapi.routeplan.BNRoutePlaner;
import com.baidu.navisdk.comapi.routeplan.IRouteResultObserver;
import com.baidu.navisdk.comapi.routeplan.RoutePlanParams.NE_RoutePlan_Mode;
import com.baidu.navisdk.comapi.setting.SettingParams;
import com.baidu.navisdk.model.NaviDataEngine;
import com.baidu.navisdk.model.RoutePlanModel;
import com.baidu.navisdk.model.datastruct.RoutePlanNode;
import com.baidu.navisdk.ui.routeguide.BNavConfig;
import com.baidu.navisdk.ui.routeguide.BNavigator;
import com.baidu.navisdk.ui.routeguide.model.RGCacheStatus;
import com.baidu.navisdk.ui.widget.RoutePlanObserver;
import com.baidu.navisdk.util.common.PreferenceHelper;
import com.baidu.navisdk.util.common.ScreenUtil;
import com.baidu.nplatform.comapi.map.MapGLSurfaceView;
public class MapActivity extends FragmentActivity implements
		OnGetPoiSearchResultListener, OnGetSuggestionResultListener {
	private PoiSearch mPoiSearch = null;
	private SuggestionSearch mSuggestionSearch = null;
	private final Handler handler = new Handler();
	private BMapManager mapManager;
	private MapView mMapView;
	private MapController mapController;
	private BaiduMap mBaiduMap;
	private static final LatLng GEO_GUANGZHOU = new LatLng(23.155, 113.264);
	private AutoCompleteTextView keyWorldsView = null;
	private AutoCompleteTextView cityview = null;
	private ArrayAdapter<String> sugAdapter = null;
	private int load_Index = 0;
	private String touchType;
	private LatLng currentPt;
	public int sx,sy,ex,ey;
	private final static double PI = Math.PI;
	private final static double r = 6371.229;
	private Map<Integer, LatLng> map = new HashMap<Integer, LatLng>();
	private static int ind = 0;
	private ImageView back;
	public int cnt = 0;
	public int pic[] ={R.drawable.item1,R.drawable.item2,R.drawable.item3,R.drawable.item4,R.drawable.item5,
	   R.drawable.item6,R.drawable.item7,R.drawable.item8,R.drawable.item9,R.drawable.item10		
	};
	double lati[] ={23.075024,23.099406,23.072627,23.134613,23.09757,23.099498,23.132071,23.092816,23.101276,23.099888,23.09732};
	double longti[]={113.397483,113.305223,113.397155,113.296518,113.306013,113.305366,113.295467,113.30145,113.305465,113.301432,113.30348};
	private RoutePlanModel mRoutePlanModel = null;
	/*private LocationClient mLocationClient;
	//public MyLocationListener mMyLocationListener;
	private LocationMode mCurrentMode = LocationMode.NORMAL;
	private volatile boolean isFristLocation = true;
	private double mCurrentLantitude;
	private double mCurrentLongitude;
	private float mCurrentAccracy;
	private MyOrientationListener myOrientationListener;
	private int mXDirection;
	private BitmapDescriptor mIconMaker;
	private RelativeLayout mMarkerInfoLy;*/
	Button mBtnPre = null;//上一个节点
    Button mBtnNext = null;//下一个节点
    int nodeIndex = -1;//节点索引,供浏览节点时使用
    RouteLine route = null;
    OverlayManager routeOverlay = null;
    boolean useDefaultIcon = false;
    public Bundle data;
    public String id;
    private TextView popupText = null;//泡泡view
    RoutePlanSearch mSearch = null; 
	private double dis[] = { 0.06456, 0.25032, 4.30667, 4.98820, 3.77589,
			3.78765, 4.29949, 3.96496, 4.11127, 0.20261 };
	public LocationClient locationClient = null;
	//�Զ���ͼ��
	public ArrayList<LatLng>list = new ArrayList<LatLng>();
	BitmapDescriptor mCurrentMarker = null;
	boolean isFirstLoc = true;// �Ƿ��״ζ�λ
    public ImageView loc;
	public BDLocationListener myListener = new BDLocationListener() {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view ��ٺ��ڴ����½��յ�λ��
			LatLng latLng = null;
			//mBaiduMap.clear();
			OverlayOptions overlayOptions = null;
			Marker marker = null;
			if (location == null || mMapView == null)
				return;
			
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
				
					.direction(location.getDirection()).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			latLng = new LatLng(location.getLatitude(),location.getLongitude());
			Log.e("point",String.valueOf(latLng.latitude)+","+String.valueOf(latLng.longitude));
			list.add(latLng);
			sx = (int)location.getLatitude()*100000;
			sy = (int)location.getLongitude()*100000;
			mBaiduMap.setMyLocationData(locData);
			/*Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.navi_map_gps_locked);
			BitmapDescriptor b = BitmapDescriptorFactory.fromBitmap(bmp);
			overlayOptions = new MarkerOptions().position(latLng)
					.icon(b).zIndex(1);
			marker = (Marker) (mBaiduMap.addOverlay(overlayOptions));*/
			BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
					.fromResource(R.drawable.navi_map_gps_locked);
			MyLocationConfiguration config = new MyLocationConfiguration(
					LocationMode.NORMAL, false, mCurrentMarker);
			mBaiduMap.setMyLocationConfigeration(config);
			
			if (isFirstLoc) {
				isFirstLoc = false;
				
				
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 16);	//���õ�ͼ���ĵ��Լ����ż���
//				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
			mPoiSearch.searchInCity((new PoiCitySearchOption())
					.city("广东")
					.keyword("中山大学")
					.pageNum(load_Index));
			 if(data!=null&&id!=null){
					
					int i = Integer.parseInt(id);
					PlanNode stNode = PlanNode.withLocation(new LatLng(lati[0],longti[0]));
			        PlanNode enNode = PlanNode.withLocation(new LatLng(lati[i],longti[i]));
			        
			        // 实际使用中请对起点终点城市进行正确的设定
			      
			           /* mSearch.transitSearch((new TransitRoutePlanOption())
			                    .from(stNode)
			                    .city("广州")
			                    .to(enNode));*/
			        mSearch.walkingSearch((new WalkingRoutePlanOption())
		                    .from(stNode)
		                    .to(enNode));
					
				}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.map);
		LayoutInflater inflater = LayoutInflater.from(this);
        mMapView = (MapView)findViewById(R.id.map1);
	    mSearch = RoutePlanSearch.newInstance();
		data = getIntent().getExtras();
		id = data.getString("gameID");
		
		sx = sy = ex = ey = -1;
		mBaiduMap = mMapView.getMap();
		locationClient = new LocationClient(getApplicationContext()); // ʵ��LocationClient��
		locationClient.registerLocationListener(myListener); // ע�������
		this.setLocationOption();	//���ö�λ����
		locationClient.start();
		//isFristLocation = true;
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		mSuggestionSearch = SuggestionSearch.newInstance();
		mSuggestionSearch.setOnGetSuggestionResultListener(this);
		keyWorldsView = (AutoCompleteTextView) findViewById(R.id.search_detail);
		cityview = (AutoCompleteTextView) findViewById(R.id.search_province);
		sugAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line);
		loc = (ImageView)findViewById(R.id.cal);
		loc.bringToFront();
		loc.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				 route = null;
			        mBtnPre.setVisibility(View.INVISIBLE);
			        mBtnNext.setVisibility(View.INVISIBLE);
			        mBaiduMap.clear();
			        // 处理搜索按钮响应
			       
			        //设置起终点信息，对于tranist search 来说，城市名无意义
			        int t = list.size();
			        PlanNode stNode = PlanNode.withLocation(list.get(0));
			        PlanNode enNode = PlanNode.withLocation(list.get(t-1));
			        
			        // 实际使用中请对起点终点城市进行正确的设定
			      
			           /* mSearch.transitSearch((new TransitRoutePlanOption())
			                    .from(stNode)
			                    .city("广州")
			                    .to(enNode));*/
			        mSearch.walkingSearch((new WalkingRoutePlanOption())
		                    .from(stNode)
		                    .to(enNode));
			     
			}
		});
      
		 mBtnPre = (Button) findViewById(R.id.pre);
	     mBtnNext = (Button) findViewById(R.id.next);
	     mBtnPre.setVisibility(View.INVISIBLE);
	     mBtnNext.setVisibility(View.INVISIBLE);
	     mSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener(){

			@Override
			  public void onGetWalkingRouteResult(WalkingRouteResult result) {
		        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
		            Toast.makeText(MapActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		        }
		        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
		            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
		            //result.getSuggestAddrInfo()
		            return;
		        }
		        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
		            nodeIndex = -1;
		            mBtnPre.setVisibility(View.VISIBLE);
		            mBtnNext.setVisibility(View.VISIBLE);
		            route = result.getRouteLines().get(0);
		            WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaiduMap);
		            mBaiduMap.setOnMarkerClickListener(overlay);
		            routeOverlay = overlay;
		            overlay.setData(result.getRouteLines().get(0));
		            overlay.addToMap();
		            overlay.zoomToSpan();
		        }

		    }

		    @Override
		    public void onGetTransitRouteResult(TransitRouteResult result) {

		        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
		            Toast.makeText(MapActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		        }
		        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
		            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
		            //result.getSuggestAddrInfo()
		            return;
		        }
		        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
		            nodeIndex = -1;
		            mBtnPre.setVisibility(View.VISIBLE);
		            mBtnNext.setVisibility(View.VISIBLE);
		            route = result.getRouteLines().get(0);
		            TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaiduMap);
		            mBaiduMap.setOnMarkerClickListener(overlay);
		            routeOverlay = overlay;
		            overlay.setData(result.getRouteLines().get(0));
		            overlay.addToMap();
		            overlay.zoomToSpan();
		        }
		    }

		    @Override
		    public void onGetDrivingRouteResult(DrivingRouteResult result) {
		        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
		            Toast.makeText(MapActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		        }
		        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
		            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
		            //result.getSuggestAddrInfo()
		            return;
		        }
		        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
		            nodeIndex = -1;
		            mBtnPre.setVisibility(View.VISIBLE);
		            mBtnNext.setVisibility(View.VISIBLE);
		            route = result.getRouteLines().get(0);
		            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
		            routeOverlay = overlay;
		            mBaiduMap.setOnMarkerClickListener(overlay);
		            overlay.setData(result.getRouteLines().get(0));
		            overlay.addToMap();
		            overlay.zoomToSpan();
		        }
		    }

	    	 
	     });
		//mIconMaker = BitmapDescriptorFactory.fromResource(R.drawable.maker);
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
		mBaiduMap.setMapStatus(msu);	
		keyWorldsView.setAdapter(sugAdapter);
		cityview.setAdapter(sugAdapter);
		
		/*
		 * Intent intent = getIntent(); if (intent.hasExtra("x") &&
		 * intent.hasExtra("y")) {
		 * 
		 * Bundle b = intent.getExtras(); LatLng p = new
		 * LatLng(b.getDouble("y"), b.getDouble("x")); mMapView = new
		 * MapView(this, new BaiduMapOptions().mapStatus(new MapStatus.Builder()
		 * .target(p).build())); } else { mMapView = new MapView(this, new
		 * BaiduMapOptions()); } setContentView(mMapView); mBaiduMap =
		 * mMapView.getMap();
		 */
		//initMyLocation();
		//initOritationListener();
		initlistener();
		
		findViewById(R.id.searchbanlayout).bringToFront();
		findViewById(R.id.search_province).bringToFront();
		findViewById(R.id.search_detail).bringToFront();
		findViewById(R.id.map_next_data).bringToFront();
		findViewById(R.id.tbtnlayout).bringToFront();
		keyWorldsView.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				if (cs.length() <= 0) {
					return;
				}
				String city = cityview.getText().toString();

				mSuggestionSearch
						.requestSuggestion((new SuggestionSearchOption())
								.keyword(cs.toString()).city(city));
			}
		});
	}
	private void initMapClickEvent()
	{
		mBaiduMap.setOnMapClickListener(new OnMapClickListener()
		{

			@Override
			public boolean onMapPoiClick(MapPoi arg0)
			{
				return false;
			}

			@Override
			public void onMapClick(LatLng arg0)
			{
				//mMarkerInfoLy.setVisibility(View.GONE);
				mBaiduMap.hideInfoWindow();

			}
		});
	}

	public void initlistener() {
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
			public void onMapClick(LatLng point) {
				touchType = "单击";
				currentPt = point;
				updateMapState();
			}

			public boolean onMapPoiClick(MapPoi poi) {
				return false;
			}
		});
		mBaiduMap.setOnMapLongClickListener(new OnMapLongClickListener() {
			public void onMapLongClick(LatLng point) {
				touchType = "长按";

				currentPt = point;
				updateMapState();
			}
		});
		mBaiduMap.setOnMapDoubleClickListener(new OnMapDoubleClickListener() {
			public void onMapDoubleClick(LatLng point) {
				touchType = "双击";
				currentPt = point;
				updateMapState();
			}
		});
		mBaiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {
			public void onMapStatusChangeStart(MapStatus status) {
				updateMapState();
			}

			public void onMapStatusChangeFinish(MapStatus status) {
				updateMapState();
			}

			public void onMapStatusChange(MapStatus status) {
				updateMapState();
			}
		});
	}

	// mWebView = (WebView) findViewById(R.id.webview);
	// WebSettings setting=mWebView.getSettings();

	// setting.setPluginState(PluginState.ON);
	// setting.setJavaScriptEnabled(true);
	/*
	 * if(check()){
	 * 
	 * }else{ install(); }
	 */

	private void updateMapState() {

		String state = "";
		if (currentPt == null) {
			state = "点击、长按、双击地图以获取经纬度和地图状态";
		} else {
			state = String.format(touchType + ",当前经度： %f 当前纬度：%f",
					currentPt.longitude, currentPt.latitude);
			map.put(ind, currentPt);
			Log.e("success", String.valueOf(currentPt.longitude));
		}
		state += "\n";
		MapStatus ms = mBaiduMap.getMapStatus();
		state += String.format("zoom=%.1f rotate=%d overlook=%d", ms.zoom,
				(int) ms.rotate, (int) ms.overlook);
		// mStateBar.setText(state);
	}

	protected boolean isRouteDisplayed() {
		return false;
	}

	

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	public void onAttach(Activity activity) {
	}

	
	public void searchButtonProcess(View v) {
		EditText editSearchKey = (EditText) findViewById(R.id.search_province);
		EditText editSearchdetail = (EditText) findViewById(R.id.search_detail);
		
		mPoiSearch.searchInCity((new PoiCitySearchOption())
				.city(editSearchKey.getText().toString())
				.keyword(editSearchdetail.getText().toString())
				.pageNum(load_Index));
		/*BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
				.fromResource(R.drawable.navi_map_gps_locked);
		MyLocationConfiguration config = new MyLocationConfiguration(
				mCurrentMode, true, mCurrentMarker);
		mBaiduMap.setMyLocationConfigeration(config);*/
	}

	public void goToNextPage(View v) {
		load_Index++;

		searchButtonProcess(null);
	}

	public void onGetPoiResult(PoiResult result) {
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			mBaiduMap.clear();
			PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
			mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener()
			{
				@Override
				public boolean onMarkerClick(final Marker marker)
				{
					// 获得marker中的数据
					

					InfoWindow mInfoWindow;
					cnt++;
					ImageView img = (ImageView)LayoutInflater.from(MapActivity.this).inflate(R.layout.popuplayout, null);;
					img.setBackgroundResource(R.drawable.verticalcard);
					// 将marker所在的经纬度的信息转化成屏幕上的坐标
					final LatLng ll = marker.getPosition();
					list.add(ll);
					Log.e("point",String.valueOf(ll.latitude)+","+String.valueOf(ll.longitude));
					Point p = mBaiduMap.getProjection().toScreenLocation(ll);
					p.y -= 47;
					final LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
					// 为弹出的InfoWindow添加点击事件
					Bitmap bmp = BitmapFactory.decodeResource(getResources(), pic[cnt%=10]);
					mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromBitmap(bmp), llInfo,5,new OnInfoWindowClickListener()
					{

						@Override
						public void onInfoWindowClick()
						{
							//ex = (int)llInfo.latitude*100000;
							//ey = (int)llInfo.longitude*100000;
							list.add(llInfo);
							Log.e("point",String.valueOf(llInfo.latitude)+","+String.valueOf(llInfo.longitude));
							Intent intent = new Intent(MapActivity.this,DetailActivity.class);
							Bundle data = getIntent().getExtras();
							data.putString("attrID", String.valueOf(cnt));
							intent.putExtras(data);
							startActivity(intent);
							
							
							// 隐藏InfoWindow
							mBaiduMap.hideInfoWindow();
						}
					});
					
					// 显示InfoWindow
					mBaiduMap.showInfoWindow(mInfoWindow);
					// 设置详细信息布局为可见
					//mMarkerInfoLy.setVisibility(View.VISIBLE);
					// 根据商家信息为详细信息布局设置信息
					
					return true;
				}
			});
			
			overlay.setData(result);
			overlay.addToMap();
			overlay.zoomToSpan();
			return;
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

			// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
			String strInfo = "在";
			for (CityInfo cityInfo : result.getSuggestCityList()) {
				strInfo += cityInfo.city;
				strInfo += ",";
			}
			strInfo += "找到结果";
			Toast.makeText(MapActivity.this, strInfo, Toast.LENGTH_LONG).show();
		}
	}

	public void onGetPoiDetailResult(PoiDetailResult result) {
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(MapActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(MapActivity.this,
					result.getName() + ": " + result.getAddress(),
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onGetSuggestionResult(SuggestionResult res) {
		if (res == null || res.getAllSuggestions() == null) {
			return;
		}
		sugAdapter.clear();
		for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
			if (info.key != null)
				sugAdapter.add(info.key);
		}
		sugAdapter.notifyDataSetChanged();
	}

	private class MyPoiOverlay extends PoiOverlay {

		public MyPoiOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);
			Log.e("index", String.valueOf(index));
			LatLng pt = currentPt;
			ind = index;
			// Log.e("pt",String.valueOf(currentPt.longitude)+","+String.valueOf(currentPt.latitude));
			PoiInfo poi = getPoiResult().getAllPoi().get(index);
			// if (poi.hasCaterDetails) {
			mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
					.poiUid(poi.uid));
			// }
			return true;
		}
	}

	public double getDistance(double longt1, double lat1, double longt2,
			double lat2) {

		double x, y, distance;

		x = (longt2 - longt1) * PI * r
				* Math.cos(((lat1 + lat2) / 2) * PI / 180) / 180;

		y = (lat2 - lat1) * PI * r / 180;

		distance = Math.hypot(x, y);

		return distance;

	}
	/*public class MyLocationListener implements BDLocationListener
	{
		@Override
		public void onReceiveLocation(BDLocation location)
		{

			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			// 构造定位数据
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(mXDirection).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mCurrentAccracy = location.getRadius();
			// 设置定位数据
			mBaiduMap.setMyLocationData(locData);
			mCurrentLantitude = location.getLatitude();
			mCurrentLongitude = location.getLongitude();
			// 设置自定义图标
			BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
					.fromResource(R.drawable.navi_map_gps_locked);
			MyLocationConfiguration config = new MyLocationConfiguration(
					mCurrentMode, true, mCurrentMarker);
			mBaiduMap.setMyLocationConfigeration(config);
			// 第一次定位时，将地图位置移动到当前位置
			if (isFristLocation)
			{
				isFristLocation = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
		}

	}*/

	/*private void initOritationListener()
	{
		Log.e("oritationlist","oritation");
		myOrientationListener = new MyOrientationListener(
				getApplicationContext());
		myOrientationListener
				.setOnOrientationListener(new OnOrientationListener()
				{
					@Override
					public void onOrientationChanged(float x)
					{
						mXDirection = (int) x;
						// 构造定位数据
						MyLocationData locData = new MyLocationData.Builder()
								.accuracy(mCurrentAccracy)
								// 此处设置开发者获取到的方向信息，顺时针0-360
								.direction(mXDirection)
								.latitude(mCurrentLantitude)
								.longitude(mCurrentLongitude).build();
						// 设置定位数据
						mBaiduMap.setMyLocationData(locData);
						// 设置自定义图标
						BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
								.fromResource(R.drawable.navi_map_gps_locked);
						MyLocationConfiguration config = new MyLocationConfiguration(
								mCurrentMode, true, mCurrentMarker);
						mBaiduMap.setMyLocationConfigeration(config);

					}
				});
	}*/
	/*private void initMyLocation()
	{
		
		Log.e("initmylo","initmylo");
		mLocationClient = new LocationClient(this);
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		// 设置定位的相关配置
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocationClient.setLocOption(option);
	}*/

	/*private void center2myLoc()
	{
		LatLng ll = new LatLng(mCurrentLantitude, mCurrentLongitude);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
		mBaiduMap.animateMapStatus(u);
	}*/
	protected void onStart()
	{
		
		Log.e("onstart","true");
		mBaiduMap.setMyLocationEnabled(true);
		
		super.onStart();
	}

	@Override
	protected void onStop()
	{
		
		mBaiduMap.setMyLocationEnabled(false);
		
		super.onStop();
	}
	 public void nodeClick(View v) {
	        if (route == null ||
	                route.getAllStep() == null) {
	            return;
	        }
	        if (nodeIndex == -1 && v.getId() == R.id.pre) {
	        	return;
	        }
	        //设置节点索引
	        if (v.getId() == R.id.next) {
	            if (nodeIndex < route.getAllStep().size() - 1) {
	            	nodeIndex++;
	            } else {
	            	return;
	            }
	        } else if (v.getId() == R.id.pre) {
	        	if (nodeIndex > 0) {
	        		nodeIndex--;
	        	} else {
	            	return;
	            }
	        }
	        //获取节结果信息
	        LatLng nodeLocation = null;
	        String nodeTitle = null;
	        Object step = route.getAllStep().get(nodeIndex);
	        if (step instanceof DrivingRouteLine.DrivingStep) {
	            nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrace().getLocation();
	            nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
	        } else if (step instanceof WalkingRouteLine.WalkingStep) {
	            nodeLocation = ((WalkingRouteLine.WalkingStep) step).getEntrace().getLocation();
	            nodeTitle = ((WalkingRouteLine.WalkingStep) step).getInstructions();
	        } else if (step instanceof TransitRouteLine.TransitStep) {
	            nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrace().getLocation();
	            nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();
	        }

	        if (nodeLocation == null || nodeTitle == null) {
	            return;
	        }
	        //移动节点至中心
	        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
	        // show popup
	        popupText = new TextView(MapActivity.this);
	        popupText.setBackgroundResource(R.drawable.popup);
	        popupText.setTextColor(0xFF000000);
	        popupText.setText(nodeTitle);
	        mBaiduMap.showInfoWindow(new InfoWindow(popupText, nodeLocation, 0));

	    }

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		mPoiSearch.destroy();
		mSuggestionSearch.destroy();
		
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		
		mMapView.onResume();
		
	}

	@Override
	protected void onPause()
	{
		super.onPause();
	
		mMapView.onPause();
		
	}
	private void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); 
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// ���ö�λģʽ
		option.setCoorType("bd09ll"); 
		option.setScanSpan(500000); 
		option.setIsNeedAddress(true); 
		option.setNeedDeviceDirect(true); 
		
		locationClient.setLocOption(option);
	}
	private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.myloc);
            }
            return null;
        }
    }

    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.myloc);
            }
            return null;
        }
    }

    private class MyTransitRouteOverlay extends TransitRouteOverlay {

        public MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.myloc);
            }
            return null;
        }
    }
   
	
}