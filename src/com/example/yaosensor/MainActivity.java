package com.example.yaosensor;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


import android.R.style;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Resources.Theme;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.ContactsContract.Contacts.Data;
import android.support.v4.view.ViewPager;
import android.text.AndroidCharacter;
import android.text.Layout;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class MainActivity extends Activity implements View.OnClickListener,SensorEventListener{

	private Button button1,button2;
	private MediaRecorder mRecorder;
	private TextView textView;
	private boolean isRecorder = false;
	private boolean isStop = false;
	private Sensor sensor, osensor;
	private SensorManager smManager;
	private WakeLock wL;
	private File file;
 	
	@TargetApi(11) @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.biaotilan);
        PowerManager pM = (PowerManager)getSystemService(Context.POWER_SERVICE);
		wL = pM.newWakeLock(PowerManager.FULL_WAKE_LOCK, "forver");
		wL.acquire();
        button1 = (Button)findViewById(R.id.bt1);
        button2 = (Button)findViewById(R.id.bt2);
        textView = (TextView)findViewById(R.id.tv);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        
        
        smManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = smManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        //MenuInflater blowUp = getMenuInflater(); //Menu充气机
		//blowUp.inflate(R.menu.cool_menu, menu);
        //menu.add(Menu.NONE, Menu.NONE, 1, "菜单1");  

        //menu.add(Menu.NONE, Menu.NONE, 2, "菜单2");  

        //menu.add(Menu.NONE, Menu.NONE, 3, "菜单3");  

        //menu.add(Menu.NONE, Menu.NONE, 4, "菜单4");  

       //menu.add(Menu.NONE, Menu.NONE, 5, "菜单5");  

       //menu.add(Menu.NONE, Menu.NONE, 6, "菜单6");

        return true;
    }
    
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		switch(item.getItemId())
		{
		case R.id.menu_introduce:
			View myIntroduceView = layoutInflater.inflate(R.layout.introduce, null);
			Dialog dialog = new AlertDialog.Builder(this).
							  setIcon(R.drawable.record).
							  setTitle("简介").
							  setView(myIntroduceView).
							  setNeutralButton("关闭", new DialogInterface.OnClickListener() {
								
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							}).create();
			dialog.show();
			break;
		case R.id.menu_setting:
			CharSequence[] items ={"路径","发送","时长","显示"};
			final boolean[] checkedItems =new boolean[]{false,false,false,false};
			Dialog dialog0 = new AlertDialog.Builder(this).
			                   setIcon(R.drawable.record).
			                   setTitle("简介").
			                   setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
								
								public void onClick(DialogInterface dialog, int which, boolean isChecked) {
									// TODO Auto-generated method stub
									checkedItems[which] = isChecked;
									switch(which)
									{
									case 0:
										Toast toast = Toast.makeText(getBaseContext(), "文件存储在"+Environment.getExternalStorageDirectory().getPath(), Toast.LENGTH_LONG);
										toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
										toast.show();
										break;
									case 1:
										break;
									case 2:
										break;
									case 3:
										break;
									}
								}
							}).
							setPositiveButton("确定", new DialogInterface.OnClickListener() {
								
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									
								}
							}).
							setNegativeButton("取消", new DialogInterface.OnClickListener() {
								
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							}).create();
			dialog0.show();
			break;
		case R.id.menu_exit:
			Dialog dialog1 = new AlertDialog.Builder(this).
			                    setIcon(R.drawable.record).
			                    setTitle("提示").
			                    setMessage("您确定要退出吗？").
			                    setPositiveButton("确定", new DialogInterface.OnClickListener() {
									
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
												wL.release();
												finish();
									}
								}).
								setNegativeButton("取消", new DialogInterface.OnClickListener() {
									
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										dialog.dismiss();
									}
								}).
			                    create();
			dialog1.show();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.bt1:
				if(!isRecorder)
				{
					startRecorder();
				}
				break;
			case R.id.bt2:
				if(!isStop&&isRecorder)
				{
					isStop=true;
					mRecorder.stop();
					button1.setText("结束");
					button1.setTextColor(Color.RED);
					//button1.setTextColor(Color.argb(100, 250, 0, 250));
					//button2.setVisibility(View.INVISIBLE);
				}
				break;
		}
	}

	private void startRecorder() {
		// TODO Auto-generated method stub
		isRecorder = true;
		mRecorder = new MediaRecorder();
		file = new File(Environment.getExternalStorageDirectory().getPath()+File.separator+"YY"+new DateFormat().format("yyyy-MM-dd hh:mm:ss", Calendar.getInstance(Locale.CHINA))+".mp3");
		Toast.makeText(getApplicationContext(), "正在录音，录音文件在"+file.getAbsolutePath(), Toast.LENGTH_LONG).show();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		mRecorder.setOutputFile(file.getAbsolutePath());
		try {
			file.createNewFile();
			mRecorder.prepare();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			mRecorder.start();
			button1.setTextColor(Color.RED);
			//button2.setVisibility(View.VISIBLE);
		}
		
	}

	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		int a = event.accuracy;
		long b = event.timestamp;
		Sensor s =event.sensor;
		float[] f = new float[3];
		float[] g = new float[3];
		f[0]=event.values[0];
		f[1]=event.values[1];
		f[2]=event.values[2];
		Date date = new Date();
		long c = date.getTime();
		CharSequence d = DateFormat.format("yyyy-MM-dd hh:mm:ss", c);
		textView.setText("精度为："+a+'\n'
				+"时间戳为："+b+"纳秒"+'\n'
				+"当次时间为："+d+'\n'
				+"传感器类型为："+s.getName()+'\n'
				+"x="+f[0]+",y="+f[1]+",z="+f[2]);
		if(!isRecorder&&Math.abs(f[0])>5f &&Math.abs(f[1])>5f)
		{
			startRecorder();
		}

	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		smManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
		smManager.registerListener(this, osensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		smManager.unregisterListener(this);
		if(!isStop&&isRecorder)
		{
			isStop=true;
			mRecorder.stop();
			button1.setText("结束");
			button1.setTextColor(Color.RED);
			//button1.setTextColor(Color.argb(100, 250, 0, 250));
			//button2.setVisibility(View.INVISIBLE);
		}
		wL.release();
		finish();
	}
}
