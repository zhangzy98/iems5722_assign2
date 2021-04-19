package hk.edu.cuhk.ie.iems5722.a2_1155069587;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by WD-MAC on 16/2/18.
 */
public class ChatRoomAdapter  extends ArrayAdapter<ChatRoom> {
    public ChatRoomAdapter(Context context, ArrayList<ChatRoom> rooms){
        super(context, 0, rooms);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ChatRoom chatRoom = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chatroom_item, parent, false);
        }
        TextView textViewRmnm = (TextView) convertView.findViewById(R.id.tv_critm_ctnm);
        textViewRmnm.setText(chatRoom.getName());
        return convertView;
    }
}
