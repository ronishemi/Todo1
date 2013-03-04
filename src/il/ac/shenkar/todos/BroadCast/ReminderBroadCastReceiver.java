package il.ac.shenkar.todos.BroadCast;

import il.ac.shenkar.todos.Todo;
import il.ac.shenkar.todos.R;

import il.ac.shenkar.todos.service.UrlService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

public class ReminderBroadCastReceiver extends BroadcastReceiver {
	public static final String COMMAND = "SENDER";

	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent data) {
		Intent myIntent;
		double latitude;
		double longitude;
		Integer uniqueId;
		PendingIntent pendingIntent;
		String key = LocationManager.KEY_PROXIMITY_ENTERING;
		Boolean entering = data.getBooleanExtra(key, false);
		if (entering) {
			Log.d(getClass().getSimpleName(), "entering");
		} else {
			Log.d(getClass().getSimpleName(), "exiting");

		}

		String service = data.getStringExtra("SERVICE");
		String proximity = data.getStringExtra("proximity");
		String message = data.getStringExtra("newMessage");
		uniqueId = data.getIntExtra("uniqueId", 0);
		latitude = data.getDoubleExtra("latitude", 0);
		longitude = data.getDoubleExtra("longitude", 0);
		System.out.println("uniqueId: " + uniqueId);
		ResultReceiver receiver;
		if (service == null) {

			if ((proximity == null) || (entering)) {
				myIntent = new Intent(context, Todo.class);

				Bundle mBundle = new Bundle();
				mBundle.putInt("uniqueId", uniqueId);
				myIntent.putExtras(mBundle);
				myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				pendingIntent = PendingIntent.getActivity(context, uniqueId,
						myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				NotificationManager notificationManager = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);

				Notification notification = new Notification(
						R.drawable.lightning, message,
						System.currentTimeMillis());
				notification.setLatestEventInfo(context, "MyReminder", message,
						pendingIntent);

				notification.defaults = Notification.DEFAULT_ALL;
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
				notificationManager.notify(uniqueId, notification);
			}
		} else if ((proximity == null) && (service != null)) {

			myIntent = new Intent(context, UrlService.class);
			receiver = data.getParcelableExtra(UrlService.RECEIVER_KEY);
			myIntent.putExtra("url", data.getStringExtra("url"));
			myIntent.putExtra("receiverTag", receiver);
			myIntent.putExtra(UrlService.RECEIVER_KEY, receiver);
			myIntent.putExtra(UrlService.COMMAND_KEY,
					UrlService.PERFORM_SERVICE_ACTIVITY);
			context.startService(myIntent);
		}
	}
}
