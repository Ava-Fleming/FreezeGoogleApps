package org.chaosdevs.devpump.freezegoogle;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupComponents();

    }
    public void setupComponents(){
        Button btn = (Button) findViewById(R.id.button);
        Button btn2 = (Button) findViewById(R.id.button2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FreezeOrUnfreeze().execute("freeze");
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FreezeOrUnfreeze().execute("unfreeze");
            }
        });
    }
public static class FreezeOrUnfreeze extends AsyncTask<String, Integer, String>{

    @Override
    protected String doInBackground(String... strings) {
        ArrayList al = new ArrayList();
        try{
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());
            outputStream.writeBytes("pm list packages\n");
            outputStream.flush();
            outputStream.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(su.getInputStream()));
            String line;
            while((line = br.readLine()) != null){
                if(line.contains("com.google")){
                    al.add(line.substring(8,line.length()));
                }
            }
            br.close();
            String typeOfAction = "disable";
            if(String.valueOf(strings) == "freeze"){
                typeOfAction = "disable";
            }
            else{
                typeOfAction = "enable";
            }
            su = Runtime.getRuntime().exec("su");
            outputStream = new DataOutputStream(su.getOutputStream());
            for(int a=0; a < al.size(); a++){
                outputStream.writeBytes("pm "+ typeOfAction + " "+ al.get(a).toString() + "\n");
            }

            outputStream.flush();
            outputStream.close();
            su.waitFor();
            su.destroy();
        }catch(IOException e){
            Log.v("IOException", e.getMessage());
        }catch(InterruptedException e){
            Log.v("InterruptedException", e.getMessage());
        }
        return String.valueOf(strings);
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i("onPostExecute", result);
        super.onPostExecute(result);
    }
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
