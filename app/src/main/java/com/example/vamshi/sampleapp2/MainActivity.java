package com.example.vamshi.sampleapp2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    static final int MSG_REGISTER_CLIENT = 1;
    static final int MSG_UNREGISTER_CLIENT = 2;
    static final int MSG_SET_VALUE = 3;
    static final int CUSTOM_MSG = 4;
    static final int CUSTOM_DELETE_MSG = 5;
    private static final String TAG = MainActivity.class.getSimpleName();
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    Button mStart, mStop, mRegister, mUnRegister;
    Messenger mService = null;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            mService = new Messenger(service);

            if (service != null) {
                try {

                    Bundle b = new Bundle();
                    b.putString("str1", getApplicationContext().getPackageName());
                    Message msg1 = Message.obtain(null, CUSTOM_MSG);
                    msg1.setData(b);
                    msg1.replyTo = mMessenger;
                    mService.send(msg1);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            Log.d(TAG, "onServiceDisconnected: ");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStart = (Button) findViewById(R.id.start);
        mStop = (Button) findViewById(R.id.stop);

        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doBindService();
            }
        });

        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doUnbindService();

            }
        });

    }

//    private void SendMessage() {
//        if (mService != null) {
//            try {
//                Bundle b = new Bundle();
//                b.putString("str1", getApplicationContext().getPackageName());
//                Message msg1 = Message.obtain(null, CUSTOM_MSG);
//                msg1.setData(b);
//                msg1.replyTo = mMessenger;
//                mService.send(msg1);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void DeleteMessage() {
//        if (mService != null) {
//            try {
//                Bundle b = new Bundle();
//                b.putString("str1", getApplicationContext().getPackageName());
//                Message msg1 = Message.obtain(null, CUSTOM_DELETE_MSG);
//                msg1.setData(b);
//                msg1.replyTo = mMessenger;
//                mService.send(msg1);
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    void doBindService() {
        Intent i = new Intent("com.example.vamshi.portal.Service");
        i.setPackage("com.example.vamshi.portal");
        bindService(i, mConnection, Context.BIND_AUTO_CREATE);
    }

    void doUnbindService() {
        if (mService != null) {
            try {
                Bundle b = new Bundle();
                b.putString("str1", getApplicationContext().getPackageName());
                Message msg1 = Message.obtain(null, CUSTOM_DELETE_MSG);
                msg1.setData(b);
                msg1.replyTo = mMessenger;
                mService.send(msg1);
//
//                Message msg = Message.obtain(null,
//                        MSG_UNREGISTER_CLIENT);
//                msg.replyTo = mMessenger;
//                mService.send(msg);

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        unbindService(mConnection);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Client onDestroy()");
        doUnbindService();
        unbindService(mConnection);
    }

    static class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SET_VALUE:
                    Log.d(TAG, "Client handleMessage: added");
                    break;
                case MSG_REGISTER_CLIENT:
                    Log.d(TAG, "Client handleMessage: connected");
                    break;
                case CUSTOM_MSG:
                    Log.d(TAG, "Client handleMessage: connected " + msg.arg1);
                    break;
                case CUSTOM_DELETE_MSG:
                    Log.d(TAG, "Client handleMessage: disconnected " + msg.arg1);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}
