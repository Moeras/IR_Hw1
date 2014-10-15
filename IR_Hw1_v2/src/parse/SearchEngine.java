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
		
		//���s����Ʈw
		ConnectionMongoDB();
		
		//���o�Ҧ��n���ɤ��ɮצW��(txt�ɦW)
		//GetAllFileName();
		
		//���w���ɤ����d��i�����(Text Mining)
		//int min =0,max=fileList.size(); //�ɮױq1�}�l
		//Parse(min,max);
		
		String test_term = "�u������";
		int test_docid = 77;
		int test_position = 78;
		
		DBQurry_updateInvertfile(test_term,test_docid,test_position);
	}
	public static void DBQurry_updateInvertfile(String Term,int DocId,int Position){
		//���o���Dictionary���(collection == table) 
		try {
			mongoClient = new MongoClient(new ServerAddress("localhost", 27017));
			db = mongoClient.getDB("IR_Hw1_TestDB");
			collection_Dictionary = db.getCollection("Dictionary");
		}
		catch (UnknownHostException e){
			e.printStackTrace();
		}
		//��term�w�s�b �n��s����
		if(isTermExist(Term)){
			
			// ��stermfrequency
	 		if(Processing_DocId != DocId){
				BasicDBObject updateDocument = 
						new BasicDBObject().append("$inc", 
						new BasicDBObject().append("IDF",1)); 
		 		collection_Dictionary.update(new BasicDBObject().append("Term", Term), updateDocument);
	 		}
	 		
	 		// ��sinvertedindexlist
	 		
	 		//�Y�o��term�q�ӨS���عL�o��document��� �إ߷s��InvertedFileIndex
	 		if(!isTermsDocumentExist(Term,DocId)){
	 			System.out.println("�Y�o��term�q�ӨS���عL�o��document��� �إ߷s��InvertedFileIndex");
	 			
	 			ArrayList position_array = new ArrayList();
				position_array.add(Position);
				
				DBObject Insert_Document = new BasicDBObject("InvertedFileList",
						new BasicDBObject("DocID",DocId).append("LocalFrequency",1).append("Position",position_array));
				
				DBObject updateQuery = new BasicDBObject("Term", Term);
				DBObject updateCommand = new BasicDBObject("$push", Insert_Document);
				collection_Dictionary.update(updateQuery,updateCommand);
				
	 		}
	 		 //�o��term�w�g�عL�o��Document����ƪ� �u�ݭn��s��invertedfile�Y�i
	 		else{
	 			System.out.println("//�o��term�w�g�عL�o��Document����ƪ� �u�ݭn��s��invertedfile�Y�i");
	 			
	 			//��sInvertedFileList ��Position 
				BasicDBObject updateQuery = new BasicDBObject("Term",Term);
				updateQuery.put("InvertedFileList.DocID",DocId);
		 		BasicDBObject updateCommand = new BasicDBObject("$push", new BasicDBObject("InvertedFileList.$.Position",Position));
				collection_Dictionary.update(updateQuery,updateCommand);
				
				//��sInvertedFileList ��Frequency
				BasicDBObject updateQuery_Frequency = new BasicDBObject("Term",Term);
				updateQuery_Frequency.put("InvertedFileList.DocID",DocId);
				BasicDBObject updateDocument = 
						new BasicDBObject().append("$inc", 
						new BasicDBObject().append("InvertedFileList.$.LocalFrequency",1)); 
		 		collection_Dictionary.update(updateQuery_Frequency,updateDocument);
				
	 		}
		}
		//��Term�S�����ɹL�A�ݭn�إ߷s��������Dictionary
		else{
			System.out.println("��Term�S�����ɹL�A�ݭn�إ߷s��������Dictionary");
			
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
	
	//�P�_��term�O�_���g�O���L�o��Document
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
	
	//�P�_��term�O�_�إߦb���L 
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
	
	
	//�@��@��i������� �N���ܦ��@�Ӥ@�ӥi�H�x�s��Token����
	public static void Tokenziation_Line(String Line,int DocId){
		String[] tokens = Line.split(" ");
		for(int i=0;i<tokens.length;i++){
			tokens[i] = tokens[i].trim();
			tokens[i] = tokens[i].replaceAll("[\\pP|~|$|^|<|>|\\||\\+|=]*", "");
			tokens[i] = tokens[i].replaceAll("\\s+", "");
			tokens[i] = tokens[i].replaceAll("'","");
			if(!tokens[i].isEmpty() && !tokens[i].equals("�� ")){
				
				//�P�_token���Ʀr�٬O�D�Ʀr
				if(isNumeric(tokens[i])){
	        		//System.out.println("�������ڬO�Ʀr: "+tokens[i]);
	        	}
	        	else if(!isNumeric(tokens[i])){
					if(isAlpha(tokens[i])){
						//System.out.println("�ڬO�­^��r: "+tokens[i]);
						String lowercasestring = CaseFolding(tokens[i]); //�j�p�g�ഫ �����ন�p�g
						
						//if(!lowercasestring.equals(tokens[i])){
							//System.out.println("��L���j�p�g��r�G "+lowercasestring);
						//}
						
						//�ϥ�Porter�t��k�i����F�٭� stemming ->lemma(����)
						if(PorterStemming_1(lowercasestring)){
							String lemma = PorterStemming_2(lowercasestring);
							System.out.println("lemma�G "+lemma);
							System.out.println("����,�­^��r: "+lemma);
							DBQurry_updateInvertfile(lemma,DocId,Processing_DoId_Position);
							Processing_DoId_Position++;
						}
						else{
							System.out.println("����r���|�ǤJ��Ʈw�G "+lowercasestring);
						}
					}
					else{
						//�i��O����Ʀr�S��Ÿ��������y�l  
						//�w�襤��y�l�P�D�­^��B�D�¼Ʀr�y�l�H�T�w���׶i��y�l����
						
						//CutSentenceInFitLength(tokens[i],1,DocId);  
						//CutSentenceInFitLength(tokens[i],2,DocId);
						CutSentenceInFitLength(tokens[i],10,DocId); 
					}
	        	}
	        	else{
	        		System.out.println("����r���|�ǤJ��Ʈw�G�ڬƻ򳣤��O�G"+tokens[i]);
	        	}
			}
			else{
				System.out.println("����r���|�ǤJ��Ʈw�G�ڬO���K�G"+tokens[i]);
			}
        }
		
	}
	
	//�N���^��`�y�l�����@��
	public static void CutSentenceInFitLength(String sentence,int fitlong,int DocId){
		
		int begin =0;
		while(sentence.length()>0){
			if(sentence.length() < fitlong){
				DBQurry_updateInvertfile(sentence,DocId,Processing_DoId_Position);
				Processing_DoId_Position++;
				System.out.println("����,���L����: "+sentence);
				return;
			}else{
				String subsentence = "";
				subsentence = sentence.substring(0,fitlong);
				sentence =  sentence.substring(fitlong,sentence.length());
				DBQurry_updateInvertfile(sentence,DocId,Processing_DoId_Position);
				Processing_DoId_Position++;
				System.out.println("����,���L����: "+subsentence);
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
		
		lemmatemp = porterstemming.Clean(originalterm); //�M���y'�z���_�Ǧr��
		if(porterstemming.hasSuffix(originalterm,"ing",str)){   //�h��ing �S�w�r��
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
	
	//�����אּ�p�g
	public static String CaseFolding(String englishterm){
		return englishterm.toLowerCase();
	}
	
	//�P�_�O�_���­^���r �S���Ʀr���嵥�����䤤
	public static boolean isAlpha(String name) {
		return name.matches("[a-zA-Z]+");
	}
	
	//�O�_���Ʀr
	public static boolean isNumeric(String str){
		
		NumberFormat formatter = NumberFormat.getInstance();
		ParsePosition pos = new ParsePosition(0);
		formatter.parse(str, pos);
		return str.length() == pos.getIndex();
		
	}
	
	//Ū���� �åB�̾ڤ@��@��Ū�� �ö}�l����token
	public static void ReadAnFile(String docname,int docid){
		
		String path = "C:/Users/Vicky/Desktop/resource_raw_data/"+docname;
		System.out.println("\t***Start Parse DocumnetNo:***"+docid);
		try{              
            FileInputStream fis = new FileInputStream(path);  
            byte[] lineb = new byte[5000];  //Ū�����j�p
            int rc = 0;  
            while((rc = fis.read(lineb))> 0){  
                String utf8_line = new String(lineb,"UTF-8");  //�HUTF-8���w�]�s�X
                //System.out.println(utf8_line); 
                Tokenziation_Line(utf8_line,docid);//������
                lineb = new byte[5000]; //�M����T �H�����¸�ƭ��_����
            }  
            fis.close();  
        }catch(FileNotFoundException e){  
            e.printStackTrace();  
        }catch(IOException ioe){  
            ioe.printStackTrace();  
        }  
		System.out.println("\t***End Parse***");
		
	}
	
	//Ū��������ɮ׭Ӽ�
	public static void Parse(int min,int max){
		
		String document_name = "";
		int document_id = 0;
		for(int i=min;i<max;i++){
			document_name=fileList.get(i);
			System.out.println("Parse : No�G"+i+" Name: "+document_name);
			document_id = i+1;
			Processing_DocId = document_id;
			Processing_DoId_Position = 1;
			ReadAnFile(document_name,document_id);
		}
	}
		
	//Ū����Ƨ����Ҧ����ɮצW��
	public static void GetAllFileName(){
		//File f = new File("C:/Users/Vicky/Desktop/Information Retrieval/hw1dataset30k/Data");  //
		File f = new File("C:/Users/Vicky/Desktop/resource_raw_data"); //���ե�
		fileList = new ArrayList<String>();
		if(f.isDirectory()){
			System.out.println("filename : "+f.getName());//�L�X�ڭ̩�Ū�쪺��Ƨ�
			String []s=f.list(); //�ŧi�@��list
			System.out.println("size : "+s.length);//�L�X��Ƨ��̪��ɮ׭Ӽ�
			for(int i=0;i<s.length;i++){
                //System.out.println(s[i]);
                fileList.add(s[i]); //�N�ɦW�@�@�s��fileList�ʺA�}�C�̭�
            }
		}
		
		
				
	}
	
	//�s��MongoDB
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
