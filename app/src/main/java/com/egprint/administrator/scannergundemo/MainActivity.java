package com.egprint.administrator.scannergundemo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.egprint.administrator.scannergundemo.ScanGunKeyEventHelper.OnScanSuccessListener;

import java.util.ArrayList;

public class MainActivity extends Activity implements OnScanSuccessListener {

    private ListView lv_main;
    private TextView tv_main_title_num;
    private ScanAdapter mAdapter;
    private ArrayList<String> mList;
    private Context mContext;
    private ScanGunKeyEventHelper mScanGunKeyEventHelper;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.test);
        btn.setVisibility(View.GONE);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mScanGunKeyEventHelper.hasScanGunName(mContext);
//            }
//        });
        initView();
        initData();
    }

    private void initView() {
        lv_main = (ListView) findViewById(R.id.lv_main);
        tv_main_title_num = (TextView) findViewById(R.id.tv_main_title_num);
    }

    private void initData() {
        mContext = this;
        mList = new ArrayList<>();
        mAdapter = new ScanAdapter(mContext, mList);
        lv_main.setAdapter(mAdapter);
        mScanGunKeyEventHelper = new ScanGunKeyEventHelper(this);
    }

    /**
     * 截获按键事件.发给ScanGunKeyEventHelper
     *
     * @param event
     * @return
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            mScanGunKeyEventHelper.analysisKeyEvent(event);
            return true;
        }
//        if (mScanGunKeyEventHelper.isScanGunEvent(event)) {
//            mScanGunKeyEventHelper.analysisKeyEvent(event);
//            return true;
//        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (!mScanGunKeyEventHelper.hasScanGun()) {
//            Toast.makeText(mContext, "未检测到扫码枪设备", Toast.LENGTH_SHORT).show();
//        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mScanGunKeyEventHelper.onDestroy();
    }

    @Override
    public void onScanSuccess(String barcode) {
        mList.add(barcode);
        tv_main_title_num.setText("" + mList.size());
        mAdapter.notifyDataSetChanged();
    }
}
