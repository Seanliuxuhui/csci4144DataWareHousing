package csci4144DataWareHousing;

import java.util.Scanner;

public class Main {
	public static void main(String[] args){
		Processing p = new Processing();
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter Data File Name:");
		String strFileName = sc.nextLine();
		p.LoadDatabase(strFileName);
		//C:\Users\Liu\Ass3\src\Ass3\data1
		System.out.println("Enter Support Rate:");
		double dbSupport = sc.nextDouble();
		System.out.println("Enter Confidence Rate");
		double dbConfidence = sc.nextDouble();
		p.Run(dbSupport, dbConfidence);
	}
}
