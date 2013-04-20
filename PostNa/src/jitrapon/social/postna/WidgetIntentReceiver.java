package jitrapon.social.postna;

import jitrapon.social.postna.R;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.widget.RemoteViews;

public class WidgetIntentReceiver extends BroadcastReceiver {

	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("jitrapon.social.intent.action.CHANGE_PICTURE")) {
//			updateWidgetPictureAndButtonListener(context);
			
			RemoteViews remoteViews = new RemoteViews( context.getPackageName(), R.layout.default_appwidget );
			String picturePath = intent.getStringExtra("imgPath");
			remoteViews.setImageViewBitmap(R.id.widget_image, BitmapFactory.decodeFile(picturePath));
			remoteViews.setTextViewText(R.id.userIDTextView, "Image Changed!");

			WidgetProvider.pushWidgetUpdate(context.getApplicationContext(), remoteViews);
		}
		
	}

	private void updateWidgetPictureAndButtonListener(Context context) {
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.default_appwidget);
		//remoteViews.setImageViewResource(R.id.widget_image, getImageToSet());
		
		//REMEMBER TO ALWAYS REFRESH YOUR BUTTON CLICK LISTENERS!!!
		remoteViews.setOnClickPendingIntent(R.id.app_gallery_btn, WidgetProvider.buildButtonPendingIntent(context));
		
		WidgetProvider.pushWidgetUpdate(context.getApplicationContext(), remoteViews);
	}

//	private int getImageToSet() {
//		clickCount++;
//		return clickCount % 2 == 0 ? R.drawable.me : R.drawable.wordpress_icon;
//	}
}

