package test;

import java.io.File;
import java.net.UnknownHostException;
import java.util.List;

import com.mongodb.DB;
import com.mongodb.MongoClient;


public class TestForTokenziation {
	private static String DBIP = "7.67.121.193";
	private static int DBPort = 27017;
	private static String DBName = "irmongodb";
	private static DB db;
	private static MongoClient mongoClient;
	public static List<String> allDBList;
	
	
	public static void main(String[] args){
		ConnectionToMongoDB();
		GetDatabaseName(); 
		GetFileNameForDataPrepocess();
	}
	
	public static void GetFileNameForDataPrepocess(){
		File f = new File("");
		
		
	}
	public static void ConnectionToMongoDB(){
		try{
			//mongoClient = new MongoClient("localhost",DBPort);
			mongoClient = new MongoClient(DBIP,DBPort);
			System.out.println("連線成功到 :" + DBIP +":"+DBPort+ "\n\tDBName: "+DBName+"\n");
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
			System.out.println("失敗連線到 :" + DBIP +":"+DBPort+ "\n\tDBName: "+DBName+"\n"+"建議在試一次!!");
		}
		
	}
	public static void GetDatabaseName(){  //得到Database 之 NameList
		allDBList = mongoClient.getDatabaseNames(); 
		for(String s:allDBList){
			System.out.println(s);
		}
	}
}