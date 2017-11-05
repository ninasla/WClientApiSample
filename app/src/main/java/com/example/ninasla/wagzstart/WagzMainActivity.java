package com.example.ninasla.wagzstart;;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.IBinder;
//import android.support.wearable.activity.WearableActivity;
import android.os.MemoryFile;
import android.os.RemoteException;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;

public class WagzMainActivity extends Activity {

    private static final java.lang.String TAG = "WagzStart";
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wagz_main);

        mButton = (Button) findViewById(R.id.itcbutton);

        Intent intent = new Intent();
        intent.setClassName("com.itc.control","com.itc.control.ApiSampleService");
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    /** Called when the user touches the button */
    public void callSampleApi(View view) {
        if (mApiSampleService != null)
        {
            String str = "Hello from WagzStart APK";
            try {
                mApiSampleService.callApiSampleFunc(str);
            }
            catch( RemoteException e)
            {
                Log.e(TAG, "ItcSample:RemoteException");
            }
        }



        drawBitmap();
        createByteArray();
        printByteArray();
        createSharedMemory();
        writeToSharedMemory();

        FileDescriptor fd;
        fd = MemoryFileUtil.getFileDescriptor(mMemfile);

        if (mApiSampleService != null)
        {
            String str = "Update screen command  WagzStart APK";
            try {
                mApiSampleService.callUpdateDisplayFunc(str);
            }
            catch( RemoteException e)
            {
                Log.e(TAG, "ItcSample:RemoteException");
            }
        }

    }

    com.itc.control.IApiSampleService mApiSampleService;
    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            // Following the example above for an AIDL interface,
            // this gets an instance of the IRemoteInterface, which we can use to call on the service
            Log.d(TAG,"ItcSample:onServiceConnected " + className.flattenToString());
            mApiSampleService = com.itc.control.IApiSampleService.Stub.asInterface(service);
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            Log.e(TAG, "ItcSample: Service has unexpectedly disconnected");
            mApiSampleService = null;
        }
    };

    Bitmap mBitmap = Bitmap.createBitmap(180,100,Bitmap.Config.ARGB_8888);
    void drawBitmap()
    {
        Canvas canvas  = new Canvas(mBitmap);
        canvas.drawColor(Color.WHITE);
        Paint paint = new Paint(Color.BLACK);
        canvas.drawCircle(50, 30, 10, paint);
    }

    byte[] mBytes = new byte[18000];
    void createByteArray() {
        ByteBuffer buf = ByteBuffer.wrap(mBytes);
        try {
            for (int r = 0; r < 180; r++) {
                for (int c = 0; c < 100; c++) {
                    int i = c * 180 + r;
                    if (mBitmap.getPixel(r, c) == Color.WHITE) {
                        buf.put(i, (byte) 0x00);
                    } else {
                        //buf.put(i, (byte) 0xFF);
                        buf.put(i, (byte) 0x08);
                    }
                }
            }
        } catch (BufferUnderflowException e) {
            // always happens
            Log.d(TAG, e.toString());
        }
    }
    void printByteArray()
    {
        try
        {
            for(int r=0; r<180; r++ )
            {
                String str = new String();
                for(int c=0; c<100; c++)
                {
                    int i = c*180+r;
                    str+=Byte.toString(mBytes[i]);
                }
                Log.d(TAG, str);
            }
        } catch (BufferUnderflowException e) {
            Log.d(TAG, e.toString());
        }
    }

    MemoryFile mMemfile;
    void createSharedMemory()
    {
        try
        {
            mMemfile = new MemoryFile("itcmemfile", 18000);
            //mMemfile.allowPurging(true);
        } catch (java.io.IOException e) {

            Log.d(TAG, e.toString());
        }
    }

    void writeToSharedMemory()
    {
        try
        {
            mMemfile.writeBytes(mBytes, 0, 0, 18000);
        } catch (java.io.IOException e) {
            Log.d(TAG, e.toString());
        }
    }


}
