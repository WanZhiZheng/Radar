package student.jnu.com.radar;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
private MapView mapView=null;
    private BaiduMap baiduMap;
     List<Friend> friendList=new ArrayList<>();
    SMSBroadcastReceiver smsBroadcastReceiver;
    IntentFilter intentFilter;






    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.main);
        //boolean a=false;
        //读取数据

            ArrayList<Serializable> friendtemp = readObject(MainActivity.this, "data");
            for (Serializable ser : friendtemp) {

                friendList.add((Friend) ser);
            }
            //获取权限
        getPrivilege();

        intentFilter=new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(1000);
        smsBroadcastReceiver=new SMSBroadcastReceiver("","","",MainActivity.this);
        //动态注册广播
        registerReceiver(smsBroadcastReceiver, intentFilter);




        //跳转到朋友列表
        Button btn_friends=(Button)findViewById(R.id.btn_friends) ;
        btn_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ArrayList<Serializable>tempList=new ArrayList<Serializable>();
//                for(int i=0;i<friendList.size();i++){
//                    tempList.add(friendList.get(i));
//                }
//                saveObject(MainActivity.this,tempList,"data");

                Intent intent=new Intent(MainActivity.this,FriendsListActivity.class);
                startActivity(intent);
            }
        });

        //地图实现
        mapView=(MapView)findViewById(R.id.bmapView);
        baiduMap=mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        LatLng center_point=new LatLng(22.256729,113.541199);
        MapStatus mapStatus=new MapStatus.Builder()
                .target(center_point).zoom(17).build();
        MyLocationData.Builder builder=new MyLocationData.Builder();
        //把我显示在地图上
        builder.latitude(22.256729);
        builder.longitude(113.541199);
        MyLocationData locationData=builder.build();
        baiduMap.setMyLocationData(locationData);

        MapStatusUpdate mapStatusUpdate= MapStatusUpdateFactory.newMapStatus(mapStatus);
        baiduMap.setMapStatus(mapStatusUpdate);

        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(MainActivity.this, "Marker被点击了", Toast.LENGTH_SHORT).show();
                Bundle bundle=marker.getExtraInfo();
                       String friend_Number=(String) bundle.get("friend_number");
                Intent intent=new Intent(MainActivity.this,FriendsDetailActivity.class);
                intent.putExtra("friend_number",friend_Number);
                //这里要传朋友手机号码过去，则在好友详情显示界面知道是显示哪个好友的信息
                startActivity(intent);
                return false;
            }
        });
//        if(tag==FRIEND_LIST_OK){
//            DrawFriends(friendList);
//        }//这部分主要是想着不用每次都要刷新，后面自己再想想实现的方法吧



        //刷新按钮,向所有好友发送信息
        ToggleButton btn_refresh=(ToggleButton)findViewById(R.id.btn_refresh);
        btn_refresh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(friendList.size()!=0) {
                        sendMessage(friendList);
                    }else{
                        Toast.makeText(getApplicationContext(), "你的好友列表为空，请添加好友！", Toast.LENGTH_SHORT).show();
                    }

                    //Toast.makeText(getApplicationContext(),friendList.get(0).getName()+"///"+friendList.get(0).getLatitude(), Toast.LENGTH_SHORT).show();
                }else{
                    if(friendList.size()!=0&&!friendList.get(0).getLatitude().isEmpty()) {
                        DrawFriends(friendList);
                        Toast.makeText(getApplicationContext(), "追踪成功", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
//        btn_refresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                for(int i=0;i<friendList.size();i++) {
//                    Friend friend=friendList.get(i);
//                    String Name = friend.getName();
//                    String Number = friend.getNumber();
//                    String content=Name+",Where are you?";
//                    SmsManager manager = SmsManager.getDefault();
//                    ArrayList<String> list = manager.divideMessage(content);
//                    for (String text : list) {
//                        manager.sendTextMessage(Number, null, text, null, null);
//
//                    }
//                }
//                Toast.makeText(getApplicationContext(),"发送完毕",Toast.LENGTH_SHORT).show();
//            }
//        });


        //定位


        //定位
        Button btn_locat=(Button)findViewById(R.id.btn_locate);
        btn_locat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapStatus mapStatus=new MapStatus.Builder()
                        .target(new LatLng(22.256729,113.541199)).zoom(17).build();
                MapStatusUpdate mapStatusUpdate= MapStatusUpdateFactory.newMapStatus(mapStatus);
                baiduMap.setMapStatus(mapStatusUpdate);
                if(friendList.size()!=0) {
                    Toast.makeText(getApplicationContext(),friendList.get(0).getName()+"///"+friendList.get(0).getLatitude(), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"请添加好友", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode){
//            case 1:
//                if(resultCode==RESULT_OK){
//                    friendList=(List<Friend>) data.getSerializableExtra("friend");
//
//                }
//        }
//
//    }

    public void DrawFriends(List<Friend> friends) {

        BaiduMap mBaidumap = mapView.getMap();
        mBaidumap.clear();
        for (int i = 0; i < friends.size(); i++) {
            Friend friend = friends.get(i);

            LatLng friend_point = new LatLng(Double.valueOf(friend.getLatitude()), Double.valueOf(friend.getLongitude()));//设定中心点坐标

            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.friend_marker);
            //准备 marker option 添加 marker 使用
            MarkerOptions markerOption = new MarkerOptions().icon(bitmap).position(friend_point);
            //获取添加的 marker 这样便于后续的操作
            Marker marker = (Marker) mBaidumap.addOverlay(markerOption);
            Bundle bundle=new Bundle();
            bundle.putString("friend_number",friend.getNumber());
            marker.setExtraInfo(bundle);

            //计算两个点之间距离
            LatLng my_point=new LatLng(22.256729,113.541199);
            double distance= DistanceUtil.getDistance(my_point,friend_point);
            String distance_show;
            //对距离进行处理
            if(distance<1000){
                BigDecimal bg=new BigDecimal(distance).setScale(1, RoundingMode.UP);
                distance_show=String.valueOf(bg.doubleValue())+"m";
            }else {
                BigDecimal bg=new BigDecimal(distance/1000).setScale(1, RoundingMode.UP);
                distance_show=String.valueOf(bg.doubleValue())+"km";
            }

            //两点间的连线
            List<LatLng> line=new ArrayList<LatLng>();
            line.add(my_point);
            line.add(friend_point);
            OverlayOptions polyline=new PolylineOptions().
                    width(8).
                    color(Color.GREEN).
                    points(line);
            mBaidumap.addOverlay(polyline);

            //将朋友的信息显示出来
            OverlayOptions textOption = new TextOptions().fontSize(50).fontColor(Color.GREEN).
                    text(friend.getName()+":"+friend.getNumber()+" , "+distance_show).
                    rotate(0).
                    position(friend_point);
            mBaidumap.addOverlay(textOption);
        }

    }



    public void sendMessage(List<Friend> friendList){
                        for(int i=0;i<friendList.size();i++) {
                    Friend friend=friendList.get(i);
                    String Name = friend.getName();
                    String Number = friend.getNumber();
                    String content=Name+",Where are you?";
                    SmsManager manager = SmsManager.getDefault();
                    ArrayList<String> list = manager.divideMessage(content);
                    for (String text : list) {
                        manager.sendTextMessage(Number, null, text, null, null);

                    }
                }
                Toast.makeText(getApplicationContext(),"发送完毕",Toast.LENGTH_SHORT).show();
    }

//判断文件是否存在
public static boolean fileExists(Context context, String filename) {
    File file = context.getFileStreamPath(filename);
    if(file == null || !file.exists()) {
        return false;
    }else
        return true;
}

    public static boolean saveObject(Context context, ArrayList<Serializable> serList, String file){

        FileOutputStream fos=null;
        ObjectOutputStream oos=null;
        try{
            fos=context.openFileOutput(file,Context.MODE_PRIVATE);
            oos=new ObjectOutputStream(fos);
            for(int i=0;i<serList.size();i++){
                oos.writeObject(serList.get(i));}
            oos.flush();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return  false;
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }finally {
            try{
                oos.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
public static ArrayList<Serializable> readObject(Context context,String file){
    ArrayList<Serializable> friendList = new ArrayList<Serializable>();

    if(fileExists(context,file)) {  //判断文件是否存在
        FileInputStream fis = null;
        ObjectInputStream ois = null;


        try {
            fis = context.openFileInput(file);
            ois = new ObjectInputStream(fis);
            Serializable ser = null;
            while ((ser = (Serializable) ois.readObject()) != null) {
                friendList.add(ser);

            }
        } catch (ClassNotFoundException e) {

            e.printStackTrace();
        } catch (InvalidClassException e) {
            e.printStackTrace();
            File data = context.getFileStreamPath(file);
            data.delete();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                ois.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    return friendList;

}

    private void getPrivilege(){
        List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.SEND_SMS);
        }
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }


        if(!permissionList.isEmpty()){
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mapView!=null)mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mapView!=null) mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ArrayList<Serializable>tempList=new ArrayList<Serializable>();
        for(int i=0;i<friendList.size();i++){
            tempList.add(friendList.get(i));
        }
        saveObject(MainActivity.this,tempList,"data");
        //撤销广播
        unregisterReceiver(smsBroadcastReceiver);
        if(mapView!=null) mapView.onDestroy();
    }
}