package com.example.ds.fid_me;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


public class RecommandActivity extends AppCompatActivity {

    TextView text;
    Button btn;
    ListView listView;

    String keyword;

    ArrayList<String> result_name_list;
    ArrayList<String> result_address_list;
    ArrayList<ArrayList> result_all_list;

    String name = "";
    String address = "";
    
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommand);

        text = (TextView) findViewById(R.id.textView);
        btn = (Button) findViewById(R.id.button);

        listView = (ListView)findViewById(R.id.recommandListView);
        Intent intent = getIntent();
        keyword = intent.getStringExtra("foodName");



      //  adapter = new RecommandAdapter();
   //     ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.recommand_listitem, result_name_list);
//        listView.setAdapter(adapter);
        result_name_list=new ArrayList<>();
        result_address_list = new ArrayList<>();
        result_all_list = new ArrayList<>();

        showList();

    }

    private void showList() {
        new Thread(new Runnable() {

            @Override
            public void run() {


                String clientId = "Bxn8VZxrR7tA6L6oV9Fa";//애플리케이션 클라이언트 아이디값";
                String clientSecret = "Uf8Ldu5n8p";//애플리케이션 클라이언트 시크릿값";
                final StringBuffer buffer = new StringBuffer();


                try {

                    result_name_list.clear();
                    // result_link_list.clear();

                    String apiURL = "https://openapi.naver.com/v1/search/local.xml?query=" + keyword +"&display=20";

                    URL url = new URL(apiURL);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("X-Naver-Client-Id", clientId);
                    con.setRequestProperty("X-Naver-Client-Secret", clientSecret);

                    InputStream is= con.getInputStream();

                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(new InputStreamReader(is, "UTF-8"));
                    String tag;

                    xpp.next();
                    int eventType = xpp.getEventType();

                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        switch (eventType) {
                            case XmlPullParser.START_DOCUMENT:
                                buffer.append("start NAVER XML parsing...\n\n");
                                break;


                            case XmlPullParser.START_TAG:

                                tag = xpp.getName();    //테그 이름 얻어오기

                                if (tag.equals("item")) ;// 첫번째 검색결과
                                else if (tag.equals("title")) {

                                    buffer.append("업소명 : ");
                                    xpp.next();
                                    buffer.append(xpp.getText()); //title 요소의 TEXT 읽어와서 문자열버퍼에 추가
                                    buffer.append("\n");          //줄바꿈 문자 추가
                                    name = xpp.getText();

                                } else if (tag.equals("category")) {

                                    buffer.append("업종 : ");
                                    xpp.next();
                                    buffer.append(xpp.getText()); //category 요소의 TEXT 읽어와서 문자열버퍼에 추가
                                    buffer.append("\n");          //줄바꿈 문자 추가

                                } else if (tag.equals("description")) {
                                    buffer.append("세부설명 :");
                                    xpp.next();
                                    buffer.append(xpp.getText()); //description 요소의 TEXT 읽어와서 문자열버퍼에 추가
                                    buffer.append("\n");          //줄바꿈 문자 추가

                                } else if (tag.equals("telephone")) {

                                    buffer.append("연락처 :");
                                    xpp.next();
                                    buffer.append(xpp.getText()); //telephone 요소의 TEXT 읽어와서 문자열버퍼에 추가
                                    buffer.append("\n");          //줄바꿈 문자 추가

                                } else if (tag.equals("address")) {

                                    buffer.append("주소 :");
                                    xpp.next();
                                    buffer.append(xpp.getText()); //address 요소의 TEXT 읽어와서 문자열버퍼에 추가
                                    buffer.append("\n");          //줄바꿈 문자 추가

                                    address = xpp.getText();
                                    result_name_list.add(name + ':' +' '+ address);


                                }

                                else if (tag.equals("mapx")) {

                                    buffer.append("지도 위치 X :");
                                    xpp.next();
                                    buffer.append(xpp.getText()); //mapx 요소의 TEXT 읽어와서 문자열버퍼에 추가
                                    buffer.append("  ,  ");          //줄바꿈 문자 추가

                                } else if (tag.equals("mapy")) {
                                    buffer.append("지도 위치 Y :");
                                    xpp.next();
                                    buffer.append(xpp.getText()); //mapy 요소의 TEXT 읽어와서 문자열버퍼에 추가
                                    buffer.append("\n");          //줄바꿈 문자 추가

                                }

                                break;

                            case XmlPullParser.TEXT:
                                break;

                            case XmlPullParser.END_TAG:
                                tag = xpp.getName();    //테그 이름 얻어오기
                                if (tag.equals("item")) buffer.append("\n"); // 첫번째 검색결과종료..줄바꿈
                                break;

                        }
                        eventType = xpp.next();
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                buffer.append("end NAVER XML parsing...\n");
                System.out.println(buffer.toString());




                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.recommand_listitem, R.id.textname, result_name_list);
                        listView.setAdapter(adapter);
                        //   text.setText(buffer.toString());n
                    }
                });

            }
        }).start();


    }

    // 검색버튼 클릭하면,
    public void mOnclick(View view) {
/*
        new Thread(new Runnable() {

            @Override
            public void run() {


                String clientId = "Bxn8VZxrR7tA6L6oV9Fa";//애플리케이션 클라이언트 아이디값";
                String clientSecret = "Uf8Ldu5n8p";//애플리케이션 클라이언트 시크릿값";
                final StringBuffer buffer = new StringBuffer();


                try {

                    result_name_list.clear();
                   // result_link_list.clear();

                    String apiURL = "https://openapi.naver.com/v1/search/local.xml?query=" + keyword +"&display=20";

                    URL url = new URL(apiURL);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("X-Naver-Client-Id", clientId);
                    con.setRequestProperty("X-Naver-Client-Secret", clientSecret);

                    InputStream is= con.getInputStream();

                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(new InputStreamReader(is, "UTF-8"));
                    String tag;

                    xpp.next();
                    int eventType = xpp.getEventType();

                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        switch (eventType) {
                            case XmlPullParser.START_DOCUMENT:
                                buffer.append("start NAVER XML parsing...\n\n");
                                break;


                            case XmlPullParser.START_TAG:

                                tag = xpp.getName();    //테그 이름 얻어오기

                                if (tag.equals("item")) ;// 첫번째 검색결과
                                else if (tag.equals("title")) {

                                    buffer.append("업소명 : ");
                                    xpp.next();
                                    buffer.append(xpp.getText()); //title 요소의 TEXT 읽어와서 문자열버퍼에 추가
                                    buffer.append("\n");          //줄바꿈 문자 추가
                                    name = xpp.getText();

                                } else if (tag.equals("category")) {

                                    buffer.append("업종 : ");
                                    xpp.next();
                                    buffer.append(xpp.getText()); //category 요소의 TEXT 읽어와서 문자열버퍼에 추가
                                    buffer.append("\n");          //줄바꿈 문자 추가

                                } else if (tag.equals("description")) {
                                    buffer.append("세부설명 :");
                                    xpp.next();
                                    buffer.append(xpp.getText()); //description 요소의 TEXT 읽어와서 문자열버퍼에 추가
                                    buffer.append("\n");          //줄바꿈 문자 추가

                                } else if (tag.equals("telephone")) {

                                    buffer.append("연락처 :");
                                    xpp.next();
                                    buffer.append(xpp.getText()); //telephone 요소의 TEXT 읽어와서 문자열버퍼에 추가
                                    buffer.append("\n");          //줄바꿈 문자 추가

                                } else if (tag.equals("address")) {

                                    buffer.append("주소 :");
                                    xpp.next();
                                    buffer.append(xpp.getText()); //address 요소의 TEXT 읽어와서 문자열버퍼에 추가
                                    buffer.append("\n");          //줄바꿈 문자 추가

                                    address = xpp.getText();
                                    result_name_list.add(name + ':' +' '+ address);


                                }

                                else if (tag.equals("mapx")) {

                                    buffer.append("지도 위치 X :");
                                    xpp.next();
                                    buffer.append(xpp.getText()); //mapx 요소의 TEXT 읽어와서 문자열버퍼에 추가
                                    buffer.append("  ,  ");          //줄바꿈 문자 추가

                                } else if (tag.equals("mapy")) {
                                    buffer.append("지도 위치 Y :");
                                    xpp.next();
                                    buffer.append(xpp.getText()); //mapy 요소의 TEXT 읽어와서 문자열버퍼에 추가
                                    buffer.append("\n");          //줄바꿈 문자 추가

                                }

                                break;

                            case XmlPullParser.TEXT:
                                break;

                            case XmlPullParser.END_TAG:
                                tag = xpp.getName();    //테그 이름 얻어오기
                                if (tag.equals("item")) buffer.append("\n"); // 첫번째 검색결과종료..줄바꿈
                                break;

                        }
                        eventType = xpp.next();
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                buffer.append("end NAVER XML parsing...\n");
                System.out.println(buffer.toString());




                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.recommand_listitem, R.id.textname, result_name_list);
                        listView.setAdapter(adapter);
                        //   text.setText(buffer.toString());n
                    }
                });

            }
        }).start();

*/

        Intent intent = new Intent(getApplicationContext(), FindActivity.class);
        startActivity(intent);
        finish();
    }


    class RecommandAdapter extends BaseAdapter {
        ArrayList<RecommandListItem> items = new ArrayList<RecommandListItem>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(RecommandListItem item){
            items.add(item);
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            RecommandItemView view = new RecommandItemView(getApplicationContext());
            RecommandListItem item = items.get(i);
            view.setName(item.getName());
            view.setAddress(item.getAddress());
            return view;
        }
    }

}

