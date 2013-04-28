package jitrapon.social.postna;

/**
 * This class implements a started service to handle TCP connection with the 
 * server. This is both a started service and a bound service that the 
 * client application can interact with
 * 
 * The service waits for the server with socket.getInputStream() and
 * once data arrives, the server broadcasts the message to the main activity
 * The main activity will implement broadcastreceiver
 * 
 * The main activity connects to the server using bindService
 * 
 * @author Jitrapon Tiachunpun
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class MediaService extends Service
{
	public boolean mConnected;
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	private final String serverIP = "192.168.1.118";
	private final int serverPort = 3654;
	private IBinder mBinder;	

	private Thread uThread;

	@Override
	public IBinder onBind(Intent intent) 
	{
		// A client is binding to the service with bindService()
		Toast.makeText(this,"Service binded", Toast.LENGTH_LONG).show();
		return mBinder;
	}

	public class LocalBinder extends Binder {
		public MediaService getService() {
			return MediaService.this;
		}
	}

	@Override
	public void onCreate() 
	{
		super.onCreate();
		socket = new Socket();
		mBinder = new LocalBinder();
	}

	public void IsBoundable(){
		Toast.makeText(this,"I bind like butter", Toast.LENGTH_LONG).show();
	}

	// This is the old onStart method that will be called on the pre-2.0
	// platform.  On 2.0 or later we override onStartCommand() so this
	// method will not be called.
	@Override
	public void onStart(Intent intent, int startId) {
		//handleCommand(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// The service is starting, due to a call to startService()
		handleCommand(intent);
		super.onStartCommand(intent, flags, startId);
		Toast.makeText(this,"Service created", Toast.LENGTH_LONG).show();


		Runnable uploadSocket = new uploadSocket("test");
		uThread = new Thread(uploadSocket);
		uThread.start();

		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	/**
	 * TODO:
	 * @param intent
	 */
	private void handleCommand(Intent intent) {

	}

	class uploadSocket implements Runnable 
	{
		private String msg = null;

		public uploadSocket(String param) {
			msg = param;
		}

		@Override
		public void run() {
			try {
				socket.connect(new InetSocketAddress(serverIP, serverPort), 3500);
				out = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream())), true);
				mConnected = true;
			} catch (IOException e) {
				mConnected = false;
			}
		}
	}

	/**
	 * Sends the message entered by client to the server
	 * @param message text entered by client
	 */
	public String sendMessage(String message)
	{
		if (out != null) {
			//send the message to the server
			out.println(message);
			out.flush();
			if (out.checkError()) {
				reconnect();
				return "Could not establish connection with the server";
			}
		}
		return "Photo uploaded to the server";
	}
	
	public void reconnect() {
		try {
			socket.close();
			socket = new Socket();
			Runnable uploadSocket = new uploadSocket("test");
			uThread = new Thread(uploadSocket);
			uThread.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		socket = null;
	}
}
