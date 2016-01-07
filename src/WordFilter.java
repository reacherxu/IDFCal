/**
 * 用于过滤标点和停用词
 * @author Reacher
 *
 */
public class WordFilter {
	public static String[] wordsSet ={";",
			".",
			",",
			"；",
			"！",
			"？",
			"，",
			"。"};
	Boolean isFiltered(String word)
	{
		int i=0;
		while(i<WordFilter.wordsSet.length)
		{
			if(word.equals(WordFilter.wordsSet[i]))
				return true;
			i++;
		}
		return false;
	}

}
