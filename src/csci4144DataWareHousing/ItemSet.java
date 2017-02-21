package csci4144DataWareHousing;

import java.util.ArrayList;
import java.util.List;

public class ItemSet {
	private List<KeyValue> kvList = new ArrayList<KeyValue>();
	private double conf;
	private double supp;
	public double getConf() {
		return conf;
	}
	public void setConf(double conf) {
		this.conf = conf;
	}
	public double getSupp() {
		return supp;
	}
	public void setSupp(double supp) {
		this.supp = supp;
	}
	public void add(KeyValue kv){
		kvList.add(kv);
	}
	public List<KeyValue> getItemSet(){
		return kvList;
	}
}
