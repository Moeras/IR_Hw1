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
import com.mongodb.CommandResult;
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
	private static DBCollection collection_DictionaryEn_a = null;
	private static DBCollection collection_DictionaryEn_b = null;
	private static DBCollection collection_DictionaryEn_c = null;
	private static DBCollection collection_DictionaryEn_d = null;
	private static DBCollection collection_DictionaryEn_e = null;
	private static DBCollection collection_DictionaryEn_f = null;
	private static DBCollection collection_DictionaryEn_g = null;
	private static DBCollection collection_DictionaryEn_h = null;
	private static DBCollection collection_DictionaryEn_i = null;
	private static DBCollection collection_DictionaryEn_j = null;
	private static DBCollection collection_DictionaryEn_k = null;
	private static DBCollection collection_DictionaryEn_l = null;
	private static DBCollection collection_DictionaryEn_m = null;
	private static DBCollection collection_DictionaryEn_n = null;
	private static DBCollection collection_DictionaryEn_o = null;
	private static DBCollection collection_DictionaryEn_p = null;
	private static DBCollection collection_DictionaryEn_q = null;
	private static DBCollection collection_DictionaryEn_r = null;
	private static DBCollection collection_DictionaryEn_s = null;
	private static DBCollection collection_DictionaryEn_t = null;
	private static DBCollection collection_DictionaryEn_u = null;
	private static DBCollection collection_DictionaryEn_v = null;
	private static DBCollection collection_DictionaryEn_w = null;
	private static DBCollection collection_DictionaryEn_x = null;
	private static DBCollection collection_DictionaryEn_y = null;
	private static DBCollection collection_DictionaryEn_z = null;
	//private static DBCollection collection_DictionaryEn = null;
	private static DBCollection collection_DictionaryCh = null;
	private static DBCollection collection_ParseTime = null;
	private static DBCollection collection_DBStats = null;
	private static ArrayList<String> fileList;
	public static int Processing_DocId =0;
	private static int Processing_DoId_Position = 0;
	
	public static void main(String[] args){
		
		//先連結資料庫
		ConnectionMongoDB();
		
		//取得所有要建檔之檔案名稱(txt檔名)
		GetAllFileName();
		
		//指定建檔之文件範圍進行建檔(Text Mining)
		//int min =15000,max=fileList.size(); //檔案從0開始
		int min =18155,max=20000; //檔案從0開始
		Parse(min,max);
		
		//String test_term = "顆顆顆顆";
		//int test_docid = 9089;
		//int test_position = 666;
		
		//DBQurry_updateInvertfile(test_term,test_docid,test_position);
	}
	public static int CheckFirstLetter(String checkterm){
		String firstLetter  = "";
		firstLetter = String.valueOf(checkterm.charAt(0));
		if(firstLetter.equals("a")) return 1;
		else if(firstLetter.equals("b"))return 2;
		else if(firstLetter.equals("c"))return 3;
		else if(firstLetter.equals("d"))return 4;
		else if(firstLetter.equals("e"))return 5;
		else if(firstLetter.equals("f"))return 6;
		else if(firstLetter.equals("g"))return 7;
		else if(firstLetter.equals("h"))return 8;
		else if(firstLetter.equals("i"))return 9;
		else if(firstLetter.equals("j"))return 10;
		else if(firstLetter.equals("k"))return 11;
		else if(firstLetter.equals("l"))return 12;
		else if(firstLetter.equals("m"))return 13;
		else if(firstLetter.equals("n"))return 14;
		else if(firstLetter.equals("o"))return 15;
		else if(firstLetter.equals("p"))return 16;
		else if(firstLetter.equals("q"))return 17;
		else if(firstLetter.equals("r"))return 18;
		else if(firstLetter.equals("s"))return 19;
		else if(firstLetter.equals("t"))return 20;
		else if(firstLetter.equals("u"))return 21;
		else if(firstLetter.equals("v"))return 22;
		else if(firstLetter.equals("w"))return 23;
		else if(firstLetter.equals("x"))return 24;
		else if(firstLetter.equals("y"))return 25;
		else  return 26;
	}
	public static void DBQurry_updateInvertfile(String Term,int DocId,int Position,Boolean EnForm){
		
		int FirstLetterNo=0;
		//該term已存在 要更新紀錄
		if(isTermExist(Term,EnForm)){
	 		
	 		// 更新invertedindexlist
	 		
	 		//若這個term從來沒有建過這個document資料 建立新的InvertedFileIndex
	 		if(!isTermsDocumentExist(Term,DocId,EnForm)){
	 			
	 			//System.out.println("若這個term從來沒有建過這個document資料 建立新的InvertedFileIndex");
	 			
	 			// 更新termfrequency
	 			BasicDBObject updateDocument = 
						new BasicDBObject().append("$inc", 
						new BasicDBObject().append("IDF",1)); 
	 			if(EnForm){
	 				FirstLetterNo = CheckFirstLetter(Term);
	 				if(FirstLetterNo == 1)	collection_DictionaryEn_a.update(new BasicDBObject().append("Term", Term), updateDocument);
	 				else if(FirstLetterNo == 2)	collection_DictionaryEn_b.update(new BasicDBObject().append("Term", Term), updateDocument);
	 				else if(FirstLetterNo == 3)	collection_DictionaryEn_c.update(new BasicDBObject().append("Term", Term), updateDocument);
	 				else if(FirstLetterNo == 4)	collection_DictionaryEn_d.update(new BasicDBObject().append("Term", Term), updateDocument);
	 				else if(FirstLetterNo == 5)	collection_DictionaryEn_e.update(new BasicDBObject().append("Term", Term), updateDocument);
	 				else if(FirstLetterNo == 6)	collection_DictionaryEn_f.update(new BasicDBObject().append("Term", Term), updateDocument);
	 				else if(FirstLetterNo == 7)	collection_DictionaryEn_g.update(new BasicDBObject().append("Term", Term), updateDocument);
	 				else if(FirstLetterNo == 8)	collection_DictionaryEn_h.update(new BasicDBObject().append("Term", Term), updateDocument);
	 				else if(FirstLetterNo == 9)	collection_DictionaryEn_i.update(new BasicDBObject().append("Term", Term), updateDocument);
	 				else if(FirstLetterNo == 10)collection_DictionaryEn_j.update(new BasicDBObject().append("Term", Term), updateDocument);
	 				else if(FirstLetterNo == 11)collection_DictionaryEn_k.update(new BasicDBObject().append("Term", Term), updateDocument);
	 				else if(FirstLetterNo == 12)collection_DictionaryEn_l.update(new BasicDBObject().append("Term", Term), updateDocument);
	 				else if(FirstLetterNo == 13)collection_DictionaryEn_m.update(new BasicDBObject().append("Term", Term), updateDocument);
	 				else if(FirstLetterNo == 14)collection_DictionaryEn_n.update(new BasicDBObject().append("Term", Term), updateDocument);
	 				else if(FirstLetterNo == 15)collection_DictionaryEn_o.update(new BasicDBObject().append("Term", Term), updateDocument);
	 				else if(FirstLetterNo == 16)collection_DictionaryEn_p.update(new BasicDBObject().append("Term", Term), updateDocument);
	 				else if(FirstLetterNo == 17)collection_DictionaryEn_q.update(new BasicDBObject().append("Term", Term), updateDocument);
	 				else if(FirstLetterNo == 18)collection_DictionaryEn_r.update(new BasicDBObject().append("Term", Term), updateDocument);
	 				else if(FirstLetterNo == 19)collection_DictionaryEn_s.update(new BasicDBObject().append("Term", Term), updateDocument);
	 				else if(FirstLetterNo == 20)collection_DictionaryEn_t.update(new BasicDBObject().append("Term", Term), updateDocument);
	 				else if(FirstLetterNo == 21)collection_DictionaryEn_u.update(new BasicDBObject().append("Term", Term), updateDocument);                
	 				else if(FirstLetterNo == 22)collection_DictionaryEn_v.update(new BasicDBObject().append("Term", Term), updateDocument);
	 				else if(FirstLetterNo == 23)collection_DictionaryEn_w.update(new BasicDBObject().append("Term", Term), updateDocument);
	 				else if(FirstLetterNo == 24)collection_DictionaryEn_x.update(new BasicDBObject().append("Term", Term), updateDocument);
	 				else if(FirstLetterNo == 25)collection_DictionaryEn_y.update(new BasicDBObject().append("Term", Term), updateDocument);
	 				else{
	 					collection_DictionaryEn_z.update(new BasicDBObject().append("Term", Term), updateDocument);
	 				}
	 			}
	 			else{
	 				collection_DictionaryCh.update(new BasicDBObject().append("Term", Term), updateDocument);
	 			}
	 			
		 		//System.out.println("更新termfrequency");
	 			
	 			
	 			ArrayList position_array = new ArrayList();
				position_array.add(Position);
				
				DBObject Insert_Document = new BasicDBObject("InvertedFileList",
						new BasicDBObject("DocID",DocId).append("LocalFrequency",1).append("Position",position_array));
				
				
				
				DBObject updateQuery = new BasicDBObject("Term", Term);
				DBObject updateCommand = new BasicDBObject("$push", Insert_Document);
				if(EnForm){
					FirstLetterNo = CheckFirstLetter(Term);
	 				if(FirstLetterNo == 1)	collection_DictionaryEn_a.update(updateQuery, updateCommand);
	 				else if(FirstLetterNo == 2)	collection_DictionaryEn_b.update(updateQuery, updateCommand);
	 				else if(FirstLetterNo == 3)	collection_DictionaryEn_c.update(updateQuery, updateCommand);
	 				else if(FirstLetterNo == 4)	collection_DictionaryEn_d.update(updateQuery, updateCommand);
	 				else if(FirstLetterNo == 5)	collection_DictionaryEn_e.update(updateQuery, updateCommand);
	 				else if(FirstLetterNo == 6)	collection_DictionaryEn_f.update(updateQuery, updateCommand);
	 				else if(FirstLetterNo == 7)	collection_DictionaryEn_g.update(updateQuery, updateCommand);
	 				else if(FirstLetterNo == 8)	collection_DictionaryEn_h.update(updateQuery, updateCommand);
	 				else if(FirstLetterNo == 9)	collection_DictionaryEn_i.update(updateQuery, updateCommand);
	 				else if(FirstLetterNo == 10)collection_DictionaryEn_j.update(updateQuery, updateCommand);
	 				else if(FirstLetterNo == 11)collection_DictionaryEn_k.update(updateQuery, updateCommand);
	 				else if(FirstLetterNo == 12)collection_DictionaryEn_l.update(updateQuery, updateCommand);
	 				else if(FirstLetterNo == 13)collection_DictionaryEn_m.update(updateQuery, updateCommand);
	 				else if(FirstLetterNo == 14)collection_DictionaryEn_n.update(updateQuery, updateCommand);
	 				else if(FirstLetterNo == 15)collection_DictionaryEn_o.update(updateQuery, updateCommand);
	 				else if(FirstLetterNo == 16)collection_DictionaryEn_p.update(updateQuery, updateCommand);
	 				else if(FirstLetterNo == 17)collection_DictionaryEn_q.update(updateQuery, updateCommand);
	 				else if(FirstLetterNo == 18)collection_DictionaryEn_r.update(updateQuery, updateCommand);
	 				else if(FirstLetterNo == 19)collection_DictionaryEn_s.update(updateQuery, updateCommand);
	 				else if(FirstLetterNo == 20)collection_DictionaryEn_t.update(updateQuery, updateCommand);
	 				else if(FirstLetterNo == 21)collection_DictionaryEn_u.update(updateQuery, updateCommand);                
	 				else if(FirstLetterNo == 22)collection_DictionaryEn_v.update(updateQuery, updateCommand);
	 				else if(FirstLetterNo == 23)collection_DictionaryEn_w.update(updateQuery, updateCommand);
	 				else if(FirstLetterNo == 24)collection_DictionaryEn_x.update(updateQuery, updateCommand);
	 				else if(FirstLetterNo == 25)collection_DictionaryEn_y.update(updateQuery, updateCommand);
	 				else{
	 					collection_DictionaryEn_z.update(updateQuery, updateCommand);
	 				}
				}
				else{
					collection_DictionaryCh.update(updateQuery,updateCommand);
				}
				
				
	 		}
	 		 //這個term已經建過這個Document的資料的 只需要更新其invertedfile即可
	 		else{
	 			//System.out.println("//這個term已經建過這個Document的資料的 只需要更新其invertedfile即可");
	 			
	 			//更新InvertedFileList 的Position 
				BasicDBObject updateQuery = new BasicDBObject("Term",Term);
				updateQuery.put("InvertedFileList.DocID",DocId);
		 		BasicDBObject updateCommand = new BasicDBObject("$push", new BasicDBObject("InvertedFileList.$.Position",Position));
		 		if(EnForm){
		 			FirstLetterNo = CheckFirstLetter(Term);
	 				if(FirstLetterNo == 1)	collection_DictionaryEn_a.update(updateQuery,updateCommand);
	 				else if(FirstLetterNo == 2)	collection_DictionaryEn_b.update(updateQuery,updateCommand);
	 				else if(FirstLetterNo == 3)	collection_DictionaryEn_c.update(updateQuery,updateCommand);
	 				else if(FirstLetterNo == 4)	collection_DictionaryEn_d.update(updateQuery,updateCommand);
	 				else if(FirstLetterNo == 5)	collection_DictionaryEn_e.update(updateQuery,updateCommand);
	 				else if(FirstLetterNo == 6)	collection_DictionaryEn_f.update(updateQuery,updateCommand);
	 				else if(FirstLetterNo == 7)	collection_DictionaryEn_g.update(updateQuery,updateCommand);
	 				else if(FirstLetterNo == 8)	collection_DictionaryEn_h.update(updateQuery,updateCommand);
	 				else if(FirstLetterNo == 9)	collection_DictionaryEn_i.update(updateQuery,updateCommand);
	 				else if(FirstLetterNo == 10)collection_DictionaryEn_j.update(updateQuery,updateCommand);
	 				else if(FirstLetterNo == 11)collection_DictionaryEn_k.update(updateQuery,updateCommand);
	 				else if(FirstLetterNo == 12)collection_DictionaryEn_l.update(updateQuery,updateCommand);
	 				else if(FirstLetterNo == 13)collection_DictionaryEn_m.update(updateQuery,updateCommand);
	 				else if(FirstLetterNo == 14)collection_DictionaryEn_n.update(updateQuery,updateCommand);
	 				else if(FirstLetterNo == 15)collection_DictionaryEn_o.update(updateQuery,updateCommand);
	 				else if(FirstLetterNo == 16)collection_DictionaryEn_p.update(updateQuery,updateCommand);
	 				else if(FirstLetterNo == 17)collection_DictionaryEn_q.update(updateQuery,updateCommand);
	 				else if(FirstLetterNo == 18)collection_DictionaryEn_r.update(updateQuery,updateCommand);
	 				else if(FirstLetterNo == 19)collection_DictionaryEn_s.update(updateQuery,updateCommand);
	 				else if(FirstLetterNo == 20)collection_DictionaryEn_t.update(updateQuery,updateCommand);
	 				else if(FirstLetterNo == 21)collection_DictionaryEn_u.update(updateQuery,updateCommand);              
	 				else if(FirstLetterNo == 22)collection_DictionaryEn_v.update(updateQuery,updateCommand);
	 				else if(FirstLetterNo == 23)collection_DictionaryEn_w.update(updateQuery,updateCommand);
	 				else if(FirstLetterNo == 24)collection_DictionaryEn_x.update(updateQuery,updateCommand);
	 				else if(FirstLetterNo == 25)collection_DictionaryEn_y.update(updateQuery,updateCommand);
	 				else{
	 					collection_DictionaryEn_z.update(updateQuery,updateCommand);
	 				}
		 		}
		 		else{
		 			collection_DictionaryCh.update(updateQuery,updateCommand);
		 		}
				
				
				//更新InvertedFileList 的Frequency
				BasicDBObject updateQuery_Frequency = new BasicDBObject("Term",Term);
				updateQuery_Frequency.put("InvertedFileList.DocID",DocId);
				BasicDBObject updateDocument = 
						new BasicDBObject().append("$inc", 
						new BasicDBObject().append("InvertedFileList.$.LocalFrequency",1)); 
		 		if(EnForm){
		 			FirstLetterNo = CheckFirstLetter(Term);
	 				if(FirstLetterNo == 1)	collection_DictionaryEn_a.update(updateQuery_Frequency,updateDocument);
	 				else if(FirstLetterNo == 2)	collection_DictionaryEn_b.update(updateQuery_Frequency,updateDocument);
	 				else if(FirstLetterNo == 3)	collection_DictionaryEn_c.update(updateQuery_Frequency,updateDocument);
	 				else if(FirstLetterNo == 4)	collection_DictionaryEn_d.update(updateQuery_Frequency,updateDocument);
	 				else if(FirstLetterNo == 5)	collection_DictionaryEn_e.update(updateQuery_Frequency,updateDocument);
	 				else if(FirstLetterNo == 6)	collection_DictionaryEn_f.update(updateQuery_Frequency,updateDocument);
	 				else if(FirstLetterNo == 7)	collection_DictionaryEn_g.update(updateQuery_Frequency,updateDocument);
	 				else if(FirstLetterNo == 8)	collection_DictionaryEn_h.update(updateQuery_Frequency,updateDocument);
	 				else if(FirstLetterNo == 9)	collection_DictionaryEn_i.update(updateQuery_Frequency,updateDocument);
	 				else if(FirstLetterNo == 10)collection_DictionaryEn_j.update(updateQuery_Frequency,updateDocument);
	 				else if(FirstLetterNo == 11)collection_DictionaryEn_k.update(updateQuery_Frequency,updateDocument);
	 				else if(FirstLetterNo == 12)collection_DictionaryEn_l.update(updateQuery_Frequency,updateDocument);
	 				else if(FirstLetterNo == 13)collection_DictionaryEn_m.update(updateQuery_Frequency,updateDocument);
	 				else if(FirstLetterNo == 14)collection_DictionaryEn_n.update(updateQuery_Frequency,updateDocument);
	 				else if(FirstLetterNo == 15)collection_DictionaryEn_o.update(updateQuery_Frequency,updateDocument);
	 				else if(FirstLetterNo == 16)collection_DictionaryEn_p.update(updateQuery_Frequency,updateDocument);
	 				else if(FirstLetterNo == 17)collection_DictionaryEn_q.update(updateQuery_Frequency,updateDocument);
	 				else if(FirstLetterNo == 18)collection_DictionaryEn_r.update(updateQuery_Frequency,updateDocument);
	 				else if(FirstLetterNo == 19)collection_DictionaryEn_s.update(updateQuery_Frequency,updateDocument);
	 				else if(FirstLetterNo == 20)collection_DictionaryEn_t.update(updateQuery_Frequency,updateDocument);
	 				else if(FirstLetterNo == 21)collection_DictionaryEn_u.update(updateQuery_Frequency,updateDocument);              
	 				else if(FirstLetterNo == 22)collection_DictionaryEn_v.update(updateQuery_Frequency,updateDocument);
	 				else if(FirstLetterNo == 23)collection_DictionaryEn_w.update(updateQuery_Frequency,updateDocument);
	 				else if(FirstLetterNo == 24)collection_DictionaryEn_x.update(updateQuery_Frequency,updateDocument);
	 				else if(FirstLetterNo == 25)collection_DictionaryEn_y.update(updateQuery_Frequency,updateDocument);
	 				else{
	 					collection_DictionaryEn_z.update(updateQuery_Frequency,updateDocument);
	 				}
		 		}
		 		else{
		 			collection_DictionaryCh.update(updateQuery_Frequency,updateDocument);
		 		}
				
				
				
	 		}
		}
		//此Term沒有建檔過，需要建立新的詞項於Dictionary
		else{
			//System.out.println("此Term沒有建檔過，需要建立新的詞項於Dictionary");
			
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
			if(EnForm){
				FirstLetterNo = CheckFirstLetter(Term);
 				if(FirstLetterNo == 1)	collection_DictionaryEn_a.insert(newDocument);
 				else if(FirstLetterNo == 2)	collection_DictionaryEn_b.insert(newDocument);
 				else if(FirstLetterNo == 3)	collection_DictionaryEn_c.insert(newDocument);
 				else if(FirstLetterNo == 4)	collection_DictionaryEn_d.insert(newDocument);
 				else if(FirstLetterNo == 5)	collection_DictionaryEn_e.insert(newDocument);
 				else if(FirstLetterNo == 6)	collection_DictionaryEn_f.insert(newDocument);
 				else if(FirstLetterNo == 7)	collection_DictionaryEn_g.insert(newDocument);
 				else if(FirstLetterNo == 8)	collection_DictionaryEn_h.insert(newDocument);
 				else if(FirstLetterNo == 9)	collection_DictionaryEn_i.insert(newDocument);
 				else if(FirstLetterNo == 10)collection_DictionaryEn_j.insert(newDocument);
 				else if(FirstLetterNo == 11)collection_DictionaryEn_k.insert(newDocument);
 				else if(FirstLetterNo == 12)collection_DictionaryEn_l.insert(newDocument);
 				else if(FirstLetterNo == 13)collection_DictionaryEn_m.insert(newDocument);
 				else if(FirstLetterNo == 14)collection_DictionaryEn_n.insert(newDocument);
 				else if(FirstLetterNo == 15)collection_DictionaryEn_o.insert(newDocument);
 				else if(FirstLetterNo == 16)collection_DictionaryEn_p.insert(newDocument);
 				else if(FirstLetterNo == 17)collection_DictionaryEn_q.insert(newDocument);
 				else if(FirstLetterNo == 18)collection_DictionaryEn_r.insert(newDocument);
 				else if(FirstLetterNo == 19)collection_DictionaryEn_s.insert(newDocument);
 				else if(FirstLetterNo == 20)collection_DictionaryEn_t.insert(newDocument);
 				else if(FirstLetterNo == 21)collection_DictionaryEn_u.insert(newDocument);              
 				else if(FirstLetterNo == 22)collection_DictionaryEn_v.insert(newDocument);
 				else if(FirstLetterNo == 23)collection_DictionaryEn_w.insert(newDocument);
 				else if(FirstLetterNo == 24)collection_DictionaryEn_x.insert(newDocument);
 				else if(FirstLetterNo == 25)collection_DictionaryEn_y.insert(newDocument);
 				else{
 					collection_DictionaryEn_z.insert(newDocument);
 				}
			}
			else{
				collection_DictionaryCh.insert(newDocument);
			}
			
			
		}
	}
	
	//判斷該term是否曾經記錄過這個Document
	public static boolean isTermsDocumentExist(String checkterm,int checkdocid,Boolean EnForm){
		BasicDBObject allQuery = new BasicDBObject();
		BasicDBObject fields = new BasicDBObject();
		allQuery.put("Term", checkterm);
		allQuery.put("InvertedFileList.DocID",checkdocid);
		DBCursor cursor;
		if(EnForm){
				int FirstLetterNo = CheckFirstLetter(checkterm);
				if(FirstLetterNo == 1)	cursor= collection_DictionaryEn_a.find(allQuery, fields);
				else if(FirstLetterNo == 2)	cursor= collection_DictionaryEn_b.find(allQuery, fields);
				else if(FirstLetterNo == 3)	cursor= collection_DictionaryEn_c.find(allQuery, fields);
				else if(FirstLetterNo == 4)	cursor= collection_DictionaryEn_d.find(allQuery, fields);
				else if(FirstLetterNo == 5)	cursor= collection_DictionaryEn_e.find(allQuery, fields);
				else if(FirstLetterNo == 6)	cursor= collection_DictionaryEn_f.find(allQuery, fields);
				else if(FirstLetterNo == 7)	cursor= collection_DictionaryEn_g.find(allQuery, fields);
				else if(FirstLetterNo == 8)	cursor= collection_DictionaryEn_h.find(allQuery, fields);
				else if(FirstLetterNo == 9)	cursor= collection_DictionaryEn_i.find(allQuery, fields);
				else if(FirstLetterNo == 10)cursor= collection_DictionaryEn_j.find(allQuery, fields);
				else if(FirstLetterNo == 11)cursor= collection_DictionaryEn_k.find(allQuery, fields);
				else if(FirstLetterNo == 12)cursor= collection_DictionaryEn_l.find(allQuery, fields);
				else if(FirstLetterNo == 13)cursor= collection_DictionaryEn_m.find(allQuery, fields);
				else if(FirstLetterNo == 14)cursor= collection_DictionaryEn_n.find(allQuery, fields);
				else if(FirstLetterNo == 15)cursor= collection_DictionaryEn_o.find(allQuery, fields);
				else if(FirstLetterNo == 16)cursor= collection_DictionaryEn_p.find(allQuery, fields);
				else if(FirstLetterNo == 17)cursor= collection_DictionaryEn_q.find(allQuery, fields);
				else if(FirstLetterNo == 18)cursor= collection_DictionaryEn_r.find(allQuery, fields);
				else if(FirstLetterNo == 19)cursor= collection_DictionaryEn_s.find(allQuery, fields);
				else if(FirstLetterNo == 20)cursor= collection_DictionaryEn_t.find(allQuery, fields);
				else if(FirstLetterNo == 21)cursor= collection_DictionaryEn_u.find(allQuery, fields);       
				else if(FirstLetterNo == 22)cursor= collection_DictionaryEn_v.find(allQuery, fields);
				else if(FirstLetterNo == 23)cursor= collection_DictionaryEn_w.find(allQuery, fields);
				else if(FirstLetterNo == 24)cursor= collection_DictionaryEn_x.find(allQuery, fields);
				else if(FirstLetterNo == 25)cursor= collection_DictionaryEn_y.find(allQuery, fields);
				else{
					cursor= collection_DictionaryEn_z.find(allQuery, fields);
				}
		}
		else{
			cursor = collection_DictionaryCh.find(allQuery, fields);
		}
	 	
	 	//System.out.println(cursor.size());
	 	if(cursor.size()==0){
	 		return false;
	 	}else{
	 		return true;
	 	}
	}
	
	//判斷該term是否建立在辭典過 
	public static boolean isTermExist(String checkterm,Boolean EnForm){
		
		BasicDBObject allQuery = new BasicDBObject();
		BasicDBObject fields = new BasicDBObject();
		allQuery.put("Term", checkterm);
		DBCursor cursor;
		if(EnForm){
			int FirstLetterNo = CheckFirstLetter(checkterm);
			if(FirstLetterNo == 1)	cursor= collection_DictionaryEn_a.find(allQuery, fields);
			else if(FirstLetterNo == 2)	cursor= collection_DictionaryEn_b.find(allQuery, fields);
			else if(FirstLetterNo == 3)	cursor= collection_DictionaryEn_c.find(allQuery, fields);
			else if(FirstLetterNo == 4)	cursor= collection_DictionaryEn_d.find(allQuery, fields);
			else if(FirstLetterNo == 5)	cursor= collection_DictionaryEn_e.find(allQuery, fields);
			else if(FirstLetterNo == 6)	cursor= collection_DictionaryEn_f.find(allQuery, fields);
			else if(FirstLetterNo == 7)	cursor= collection_DictionaryEn_g.find(allQuery, fields);
			else if(FirstLetterNo == 8)	cursor= collection_DictionaryEn_h.find(allQuery, fields);
			else if(FirstLetterNo == 9)	cursor= collection_DictionaryEn_i.find(allQuery, fields);
			else if(FirstLetterNo == 10)cursor= collection_DictionaryEn_j.find(allQuery, fields);
			else if(FirstLetterNo == 11)cursor= collection_DictionaryEn_k.find(allQuery, fields);
			else if(FirstLetterNo == 12)cursor= collection_DictionaryEn_l.find(allQuery, fields);
			else if(FirstLetterNo == 13)cursor= collection_DictionaryEn_m.find(allQuery, fields);
			else if(FirstLetterNo == 14)cursor= collection_DictionaryEn_n.find(allQuery, fields);
			else if(FirstLetterNo == 15)cursor= collection_DictionaryEn_o.find(allQuery, fields);
			else if(FirstLetterNo == 16)cursor= collection_DictionaryEn_p.find(allQuery, fields);
			else if(FirstLetterNo == 17)cursor= collection_DictionaryEn_q.find(allQuery, fields);
			else if(FirstLetterNo == 18)cursor= collection_DictionaryEn_r.find(allQuery, fields);
			else if(FirstLetterNo == 19)cursor= collection_DictionaryEn_s.find(allQuery, fields);
			else if(FirstLetterNo == 20)cursor= collection_DictionaryEn_t.find(allQuery, fields);
			else if(FirstLetterNo == 21)cursor= collection_DictionaryEn_u.find(allQuery, fields);       
			else if(FirstLetterNo == 22)cursor= collection_DictionaryEn_v.find(allQuery, fields);
			else if(FirstLetterNo == 23)cursor= collection_DictionaryEn_w.find(allQuery, fields);
			else if(FirstLetterNo == 24)cursor= collection_DictionaryEn_x.find(allQuery, fields);
			else if(FirstLetterNo == 25)cursor= collection_DictionaryEn_y.find(allQuery, fields);
			else{
				cursor= collection_DictionaryEn_z.find(allQuery, fields);
			}
		}
		else{
			cursor= collection_DictionaryCh.find(allQuery, fields);
		}
	 	//System.out.println(cursor.size());
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
							//System.out.println("lemma： "+lemma);
							//System.out.println("建檔,純英文字: "+lemma);
							DBQurry_updateInvertfile(lemma,DocId,Processing_DoId_Position,true);
							Processing_DoId_Position++;
						}
						else{
							//System.out.println("此單字不會納入資料庫： "+lowercasestring);
						}
					}
					else{
						//可能是中文數字特殊符號交雜之句子  
						//針對中文句子與非純英文、非純數字句子以固定長度進行句子切割
						
						CutSentenceInFitLength(tokens[i],1,DocId);
						//CutSentenceInFitLength(tokens[i],2,DocId);
						//CutSentenceInFitLength(tokens[i],3,DocId);
						//CutSentenceInFitLength(tokens[i],10,DocId); 
					}
	        	}
	        	else{
	        		//System.out.println("此單字不會納入資料庫：我甚麼都不是："+tokens[i]);
	        	}
			}
			else{
				//System.out.println("此單字不會納入資料庫：我是蝦密："+tokens[i]);
			}
        }
		
	}
	
	//將中英文常句子切除一個
	public static void CutSentenceInFitLength(String sentence,int fitlong,int DocId){
		
		int begin =1;
		while(sentence.length()>0){
			if(sentence.length() < fitlong){
				DBQurry_updateInvertfile(sentence,DocId,Processing_DoId_Position,false);
				Processing_DoId_Position++;
				//System.out.println("建檔,切過的喔: "+sentence);
				return;
			}
			else{
				String subsentence = "";
				subsentence = sentence.substring(0,fitlong);
				sentence = sentence.substring(fitlong,sentence.length());
				DBQurry_updateInvertfile(subsentence,DocId,Processing_DoId_Position,false);
				Processing_DoId_Position++;
				//System.out.println("建檔,切過的喔: "+subsentence);
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
		String path = "C:/Users/Vicky/Desktop/Information Retrieval/hw1dataset30k/Data/"+docname;
		//String path = "C:/Users/Vicky/Desktop/resource_raw_data/"+docname;
		long startTime = System.currentTimeMillis();
		System.out.print("\tStart Parse ->\t DocNo:"+ docid+"\t DocName:"+ docname);
		
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
		
		long totTime = System.currentTimeMillis()-startTime;
		SaveUsingTime(docname,docid,totTime);
		System.out.print("\n\t - Using Time:"+totTime+" -> End Parse.\n");
	}
	public static void SaveUsingTime(String docname,int docid,long time){
		
		BasicDBObject timeDocument = new BasicDBObject();
		timeDocument.put("DocID",docid);
		timeDocument.put("DocName", docname);
		timeDocument.put("ParseTime",time);
		
		collection_ParseTime.insert(timeDocument);
		
	}
	
	//讀取選取的檔案個數
	public static void Parse(int min,int max){
		
		int totalfile = max-min-1;
		int DbNo = 522;
		long startTime = System.currentTimeMillis();
		System.out.println("Start Parse File...");
		System.out.println("Total files："+ totalfile);
		String document_name = "";
		int document_id = 0;
		for(int i=min;i<max;i++){
			document_name=fileList.get(i);
			document_id = i+1;
			Processing_DoId_Position = 1;
			if(System.currentTimeMillis()-startTime > 80000){ 
				startTime = System.currentTimeMillis();
				BasicDBObject Status = new BasicDBObject(db.getStats());
				collection_DBStats.insert(Status);
				ReConnectionMongoDB(Integer.toString(DbNo));
				DbNo++;
			}
			ReadAnFile(document_name,document_id);
			
		}
		System.out.println("End Parse.");
	}
		
	//讀取資料夾中所有的檔案名稱
	public static void GetAllFileName(){
		File f = new File("C:/Users/Vicky/Desktop/Information Retrieval/hw1dataset30k/Data");  //
		//File f = new File("C:/Users/Vicky/Desktop/resource_raw_data"); //測試用
		fileList = new ArrayList<String>();
		if(f.isDirectory()){
			System.out.println("filename : "+f.getName());//印出我們所讀到的資料夾
			String []s=f.list(); //宣告一個list
			//System.out.println("size : "+s.length);//印出資料夾裡的檔案個數
			for(int i=0;i<s.length;i++){
                //System.out.println(s[i]);
                fileList.add(s[i]); //將檔名一一存到fileList動態陣列裡面
            }
		}
		
		
				
	}
	public static void ReConnectionMongoDB(String DBNo){
		try {
			mongoClient = new MongoClient(new ServerAddress("localhost", 27017));
			db = mongoClient.getDB("IR_Hw1_NBDB_"+DBNo);
			collection_DictionaryEn_a = db.getCollection("DictionaryEnA");
			collection_DictionaryEn_b = db.getCollection("DictionaryEnB");
			collection_DictionaryEn_c = db.getCollection("DictionaryEnC");
			collection_DictionaryEn_d = db.getCollection("DictionaryEnD");
			collection_DictionaryEn_e = db.getCollection("DictionaryEnE");
			collection_DictionaryEn_f = db.getCollection("DictionaryEnF");
			collection_DictionaryEn_g = db.getCollection("DictionaryEnG");
			collection_DictionaryEn_h = db.getCollection("DictionaryEnH");
			collection_DictionaryEn_i = db.getCollection("DictionaryEnI");
			collection_DictionaryEn_j = db.getCollection("DictionaryEnJ");
			collection_DictionaryEn_k=  db.getCollection("DictionaryEnK");
			collection_DictionaryEn_l = db.getCollection("DictionaryEnL");
			collection_DictionaryEn_m = db.getCollection("DictionaryEnM");
			collection_DictionaryEn_n = db.getCollection("DictionaryEnN");
			collection_DictionaryEn_o = db.getCollection("DictionaryEnO");
			collection_DictionaryEn_p = db.getCollection("DictionaryEnP");
			collection_DictionaryEn_q = db.getCollection("DictionaryEnQ");
			collection_DictionaryEn_r = db.getCollection("DictionaryEnR");
			collection_DictionaryEn_s = db.getCollection("DictionaryEnS");
			collection_DictionaryEn_t = db.getCollection("DictionaryEnT");
			collection_DictionaryEn_u = db.getCollection("DictionaryEnU");
			collection_DictionaryEn_v = db.getCollection("DictionaryEnV");
			collection_DictionaryEn_w = db.getCollection("DictionaryEnW");
			collection_DictionaryEn_x = db.getCollection("DictionaryEnX");
			collection_DictionaryEn_y = db.getCollection("DictionaryEnY");
			collection_DictionaryEn_z = db.getCollection("DictionaryEnZ");
			
			//collection_DictionaryEn = db.getCollection("DictionaryEn");
			collection_DictionaryCh = db.getCollection("DictionaryCh");
			collection_ParseTime = db.getCollection("ParseTime");
			collection_DBStats = db.getCollection("DBStats");
		}
		catch (UnknownHostException e){
			e.printStackTrace();
		}
	}
	//連結MongoDB
	public static void ConnectionMongoDB(){
		try {
			mongoClient = new MongoClient(new ServerAddress("localhost", 27017));
			db = mongoClient.getDB("IR_Hw1_NBDB_521");
			collection_DictionaryEn_a = db.getCollection("DictionaryEnA");
			collection_DictionaryEn_b = db.getCollection("DictionaryEnB");
			collection_DictionaryEn_c = db.getCollection("DictionaryEnC");
			collection_DictionaryEn_d = db.getCollection("DictionaryEnD");
			collection_DictionaryEn_e = db.getCollection("DictionaryEnE");
			collection_DictionaryEn_f = db.getCollection("DictionaryEnF");
			collection_DictionaryEn_g = db.getCollection("DictionaryEnG");
			collection_DictionaryEn_h = db.getCollection("DictionaryEnH");
			collection_DictionaryEn_i = db.getCollection("DictionaryEnI");
			collection_DictionaryEn_j = db.getCollection("DictionaryEnJ");
			collection_DictionaryEn_k=  db.getCollection("DictionaryEnK");
			collection_DictionaryEn_l = db.getCollection("DictionaryEnL");
			collection_DictionaryEn_m = db.getCollection("DictionaryEnM");
			collection_DictionaryEn_n = db.getCollection("DictionaryEnN");
			collection_DictionaryEn_o = db.getCollection("DictionaryEnO");
			collection_DictionaryEn_p = db.getCollection("DictionaryEnP");
			collection_DictionaryEn_q = db.getCollection("DictionaryEnQ");
			collection_DictionaryEn_r = db.getCollection("DictionaryEnR");
			collection_DictionaryEn_s = db.getCollection("DictionaryEnS");
			collection_DictionaryEn_t = db.getCollection("DictionaryEnT");
			collection_DictionaryEn_u = db.getCollection("DictionaryEnU");
			collection_DictionaryEn_v = db.getCollection("DictionaryEnV");
			collection_DictionaryEn_w = db.getCollection("DictionaryEnW");
			collection_DictionaryEn_x = db.getCollection("DictionaryEnX");
			collection_DictionaryEn_y = db.getCollection("DictionaryEnY");
			collection_DictionaryEn_z = db.getCollection("DictionaryEnZ");
			
			//collection_DictionaryEn = db.getCollection("DictionaryEn");
			collection_DictionaryCh = db.getCollection("DictionaryCh");
			collection_ParseTime = db.getCollection("ParseTime");
			collection_DBStats = db.getCollection("DBStats");
		}
		catch (UnknownHostException e){
			e.printStackTrace();
		}
	}
}
