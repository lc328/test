package cn.richinfo.domino.demo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesFactory;
import lotus.domino.Session;
import lotus.domino.View;

public class ParseDominoDemo {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		//第一种，与Domino Server联系获取
		String dominoHost = "172.16.41.83"; //Domino　ip地址
		String strIOR = null;
		URL url = new URL("http://"+dominoHost+"/diiop_ior.txt");
		InputStream in = url.openStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		for(boolean bExit = false;!bExit;){
			String line =br.readLine();
			if(line==null){
				bExit = true;
			}else{
				if(strIOR == null)
					strIOR = line;
				else
					strIOR += line;
				if(strIOR.startsWith("IOR:"))
					bExit = true;
			}
		}
		br.close();
		//第二种，取得本地文件:
//		FileInputStream fin = new FileInputStream("F:\\diiop_ior.txt");
//		InputStreamReader fisr = new InputStreamReader(fin);
//		BufferedReader br = new BufferedReader(fisr);
//		String ior = br.readLine();
//		fin.close();
		//创建session
		Session s = NotesFactory.createSessionWithIOR(strIOR);
		
		//打开names.nsf
		String db_names = "names.nsf";
		Database dbCache = s.getDatabase(s.getServerName(), db_names,true);
		
		//得到Users View
		View view = dbCache.getView("($Users)");
		
		//遍历Users View
		Document docKey = view.getDocumentByKey("bailu");
		if(docKey == null){
			System.out.println("docKey not found!");
		}else{
			String strMailFile = docKey.getItemValueString("MailFile");
			if(strMailFile == null){
				System.out.println("strMailFile is null!");
			}else{
				System.out.println("strMailFile = " + strMailFile);
			}
		}
		
		//打开邮箱数据库文件
		Database dbMail = s.getDatabase(s.getServerName(), "mail\\bailu.nsf",false);
		if(dbMail == null)
			System.out.println("cannot open database");
		else{
			
			DocumentCollection dc = dbMail.getAllDocuments();
			System.out.println("Mail database:" + dbMail.getTitle() + "is" + ((int)(dbMail.getSize()/1024))+"KB long and has "+dc.getCount()+ " documents");
			String fn = dbMail.getFileName();
			String title = dbMail.getTitle();
			System.out.println(fn.toUpperCase()+"-"+title);
			
			Document doc = dc.getFirstDocument();
			
			while(doc!=null){
				System.out.println("Subject="+doc.getItemValueString("Subject"));
				System.out.println("~~~~~~~~~~~~~~~~~~~~~\r\n");
				System.out.println("From="+doc.getItemValueString("From"));
				System.out.println("~~~~~~~~~~~~~~~~~~~~~\r\n");
				System.out.println("Body="+doc.getItemValueString("Body"));
				System.out.println("~~~~~~~~~~~~~~~~~~~~~\r\n");
				
			}
		}
		
		
	}
}
