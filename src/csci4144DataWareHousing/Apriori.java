package csci4144DataWareHousing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

public class Apriori {

	/**
	 * private property initialization 
	 */
	private Database db;
	private List<KeyValue> items;
	private double sup_rate;
	private double conf_rate;
	private List<ItemSet> frequentItemSets = new ArrayList<>();
	private List<ItemSet> candidateItemSets = null;
	private Map<Integer, List<Set<String>>> frequentItemSetStringMap = new HashMap<>();
	private Map<Set<String>, ItemSet> contentItemSetMap = new HashMap<>();
	private ItemSet fset;
	public Apriori(Database db, double support, double confidence){
		this.db = db;
		this.sup_rate = support;
		this.conf_rate = confidence;
	}
	
	/**
	 * main function to generate the frequent item set and rules
	 */
	public List<ItemSet> RunApriori(){
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
			this.addFreqItemSetString(firstItemSet, 1);
			this.addItemSets(this.frequentItemSets);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
					if(tmp.size() == m){
						List<KeyValue> result = this.buildKeyValueListFromString(tmp);
						double newSupportRate = this.GetSupportValueForItemSet(result);
						if(newSupportRate > this.sup_rate){
							ItemSet newItemSet = new ItemSet();
							newItemSet.addAll(result);
							newItemSet.setSupp(newSupportRate);
							newItemSet.addStringKVList(tmp);
							candidateItemSets.add(newItemSet);
						}
					}
				}
			}
			tmpItemSet = this.GenerateFrequentItemSet(candidateItemSets,m);
			this.addItemSets(tmpItemSet);
			this.addFreqItemSetString(tmpItemSet, m++);
			frequentItemSets.addAll(new ArrayList<ItemSet>(tmpItemSet));
		}
		
		this.printFreqItemSet(frequentItemSets);
		return this.frequentItemSets;
		
	}
	
	// section for frequent item set generation
	
	
	/**
	 * generate the first item set
	 * @return
	 * @throws Exception
	 */
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
		Map<HashSet<String>, ItemSet> resultMap = new HashMap<HashSet<String>, ItemSet>(); 
		List<ItemSet> result = new ArrayList<>();
		if(K > 2){
			List<Set<String>> frequentK_1ItemSet = this.frequentItemSetStringMap.get(K - 1);
			for(ItemSet iSet: lstKItemSet){
				int count = 0;
				
				for(Set<String> key: frequentK_1ItemSet){
					if(iSet.getItemSetKeyValuesList().containsAll(key)){
						count++;
					}
				}
				if(count == K){
					resultMap.put((HashSet<String>) iSet.getItemSetKeyValuesList(), iSet);
					count = 0;
				}
			}
			for(ItemSet iset: resultMap.values()){
				result.add(iset);
			}
			return result;
		}
		return lstKItemSet;
		
	}
	private List<ItemSet> GenerateFrequentItemSet(List<ItemSet> lstItemSet, int K){
		
		return this.PruneKItemSet(lstItemSet, K);
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
	
	/**
	 * section of rules generation 
	 */
	
	public List<Rules> ProcessRules(){
		List<ItemSet> frequentKItemSet = new ArrayList<ItemSet>();
		for(int i = this.frequentItemSetStringMap.keySet().size(); i > 1; i--){
			if(!this.frequentItemSetStringMap.get(i).isEmpty()){
				frequentKItemSet.addAll(this.findItemsetsWithItemSetStringValue(this.frequentItemSetStringMap.get(i)));
			}
		}

		return this.GenerateRules(frequentKItemSet);
	}
	
	public void GenerateRulesSets(List<Rules> lstRules, List<KeyValue> lstKeyValue){
		
	}
	
	private List<HashSet<String>> SubsetFrequentItemSet(Set<String> freqItemSet){
		List<HashSet<String>> ans = new ArrayList<HashSet<String>>();
		if(freqItemSet.size() > 1){
			HashSet<String> oneElem = null;
			HashSet<String> restElems = null;
			for(String str: freqItemSet){
				restElems = new HashSet<String>(freqItemSet);
				oneElem = new HashSet<String>();
				oneElem.add(str);
				restElems.removeAll(oneElem);
				ans.add(restElems);
				
			}
		}
		return ans;
	}
	
	private List<Rules> GenerateRules(List<ItemSet> frequentItemSet){
		List<Rules> rules = new ArrayList<Rules>(); 
		Queue<HashSet<String>> subset = new LinkedList<HashSet<String>>();
		Set<String> failedItem = new HashSet<>();
		for(ItemSet fset: frequentItemSet){
			subset = this.addNewElemIntoSubset(subset, this.SubsetFrequentItemSet(fset.getItemSetKeyValuesList()));
			while(subset.size() > 0){
				Set<String> bigSet = subset.remove();
				ItemSet restElemItemSet = this.findItemSetWithItemSetValue(bigSet);
				if(restElemItemSet != null && (failedItem.size() == 0 ||!bigSet.containsAll(failedItem))){
					double conf_calculated = BigDecimal.valueOf(fset.getSupp() / restElemItemSet.getSupp()).setScale(2, RoundingMode.HALF_UP).doubleValue();
					if(conf_calculated > this.conf_rate){
						Set<String> ss = new HashSet<String>(fset.getItemSetKeyValuesList());
						ss.removeAll(bigSet);
						Rules newrule = new Rules(fset.getSupp(), conf_calculated, restElemItemSet, this.findItemSetWithItemSetValue(ss));
						rules.add(newrule);
						subset = this.addNewElemIntoSubset(subset, this.SubsetFrequentItemSet(bigSet));
					}else{
						failedItem.addAll(bigSet);
					}
				}
			}
			failedItem = new HashSet<>();
		}
		
		return rules;
	}
	
	
	
//	 helper functions 
	private boolean checkIfItemSetExisted(Queue<HashSet<String>> subset, HashSet<String> newItemSet){
		for(HashSet<String> s: subset){
			if(newItemSet.containsAll(s)){
				return true;
			}
		}
		return false;
	}
	
	private Queue<HashSet<String>> addNewElemIntoSubset(Queue<HashSet<String>> subset, List<HashSet<String>> elems){
		for(HashSet<String> elem: elems){
			if(!this.checkIfItemSetExisted(subset, elem)){
				subset.add(elem);
			}
		}
		return subset;
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
	
	private void printKVList(List<ArrayList<KeyValue>> kvList){
		for(int i = 0; i < kvList.size(); i++){
			if(!kvList.get(i).isEmpty()){
				System.out.print("{");
				for(KeyValue kv: kvList.get(i)){
					System.out.println("label: " + kv.getKey() + " value: " + kv.getValue());
				}
				System.out.println("}");
			}
		}
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
	
	private double GetSupportValueForItemSet(List<KeyValue> kvList){
		double freq = db.countLiteral(kvList) * 1.0;
		double freq_rate = BigDecimal.valueOf(freq/db.RowCount()).setScale(2, RoundingMode.HALF_UP).doubleValue();
		return freq_rate ;
	}
	
	private ItemSet findItemSetWithItemSetValue(Set<String> items){
		if(this.contentItemSetMap.containsKey(items)){
			return this.contentItemSetMap.get(items);
		}
		return null;
	}
	
	private List<ItemSet> findItemsetsWithItemSetStringValue(List<Set<String>> iSets){
		List<ItemSet> results = new ArrayList<>();
		for(Set<String> s: iSets){
			results.add(this.findItemSetWithItemSetValue(s));
		}
		return results;
	}
	
	private void addItemSets(List<ItemSet> iSets){
		for(ItemSet iSet: iSets){
			this.addItemSetIntoContentItemSetMap(iSet);
		}
	}
	private void addItemSetIntoContentItemSetMap(ItemSet iSet){
		this.contentItemSetMap.put(iSet.getItemSetKeyValuesList(), iSet);
	}
	
	private void addFreqItemSetString(List<ItemSet> iSets, int K){
		List<Set<String>> freqSets = new ArrayList<>();
		for(ItemSet iset: iSets){
			freqSets.add(iset.getItemSetKeyValuesList());
		}
		this.frequentItemSetStringMap.put(K, freqSets);
	}
}
