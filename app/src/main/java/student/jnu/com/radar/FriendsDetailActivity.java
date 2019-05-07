package student.jnu.com.radar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.List;

public class FriendsDetailActivity extends Activity {
    List<Friend> friendList=new ArrayList<>();
   int tag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_detail);
        //读取数据
        ArrayList<Serializable>friendtemp=readObject(FriendsDetailActivity.this,"data");
        for(Serializable ser:friendtemp){
            friendList.add((Friend) ser);
        }

        Intent intent=getIntent();
        String friend_number=intent.getStringExtra("friend_number");

        TextView txt_friend_name=(TextView)findViewById(R.id.txt_friend_name);
        //txt_friend_name.setText(friend_name);

        TextView txt_friend_number=(TextView)findViewById(R.id.txt_friend_number);
        //txt_friend_name

        TextView txt_friend_latitude_longitude=(TextView)findViewById(R.id.txt_friend_long_lang);


        Friend temp_friend=new Friend("","","","");

        for(int i=0;i<friendList.size();i++){

            temp_friend.setName(friendList.get(i).getName());
            temp_friend.setNumber(friendList.get(i).getNumber());
            temp_friend.setLatitude(friendList.get(i).getLatitude());
            temp_friend.setLongitude(friendList.get(i).getLongitude());

            if(friend_number.equals(temp_friend.getNumber())){
                //标记该对象
                tag=i;


                txt_friend_name.setText(temp_friend.getName());
                txt_friend_number.setText(temp_friend.getNumber());
                //设置纬度
                double latitude=Double.parseDouble(temp_friend.getLatitude());
                BigDecimal data_latitude=new BigDecimal(latitude);
                BigDecimal data_latitude_zero=new BigDecimal(0);
                int latitude_tag=data_latitude.compareTo(data_latitude_zero);
                if(latitude_tag==-1)
                {
                   temp_friend.setLatitude(temp_friend.getLatitude()+"S");
                }else if(latitude_tag==0){
                    temp_friend.setLatitude(temp_friend.getLatitude());
                }else{
                    temp_friend.setLatitude(temp_friend.getLatitude()+"N");
                }
                //设置经度
                double longitude=Double.parseDouble(temp_friend.getLongitude());
                BigDecimal data_longitude=new BigDecimal(longitude);
                BigDecimal data_longitude_zero=new BigDecimal(0);
                int longitude_tag=data_longitude.compareTo(data_longitude_zero);
                if(longitude_tag==-1)
                {
                    temp_friend.setLongitude(temp_friend.getLongitude()+"W");
                }else if(longitude_tag==0){
                    temp_friend.setLongitude(temp_friend.getLongitude());
                }else{
                    temp_friend.setLongitude(temp_friend.getLongitude()+"E");
                }

                txt_friend_latitude_longitude.setText(temp_friend.getLatitude()+"/"+temp_friend.getLongitude());
                break;
            }
        }
        //跳转到朋友界面
        Button btn_friendlist=(Button)findViewById(R.id.btn_friends_list);
        btn_friendlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(FriendsDetailActivity.this,FriendsListActivity.class);
                startActivity(intent1);
            }
        });
        //删除好友
        Button btn_delete=(Button)findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                friendList.remove(tag);
                Toast.makeText(getApplicationContext(), String.valueOf(friendList.size()), Toast.LENGTH_SHORT).show();

                ArrayList<Serializable>tempList=new ArrayList<Serializable>();
                for(int i=0;i<friendList.size();i++){
                    tempList.add(friendList.get(i));
                }
                saveObject(FriendsDetailActivity.this,tempList,"data");

                Intent intent2=new Intent(FriendsDetailActivity.this,FriendsListActivity.class);
                startActivity(intent2);
            }

        });



    }


    //判断文件是否存在
    public static boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if(file == null || !file.exists()) {
            return false;
        }else
            return true;
    }
    //保存
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
    //读取方法
    public static ArrayList<Serializable> readObject(Context context, String file){
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
}
