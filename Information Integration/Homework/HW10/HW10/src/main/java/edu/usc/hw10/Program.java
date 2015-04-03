package edu.usc.hw10;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
		LinkedHashMap<String, String> output = obj.get_output();
		HashMap<String, String> gt = obj.get_groundtruth();
		
		//Initialize
		File file = null;
		FileOutputStream fout = null;
		BufferedWriter out = null;
		try {
			file = new File("data/results.csv");
			fout = new FileOutputStream(file);
			out = new BufferedWriter(new OutputStreamWriter(fout));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		//Line number for output
		String str = null;
		Float count = 0f;
		Iterator<String> it = output.keySet().iterator();
		try{
			out.write("d1,d2\n");
			
			while(it.hasNext()) {
				str = it.next();
//				System.out.println(str + ": " + d1.get(str) + ", " + d2.get(output.get(str)));
				out.write(d1.get(str) + "," + d2.get(output.get(str)) + "\n");
				
				//count for true positive matches
				if (output.get(str).equalsIgnoreCase(gt.get(str)))
					count++;
				else {
					System.out.println();
					System.out.println("No matches for:");
					System.out.println(str + " : " + output.get(str) + " - " + gt.get(str));
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
		Float precision = count / output.size();
		System.out.println("Precision: " + precision);
		Float recall = count / gt.size();
		System.out.println("Recall: " + recall);
		Float fscore = 2 * ((precision * recall)/(precision + recall));
		System.out.println("F-Score: " + fscore);
	}

	public HashMap<String, Integer> get_d(String fileName, int N) {
		HashMap<String, Integer> d = new HashMap<String, Integer>();

		// Get file from resources folder
		
		File file = new File(fileName);
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			System.out.println(fileName + " not found");
			System.exit(0);
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
		
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
			fin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return d;
	}

	public LinkedHashMap<String, String> get_output() {

		//Initialize data structure
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		
		// Get file from resources folder
		
		File file = new File("data/output.csv");
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			System.out.println("output.csv not found");
			e1.printStackTrace();
			System.exit(0);
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
		
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
			fin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public HashMap<String, String> get_groundtruth() {

		//Initialize data structure
		HashMap<String, String> map = new HashMap<String, String>();
		
		// Get file from resources folder
		
		File file = new File("data/groundtruth.csv");
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			System.out.println("groundtruth.csv not found");
			e1.printStackTrace();
			System.exit(0);
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
		
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
			fin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

}
