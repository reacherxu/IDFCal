import java.util.Comparator;


@SuppressWarnings("rawtypes")
public class ScoreCompare implements Comparator{

	public int compare(Object arg0, Object arg1) {
		DocItem doc1 = (DocItem)arg0;
		DocItem doc2 = (DocItem)arg1;
		if(doc1.score<doc2.score)
			return 1;
		else 
			return 0;
	}
	

}
