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
import java.net.UnknownHostException;

import android.os.AsyncTask;

public class ConnectionManager extends AsyncTask<String, Void, String>
{
	public boolean isConnected;
	private Socket socket;
	private PrintWriter out;
    private BufferedReader in;
    private final String serverIP = "192.168.1.118";
    private final int serverPort = 3654;

	/**
	 * Default ctor
	 */
	public ConnectionManager() 
	{
		isConnected = false;
	}

	@Override
	protected String doInBackground(String... params) {
		try {
//			socket = new Socket(serverIP, serverPort);
			socket = new Socket();
			socket.connect(new InetSocketAddress(serverIP, serverPort), 3500);
			//send the message to the server
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            sendMessage(params[0]);
			socket.close();
		} catch (UnknownHostException e) {
			return "Server Offline";
		} catch (IOException e) {
			return "Server Offline";
		}
		return "Uploaded";
	}
	
	/**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    public void sendMessage(String message){
        if (out != null && !out.checkError()) {
            out.println(message);
            out.flush();
        }
    }

	@Override
	protected void onPostExecute(String result) {

	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected void onProgressUpdate(Void... values) {
	}
}
