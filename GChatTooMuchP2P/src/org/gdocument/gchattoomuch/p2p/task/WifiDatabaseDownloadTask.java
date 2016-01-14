package org.gdocument.gchattoomuch.p2p.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.gdocument.gchattoomuch.p2p.common.P2PConstant;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class WifiDatabaseDownloadTask extends AsyncTask<Void, Void, String> {
	
	private String TAG = WifiDatabaseDownloadTask.class.getName();

	private Context context;
	private TextView statusText;
	private ServerSocket serverSocket = null;

	public WifiDatabaseDownloadTask(Context context, View statusText) {
		this.context = context;
		this.statusText = (TextView) statusText;
	}

	@Override
	protected String doInBackground(Void... params) {
		try {
			/**
			 * * Create a server socket and wait for client connections. This *
			 * call blocks until a connection is accepted from a client
			 */
			serverSocket = new ServerSocket(P2PConstant.P2P_SERVER_PORT);
			serverSocket.setSoTimeout(P2PConstant.P2P_SERVER_TIMEOUT);
			Socket client = serverSocket.accept();
			/**
			 * * If this code is reached, a client has connected and transferred
			 * data * Save the input stream from the client as a JPEG file
			 */
			final File f = new File(Environment.getExternalStorageDirectory() + "/" + context.getPackageName()
					+ "/database-" + System.currentTimeMillis() + ".zip");
			File dirs = new File(f.getParent());
			if (!dirs.exists()) {
				dirs.mkdirs();
			}
			f.createNewFile();
			InputStream inputstream = client.getInputStream();
			copyFile(inputstream, new FileOutputStream(f));
			return f.getAbsolutePath();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		} catch(RuntimeException e) {
			Log.e(TAG, e.getMessage());
		}
		return null;
	}

	/** * Start activity that can handle the JPEG image */
	@Override
	protected void onPostExecute(String result) {
		if (result != null && statusText != null) {
			statusText.setText("File copied - " + result);
		}
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			} catch(RuntimeException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		try {
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
		} finally {
			out.close();
			in.close();
		}
	}
}