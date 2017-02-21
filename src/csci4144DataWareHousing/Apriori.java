package csci4144DataWareHousing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Apriori {

	private Database db;
	private List<KeyValue> items;
	private double sup_rate;
	private double conf_rate;
	public Apriori(Database db, double support, double confidence){
		this.db = db;
		this.sup_rate = support;
		this.conf_rate = confidence;
	}
	public void RunApriori(){
		//1. generate keyvalue list from db 
		int labelSize = db.getlabelSize();
		items = new ArrayList<KeyValue>();
		for(int i = 0; i < labelSize; i++){
			String tmpLabel = db.getLabelName(i);
			List<String> distinctVals = db.GetColumnDistinctValues(tmpLabel);
			for(String val: distinctVals){
				KeyValue kv = new KeyValue(tmpLabel, val);
				items.add(kv);
			}
		}
		
		//2. use the generated keyvalue list to build the firstitemset
		List<ItemSet> firstItemSet = null;
		try {
			firstItemSet = this.GenerateFirstItemSet();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//testing firstItemSet
		for(ItemSet item: firstItemSet){
			KeyValue kv = item.getItemSet().get(0);
			String key = kv.getKey();
			String val = kv.getValue();
			System.out.println(key + " " + val);
			System.out.println(item.getSupp());
		}
		
		int m = 2;
		
		//3. with first itemset we continue to run generate frequent itemset until some condition are met.
		this.GenerateFrequentItemSet(firstItemSet, m );
		
	}
	public List<ItemSet> SortList (List<ItemSet> lstKItemItemSetCopied){
		return new ArrayList<>();
	}
	
	private void ProcessRules(){
		
	}
	public void GenerateRulesSets(List<Rules> lstRules, List<KeyValue> lstKeyValue){
		
	}
	private boolean IsCombinationExists(List<KeyValue> lstItemMultiply, List<List<KeyValue>> list){
		return true;
	}
	private List<ItemSet> GenerateFirstItemSet() throws Exception{
		List<ItemSet> firstItemSet = new ArrayList<ItemSet>();
		ItemSet tmpItemSet = null;
		for(KeyValue kv: this.items){
			List<KeyValue> tmp = new ArrayList<>();
			tmp.add(kv);
			double freq = db.countLiteral(tmp) * 1.0;
			double freq_rate = BigDecimal.valueOf(freq/db.RowCount()).setScale(2, RoundingMode.HALF_UP).doubleValue();
			if(freq_rate > this.sup_rate){
				tmpItemSet = new ItemSet();
				tmpItemSet.add(kv);
				tmpItemSet.setSupp(freq_rate);
				firstItemSet.add(tmpItemSet);
			}
		}
		return firstItemSet;
	}
	private void PruneKItemSet(List<ItemSet> lstKItemSet) throws ParseException
	{
		
	}
	private List<ItemSet> GenerateFrequentItemSet(List<ItemSet> lstItemSet, int iLevelGenerated){
		
		return new ArrayList<>();
	}
	private boolean IsItemSetExists(List<ItemSet> lstKLevelItemSet, List<KeyValue> lstKeyValues){
		return true;
	}
	private List<KeyValue> JoinItemSet(int K, List<KeyValue> itemsOutter, List<KeyValue> itemsInner){
		return new ArrayList<>();
	}
	private boolean IsPruneRules(){
		return true;
	}
	private double GetSupportValueForItemSet(List<KeyValue> kvList){
		return 1;
	}
	private List<Rules> GenerateRules(List<List<KeyValue>> lstConditions, List<KeyValue> lstKeyValue){
		return new ArrayList<>();
	}
	private List<KeyValue> GetJoinedItems(KeyValue keyValue, List<KeyValue> list){
		return new ArrayList<>();
	}
	private boolean IsRuleExists(List<Rules> lstRules, Rules ruleToFind){
		return true;
	}
}
