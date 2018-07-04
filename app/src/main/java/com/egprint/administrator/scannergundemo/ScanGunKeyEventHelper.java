package com.egprint.administrator.scannergundemo;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2018/7/4 0004.
 * <p>
 * 扫码枪事件解析类
 */

public class ScanGunKeyEventHelper {
    private final static long MESSAGE_DELAY = 500;             //延迟500ms，判断扫码是否完成。
    private StringBuffer mStringBufferResult;                  //扫码内容
    private boolean mCaps;                                     //大小写区分
    private final Handler mHandler;
    private final BluetoothAdapter mBluetoothAdapter;
    private final Runnable mScanningFishedRunnable;
    private OnScanSuccessListener mOnScanSuccessListener;
    private String mDeviceName;

    public ScanGunKeyEventHelper(OnScanSuccessListener onScanSuccessListener) {
        mOnScanSuccessListener = onScanSuccessListener;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mStringBufferResult = new StringBuffer();
        mHandler = new Handler();
        mScanningFishedRunnable = new Runnable() {
            @Override
            public void run() {
                performScanSuccess();
            }
        };
    }


    /**
     * 返回扫码成功后的结果
     */
    private void performScanSuccess() {
        String barcode = mStringBufferResult.toString();
        if (mOnScanSuccessListener != null)
            mOnScanSuccessListener.onScanSuccess(barcode);
        mStringBufferResult.setLength(0);
    }


    /**
     * 扫码枪事件解析
     *
     * @param event
     */
    public void analysisKeyEvent(KeyEvent event) {

        int keyCode = event.getKeyCode();

        //字母大小写判断
        checkLetterStatus(event);

        if (event.getAction() == KeyEvent.ACTION_DOWN) {

            char aChar = getInputCode(event);

            if (aChar != 0) {
                mStringBufferResult.append(aChar);
            }

            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                //若为回车键，直接返回
                mHandler.removeCallbacks(mScanningFishedRunnable);
                mHandler.post(mScanningFishedRunnable);
            } else {
                //延迟post，若500ms内，有其他事件
                mHandler.removeCallbacks(mScanningFishedRunnable);
                mHandler.postDelayed(mScanningFishedRunnable, MESSAGE_DELAY);
            }

        }
    }

    //检查shift键
    private void checkLetterStatus(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT || keyCode == KeyEvent.KEYCODE_SHIFT_LEFT) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                //按着shift键，表示大写
                mCaps = true;
            } else {
                //松开shift键，表示小写
                mCaps = false;
            }
        }
    }


    //获取扫描内容
    private char getInputCode(KeyEvent event) {

        int keyCode = event.getKeyCode();

        char aChar;

        if (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z) {
            //字母
            aChar = (char) ((mCaps ? 'A' : 'a') + keyCode - KeyEvent.KEYCODE_A);
        } else if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
            //数字
            aChar = (char) ('0' + keyCode - KeyEvent.KEYCODE_0);
        } else {
            //其他符号
            switch (keyCode) {
                case KeyEvent.KEYCODE_PERIOD:
                    aChar = '.';
                    break;
                case KeyEvent.KEYCODE_MINUS:
                    aChar = mCaps ? '_' : '-';
                    break;
                case KeyEvent.KEYCODE_SLASH:
                    aChar = '/';
                    break;
                case KeyEvent.KEYCODE_BACKSLASH:
                    aChar = mCaps ? '|' : '\\';
                    break;
                case KeyEvent.KEYCODE_COMMA:
                    aChar = ',';
                    break;
                default:
                    aChar = 0;
                    break;
            }
        }

        return aChar;

    }


    public interface OnScanSuccessListener {
        void onScanSuccess(String barcode);
    }


    public void onDestroy() {
        mHandler.removeCallbacks(mScanningFishedRunnable);
        mOnScanSuccessListener = null;
    }


    //部分手机如三星，无法使用该方法
//    public boolean hasScanGun() {
//        Configuration cfg = getResources().getConfiguration();
//        return cfg.keyboard != Configuration.KEYBOARD_NOKEYS;
//    }

//    public void hasScanGunName(Context ctx) {
//        final UsbManager mUsbManager = (UsbManager) ctx.getSystemService(Context.USB_SERVICE);
//        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
//        List<UsbDevice> data = new ArrayList<>();
//        for (UsbDevice item : deviceList.values()) {
//            if (item.getInterfaceCount() > 0) {
//                if (item.getInterface(0).getInterfaceClass() == UsbConstants.USB_CLASS_HID) {
////                    data.add(item);
////                /**
////                 * USB class for human interface devices (for example, mice and keyboards).
////                 */
////                public static final int USB_CLASS_HID = 3;
//
//                    Log.d("chensy", "hasScanGunName: item.getVendorId()" + item.getVendorId());   // 34952
//                    Log.d("chensy", "hasScanGunName: item.getDeviceName()" + item.getDeviceName()); //
//                    Log.d("chensy", "hasScanGunName: item.getProductName()" + item.getProductName()); // Usb211
//                    Log.d("chensy", "hasScanGunName: item.getManufacturerName()" + item.getManufacturerName()); // Usb211
//                    Log.d("chensy", "hasScanGunName: item.getProductId()" + item.getProductId()); // 3886
//                    Log.d("chensy", "hasScanGunName: item.getDeviceId()" + item.getDeviceId()); // 2006
//                    Log.d("chensy", "+++++++++++++++");
//                    Log.d("chensy", "---------------");
//                    Log.d("chensy", "+++++++++++++++");
//
//
//                }
//
//            }
//        }
//    }

//    /**
//     * 扫描枪是否连接
//     * @return
//     */
//    public boolean hasScanGun() {
//
//        if (mBluetoothAdapter == null) {
//            return false;
//        }
//
//        Set<BluetoothDevice> blueDevices = mBluetoothAdapter.getBondedDevices();
//
//        if (blueDevices == null || blueDevices.size() <= 0) {
//            return false;
//        }
//
//        for (Iterator<BluetoothDevice> iterator = blueDevices.iterator(); iterator.hasNext(); ) {
//            BluetoothDevice bluetoothDevice = iterator.next();
//
//            if (bluetoothDevice.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.PERIPHERAL) {
//                mDeviceName = bluetoothDevice.getName();
//                return isInputDeviceExist(mDeviceName);
//            }
//
//        }
//
//        return false;
//
//    }


    /**
     * 输入设备是否存在
     *
     * @param deviceName
     * @return
     */
    private boolean isInputDeviceExist(String deviceName) {
        int[] deviceIds = InputDevice.getDeviceIds();

        for (int id : deviceIds) {
            if (InputDevice.getDevice(id).getName().equals(deviceName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 是否为扫码枪事件(部分机型KeyEvent获取的名字错误)
     *
     * @param event
     * @return
     */
    @Deprecated
    public boolean isScanGunEvent(KeyEvent event) {
        return event.getDevice().getName().equals(mDeviceName);
    }
}
