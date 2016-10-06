package com.gowhich.androidhandlerproduct;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ProgressDialog progressDialog;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) this.findViewById(R.id.listView);
        progressDialog = new ProgressDialog(this);
        myAdapter = new MyAdapter(this);

        progressDialog.setTitle("下载提示");
        progressDialog.setMessage("正在下载中...");
        new MyTask().execute(CommonUtils.PRODUCT_URL);


    }

    public class MyTask extends AsyncTask<String, Void, List<Map<String, Object>>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> maps) {
            super.onPostExecute(maps);
            myAdapter.setData(maps);
            listView.setAdapter(myAdapter);
            myAdapter.notifyDataSetChanged();
            progressDialog.dismiss();
        }

        @Override
        protected List<Map<String, Object>> doInBackground(String... params) {
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(params[0]);
            HttpResponse httpResponse = null;
            try {
                httpResponse = httpClient.execute(httpGet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                try {
                    String jsonString = EntityUtils.toString(httpResponse.getEntity(), "utf-8");

                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONArray jsonArray = jsonObject.getJSONArray("products");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        Map<String, Object> map = new HashMap<String, Object>();
                        //迭代出json
                        Iterator<String> iterator = jsonObject1.keys();
                        while (iterator.hasNext()) {
                            String key = iterator.next();
                            Object value = jsonObject1.get(key);
                            map.put(key, value);
                        }
                        list.add(map);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            return list;
        }
    }

    public class MyAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;
        private List<Map<String, Object>> list = null;

        public MyAdapter(Context context) {
            this.context = context;
            layoutInflater = layoutInflater.from(context);
        }

        public void setData(List<Map<String, Object>> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.product_item, null);
            } else {
                view = convertView;
            }

            TextView name = (TextView) view.findViewById(R.id.textView);
            TextView price = (TextView) view.findViewById(R.id.textView2);
            final ImageView image = (ImageView) view.findViewById(R.id.imageView);

            name.setText(list.get(position).get("proname").toString());
            price.setText(list.get(position).get("proprice").toString());
            DownloadImage downloadImage = new DownloadImage(CommonUtils.IMAGE_URL + list.get(position).get("proimage").toString());
            downloadImage.loadImage(new DownloadImage.ImageCallback() {
                @Override
                public void getDrawable(Drawable drawable) {
                    image.setImageDrawable(drawable);
                }
            });
            return view;
        }
    }
}
