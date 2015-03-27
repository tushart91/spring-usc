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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
	private static final String[] fileName = {"name.csv", "addr.csv", 
		"city.csv", "type.csv"};
	private static final String[] algoName = {"Levenshtein", "Needleman-Wunch", "Smith_waterman",
		"Jaro-Winkler", "Soundex"};

	public static void main(String[] args) {

		AbstractStringMetric lev = new Levenshtein();
		AbstractStringMetric nw  = new NeedlemanWunch();
		AbstractStringMetric sw  = new SmithWaterman();
		AbstractStringMetric jw  = new JaroWinkler();
		AbstractStringMetric sx  = new Soundex();
		// this single line performs the similarity test
//		System.out.println(sw.getSimilarity("'bel-air hotel'", "'empire korea'"));
//		System.out.println(sw.getSimilarity("'bel-air hotel'", "ct"));
//		System.out.println(sw.getSimilarity("'bel-air hotel'", "'belvedere the'"));
//		System.out.println(sw.getSimilarity("'bel-air hotel'", "'hotel bel-air'"));

		// outputs the results
//		SimpleExample.outputResult(result, metric, str1, str2);
		
		//declarations
		Program obj = new Program();
		LinkedHashMap<String, List<String>>[] d1 = obj.get_d1(N);
		Set<String>[] d2 = obj.get_d2(N);
		String str1 = "";
		String str2 = "";
		String res[]  = {"", "", "", "", ""};
		float[] max = {-1, -1, -1, -1, -1};
		Iterator<String> it_D1 = null;
		Iterator<String> it_D2 = null;
		ArrayList<String> arr;
		File file = null;
		FileOutputStream fout = null;
		BufferedWriter out = null;
		
		//iterate over columns
		for (int i = 0; i < N; i++) {
			try {
				file = new File(fileName[i]);
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
				
				//clear evrything
				arr = new ArrayList<String>();
				max[LEV] = max[SW] = max[NW] = max[JW] = max[SX] =  -1;
				res[LEV] = res[SW] = res[NW] = res[JW] = res[SX] = "";
				
				//Iterate over d2.csv
				while (it_D2.hasNext()) {
					str2 = it_D2.next();
					compare(str2, res, max, lev.getSimilarity(str1, str2), LEV);
					compare(str2, res, max, nw.getSimilarity(str1, str2), NW);
					compare(str2, res, max, sw.getSimilarity(str1, str2), SW);
					compare(str2, res, max, jw.getSimilarity(str1, str2), JW);
					compare(str2, res, max, sx.getSimilarity(str1, str2), SX);
				}
				
				arr.add(res[LEV]);
				arr.add(res[SW]);
				arr.add(res[NW]);
				arr.add(res[JW]);
				arr.add(res[SX]);
				d1[i].put(str1, arr);
				//System.out.println(str1 + ": " + res[LEV] + " " + res[SW] + " " + res[NW] + " " + 
				//res[JW] + " " + res[SX]);
				write(out, str1, res);
			}
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void compare(String str, String[] res, float[] max, float result, Integer func) {
		if (result > max[func]) {
			max[func] = result;
			res[func] = str;
		}
	}
	
	public static void write(BufferedWriter out, String d1, String[] algo) {
		StringBuilder res = new StringBuilder(d1);
		for (String string : algo) {
			res.append("," + string);
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
	
	public void get_groundtruth() {
		
		//Initialize data structure
		HashMap<Integer, HashSet<String>>[] classToString = new HashMap[N];
		HashMap<String, HashSet<Integer>>[] stringToClass = new HashMap[N];
		HashSet<String> stringSet = null;
		HashSet<Integer> classSet = null;
		for (int i = 0; i < N; i++) {
			classToString[i] = new HashMap<Integer, HashSet<String>>();
			stringToClass[i]  = new HashMap<String, HashSet<Integer>>();
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
					
					//add to set and assign set to key in dict
					stringSet.add(parts[i]);
					classToString[i].put(klass, stringSet);
					
					//stringToClass HashSet
					if (stringToClass[i].containsKey(parts[i]))
						classSet = stringToClass[i].get(parts[i]);
					else
						classSet = new HashSet<Integer>();
						
					//add to set and assign set to key in dict
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

}
