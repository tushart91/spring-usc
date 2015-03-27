package edu.usc.iiotw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import uk.ac.shef.wit.simmetrics.similaritymetrics.*;
import uk.ac.shef.wit.simmetrics.SimpleExample;

public class Program {

	private static final Integer NAME = 0;
	private static final Integer ADDR = 1;
	private static final Integer CITY = 2;
	private static final Integer TYPE = 3;
	private static final Integer CLASS = 4;
	private static final Integer N    = 4;
	private static final Integer LEV  = 0;
	private static final Integer NW   = 1;
	private static final Integer SW   = 2;
	private static final Integer JW   = 3;
	private static final Integer SX   = 4;
	private static final Integer CS   = 5;
	private static final Integer L    = 6;
	private static final String[] fileName = {"name", "addr", "city", "type"};
	private static final String[] algoName = {"Levenshtein", "Needleman-Wunch", "Smith-Waterman",
		"Jaro-Winkler", "Soundex", "Cosine-Similarity"};

	public static void main(String[] args) {

		AbstractStringMetric lev = new Levenshtein();
		AbstractStringMetric nw  = new NeedlemanWunch();
		AbstractStringMetric sw  = new SmithWaterman();
		AbstractStringMetric jw  = new JaroWinkler();
		AbstractStringMetric sx  = new Soundex();
		AbstractStringMetric cs  = new CosineSimilarity();
		// this single line performs the similarity test
//		System.out.println(sw.getSimilarity("'french bistro'", "french"));
		// outputs the results
//		SimpleExample.outputResult(result, metric, str1, str2);
		
		//declarations for data structure
		Program obj = new Program();
		LinkedHashMap<String, List<String>>[] d1 = obj.get_d1(N);
		Set<String>[] d2 = obj.get_d2(N);
		LinkedHashMap<String, HashSet<Integer>>[] stringToClass = new LinkedHashMap[N];
		LinkedHashMap<Integer, HashSet<String>>[] classToString = new LinkedHashMap[N];
		obj.get_groundtruth(classToString, stringToClass);
		
		//declarations
		Integer[][] posMatches = new Integer[N][L];
		String str1 = "";
		String str2 = "";
		String res[]  = {"", "", "", "", "", ""};
		float[] max = {-1, -1, -1, -1, -1, -1};
		Iterator<String> it_D1 = null;
		Iterator<String> it_D2 = null;
		List<String> arr;
		File file = null;
		FileOutputStream fout = null;
		BufferedWriter out = null;
		File aFile = null;
		FileOutputStream aFout = null;
		BufferedWriter aOut = null;
		try {
			aFile = new File("accuracy.csv");
			aFout = new FileOutputStream(aFile);
			aOut = new BufferedWriter(new OutputStreamWriter(aFout));
			write(aOut, "column", algoName);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < L; j++) { 
				posMatches[i][j] = new Integer(0);
			}
		}
		
		//iterate over columns
		for (int i = 0; i < N; i++) {
			try {
				file = new File(fileName[i] + ".csv");
				fout = new FileOutputStream(file);
				out = new BufferedWriter(new OutputStreamWriter(fout));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			it_D1 = d1[i].keySet().iterator();
			
			//write csv headers
			write(out, "d1", algoName);
			
			//iterate over d1.csv
			while (it_D1.hasNext()) {
				
				str1 = it_D1.next();
				it_D2 = d2[i].iterator();
				
				//clear everything
				for (int j = 0; j < L; j++) {
					max[j] = -1;
					res[j] = "";
				}
				
				//Iterate over d2.csv
				while (it_D2.hasNext()) {
					str2 = it_D2.next();
					
					compare(str2, res, max, lev.getSimilarity(str1, str2), LEV);
					compare(str2, res, max, nw.getSimilarity(str1, str2), NW);
					compare(str2, res, max, sw.getSimilarity(str1, str2), SW);
					compare(str2, res, max, jw.getSimilarity(str1, str2), JW);
					compare(str2, res, max, sx.getSimilarity(str1, str2), SX);
					compare(str2, res, max, cs.getSimilarity(str1, str2), CS);
				}
				arr = new ArrayList<String>(Arrays.asList(res));
				d1[i].put(str1, arr);
				//System.out.println(str1 + ": " + res[LEV] + " " + res[SW] + " " + res[NW] + " " + 
				//res[JW] + " " + res[SX]);
				
				write(out, str1, res);
				accuracy(posMatches[i], stringToClass[i], classToString[i], str1, res);
			}
			closeBufferedWriter(out);
			closeFileOutputStream(fout);
			System.out.println(fileName[i]);
			for (int j = 0; j < L; j++) {
				System.out.println(algoName[j] + ": " + ": " + posMatches[i][j]);
			}
			write(aOut, fileName[i], posMatches[i]);
		}
		closeBufferedWriter(aOut);
		closeFileOutputStream(aFout);
	}
	
	private static void accuracy(Integer[] matches, 
			LinkedHashMap<String, HashSet<Integer>> stringToClass,
			LinkedHashMap<Integer, HashSet<String>> classToString, String str, String[] result) {
		HashSet<Integer> klass = null;
		Iterator<Integer> it = null;
		for (int i = 0; i < L; i++) {
			klass = stringToClass.get(str);
			it = klass.iterator();
//			Boolean flag = false;
			while(it.hasNext()) {
				if (classToString.get(it.next()).contains(result[i])) {
					matches[i]++;
//					flag  = true;
					break;
				}
			}
//			if (!flag) {
//				System.out.println(algoName[i] + " " + str + " " + result[i]);
//			}
		}
	}
	
	private static void compare(String str, String[] res, float[] max, float result, Integer func) {
		if (result > max[func]) {
			max[func] = result;
			res[func] = str;
		}
	}
	
	public static void write(BufferedWriter out, String d1, Object[] algo) {
		StringBuilder res = new StringBuilder(d1);
		
		for (Object string : algo) {
			if (string instanceof String)
				res.append("," + string);
			else if (string instanceof Integer)
				res.append("," + string.toString());
		}
		res.append("\n");
		try {
			out.write(res.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public LinkedHashMap<String, List<String>>[] get_d1(int N) {
		LinkedHashMap<String, List<String>>[] d1 = new LinkedHashMap[N];
		for (int i = 0; i < N; i++) {
			d1[i] = new LinkedHashMap<String, List<String>>();
		}

		// Get file from resources folder
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream("d1.csv");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		String line = "";
		String[] parts = null;
		try {
			reader.readLine();
			while ((line = reader.readLine()) != null) {
				parts = line.split(",", 4);
				d1[NAME].put(parts[NAME], null);
				d1[ADDR].put(parts[ADDR], null);
				d1[CITY].put(parts[CITY], null);
				d1[TYPE].put(parts[TYPE], null);
			}
			reader.close();
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return d1;
	}

	public Set<String>[] get_d2(int N) {
		Set<String>[] d2 = new LinkedHashSet[N];
		for (int i = 0; i < N; i++) {
			d2[i] = new LinkedHashSet<String>();
		}

		// Get file from resources folder
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream("d2.csv");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		String line = "";
		String[] parts = null;
		try {
			reader.readLine();
			while ((line = reader.readLine()) != null) {
				parts = line.split(",");
				d2[NAME].add(parts[NAME]);
				d2[ADDR].add(parts[ADDR]);
				d2[CITY].add(parts[CITY]);
				d2[TYPE].add(parts[TYPE]);
			}
			reader.close();
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return d2;
	}
	
	public void get_groundtruth(LinkedHashMap<Integer, HashSet<String>>[] classToString, 
			LinkedHashMap<String, HashSet<Integer>>[] stringToClass) {
		
		//Initialize data structure
		HashSet<String> stringSet = null;
		HashSet<Integer> classSet = null;
		for (int i = 0; i < N; i++) {
			classToString[i] = new LinkedHashMap<Integer, HashSet<String>>();
			stringToClass[i]  = new LinkedHashMap<String, HashSet<Integer>>();
		}
		
		// Get file from resources folder
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream("groundtruth.csv");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		String line = "";
		String[] parts = null;
		Integer klass = 0;
		
		//Read groundtruth.csv
		try {
			reader.readLine();
			while ((line = reader.readLine()) != null) {
				parts = line.split(",", 5);
				for (int i = 0; i < N; i++) {
					klass = Integer.parseInt(parts[CLASS]);
					//classToString HashSet
					if (classToString[i].containsKey(klass)) 
						stringSet = classToString[i].get(klass);
					else
						stringSet = new HashSet<String>();
					
					//add to set and assign set to key in dictionary
					stringSet.add(parts[i]);
					classToString[i].put(klass, stringSet);
					
					//stringToClass HashSet
					if (stringToClass[i].containsKey(parts[i]))
						classSet = stringToClass[i].get(parts[i]);
					else
						classSet = new HashSet<Integer>();
						
					//add to set and assign set to key in dictionary
					classSet.add(klass);
					stringToClass[i].put(parts[i], classSet);
				}
			}
			reader.close();
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void closeBufferedWriter(BufferedWriter out) {
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	private static void closeFileOutputStream(FileOutputStream fout) {
		try {
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

}
