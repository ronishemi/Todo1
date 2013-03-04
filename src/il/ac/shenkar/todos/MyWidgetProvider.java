package il.ac.shenkar.todos;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class MyWidgetProvider extends AppWidgetProvider {
	// Properties
	private static final String ACTION_CLICK = "ACTION_CLICK";

	// Update of Widget
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		// Get all id's
		ComponentName thisWidget = new ComponentName(context,
				MyWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		for (int widgetId : allWidgetIds) {

			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
					R.layout.widget_layout);

			// Set the text
			remoteViews.setTextViewText(R.id.update, "The best Todo App");

			// Register an onClickListener
			Intent launchActivity = new Intent(context, Todo.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
					launchActivity, 0);
			remoteViews.setOnClickPendingIntent(R.id.update, pendingIntent);
			AppWidgetManager manager = AppWidgetManager.getInstance(context);
			manager.updateAppWidget(thisWidget, remoteViews);

		}
	}
}