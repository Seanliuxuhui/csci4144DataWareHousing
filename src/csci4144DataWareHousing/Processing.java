package csci4144DataWareHousing;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Processing {
	private Database db;
	private Apriori apr;
	public void Run(double dbSupport, double dbConfidence){
		//run apriori task
		apr = new Apriori(db, dbSupport, dbConfidence);
		apr.RunApriori();
		
		
	}
	public void PrintDataOnScreen(){
		
	}
	public void SaveRules(double dbSupport, double dbConfidence){
		
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
