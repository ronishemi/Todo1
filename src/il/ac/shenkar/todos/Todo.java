package il.ac.shenkar.todos;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Tracker;

import il.ac.shenkar.todos.BroadCast.ReminderBroadCastReceiver;
import il.ac.shenkar.todos.service.GetUrlFromTomer;
import il.ac.shenkar.todos.service.MyLocationListener;

import il.ac.shenkar.todos.service.UrlService;
import il.ac.shenkar.todos.service.MyResultReceiver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Todo extends Activity implements MyResultReceiver.Receiver,
		SensorEventListener {
	// Properties
	private MyResultReceiver receiver;
	private int REL_SWIPE_MIN_DISTANCE;
	private int REL_SWIPE_MAX_OFF_PATH;
	private int REL_SWIPE_THRESHOLD_VELOCITY;
	private SingelDB dB1;
	private IntentFilter filter;
	private Resources res = null;
	private Drawable drawable;
	private LinearLayout linearLayout;
	private Button btnAdd;
	private Button btnUrl;
	private ListView lv1;
	private ItemListBaseAdapter adapter;
	static final int NEW_ACTIVITY = 100;
	private int posiTion;
	private URL url;
	private GetUrlFromTomer getUrlFromTomer;
	private SharedPreferences sharedPref;
	private Boolean firstTime;
	private Boolean waitForTomer;
	private LocationManager locationManager;
	private int toPhone, backround;
	private Integer unqueId;
	private double latitude;
	private double longitude;
	private String returnTask;
	private Tracker tracker;
	private SensorManager sensorManager;
	private long lastUpdate;
	private Long timeToBroadCastReceiver;
	// expire
	private static final long PROX_ALERT_EXPIRATION = -1; // It will never
	private static final long POINT_RADIUS = 100; // in Meters
	public static final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 100; // in
																		// Meters
	public static final long MINIMUM_TIME_BETWEEN_UPDATE = 10000; // in
																	// Milliseconds

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		android.view.Window window = getWindow();
		window.addFlags(android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		window.addFlags(android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		window.setContentView(R.layout.main);
		sharedPref = getSharedPreferences("hasRunBefore", MODE_PRIVATE);
		firstTime = sharedPref.getBoolean("isLauncedTime", false);
		backround = sharedPref.getInt("backround", 0);
		DisplayMetrics dm = getResources().getDisplayMetrics();
		REL_SWIPE_MIN_DISTANCE = (int) (120.0f * dm.densityDpi / 160.0f + 0.5);
		REL_SWIPE_MAX_OFF_PATH = (int) (250.0f * dm.densityDpi / 160.0f + 0.5);
		REL_SWIPE_THRESHOLD_VELOCITY = (int) (200.0f * dm.densityDpi / 160.0f + 0.5);

		// set accelerometer
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		lastUpdate = System.currentTimeMillis();

		// Set our receiver
		receiver = new MyResultReceiver(new Handler());
		receiver.setReceiver(this);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		try {
			url = new URL(
					"http://mobile1-tasks-dispatcher.herokuapp.com/task/random");
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		dB1 = SingelDB.getInstance(this);
		// button configuration
		btnAdd = (Button) findViewById(R.id.newAdd);

		// ========================================================
		lv1 = (ListView) findViewById(R.id.listV_main);
		adapter = new ItemListBaseAdapter(Todo.this, dB1.getList());
		lv1.setAdapter(adapter);
		@SuppressWarnings("deprecation")
		final GestureDetector gestureDetector = new GestureDetector(
				new MyGestureDetector());
		View.OnTouchListener gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {

				return gestureDetector.onTouchEvent(event);
			}
		};
		lv1.setOnTouchListener(gestureListener);
		// Set context
		EasyTracker.getInstance().setContext(getApplicationContext());
		// Instantiate the Tracker
		tracker = EasyTracker.getTracker();
		// Add new task
		btnAdd.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			public void onClick(View v) {
				tracker.trackEvent("ui_action", "button_press", "new Task", 0L);
				Intent intent = new Intent(Todo.this, TaskListActivity.class);
				startActivityForResult(intent, Todo.NEW_ACTIVITY);

			}
		});
		// button configuration
		btnUrl = (Button) findViewById(R.id.btUrl);
		// Add new task from service
		btnUrl.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!waitForTomer) {
					waitForTomer = true;
					tracker.trackEvent("ui_action", "button_press", "GET url",
							0L);
					GetTodoFromWeb task = new GetTodoFromWeb();
					task.execute(url);
				} 
			}

		});

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		// Send a screen view when the Activity is displayed to the user.
		EasyTracker.getInstance().activityStart(this); // Add this method
		waitForTomer = false;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		EasyTracker.getInstance().activityStop(this); // Add this method

	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		sharedPref.edit().putBoolean("isLauncedTime", true).commit();
		if (backround > 0) {
			res = getResources();
			drawable = res.getDrawable(backround);
			linearLayout = (LinearLayout) findViewById(R.id.GameLayout);
			linearLayout.setBackgroundDrawable(drawable);
		}
		adapter.notifyDataSetChanged();
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == NEW_ACTIVITY && resultCode == RESULT_OK) {
			unqueId = 0;
			String editPosition = null;
			String wall = data.getStringExtra("toPhone");
			if (wall != null)
				toPhone = Integer.parseInt(wall);

			latitude = data.getDoubleExtra("latitude", 0);
			longitude = data.getDoubleExtra("longitude", 0);

			String prox = data.getStringExtra("proximity");
			if ((prox != null) && ((latitude == 0) || (longitude == 0)))
				return;

			editPosition = data.getStringExtra("posiTion");

			returnTask = data.getStringExtra("newMessage");
			String timestamp = data.getStringExtra("TimeToBroadCastReceiver");
			timeToBroadCastReceiver = (long) 0;

			if ((toPhone == 0) && (prox == null) && (latitude == 0)
					&& (longitude == 0)) {
				if (timestamp != null)
					timeToBroadCastReceiver = Long.parseLong(timestamp);
				if (editPosition == null) {
					dB1.addList(returnTask, returnTask, "ok",
							timeToBroadCastReceiver, (double) 0, (double) 0);
					unqueId = dB1.getLastItemDetails().get_id();
				} else {
					unqueId = Integer.parseInt(editPosition);
					ItemListBaseAdapter.cancelAlarm(Todo.this
							.getApplicationContext(),
							dB1.getItemDetails(unqueId).getName(), null, dB1
									.getItemDetails(unqueId).get_id());
					dB1.getItemDetails(unqueId).setName(returnTask);
					dB1.getItemDetails(unqueId).setTimeStamp(
							timeToBroadCastReceiver);
					dB1.getEntry().updateItemDetails(
							dB1.getItemDetails(unqueId));
					unqueId = dB1.getItemDetails(unqueId).get_id();
				}
				adapter.notifyDataSetChanged();

				// unqueId =Id++;

				if ((latitude == 0) || (longitude == 0)) {
					if ((timeToBroadCastReceiver > 0)
							&& (returnTask.compareTo("SERVICE") != 0)) {
						setAlarm();
					} else if ((returnTask.compareTo("SERVICE") == 0))
						setService();
				}
			} else {
				if (wall != null) {
					res = getResources();
					drawable = res.getDrawable(toPhone);
					linearLayout = (LinearLayout) findViewById(R.id.GameLayout);
					linearLayout.setBackgroundDrawable(drawable);
					sharedPref.edit().putInt("backround", toPhone).commit();
					backround = toPhone;
					toPhone = 0;

				} else {

					if (editPosition == null) {
						dB1.addList(returnTask, "service", "ok",
								timeToBroadCastReceiver, latitude, longitude);
						unqueId = dB1.getLastItemDetails().get_id();
					} else {
						unqueId = Integer.parseInt(editPosition);
						ItemListBaseAdapter
								.cancelAlarm(Todo.this.getApplicationContext(),
										dB1.getItemDetails(unqueId).getName(),
										"service", dB1.getItemDetails(unqueId)
												.get_id());
						dB1.getItemDetails(unqueId).setName(returnTask);
						dB1.getItemDetails(unqueId).setTimeStamp(
								timeToBroadCastReceiver);
						dB1.getItemDetails(unqueId).setLatitude(latitude);
						dB1.getItemDetails(unqueId).setLongitude(longitude);
						dB1.getEntry().updateItemDetails(
								dB1.getItemDetails(unqueId));
						unqueId = dB1.getItemDetails(unqueId).get_id();
					}
					adapter.notifyDataSetChanged();
					setLocation();

				}
			}
		}
	}

	@SuppressWarnings("unused")
	private void myOnItemClick(int position) {
		
	}

	private void onLTRFling() {
		try {

			if ((!dB1.getList().isEmpty()) && posiTion <= dB1.getList().size()
					&& posiTion >= 0) {
				ItemDetails itemDetails = dB1.getItemDetails(posiTion);
				itemDetails.setDeleted("deleted");
				dB1.getEntry().updateItemDetails(itemDetails);
				adapter.notifyDataSetChanged();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		sensorManager.unregisterListener(this);
	}

	private void onRTLFling() {
		try {
			if ((!dB1.getList().isEmpty()) && posiTion <= dB1.getList().size()
					&& posiTion >= 0) {
				ItemDetails itemDetails = dB1.getItemDetails(posiTion);
				itemDetails.setDeleted("ok");
				dB1.getEntry().updateItemDetails(itemDetails);
				adapter.notifyDataSetChanged();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class MyGestureDetector extends SimpleOnGestureListener {

		// Detect a single-click and call my own handler.
		@Override
		public boolean onSingleTapUp(MotionEvent e) {

			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub
			super.onLongPress(e);
			posiTion = lv1.pointToPosition((int) e.getX(), (int) e.getY());
			Intent intent = new Intent(Todo.this, TaskListActivity.class);
			intent.putExtra("latitude", dB1.getItemDetails(posiTion)
					.getLatitude());
			intent.putExtra("longitude", dB1.getItemDetails(posiTion)
					.getLongitude());
			intent.putExtra("posiTion", String.valueOf(posiTion));
			intent.putExtra("massage", dB1.getItemDetails(posiTion).getName());
			intent.putExtra("TimeToBroadCastReceiver",
					dB1.getItemDetails(posiTion).getTimeStamp());
			startActivityForResult(intent, Todo.NEW_ACTIVITY);

		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				posiTion = lv1
						.pointToPosition((int) e1.getX(), (int) e1.getY());
				if (Math.abs(e1.getY() - e2.getY()) > REL_SWIPE_MAX_OFF_PATH)
					return false;
				if (e1.getX() - e2.getX() > REL_SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) {
					onRTLFling();
				} else if (e2.getX() - e1.getX() > REL_SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) {
					onLTRFling();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	}

	private class GetTodoFromWeb extends AsyncTask<URL, Integer, String> {

		/**
		 * Logger's tag.
		 */
		private static final String TAG = "GetTodoFromWeb";

		protected String doInBackground(URL... args) {
			publishProgress(0); // Invokes onProgressUpdate()
			getUrlFromTomer = new GetUrlFromTomer(args[0]);
			return getUrlFromTomer.readTodo();

		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);

			ImageView img = (ImageView) findViewById(R.id.time);
			img.setImageResource(R.drawable.sand_clock);
			img.setVisibility(View.VISIBLE);

		}

		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			ImageView img = (ImageView) findViewById(R.id.time);
			img.setImageResource(R.drawable.sand_clock);
			img.setVisibility(View.INVISIBLE);

			try {
				if((result != null)&&(!result.isEmpty())){
				JSONObject jsonObject = new JSONObject(result);
				String topic = (String) jsonObject.get("topic");
				String description = (String) jsonObject.get("description");
				if (topic != null) {
					if(dB1.addList(topic, description, "ok", (long) 0, (double) 0,
							(double) 0))
					adapter.notifyDataSetChanged();
				}
				Log.d(TAG, "jsonObj: " + jsonObject);
			  }
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				waitForTomer = false;
			}
		}

	}

	public void onReceiveResult(int resultCode, Bundle resultBundle) {

		switch (resultCode) {
		case UrlService.STATUS_RUNNING:
			// Don't do anything, the service is running
			break;
		case UrlService.STATUS_SUCCESS:
			boolean wasSuccess = resultBundle
					.getBoolean(UrlService.SERVICE_WAS_SUCCESS_KEY);
			if (wasSuccess) {
				Toast.makeText(getApplicationContext(),
						"The service was a success", Toast.LENGTH_LONG).show();
			} else {
				// Show not success message
				Toast.makeText(getApplicationContext(),
						"The service was a failure", Toast.LENGTH_LONG).show();
			}
			break;
		case UrlService.STATUS_FINISHED:

			try {
				String service = (String) resultBundle.get("ServiceResult");
				if (service != null) {
					JSONObject jsonObject = new JSONObject(service);
					String topic = (String) jsonObject.get("topic");
					String description = (String) jsonObject.get("description");
					System.out.println("from Tomer: " + topic);
					if (topic != null) {
						dB1.addList(topic, description, "ok", (long) 0,
								(double) 0, (double) 0);
						adapter.notifyDataSetChanged();

					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case UrlService.STATUS_ERROR:
			Toast.makeText(getApplicationContext(), "The service had an error",
					Toast.LENGTH_LONG).show();
			break;
		}
	}

	// Set location to task
	public void setLocation() {
		locationManager = (LocationManager) getSystemService(Todo.this
				.getApplicationContext().LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				MINIMUM_TIME_BETWEEN_UPDATE, MINIMUM_DISTANCECHANGE_FOR_UPDATE,
				new MyLocationListener());

		Intent intent1 = new Intent(Todo.this.getApplicationContext(),
				ReminderBroadCastReceiver.class);
		intent1.putExtra("proximity", "proximity");
		intent1.putExtra("uniqueId", unqueId);
		intent1.putExtra("newMessage", returnTask);
		PendingIntent proximityIntent = PendingIntent.getBroadcast(
				Todo.this.getApplicationContext(), unqueId, intent1,
				PendingIntent.FLAG_UPDATE_CURRENT);
		locationManager.addProximityAlert(latitude, longitude, POINT_RADIUS,
				PROX_ALERT_EXPIRATION, proximityIntent);
		sendBroadcast(new Intent(intent1));

	}

	// Set alarm to task
	public void setAlarm() {

		Intent intent = new Intent(Todo.this.getApplicationContext(),
				ReminderBroadCastReceiver.class);
		intent.putExtra("newMessage", returnTask);
		intent.putExtra("uniqueId", unqueId);
		intent.setAction(returnTask);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				Todo.this.getApplicationContext(), unqueId, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) Todo.this
				.getApplicationContext().getSystemService(ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, timeToBroadCastReceiver,
				pendingIntent);
	}

	public void setService() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.SECOND, 00);
		calendar.set(Calendar.MINUTE, 00);
		calendar.set(Calendar.HOUR_OF_DAY, 00);
		// calendar.add(Calendar.DAY_OF_MONTH, 1);
		Intent intent = new Intent(Todo.this.getApplicationContext(),
				ReminderBroadCastReceiver.class);
		intent.putExtra("url", url.toString());
		intent.putExtra("uniqueId", unqueId);
		intent.putExtra("SERVICE", "SERVICE");
		intent.putExtra(UrlService.RECEIVER_KEY, receiver);
		intent.putExtra(UrlService.COMMAND_KEY,
				UrlService.PERFORM_SERVICE_ACTIVITY);
		intent.setAction(returnTask);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				Todo.this.getApplicationContext(), unqueId, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) Todo.this
				.getApplicationContext().getSystemService(ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
				pendingIntent);
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	// Check sensor activity
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			getAccelerometer(event);
		}

	}

	// Get accelerometer sensor activity
	private void getAccelerometer(SensorEvent event) {
		float[] values = event.values;
		// Movement
		float x = values[0];
		float y = values[1];
		float z = values[2];

		float accelationSquareRoot = (x * x + y * y + z * z)
				/ (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
		long actualTime = System.currentTimeMillis();
		if (accelationSquareRoot >= 2) //
		{
			if ((actualTime - lastUpdate) < 200) {
				return;
			}
			lastUpdate = actualTime;
			// delete marked object
			unqueId = 0;
			ItemDetails item;
			int size = 0;
			boolean data_changed=false;
			boolean del=false;
			if (!dB1.getList().isEmpty())
				size = dB1.getList().size();
			if (size > 0) {
				do {
					item = dB1.getList().get(unqueId);
					System.out.println(item.getDeleted());
					if (item.getDeleted().matches("deleted")) {
						if (item.getItemDescription().matches("service")) {
							returnTask = item.getName();
							dB1.delList(unqueId);
							ItemListBaseAdapter.cancelAlarm(
									Todo.this.getApplicationContext(),
									returnTask, "service", item.get_id());
							unqueId = 0;
							data_changed=del=true;
							if (!dB1.getList().isEmpty())
								size = dB1.getList().size();
							else
								size = 0;
						} else {
							returnTask = item.getName();
							dB1.delList(unqueId);
							
							ItemListBaseAdapter.cancelAlarm(
									Todo.this.getApplicationContext(),
									returnTask, null, item.get_id());
							unqueId = 0;
							data_changed=del=true;
							if (!dB1.getList().isEmpty())
								size = dB1.getList().size();
							else
								size = 0;
						}

					}
					if(!del)
					 ++unqueId;
					del=false;
				} while ((size > 0) && (size > unqueId));
				if(data_changed)
					adapter.notifyDataSetChanged();
				return;
			}
		}
	}
}