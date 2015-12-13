package com.example.eric.ericlightswitch;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SetupListeners();
    }

    private void SetupListeners()
    {
        //todo: get all buttons on view
        final List<ToggleButton> buttons = new ArrayList<ToggleButton>();
        buttons.add((ToggleButton) findViewById(R.id.toggleButton1));
        buttons.add((ToggleButton) findViewById(R.id.toggleButton2));
        buttons.add((ToggleButton) findViewById(R.id.toggleButton3));

        final Switch toggleAllSwitch = (Switch) findViewById(R.id.switchAll);
        toggleAllSwitch.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                for (ToggleButton button : buttons) {
                    int number = (Integer) button.getTag();
                    ToggleSwitch(number);
                }
            }
        });
    }

    public void ButtonClick(View view) {
        ToggleButton button = (ToggleButton) view;
        int number = (Integer) button.getTag();
        boolean success = ToggleSwitch(number);

        if (!success)
        {
            Toast.makeText(MainActivity.this, "No dice", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean ToggleSwitch(int lightNumber)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String piAddress = settings.getString("preference_ip_address", "");

        //String piAddress = getString(R.string.pi_ip_address);
        String url = piAddress + "/toggle/" + lightNumber;
        String response = GetPostData(url);
        return true;
    }

    private String GetPostData(String url)
    {
        String responseString = null;
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpResponse response = httpClient.execute(new HttpGet(url));

            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                responseString = out.toString();
                out.close();
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        }
        catch (Exception e) {

        }

        return responseString;
    }
}