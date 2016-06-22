package test.android.readerdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class ChaActivity extends Activity {

    private ListView lv;
    private ArrayList<File> files;
    private MySimpleAdapter adapter;

    private Bundle bundle;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cha);
        lv = (ListView) findViewById(R.id.lv);
        files = new ArrayList<>();
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.i("TAG", "ChaActivity onCreate()");
            File path = Environment.getExternalStorageDirectory();// 获得SD卡路径
            // File path = new File("/mnt/sdcard/");
            File[] filespath = path.listFiles();// 读取
            getFileName(filespath);
            FromFile2String(files);
        }
        Log.i("TAG", files.size() + "");
        adapter = new MySimpleAdapter();
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(ChaActivity.this, ""+files.get(position), Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(ChaActivity.this,ReadActivity.class);

                bundle = new Bundle();
                File file = files.get(position);

                bundle.putString("FileName", file.getAbsolutePath());
                intent.putExtras(bundle);

                startActivityForResult(intent,0);
            }
        });
    }

    private class MySimpleAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return files.size();
        }

        @Override
        public Object getItem(int position) {
            return files.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(ChaActivity.this, R.layout.item_list, null);
                holder = new ViewHolder();
                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tv_title.setText(names.get(position));

            return convertView;
        }
    }

    static class ViewHolder {
        private TextView tv_title;
    }

//一会把这个在分线程中操作


    private void getFileName(final File[] file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (file != null) {// 先判断目录是否为空，否则会报空指针
                    for (File filer : file) {
                        if (filer.isDirectory()) {

                            getFileName(filer.listFiles());
                        } else {
                            if (isValidFileOrDir(filer)) {
                                files.add(filer);
                            }
                        }
                    }
                }
            }
        }).start();
//        if (file != null) {// 先判断目录是否为空，否则会报空指针
//            for (File filer : file) {
//                if (filer.isDirectory()) {
//
//                    getFileName(filer.listFiles());
//                } else {
//                    if (isValidFileOrDir(filer)) {
//                        files.add(filer);
//                    }
//                }
//            }
//        }
    }
    private ArrayList<String> names;

    /**
     * 将文件集合转化成文件名集合，用于显示
     * @param files
     */
    private void FromFile2String(ArrayList<File> files){
        names=new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            String nameString = files.get(i).getName();
            names.add(nameString);
        }
    }

    /**
     *检查是否为合法的文件名
     */
    private boolean isValidFileOrDir(File file) {
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".txt")) {
            return true;
        }
        return false;
    }

    private boolean esc=false;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    esc=false;
                    break;
            }
        }
    };

    /**
     * 两次点击返回退出应用
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(!esc){
                esc=true;
                Toast.makeText(ChaActivity.this, "再次点击退出应用", Toast.LENGTH_SHORT).show();
                handler.sendEmptyMessageDelayed(0,2000);
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

}
