package csci4144DataWareHousing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Database {
	private List<List<String>> dbContent = new ArrayList<>();
	private Map<String, List<String>> labelMap = new HashMap<>();
	public Database(String strFileName) throws FileNotFoundException, IOException{
		loadData(strFileName);
	}
	/**
	 * load the data into memory
	 * @param stringFile
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private void loadData(String strFileName) throws FileNotFoundException, IOException{
		List<String> s = null;
		try(BufferedReader  br = new BufferedReader (new FileReader(strFileName))){
			String line = br.readLine();
			while(line != null){
				if(line.length()>0){
					String[] words = line.split(" ");
					s = new ArrayList<String>();
					for(int j = 0; j < words.length; j++){
						if(!words[j].isEmpty()){
							s.add(words[j]);
						}
					}
					dbContent.add(s);
				}
				line = br.readLine();
			}
			br.close();
		}
		
		//get attribute names of this dataset
		List<String> labels = dbContent.get(0);
		
		for(String label: labels){
			labelMap.put(label, new ArrayList<String>());
		}
		
		List<String> vals = null;
		
		for(int i = 1; i < dbContent.size(); i++){
			vals = new ArrayList<String>();
			vals.addAll(dbContent.get(i));
			for(int j = 0; j < vals.size() ; j++){
				List<String> t = labelMap.get(labels.get(j)); 
				if(!t.contains(vals.get(j))){
					t.add(vals.get(j));
					labelMap.put(labels.get(j), t);
				}
			}
		}
	}
	
	public int RowCount(){
		return dbContent.size() - 1;
	}
	public List<String> GetColumnDistinctValues(String labelName){
		return labelMap.get(labelName);
	}
	
	public String getLabelName(int iColumnIndex){
		return dbContent.get(0).get(iColumnIndex);
	}
	
	public int getlabelSize(){
		return dbContent.get(0).size();
	}
	
	public List<String> getLabelNames(){
		return dbContent.get(0);
	}
	public int countLiteral(List<KeyValue> kvList){
		boolean found = false;
		
		int count = 0;
		
		for(int i = 1; i < dbContent.size(); i++){
			List<String> item = dbContent.get(i);
			for(KeyValue kv: kvList){
				if(item.contains(kv.getValue())){
					found = true;
				}else{
					found = false;
				}
			}
			if(found){
				count++;
			}
			found = false;
		}
		
		return count;
	}
}
