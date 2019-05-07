package student.jnu.com.radar;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by lenovo on 2018/12/7.
 */

public class AddFriendDialog extends Dialog{
    private Context context;
    private ClickListenerInterface clickListenerInterface;
    private String timeInfo;

    private EditText dialog_name;
    private EditText dialog_number;

    public interface ClickListenerInterface {

        public void doConfirm();

        public void doCancel();
    }

    public AddFriendDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.ubw_add_friend_dialog, null);
        setContentView(view);

        ImageView tvConfirm = (ImageView) view.findViewById(R.id.dialog_confirm);
        ImageView tvCancel = (ImageView) view.findViewById(R.id.dialog_cancel);

        dialog_name = (EditText)findViewById(R.id.dialog_name);
        dialog_number = (EditText)findViewById(R.id.dialog_number);

        tvConfirm.setOnClickListener(new clickListener());
        tvCancel.setOnClickListener(new clickListener());

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6
        dialogWindow.setAttributes(lp);
    }

    public void setClicklistener(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    private class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int id = v.getId();
            switch (id) {
                case R.id.dialog_confirm:
                    clickListenerInterface.doConfirm();
                    break;
                case R.id.dialog_cancel:
                    clickListenerInterface.doCancel();
                    break;
            }
        }

    };

    public String getName(){
        return dialog_name.getText().toString();
    }

    public String getNumber(){
        return dialog_number.getText().toString();
    }
}
