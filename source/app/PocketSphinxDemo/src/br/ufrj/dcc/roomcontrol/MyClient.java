package br.ufrj.dcc.roomcontrol;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class MyClient extends AsyncTask<String, Void, Void> {
    private final HttpClient client = new DefaultHttpClient();
    HttpResponse response = null;
    private String Error = null;
    private ProgressDialog Dialog;// = new ProgressDialog(MainActivity.this);
    private Context mainActivity;
    
    public MyClient(Context mainActivity) {
		super();
		this.mainActivity = mainActivity;
		Dialog = new ProgressDialog(mainActivity);
	}

	protected void onPreExecute() {
        Dialog.setMessage("Iniciando..");
        Dialog.show();
    }

    protected Void doInBackground(String... urls) {
        try {
            HttpGet request = new HttpGet("http://"+urls[0]);
            response = client.execute(request);
        } catch (ClientProtocolException e) {
            Error = e.getMessage();
            cancel(true);
        } catch (IOException e) {
            Error = e.getMessage();
            cancel(true);
        } catch (Exception e){
        	Error = e.getMessage();
        }
        
        return null;
    }
    
    protected void onPostExecute(Void unused) {
        Dialog.dismiss();
        if (Error != null) {
            Toast.makeText(mainActivity, Error, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mainActivity, HttpHelper.request(response), Toast.LENGTH_LONG).show();
        }
    }
    
}