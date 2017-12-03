package org.oc2kb;

import java.io.File;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;

public class RDF4J {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RepositoryManager repositoryManager =
		        new RemoteRepositoryManager( "http://localhost:7200" );
		repositoryManager.initialize();
		
		
		// Get the repository from repository manager, note the repository id set in configuration .ttl file
		Repository repository = repositoryManager.getRepository("oc2kbtest");

		// Open a connection to this repository
		RepositoryConnection repositoryConnection = repository.getConnection();
		
		// ... use the repository
		String queryString = "SELECT * WHERE { ?x ?p <http://obesity-cancer-uf/cancer> } ";
		TupleQuery tupleQuery = repositoryConnection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
//		TupleQueryResult result = tupleQuery.evaluate();
		try (TupleQueryResult result = tupleQuery.evaluate()) {
		      while (result.hasNext()) {  // iterate over the result
		    	  BindingSet bindingSet = result.next();
		    	  Value valueOfX = bindingSet.getValue("x");
		    	  Value valueOfP = bindingSet.getValue("p");
//		    	  Value valueOfO = bindingSet.getValue("o");
		    	  System.out.println(valueOfX);
		    	  System.out.println(valueOfP);
//		    	  System.out.println(valueOfO);
		   // do something interesting with the values here...
		      }
		   }
		
		
		// Shutdown connection, repository and manager
		repositoryConnection.close();
		repository.shutDown();
		repositoryManager.shutDown();
	}

}
