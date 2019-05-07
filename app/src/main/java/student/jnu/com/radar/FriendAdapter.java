package student.jnu.com.radar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by ASUS on 2018/12/14.
 */

public class FriendAdapter extends ArrayAdapter<Friend> {
    private int resourceId;
    FriendsListActivity friendsListActivity;

    public FriendAdapter(Context context, int textViewResourceId, List<Friend> objects,FriendsListActivity friendsListActivity){
        super(context,textViewResourceId,objects);
        this.friendsListActivity = friendsListActivity;
        resourceId=textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Friend friend=getItem(position);
        View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView friendName=(TextView)view.findViewById(R.id.name_cell);
        friendName.setText(friend.getName());

        //实现删除按钮
        Button btn_del=(Button)view.findViewById(R.id.delete_button_cell);
        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              del_item(friend, friendsListActivity);
            }
        });
        return view;
    }

    public void del_item(final Friend friend, final FriendsListActivity friendsListActivity){
        AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
        dialog.setTitle("DELETE FRIEND");
        dialog.setMessage("Name:"+friend.getName()+"  Number:"+friend.getNumber());
        dialog.setCancelable(false);
        dialog.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                friendsListActivity.del(friend);
                Toast.makeText(friendsListActivity,"DELETE SUCCESSFULLY",Toast.LENGTH_SHORT).show();
            }
        });
        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();

    }

}
