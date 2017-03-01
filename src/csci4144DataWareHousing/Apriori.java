package csci4144DataWareHousing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Apriori {

	private Database db;
	private List<KeyValue> items;
	private double sup_rate;
	private double conf_rate;
	private List<ItemSet> frequentItemSets = new ArrayList<>();
	private List<ItemSet> candidateItemSets = null;
	private Map<Integer, List<ItemSet>> frequentItemSetMap = new HashMap<>();
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
			int t = 1;
			for(String val: distinctVals){
				KeyValue kv = new KeyValue(tmpLabel, val, i + ":" + t++);
				items.add(kv);
			}
		}
		
		//2. use the generated keyvalue list to build the firstitemset
		List<ItemSet> firstItemSet = null;
		try {
			firstItemSet = this.GenerateFirstItemSet();
			frequentItemSets.addAll(firstItemSet);
			this.frequentItemSetMap.put(1, firstItemSet);
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
		boolean exists = false;
		
		//3. with first itemset we continue to run generate frequent itemset until some condition are met.
		
		List<ItemSet> tmpItemSet = firstItemSet;
		while(tmpItemSet.size() > 1){
			candidateItemSets = new ArrayList<>();
			for(int i = 0; i < tmpItemSet.size() - 1; i++){
				ItemSet iItemSet = tmpItemSet.get(i);
				for(int j = i + 1; j < tmpItemSet.size(); j++){
					ItemSet jItemSet = tmpItemSet.get(j);
					Set<String> tmp = JoinItemSet(m, iItemSet.getItemSetKeyValuesList(), jItemSet.getItemSetKeyValuesList());
					for(ItemSet it: candidateItemSets){
						if(it.getItemSetKeyValuesList().containsAll(tmp)){
							exists= true;
						}
					}
					if(tmp.size() == m && !exists){
						if(m == 4){
							System.out.println(tmp.toString());
						}
						List<KeyValue> result = this.buildKeyValueListFromString(tmp);
						double newSupportRate = this.GetSupportValueForItemSet(result);
						if(m == 4){
							System.out.println(newSupportRate);
						}
						if(newSupportRate > this.sup_rate){
							ItemSet newItemSet = new ItemSet();
							newItemSet.addAll(result);
							newItemSet.setSupp(newSupportRate);
							newItemSet.addStringKVList(tmp);
							candidateItemSets.add(newItemSet);
						}
					}
					exists = false;
				}
			}
			if(m == 4){
				for(ItemSet it: candidateItemSets){
					System.out.println(it.getItemSetKeyValuesList().toString());
				}
			}
			tmpItemSet = this.GenerateFrequentItemSet(candidateItemSets,m);
			this.frequentItemSetMap.put(m++, new ArrayList<ItemSet>(tmpItemSet));
			frequentItemSets.addAll(new ArrayList<ItemSet>(tmpItemSet));
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
			System.out.println(iSet.getItemSetKeyValuesList().toString());
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
				Set<String> s = new HashSet<>();
				s.add(kv.getKeyValueString());
				tmpItemSet = new ItemSet();
				tmpItemSet.add(kv);
				tmpItemSet.setSupp(freq_rate);
				tmpItemSet.addStringKVList(s);
				firstItemSet.add(tmpItemSet);
			}
		}
		return firstItemSet;
	}
	private List<ItemSet> PruneKItemSet(List<ItemSet> lstKItemSet, int K)
	{   
		List<ItemSet> result = new ArrayList<>();
		if(K > 2){
			List<ItemSet> frequentK_1ItemSet = this.frequentItemSetMap.get(K - 1);
			for(ItemSet iSet: lstKItemSet){
				int count = 0;
				for(ItemSet fSet: frequentK_1ItemSet){
					if(iSet.getItemSetKeyValuesList().containsAll(fSet.getItemSetKeyValuesList())){
						count++;
					}
				}
				if(count == K){
					result.add(iSet);
				}
			}
			return result;
		}
		return lstKItemSet;
		
	}
	private List<ItemSet> GenerateFrequentItemSet(List<ItemSet> lstItemSet, int K){
		
		return this.PruneKItemSet(lstItemSet, K);
	}
	private boolean IsItemSetExists(List<ItemSet> lstKLevelItemSet, List<KeyValue> lstKeyValues){
		return true;
	}
	private Set<String> JoinItemSet(int K, Set<String> itemsOutter, Set<String> itemsInner){
		Set<String> outter = new HashSet<String>(itemsOutter);
		Set<String> inner = new HashSet<String>(itemsInner);
		Set<String> kv = new HashSet<>();
		if(K == 2){
			for(String str_out: itemsOutter){
				for(String str_in: itemsInner){
					if(!str_out.startsWith(str_in.substring(0, 1))){
						kv.add(str_in);
						kv.add(str_out);
					}
				}
			}
		}else{

			int size = outter.size();
			Set<String> tmp = new HashSet<>(outter);
			outter.retainAll(inner);
			if(outter.size() == size - 1){
				kv.addAll(tmp);
				kv.addAll(inner);
				List<String> lTmp = new ArrayList<String>();
				lTmp.addAll(kv);
				Collections.sort(lTmp, String.CASE_INSENSITIVE_ORDER);
				String r ="";
				for(String str: lTmp){
					if(str.length() > 0 && r.length() > 0 ){
						if(str.startsWith(r.substring(0, 1))){
							return new HashSet<String>();
						}
					}
					r = str;
				}
				kv.clear();
				kv.addAll(lTmp);
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
	private List<KeyValue> buildKeyValueListFromString(Set<String> kvStrings){
		List<KeyValue> result = new ArrayList<>();
		for(String str: kvStrings){
			for(KeyValue kv: this.items){
				if(kv.getKeyValueString().equals(str)){
					result.add(kv);
				}
			}
		}
		return result;
	}
}
