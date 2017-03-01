package csci4144DataWareHousing;

public class KeyValue {
	private String key;
	private String value;
	private String KeyValueString;
	public KeyValue(String key, String val, String keyValueString ){
		this.key = key;
		this.value = val;
		this.KeyValueString = keyValueString;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getKeyValueString() {
		return KeyValueString;
	}
	public void setKeyValueString(String keyValueString) {
		KeyValueString = keyValueString;
	}
	
	
}
