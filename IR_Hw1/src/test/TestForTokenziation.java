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
		//ConnectionToMongoDB();
		//GetDatabaseName(); 
		GetFileNameForDataPrepocess();
		//TestFunction();
		 //String enc = System.getProperty("file.encoding");
	       //System.out.println(enc);
		
	}
	
	public static void ReadAnFile(String filename){   //成功讀取最大檔案 但會出現 ? (有編碼問題存在)
		
		String path = "C:/Users/Vicky/Desktop/resource_raw_data/"+filename;
		System.out.println("\t***Using encoding***");
		try{              
            FileInputStream fis = new FileInputStream(path);  
            byte[] lineb = new byte[5000];  //讀取每一行的大小
            int rc = 0;  
            StringBuffer sb= new StringBuffer("");  
            
            
            while((rc = fis.read(lineb))> 0){  
                String utf8_line = new String(lineb,"UTF-8");  //以UTF-8為預設編碼
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
	
	//切除line->tokens
	public static void Tokenziation(String line){
		String[] tokens = line.split(" ");
		for(int i=0;i<tokens.length;i++){
			tokens[i] = tokens[i].trim();
			tokens[i] = tokens[i].replaceAll("[\\pP|~|$|^|<|>|\\||\\+|=]*", "");
			tokens[i] = tokens[i].replaceAll("\\s+", "");
			tokens[i] = tokens[i].replaceAll("'","");
			//tokens[i] = tokens[i].replaceAll("[\\s+|\\t+]*", "");
			
			
			//tokens[i] = tokens[i].replaceAll("\n\n+", "\n").replaceAll("^\n+", "");
			if(!tokens[i].isEmpty() && !tokens[i].equals("�� ")){
				
				if(isNumeric(tokens[i])){
	        		//System.out.println("哈哈哈我是數字: "+tokens[i]);
	        	}
	        	else if(!isNumeric(tokens[i])){
					if(isAlpha(tokens[i])){
						System.out.println("我是純英文字: "+tokens[i]);
						String lowercasestring = CaseFolding(tokens[i]); //大小寫轉換 全部轉成小寫
						if(!lowercasestring.equals(tokens[i])){
							System.out.println("改過的大小寫單字： "+lowercasestring);
						}
						//使用Porter演算法進行詞幹還原 stemming ->lemma(詞元)
						if(PorterStemming_1(lowercasestring)){
							String lemma = PorterStemming_2(lowercasestring);
							System.out.println("lemma： "+lemma);
						}
						else{
							System.out.println("此單字不會納入資料庫： "+lowercasestring);
						}
					}
					else{
						//可能是中文，需要把stopword拿掉嗎? 
						//針對中文句子與非純英文、非純數字句子以固定長度進行句子切割
						//CutSentenceInFitLong(tokens[i],1);  
						//CutSentenceInFitLong(tokens[i],2);
						CutSentenceInFitLong(tokens[i],10); 
					}
	        	}
	        	else{
	        		System.out.println("我甚麼都不是："+tokens[i]);
	        	}
			}
			else{
				//System.out.println("我是蝦密："+tokens[i]);
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
	public static String CaseFolding(String englishterm){
		return englishterm.toLowerCase();
	}
	public static void CutSentenceInFitLong(String sentence,int fitlong){
		
		int begin =0;
		while(sentence.length()>0){
			if(sentence.length() < fitlong){
				System.out.println("切過的喔: "+sentence);
				return;
			}else{
				String subsentence = "";
				subsentence = sentence.substring(0,fitlong);
				sentence =  sentence.substring(fitlong,sentence.length());
				System.out.println("切過的喔: "+subsentence);
			}
		}
	}
	
	public static void ReadAnFileTest(String filename){   //成功讀取最大檔案 但會出現 ? (有編碼問題存在)
		
		String path = "C:/Users/Vicky/Desktop/resource_raw_data/"+filename;
		System.out.println("\t***Using encoding***");
		try{              
            FileInputStream fis = new FileInputStream(path);  
            byte[] lineb = new byte[5000];  //讀取單位大小
            int rc = 0;  
            while((rc = fis.read(lineb))> 0){  
            	//String utf8_line = new String(lineb);
                String utf8_line = new String(lineb,"UTF-8");  //以UTF-8為預設編碼
                //System.out.println(utf8_line); 
                Tokenziation(utf8_line);//詞條化
                lineb = new byte[5000]; //清除資訊 以防止舊資料重復紀錄
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
		File f = new File("C:/Users/Vicky/Desktop/resource_raw_data"); //測試用
		ArrayList<String> fileList = new ArrayList<String>();
		if(f.isDirectory()){
			System.out.println("filename : "+f.getName());//印出我們所讀到的資料夾
			String []s=f.list(); //宣告一個list
			System.out.println("size : "+s.length);//印出資料夾裡的檔案個數
			for(int i=0;i<s.length;i++){
                //System.out.println(s[i]);
                fileList.add(s[i]); //將檔名一一存到fileList動態陣列裡面
            }
		}
		
		String file = "";
		for(int i=0;i<fileList.size();i++){
			file = fileList.get(i);
            System.out.println(file); //印出資料夾內的檔名
            ReadAnFileTest(file);    
        }
				
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