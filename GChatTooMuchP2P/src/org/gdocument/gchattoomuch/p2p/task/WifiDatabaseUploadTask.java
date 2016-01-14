package org.gdocument.gchattoomuch.p2p.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.gdocument.gchattoomuch.p2p.common.P2PConstant;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class WifiDatabaseUploadTask extends AsyncTask<Void, Void, Void> {

	private String TAG = WifiDatabaseUploadTask.class.getName();
	private Context context;

	public WifiDatabaseUploadTask(Context context) {
		this.context = context;
	}

	@Override
	protected Void doInBackground(Void... params) {
		String filename = Environment.getExternalStorageDirectory() + "/db" + System.currentTimeMillis() + ".piz";
		createZip(filename);
		uploadFile(filename);
		deleteFile(filename);
		return null;
	}

	/** * Start activity that can handle the JPEG image */
	@Override
	protected void onPostExecute(Void result) {
	}

	private void uploadFile(String filename) {
		String host = P2PConstant.P2P_CLIENT_HOST;
		int port  = P2PConstant.P2P_CLIENT_PORT;
		Socket socket = new Socket();
		try {
		    /**
		     * Create a client socket with the host,
		     * port, and timeout information.
		     */
		    socket.bind(null);
		    socket.connect((new InetSocketAddress(host, port)), P2PConstant.P2P_CLIENT_TIMEOUT);

		    /**
		     * Create a byte stream from a JPEG file and pipe it to the output stream
		     * of the socket. This data will be retrieved by the server device.
		     */
		    OutputStream outputStream = socket.getOutputStream();
		    ContentResolver cr = context.getContentResolver();
		    InputStream inputStream = cr.openInputStream(Uri.parse(filename));
		    copyFile(inputStream, outputStream);
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		} catch(RuntimeException e) {
			Log.e(TAG, e.getMessage());
		}

		/**
		 * Clean up any open sockets when done
		 * transferring or if an exception occurred.
		 */
		finally {
		    if (socket != null) {
		        if (socket.isConnected()) {
		            try {
		                socket.close();
		            } catch (IOException e) {
		    			Log.e(TAG, e.getMessage());
		            }
		        }
		    }
		}
	}

	private void createZip(String filename) {
		   // These are the files to include in the ZIP file 
	    String[] source = new String[]{"source1", "source2"}; 
	 
	    // Create a buffer for reading the files 
	    byte[] buf = new byte[1024]; 
	 
	    try { 
			// Create the ZIP file 
	        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(new File(filename))); 
	 
	        // Compress the files 
	        for (int i=0; i<source.length; i++) { 
	            FileInputStream in = new FileInputStream(source[i]); 
	 
	            // Add ZIP entry to output stream. 
	            out.putNextEntry(new ZipEntry(source[i])); 
	 
	            // Transfer bytes from the file to the ZIP file 
	            int len; 
	            while ((len = in.read(buf)) > 0) { 
	                out.write(buf, 0, len); 
	            } 
	 
	            // Complete the entry 
	            out.closeEntry(); 
	            in.close(); 
	        } 
	 
	        // Complete the ZIP file 
	        out.close(); 
	    } catch (IOException e) { 
			Log.e(TAG, e.getMessage());
	    } catch (RuntimeException e) {
			Log.e(TAG, e.getMessage());
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

	private void deleteFile(String filename) {
		try {
			new File(filename).delete();
	    } catch (RuntimeException e) {
			Log.e(TAG, e.getMessage());
	    }
	}
}