package csci4144DataWareHousing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Apriori {

	private Database db;
	private List<KeyValue> items;
	private double sup_rate;
	private double conf_rate;
	private List<ItemSet> frequentItemSets = new ArrayList<>();
	private List<ItemSet> candidateItemSets = null;
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
		
		List<ItemSet> tmpItemSet = firstItemSet;
		while(tmpItemSet.size() > 1){
			candidateItemSets = new ArrayList<>();
			for(int i = 0; i < tmpItemSet.size() - 1; i++){
				ItemSet iItemSet = tmpItemSet.get(i);
				for(int j = i + 1; j < tmpItemSet.size(); j++){
					ItemSet jItemSet = tmpItemSet.get(j);
					List<KeyValue> tmp = this.JoinItemSet(m, iItemSet.getItemSet(), jItemSet.getItemSet());
					if(tmp.size() != 0){
						double newSupportRate = this.GetSupportValueForItemSet(tmp);
						ItemSet newItemSet = new ItemSet();
						newItemSet.addAll(tmp);
						newItemSet.setSupp(newSupportRate);
						candidateItemSets.add(newItemSet);
					}
				}
			}
			tmpItemSet = this.GenerateFrequentItemSet(candidateItemSets);
			m++;
			frequentItemSets.addAll(tmpItemSet);
		}
		
		this.printFreqItemSet(frequentItemSets);
		
	}
	
	private void printFreqItemSet(List<ItemSet> freqItemSet){
		int count = 1;
		for(ItemSet iSet: freqItemSet){
			List<KeyValue> kvList = iSet.getItemSet();
			System.out.println("frequent Item Set: " + count++ );
			for(int i = 0; i < kvList.size(); i++){
				KeyValue kv = kvList.get(i);
				System.out.println("label: " + kv.getKey() + " value: " + kv.getValue());
			}
			System.out.println(iSet.getSupp());
		}
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
			double freq_rate = this.GetSupportValueForItemSet(tmp);
			if(freq_rate > this.sup_rate){
				tmpItemSet = new ItemSet();
				tmpItemSet.add(kv);
				tmpItemSet.setSupp(freq_rate);
				firstItemSet.add(tmpItemSet);
			}
		}
		return firstItemSet;
	}
	private List<ItemSet> PruneKItemSet(List<ItemSet> lstKItemSet)
	{
		List<ItemSet> result = new ArrayList<>();
		for(ItemSet iSet: lstKItemSet){
			if(iSet.getSupp() > this.sup_rate){
				result.add(iSet);
			}
		}
		return result;
	}
	private List<ItemSet> GenerateFrequentItemSet(List<ItemSet> lstItemSet){
		
		return this.PruneKItemSet(lstItemSet);
	}
	private boolean IsItemSetExists(List<ItemSet> lstKLevelItemSet, List<KeyValue> lstKeyValues){
		return true;
	}
	private List<KeyValue> JoinItemSet(int K, List<KeyValue> itemsOutter, List<KeyValue> itemsInner){
		List<KeyValue> kv = new ArrayList<>();
		Set<KeyValue> s = new HashSet<>();
		if(K == 2){
			if(!itemsOutter.get(0).getKey().equals(itemsInner.get(0).getKey())){
				kv.addAll(itemsOutter);
				kv.addAll(itemsInner);
				kv = this.sortItemSet(kv);
			}
		}else{

			itemsOutter = this.sortItemSet(itemsOutter);
			itemsInner = this.sortItemSet(itemsInner);
			boolean forward = true;
			boolean backward = true;
			for(int i = 0; i < K - 2; i++){
				if(!itemsOutter.get(i).equals(itemsInner.get(i))){
					forward = false;
				}
			}
			
			if(!forward){
				for(int i = K - 2; i >= 1; i--){
					if(!itemsOutter.get(i).equals(itemsInner.get(i))){
						backward = false;
					}
				}
			}
			
			if(forward){
				if(!itemsOutter.get(K - 2).getKey().equals(itemsInner.get(K - 2).getKey())){
					kv.addAll(itemsOutter);
					kv.add(itemsInner.get(K - 2));
				}
			}else if(backward){
				if(!itemsOutter.get(0).getKey().equals(itemsOutter.get(0).getKey())){
					kv.addAll(itemsOutter);
					kv.add(itemsInner.get(0));
				}
			}
			
			if(kv.size() > 0){
				kv = this.sortItemSet(kv);	
			}
			
		}
		return kv;
	}
	
	private boolean IsPruneRules(){
		return true;
	}
	
	private double GetSupportValueForItemSet(List<KeyValue> kvList){
		double freq = db.countLiteral(kvList) * 1.0;
		double freq_rate = BigDecimal.valueOf(freq/db.RowCount()).setScale(2, RoundingMode.HALF_UP).doubleValue();
		return freq_rate ;
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
	private List<KeyValue> sortItemSet(List<KeyValue> kvList){
		List<KeyValue> result = new ArrayList<>();
		List<String> labels = db.getLabelNames();
		Map<Integer, KeyValue> orderMap = new TreeMap<>();
		for(int i = 0; i < kvList.size(); i++){
			orderMap.put(labels.indexOf(kvList.get(i).getKey()), kvList.get(i));
		}
		result.addAll(orderMap.values());
		return result;
	}
}
