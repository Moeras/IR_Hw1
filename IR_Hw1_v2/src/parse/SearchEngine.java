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
		
		//¥ý³sµ²¸ê®Æ®w
		ConnectionMongoDB();
		
		//¨ú±o©Ò¦³­n«ØÀÉ¤§ÀÉ®×¦WºÙ(txtÀÉ¦W)
		//GetAllFileName();
		
		//«ü©w«ØÀÉ¤§¤å¥ó½d³ò¶i¦æ«ØÀÉ(Text Mining)
		//int min =0,max=fileList.size(); //ÀÉ®×±q1¶}©l
		//Parse(min,max);
		
		String test_term = "¯uªº°²ªº";
		int test_docid = 77;
		int test_position = 78;
		
		DBQurry_updateInvertfile(test_term,test_docid,test_position);
	}
	public static void DBQurry_updateInvertfile(String Term,int DocId,int Position){
		//¨ú±o¸ê®ÆDictionaryÃã¨å(collection == table) 
		try {
			mongoClient = new MongoClient(new ServerAddress("localhost", 27017));
			db = mongoClient.getDB("IR_Hw1_TestDB");
			collection_Dictionary = db.getCollection("Dictionary");
		}
		catch (UnknownHostException e){
			e.printStackTrace();
		}
		//¸Óterm¤w¦s¦b ­n§ó·s¬ö¿ý
		if(isTermExist(Term)){
			
			// §ó·stermfrequency
	 		if(Processing_DocId != DocId){
				BasicDBObject updateDocument = 
						new BasicDBObject().append("$inc", 
						new BasicDBObject().append("IDF",1)); 
		 		collection_Dictionary.update(new BasicDBObject().append("Term", Term), updateDocument);
	 		}
	 		
	 		// §ó·sinvertedindexlist
	 		
	 		//­Y³o­Óterm±q¨Ó¨S¦³«Ø¹L³o­Ódocument¸ê®Æ «Ø¥ß·sªºInvertedFileIndex
	 		if(!isTermsDocumentExist(Term,DocId)){
	 			System.out.println("­Y³o­Óterm±q¨Ó¨S¦³«Ø¹L³o­Ódocument¸ê®Æ «Ø¥ß·sªºInvertedFileIndex");
	 			
	 			ArrayList position_array = new ArrayList();
				position_array.add(Position);
				
				DBObject Insert_Document = new BasicDBObject("InvertedFileList",
						new BasicDBObject("DocID",DocId).append("LocalFrequency",1).append("Position",position_array));
				
				DBObject updateQuery = new BasicDBObject("Term", Term);
				DBObject updateCommand = new BasicDBObject("$push", Insert_Document);
				collection_Dictionary.update(updateQuery,updateCommand);
				
	 		}
	 		 //³o­Óterm¤w¸g«Ø¹L³o­ÓDocumentªº¸ê®Æªº ¥u»Ý­n§ó·s¨äinvertedfile§Y¥i
	 		else{
	 			System.out.println("//³o­Óterm¤w¸g«Ø¹L³o­ÓDocumentªº¸ê®Æªº ¥u»Ý­n§ó·s¨äinvertedfile§Y¥i");
	 			
	 			//§ó·sInvertedFileList ªºPosition 
				BasicDBObject updateQuery = new BasicDBObject("Term",Term);
				updateQuery.put("InvertedFileList.DocID",DocId);
		 		BasicDBObject updateCommand = new BasicDBObject("$push", new BasicDBObject("InvertedFileList.$.Position",Position));
				collection_Dictionary.update(updateQuery,updateCommand);
				
				//§ó·sInvertedFileList ªºFrequency
				BasicDBObject updateQuery_Frequency = new BasicDBObject("Term",Term);
				updateQuery_Frequency.put("InvertedFileList.DocID",DocId);
				BasicDBObject updateDocument = 
						new BasicDBObject().append("$inc", 
						new BasicDBObject().append("InvertedFileList.$.LocalFrequency",1)); 
		 		collection_Dictionary.update(updateQuery_Frequency,updateDocument);
				
	 		}
		}
		//¦¹Term¨S¦³«ØÀÉ¹L¡A»Ý­n«Ø¥ß·sªºµü¶µ©óDictionary
		else{
			System.out.println("¦¹Term¨S¦³«ØÀÉ¹L¡A»Ý­n«Ø¥ß·sªºµü¶µ©óDictionary");
			
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
	
	//§PÂ_¸Óterm¬O§_´¿¸g°O¿ý¹L³o­ÓDocument
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
	
	//§PÂ_¸Óterm¬O§_«Ø¥ß¦bÃã¨å¹L 
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
	
	
	//¤@¦æ¤@¦æ¶i¦æµü±ø¤Æ ±N¤§ÅÜ¦¨¤@­Ó¤@­Ó¥i¥HÀx¦sªºTokenµü¶µ
	public static void Tokenziation_Line(String Line,int DocId){
		String[] tokens = Line.split(" ");
		for(int i=0;i<tokens.length;i++){
			tokens[i] = tokens[i].trim();
			tokens[i] = tokens[i].replaceAll("[\\pP|~|$|^|<|>|\\||\\+|=]*", "");
			tokens[i] = tokens[i].replaceAll("\\s+", "");
			tokens[i] = tokens[i].replaceAll("'","");
			if(!tokens[i].isEmpty() && !tokens[i].equals("„Ï ")){
				
				//§PÂ_token¬°¼Æ¦rÁÙ¬O«D¼Æ¦r
				if(isNumeric(tokens[i])){
	        		//System.out.println("«¢«¢«¢§Ú¬O¼Æ¦r: "+tokens[i]);
	        	}
	        	else if(!isNumeric(tokens[i])){
					if(isAlpha(tokens[i])){
						//System.out.println("§Ú¬O¯Â­^¤å¦r: "+tokens[i]);
						String lowercasestring = CaseFolding(tokens[i]); //¤j¤p¼gÂà´« ¥þ³¡Âà¦¨¤p¼g
						
						//if(!lowercasestring.equals(tokens[i])){
							//System.out.println("§ï¹Lªº¤j¤p¼g³æ¦r¡G "+lowercasestring);
						//}
						
						//¨Ï¥ÎPorterºtºâªk¶i¦æµü·FÁÙ­ì stemming ->lemma(µü¤¸)
						if(PorterStemming_1(lowercasestring)){
							String lemma = PorterStemming_2(lowercasestring);
							System.out.println("lemma¡G "+lemma);
							System.out.println("«ØÀÉ,¯Â­^¤å¦r: "+lemma);
							DBQurry_updateInvertfile(lemma,DocId,Processing_DoId_Position);
							Processing_DoId_Position++;
						}
						else{
							System.out.println("¦¹³æ¦r¤£·|¯Ç¤J¸ê®Æ®w¡G "+lowercasestring);
						}
					}
					else{
						//¥i¯à¬O¤¤¤å¼Æ¦r¯S®í²Å¸¹¥æÂø¤§¥y¤l  
						//°w¹ï¤¤¤å¥y¤l»P«D¯Â­^¤å¡B«D¯Â¼Æ¦r¥y¤l¥H©T©wªø«×¶i¦æ¥y¤l¤Á³Î
						
						//CutSentenceInFitLength(tokens[i],1,DocId);  
						//CutSentenceInFitLength(tokens[i],2,DocId);
						CutSentenceInFitLength(tokens[i],10,DocId); 
					}
	        	}
	        	else{
	        		System.out.println("¦¹³æ¦r¤£·|¯Ç¤J¸ê®Æ®w¡G§Ú¬Æ»ò³£¤£¬O¡G"+tokens[i]);
	        	}
			}
			else{
				System.out.println("¦¹³æ¦r¤£·|¯Ç¤J¸ê®Æ®w¡G§Ú¬O½¼±K¡G"+tokens[i]);
			}
        }
		
	}
	
	//±N¤¤­^¤å±`¥y¤l¤Á°£¤@­Ó
	public static void CutSentenceInFitLength(String sentence,int fitlong,int DocId){
		
		int begin =0;
		while(sentence.length()>0){
			if(sentence.length() < fitlong){
				DBQurry_updateInvertfile(sentence,DocId,Processing_DoId_Position);
				Processing_DoId_Position++;
				System.out.println("«ØÀÉ,¤Á¹Lªº³á: "+sentence);
				return;
			}else{
				String subsentence = "";
				subsentence = sentence.substring(0,fitlong);
				sentence =  sentence.substring(fitlong,sentence.length());
				DBQurry_updateInvertfile(sentence,DocId,Processing_DoId_Position);
				Processing_DoId_Position++;
				System.out.println("«ØÀÉ,¤Á¹Lªº³á: "+subsentence);
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
	
	//¥þ³¡§ï¬°¤p¼g
	public static String CaseFolding(String englishterm){
		return englishterm.toLowerCase();
	}
	
	//§PÂ_¬O§_¬°¯Â­^¤å³æ¦r ¨S¦³¼Æ¦r¤¤¤åµ¥§¨Âø¨ä¤¤
	public static boolean isAlpha(String name) {
		return name.matches("[a-zA-Z]+");
	}
	
	//¬O§_¬°¼Æ¦r
	public static boolean isNumeric(String str){
		
		NumberFormat formatter = NumberFormat.getInstance();
		ParsePosition pos = new ParsePosition(0);
		formatter.parse(str, pos);
		return str.length() == pos.getIndex();
		
	}
	
	//Åª§¹ÀÉ ¨Ã¥B¨Ì¾Ú¤@¦æ¤@¦æÅª¨ú ¨Ã¶}©l¤Á°£token
	public static void ReadAnFile(String docname,int docid){
		
		String path = "C:/Users/Vicky/Desktop/resource_raw_data/"+docname;
		System.out.println("\t***Start Parse DocumnetNo:***"+docid);
		try{              
            FileInputStream fis = new FileInputStream(path);  
            byte[] lineb = new byte[5000];  //Åª¨ú³æ¦ì¤j¤p
            int rc = 0;  
            while((rc = fis.read(lineb))> 0){  
                String utf8_line = new String(lineb,"UTF-8");  //¥HUTF-8¬°¹w³]½s½X
                //System.out.println(utf8_line); 
                Tokenziation_Line(utf8_line,docid);//µü±ø¤Æ
                lineb = new byte[5000]; //²M°£¸ê°T ¥H¨¾¤îÂÂ¸ê®Æ­«´_¬ö¿ý
            }  
            fis.close();  
        }catch(FileNotFoundException e){  
            e.printStackTrace();  
        }catch(IOException ioe){  
            ioe.printStackTrace();  
        }  
		System.out.println("\t***End Parse***");
		
	}
	
	//Åª¨ú¿ï¨úªºÀÉ®×­Ó¼Æ
	public static void Parse(int min,int max){
		
		String document_name = "";
		int document_id = 0;
		for(int i=min;i<max;i++){
			document_name=fileList.get(i);
			System.out.println("Parse : No¡G"+i+" Name: "+document_name);
			document_id = i+1;
			Processing_DocId = document_id;
			Processing_DoId_Position = 1;
			ReadAnFile(document_name,document_id);
		}
	}
		
	//Åª¨ú¸ê®Æ§¨¤¤©Ò¦³ªºÀÉ®×¦WºÙ
	public static void GetAllFileName(){
		//File f = new File("C:/Users/Vicky/Desktop/Information Retrieval/hw1dataset30k/Data");  //
		File f = new File("C:/Users/Vicky/Desktop/resource_raw_data"); //´ú¸Õ¥Î
		fileList = new ArrayList<String>();
		if(f.isDirectory()){
			System.out.println("filename : "+f.getName());//¦L¥X§Ú­Ì©ÒÅª¨ìªº¸ê®Æ§¨
			String []s=f.list(); //«Å§i¤@­Ólist
			System.out.println("size : "+s.length);//¦L¥X¸ê®Æ§¨¸ÌªºÀÉ®×­Ó¼Æ
			for(int i=0;i<s.length;i++){
                //System.out.println(s[i]);
                fileList.add(s[i]); //±NÀÉ¦W¤@¤@¦s¨ìfileList°ÊºA°}¦C¸Ì­±
            }
		}
		
		
				
	}
	
	//³sµ²MongoDB
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
