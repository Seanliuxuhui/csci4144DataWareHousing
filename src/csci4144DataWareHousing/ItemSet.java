package csci4144DataWareHousing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemSet {
	private List<KeyValue> kvList = new ArrayList<KeyValue>();
	private Set<String> kvValueList = new HashSet<String>();
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
	public void addAll(List<KeyValue> newKVList){
		kvList.addAll(newKVList);
	}
	public List<KeyValue> getItemSet(){
		return kvList;
	}
	public void addStringKVList(Set<String> strKVList){
		kvValueList.addAll(strKVList);
	}
	public Set<String> getItemSetKeyValuesList(){
		return this.kvValueList;
	}
}
