package student.jnu.com.radar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ASUS on 2018/12/16.
 */

public class SMSBroadcastReceiver extends BroadcastReceiver {
   // private static MessageListener mMessageListener;

    private String number;
    private String latitude;
    private String longitude;
    MainActivity mainActivity;
    private static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";

    public SMSBroadcastReceiver(String number,String latitude,String longitude,MainActivity mainActivity){
        this.number=number;
        this.latitude=latitude;
        this.longitude=longitude;
        this.mainActivity=mainActivity;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        //判断广播消息
        if (action.equals(SMS_RECEIVED_ACTION)){
            Bundle bundle = intent.getExtras();
            //如果不为空
            if (bundle != null){
                //将pdus里面的内容转化成Object[]数组
                Object pdusData[] = (Object[]) bundle.get("pdus");
                //解析短信
                SmsMessage[] msg = new SmsMessage[pdusData.length];
                for (int i = 0;i < msg.length;i++){
                    byte pdus[] = (byte[]) pdusData[i];
                    msg[i] = SmsMessage.createFromPdu(pdus);
                }
                StringBuffer content = new StringBuffer();//获取短信内容
                StringBuffer phoneNumber = new StringBuffer();//获取地址
                StringBuffer receiveData = new StringBuffer();//获取时间
                //分析短信具体参数
                for (SmsMessage temp : msg){
                    content.append(temp.getMessageBody());
                    phoneNumber.append(temp.getOriginatingAddress());
                    receiveData.append(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS")
                            .format(new Date(temp.getTimestampMillis())));
                }
                /**
                 * 这里还可以进行好多操作，比如我们根据手机号进行拦截（取消广播继续传播）等等
                 */
//                Toast.makeText(context,phoneNumber.toString()+content+receiveData, Toast.LENGTH_LONG).show();//短信内容

                Toast.makeText(context,"收到短信来自:"+phoneNumber.toString(), Toast.LENGTH_LONG).show();
                //发送短信的号码
                number=phoneNumber.toString();//这里不需要取最后4位，1555521-5556,  添加好友时 电话号码也必须为11位
                //content的格式是 纬度,经度(xx.xxxxx,yyy.yyyyy )    前8个字符为纬度，后9个字符为经度
                String lat_long=content.toString();
                for(int i=0;i<lat_long.length();i++) {
                    if(lat_long.charAt(i)==',') {
                        latitude = lat_long.substring(0, i);
                        longitude = lat_long.substring(i+1);
                    }
                }

//                mainActivity.Num=mainActivity.friendList.size();
//                mainActivity.friendList.get(0).setLongtitude(number);
                for(int i=0;i<mainActivity.friendList.size();i++){
                    if(number.equals(mainActivity.friendList.get(i).getNumber())){
                        mainActivity.friendList.get(i).setLatitude(latitude);
                        mainActivity.friendList.get(i).setLongitude(longitude);
                        break;
                    }
                }
                ArrayList<Serializable>tempList=new ArrayList<Serializable>();
                for(int i=0;i<mainActivity.friendList.size();i++){
                    tempList.add(mainActivity.friendList.get(i));
                }
                saveObject(mainActivity,tempList,"data");
//                22.2451549124,113.5404167221
//                MainActivity.getPrivilege(Manifest.permission.SEND_SMS);

                SmsManager manager = SmsManager.getDefault();
                manager.sendTextMessage(	phoneNumber.toString(), null, "22.2606,113.543", null, null);
                //Toast.makeText(context,"已经发回经纬度信息", Toast.LENGTH_LONG).show();
                //自己:22.256729,113.541199       15555215554
                //Tom :22.2606,113.543          15555215556
                //Jack:22.25608,113.537835    15555215558
            }
        }

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

}
