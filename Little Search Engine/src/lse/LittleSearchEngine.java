package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages
 * in which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {

	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the
	 * associated value is an array list of all occurrences of the keyword in
	 * documents. The array list is maintained in DESCENDING order of frequencies.
	 */
	HashMap<String, ArrayList<Occurrence>> keywordsIndex;
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;

	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String, ArrayList<Occurrence>>(1000, 2.0f);
		noiseWords = new HashSet<String>(100, 2.0f);
	}
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword
	 * occurrences in the document. Uses the getKeyWord method to separate keywords
	 * from other words.
	 *
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an
	 *         Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String, Occurrence> loadKeywordsFromDocument(String docFile) throws FileNotFoundException {
		/** COMPLETE THIS METHOD **/
		HashMap<String, Occurrence> map = new HashMap<>(); // Store keywords
		Scanner sc = new Scanner(new File(docFile)); // Scans document
		
		while (sc.hasNext()) {
			String word = sc.next(); // Get individual words
			if (word.length() == 0) // Continue if word length is 0 but do not add
				continue;
			String keyword = getKeyword(word); // Gets keyword
			if (keyword == null) // Don't want to add null word
				continue;
			Occurrence occ = map.get(keyword);
			if (occ == null) { // If not in map
				map.put(keyword, new Occurrence(docFile, 1)); // Adds keyword to map with docFile and 1 as frequency
			} else {
				occ.frequency++; // Add one to frequency if already in map
				map.put(keyword, occ); // Put back into map
			}
		}
		sc.close();
		return map;
	}

	/**
	 * Merges the keywords for a single document into the master keywordsIndex hash
	 * table. For each keyword, its Occurrence in the current document must be
	 * inserted in the correct place (according to descending order of frequency) in
	 * the same keyword's Occurrence list in the master hash table. This is done by
	 * calling the insertLastOccurrence method.
	 *
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String, Occurrence> kws) {
		for (String keywords : kws.keySet()) {
			if (keywordsIndex.containsKey(keywords)) { // If master keyword list already has the key
				ArrayList<Occurrence> tmp = keywordsIndex.get(keywords);
				tmp.add(kws.get(keywords));
				insertLastOccurrence(tmp);
				keywordsIndex.put(keywords, tmp);
			} else if (!(keywordsIndex.containsKey(keywords))) { // If master keyword list does not have the key
				ArrayList<Occurrence> tmp = new ArrayList<Occurrence>();
				tmp.add(kws.get(keywords));
				insertLastOccurrence(tmp);
				keywordsIndex.put(keywords, tmp);
			}
		}

	}
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of
	 * any trailing punctuation, consists only of alphabetic letters, and is not a
	 * noise word. All words are treated in a case-INsensitive manner.
	 *
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 *
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		String keyWord = "";
		word = word.toLowerCase(); // Lowercase check
		if (word.equals(null) || word.length() <= 0) { // Check if word is valid
			return null;
		} else {
			for (int i = 0; i < word.length() - 1; i++) {
				if (Character.isLetter(word.charAt(i))) {
					keyWord = keyWord + Character.toString(word.charAt(i));
				} else if (!Character.isLetter(word.charAt(i))) { // Check for punctuation
					if (Character.toString(word.charAt(i)).equals(".") || Character.toString(word.charAt(i)).equals(",")
							|| Character.toString(word.charAt(i)).equals("?")
							|| Character.toString(word.charAt(i)).equals(":")
							|| Character.toString(word.charAt(i)).equals(";")
							|| Character.toString(word.charAt(i)).equals("!")) {
						if (Character.isLetter(word.charAt(i + 1))) {
							return null;
						}
					} else { // if number or other punctuation
						return null;
					}
				}
			}
		}
		if (Character.isLetter(word.charAt(word.length() - 1))) { // Puts word back together
			keyWord = keyWord + Character.toString(word.charAt(word.length() - 1));
		}
		if (noiseWords.contains(word)) { // If noiseword after trimming word
			return null;
		}
		return keyWord; // Return final keyword
	}
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in
	 * the list, based on ordering occurrences on descending frequencies. The
	 * elements 0..n-2 in the list are already in the correct order. Insertion is
	 * done by first finding the correct spot using binary search, then inserting at
	 * that spot.
	 *
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary
	 *         search process, null if the size of the input list is 1. This
	 *         returned array list is only used to test your code - it is not used
	 *         elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		int[] index = new int[occs.size()];
		for (int i = 0; i < occs.size(); i++) {
			index[i] = occs.get(i).frequency;
		}
		int search = occs.get(occs.size() - 1).frequency;
		return binarySearch(index, search); // Binary Search
	}
	//binary search helper method
	private ArrayList<Integer> binarySearch(int[] array, int val) {
		ArrayList<Integer> index = new ArrayList<Integer>();
		int l = 0;
		int r = array.length - 1;
		while (l <= r) {
			int mid = (l + r) / 2;
			index.add(mid);
			if (array[mid] == val) {
				return index;
			} else if (val < array[mid]) {
				l = mid + 1;
			} else {
				r = mid - 1;
			}
		}
		if (index.isEmpty()) {
			return null;
		} else {
			return index;
		}
	}
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all
	 * keywords, each of which is associated with an array list of Occurrence
	 * objects, arranged in decreasing frequencies of occurrence.
	 *
	 * @param docsFile       Name of file that has a list of all the document file
	 *                       names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise
	 *                       word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input
	 *                               files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) throws FileNotFoundException {
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String, Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2
	 * occurs in that document. Result set is arranged in descending order of
	 * document frequencies. (Note that a matching document will only appear once in
	 * the result.) Ties in frequency values are broken in favor of the first
	 * keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will take precedence over doc2 in
	 * the result. The result set is limited to 5 entries. If there are no matches
	 * at all, result is null.
	 *
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in
	 *         descending order of frequencies. The result size is limited to 5
	 *         documents. If there are no matches, returns null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		//If both lists are null
		if(kw1 == null && kw2 == null)
			return null;
		//Changes kw1 and kw2 to lowercase
		kw1 = kw1.toLowerCase();
		kw2 = kw2.toLowerCase();
		ArrayList<Occurrence> occ1 = new ArrayList<Occurrence>();
		//Adds occurrences of kw1 to occ1 list to be added to final result
		if(keywordsIndex.get(kw1) != null){
			occ1 = new ArrayList<Occurrence>();
			occ1.addAll(keywordsIndex.get(kw1));
		}
		//Adds occurrences of kw2 to occ2 to be added to final result
		ArrayList<Occurrence> occ2 = new ArrayList<Occurrence>();
		if(keywordsIndex.get(kw2) != null){
			occ2 = keywordsIndex.get(kw2);
			//Loops through occ1 and occ2
			for(int i = 0; i < occ2.size();i++){
				for(int j = 0; j < occ1.size();j++){
					//If freqeuncy is equal
					if(occ2.get(i).frequency == occ1.get(j).frequency){
						//Adds it to correct position in occ1
						occ1.add(j+1,occ2.get(i));
						j++;
					}
					//If frequency is greater
					else if(occ2.get(i).frequency > occ1.get(j).frequency){
						//Adds it in correct position of occ1
						occ1.add(j, occ2.get(i));
						j++;
					}
				}
			}
		}
		//Check empty
		if(occ1.isEmpty())
			return null;
		//Loop through occ1 since all occurrences have been store in occ1
		ArrayList<String> result = new ArrayList<String>();
		for(Occurrence x: occ1){
			if(!result.contains(x.document) && result.size() < 5)
				result.add(x.document);
		}
		return result;
	}
}