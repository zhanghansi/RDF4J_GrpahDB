package org.oc2kb;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.ModelFactory;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;


public class RDFTOKB {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			// step 1: read and prepare the triples strings
			List<List<String>> triples = new ArrayList<List<String>>();
			List<String> subject = new ArrayList<String>();
			List<String> object = new ArrayList<String>();  
			List<String> predicat = new ArrayList<String>();
			String[] triples_input = new String[3];
			triples_input[0] = "/Users/hansizhang/Dropbox (UFL)/project/oc-2-kb/sample_data/aSubject.txt";
			triples_input[1] = "/Users/hansizhang/Dropbox (UFL)/project/oc-2-kb/sample_data/bObject.txt";
			triples_input[2] = "/Users/hansizhang/Dropbox (UFL)/project/oc-2-kb/sample_data/cPredicate.txt";
			triples.add(subject);
			triples.add(object);
			triples.add(predicat);
			for(int i=0; i<3; i++) {
				try (Stream<String> stream = Files.lines(Paths.get(triples_input[i]))) {
					triples.get(i).addAll(stream.collect(Collectors.toList()));
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}
			}
			// step 2: create RDF triples using RDF4J
			ValueFactory factory = SimpleValueFactory.getInstance();
			String Namespace = "http://obesity-cancer-uf/";
			Model model = new LinkedHashModel();
	            for(int j=0;j<triples.get(0).size();j++){
	            	IRI s = factory.createIRI(Namespace, triples.get(0).get(j).replace(" ", "_"));
	            	IRI o = factory.createIRI(Namespace, triples.get(1).get(j).replace(" ", "_"));
	            	IRI p = factory.createIRI(Namespace, triples.get(2).get(j).replace(" ", "_")); 
	                //instance1.addProperty(hasName, instance2); 
	            	Statement statement = factory.createStatement(s, p, o);
	                model.add(statement); // add the statement (triple) to the model
	            }
	            
	        // step 3: write to RDF file
//            FileOutputStream out = new FileOutputStream("/Users/hansizhang/Desktop/oc2kb.rdf");
//            RDFWriter writer = Rio.createWriter(RDFFormat.RDFXML, out);
//            try {
//            	 Rio.write(model, out, RDFFormat.RDFXML);
//            }
//            finally {
//            	 out.close();
//            }
//	        
	        
	        
			// step 4: write RDF model to GraphDB
	        RepositoryManager repositoryManager = new RemoteRepositoryManager( "http://localhost:7200/" );
			repositoryManager.initialize();
			Repository repository = repositoryManager.getRepository("test");

			RepositoryConnection repositoryConnection = repository.getConnection();

//			repositoryConnection.add(model);   // add RDF statements to GrpahDB
			
//			try (RepositoryResult<Statement> result = repositoryConnection.getStatements(null, null, null);) {
//				while (result.hasNext()) {
//					Statement st = result.next();
//					System.out.println("db contains: " + st);
//				}
//			}
			String queryString = "SELECT * WHERE { <http://obesity-cancer-uf/obesity> ?p ?o} ";
			TupleQuery tupleQuery = repositoryConnection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
			try (TupleQueryResult result = tupleQuery.evaluate()) {
			      while (result.hasNext()) {  // iterate over the result
			    	  BindingSet bindingSet = result.next();
//			    	  Value valueOfX = bindingSet.getValue("x");
			    	  Value valueOfP = bindingSet.getValue("p");
			    	  Value valueOfO = bindingSet.getValue("o");
//			    	  System.out.println(valueOfX);
			    	  System.out.println(valueOfP);
			    	  System.out.println(valueOfO);

			      }
			   }

			repositoryConnection.close();
			repository.shutDown();
			repositoryManager.shutDown();

		}catch(Exception ex){
            System.err.println("Error extractRDF: " + ex.toString());
        }

	}

}
