package edu.usc.hw10;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class Program {

	private static final Integer NAME = 0;
	private static final Integer N = 4;

	public static void main(String[] args) {
		
		Program obj = new Program();
		
		//Initialize data structure
		HashMap<String, Integer> d1 = obj.get_d("data/d1.csv", N);
		HashMap<String, Integer> d2 = obj.get_d("data/d2.csv", N);
		LinkedHashMap<String, String> result = obj.get_results();
		HashMap<String, String> gt = obj.get_groundtruth();
		
		//Initialize
		File file = null;
		FileOutputStream fout = null;
		BufferedWriter out = null;
		try {
			file = new File("output.csv");
			fout = new FileOutputStream(file);
			out = new BufferedWriter(new OutputStreamWriter(fout));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		//Line number for output
		String str = null;
		Float count = 0f;
		Iterator<String> it = result.keySet().iterator();
		try{
			out.write("d1,d2\n");
			
			while(it.hasNext()) {
				str = it.next();
				System.out.println(str + ": " + d1.get(str) + ", " + d2.get(result.get(str)));
				out.write(d1.get(str) + "," + d2.get(result.get(str)) + "\n");
				
				//count for true positive matches
				if (result.get(str).equalsIgnoreCase(gt.get(str)))
					count++;
				else {
					System.out.println();
					System.out.println("No matches for:");
					System.out.println(str + " : " + result.get(str) + " - " + gt.get(str));
					System.out.println();
				}
				
			}
			out.close();
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Compute F-Score
		System.out.println(count.intValue() + " true positive matches");
		Float precision = count / result.size();
		System.out.println("Precision: " + precision);
		Float recall = count / gt.size();
		System.out.println("Recall: " + recall);
		Float fscore = 2 * ((precision * recall)/(precision + recall));
		System.out.println("F-Score: " + fscore);
	}

	public HashMap<String, Integer> get_d(String fileName, int N) {
		HashMap<String, Integer> d = new HashMap<String, Integer>();

		// Get file from resources folder
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(fileName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		String line = "";
		String[] parts = null;
		Integer i = 1;
		try {
			reader.readLine();
			while ((line = reader.readLine()) != null) {
				parts = line.split(",", 4);
				d.put(parts[NAME].trim().toLowerCase(), i++);
			}
			reader.close();
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return d;
	}

	public LinkedHashMap<String, String> get_results() {

		//Initialize data structure
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		
		// Get file from resources folder
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream("data/results.csv");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		String line = "";
		String[] parts = null;
		//Read results.csv
		try {
			reader.readLine();
			while ((line = reader.readLine()) != null) {
				parts = line.replace("\"", "").split(",", 9);
				map.put(parts[4].toLowerCase(), parts[0].toLowerCase());
			}
			reader.close();
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public HashMap<String, String> get_groundtruth() {

		//Initialize data structure
		HashMap<String, String> map = new HashMap<String, String>();
		
		// Get file from resources folder
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream("data/groundtruth.csv");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		String line = "";
		String[] parts = null;
		String key = null;
		
		//Read groundtruth.csv
		try {
			reader.readLine();
			while ((line = reader.readLine()) != null) {
				parts = line.split(",", 4);
				key = parts[0].toLowerCase();
				line = reader.readLine();
				parts = line.split(",", 4);
				map.put(parts[0].toLowerCase(), key);
			}
			reader.close();
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

}
