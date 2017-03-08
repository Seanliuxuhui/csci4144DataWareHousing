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
	private List<ItemSet> freqItemSets = null;
	private Map<Integer, List<Set<String>>> frequentItemSetStringMap = new HashMap<>();
	private Map<Set<String>, ItemSet> contentItemSetMap = new HashMap<>();
	private ItemSet fset;
	private List<Set<String>> freqK_1ItemSet = null;
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
		
		//3. with first itemset we continue to run generate frequent itemset until some condition are met.
		
		freqItemSets = firstItemSet;
		while(freqItemSets.size() > 1){
			List<ItemSet> tmpItemSets = new ArrayList<>();
			List<Set<String>> tmpStringSet = new ArrayList<>();
			this.freqK_1ItemSet = this.frequentItemSetStringMap.get(m - 1);
			for(int i = 0; i < freqItemSets.size() - 1; i++){
				ItemSet iItemSet = freqItemSets.get(i);
				for(int j = i + 1; j < freqItemSets.size(); j++){
					ItemSet jItemSet = freqItemSets.get(j);
					Set<String> tmp = JoinItemSet(m, iItemSet.getItemSetKeyValuesList(), jItemSet.getItemSetKeyValuesList());
					if(tmp.size() == m){
						if(this.subsetExists(tmp, m) && !tmpStringSet.contains(tmp)){
							List<KeyValue> result = this.buildKeyValueListFromString(tmp);
							double newSupportRate = this.GetSupportValueForItemSet(result);
							if(newSupportRate > this.sup_rate){
								ItemSet newItemSet = new ItemSet(result, tmp, newSupportRate, 0.0);
								tmpItemSets.add(newItemSet);
								tmpStringSet.add(tmp);
							}
						};
					}
				}
			}
			freqItemSets = tmpItemSets;
			this.addItemSets(freqItemSets);
			this.addFreqItemSetString(freqItemSets, m++);
			frequentItemSets.addAll(freqItemSets);
			
		}
		
//		this.printFreqItemSet(frequentItemSets);
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
				tmpItemSet = new ItemSet(tmp, s, freq_rate, 0);
				firstItemSet.add(tmpItemSet);
			}
		}
		return firstItemSet;
	}
	
	/**
	 * subset the given string set (represent the items in string format) and compare to the existing k-1 frequent set to check whether 
	 * the subset of the candidate set is frequent element or infrequent element.
	 * @param s
	 * @param K
	 * @return
	 */
	private boolean subsetExists(Set<String> s, int K){
		if(K > 2) {
			int count = 0;
			for(Set<String> key: this.freqK_1ItemSet){
				if(s.containsAll(key)){
					count++;
				}
			}
			
			if(count == K){
				return true;
			}else{
				return false;
			}
		}else{
			return true;
		}
	}
	
	/**
	 * join two sets together if them have the k-2 same elements
	 * @param K
	 * @param itemsOutter
	 * @param itemsInner
	 * @return
	 */
	
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
	
	
	/**
	 * function of copying the frequent Itemsets' string value to a list. The itemset is organized from the complex itemset to simplest itemset
	 * @return
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
	
	
	/**
	 * subset the frequent item set. it only exclude one element from the parent frequent itemset for each iteration. 
	 * @param freqItemSet
	 * @return
	 */
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
	
	/**
	 * generating the strongest rules. This part involves the implementation of rules pruning. 
	 * Here, queue data structure is used to facilitate the rule pruning idea. 
	 * The general idea of pruning algorithm here is that for a given frequent item set,  it generates k - 1 length of sub item sets at each iteration. 
	 * These sub item sets are then compared with min_confidence, only the item set whose confidence rate will be added into the queue for next iteration
	 * of subsetting.  
	 * @param frequentItemSet
	 * @return
	 */
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
	/**
	 * check whether itemset exists in the queue or not. 
	 * @param subset
	 * @param newItemSet
	 * @return
	 */
	private boolean checkIfItemSetExisted(Queue<HashSet<String>> subset, HashSet<String> newItemSet){
		for(HashSet<String> s: subset){
			if(newItemSet.containsAll(s)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * adding the subset of frequent itemset into queue
	 * @param subset
	 * @param elems
	 * @return
	 */
	private Queue<HashSet<String>> addNewElemIntoSubset(Queue<HashSet<String>> subset, List<HashSet<String>> elems){
		for(HashSet<String> elem: elems){
			if(!this.checkIfItemSetExisted(subset, elem)){
				subset.add(elem);
			}
		}
		return subset;
	}
	
	/**
	 * build the KeyValue object list by the given item string
	 * @param kvStrings
	 * @return
	 */
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
	
	/**
	 * sort items in the itemset by their numerical number
	 * @param kvList
	 * @return
	 */
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
	
	/**
	 * print the KeyValue pair 
	 * @param kvList
	 */
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
	
	/**
	 * print the Frequent itemset for testing
	 * @param freqItemSet
	 */
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
	
	/**
	 * count the occurrences of item unions from the database
	 * @param kvList
	 * @return
	 */
	private double GetSupportValueForItemSet(List<KeyValue> kvList){
		double freq = db.countLiteral(kvList) * 1.0;
		double freq_rate = BigDecimal.valueOf(freq/db.RowCount()).setScale(2, RoundingMode.HALF_UP).doubleValue();
		return freq_rate ;
	}
	
	/**
	 * used to check whether the given items set exists or not
	 * @param items
	 * @return
	 */
	private ItemSet findItemSetWithItemSetValue(Set<String> items){
		if(this.contentItemSetMap.containsKey(items)){
			return this.contentItemSetMap.get(items);
		}
		return null;
	}
	
	/**
	 * construct a list of ItemSet objects by the given itemset strings
	 * @param iSets
	 * @return
	 */
	private List<ItemSet> findItemsetsWithItemSetStringValue(List<Set<String>> iSets){
		List<ItemSet> results = new ArrayList<>();
		for(Set<String> s: iSets){
			results.add(this.findItemSetWithItemSetValue(s));
		}
		return results;
	}
	
	/**
	 * add a list of itemset into the itemset string map object
	 * @param iSets
	 */
	private void addItemSets(List<ItemSet> iSets){
		for(ItemSet iSet: iSets){
			this.addItemSetIntoContentItemSetMap(iSet);
		}
	}
	
	/**
	 * add one itemset intot hte itemset string map object
	 * @param iSet
	 */
	private void addItemSetIntoContentItemSetMap(ItemSet iSet){
		this.contentItemSetMap.put(iSet.getItemSetKeyValuesList(), iSet);
	}
	
	
	/**
	 * add frequent itemset string into a list, differs from the stringmap which is not a list. 
	 * it is for the coding convenience
	 * @param iSets
	 * @param K
	 */
	private void addFreqItemSetString(List<ItemSet> iSets, int K){
		List<Set<String>> freqSets = new ArrayList<>();
		for(ItemSet iset: iSets){
			freqSets.add(iset.getItemSetKeyValuesList());
		}
		this.frequentItemSetStringMap.put(K, freqSets);
	}
}
