package csci4144DataWareHousing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
			int i = 0;
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
	
	public List<String> GetColumnDistinctValues(int iColumnIndex){
//		String[] r = (String[]) labelMap.get(dbContent.get(0).toArray()[iColumnIndex]).toArray();
		List<String> result = new ArrayList<>();
		result.addAll(labelMap.get(dbContent.get(0).toArray()[iColumnIndex]));
//		for(String d: r){
//			result.add
//		}
		return result;
	}
}
