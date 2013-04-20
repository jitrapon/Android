package jitrapon.social.postna;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class Home extends Activity {

	private static int RESULT_LOAD_IMAGE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

			ImageView imageView = (ImageView) findViewById(R.id.app_image);
			//imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
			imageView.setImageBitmap(
				    ImageUtils.decodeSampledBitmapFromFile(picturePath, 100, 100));
			
			// Send broadcast to widget to update its imageview
			Intent intent = new Intent(this, WidgetProvider.class);
			intent.setAction("jitrapon.social.intent.action.CHANGE_PICTURE");
			// Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
			// since it seems the onUpdate() is only fired on that:
			int[] ids = AppWidgetManager.getInstance(getApplication()).
					getAppWidgetIds(new ComponentName(getApplication(), WidgetProvider.class));
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
			intent.putExtra("picturePath", picturePath);
			sendBroadcast(intent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

}
