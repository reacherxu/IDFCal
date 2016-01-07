
public class Term {
	private String term;
	private double termFreq;
	private double idf;
	public Term(String stringTerm)
	{
		term = stringTerm;
		termFreq = 1;
		idf = 0;
	}
	public String getTerm()
	{
		return term;
	}
	public void addTermFreq(double num)
	{
		termFreq = termFreq + num;
	}
	public double getTermFreq()
	{
		return termFreq;
	}
	public void setIDF(double idf)
	{
		this.idf = idf;
	}
	public double getIDF()
	{
		return idf;
	}
}
