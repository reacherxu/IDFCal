import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 对中文句子进行相似度计算，有计算句子权值、排序、两两句子之间的相似度计算
 * 问题是全部加载到内存    不适合特别大的语料
 * @author Reacher
 *
 */
public class IDFCal {
	String dataFile;
	ArrayList<String> docs;
	ArrayList<Term> termList;
	WordFilter filter;
	double docNum;
	
	public IDFCal()
	{
		dataFile = "data.txt";	
		docs = new ArrayList<String>();
		termList = new ArrayList<Term>();
		filter = new WordFilter(); 
	}
	
	/**
	 * 1行为1篇文档
	 */
	public void getDocs() 
	{
		String docLine;
		try {
			BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(new File(dataFile))));
			while((docLine = br.readLine())!=null)
			{
				//XXX:全部加载到内存，不适合特别大的语料
				docs.add(docLine);
			}
			
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		docNum = docs.size();
		
	}
	
	public void getTermList()
	{
		String docInstance;
		for(int i=0; i<docs.size();i++)
		{
			ArrayList<Term> termInDoc = new ArrayList<Term>();
			docInstance = docs.get(i);
			int start  = 0;
			int end  = 0;
			for(end=0; end<docInstance.length();end++)
			{
				if(docInstance.substring(end, end+1).equals(" "))
				{
					start = end;
				}
				if(docInstance.substring(end, end+1).equals("/"))
				{
					if(!filter.isFiltered(docInstance.substring(start+1, end)))
					{
						addItemInDoc(docInstance.substring(start+1, end),termInDoc);	
					}
					
					System.out.println("add item: "+docInstance.substring(start+1, end));
				}
			}
			refreshTermList(termInDoc);
		}
		setIDF();
		printTermList();
		
	}
	
	public void word2Vec() {
		try {
			BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(new File("vector"))));
			
			String docInstance;
			for(int i=0; i<docs.size();i++)
			{
				docInstance = docs.get(i);
				int start  = 0;
				int end  = 0;
				for(end=0; end<docInstance.length();end++)
				{
					if(docInstance.substring(end, end+1).equals(" "))
					{
						start = end;
					}
					if(docInstance.substring(end, end+1).equals("/"))
					{
						String tmpTerm = docInstance.substring(start+1, end);
						if(!filter.isFiltered(tmpTerm))
						{
							int termIdx = termList.indexOf(tmpTerm);
							System.out.print( tmpTerm + ":"
									+ termList.get(termIdx).getTermFreq() * termList.get(termIdx).getIDF());
//							bw.write(str);	
						}
						
//						System.out.println("add item: "+docInstance.substring(start+1, end));
					}
				}
			}
			
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void refreshTermList(ArrayList<Term> termInDoc) {
		for(int i=0;i<termInDoc.size();i++)
		{
			Boolean isMatchedFound = false;
			//顺序查找，写起来简单，但是效率低
			for(int j=0;j<termList.size();j++)
			{
				if(termList.get(j).getTerm().equals(termInDoc.get(i).getTerm()))
				{
					isMatchedFound = true;
					termList.get(j).addTermFreq(1);
				}
			}
			if(!isMatchedFound)
			{
				termList.add(new Term(termInDoc.get(i).getTerm()));
			}
		}
		
	}
	
	
	@SuppressWarnings("unused")
	private void printTermList() {
		System.out.println("total terms :"+termList.size());
		for(int i=0;i<termList.size();i++)
		{
			System.out.println(i+"th term：" + termList.get(i).getTerm()+" "
					+termList.get(i).getTermFreq()+" "
					+termList.get(i).getIDF());
		}
		
	}
	
	private void addItemInDoc(String stringTerm,ArrayList<Term> termInDoc) {
		if(termInDoc.size()==0)
		{
			termInDoc.add(new Term(stringTerm));
		}
		else
		{
			Boolean isFoundMatched = false;
			for(int i=0;i<termInDoc.size();i++)
			{
				if(termInDoc.get(i).getTerm().equals(stringTerm))
				{
					isFoundMatched = true;
					termInDoc.get(i).addTermFreq(1);
				}
			}
			if(!isFoundMatched)
			{
				termInDoc.add(new Term(stringTerm));
			}
		}		
	}
	
	private void setIDF()
	{
		for(int i=0;i<termList.size();i++)
		{
			double idf = Math.log10(docNum/termList.get(i).getTermFreq());
			termList.get(i).setIDF(idf);
		}
	}
	
	private double calSim(String doc1, String doc2) {
		int start  = 0;
		int end  = 0;
		
		String docInstance = doc1;
		ArrayList<Term> termInDoc1 = new ArrayList<Term>();		
		for(end=0; end<docInstance.length();end++)
		{
			if(docInstance.substring(end, end+1).equals(" "))
			{
				start = end;
			}
			if(docInstance.substring(end, end+1).equals("/"))
			{
				if(!filter.isFiltered(docInstance.substring(start+1, end)))
				{					
					addItemInDoc(docInstance.substring(start+1, end),termInDoc1);
				}
				//System.out.println("add item: "+docInstance.substring(start+1, end));
			}
		}
		start = 0;
		end = 0;
		docInstance = doc2;
		ArrayList<Term> termInDoc2 = new ArrayList<Term>();		
		for(end=0; end<docInstance.length();end++)
		{
			if(docInstance.substring(end, end+1).equals(" "))
			{
				start = end;
			}
			if(docInstance.substring(end, end+1).equals("/"))
			{
				if(!filter.isFiltered(docInstance.substring(start+1, end)))
				{					
					addItemInDoc(docInstance.substring(start+1, end),termInDoc2);
				}
				//System.out.println("add item: "+docInstance.substring(start+1, end));
			}
		}
		calTermInDoc(termInDoc1);
		calTermInDoc(termInDoc2);
		double lengthDoc1 = calLengthOfDoc(termInDoc1);
		double lengthDoc2 = calLengthOfDoc(termInDoc2);
		double docProduct = calDocProduct(termInDoc1,termInDoc2);
		return (docProduct/(lengthDoc1*lengthDoc2));
	}
	private double calDocProduct(ArrayList<Term> termInDoc1,
			ArrayList<Term> termInDoc2) {
		double product = 0;
		for(int i=0;i<termInDoc1.size();i++)
		{
			for(int j=0;j<termInDoc2.size();j++)
			{
				if(termInDoc1.get(i).getTerm().equals(termInDoc2.get(j).getTerm()))
				{
					product = product+termInDoc1.get(i).getIDF()*termInDoc2.get(j).getIDF();
				}
			}
		}
		return product;
	}
	private double calLengthOfDoc(ArrayList<Term> termInDoc) {
		double length = 0;
		for(int i=0;i<termInDoc.size();i++)
		{
			length=length+termInDoc.get(i).getIDF()*termInDoc.get(i).getIDF();
		}
		
		return Math.sqrt(length);
	}
	private void calTermInDoc(ArrayList<Term> termInDoc) {
		for(int i=0;i<termInDoc.size();i++)
		{
			Boolean isFoundMatched = false;
			for(int j =0;j<termList.size();j++)
			{
				if(termInDoc.get(i).getTerm().equals(termList.get(j).getTerm()))
				{
					isFoundMatched = true;
					double tfidf = termInDoc.get(i).getTermFreq()*termList.get(j).getIDF();
					termInDoc.get(i).setIDF(tfidf);
				}
			}
			if(!isFoundMatched)
			{
				System.out.println("unexpected error!!");
			}
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args)
	{
		
		IDFCal idfCal = new IDFCal();
		idfCal.getDocs();
		idfCal.getTermList();
		idfCal.word2Vec();
		/*String doc1 ;//= "1 他/r  早年/t  立志/v  救国/vn  ，/w  在/p  中学/n  时期/n  就/d  开始/v  注意/v  军事/n  问题/n  。/w";
		String doc2 ;//= "1 他/r  早年/t  立志/v  救国/vn  ，/w  在/p  中学/n  时期/n  就/d  开始/v  注意/v  军事/n  问题/n  。/w";
		//doc2=idfCal.docs.get(idfCal.docs.size()-1);
		doc2 = idfCal.docs.get(0);
		ArrayList <DocItem> scoreList = new ArrayList<DocItem>();
		for(int i=0;i<idfCal.docs.size();i++)
		{
			//System.out.print("第"+i+"个：");
			doc1 = idfCal.docs.get(i);
			scoreList.add(new DocItem(doc1,idfCal.calSim(doc1,doc2)));			
		}
		Collections.sort(scoreList,new ScoreCompare()); 
		for(int i=0;i<scoreList.size();i++)
		{
			System.out.println("第"+i+"个："+scoreList.get(i).score+" "+scoreList.get(i).doc);
		}*/
	}	
}
