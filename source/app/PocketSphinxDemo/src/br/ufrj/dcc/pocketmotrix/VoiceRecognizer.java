package br.ufrj.dcc.pocketmotrix;

import java.util.concurrent.LinkedBlockingQueue;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import edu.cmu.pocketsphinx.Config;
import edu.cmu.pocketsphinx.Decoder;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.SegmentIterator;
import edu.cmu.pocketsphinx.pocketsphinx;
import edu.cmu.pocketsphinx.demo.PocketSphinxDemo;
import edu.cmu.pocketsphinx.demo.RecognitionListener;

public class VoiceRecognizer implements Runnable {

	private boolean done = false;
	/**
	 * PocketSphinx native decoder object.
	 */
	Decoder ps;
	/**
	 * Audio recording task.
	 */
	AudioTask audio;
	/**
	 * Thread associated with recording task.
	 */
	Thread audio_thread;
	/**
	 * Queue of audio buffers.
	 */
	LinkedBlockingQueue<short[]> audioq;
	/**
	 * Listener for recognition results.
	 */
	RecognitionListener rl;
	/**
	 * Whether to report partial results.
	 */
	boolean use_partials;

	SegmentIterator si;

	public RecognitionListener getRecognitionListener() {
		return rl;
	}

	public void setRecognitionListener(RecognitionListener rl) {
		this.rl = rl;
	}

	public void setUsePartials(boolean use_partials) {
		this.use_partials = use_partials;
	}

	public boolean getUsePartials() {
		return this.use_partials;
	}

	public VoiceRecognizer() {
		pocketsphinx.setLogfile("/sdcard/models/pocketsphinx.log");
		Config c = new Config();
		/*
		 * In 2.2 and above we can use getExternalFilesDir() or whatever it's
		 * called
		 */
		c.setString("-hmm", "/sdcard/models/hmm/en_US/hub4wsj_sc_8k");
		c.setString("-dict", "/sdcard/models/lm/en_US/hub4.5000.dic");
		c.setString("-lm", "/sdcard/models/lm/en_US/hub4.5000.DMP");

		c.setString("-rawlogdir", "/sdcard/models");
		c.setFloat("-samprate", 8000.0);
		c.setInt("-maxhmmpf", 2000);
		c.setInt("-maxwpf", 10);
		c.setInt("-pl_window", 2);
		c.setBoolean("-backtrace", true);
		c.setBoolean("-bestpath", false);
		this.ps = new Decoder(c);
		this.audio = null;
		this.audioq = new LinkedBlockingQueue<short[]>();
		this.use_partials = false;

	}

	private synchronized void processBuffer() throws InterruptedException {
		short[] buf = this.audioq.take();
		//if((buf = this.audioq.poll()) != null){
			Log.d(getClass().getName(), "Reading " + buf.length + " samples from queue");
			this.ps.processRaw(buf, buf.length, false, false);
			si = new SegmentIterator(ps);
			boolean wasWordIdentified = false, wasSilIdentified = false;
			String word = new String();
			String text = new String();
			while(!done){
				word = si.getWord();
				if(word.charAt(0) != '<' && word.charAt(0) != '+'){
					wasWordIdentified = true;
					wasSilIdentified = true;
				}
				if (wasWordIdentified && wasSilIdentified){
					ps.endUtt();
					text += word; 
					Log.d(getClass().getName(), "Parcial hypothesis: " + word);
					ps.startUtt();					
				}
				if((si = si.getNext())!=null)
					continue;
				
				break;
			}
			if (wasWordIdentified && wasSilIdentified){
				if (this.rl != null) {
					Bundle b = new Bundle();
					Log.d(getClass().getName(), "Final hypothesis: " + text);
					b.putString("hyp", text);
					this.rl.onResults(b);
				}
			}
		//}
	}

	private void stopBuffering() {
		Log.d(getClass().getName(), "STOP BUFFERING");
		assert this.audio != null;
		this.audio.stop();
		try {
			this.audio_thread.join();
		} catch (InterruptedException e) {
			Log.e(getClass().getName(),"Interrupted waiting for audio thread, shutting down");
			done = true;
		}
	}

	private void startBuffering() {
		Log.d(getClass().getName(), "START BUFFERING");
		this.ps.startUtt();
		this.audio_thread.start();
	}

	@Override
	public void run() {
		this.audio = new AudioTask(this.audioq, 1024);
		this.audio_thread = new Thread(this.audio);

		synchronized (this.audioq) {
			startBuffering();
			while (!done) {
				try {
					if (!this.audioq.isEmpty()) {
						processBuffer();
						Thread.sleep(1);	
					}
				} catch (InterruptedException e) {
					Log.d(getClass().getName(), e.getMessage());
					stopBuffering();
				}
			}
			Log.d(getClass().getName(), "SHUTDOWN");
			if (this.audio != null) {
				this.audio.stop();
				assert this.audio_thread != null;
				try {
					this.audio_thread.join();
				}
				catch (InterruptedException e) {
					Log.d(getClass().getName(), e.getMessage());
				}
			}
			this.audio = null;
			this.audio_thread = null;
		}
	}

	public synchronized void stop() {
		stopBuffering();
	}

	public RecognitionListener getRl() {
		return rl;
	}

	public void setRl(RecognitionListener rl) {
		this.rl = rl;
	}

	public boolean isUse_partials() {
		return use_partials;
	}

	public void setUse_partials(boolean use_partials) {
		this.use_partials = use_partials;
	}
}
