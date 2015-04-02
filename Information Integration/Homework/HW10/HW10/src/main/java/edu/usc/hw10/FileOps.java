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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileOps {

	void convertd1(String filename) {
		
		// Get file from resources folder
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(filename);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		File file = new File("d1.csv");
		FileOutputStream fout = null;
		BufferedWriter out = null;
		try {
			fout = new FileOutputStream(file);
			out = new BufferedWriter(new OutputStreamWriter(fout));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		String line = "";
		
		String namePatternString = "(.*?)\\d+.*\\d+/\\s?\\d+-\\d+";
		Pattern namePattern = Pattern.compile(namePatternString);
		String addressPatternString = "(.*?)\\d+/\\s?\\d+-\\d+";
		Pattern addressPattern = Pattern.compile(addressPatternString);
		String phonePatternString = "(\\d+/\\s?\\d+-\\d+)";
		Pattern phonePattern = Pattern.compile(phonePatternString);
		Matcher matcher = null;
		String arr = null;
		String origLine = null;
		int i = 0;
		try {
			out.write("name,addr,phone,type\n");
			while ((line = reader.readLine()) != null) {
				arr = "";
//				System.out.println(line);
				line = line.trim();
				origLine = line;
				System.out.println(i++);
				
				try {
					//Match Name
					matcher = namePattern.matcher(line);
					if (matcher.find() && matcher.groupCount() == 1 && 
							!matcher.group(1).trim().equalsIgnoreCase("")) {
//						System.out.println(matcher.group(1));
						arr = matcher.group(1);
						out.write(arr.trim() + ",");
						line = line.replace(arr, "");
					} else throw new NullPointerException();
					
					//Match Address
					matcher = addressPattern.matcher(line);
					if (matcher.find() && matcher.groupCount() == 1 && 
							!matcher.group(1).trim().equalsIgnoreCase("")) { 
	//					System.out.println(matcher.group(1));
						arr = matcher.group(1);
						out.write(arr.trim() + ",");
						line = line.replace(arr, "");
					} else throw new NullPointerException();
						
					//Match Phone
					matcher = phonePattern.matcher(line);
					if (matcher.find() && matcher.groupCount() == 1 && 
							!matcher.group(1).trim().equalsIgnoreCase("")) {
//						System.out.println(matcher.group(1));
						arr = matcher.group(1);
						out.write(arr.trim() + ",");
						line = line.replace(arr, "");
					} else throw new NullPointerException();
				}
				
				catch(NullPointerException e) {	
					System.out.println(origLine);
				}
				finally {
					//Remaining Type
//					System.out.println(line);
					out.write(line.trim() + "\n");
				}
				
//				break;
			}
			reader.close();
			inputStream.close();
			out.close();
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
void convertd2(String filename) {
		
		// Get file from resources folder
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(filename);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		File file = new File("d2.csv");
		FileOutputStream fout = null;
		BufferedWriter out = null;
		try {
			fout = new FileOutputStream(file);
			out = new BufferedWriter(new OutputStreamWriter(fout));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		String line = "";
		
		String namePatternString = "(.*?)\\d+.*\\d+-\\d+-\\d+";
		Pattern namePattern = Pattern.compile(namePatternString);
		String addressPatternString = "(.*?)\\d+-\\d+-\\d+";
		Pattern addressPattern = Pattern.compile(addressPatternString);
		String phonePatternString = "(\\d+-\\d+-\\d+)";
		Pattern phonePattern = Pattern.compile(phonePatternString);
		Matcher matcher = null;
		String arr = null;
		String origLine = null;
		int i = 0;
		try {
			out.write("name,addr,phone,type\n");
			while ((line = reader.readLine()) != null) {
				arr = "";
//				System.out.println(line);
				line = line.trim();
				origLine = line;
				System.out.println(i++);
				
				try {
					//Match Name
					matcher = namePattern.matcher(line);
					if (matcher.find() && matcher.groupCount() == 1 && 
							!matcher.group(1).trim().equalsIgnoreCase("")) {
//						System.out.println(matcher.group(1));
						arr = matcher.group(1);
						out.write(arr.trim() + ",");
						line = line.replace(arr, "");
					} else throw new NullPointerException();
					
					//Match Address
					matcher = addressPattern.matcher(line);
					if (matcher.find() && matcher.groupCount() == 1 && 
							!matcher.group(1).trim().equalsIgnoreCase("")) { 
	//					System.out.println(matcher.group(1));
						arr = matcher.group(1);
						out.write(arr.trim() + ",");
						line = line.replace(arr, "");
					} else throw new NullPointerException();
						
					//Match Phone
					matcher = phonePattern.matcher(line);
					if (matcher.find() && matcher.groupCount() == 1 && 
							!matcher.group(1).trim().equalsIgnoreCase("")) {
//						System.out.println(matcher.group(1));
						arr = matcher.group(1);
						out.write(arr.trim() + ",");
						line = line.replace(arr, "");
					} else throw new NullPointerException();
				}
				
				catch(NullPointerException e) {	
					System.out.println(origLine);
				}
				finally {
					//Remaining Type
//					System.out.println(line);
					out.write(line.trim() + "\n");
				}
				
//				break;
			}
			reader.close();
			inputStream.close();
			out.close();
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

void convertgt(String filename) {
	
	// Get file from resources folder
	ClassLoader classLoader = getClass().getClassLoader();
	InputStream inputStream = classLoader.getResourceAsStream(filename);
	BufferedReader reader = new BufferedReader(new InputStreamReader(
			inputStream));
	File file = new File("groundtruth.csv");
	FileOutputStream fout = null;
	BufferedWriter out = null;
	try {
		fout = new FileOutputStream(file);
		out = new BufferedWriter(new OutputStreamWriter(fout));
	} catch (FileNotFoundException e1) {
		e1.printStackTrace();
	}
	String line = "";
	
	String namePatternString = null;
	Pattern namePattern = null;
	String addressPatternString = null;
	Pattern addressPattern = null;
	String phonePatternString = null;
	Pattern phonePattern = null;
	Matcher matcher = null;
	String arr = null;
	String origLine = null;
	int i = 0;
	Boolean flag = true;
	try {
		out.write("name,addr,phone,type\n");
		while ((line = reader.readLine()) != null) {
			
			if (line.startsWith("#")) continue;
			if (line.trim().equalsIgnoreCase("")) continue;
			if (flag) {
				namePatternString = "(.*?)\\d+.*\\d+-\\d+-\\d+";
				addressPatternString = "(.*?)\\d+-\\d+-\\d+";
				phonePatternString = "(\\d+-\\d+-\\d+)";
			}
			else {
				namePatternString = "(.*?)\\d+.*\\d+/\\s?\\d+-\\d+";
				addressPatternString = "(.*?)\\d+/\\s?\\d+-\\d+";
				phonePatternString = "(\\d+/\\s?\\d+-\\d+)";
			}
			namePattern = Pattern.compile(namePatternString);
			addressPattern = Pattern.compile(addressPatternString);
			phonePattern = Pattern.compile(phonePatternString);
			flag = !flag;
			arr = "";
//			System.out.println(line);
			line = line.trim();
			origLine = line;
			System.out.println(i++);
			
			try {
				//Match Name
				matcher = namePattern.matcher(line);
				if (matcher.find() && matcher.groupCount() == 1 && 
						!matcher.group(1).trim().equalsIgnoreCase("")) {
//					System.out.println(matcher.group(1));
					arr = matcher.group(1);
					out.write(arr.trim() + ",");
					line = line.replace(arr, "");
				} else throw new NullPointerException();
				
				//Match Address
				matcher = addressPattern.matcher(line);
				if (matcher.find() && matcher.groupCount() == 1 && 
						!matcher.group(1).trim().equalsIgnoreCase("")) { 
//					System.out.println(matcher.group(1));
					arr = matcher.group(1);
					out.write(arr.trim() + ",");
					line = line.replace(arr, "");
				} else throw new NullPointerException();
					
				//Match Phone
				matcher = phonePattern.matcher(line);
				if (matcher.find() && matcher.groupCount() == 1 && 
						!matcher.group(1).trim().equalsIgnoreCase("")) {
//					System.out.println(matcher.group(1));
					arr = matcher.group(1);
					out.write(arr.trim() + ",");
					line = line.replace(arr, "");
				} else throw new NullPointerException();
			}
			
			catch(NullPointerException e) {	
				System.out.println(origLine);
			}
			finally {
				//Remaining Type
//				System.out.println(line);
				out.write(line.trim() + "\n");
			}
			
//			break;
		}
		reader.close();
		inputStream.close();
		out.close();
		fout.close();
	} catch (IOException e) {
		e.printStackTrace();
	}
}
}
