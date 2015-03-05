package edu.usc.iiotw;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import info.aduna.iteration.Iterations;

import org.apache.commons.io.IOUtils;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sparql.SPARQLRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.sail.memory.MemoryStore;

public class Homework {

	static String dbpedia = "http://dbpedia.org/resource/";
	static String schema = "http://schema.org/";
	static String dublin = "http://purl.org/dc/terms/";

	public static void main(String args[]) {

		Repository rep = new SailRepository(new MemoryStore());
		Repository dbpedia = new SPARQLRepository("http://dbpedia.org/sparql");
		RepositoryConnection conn = null;
		String queryString = "BASE                  <http://dbpedia.org/resource/>"
				+ "PREFIX dbpedia-owl:   <http://dbpedia.org/ontology/>"
				+ "PREFIX dbpprop:       <http://dbpedia.org/property/>"
				+ "SELECT ?university, ?name WHERE"
				+ "{    ?university dbpprop:type <Private_university>    ."
				+ "     ?university dbpedia-owl:state <California>    ."
				+ "     ?university rdfs:label ?name    ." + "}";

		TupleQuery tupleQuery = null;
		TupleQueryResult result = null;

		try {
			rep.initialize();

			dbpedia.initialize();
			conn = dbpedia.getConnection();
			tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL,
					queryString);
			conn.close();
			conn = rep.getConnection();
			result = null;
			result = tupleQuery.evaluate();

			ValueFactory f = rep.getValueFactory();
			URI priUni_uri = f
					.createURI(Homework.dbpedia, "Private_university");

			while (result.hasNext()) { // iterate over the result
				BindingSet bindingSet = result.next();
				Value university = bindingSet.getValue("university");
				Value name = bindingSet.getValue("name");
				URI subject = f.createURI(university.toString());
				String object = name.toString().substring(1,
						name.toString().length() - 4);
				String language = name.toString().substring(
						name.toString().length() - 2);
				try {
					conn.add(subject, RDF.TYPE, priUni_uri);
					conn.add(subject, RDFS.LABEL,
							f.createLiteral(object, language));
				} catch (RepositoryException e) {
					System.out.println("a");
					e.printStackTrace();
				}
			}

			RepositoryResult<Statement> statements;
			statements = conn.getStatements(null, null, null, true);

			Model model = generateModel(statements);

			System.out.println("Question 1\n");
			Rio.write(model, System.out, RDFFormat.TURTLE);
			System.out.println("\n\nQuestion 3:\n");
			rep = question3(rep);
			System.out.println("\n\nQuestion 4:\n");
			question4(rep, "4");
			System.out.println("\n\nQuestion 5&6:\n");
			rep = question5(rep);
			System.out.println("\n\nQuestion 7&8:\n");
			Map<String, Repository> map;
			map = question7(rep);
			System.out.println("\n\nQuestion 9:\n");
			map = question9(map);
			System.out.println("\n\nQuestion 10:\n");
			question10(map);
		} catch (RepositoryException e1) {
			e1.printStackTrace();
		} catch (MalformedQueryException e1) {
			e1.printStackTrace();
		} catch (QueryEvaluationException e1) {
			e1.printStackTrace();
		} catch (RDFHandlerException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} finally {
			closeTupleQueryResult(result);
			closeRepositoryConnection(conn);
		}
	}

	/*
	 * Question 10
	 */
	public static void question10(Map<String, Repository> map) {
		RepositoryConnection conn = null;
		RepositoryConnection fc_conn = null;
		Repository rep = map.get("rep");
		Repository fc_rep = map.get("fc_rep");
		String queryString = "PREFIX dbpedia: <http://dbpedia.org/resource/>"
				+ "PREFIX schema:  <http://schema.org/> "
				+ "PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "SELECT ?person WHERE"
				+ "{    ?person rdf:type schema:Person }";
		TupleQuery tupleQuery = null;
		TupleQuery fc_tupleQuery = null;
		BufferedWriter out = null;
		TupleQueryResult result = null;
		TupleQueryResult fc_result = null;
		try {
			conn = rep.getConnection();
			fc_conn = fc_rep.getConnection();
			tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL,
					queryString);
			fc_tupleQuery = fc_conn.prepareTupleQuery(QueryLanguage.SPARQL,
					queryString);

			result = tupleQuery.evaluate();
			fc_result = fc_tupleQuery.evaluate();
			out = new BufferedWriter(new FileWriter("q10.txt", false));
			System.out.println("Non-FC:");
			out.write("Non-FC:");
			BindingSet bindingSet = null;
			String value = null;
			while (result.hasNext()) { // iterate over the result
				bindingSet = result.next();
				value = bindingSet.getValue("person").toString();
				out.write(value + "\n");
				System.out.println(value);
			}
			System.out.println("\nFC:");
			out.write("\nFC:\n");
			while (fc_result.hasNext()) { // iterate over the result
				bindingSet = fc_result.next();
				value = bindingSet.getValue("person").toString();
				out.write(value + "\n");
				System.out.println(value);
			}

		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} finally {
			closeBufferedWriter(out);
			closeTupleQueryResult(result);
			closeRepositoryConnection(conn);
			closeTupleQueryResult(fc_result);
			closeRepositoryConnection(fc_conn);
		}
	}

	/*
	 * Question 9
	 */

	public static Map<String, Repository> question9(Map<String, Repository> map) {
		RepositoryConnection conn = null;
		RepositoryConnection fc_conn = null;
		Repository rep = map.get("rep");
		Repository fc_rep = map.get("fc_rep");
		ValueFactory f = null;
		URI max = null, usc = null, alumni = null;
		try {
			conn = rep.getConnection();
			f = rep.getValueFactory();
			max = f.createURI(dbpedia, "C._L._Max_Nikias");
			usc = f.createURI(dbpedia, "University_of_Southern_California");
			alumni = f.createURI(schema, "alumni");
			conn.add(usc, alumni, max);

			fc_conn = fc_rep.getConnection();
			f = fc_rep.getValueFactory();
			max = f.createURI(dbpedia, "C._L._Max_Nikias");
			usc = f.createURI(dbpedia, "University_of_Southern_California");
			alumni = f.createURI(schema, "alumni");
			fc_conn.add(usc, alumni, max);
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} finally {
			closeRepositoryConnection(conn);
			closeRepositoryConnection(fc_conn);
		}
		map.put("fc_rep", fc_rep);
		map.put("rep", rep);
		return map;
	}

	/*
	 * Question 7 & 8
	 */

	public static Map<String, Repository> question7(Repository rep) {
		RepositoryConnection conn = null;
		RepositoryConnection fc_conn = null;
		Repository fc_rep = new SailRepository(
				new ForwardChainingRDFSInferencer(new MemoryStore()));
		try {
			conn = rep.getConnection();
			fc_rep.initialize();
			fc_conn = fc_rep.getConnection();
			RepositoryResult<Statement> statements = conn.getStatements(null,
					null, null, true);
			fc_conn.add(statements);
			conn.clear();
			conn.add(fc_conn.getStatements(null, null, null, true));
		} catch (RepositoryException e) {
			e.printStackTrace();
		} finally {
			closeRepositoryConnection(fc_conn);
			closeRepositoryConnection(conn);
		}
		question4(fc_rep, "8");
		Map<String, Repository> map = new HashMap<String, Repository>();
		map.put("fc_rep", fc_rep);
		map.put("rep", rep);
		return map;
	}

	/*
	 * Question 5 and 6
	 */

	public static Repository question5(Repository rep) {
		RepositoryConnection conn = null;
		InputStream in = null;
		try {
			conn = rep.getConnection();
			// Get file from resources folder
			Homework obj = new Homework();
			in = obj.getFileWithUtil("all.nt");
			conn.add(in, schema, RDFFormat.NTRIPLES);
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RDFParseException e) {
			e.printStackTrace();
		} finally {
			closeInputStream(in);
			closeRepositoryConnection(conn);
		}
		question4(rep, "6");
		return rep;
	}

	/*
	 * Question 4
	 */

	public static void question4(Repository rep, String q) {

		String queryString = "PREFIX dbpedia: <http://dbpedia.org/resource/>"
				+ "PREFIX schema:  <http://schema.org/> "
				+ "PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "SELECT ?university WHERE"
				+ "{    ?university rdf:type schema:Organization  }";
		TupleQuery tupleQuery = null;
		RepositoryConnection conn = null;
		BufferedWriter out = null;
		TupleQueryResult result = null;
		try {
			conn = rep.getConnection();
			tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL,
					queryString);

			out = new BufferedWriter(new FileWriter("q" + q + ".txt", false));
			result = tupleQuery.evaluate();
			while (result.hasNext()) { // iterate over the result
				BindingSet bindingSet = result.next();
				String value = bindingSet.getValue("university").toString();
				out.write(value + "\n");
				System.out.println(value);
			}
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} finally {
			closeBufferedWriter(out);
			closeTupleQueryResult(result);
			closeRepositoryConnection(conn);
		}
	}

	/*
	 * Question 3:
	 */

	public static Repository question3(Repository rep) {
		RepositoryConnection conn = null;
		RepositoryConnection new_conn = null;
		Repository new_rep = new SailRepository(new MemoryStore());

		String queryString = "PREFIX schema:  <http://schema.org/> "
				+ "PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX dbpedia: <http://dbpedia.org/resource/> "
				+ "CONSTRUCT {"
				+ "     ?university rdf:type schema:CollegeOrUniversity ."
				+ "     ?university schema:name ?name ." + "} WHERE"
				+ "{    ?university rdf:type dbpedia:Private_university ."
				+ "     ?university rdfs:label ?name ." + "}";

		GraphQuery graphQuery = null;
		GraphQueryResult result = null;
		OutputStream out = null;
		try {
			new_rep.initialize();
			new_conn = new_rep.getConnection();
			conn = rep.getConnection();

			graphQuery = conn.prepareGraphQuery(QueryLanguage.SPARQL,
					queryString);
			result = graphQuery.evaluate();

			new_conn.add(result);
			out = new FileOutputStream("q3.turtle");

			printRep(new_conn, System.out);
			printRep(new_conn, out);
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		finally {
			closeOutputStream(out);
			closeGraphQueryResult(result);
			closeRepositoryConnection(conn);
			closeRepositoryConnection(new_conn);
		}
		return new_rep;
	}

	private InputStream getFileWithUtil(String fileName) {

		InputStream result = null;

		ClassLoader classLoader = getClass().getClassLoader();
		result = classLoader.getResourceAsStream(fileName);

		return result;
	}

	static void printRep(RepositoryConnection conn, OutputStream out) {
		RepositoryResult<Statement> statements = null;
		Model model = null;
		try {
			statements = conn.getStatements(null, null, null, true);

			model = generateModel(statements);

			Rio.write(model, out, RDFFormat.TURTLE);
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (RDFHandlerException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	static void printRep(Repository rep, OutputStream out) {
		RepositoryResult<Statement> statements = null;
		Model model = null;
		RepositoryConnection conn = null;
		try {
			conn = rep.getConnection();
			statements = conn.getStatements(null, null, null, true);

			model = generateModel(statements);

			Rio.write(model, out, RDFFormat.TURTLE);
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (RDFHandlerException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} finally {
			closeRepositoryConnection(conn);
		}
	}

	/*
	 * Construct model for Rio
	 */
	static Model generateModel(RepositoryResult<Statement> statements) {

		Model model = null;
		try {
			model = Iterations.addAll(statements, new LinkedHashModel());
		} catch (RepositoryException e) {
			e.printStackTrace();
		}

		model.setNamespace("rdf", RDF.NAMESPACE);
		model.setNamespace("rdfs", RDFS.NAMESPACE);
		model.setNamespace("foaf", FOAF.NAMESPACE);
		model.setNamespace("schema", schema);
		model.setNamespace("dbpedia", dbpedia);
		model.setNamespace("dc", dublin);
		return model;
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
