package il.ac.shenkar.todos;

public class ItemDetails {
	// Properties
	private Integer _id;			//item id number
	private String name;			//item name
	private String itemDescription;	//item description
	private String deleted;			//flag if deleted
	private Long timeStamp;			//item time stamp if exist
	private Double latitude;		//item location if exist
	private Double longitude;		//item location if exist

	// Default constructor
	public ItemDetails() {
	}

	// Constructor
	public ItemDetails(Integer _id, String name, String itemDescription,
			String deleted, Long timeStamp, Double latitude, Double longitude) {
		super();
		this._id = _id;
		this.name = name;
		this.itemDescription = itemDescription;
		this.deleted = deleted;
		this.timeStamp = timeStamp;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	// Constructor
	public ItemDetails(String name, String itemDescription, String deleted,
			Long timeStamp, Double latitude, Double longitude) {
		super();
		this.name = name;
		this.itemDescription = itemDescription;
		this.deleted = deleted;
		this.timeStamp = timeStamp;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	// Getter
	public Integer get_id() {
		return _id;
	}

	// Setter
	public void set_id(Integer _id) {
		this._id = _id;
	}

	// Getter
	public String getName() {
		return name;
	}

	// Setter
	public void setName(String name) {
		this.name = name;
	}

	// Getter
	public String getItemDescription() {
		return itemDescription;
	}

	// Setter
	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	// Getter
	public Long getTimeStamp() {
		return timeStamp;
	}

	// Setter
	public void setTimeStamp(Long timeStamp) {
		this.timeStamp = timeStamp;
	}

	// Getter
	public String getDeleted() {
		return deleted;
	}

	// Setter
	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}

	// Getter
	public Double getLatitude() {
		return latitude;
	}

	// Setter
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	// Getter
	public Double getLongitude() {
		return longitude;
	}

	// Setter
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	// Set to string procedure
	@Override
	public String toString() {
		return "ItemDetails [_id=" + _id + ", name=" + name
				+ ", itemDescription=" + itemDescription + ", timeStamp="
				+ timeStamp + ", deleted=" + deleted + ", latitude=" + latitude
				+ ", longitude=" + longitude + "]";
	}

}