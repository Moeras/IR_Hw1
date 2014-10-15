package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;


public class TestForTokenziation {
	private static String DBIP = "7.67.121.193";
	private static int DBPort = 27017;
	private static String DBName = "IRTestDB";
	public static List<String> allDBList;
	
	
	private static MongoClient mongoClient;
	private static DBCollection collection_Dictionary = null;
	private static DB db;
	
	
	public static void main(String[] args){
		//ConnectionToMongoDB();
		//GetDatabaseName(); 
		//GetFileNameForDataPrepocess();
		String Term = "apple";
		int DocId = 3;
		String DocName = "djfsldkjfsldjfslda.txt";
		int Position = 33;
		
		DBQurry_Invertfile(Term,DocId,DocName,Position);
		
		TestDBQurry_invertfile(99,"AMY"); //fileid,term
		//String enc = System.getProperty("file.encoding");
        //System.out.println(enc);
		
	}
	public static void DBQurry_Invertfile(String term,int docid,String docname,int position){
		
		//¨ú±o¸ê®ÆDictionaryÃã¨å(collection == table) 
		try {
			mongoClient = new MongoClient(new ServerAddress("localhost", 27017));
			db = mongoClient.getDB("IR_Hw1_TestDB");
			collection_Dictionary = db.getCollection("Dictionary");
		}
		catch (UnknownHostException e){
			e.printStackTrace();
		}
		if(isTermExist(term)){
			
		}
		
			 	
		
	}
	//§PÂ_¸Óterm¬O§_«Ø¥ß¦bÃã¨å¹L 
	public static boolean isTermExist(String checkterm){
		
		BasicDBObject allQuery = new BasicDBObject();
		BasicDBObject fields = new BasicDBObject();
		allQuery.put("term", checkterm);
	 	DBCursor cursor = collection_Dictionary.find(allQuery, fields);
	 	System.out.println(cursor.size());
	 	if(cursor.size()==0){
	 		return false;
	 	}else{
	 		return true;
	 	}
	 	
	}
	public static void TestDBQurry_invertfile(int fileid,String term){
		
		//¨ú±o¸ê®ÆDictionaryÃã¨å(collection == table) 
		MongoClient mongoClient;
		DBCollection collection_Dictionary = null;
		DB db;
		try {
			mongoClient = new MongoClient(new ServerAddress("localhost", 27017));
			db = mongoClient.getDB("IR_Hw1_TestDB");
			collection_Dictionary = db.getCollection("Dictionary");
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		//§PÂ_¸Óterm¬O§_«Ø¥ß¦bÃã¨å¹L 
		BasicDBObject allQuery = new BasicDBObject();
		BasicDBObject fields = new BasicDBObject();
		allQuery.put("term", term);
	 	DBCursor cursor = collection_Dictionary.find(allQuery, fields);
	 	System.out.println(cursor.size());
	 	
	 	//¨S¦³«Ø¥ß¸Ótermªºdocumentªº¬ö¿ý  create ¤@­Ó document ¨Ã¥B§ó·sinvertedindexlist 
	 	if(cursor.size()==0){
	 		ArrayList invertindex = new ArrayList();
			invertindex.add(fileid);
	 		BasicDBObject document = new BasicDBObject();
	 		document.put("term", term);
	 		document.put("termfrequency",1);
	 		document.put("invertedindexlist",invertindex);
	 		collection_Dictionary.insert(document);
	    }
	 	//¤w¸g«Ø¥ß©óDictionary¤¤ §ó·stermfrequency ©M  invertedindexlist
	 	else
	 	{
	 		// §ó·stermfrequency
	 		BasicDBObject newDocument = 
					new BasicDBObject().append("$inc", 
					new BasicDBObject().append("termfrequency",1)); 
	 		collection_Dictionary.update(new BasicDBObject().append("term", term), newDocument);
	 		// §ó·s invertedindexlist
	 		BasicDBObject updateQuery = new BasicDBObject("term",term);
	 		BasicDBObject updateCommand = new BasicDBObject("$push", new BasicDBObject("invertedindexlist",fileid));
			collection_Dictionary.update(updateQuery,updateCommand);
	 	}
	    //System.out.println(cursor.size());
	    //System.out.println(result.iterator().next());
	    
	    
		//§ó·s¤Ï¦V¯Á¤Þ°}¦C invertedindexlist,®Ú¾Útermµü¶µ·j´M,¦A±N¤å³¹ID¤©¥H¬ö¿ý
		//BasicDBObject updateQuery = new BasicDBObject("term",term);
		//BasicDBObject updateCommand = new BasicDBObject("$push", new BasicDBObject("invertedindexlist",fileid));
		//collection_Dictionary.update(updateQuery,updateCommand);
		//System.out.println(collection_Dictionary.findOne().toString());
		
		//updateQuery.put("invertedindexlist",fileid);
		
		//ArrayList invertindex = new ArrayList();
		//invertindex.add(fileid);
		
	}
	
	public static void ReadAnFile(String filename){   //¦¨¥\Åª¨ú³Ì¤jÀÉ®× ¦ý·|¥X²{ ? (¦³½s½X°ÝÃD¦s¦b)
		
		String path = "C:/Users/Vicky/Desktop/resource_raw_data/"+filename;
		System.out.println("\t***Using encoding***");
		try{              
            FileInputStream fis = new FileInputStream(path);  
            byte[] lineb = new byte[5000];  //Åª¨ú¨C¤@¦æªº¤j¤p
            int rc = 0;  
            StringBuffer sb= new StringBuffer("");  
            
            
            while((rc = fis.read(lineb))> 0){  
                String utf8_line = new String(lineb,"UTF-8");  //¥HUTF-8¬°¹w³]½s½X
                System.out.println(utf8_line); 
                lineb = new byte[5000];
            }  
            fis.close();  
        }catch(FileNotFoundException e){  
            e.printStackTrace();  
        }catch(IOException ioe){  
            ioe.printStackTrace();  
        }  
	}

	public static boolean isNumeric(String str)
	{
	  NumberFormat formatter = NumberFormat.getInstance();
	  ParsePosition pos = new ParsePosition(0);
	  formatter.parse(str, pos);
	  return str.length() == pos.getIndex();
	}
	public static boolean isAlpha(String name) {
		return name.matches("[a-zA-Z]+");
	}
	
	//¤Á°£line->tokens
	public static void Tokenziation(String line,int fileid){
		String[] tokens = line.split(" ");
		for(int i=0;i<tokens.length;i++){
			tokens[i] = tokens[i].trim();
			tokens[i] = tokens[i].replaceAll("[\\pP|~|$|^|<|>|\\||\\+|=]*", "");
			tokens[i] = tokens[i].replaceAll("\\s+", "");
			tokens[i] = tokens[i].replaceAll("'","");
			//tokens[i] = tokens[i].replaceAll("[\\s+|\\t+]*", "");
			
			
			//tokens[i] = tokens[i].replaceAll("\n\n+", "\n").replaceAll("^\n+", "");
			if(!tokens[i].isEmpty() && !tokens[i].equals("„Ï ")){
				
				if(isNumeric(tokens[i])){
	        		//System.out.println("«¢«¢«¢§Ú¬O¼Æ¦r: "+tokens[i]);
	        	}
	        	else if(!isNumeric(tokens[i])){
					if(isAlpha(tokens[i])){
						System.out.println("§Ú¬O¯Â­^¤å¦r: "+tokens[i]);
						String lowercasestring = CaseFolding(tokens[i]); //¤j¤p¼gÂà´« ¥þ³¡Âà¦¨¤p¼g
						if(!lowercasestring.equals(tokens[i])){
							System.out.println("§ï¹Lªº¤j¤p¼g³æ¦r¡G "+lowercasestring);
						}
						//¨Ï¥ÎPorterºtºâªk¶i¦æµü·FÁÙ­ì stemming ->lemma(µü¤¸)
						if(PorterStemming_1(lowercasestring)){
							String lemma = PorterStemming_2(lowercasestring);
							System.out.println("lemma¡G "+lemma);
						}
						else{
							System.out.println("¦¹³æ¦r¤£·|¯Ç¤J¸ê®Æ®w¡G "+lowercasestring);
						}
					}
					else{
						//¥i¯à¬O¤¤¤å¡A»Ý­n§âstopword®³±¼¶Ü? 
						//°w¹ï¤¤¤å¥y¤l»P«D¯Â­^¤å¡B«D¯Â¼Æ¦r¥y¤l¥H©T©wªø«×¶i¦æ¥y¤l¤Á³Î
						//CutSentenceInFitLength(tokens[i],1);  
						//CutSentenceInFitLength(tokens[i],2);
						CutSentenceInFitLength(tokens[i],10); 
					}
	        	}
	        	else{
	        		System.out.println("§Ú¬Æ»ò³£¤£¬O¡G"+tokens[i]);
	        	}
			}
			else{
				//System.out.println("§Ú¬O½¼±K¡G"+tokens[i]);
			}
        }
	}
	public static boolean PorterStemming_1(String originalterm){
		
		Porter porterstemming = new Porter();
		if(!porterstemming.containsVowel(originalterm))return false;
		
		return true;
	}
	public static String PorterStemming_2(String originalterm){
		Porter porterstemming = new Porter();
		String lemmatemp="";
		NewString str = new NewString();
		
		lemmatemp = porterstemming.Clean(originalterm); //²M°£¡y'¡zµ¥©_©Ç¦r¤¸
		if(porterstemming.hasSuffix(originalterm,"ing",str)){   //¥h°£ing ¯S©w¦r¤¸
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
	public static String CaseFolding(String englishterm){
		return englishterm.toLowerCase();
	}
	public static void CutSentenceInFitLength(String sentence,int fitlong){
		
		int begin =0;
		while(sentence.length()>0){
			if(sentence.length() < fitlong){
				System.out.println("¤Á¹Lªº³á: "+sentence);
				return;
			}else{
				String subsentence = "";
				subsentence = sentence.substring(0,fitlong);
				sentence =  sentence.substring(fitlong,sentence.length());
				System.out.println("¤Á¹Lªº³á: "+subsentence);
			}
		}
	}
	
	public static void ReadAnFileTest(String filename,int fileid){   //¦¨¥\Åª¨ú³Ì¤jÀÉ®× ¦ý·|¥X²{ ? (¦³½s½X°ÝÃD¦s¦b)
		
		String path = "C:/Users/Vicky/Desktop/resource_raw_data/"+filename;
		System.out.println("\t***Using encoding***");
		try{              
            FileInputStream fis = new FileInputStream(path);  
            byte[] lineb = new byte[5000];  //Åª¨ú³æ¦ì¤j¤p
            int rc = 0;  
            while((rc = fis.read(lineb))> 0){  
            	//String utf8_line = new String(lineb);
                String utf8_line = new String(lineb,"UTF-8");  //¥HUTF-8¬°¹w³]½s½X
                //System.out.println(utf8_line); 
                Tokenziation(utf8_line,fileid);//µü±ø¤Æ
                lineb = new byte[5000]; //²M°£¸ê°T ¥H¨¾¤îÂÂ¸ê®Æ­«´_¬ö¿ý
            }  
            fis.close();  
        }catch(FileNotFoundException e){  
            e.printStackTrace();  
        }catch(IOException ioe){  
            ioe.printStackTrace();  
        }  
	}
	public static void GetFileNameForDataPrepocess(){
		//File f = new File("C:/Users/Vicky/Desktop/Information Retrieval/hw1dataset30k/Data");  //
		File f = new File("C:/Users/Vicky/Desktop/resource_raw_data"); //´ú¸Õ¥Î
		ArrayList<String> fileList = new ArrayList<String>();
		if(f.isDirectory()){
			System.out.println("filename : "+f.getName());//¦L¥X§Ú­Ì©ÒÅª¨ìªº¸ê®Æ§¨
			String []s=f.list(); //«Å§i¤@­Ólist
			System.out.println("size : "+s.length);//¦L¥X¸ê®Æ§¨¸ÌªºÀÉ®×­Ó¼Æ
			for(int i=0;i<s.length;i++){
                //System.out.println(s[i]);
                fileList.add(s[i]); //±NÀÉ¦W¤@¤@¦s¨ìfileList°ÊºA°}¦C¸Ì­±
            }
		}
		
		String file = "";
		int fileid=0;
		for(int i=0;i<fileList.size();i++){
			file = fileList.get(i);
            System.out.println(file); //¦L¥X¸ê®Æ§¨¤ºªºÀÉ¦W
            fileid = i+1;
            ReadAnFileTest(file,fileid);    
        }
				
	}
	public static void ConnectionToMongoDB(){
		try{
			mongoClient = new MongoClient("localhost",DBPort);
			//mongoClient = new MongoClient(DBIP,DBPort);
			System.out.println("³s½u¦¨¥\¨ì :" + DBIP +":"+DBPort+ "\n\tDBName: "+DBName+"\n");
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
			System.out.println("¥¢±Ñ³s½u¨ì :" + DBIP +":"+DBPort+ "\n\tDBName: "+DBName+"\n"+"«ØÄ³¦b¸Õ¤@¦¸!!");
		}
		
	}
	public static void GetDatabaseName(){  //±o¨ìDatabase ¤§ NameList
		allDBList = mongoClient.getDatabaseNames(); 
		for(String s:allDBList){
			System.out.println(s);
		}
	}
}