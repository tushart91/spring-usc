package edu.usc.isi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;

public class Program {

	static private int output = 1;
	static private int person = 0;
	static private int city = 1;
	static private int generic = 2;
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
		String[] _args = {args[0] + "/input", args[0] + "/output"};
		args = _args;
		//File Initialize
		File outFile = null;
		FileOutputStream fout = null;
		BufferedWriter writer = null;
		try {
			outFile = new File("entities.csv");
			fout = new FileOutputStream(outFile);
			writer = new BufferedWriter(new OutputStreamWriter(fout));
			writer.write("uri,label\n");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Repository repo = null;
		File folder = new File(args[output]);
		File[] file = folder.listFiles();
		System.out.println("Calling REST API");
//		HttpClientPost.call(args);
		System.out.println("Output in " + args[output]);
		System.out.println();
		Map<ArrayList<String>, Integer>[] map = new HashMap[3];
		for (int i = 0; i < 3; i++) {
			map[i] = new HashMap<ArrayList<String>, Integer>();
		}
		for (int i = 0; i < file.length; i++) {
			if (file[i].getName().contains("DS_Store")) {
				i++;
				continue;
			}
			repo = addToStore(file[i]);
			System.out.println();
			String [] vars = {"exact", "name"};
			
			map[0] = query(repo, "person", vars, writer);
			
			map[1] = query(repo, "city", vars, writer);
			
			map[2] = query(repo, "generic", vars, writer);
			
		}
		
		try {
			writer.close();
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*
		 * Calculate F-Score
		 */
		
		//File Initialize
		File inFile = null;
		FileInputStream fin = null;
		BufferedReader reader = null;
		try {
			inFile = new File("tables.csv");
			fin = new FileInputStream(inFile);
			reader = new BufferedReader(new InputStreamReader(fin));
			reader.readLine();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			reader.close();
			fin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	/*
	 * Util for querying
	 */
	
	public static Map<ArrayList<String>, Integer> query(Repository repo, String query, String[] vars,
			BufferedWriter writer) {
		
		RepositoryConnection con = null;
		TupleQuery tupleQuery = null;
		TupleQueryResult result = null;
		BindingSet bindingSet = null;
		ArrayList<String> value = null;
		Map<ArrayList<String>, Integer> map = new HashMap<ArrayList<String>, Integer>();
		
		String queryString = getQuery(query);
		String temp = "";
		try {
			con = repo.getConnection();
			tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,
					queryString);
			result = tupleQuery.evaluate();
			while (result.hasNext()) {
				bindingSet = result.next();
				value = new ArrayList<String>();
				for (String var : vars) {
					temp = _replace(bindingSet.getValue(var));
					value.add(temp);
				}
				if (map.containsKey(value))
					map.put(value, map.get(value) + 1);
				else
					map.put(value, 1);
				
				try {
					temp = _replace(bindingSet.getValue("url")) +
							"," + _replace(bindingSet.getValue("exact")) + "\n";
					writer.write(temp);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		} finally {
			closeTupleQueryResult(result);
			closeRepositoryConnection(con);
		}
		printMap(map);
		return map;
	}
	
	public static String _replace(Value str) {
		return str.toString().replace("\n", " ");
	}
	
	/*
	 * Util for printing hashmap
	 */
	
	public static void printMap(Map<ArrayList<String>, Integer> map) {
		Iterator<ArrayList<String>> it = map.keySet().iterator();
		ArrayList<String> key = null;
		while (it.hasNext()) {
			key = it.next();
			System.out.println(key + ": " + map.get(key));
		}
		System.out.println();
	}
	
	/*
	 * add file received from calais API to repository
	 */
	
	public static Repository addToStore(File file) {
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
		
		Repository repo = new SailRepository(new MemoryStore());
		RepositoryConnection con = null;
		String baseURI = "http://s.opencalais.com/1/type/em";
		
		try {
			repo.initialize();
			con = repo.getConnection();
			con.add(file, baseURI, RDFFormat.RDFXML);
		}
		catch (RepositoryException e) {
			e.printStackTrace();
		} catch (RDFParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} finally {
			closeRepositoryConnection(con);
			closeBufferedReader(reader);
			closeInputStream(fin);
		}
		return repo;
	}
	
	/*
	 * Util for reading queries
	 */
	private static String getQuery(String q) {
		Program obj = new Program();
		InputStream in = obj.getFileWithUtil("query.ttl");
		Properties prop = new Properties();
		try {
			prop.load(in);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop.getProperty(q);
	}
	
	/*
	 * Util for reading schema file
	 */
	private InputStream getFileWithUtil(String fileName) {

		InputStream result = null;
		ClassLoader classLoader = getClass().getClassLoader();
		result = classLoader.getResourceAsStream(fileName);
		return result;
	}
	
	/*
	 * All the closures
	 */
	static void closeRepositoryConnection(RepositoryConnection conn) {
		try {
			conn.close();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	static void closeInputStream(InputStream in) {
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	static void closeOutputStream(OutputStream out) {
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	static void closeBufferedWriter(BufferedWriter out) {
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	static void closeBufferedReader(BufferedReader in) {
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	static void closeGraphQueryResult(GraphQueryResult result) {
		try {
			result.close();
		} catch (QueryEvaluationException e1) {
			e1.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	static void closeTupleQueryResult(TupleQueryResult result) {
		try {
			result.close();
		} catch (QueryEvaluationException e1) {
			e1.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

}
