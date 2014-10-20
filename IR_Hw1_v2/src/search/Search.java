package search;

import java.io.File;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Iterator;

import org.bson.types.ObjectId;

import parse.NewString;
import parse.Porter;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;

public class Search {
	private static MongoClient mongoClient;
	public static DB[] db = new DB[100];
	private static String DBIP = "localhost";
	private static DBCollection collection_DictionaryCh;
	private static String Hw ="IR_Hw1";
	public static String[] Location = {"NBDB"};
	private static int[] DBNobegin={53};
	private static int[] DBSum = {54};
	//public static String[] location = {"LabDB","NBDB","KiethDB","MARTINDB","AlanDB","JonnyDB"};
	//private static int[] DBSum = {100,100,100,100,100,100};
	private static String[] LetterUPAll ={"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	private static String[] LetterLOAll ={"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
	public static DBCollection[]  collection_DictionaryEn = new DBCollection[27];

	public static int CreateHashTavleFirst = 0;  //是否建立hash索引
	public static String[] qurry_token;
	public static int qurry_token_number=0;
	public static int[] qurry_token_type; //0=ch 1=a 2=b ...27 = 數字;
	public static double[] TermIDF;
	public static double[][] TermTF;
	public static DBCollection search_Dictionary;
	public static double[] Score_DocWeight;
	public static int[] Score_No;
	public static double[] Score_DocWeight_Sort;
	private static ArrayList<String> DocList;
	public static int sumfile=0;
	
	public static void main(String[] args){
		
		//先連結資料庫
		ConnectionMongoDB();
		
		//取得所有資料之檔案名稱  提供後續對應用
		GetAllFileName();
		
		//搜尋
		String Qurry = " The polymorphous stream processor ";
		String Qurry2 = "較於超 332  純量";
		String Qurry3 = "Conventional  ";
		int TopK = 5;
		Search(Qurry2,TopK);
		
	}
	
	//搜尋
	public static void Search(String qurry,int topk){
		
		//處理qurry 並更新 qurry_token and qurry_token_type 決定要怎麼搜尋
		Tokenziation_Line(qurry);

		//初始化陣列(動態 依據資料多寡)
		TermIDF = new double[qurry_token.length];
		TermTF = new double[sumfile][qurry_token.length];
		
		//TestFunction();
		
		//一個一個的term 接著對DB下 qurry
		for(int i=0;i<qurry_token.length;i++){
			//若qurry為null則不進行搜尋
			if(qurry_token[i] != null){
				if(!qurry_token[i].equals(" ")){
					double IDF_Term = DBQurry(qurry_token[i],qurry_token_type[i],topk,i);
					if(IDF_Term > 0){
						TermIDF[i] =Math.log10(sumfile/IDF_Term);
					}
				}
			}
		}
		CauculationTF_IDF();
		ShowtheArticleList(topk);
		TestFunction();
	}
	public static void ShowtheArticleList(int topk){
		
		for(int i=0;i<sumfile;i++){
			//System.out.println("i = "+i);'
			Score_DocWeight_Sort[i] = Score_DocWeight[i];
			Score_No[i]=i;
		}
		for (int i = Score_DocWeight.length - 1; i > 0; --i){
			for (int j = 0; j < i; ++j){
				if (Score_DocWeight_Sort[j] < Score_DocWeight_Sort[j + 1]){
					Swap(Score_DocWeight_Sort, j, j + 1);
					Swap2(Score_No, j, j + 1);
				}
			}
		}
		System.out.println("建議文章：");
		for(int u=0;u<=topk;u++){
			System.out.println("No."+Score_No[u]+" 檔案名稱："+DocList.get(Score_No[u]-1));
		}
	
	}
	private static void Swap2(int[] array, int indexA, int indexB){
        
		int tmp = array[indexA];
        array[indexA] = array[indexB];
        array[indexB] = tmp;
    
	}
	
	private static void Swap(double[] array, int indexA, int indexB){
        
		double tmp = array[indexA];
        array[indexA] = array[indexB];
        array[indexB] = tmp;
    
	}
	public static void CauculationTF_IDF(){
		
		
		for(int i=0;i<qurry_token.length;i++){
			if(qurry_token[i] != null){
				for(int j=0;j<sumfile;j++){
					if(TermTF[j][i] != 0){
						Score_DocWeight[j] = TermTF[j][i]*TermIDF[i];
					}
					else{
						Score_DocWeight[j] = 0;
					}
				}
			}
		}
	}
	
	
	public static void GetDBCollectionIndex(DB dbindex,int type){
		String temppath="";
		if(type == 0) search_Dictionary = dbindex.getCollection("DictionaryCh");
		else if(type == -1){
			System.out.println("NO this term Record.");
		} 
		else if(type>0 && type<=26){
			temppath = "DictionaryEn"+LetterUPAll[type-1];
			//System.out.println("DictionaryEn"+LetterUPAll[type-1]);
			search_Dictionary = dbindex.getCollection(temppath);
		}
	}
	
	//針對DB下qurry 該term在所有DB中總和之IDF為多少 可以計算出
	public static int DBQurry(String searchterm,int searchtermtype,int k,int NoTermin_TermIDF){
		
		BasicDBObject SearchTermQurry = new BasicDBObject();
		SearchTermQurry.put("Term", searchterm);
		DBCursor cursor = null;
		int sumofterminDBS=0;
		for(int j=0;j<db.length;j++){
			if(db[j]!=null){
				GetDBCollectionIndex(db[j],searchtermtype);
				cursor= search_Dictionary.find(SearchTermQurry);
				
				//判斷該DB是否有找到term 
				if(cursor.size()==0){
					System.out.println("No  "+j+" DB沒找到");
					return 0;
			 	}else{
			 		System.out.println("Yes "+j+" DB找到");;
			 		while (cursor.hasNext()) {
			 			System.out.println(cursor.next());
			 			//找到資料後 進行累加 (目標：累加所有DB得結果IDF)
			 			sumofterminDBS =  sumofterminDBS + SumAggregationIDF(searchterm);
			 			
			 			//更新TFTable 只要是曾經出現該term都應該進行紀錄
			 			AggregationTFTable(searchterm,NoTermin_TermIDF);
			 		}
			 	}
			}
		}
		return sumofterminDBS;
	}
	
	public static void  AggregationTFTable(String FindTerm,int numberinTermIDF){
		
	    DBObject query = new BasicDBObject("Term", FindTerm);
	    
	    ///*對搜尋出來的Document 進行文字分析 比對 找出開document 的localfrequency
	    DBCursor cursor = search_Dictionary.find(query);
		String temp;
		String[] DocTemp = null;
		while(cursor.hasNext()){
			try{
				temp = cursor.next().toString();
				DocTemp = temp.split("\"");
				for(int i=0;i<DocTemp.length;i++){
					DocTemp[i] = DocTemp[i].replaceAll(":", "");
					DocTemp[i] = DocTemp[i].replaceAll("\\s+", "");
					DocTemp[i] = DocTemp[i].replaceAll(",", "");
					//System.out.println("DocTemp["+i+"]="+DocTemp[i]);
				}
			}
			catch(RuntimeException e){
				System.out.println("SHIT");
			}
		}
		for(int j=0;j<DocTemp.length;j++){
			if(DocTemp[j].equals("DocID")){
				//更新doc的TF值
				TermTF[Integer.valueOf(DocTemp[j+1])][numberinTermIDF]=Double.valueOf(DocTemp[j+3]);
			}
		}
	    
	}
	
	//沒用的fuuction 但是似乎達到了某種需求 可以留著參考 for futrue
	public static void AggregationTFTable4(String FindTerm,int numberinTermIDF){
		
	    
	    DBObject statusQuery = new BasicDBObject("DocID", 15206);
	    DBObject fields = new BasicDBObject("$elemMatch", statusQuery);
	    DBObject mathcObjects = new BasicDBObject("Term", FindTerm).
	    		append("InvertedFileList",fields);
	    //mathcObjects.put("InvertedFileList.LocalFrequency", 1);
	    DBObject match = new BasicDBObject("$match", mathcObjects);
	    
	    if(match != null)System.out.println("match = "+ match);
	    
	    // Now the $group operation
	    DBObject groupFields = new BasicDBObject( "_id", "$Term");
	    //groupFields.put("Doc",new BasicDBObject("$sum","$InvertedFileList.LocalFrequency"));
	    groupFields.put("TFsum",new BasicDBObject("$sum","InvertedFileList.0.LocalFrequency"));
	    
	    
	    DBObject group = new BasicDBObject("$group", groupFields);
	       
	    // run aggregation
	    AggregationOutput output = null;
	    try {
	    	output = search_Dictionary.aggregate(match,group);
	    } catch (MongoException e) {
	    	e.printStackTrace();
	    }
	    
	    CommandResult commandResult = output.getCommandResult();
	    BasicDBList photosDbObjectList = (BasicDBList) commandResult.get("result");
	  
	    Iterator<Object> it = photosDbObjectList.iterator();
	    String key ;
	    String sum = null ;
	    if(it.hasNext()==false) System.out.println("HHHHHHHHHHHHHHHH");
	    else{
	    	System.out.println("JJJJJJJJJJJJJJJJJJJJJJJ");
	    	System.out.println("output = "+output);
	    }
	    while ( it.hasNext()) {
	    	BasicDBObject basicDBObject = (BasicDBObject) it.next();
	    	System.out.println("basicDBObject = "+ basicDBObject);
	    	key= basicDBObject.get("_id").toString();
	    	sum= basicDBObject.get("TFsum").toString();
	    	System.out.println("TFsum "+key + " : " + sum);
	    }
	}
	
	public static int SumAggregationIDF(String findterm){
		
		DBObject mathcObjects = new BasicDBObject();
	    mathcObjects.put("Term", findterm);
	    DBObject match = new BasicDBObject("$match", mathcObjects );
	    
	    // Now the $group operation
	    DBObject groupFields = new BasicDBObject( "_id", "$Term");
	    groupFields.put("IDFsum", new BasicDBObject( "$sum", "$IDF"));
	    DBObject group = new BasicDBObject("$group", groupFields);
	       
	     // run aggregation
	    AggregationOutput output = null;
	    try {
	    	output = search_Dictionary.aggregate(match,group);
	    } catch (MongoException e) {
	    	e.printStackTrace();
	    }
	    
	    CommandResult commandResult = output.getCommandResult();
	    BasicDBList photosDbObjectList = (BasicDBList) commandResult.get("result");
	  
	    Iterator<Object> it = photosDbObjectList.iterator();
	    String key ;
	    String sum = null ;
	    while ( it.hasNext()) {
	    	BasicDBObject basicDBObject = (BasicDBObject) it.next();
	    	key= basicDBObject.get("_id").toString();
	    	sum= basicDBObject.get("IDFsum").toString();
	    	System.out.println(key + " : " + sum);
	    }
	    return Integer.parseInt(sum);
	  
	}
	public static void TestFunction(){
	
		System.out.println("qurry_token.len = "+qurry_token.length);
		System.out.println("qurry_token_type.len = "+qurry_token_type.length);
		System.out.println("TermIDF.len = "+TermIDF.length);
		System.out.println("TermTF.len = "+TermTF.length);
		System.out.println("TermTF[0].len = "+TermTF[0].length);
		
		for(int i=0;i<qurry_token.length;i++){
			System.out.println("i = "+i);
			for(int j=0;j<sumfile;j++){
				if(TermTF[j][i]>0){
					//System.out.println("TermTF["+j+"]["+i+"] = "+ String.valueOf(TermTF[j][i]));
				}
			}
		}
		//秀出所有doc的權重
		for(int j=0;j<sumfile;j++){
			if(Score_DocWeight[j] != 0){
				//System.out.println("Score_DocWeight["+j+"] = "+ String.valueOf(Score_DocWeight[j]));
			}
		}
	}
	
	//取得所有file的檔案名稱
	public static void GetAllFileName(){
			
		File f = new File("C:/Users/Vicky/Desktop/Information Retrieval/hw1dataset30k/Data");  //
		DocList = new ArrayList<String>();
		if(f.isDirectory()){
			String []s=f.list(); //宣告一個list
			System.out.println("Sum of Docunments: "+s.length +" 個");//印出資料夾裡的檔案個數
			for(int i=0;i<s.length;i++){
                //System.out.println(s[i]);
				DocList.add(s[i]); //將檔名一一存到fileList動態陣列裡面
            }
			sumfile = s.length;
			Score_DocWeight = new double[s.length]; //初始化Doc權重
			Score_DocWeight_Sort = new double[s.length];
			Score_No = new int[s.length];
		}
	}
	
	//將中英文常句子切除一個
	public static void CutSentenceInFitLength(String sentence,int fitlong){
		
		int begin =1;
		while(sentence.length()>0){
			if(sentence.length() < fitlong){
				//System.out.println("建檔,切過的喔: "+sentence);
				System.out.println("中文片語: "+sentence);
				qurry_token[qurry_token_number] = sentence;
				qurry_token_type[qurry_token_number] = CheckFirstLetter(sentence);
				qurry_token_number++;
				return;
			}
			else{
				String subsentence = "";
				subsentence = sentence.substring(0,fitlong);
				sentence = sentence.substring(fitlong,sentence.length());
				System.out.println("中文片語: "+subsentence);
				qurry_token[qurry_token_number] = subsentence;
				qurry_token_type[qurry_token_number] = CheckFirstLetter(subsentence);
				qurry_token_number++;
				//System.out.println("建檔,切過的喔: "+subsentence);
			}
		}
	}
	
	//確認字母開頭為何 可以減少之後搜尋時間
	public static int CheckFirstLetter(String checkterm){
		String firstLetter  = "";
		firstLetter = String.valueOf(checkterm.charAt(0));
		//System.out.println("firstLetter = "+ firstLetter);
		for(int i=0;i<26;i++){
			if(firstLetter.equals(LetterLOAll[i])) return i+1;
		}
		return 0; //若字母不為英文則認定為中文或特殊字詞
	}
	
	//一行一行進行詞條化 將之變成一個一個可以儲存的Token詞項
	public static void Tokenziation_Line(String Line){
		String[] tokens = Line.split(" ");
		qurry_token = new String[20];
		qurry_token_type = new int[20];
		for(int i=0;i<tokens.length;i++){
			tokens[i] = tokens[i].trim();
			tokens[i] = tokens[i].replaceAll("[\\pP|~|$|^|<|>|\\||\\+|=]*", "");
			tokens[i] = tokens[i].replaceAll("\\s+", "");
			tokens[i] = tokens[i].replaceAll("'","");
			if(!tokens[i].isEmpty() && !tokens[i].equals(" ")){
				
				//判斷token為數字還是非數字
				if(isNumeric(tokens[i])){
					//qurry_token[qurry_token_number] = tokens[i];
					//qurry_token_type[i] = 27;
	        		System.out.println("數字: "+tokens[i]);
	        	}
	        	else if(!isNumeric(tokens[i])){
					if(isAlpha(tokens[i])){
						
						System.out.println("英文： "+tokens[i]);
						
						
						//大小寫轉換 全部轉成小寫
						String lowercasestring = CaseFolding(tokens[i]);
						
						//使用Porter演算法進行詞幹還原 stemming ->lemma(詞元)
						if(PorterStemming_1(lowercasestring)){
							String lemma = PorterStemming_2(lowercasestring);
							System.out.println("開頭為 = "+ LetterLOAll[CheckFirstLetter(lemma)-1]);
							qurry_token[qurry_token_number] = lemma;
							qurry_token_type[qurry_token_number] = CheckFirstLetter(lemma);
							qurry_token_number++;
						}
						else{
							System.out.println("無用： "+lowercasestring);
						}
					}
					else{
						//可能是中文數字特殊符號交雜之句子  
						//針對中文句子與非純英文、非純數字句子以固定長度進行句子切割
						CutSentenceInFitLength(tokens[i],1);
					}
	        	}
	        	else{
	        		System.out.println("無用： "+tokens[i]);
	        	}
			}
			else{
				System.out.println("無用： "+tokens[i]);
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
		String dbpath ;
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
					
					if(CreateHashTavleFirst == 1){ 
						CreateHashIndex(db[dbno]);
					}
				
				}
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.out.println("db：");
		}
		
	}
	
}
