package edu.usc.hw10;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

public class Program {

	private static final Integer NAME = 0;
	private static final Integer N = 4;

	public static void main(String[] args) {
		
		Program obj = new Program();
		LinkedHashMap<String, Integer> d1 = obj.get_d("data/d1.csv", N);
		LinkedHashMap<String, Integer> d2 = obj.get_d("data/d2.csv", N);
		
	}

	public LinkedHashMap<String, Integer> get_d(String fileName, int N) {
		LinkedHashMap<String, Integer> d = new LinkedHashMap<String, Integer>();

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
				d.put(parts[NAME].toLowerCase(), i++);
			}
			reader.close();
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return d;
	}

	public HashMap<String, String> get_result() {

		//Initialize data structure
		HashMap<String, String> map = new HashMap<String, String>();
		
		// Get file from resources folder
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream("results.csv");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		String line = "";
		String[] parts = null;
		
		//Read results.csv
		try {
			reader.readLine();
			while ((line = reader.readLine()) != null) {
				parts = line.split(",", 8);
				map.put(parts[0].toLowerCase(), parts[4].toLowerCase());
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
		InputStream inputStream = classLoader.getResourceAsStream("groundtruth.csv");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		String line = "";
		String[] parts = null;
		String key = null;
		
		//Read results.csv
		try {
			reader.readLine();
			while ((line = reader.readLine()) != null) {
				parts = line.split(",", 8);
				key = parts[0].toLowerCase();
				line = reader.readLine();
				parts = line.split(",", 8);
				map.put(key, parts[0].toLowerCase());
			}
			reader.close();
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

}
