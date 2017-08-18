package com.workdawn.androiddynamicplugin;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.workdawn.simpledynamicplugin.PluginManager;


/**
 * 测试代码
 * Created by Administrator
 */
public class MainActivity extends Activity {

    Button button, button1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.test);
        button1 = (Button) findViewById(R.id.test1);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    PluginManager.getPluginManager().loadPlugin(Environment.getExternalStorageDirectory() + "/test_plugin.apk");
                    Fragment fragment = PluginManager.getPluginManager().loadPluginFragment("com.workdrawn.actvaluetransfer","com.workdrawn.actvaluetransfer.PluginFragment");
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                    //PluginManager.getPluginManager().loadPlugin(Environment.getExternalStorageDirectory().getAbsolutePath());
                    //PluginManager.getPluginManager().startActivity(MainActivity.this, new WrapperIntent("com.workdrawn.actvaluetransfer" , "com.workdrawn.actvaluetransfer.MainActivity"));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    PluginManager.getPluginManager().loadPlugin(Environment.getExternalStorageDirectory() + "/test_plugin.apk");
                    PluginManager.getPluginManager().startDefaultActivity(MainActivity.this);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
