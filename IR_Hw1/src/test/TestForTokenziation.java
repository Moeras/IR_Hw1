package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
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
                String utf8_line = new String(lineb,"UTF-8");  
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
	
	
	public static void ReadAnFile2(String filename){
		
		Scanner scanner = null;
		String path = "C:/Users/Vicky/Desktop/resource_raw_data/"+filename;
		System.out.println("\t***Using encoding***");
		try{              
            FileInputStream fis = new FileInputStream(path);  
            byte[] lineb = new byte[2000];  //讀取每一行的大小
            int rc = 0;  
            StringBuffer sb= new StringBuffer("");  
            
            
            while((rc = fis.read(lineb))> 0){  
                String utf8 = new String(lineb,"UTF-8");               
                sb.append(utf8+"\n"); 
                
            }  
            fis.close();  
            System.out.println(sb.toString()); 
            lineb = null;            
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
            ReadAnFile(file);    
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