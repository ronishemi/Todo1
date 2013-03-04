package il.ac.shenkar.todos;

import il.ac.shenkar.todos.BroadCast.ReminderBroadCastReceiver;

import il.ac.shenkar.todos.service.MyLocationListener;

import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.LocationManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ItemListBaseAdapter extends BaseAdapter {

	
	// Properties
	protected static final String TAG = "ItemListBaseAdapter";
	private static List<ItemDetails> itemDetailsrrayList;
	private LayoutInflater l_Inflater;
	private Context context_;
	public SingelDB dB1 = SingelDB.getInstance(context_);

	// Constructor
	public ItemListBaseAdapter(Context context, List<ItemDetails> results) {
		context_ = context;
		itemDetailsrrayList = results;
		l_Inflater = LayoutInflater.from(context);
	}

	// Get number of items
	public int getCount() {
		if (itemDetailsrrayList.isEmpty())
			return 0;
		return itemDetailsrrayList.size();
	}

	// Get item from its position
	public Object getItem(int position) {
		if (itemDetailsrrayList.isEmpty())
			return null;
		return itemDetailsrrayList.get(position);
	}

	// Get item position
	public long getItemId(int position) {

		return position;
	}

	// Cancel service or reminder
	@SuppressWarnings("static-access")
	public static void cancelAlarm(Context context, String massage,
			String type, int id) {

		LocationManager locationManager;
		if ((type == null) || (type.matches("service") == false)) {
			Intent intent = new Intent(context.getApplicationContext(),
					ReminderBroadCastReceiver.class);
			intent.putExtra("newMessage", massage);
			intent.putExtra("uniqueId", id);
			intent.setAction(massage);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(
					context.getApplicationContext(), id, intent,
					PendingIntent.FLAG_CANCEL_CURRENT);
			AlarmManager alarmManager = (AlarmManager) context
					.getApplicationContext().getSystemService(
							Context.ALARM_SERVICE);
			alarmManager.cancel(pendingIntent);
		} else {

			locationManager = (LocationManager) context.getApplicationContext()
					.getSystemService(
							context.getApplicationContext().LOCATION_SERVICE);
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER,
					Todo.MINIMUM_TIME_BETWEEN_UPDATE,
					Todo.MINIMUM_DISTANCECHANGE_FOR_UPDATE,
					new MyLocationListener());
			Intent intent1 = new Intent(context.getApplicationContext(),
					ReminderBroadCastReceiver.class);
			intent1.putExtra("proximity", "proximity");
			intent1.putExtra("uniqueId", id);
			intent1.putExtra("newMessage", massage);
			PendingIntent proximityIntent = PendingIntent.getBroadcast(
					context.getApplicationContext(), id, intent1,
					PendingIntent.FLAG_CANCEL_CURRENT);
			locationManager.removeProximityAlert(proximityIntent);
			context.getApplicationContext().sendBroadcast(new Intent(intent1));

		}
	}

	// Get List View
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;

		if (convertView == null) {
			convertView = l_Inflater.inflate(R.layout.item_details_view, null);
			holder = new ViewHolder();

			holder.txt_itemName = (TextView) convertView
					.findViewById(R.id.name);
			holder.btn_item = (Button) convertView.findViewById(R.id.btDone);

			holder.btn_item.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					ItemDetails itemDetails = new ItemDetails();
					int unqueId = (Integer) v.getTag();
					itemDetails = dB1.getItemDetails(unqueId);
					dB1.delList(unqueId);
					notifyDataSetChanged();
					cancelAlarm(context_, itemDetails.getName(),
							itemDetails.getItemDescription(),
							(itemDetails.get_id()));
				}
			});

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();

		}

		holder.txt_itemName
				.setText(itemDetailsrrayList.get(position).getName());

		holder.btn_item.setTag(position);
		if ((itemDetailsrrayList.get(position).getDeleted() != null)
				&& itemDetailsrrayList.get(position).getDeleted()
						.compareTo("deleted") == 0) {
			holder.txt_itemName.setTextColor(Color.GRAY);
			holder.txt_itemName.setPaintFlags(holder.txt_itemName
					.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		} else {
			holder.txt_itemName.setTextColor(Color.WHITE);
			holder.txt_itemName.setPaintFlags(holder.txt_itemName
					.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
		}
		return convertView;
	}

	// Class of List items - item and cancel button
	static class ViewHolder {
		TextView txt_itemName;
		Button btn_item;
	}

}
