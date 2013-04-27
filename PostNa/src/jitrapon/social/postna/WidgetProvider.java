package jitrapon.social.postna;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.default_appwidget);
		//		remoteViews.setOnClickPendingIntent(R.id.app_gallery_btn, buildButtonPendingIntent(context));

		// When we click the widget, we want to open our main activity.
		Intent launchActivity = new Intent(context, Home.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchActivity, 0);
		remoteViews.setOnClickPendingIntent(R.id.widget_image, pendingIntent);;
		pushWidgetUpdate(context, remoteViews);

	}

	@Override
	/**
	 * Upon receiving broadcast from Activity
	 */
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		if (intent.getAction().equals("jitrapon.social.intent.action.CHANGE_PICTURE")) {
//			updateWidgetPictureAndButtonListener(context);
			
			RemoteViews remoteViews = new RemoteViews( context.getPackageName(), R.layout.default_appwidget );
			//remoteViews.setImageViewResource(R.id.widget_image, R.id.app_image);
			String picturePath = intent.getStringExtra("picturePath");
			remoteViews.setImageViewBitmap(R.id.widget_image, 
					ImageUtils.decodeSampledBitmapFromFile(picturePath, 200, 200));
//			remoteViews.setImageViewBitmap(R.id.widget_image, BitmapFactory.decodeFile(picturePath));
			remoteViews.setTextViewText(R.id.userIDTextView, picturePath);

			WidgetProvider.pushWidgetUpdate(context.getApplicationContext(), remoteViews);
		}
		
	}


public static PendingIntent buildButtonPendingIntent(Context context) {
	Intent intent = new Intent();
	intent.setAction("jitrapon.social.intent.action.CHANGE_PICTURE");
	return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
}

public static void pushWidgetUpdate(Context context, RemoteViews remoteViews) {
	ComponentName myWidget = new ComponentName(context, WidgetProvider.class);
	AppWidgetManager manager = AppWidgetManager.getInstance(context);
	manager.updateAppWidget(myWidget, remoteViews);		
}
}

