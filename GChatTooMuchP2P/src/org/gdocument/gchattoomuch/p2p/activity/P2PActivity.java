package org.gdocument.gchattoomuch.p2p.activity;

import org.gdocument.gchattoomuch.p2p.R;
import org.gdocument.gchattoomuch.p2p.task.WifiDatabaseDownloadTask;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;

public class P2PActivity extends Activity {

	private AsyncTask<Void, Void, String> uploadTask = null;
	private Button btnUpload;

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.p2p);
		btnUpload = (Button)findViewById(R.id.btn_upload);
	};

	public void onClickClientUpload(View view) {
		new WifiDatabaseDownloadTask(this, null).execute();
	}

	public void onClickServerDownload(View view) {
		if (uploadTask == null) {
			uploadTask = new WifiDatabaseDownloadTask(this, null) {
				protected void onPreExecute() {
					super.onPreExecute();
					btnUpload.setText(getString(R.string.btn_text_server_download_stop));
				};
				protected void onPostExecute(String result) {
					btnUpload.setText(getString(R.string.btn_text_server_download));
					uploadTask = null;
					super.onPostExecute(result);
				};
			}.execute();
		} else {
			btnUpload.setText(getString(R.string.btn_text_server_download));
			uploadTask.cancel(true);
			uploadTask = null;
		}
	}
}
