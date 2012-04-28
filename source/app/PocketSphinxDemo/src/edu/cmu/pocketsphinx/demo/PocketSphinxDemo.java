package edu.cmu.pocketsphinx.demo;

import java.util.Date;

import br.ufrj.dcc.pocketmotrix.VoiceRecognizer;
import br.ufrj.dcc.roomcontrol.MyClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class PocketSphinxDemo extends Activity implements RecognitionListener{
	static {
		System.loadLibrary("pocketsphinx_jni");
	}
	/**
	 * Recognizer task, which runs in a worker thread.
	 */
	//RecognizerTask rec;
	private VoiceRecognizer rec;
	/**
	 * Thread in which the recognizer task runs.
	 */
	private Thread rec_thread;
	/**
	 * Time at which current recognition started.
	 */
	private Date start_date;
	/**
	 * Number of seconds of speech.
	 */
	private float speech_dur;
	/**
	 * Are we listening?
	 */
	private boolean listening;
	/**
	 * Progress dialog for final recognition.
	 */
	private ProgressDialog rec_dialog;
	
	private TextView performance_text;
	
	private EditText edit_text;
	
	private String gateWayURL = "";
	private EditText urlInput;
	private Button btnURL;
	private Button btnOnOff;
	private Button btnChUp;
	private Button btnChDown;
	private Button btnVolUp;
	private Button btnVolDown;
	private boolean ready = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		this.rec = new VoiceRecognizer();
		this.listening = false;
		urlInput = (EditText)findViewById(R.id.url_input);
        btnURL = (Button)findViewById(R.id.url_button);
        btnOnOff = (Button)findViewById(R.id.on_off_button);
        btnChUp = (Button)findViewById(R.id.ch_up_button);
        btnChDown = (Button)findViewById(R.id.ch_down_button);
        btnVolUp = (Button)findViewById(R.id.vol_up_button);
        btnVolDown = (Button)findViewById(R.id.vol_down_button);
        edit_text = (EditText) findViewById(R.id.EditText01);
        performance_text = (TextView) findViewById(R.id.PerformanceText);
        
	}
	
	@Override
	public void onStart(){
		super.onStart();
		btnURL.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
            	gateWayURL = urlInput.getText().toString();
            	ready = true;
            	Toast.makeText(PocketSphinxDemo.this, "http://"+gateWayURL, Toast.LENGTH_LONG).show();
            }
        });
		btnOnOff.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
            	if(ready)
            		getRequest(gateWayURL+"/OnOff");
            	else
            		Toast.makeText(PocketSphinxDemo.this, "Defina o endereço do GateWay.", Toast.LENGTH_LONG).show();
            }
        });
        btnChUp.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
            	if(ready)
            		getRequest(gateWayURL+"/ChUp");
            	else
            		Toast.makeText(PocketSphinxDemo.this, "Defina o endereço do GateWay.", Toast.LENGTH_LONG).show();
            }
        });
        btnChDown.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
            	if(ready)
            		getRequest(gateWayURL+"/ChDown");
            	else
            		Toast.makeText(PocketSphinxDemo.this, "Defina o endereço do GateWay.", Toast.LENGTH_LONG).show();
            }
        });
        btnVolUp.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
            	if(ready)
            		getRequest(gateWayURL+"/VolUp");
            	else
            		Toast.makeText(PocketSphinxDemo.this, "Defina o endereço do GateWay.", Toast.LENGTH_LONG).show();
            }
        });
        btnVolDown.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
            	if(ready)
            		getRequest(gateWayURL+"/VolDown");
            	else
            		Toast.makeText(PocketSphinxDemo.this, "Defina o endereço do GateWay.", Toast.LENGTH_LONG).show();
            }
        });
		this.rec_thread = new Thread(this.rec);
    	this.rec.setRecognitionListener(this);
    	this.rec_thread.start();
	}

	/** Called when partial results are generated. */
	public void onPartialResults(Bundle b) {
		final PocketSphinxDemo that = this;
		final String hyp = b.getString("hyp");
		that.edit_text.post(new Runnable() {
			public void run() {
				that.edit_text.setText(hyp);
			}
		});
	}

	/** Called with full results are generated. */
	public void onResults(Bundle b) {
		final String hyp = b.getString("hyp");
		final PocketSphinxDemo that = this;
		this.edit_text.post(new Runnable() {
			public void run() {
				that.edit_text.setText(hyp);
//				Date end_date = new Date();
//				long nmsec = end_date.getTime() - that.start_date.getTime();
//				float rec_dur = (float)nmsec / 1000;
//				that.performance_text.setText(String.format("%.2f seconds %.2f xRT",
//															that.speech_dur,
//															rec_dur / that.speech_dur));
//				Log.d(getClass().getName(), "Hiding Dialog");
//				that.rec_dialog.dismiss();
			}
		});
	}

	public void onError(int err) {
		final PocketSphinxDemo that = this;
		that.edit_text.post(new Runnable() {
			public void run() {
				that.rec_dialog.dismiss();
			}
		});
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		this.rec.stop();
//		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	public void getRequest(String url){
		new MyClient(PocketSphinxDemo.this).execute(url);
	}
}