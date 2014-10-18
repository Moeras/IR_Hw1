package search;

import java.net.UnknownHostException;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

public class Search {
	private static MongoClient mongoClient;
	public static DB[] db = new DB[100];
	private static String DBIP = "localhost";
	private static DBCollection collection_DictionaryCh;
	private static String Hw ="IR_Hw1";
	public static String[] Location = {"NBDB"};
	private static int[] DBNobegin={53};
	private static int[] DBSum = {53};
	//public static String[] location = {"LabDB","NBDB","KiethDB","MARTINDB","AlanDB","JonnyDB"};
	//private static int[] DBSum = {100,100,100,100,100,100};
	private static String[] LetterUPAll ={"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	private static String[] LetterLOAll ={"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
	public static DBCollection[]  collection_DictionaryEn = new DBCollection[27];
	
	public static void main(String[] args){
		//先連結資料庫
		ConnectionMongoDB();
	}
	
    //確認DB編號 以建立DB[] 
	public static int CheckNumofDB(int location,int DBNo){
		if(location == 0)return DBNo;
		if(location ==1) return DBSum[0]+DBNo;
		if(location ==2) return DBSum[0]+DBSum[1]+DBNo;
		if(location ==3) return DBSum[0]+DBSum[1]+DBSum[2]+DBNo;
		if(location ==4) return DBSum[0]+DBSum[1]+DBSum[2]+DBSum[3]+DBNo;
		if(location ==5) return DBSum[0]+DBSum[1]+DBSum[2]+DBSum[3]+DBSum[4]+DBNo;
		else return DBNo;
	}
	
	//為DB建立雜湊索引 雜湊索引 hashindex
	private static void CreateHashIndex(DB db) {
		DBObject index2d = BasicDBObjectBuilder.start("Term", "hashed").get(); 
		for(int i=0;i<26;i++){
			collection_DictionaryEn[i].ensureIndex(index2d);
		}
		collection_DictionaryCh.ensureIndex(index2d);
	}
	public static void ConnectionMongoDB(){
		String dbpath ;//= Hw+"_"+Location[j]+"_"+Integer.toString(i);
		String collectionpath="";
		int dbno=0;
		try {
			mongoClient = new MongoClient(new ServerAddress("localhost", 27017));
			for(int j=0; j<Location.length;j++){
				for(int i=DBNobegin[j];i<=DBSum[j];i++){
					//初始化DB
					dbpath = Hw+"_"+Location[j]+"_"+Integer.toString(i);
					dbno = CheckNumofDB(j,i);
					db[dbno] = mongoClient.getDB(dbpath);
					
					//初始化collection:DictionaryEnA~DictionaryEnZ
					for(int k =0;k<26;k++){
						collectionpath = "DictionaryEn"+LetterUPAll[k];
						collection_DictionaryEn[k] = db[dbno].getCollection(collectionpath);
					}
					
					//初始化collection:DictionaryCh
					collection_DictionaryCh = db[dbno].getCollection("DictionaryCh");
					CreateHashIndex(db[dbno]);
				}
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.out.println("db：");
		}
		
	}
	
}
