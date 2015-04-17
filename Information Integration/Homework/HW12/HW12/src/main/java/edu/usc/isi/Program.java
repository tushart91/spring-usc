package edu.usc.isi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;

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
	
	public static void main(String[] args) {
		
		Repository repo = null;
		File folder = new File(args[output]);
		File[] file = folder.listFiles();
		for (int i = 0; i < file.length; i++) {
			System.out.println("Calling REST API");
			//HttpClientPost.call(args);
			System.out.println("Output in " + args[1]);
			System.out.println();
			if (file[i].getName().contains("DS_Store")) {
				i++;
			}
			repo = addToStore(file[i]);
//			query(repo, "person");
//			query(repo, "city");
			System.out.println();
			String [] vars = {"name", "exact"};
			query(repo, "org", vars);
		}
	}
	
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
	 * Util for querying
	 */
	
	public static void query(Repository repo, String query, String[] vars) {
		
		RepositoryConnection con = null;
		TupleQuery tupleQuery = null;
		TupleQueryResult result = null;
		BindingSet bindingSet = null;
		ArrayList<String> value = new ArrayList<String>();
		
		String queryString = getQuery(query);
		try {
			con = repo.getConnection();
			tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,
					queryString);
			result = tupleQuery.evaluate();
			while (result.hasNext()) {
				bindingSet = result.next();
				value.clear();
				for (String var : vars) {
					value.add(bindingSet.getValue(var).toString().replace("\n", " "));
				}
				System.out.println(value);
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
