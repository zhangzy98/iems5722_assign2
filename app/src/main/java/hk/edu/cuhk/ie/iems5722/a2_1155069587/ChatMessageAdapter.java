package hk.edu.cuhk.ie.iems5722.a2_1155069587;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by WD-MAC on 16/2/2.
 */
public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {
    public ChatMessageAdapter(Context context, ArrayList<ChatMessage> messages){
        super(context, 0, messages);
    }
    /*    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // Get the data item for this position
       User user = getItem(position);
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
          convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
       }
       // Lookup view for data population
       TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
       TextView tvHome = (TextView) convertView.findViewById(R.id.tvHome);
       // Populate the data into the template view using the data object
       tvName.setText(user.name);
       tvHome.setText(user.hometown);
       // Return the completed view to render on screen
       return convertView;
   }*/

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ChatMessage chatMessage = getItem(position);

      //  if(convertView == null){

           // Log.d("messageadapter","isself="+chatMessage.isSelf());
            if(chatMessage.isSelf()) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_item_right, parent, false);
            }
            else{
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_item_left, parent, false);
            }
       // }
        TextView textViewUsrnm = (TextView) convertView.findViewById(R.id.tv_ctitm_usrnm);
        TextView textViewMsg = (TextView) convertView.findViewById(R.id.tv_ctitm_msg);
        TextView textViewTm = (TextView) convertView.findViewById(R.id.tv_ctitm_time);
        textViewUsrnm.setText(chatMessage.getFromName());
        textViewMsg.setText(chatMessage.getMessage());
        textViewTm.setText((chatMessage.getTimeStamp()));
        return convertView;
    }
    /*public static String setTime(long timeStamp){

        SimpleDateFormat time = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        return time.format(calendar.getTime());
    }*/
//////////below is modified//////////////
    @Override
    public ChatMessage getItem(int position) {
        return super.getItem(super.getCount() - position - 1);

    }
}
