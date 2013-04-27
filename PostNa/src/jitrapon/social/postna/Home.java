package jitrapon.social.postna;

import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class Home extends Activity 
{

	private static int RESULT_LOAD_IMAGE = 1;
	private static ConnectionManager cManager;


	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		Button buttonLoadImage = (Button) findViewById(R.id.app_gallery_btn);
		buttonLoadImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// in onCreate or any event where your want the user to
				// select a file
				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
		});
		cManager = new ConnectionManager();
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo(); 
		if (info != null) {
			// There exists a connection
			cManager.isConnected = info.isConnected();
			displayDialogBox("Alert", "You are connected using " + info.getTypeName());

		} else {
			displayDialogBox("Alert", "You are not connected to the internet!");
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();


			//  Set the preview image to be the one chosen from gallery
			ImageView imageView = (ImageView) findViewById(R.id.app_image);
			//imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
			imageView.setImageBitmap(
					ImageUtils.decodeSampledBitmapFromFile(picturePath, 100, 100));

			// Send broadcast to widget to update its imageview object
			updateImageWidget(picturePath);

			// Attempt to connect to the server to trigger PHOTO_CHANGE event
			if (cManager.isConnected) {
				try {
					String result = new ConnectionManager().execute(picturePath).get();
					displayDialogBox("Alert", result);
				} catch (InterruptedException e) {
					displayDialogBox("Alert", "Server Offline");
				} catch (ExecutionException e) {
					displayDialogBox("Alert", "Server Offline");
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	/**
	 * Receives broadcast event intents from running service
	 */
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction() == "jitrapon.social.intent.action.CHANGE_PICTURE") {
				displayDialogBox("Alert", "Received something from server"); 
				updateImageWidget(intent.getStringExtra("picturePath"));
			}	
		}
	};

	/**
	 * Dialog Box
	 */
	public void displayDialogBox(String title, String msg) 
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder
		.setTitle(title)
		.setMessage(msg)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setPositiveButton("Ok", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which) 
			{       
				//do some thing here which you need
			}
		});             
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which) 
			{   
				dialog.dismiss();           
			}
		});         
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Sends broadcast with the intent to update the current widget 
	 * with the new chosen image
	 * 
	 * @param srcPath The path of the original chosen image
	 */
	public void updateImageWidget(String srcPath) 
	{
		/*
		 *  Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
		 *  since it seems the onUpdate() is only fired on that:
		 */ 
		Intent intent = new Intent(this, WidgetProvider.class);
		intent.setAction("jitrapon.social.intent.action.CHANGE_PICTURE");
		int[] ids = AppWidgetManager.getInstance(getApplication()).
				getAppWidgetIds(new ComponentName(getApplication(), WidgetProvider.class));
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
		intent.putExtra("picturePath", srcPath);
		sendBroadcast(intent);
	}
}
