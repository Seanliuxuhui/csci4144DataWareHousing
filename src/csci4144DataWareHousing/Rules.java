package csci4144DataWareHousing;

public class Rules {
	private double supp_rate; 
	private double conf_rate;
	private ItemSet preItemSet = null;
	private ItemSet postItemSet = null;
	
	public Rules(double supp_rate, double conf_rate, ItemSet preItemSet, ItemSet postItemSet){
		this.supp_rate = supp_rate;
		this.conf_rate = conf_rate;
		this.preItemSet = preItemSet;
		this.postItemSet = postItemSet;
	}
	public double getSupp_rate() {
		return supp_rate;
	}
	public void setSupp_rate(double supp_rate) {
		this.supp_rate = supp_rate;
	}
	public double getConf_rate() {
		return conf_rate;
	}
	public void setConf_rate(double conf_rate) {
		this.conf_rate = conf_rate;
	}
	public ItemSet getPreItemSet() {
		return preItemSet;
	}
	public void setPreItemSet(ItemSet preItemSet) {
		this.preItemSet = preItemSet;
	}
	public ItemSet getPostItemSet() {
		return postItemSet;
	}
	public void setPostItemSet(ItemSet postItemSet) {
		this.postItemSet = postItemSet;
	}
	
	
	
}
