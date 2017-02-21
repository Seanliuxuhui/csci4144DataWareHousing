package csci4144DataWareHousing;

public class KeyValue {
	private String key;
	private String value;
	public KeyValue(String key, String val){
		this.key = key;
		this.value = val;
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
	
}
