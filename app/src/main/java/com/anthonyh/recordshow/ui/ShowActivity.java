package com.anthonyh.recordshow.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.anthonyh.recordshow.MainActivity;
import com.anthonyh.recordshow.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hf on 2019-04-10.
 */
public class ShowActivity extends Activity {

    private static final String TAG = "ShowActivity";
    ListView listView;

    List<String> dataList = new ArrayList<>();
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (dataList.size() == 0) {
                Toast.makeText(getApplicationContext(), "没有文件", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!ShowActivity.this.isDestroyed()) {
                adapter.notifyDataSetChanged();
            }
        }
    };
    Adapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        listView = findViewById(R.id.listView);
        adapter = new Adapter(dataList);
        listView.setAdapter(adapter);
        new Thread(new GetFile()).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    class GetFile implements Runnable {
        @Override
        public void run() {
            File file = new File(MainActivity.AudioPath);
            if (!file.exists()) {
                return;
            }
            File[] fileArray = file.listFiles();
            for (File fileContent : fileArray) {
                dataList.add(fileContent.getName());
                Log.e(TAG, "run: " + fileContent.getName());
            }
            handler.sendEmptyMessage(0);
        }
    }


    class Adapter extends BaseAdapter {

        List<String> list;

        public Adapter(List<String> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.item, null);
            TextView textView = view.findViewById(R.id.itemTv);
            textView.setText(list.get(position));
            return view;
        }
    }

}
