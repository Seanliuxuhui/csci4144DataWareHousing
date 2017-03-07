package csci4144DataWareHousing;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Processing {
	private Database db;
	private Apriori apr;
	private List<ItemSet> freqItemSet = null;
	private List<Rules> rules = null;
	public void Run(double dbSupport, double dbConfidence){
		//run apriori task
		apr = new Apriori(db, dbSupport, dbConfidence);
		this.freqItemSet = apr.RunApriori();
	}
	public void PrintDataOnScreen(){
		System.out.println("Total rules found: " + this.rules.size());
		System.out.println("Total k Item ItemSets: " + this.freqItemSet.size() );
		System.out.println("Rules saved to file successfully");
	}
	public void SaveRules(double dbSupport, double dbConfidence){
		this.rules = apr.ProcessRules();
		try(BufferedWriter bw = new BufferedWriter(new FileWriter("Rules"))){
			StringBuffer buffer = new StringBuffer();
			
			String str = "Summary: \n";
			str += "Total rows in the original set: " + db.RowCount() + "\n";
			str += "Total rules discovered:" + this.rules.size() + "\n";
			str += "The selected measures: Support = " + dbSupport + " Confidence = " + dbConfidence + "\n";
			str += "----------------------------------------------------------------\n\n Rules:\n\n";
			buffer.append(str);
			for(int i = 0; i < rules.size(); i++){
				str = new String();
				Rules rule = rules.get(i);
				str = "Rules#" + i + ":";
				str += "(Support=" + rule.getSupp_rate() + ",";
				str += " Confidence=" + rule.getConf_rate() +")\n";
				str += "{ ";
				List<KeyValue> kvLists = rule.getPreItemSet().getItemSet();
				for(int j = 0 ; j < kvLists.size(); j++){
					KeyValue kv = kvLists.get(j);
					str += kv.getKey() + "=" + kv.getValue();
					if(j != kvLists.size() - 1){
						str += "  ";
					}
				}
				str += " }\n ---->";
				str += "{ ";
				kvLists = rule.getPostItemSet().getItemSet();
				for(int k = 0; k < kvLists.size(); k++){
					KeyValue kv = kvLists.get(k);
					str += kv.getKey() + "=" + kv.getValue();
					if(k != kvLists.size() - 1){
						str += "  ";
					}
				}
				str += " }\n\n";
				buffer.append(str);
			}
			
			
			bw.write(buffer.toString());
			
			this.PrintDataOnScreen();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public void LoadDatabase(String strFileName) {
		//initialize the database
		try{
			db = new Database(strFileName);
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
