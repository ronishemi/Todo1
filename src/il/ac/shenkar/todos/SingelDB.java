package il.ac.shenkar.todos;

import il.ac.shenkar.todos.database.DatabaseHandler;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

public class SingelDB {

	/**
	 * Logger's tag.
	 */
	// Properties
	private static final String TAG = "SingelDB";
	private static SingelDB singelDB;
	private static List<ItemDetails> array;
	private Context _context;
	private static DatabaseHandler entry;

	// Constructor
	private SingelDB(Context context) {
		_context = context;
		array = new ArrayList<ItemDetails>();
		entry = new DatabaseHandler(context);
		array = entry.getAllItemDetails();

	}

	// Get instance from database
	static SingelDB getInstance(Context context) {
		if (singelDB == null) {
			singelDB = new SingelDB(context);
		}
		return singelDB;
	}

	// Get entry
	public DatabaseHandler getEntry() {
		return entry;
	}

	// Get items list
	public List<ItemDetails> getList() {
		return array;
	}

	// Add to List
	public boolean addList(String name, String description, String deleted,
			Long timeStamp, Double latitude, Double longitude) {
		singelDB.getEntry().addItemDetails(
				new ItemDetails(name, description, deleted, timeStamp,
						latitude, longitude));

		ItemDetails item_details = new ItemDetails();
		item_details.setName(name);
		item_details.setItemDescription(description);
		item_details.setDeleted(deleted);
		item_details.setTimeStamp(timeStamp);
		item_details.setLatitude(latitude);
		item_details.setLongitude(longitude);
		if (array.isEmpty())
			item_details.set_id(1);
		else{
			if(singelDB.getEntry().getItemDetails(array.size() + 1) != null)
				item_details.set_id(singelDB.getEntry()
					.getItemDetails(array.size() + 1).get_id());
			}
		if(item_details != null)
			return SingelDB.array.add(item_details);
		return false;
	}
	// Delete item
	public boolean delList(int position) {
		if(array.isEmpty())
			return false;
		ItemDetails item_details = array.get(position);
		if (item_details != null) {
		singelDB.getEntry().deleteItemDetails(item_details);
		item_details = SingelDB.array.remove(position);
		}else
			return false;
		
		return true;
	}

	// Get item details
	public ItemDetails getItemDetails(int index) {
		return SingelDB.array.get(index);
	}

	// Get last item details
	public ItemDetails getLastItemDetails() {
		
		if(array.isEmpty())
			return null;
		return SingelDB.array
				.get(singelDB.getEntry().getItemDetailsCount() - 1);
	}

	// Get position of item
	public int getPosition(ItemDetails temDetails) {
		int i = 0;
		if(temDetails == null)
			return -1;
		while ((SingelDB.array.size() >= i)
				&& (temDetails.get_id() != SingelDB.array.get(i).get_id())) {
			++i;
		}
		return i;
	}

}
