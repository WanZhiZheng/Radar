package student.jnu.com.radar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASUS on 2018/12/14.
 */

public class FriendsListActivity extends Activity{
    public List<Friend> friendList=new ArrayList<>();
    String add_name;
    FriendAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_list);



        //读取信息
        ArrayList<Serializable>friendtemp=readObject(FriendsListActivity.this,"data");
        for(Serializable ser:friendtemp){
            friendList.add((Friend) ser);
        }

        adapter=new FriendAdapter(FriendsListActivity.this,R.layout.friends_list_item,friendList,FriendsListActivity.this);
        ListView listView=(ListView) findViewById(R.id.lvw_friends_list);
        listView.setAdapter(adapter);




        //添加好友，注意这里电话号码填11位,不要填后4位！（虚拟机可以填后4位或者11位）
        Button add_friend=(Button) findViewById(R.id.btn_friends_list_add);
        add_friend.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                final AddFriendDialog dialog = new AddFriendDialog(FriendsListActivity.this);
                dialog.show();
                dialog.setClicklistener(new AddFriendDialog.ClickListenerInterface() {
                    @Override
                    public void doConfirm() {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                        Friend friend = new Friend(dialog.getName(), dialog.getNumber(),"","");
                        add(friend);
                    }
                    @Override
                    public void doCancel() {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });
            }
        });
        //返回雷达按钮
      Button btn_radar=(Button)findViewById(R.id.btn_friends_list_radar);
        btn_radar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FriendsListActivity.this,MainActivity.class);
                ArrayList<Serializable>tempList=new ArrayList<Serializable>();
                for(int i=0;i<adapter.getCount();i++){
                    tempList.add(adapter.getItem(i));
                }
                saveObject(FriendsListActivity.this,tempList,"data");
                startActivity(intent);
//                Intent intent=new Intent();
//                Bundle bundle=new Bundle();
//                bundle.putSerializable("friend",(Serializable)friendList);
//                intent.putExtras(bundle);
//                setResult(RESULT_OK,intent);
//                finish();
            }
        });


    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        ArrayList<Serializable>tempList=new ArrayList<Serializable>();
//        for(int i=0;i<adapter.getCount();i++){
//            tempList.add(adapter.getItem(i));
//        }
//        saveObject(FriendsListActivity.this,tempList,"data");
//
//    }

    public void add(Friend friend){
        adapter.add(friend);
        adapter.notifyDataSetChanged();
    }
    public void del(Friend friend){
        adapter.remove(friend);
        adapter.notifyDataSetChanged();
    }

    //判断文件是否存在
    public static boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if(file == null || !file.exists()) {
            return false;
        }else
        return true;
    }

    //序列化保存方法
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



}
