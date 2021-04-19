package hk.edu.cuhk.ie.iems5722.a2_1155069587;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ChatActivity extends AppCompatActivity {

    private ListView mListview;
    private ChatMessageAdapter mChatMessageAdapter;
    private EditText mMessageET;
    private ArrayList<ChatMessage> mChatMessages;

    private int mChatRoomId;
    private String mChatRoomName;

    private int mNextPage;
    private int mTotalPages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Bundle extras = getIntent().getExtras();
        this.mChatRoomId = extras.getInt("id");
        this.mChatRoomName = extras.getString("name");

        setTitle(this.mChatRoomName);

        this.mListview = (ListView) findViewById(R.id.lv_cr_content);

        this.mChatMessages = new ArrayList<ChatMessage>();
        this.mChatMessageAdapter = new ChatMessageAdapter(this, this.mChatMessages);//modified
        this.mListview.setAdapter(this.mChatMessageAdapter);

        this.mMessageET = (EditText) findViewById(R.id.et_cr_msget);


        String chatItemsApi = getString(R.string.chatitemlist_api);

        chatItemsApi = chatItemsApi + '?' + getString(R.string.chatitemlist_param1) + '=' + mChatRoomId
                + '&' + getString(R.string.chatitemlist_param2) + '=' + 1;
        Log.d("ChatActivity",chatItemsApi);
        new getChatItemsTask().execute(chatItemsApi);
        // Log.d("ChatActivity", "oncreate finished");

        this.mListview.setOnScrollListener(new AbsListView.OnScrollListener() {
            private boolean isFirst = false;

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.d("ChatSctivity", "fistvisibleitem=" + firstVisibleItem);
                if(mListview.getChildAt(firstVisibleItem)!=null) {
                    isFirst = false;

                    int top = mListview.getChildAt(firstVisibleItem).getTop();
                    Log.d("Chatactivity", "top=" + top);
                //    Log.d("Chatactivity", "topmessage"+ mChatMessages.get(0).getMessage());
                    if (firstVisibleItem == 0 && top == 0) {
                        isFirst = true;
                    }
                }

            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (isFirst && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (mNextPage <= mTotalPages) {

                        String chatItemsApi = getString(R.string.chatitemlist_api);
                        chatItemsApi = chatItemsApi + '?' + getString(R.string.chatitemlist_param1) + '=' + mChatRoomId
                                + '&' + getString(R.string.chatitemlist_param2) + '=' + mNextPage;
                        Log.d("ChatActivity", "fetch a new page page=" + mNextPage);
                        new getChatItemsTask().execute(chatItemsApi);

                        //mListview.setSelection(5);
                        //Log.d("ChatActivity","set selection to 5");

                    }


                }

            }
        });




    }

    public void refreshMessage(MenuItem menuItem){
        Log.d("ChatActivity","refreshed");
        String chatItemsApi = getString(R.string.chatitemlist_api);
        mChatMessages.clear();

        chatItemsApi = chatItemsApi + '?' + getString(R.string.chatitemlist_param1) + '=' + mChatRoomId
                + '&' + getString(R.string.chatitemlist_param2) + '=' + 1;
        Log.d("ChatActivity",chatItemsApi);
        new getChatItemsTask().execute(chatItemsApi);

    }

    public void sendMessage(View view){
        String message = mMessageET.getText().toString();
        if (message.equals(""))
            return;
        else if(message.trim().equals("")) {
            mMessageET.setText("");
            Toast.makeText(getApplicationContext(),getString(R.string.noblank),Toast.LENGTH_SHORT).show();
            return;
        }
        mMessageET.setText("");
        String myName = getString(R.string.myname);

        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        String sTime = time.format(calendar.getTime());
      //  ChatMessage chatmessage = new ChatMessage(myName,getResources().getInteger(R.integer.myuserid), true, message, sTime);//to be modified
      //  Log.d("ChatActivity","time="+time.toString());
    //    Log.d("ChatActivity", "myself is myself=" + chatmessage.isSelf());

        String sendMessageApi = getString(R.string.sendmessage_api);

        new sendChatItemTask().execute(sendMessageApi,""+mChatRoomId, ""+getResources().getInteger(R.integer.myuserid),
                getString(R.string.myname),message);

        String chatItemsApi = getString(R.string.chatitemlist_api);

        chatItemsApi = chatItemsApi + '?' + getString(R.string.chatitemlist_param1) + '=' + mChatRoomId
                + '&' + getString(R.string.chatitemlist_param2) + '=' + 1;
        Log.d("ChatActivity",chatItemsApi);
        mChatMessages.clear();
        new getChatItemsTask().execute(chatItemsApi);


        //mChatMessages.add(chatmessage);


        //mChatMessageAdapter.notifyDataSetChanged();
      //  mSentMessage = null;
       // mChatMessageAdapter.add(chatMessage);
    }

    //hide the keyboard when touch somewhere else
    //reference: http://stackoverflow.com/questions/8697499/hide-keyboard-when-user-taps-anywhere-else-on-the-screen-in-android
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return true;
    }



    class sendChatItemTask extends AsyncTask<String,Void, String>{
        private Exception exception;

        @Override
        protected String doInBackground(String... params) {
            String sendResult =  "";
            String line;

            try{
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                Uri.Builder builder = new Uri.Builder();
          //      builder.appendQueryParameter(getString(R.string.sendmessage_param1),mChatRoomId);
                builder.appendQueryParameter(getString(R.string.sendmessage_param1),params[1]);
                builder.appendQueryParameter(getString(R.string.sendmessage_param2), params[2]);
                builder.appendQueryParameter(getString(R.string.sendmessage_param3), params[3]);
                builder.appendQueryParameter(getString(R.string.sendmessage_param4), params[4]);

                String query = builder.build().getEncodedQuery();

                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                int response = conn.getResponseCode();
                if (response != 200){
                    Toast.makeText(getApplicationContext(),getString(R.string.servererror),Toast.LENGTH_SHORT).show();
                    return null;

                }

                Log.d("SendMessage", "response = " + response);
                InputStream is = conn.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null){
                    sendResult += line;
                }


            } catch (Exception e){
                Toast.makeText(getApplicationContext(),getString(R.string.servererror),Toast.LENGTH_SHORT).show();
                exception = e;
                return  null;
            }
            return sendResult;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null){
                try {
                    JSONObject json = new JSONObject(s);
                    String status = json.getString("status");
                    if (!status.equals("OK")) {
                        Toast.makeText(getApplicationContext(), getString(R.string.servererror), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){
                    Toast.makeText(getApplicationContext(),getString(R.string.servererror),Toast.LENGTH_SHORT).show();
                }
            }
            super.onPostExecute(s);
        }
    }

    class getChatItemsTask extends AsyncTask<String, Void, String>{

        private Exception exception;

        @Override
        protected String doInBackground(String... params) {
            String chatItemsData = "";
            String line;

            try{
                InputStream is = null;

                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");

                conn.setDoInput(true);
                conn.connect();

                int response = conn.getResponseCode();
                if (response != 200){
                    Toast.makeText(getApplicationContext(),getString(R.string.servererror),Toast.LENGTH_SHORT).show();
                    return null;
                }


                Log.d("ChatActivity", "response = " + response);

                is = conn.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null){
                    chatItemsData += line;
                }



            } catch (Exception e){
                exception = e;
                Toast.makeText(getApplicationContext(),getString(R.string.servererror),Toast.LENGTH_SHORT).show();
                return null;
            }

            return chatItemsData;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {

                try {
                    JSONObject json = new JSONObject(s);
                    String status = json.getString("status");
                    Log.d("ChatActivity", "status=" + status);
                    if (!status.equals("OK")) {
                        Toast.makeText(getApplicationContext(), getString(R.string.servererror), Toast.LENGTH_SHORT).show();
                        Log.d("ChatActivity", "not ok");
                    } else {

                        //Log.d("ChatActivity",s);
                        JSONObject data = json.getJSONObject("data");
                        JSONArray array = data.getJSONArray("messages");

//                    if(array!=null) {


                        mNextPage = data.getInt("current_page") + 1;
                        mTotalPages = data.getInt("total_pages");

                        Log.d("ChatActivity", "CurrentPage ==" + mNextPage);


                        for (int i = 0; i < array.length(); i++) {
                            String name = array.getJSONObject(i).getString("name");
                            boolean isSelf;
                            if (name.equals(getString(R.string.myname))) {
                                isSelf = true;
                            } else {
                                isSelf = false;

                            }
                            //    Log.d("ChatActivity","isSelf="+isSelf);
                            ChatMessage cm = new ChatMessage(array.getJSONObject(i).getString("name"),
                                    array.getJSONObject(i).getInt("user_id"), isSelf, array.getJSONObject(i).getString("message"),
                                    array.getJSONObject(i).getString("timestamp"));
                            //mChatMessages.add(0, cm);
                            mChatMessages.add(cm);
                            mChatMessageAdapter.notifyDataSetChanged();
                            //                       }
                        }
                        /*if (mChatMessages.size() >= 5) {
                            mListview.setSelection(5);
                            Log.d("ChatActivity", "set selection to 5");
                        }*/
                        //mListview.smoothScrollToPosition(0);
                        //mListview.smoothScrollToPositionFromTop(0,-10);
                    }


                } catch (Exception e) {
                    exception = e;
                    Toast.makeText(getApplicationContext(), getString(R.string.servererror), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            super.onPostExecute(s);
        }
    }

}
