//package student.jnu.com.myapplication;
//
//import android.app.Activity;
//import android.content.pm.PackageManager;
//import android.provider.Telephony;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.telephony.SmsManager;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import java.util.ArrayList;
//import java.util.jar.Manifest;
//
//public class MainActivity extends Activity {
//    private Button btn_send;
//    private EditText edit_phone,edit_content;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        btn_send=(Button)findViewById(R.id.btn_send);
//        edit_phone=(EditText)findViewById(R.id.edit_phone);
//        edit_content=(EditText)findViewById(R.id.content);
//
//        btn_send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String phone=edit_phone.getText().toString();
//                String content=edit_content.getText().toString();
//                SmsManager manager=SmsManager.getDefault();
//                ArrayList<String> list=manager.divideMessage(content);
//                for(String text:list){
//                    manager.sendTextMessage(phone,null,text,null,null);
//
//                }
//                Toast.makeText(getApplicationContext(),"发送完毕",Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//}