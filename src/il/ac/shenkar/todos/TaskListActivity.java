package il.ac.shenkar.todos;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Tracker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class TaskListActivity extends Activity {
	// Properties
	static final int NEW_ACTIVITY1 = 200;
	static final int NEW_ACTIVITY2 = 300;
	static final int NEW_ACTIVITY3 = 400;
	// Buttons
	private Button ShowDatePicker;	//show date picker window
	private Button ShowTimePicker;	//show time picker window
	private Button Set;				//set data button
	private Button Cancel;			//cancel data button
	private Button btnVoice;		//add task by voice button
	private Button btnWall;			//change wallpaper button
	private Button btnShare;		//share task button
	private Button btnEdit;			//accept task button
	private Button btnReminder;		//add reminder button 
	private Button btnProximity;	//add location reminder button
	private Button address_ok;		//accept address

	// Date and Time picker
	private DatePicker DPic;
	private TimePicker TPic;
	private TextView Date;
	private Dialog dialog;
	final Calendar c = Calendar.getInstance();
	private SimpleDateFormat dfDate = new SimpleDateFormat("dd-MMM-yyyy kk:mm");
	private ViewSwitcher switcher;
	private String posiTion = null;
	private EditText editText;
	static final int DATE_TIME_DIALOG_ID = 0;

	// Location
	private Geocoder geocoder;
	private List<Address> addresses;
	private double latitude, longitude;
	private EditText address;
	private String showAddress;
	private TextView editTextProperties;
	long TimeToBroadCastReceiver;
	private Intent intent;
	private Tracker tracker; // google analytics tracker
	private Drawable first, second, Proxyfirst, Proxysecond; // icon colors

	// OnCreate main method
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_list);
		// Buttons configuration
		btnEdit = (Button) findViewById(R.id.btnMessage);
		btnReminder = (Button) findViewById(R.id.btnReminder);
		first = getResources().getDrawable(R.drawable.reminder_btt);
		second = getResources().getDrawable(R.drawable.bellred);
		Proxyfirst = getResources().getDrawable(R.drawable.pin_btt);
		Proxysecond = getResources().getDrawable(R.drawable.pinred);
		editText = (EditText) findViewById(R.id.edit_message);
		btnProximity = (Button) findViewById(R.id.btnGPS);

		// ====================font settings=====================
		// Font path
		String fontPath = "fonts/NOTEPAD_.TTF";
		// text view label
		TextView txt = (TextView) findViewById(R.id.edit_message);
		// Loading Font Face
		Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
		// Applying font
		txt.setTypeface(tf);

		// ===================tracker settings====================
		// Set context
		EasyTracker.getInstance().setContext(getApplicationContext());
		// Instantiate the Tracker
		tracker = EasyTracker.getTracker();

		// =========================================================
		intent = new Intent(TaskListActivity.this, Todo.class);
		Bundle extras = getIntent().getExtras();
		editTextProperties = (TextView) findViewById(R.id.editText1);
		Date = ((TextView) findViewById(R.id.Date));
		if (extras != null) {
			latitude = extras.getDouble("latitude", 0);
			longitude = extras.getDouble("longitude", 0);
			geocoder = new Geocoder(TaskListActivity.this);
			posiTion = extras.getString("posiTion");
			editText.setText(extras.getString("massage"));
			TimeToBroadCastReceiver = extras.getLong("TimeToBroadCastReceiver",
					0);
			// Change color of button if you have address or date&time
			// notification
			if ((latitude == 0) && (longitude == 0)) {
				btnProximity.setBackgroundDrawable(Proxyfirst);
			} else {
				btnProximity.setBackgroundDrawable(Proxysecond);
			}
			if (TimeToBroadCastReceiver == 0) {
				btnReminder.setBackgroundDrawable(first);
			} else {
				btnReminder.setBackgroundDrawable(second);
			}
			try {
				addresses = geocoder.getFromLocation(latitude, longitude, 10);
				if (!addresses.isEmpty()) {
					showAddress = addresses.get(0).getAddressLine(1) + " "
							+ addresses.get(0).getAddressLine(0);
					editTextProperties.setVisibility(View.VISIBLE);
					editTextProperties.setText(showAddress);
					intent.putExtra("latitude", latitude);
					intent.putExtra("longitude", longitude);
					intent.putExtra("proximity", "proximity");
				} else
					showAddress = "";
				if (TimeToBroadCastReceiver != 0) {
					c.setTimeInMillis(TimeToBroadCastReceiver);
					Date.setText(dfDate.format(c.getTime()));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/*-----------------Button to Add a new ProximityAlert list------------------------*/
		btnProximity.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// Buttons configuration
				address = (EditText) findViewById(R.id.editAddress);
				address_ok = (Button) findViewById(R.id.btnAddress);
				address.setVisibility(View.VISIBLE);
				address_ok.setVisibility(View.VISIBLE);
				btnEdit.setVisibility(View.INVISIBLE);
				// address accepting button onClick function
				address_ok.setOnClickListener(new OnClickListener() {
					@SuppressWarnings("deprecation")
					public void onClick(View v) {
						geocoder = new Geocoder(TaskListActivity.this);
						try {
							String message = editText.getText().toString();
							String addressMessage = address.getText()
									.toString();
							addresses = geocoder.getFromLocationName(
									addressMessage, 10);

							if (!addresses.isEmpty()) {
								tracker.trackEvent("ui_action", "button_press",
										"ADD ADDRESS", 0L);
								latitude = addresses.get(0).getLatitude();
								longitude = addresses.get(0).getLongitude();
							} else {
								CharSequence text = "adress not found!";
								int duration = Toast.LENGTH_SHORT;
								Toast toast = Toast.makeText(
										TaskListActivity.this, text, duration);
								toast.show();
								latitude = 0;
								longitude = 0;
							}
							System.out.println("lat  " + latitude + " long "
									+ longitude);

							intent.putExtra("latitude", latitude);
							intent.putExtra("longitude", longitude);
							intent.putExtra("proximity", "proximity");
							intent.putExtra("posiTion", posiTion);
							intent.putExtra("newMessage", message);
							if ((message == null) || (message.isEmpty())) {
								setResult(RESULT_CANCELED, intent);

							} else {
								setResult(RESULT_OK, intent);
								if (btnProximity.getBackground().equals(
										Proxyfirst)) {
									btnProximity
											.setBackgroundDrawable(Proxysecond);
								} else {
									btnProximity
											.setBackgroundDrawable(Proxysecond);
								}
							}
							finish();

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				});
			}
		});

		/*-----------------Button to Add a new item to list------------------------*/
		btnEdit.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				editTextProperties.setVisibility(View.INVISIBLE);
				String message = editText.getText().toString();
				intent.putExtra("posiTion", posiTion);
				intent.putExtra("newMessage", message);
				intent.putExtra("TimeToBroadCastReceiver",
						String.valueOf(TimeToBroadCastReceiver));
				tracker.trackEvent("ui_action", "button_press", "ADD NEW ITEM",
						0L);
				if ((message == null) || (message.isEmpty()))
					setResult(RESULT_CANCELED, intent);
				else
					setResult(RESULT_OK, intent);
				finish();
			}
		});

		/*------------------Button to Set voice reminder --------------------------*/
		// button configuration
		btnVoice = (Button) findViewById(R.id.microphone_btt);
		// adding task with voice
		btnVoice.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(TaskListActivity.this,
						VoiceToString.class);
				startActivityForResult(intent, TaskListActivity.NEW_ACTIVITY2);
			}
		});
		/*--------------Button to Share content of Task Application -------------------------*/
		// button configuration
		btnShare = (Button) findViewById(R.id.btnShare);
		btnShare.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				tracker.trackEvent("ui_action", "button_press", "SHARE ITEM",
						0L);
				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_TEXT, editText.getText()
						.toString());
				sendIntent.setType("text/plain");
				startActivity(Intent.createChooser(sendIntent, getResources()
						.getText(R.string.edit_message)));
			}
		});

		/*------------------Button to Set Wallpaper --------------------------*/
		// button configuration
		btnWall = (Button) findViewById(R.id.btnOpt);
		btnWall.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(TaskListActivity.this,
						Wallpaper.class);
				tracker.trackEvent("ui_action", "button_press",
						"Set Wallpaper", 0L);
				startActivityForResult(intent, TaskListActivity.NEW_ACTIVITY3);

			}
		});
		/*-----------------Button to Set a time and date reminder ---------------------------*/
		// buttons configuration
		dialog = new Dialog(this);
		dialog.setContentView(R.layout.datetimepicker);
		switcher = (ViewSwitcher) dialog.findViewById(R.id.DateTimePickerVS);
		DPic = (DatePicker) dialog.findViewById(R.id.DatePicker);
		TPic = (TimePicker) dialog.findViewById(R.id.TimePicker);
		TPic.setIs24HourView(true);
		ShowDatePicker = ((Button) dialog.findViewById(R.id.SwitchToDate));
		// Show date setting window
		ShowDatePicker.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				switcher.showPrevious();
				ShowDatePicker.setEnabled(false);
				ShowTimePicker.setEnabled(true);
			}
		});
		// button configuration
		ShowTimePicker = ((Button) dialog.findViewById(R.id.SwitchToTime));
		// Show time setting window
		ShowTimePicker.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				switcher.showNext();
				ShowDatePicker.setEnabled(true);
				ShowTimePicker.setEnabled(false);
			}
		});
		// button configuration
		Set = ((Button) dialog.findViewById(R.id.SetDateTime));
		// set data
		Set.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			public void onClick(View v) {
				c.set(DPic.getYear(), DPic.getMonth(), DPic.getDayOfMonth(),
						TPic.getCurrentHour(), TPic.getCurrentMinute());
				TimeToBroadCastReceiver = c.getTimeInMillis();
				if (btnReminder.getBackground().equals(first)) {
					btnReminder.setBackgroundDrawable(second);
				} else {
					btnReminder.setBackgroundDrawable(second);
				}
				dialog.cancel();
			}
		});
		// button configuration
		Cancel = ((Button) dialog.findViewById(R.id.CancelDialog));
		// cancel data
		Cancel.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			public void onClick(View v) {
				if (btnReminder.getBackground().equals(second)) {
					btnReminder.setBackgroundDrawable(first);
				} else {
					btnReminder.setBackgroundDrawable(first);
				}
				dialog.cancel();
			}
		});
		// reminder setting button
		btnReminder.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			public void onClick(View v) {
				DPic.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
						c.get(Calendar.DAY_OF_MONTH));
				TPic.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
				TPic.setCurrentMinute(c.get(Calendar.MINUTE));
				showDialog(DATE_TIME_DIALOG_ID);

			}
		});
		dialog.setTitle("Select Date Time");
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		EasyTracker.getInstance().activityStart(this); // Add this method
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		EasyTracker.getInstance().activityStop(this); // Add this method
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == NEW_ACTIVITY1 && resultCode == RESULT_OK) {
			String returnTask = data.getStringExtra("newMessage");
			editText.setText(returnTask);
		}
		if (requestCode == NEW_ACTIVITY2 && resultCode == RESULT_OK) {
			String returnTask = data.getStringExtra("newMessage");
			editText.setText(returnTask);
		}
		if (requestCode == NEW_ACTIVITY3 && resultCode == RESULT_OK) {
			Intent intent = new Intent(TaskListActivity.this, Todo.class);
			String toPhone = data.getStringExtra("toPhone");
			intent.putExtra("toPhone", toPhone);

			if ((toPhone == null) || (toPhone.matches("0")))
				setResult(RESULT_CANCELED, intent);
			else
				setResult(RESULT_OK, intent);
			finish();
		}
	}

	// Create Dialog Date&Time
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_TIME_DIALOG_ID:

			return dialog;
		}
		return null;
	}
}
