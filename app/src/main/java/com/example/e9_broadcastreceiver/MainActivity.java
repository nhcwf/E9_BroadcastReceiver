package com.example.e9_broadcastreceiver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String ACTION_BROADCAST_DATE_VALIDATION = "broadcast-intent-date-validation";
    public static ExclusiveBroadcastReceiver broadcastReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText dateString = (EditText) findViewById(R.id.date_string_edittext);

        findViewById(R.id.start_service_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message;
                if (isValidFormat(DATE_FORMAT, dateString.getText().toString())) {
                    message = String.format("The date is %s.", dateString.getText().toString());
                } else {
                    message = "Invalid date!";
                }

                startSendingLocalBroadcast(message);

                broadcastReceiver = new ExclusiveBroadcastReceiver();
                IntentFilter intentFilter = new IntentFilter();

                intentFilter.addAction(ACTION_BROADCAST_DATE_VALIDATION);
                LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(broadcastReceiver, intentFilter);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(broadcastReceiver);
    }

    void startSendingLocalBroadcast(String sendingData) {
        Intent intent = new Intent(ACTION_BROADCAST_DATE_VALIDATION);
        intent.putExtra("broadcastIntent", sendingData);
        LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
    }

    boolean isValidFormat(String format, String value) {
        Date date = null;
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(value);
            if (!value.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return date != null;
    }
}

class ExclusiveBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), MainActivity.ACTION_BROADCAST_DATE_VALIDATION)) {
            String data = intent.getStringExtra("broadcastIntent");
            Toast.makeText(context, data, Toast.LENGTH_SHORT).show();

            LocalBroadcastManager.getInstance(context).unregisterReceiver(MainActivity.broadcastReceiver);
        }
    }
}