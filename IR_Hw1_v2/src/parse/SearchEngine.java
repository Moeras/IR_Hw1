package parse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

public class SearchEngine {
	
	//DB Connection Parameter
	private static MongoClient mongoClient;
	private static DB db;
	private static String DBIP = "localhost";
	private static int DBPort = 27017;
	private static String DBName = "IR_Hw1_TestDB";
	private static DBCollection collection_Dictionary = null;
	private static ArrayList<String> fileList;
	private static int Processing_DocId =0;
	private static int Processing_DoId_Position = 0;
	
	public static void main(String[] args){
		
		//先連結資料庫
		ConnectionMongoDB();
		
		//取得所有要建檔之檔案名稱(txt檔名)
		//GetAllFileName();
		
		//指定建檔之文件範圍進行建檔(Text Mining)
		//int min =0,max=fileList.size(); //檔案從1開始
		//Parse(min,max);
		
		String test_term = "真的假的";
		int test_docid = 77;
		int test_position = 78;
		
		DBQurry_updateInvertfile(test_term,test_docid,test_position);
	}
	public static void DBQurry_updateInvertfile(String Term,int DocId,int Position){
		//取得資料Dictionary辭典(collection == table) 
		try {
			mongoClient = new MongoClient(new ServerAddress("localhost", 27017));
			db = mongoClient.getDB("IR_Hw1_TestDB");
			collection_Dictionary = db.getCollection("Dictionary");
		}
		catch (UnknownHostException e){
			e.printStackTrace();
		}
		//該term已存在 要更新紀錄
		if(isTermExist(Term)){
			
			// 更新termfrequency
	 		if(Processing_DocId != DocId){
				BasicDBObject updateDocument = 
						new BasicDBObject().append("$inc", 
						new BasicDBObject().append("IDF",1)); 
		 		collection_Dictionary.update(new BasicDBObject().append("Term", Term), updateDocument);
	 		}
	 		
	 		// 更新invertedindexlist
	 		
	 		//若這個term從來沒有建過這個document資料 建立新的InvertedFileIndex
	 		if(!isTermsDocumentExist(Term,DocId)){
	 			System.out.println("若這個term從來沒有建過這個document資料 建立新的InvertedFileIndex");
	 			
	 			ArrayList position_array = new ArrayList();
				position_array.add(Position);
				
				DBObject Insert_Document = new BasicDBObject("InvertedFileList",
						new BasicDBObject("DocID",DocId).append("LocalFrequency",1).append("Position",position_array));
				
				DBObject updateQuery = new BasicDBObject("Term", Term);
				DBObject updateCommand = new BasicDBObject("$push", Insert_Document);
				collection_Dictionary.update(updateQuery,updateCommand);
				
	 		}
	 		 //這個term已經建過這個Document的資料的 只需要更新其invertedfile即可
	 		else{
	 			System.out.println("//這個term已經建過這個Document的資料的 只需要更新其invertedfile即可");
	 			
	 			//更新InvertedFileList 的Position 
				BasicDBObject updateQuery = new BasicDBObject("Term",Term);
				updateQuery.put("InvertedFileList.DocID",DocId);
		 		BasicDBObject updateCommand = new BasicDBObject("$push", new BasicDBObject("InvertedFileList.$.Position",Position));
				collection_Dictionary.update(updateQuery,updateCommand);
				
				//更新InvertedFileList 的Frequency
				BasicDBObject updateQuery_Frequency = new BasicDBObject("Term",Term);
				updateQuery_Frequency.put("InvertedFileList.DocID",DocId);
				BasicDBObject updateDocument = 
						new BasicDBObject().append("$inc", 
						new BasicDBObject().append("InvertedFileList.$.LocalFrequency",1)); 
		 		collection_Dictionary.update(updateQuery_Frequency,updateDocument);
				
	 		}
		}
		//此Term沒有建檔過，需要建立新的詞項於Dictionary
		else{
			System.out.println("此Term沒有建檔過，需要建立新的詞項於Dictionary");
			
			ArrayList position_array = new ArrayList();
			position_array.add(Position);
			
			ArrayList newInnerInnerDoc = new ArrayList();
			newInnerInnerDoc.add(new BasicDBObject("DocID",DocId).
					append("LocalFrequency", 1).
					append("Position", position_array));
			
			BasicDBObject newDocument = new BasicDBObject();
			newDocument.put("Term",Term);
			newDocument.put("IDF",1);
			newDocument.put("InvertedFileList",newInnerInnerDoc);
			
			collection_Dictionary.insert(newDocument);
			
		}
	}
	
	//判斷該term是否曾經記錄過這個Document
	public static boolean isTermsDocumentExist(String checkterm,int checkdocid){
		BasicDBObject allQuery = new BasicDBObject();
		BasicDBObject fields = new BasicDBObject();
		allQuery.put("Term", checkterm);
		allQuery.put("InvertedFileList.DocID",checkdocid);
	 	DBCursor cursor = collection_Dictionary.find(allQuery, fields);
	 	System.out.println(cursor.size());
	 	if(cursor.size()==0){
	 		return false;
	 	}else{
	 		return true;
	 	}
	}
	
	//判斷該term是否建立在辭典過 
	public static boolean isTermExist(String checkterm){
		
		BasicDBObject allQuery = new BasicDBObject();
		BasicDBObject fields = new BasicDBObject();
		allQuery.put("Term", checkterm);
	 	DBCursor cursor = collection_Dictionary.find(allQuery, fields);
	 	System.out.println(cursor.size());
	 	if(cursor.size()==0){
	 		return false;
	 	}else{
	 		return true;
	 	}
	 	
	}
	
	
	//一行一行進行詞條化 將之變成一個一個可以儲存的Token詞項
	public static void Tokenziation_Line(String Line,int DocId){
		String[] tokens = Line.split(" ");
		for(int i=0;i<tokens.length;i++){
			tokens[i] = tokens[i].trim();
			tokens[i] = tokens[i].replaceAll("[\\pP|~|$|^|<|>|\\||\\+|=]*", "");
			tokens[i] = tokens[i].replaceAll("\\s+", "");
			tokens[i] = tokens[i].replaceAll("'","");
			if(!tokens[i].isEmpty() && !tokens[i].equals("�� ")){
				
				//判斷token為數字還是非數字
				if(isNumeric(tokens[i])){
	        		//System.out.println("哈哈哈我是數字: "+tokens[i]);
	        	}
	        	else if(!isNumeric(tokens[i])){
					if(isAlpha(tokens[i])){
						//System.out.println("我是純英文字: "+tokens[i]);
						String lowercasestring = CaseFolding(tokens[i]); //大小寫轉換 全部轉成小寫
						
						//if(!lowercasestring.equals(tokens[i])){
							//System.out.println("改過的大小寫單字： "+lowercasestring);
						//}
						
						//使用Porter演算法進行詞幹還原 stemming ->lemma(詞元)
						if(PorterStemming_1(lowercasestring)){
							String lemma = PorterStemming_2(lowercasestring);
							System.out.println("lemma： "+lemma);
							System.out.println("建檔,純英文字: "+lemma);
							DBQurry_updateInvertfile(lemma,DocId,Processing_DoId_Position);
							Processing_DoId_Position++;
						}
						else{
							System.out.println("此單字不會納入資料庫： "+lowercasestring);
						}
					}
					else{
						//可能是中文數字特殊符號交雜之句子  
						//針對中文句子與非純英文、非純數字句子以固定長度進行句子切割
						
						//CutSentenceInFitLength(tokens[i],1,DocId);  
						//CutSentenceInFitLength(tokens[i],2,DocId);
						CutSentenceInFitLength(tokens[i],10,DocId); 
					}
	        	}
	        	else{
	        		System.out.println("此單字不會納入資料庫：我甚麼都不是："+tokens[i]);
	        	}
			}
			else{
				System.out.println("此單字不會納入資料庫：我是蝦密："+tokens[i]);
			}
        }
		
	}
	
	//將中英文常句子切除一個
	public static void CutSentenceInFitLength(String sentence,int fitlong,int DocId){
		
		int begin =0;
		while(sentence.length()>0){
			if(sentence.length() < fitlong){
				DBQurry_updateInvertfile(sentence,DocId,Processing_DoId_Position);
				Processing_DoId_Position++;
				System.out.println("建檔,切過的喔: "+sentence);
				return;
			}else{
				String subsentence = "";
				subsentence = sentence.substring(0,fitlong);
				sentence =  sentence.substring(fitlong,sentence.length());
				DBQurry_updateInvertfile(sentence,DocId,Processing_DoId_Position);
				Processing_DoId_Position++;
				System.out.println("建檔,切過的喔: "+subsentence);
			}
		}
	}
	
	//PorterStemming
	public static boolean PorterStemming_1(String originalterm){
		
		Porter porterstemming = new Porter();
		if(!porterstemming.containsVowel(originalterm))return false;
		
		return true;
	}
	
	//PorterStemming
	public static String PorterStemming_2(String originalterm){
		
		Porter porterstemming = new Porter();
		String lemmatemp="";
		NewString str = new NewString();
		
		lemmatemp = porterstemming.Clean(originalterm); //清除『'』等奇怪字元
		if(porterstemming.hasSuffix(originalterm,"ing",str)){   //去除ing 特定字元
			lemmatemp = str.str;
		}
		String s1 = porterstemming.step1(lemmatemp);
        String s2 = porterstemming.step2(s1);
        String s3= porterstemming.step3(s2);
        String s4= porterstemming.step4(s3);
        String s5= porterstemming.step5(s4);
        lemmatemp = s5;
		//System.out.println(lemmatemp);
		return lemmatemp;
	}
	
	//全部改為小寫
	public static String CaseFolding(String englishterm){
		return englishterm.toLowerCase();
	}
	
	//判斷是否為純英文單字 沒有數字中文等夾雜其中
	public static boolean isAlpha(String name) {
		return name.matches("[a-zA-Z]+");
	}
	
	//是否為數字
	public static boolean isNumeric(String str){
		
		NumberFormat formatter = NumberFormat.getInstance();
		ParsePosition pos = new ParsePosition(0);
		formatter.parse(str, pos);
		return str.length() == pos.getIndex();
		
	}
	
	//讀完檔 並且依據一行一行讀取 並開始切除token
	public static void ReadAnFile(String docname,int docid){
		
		String path = "C:/Users/Vicky/Desktop/resource_raw_data/"+docname;
		System.out.println("\t***Start Parse DocumnetNo:***"+docid);
		try{              
            FileInputStream fis = new FileInputStream(path);  
            byte[] lineb = new byte[5000];  //讀取單位大小
            int rc = 0;  
            while((rc = fis.read(lineb))> 0){  
                String utf8_line = new String(lineb,"UTF-8");  //以UTF-8為預設編碼
                //System.out.println(utf8_line); 
                Tokenziation_Line(utf8_line,docid);//詞條化
                lineb = new byte[5000]; //清除資訊 以防止舊資料重復紀錄
            }  
            fis.close();  
        }catch(FileNotFoundException e){  
            e.printStackTrace();  
        }catch(IOException ioe){  
            ioe.printStackTrace();  
        }  
		System.out.println("\t***End Parse***");
		
	}
	
	//讀取選取的檔案個數
	public static void Parse(int min,int max){
		
		String document_name = "";
		int document_id = 0;
		for(int i=min;i<max;i++){
			document_name=fileList.get(i);
			System.out.println("Parse : No："+i+" Name: "+document_name);
			document_id = i+1;
			Processing_DocId = document_id;
			Processing_DoId_Position = 1;
			ReadAnFile(document_name,document_id);
		}
	}
		
	//讀取資料夾中所有的檔案名稱
	public static void GetAllFileName(){
		//File f = new File("C:/Users/Vicky/Desktop/Information Retrieval/hw1dataset30k/Data");  //
		File f = new File("C:/Users/Vicky/Desktop/resource_raw_data"); //測試用
		fileList = new ArrayList<String>();
		if(f.isDirectory()){
			System.out.println("filename : "+f.getName());//印出我們所讀到的資料夾
			String []s=f.list(); //宣告一個list
			System.out.println("size : "+s.length);//印出資料夾裡的檔案個數
			for(int i=0;i<s.length;i++){
                //System.out.println(s[i]);
                fileList.add(s[i]); //將檔名一一存到fileList動態陣列裡面
            }
		}
		
		
				
	}
	
	//連結MongoDB
	public static void ConnectionMongoDB(){
		try {
			mongoClient = new MongoClient(new ServerAddress("localhost", 27017));
			db = mongoClient.getDB("IR_Hw1_TestDB");
			collection_Dictionary = db.getCollection("Dictionary");
		}
		catch (UnknownHostException e){
			e.printStackTrace();
		}
	}
}
