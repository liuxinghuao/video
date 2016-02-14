package com.android;

import java.io.File;
import java.io.IOException;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.PixelFormat;
import android.hardware.Camera;

public class video extends Activity implements SurfaceHolder.Callback 
{
	private String strTempFile = "User1_";
	private File myRecAudioFile;
	private File myRecAudioDir;
	private Camera mCamera01;
	private MediaRecorder recorder;
	private SurfaceView surfaceView1 ;
	private SurfaceHolder surfaceHolder;
	private boolean isStopRecord;
	private boolean sdCardExit;
	private boolean recording = false;
	private Button btn01,btn02,btn03,btn04;
	private TextView tv01,tv02,tv03;
	//�趨¼�Ƶ�ʱ��
	private int minute = 5;
	private int second = 0;
	private boolean bool;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(null);
		//ȫ����ʾ
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//������ʾ
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.main);
		
		btn01 = (Button) findViewById(R.id.Button01);
		btn02 = (Button) findViewById(R.id.Button02);
		btn03 = (Button) findViewById(R.id.Button03);
		btn04 = (Button) findViewById(R.id.Button04);
		
		tv01 = (TextView) findViewById(R.id.TextView01);
		tv02 = (TextView) findViewById(R.id.TextView02);
		tv03 = (TextView) findViewById(R.id.TextView03);
		
		//����TextView���ɼ�
		tv01.setVisibility(View.GONE);
		tv02.setVisibility(View.GONE);
		tv03.setVisibility(View.GONE);
		
		// �ж�SD Card�Ƿ���� 
		sdCardExit = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		// ���sd������,ȡ��SD Card·����Ϊ¼�����ļ�λ��
		if(sdCardExit){
			myRecAudioDir = Environment.getExternalStorageDirectory();
		}else {
			mMakeTextToast("�洢��������",true);
		}
		surfaceView1 = (SurfaceView) findViewById(R.id.SurfaceView);
        //��surfaceView��ȡ��surfaceHolder����
		surfaceHolder = surfaceView1.getHolder();
        //activity����ʵ��surfaceHolder.Callback()
		surfaceHolder.addCallback(video.this);
        //������ʾ������
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		//Ԥ��
		btn01.setOnClickListener(mBtn01);
		//��ʼ¼��
		btn02.setOnClickListener(mBtn02);
		//ֹͣ¼��
		btn03.setOnClickListener(mBtn03);
		//����
		btn04.setOnClickListener(mBtn04);
	}
	
	private Button.OnClickListener mBtn01 = new Button.OnClickListener(){
		@Override
		public void onClick(View v) {
			//��ʼ������ͷ
			initCamera();
		}
	};
	
	private Button.OnClickListener mBtn02 = new Button.OnClickListener(){
		@Override
		public void onClick(View v) {
			//¼����Ƶ
			recorder();
		}
	};
	
	private Button.OnClickListener mBtn03 = new Button.OnClickListener(){
		@Override
		public void onClick(View v) {
			//ֹͣ¼��
			stop();
		}
	};
	
	private Button.OnClickListener mBtn04 = new Button.OnClickListener(){
		@Override
		public void onClick(View v) {
			//����
			back();
		}
	};
	
	public void initCamera(){
    	if(!recording){
    		mCamera01 = Camera.open();
    	}
    	if(mCamera01 != null && !recording){
    		try {
    			//����Camera.Parameter����
    			Camera.Parameters parameters = mCamera01.getParameters();
    			//����Preview�ĳߴ�
    			parameters.setPreviewSize(640,480);
    			//�����������parameters
    			mCamera01.setParameters(parameters);
				mCamera01.setPreviewDisplay(surfaceHolder);
				//��������Preview
				mCamera01.startPreview();
				recording = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
	
	//��ʼ¼��
	public void recorder(){
		try {
			bool = true;
			myRecAudioFile = File.createTempFile(strTempFile,".mpg",myRecAudioDir);
			recorder = new MediaRecorder(); 
			recorder.setPreviewDisplay(surfaceHolder.getSurface());
			recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA); 
			//����¼��ԴΪ��˷�
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			//���������ʽΪ3gp
			recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);  
			//������Ƶ��С
			recorder.setVideoSize(176,144);   
			//ÿ���֡��
			recorder.setVideoFrameRate(15);   
			//������Ƶ����
			recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H263); 
			//������Ƶ����
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);          
			recorder.setOutputFile(myRecAudioFile.getAbsolutePath());
			recorder.prepare();
			tv01.setVisibility(View.VISIBLE);
			tv02.setVisibility(View.VISIBLE);
			tv03.setVisibility(View.VISIBLE);
			tv01.setText(format(minute));
			tv03.setText(format(second));
			handler.postDelayed(task, 1000);
			recorder.start();
			recording = true;
			isStopRecord = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//ֹͣ¼��
	public void stop(){
		if (recorder == null){
			return;
		}
		recorder.stop();
		bool = false;
		tv01.setText(format(minute));
		tv03.setText(format(second));
		recorder.release();
		recording = false;
		recorder = null;
		mMakeTextToast("ֹͣ��" + myRecAudioFile.getName(),true);
		// ֹͣ¼��
		isStopRecord = true;
	}
	
	//����
	public void back(){
		if (recorder != null && !isStopRecord){
  		  //ֹͣ¼�� 
  		  recorder.stop();
  		  recorder.release();
		}
  	  	finish();
	}
	
	//��ʱ�����ã�ʵ�ּ�ʱ
	private Handler handler = new Handler();   
    private Runnable task = new Runnable() {   
        public void run() {   
        	if(bool){
        		handler.postDelayed(this, 1000); 
        		second = minute * 60 + second;
        		second --;
        		if(second >= 60){
        			minute = second / 60;
        			second = second % 60;
        			tv01.setText(format(minute));
        			tv03.setText(format(second));
        		}else if(second >= 0){
        			minute = 0;
        			tv01.setText(format(minute));
        			tv03.setText(format(second));
        		}else{
        			stop();
        			tv01.setText(format(0));
        			tv03.setText(format(0));
        		}
        	}
        }   
    }; 
	
	private void mMakeTextToast(String string, boolean b) {
		if(b == true){
			Toast.makeText(video.this, string, Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(video.this, string, Toast.LENGTH_SHORT).show();
		}
	}
	
	//��ʽ��ʱ��
	public String format(int i){
		String s = i + "";
		if(s.length() == 1){
			s = "0" + s;
		}
		return s;
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
	        
	}
	public void surfaceCreated(SurfaceHolder holder) {
	        
	}
	public void surfaceDestroyed(SurfaceHolder holder) {
	        
	}
}