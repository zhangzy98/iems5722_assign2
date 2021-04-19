package hk.edu.cuhk.ie.iems5722.a2_1155069587;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;


    //   AtomicInteger msgId = new AtomicInteger();

    private String regid;


    private ListView mListview;
    private ArrayList<ChatRoom> mChatrooms = new ArrayList<>();
    private ChatRoomAdapter mChatroomsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity", "Before oncreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            SharedPreferences sharedPreferences = getSharedPreferences("iems5722", MODE_PRIVATE);
            SharedPreferences.Editor edit =  sharedPreferences.edit();
            //edit.clear();
            //edit.apply();

            regid = sharedPreferences.getString("token", "");
            Log.i("check gcm",""+regid);
            if (regid.isEmpty()) {
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }
        }

        this.mListview = (ListView) findViewById(R.id.lv_m_rmlst);

        mChatroomsAdapter = new ChatRoomAdapter(this, this.mChatrooms);
        //new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mChatroomNames);
        // mChatrooms.add("aaaa");
        // mChatrooms.add("bbbbb");

        this.mListview.setAdapter(mChatroomsAdapter);

        String chatroomsListApi = getString(R.string.chatroomlist_api);

        new GetChatroomsTask().execute(chatroomsListApi);

        this.mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatRoom rm = (ChatRoom) mListview.getItemAtPosition(position);
                int crid = rm.getId();
                String crname = rm.getName();
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                intent.putExtra("id", crid);
                intent.putExtra("name", crname);
                startActivity(intent);
             }

        });
    }


    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }

            return false;
        }

        return true;
    }

   /* public void openChatroom (View view){
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);

    }*/
    class GetChatroomsTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        @Override
        protected String doInBackground(String... urls) {

            String chatroomData = "";
            String line;
            try{

                InputStream is = null;

                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.setRequestMethod("GET");

                conn.setDoInput(true);

                conn.connect();

                int response = conn.getResponseCode();
                if (response != 200){
                    Toast.makeText(getApplicationContext(), getString(R.string.servererror), Toast.LENGTH_SHORT).show();
                    return null;

                }
                // todo : add some error control code
                Log.d("MainActivity", "response = " + response);

                is = conn.getInputStream();


                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    chatroomData += line;
                }

                Log.d("MainActivity",chatroomData);

            } catch (Exception e){
                this.exception = e;
                return null;
            }

            return chatroomData; // 會傳給 onPostExecute(String result) 的 String result
        }


        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            Log.d("MainActivity", "before process json");
            if (result!=null) {
                try {
                    JSONObject json = new JSONObject(result);
                    String status = json.getString("status");
                    if (!status.equals("OK")) {
                        //todo handle if status is not ok
                    } else {
                        JSONArray array = json.getJSONArray("data");
                        for (int i = 0; i < array.length(); i++) {
                            ChatRoom chatRoom = new ChatRoom(array.getJSONObject(i).getString("name"), array.getJSONObject(i).getInt("id"));
                            //  mChatrooms.add(array.getJSONObject(i).getString("name"));
                            mChatrooms.add(chatRoom);
                            Log.d("MainActivity", "processing json" + array.getJSONObject(i).getString("name"));
                            mChatroomsAdapter.notifyDataSetChanged();

                        }
                    }
                } catch (Exception e) {
                    this.exception = e;
                }
            }

            super.onPostExecute(result);



        }




    }



}
