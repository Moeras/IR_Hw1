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

	public static void ReadAnFile(String filename){   //���\Ū���̤j�ɮ� ���|�X�{ ? (���s�X���D�s�b)
		
		String path = "C:/Users/Vicky/Desktop/resource_raw_data/"+filename;
		System.out.println("\t***Using encoding***");
		try{              
            FileInputStream fis = new FileInputStream(path);  
            byte[] lineb = new byte[5000];  //Ū���C�@�檺�j�p
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
            byte[] lineb = new byte[2000];  //Ū���C�@�檺�j�p
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
		File f = new File("C:/Users/Vicky/Desktop/resource_raw_data"); //���ե�
		ArrayList<String> fileList = new ArrayList<String>();
		if(f.isDirectory()){
			System.out.println("filename : "+f.getName());//�L�X�ڭ̩�Ū�쪺��Ƨ�
			String []s=f.list(); //�ŧi�@��list
			System.out.println("size : "+s.length);//�L�X��Ƨ��̪��ɮ׭Ӽ�
			for(int i=0;i<s.length;i++){
                //System.out.println(s[i]);
                fileList.add(s[i]); //�N�ɦW�@�@�s��fileList�ʺA�}�C�̭�
            }
		}
		
		String file = "";
		for(int i=0;i<fileList.size();i++){
			file = fileList.get(i);
            System.out.println(file); //�L�X��Ƨ������ɦW
            ReadAnFile(file);    
        }
				
	}
	public static void ConnectionToMongoDB(){
		try{
			//mongoClient = new MongoClient("localhost",DBPort);
			mongoClient = new MongoClient(DBIP,DBPort);
			System.out.println("�s�u���\�� :" + DBIP +":"+DBPort+ "\n\tDBName: "+DBName+"\n");
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
			System.out.println("���ѳs�u�� :" + DBIP +":"+DBPort+ "\n\tDBName: "+DBName+"\n"+"��ĳ�b�դ@��!!");
		}
		
	}
	public static void GetDatabaseName(){  //�o��Database �� NameList
		allDBList = mongoClient.getDatabaseNames(); 
		for(String s:allDBList){
			System.out.println(s);
		}
	}
}