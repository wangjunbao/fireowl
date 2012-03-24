package br.ufrj.dcc.felipealmeida.fowl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

public class EntidadesMgr {

	private double confidence;
	private int support;

	public EntidadesMgr() {
		this(0.0, 0);
	}

	EntidadesMgr(double confidence, int support) {
		this.confidence = confidence;
		this.support = support;
	}

	public List<Entidade> identificarEntidades(String texto) throws Exception {

		String jsonStr = consultarSpotlight(texto);
		List<Entidade> entidades = extrairEntidadesJson(jsonStr);

		return entidades;
	}

	private List<Entidade> extrairEntidadesJson(String jsonStr) {
		List<Entidade> entidades = new ArrayList<Entidade>();

		try {

			JSONObject jsonObj = new JSONObject(jsonStr);
			JSONArray resources = jsonObj.getJSONArray("Resources");

			for (int i = 0; i < resources.length(); i++) {
				JSONObject jsObj = resources.getJSONObject(i);

				String uri = jsObj.getString("@URI");

				Entidade entidade = new Entidade(uri);

				if (!entidades.contains(entidade)) {
					entidade.hit();
					entidades.add(entidade);
				} else {
					int idx = entidades.indexOf(entidade);
					Entidade entidadeExistente = entidades.get(idx);
					entidadeExistente.hit();
				}
			}

			Collections.sort(entidades);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return entidades;
	}

	private String consultarSpotlight(String texto) throws Exception {
		StringBuffer jsonStr = new StringBuffer();

		try {

			String encodedurl = String.format(Locale.US,
					"text=%s&confidence=%.1f&support=%d",
					URLEncoder.encode(texto, "UTF-8"), confidence, support);

			URL url = new URL("http://spotlight.dbpedia.org/rest/annotate?"
					+ encodedurl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				if (conn.getResponseCode() == 414) {
					throw new FowlException("Desculpe, mas o conteúdo da página é muito extenso para ser analisado. Tente um conteúdo mais curto");
				}
				else {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
				}
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;

			while ((output = br.readLine()) != null) {
				jsonStr.append(output);
			}

			conn.disconnect();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonStr.toString();
	}

	public static void definirRelacionamentos(List<Entidade> entidades) {
		for (Entidade ent : entidades) {
			consultarDefinirAssunto(ent);
		}
		
		for (Entidade e1 : entidades) {
			for (Entidade e2 : entidades) {
				
				if (!e1.equals(e2)) {
				
					List<String> assuntos_e1 = new ArrayList<String>(e1.getAssuntos());
					List<String> assuntos_e2 = new ArrayList<String>(e2.getAssuntos());
					
					assuntos_e1.retainAll(assuntos_e2);
					
					if (assuntos_e1.size() > 0) {
						e1.criarLigacao(e2, assuntos_e1.size());
					}
					
				}
				
			}
		}
		
	}

	private static void consultarDefinirAssunto(Entidade ent) {
		String queryString = String.format("PREFIX dcterms: <http://purl.org/dc/terms/> "
				+ " "
				+ "SELECT ?assunto WHERE { "
				+ "<%s> dcterms:subject ?assunto }",
				ent.getURI());
		
		Query query = QueryFactory.create(queryString);
		
//		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
		
		QueryEngineHTTP qexec = (QueryEngineHTTP) QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
		
		qexec.addParam("timeout", "5000");
		
		try {
			ResultSet results = qexec.execSelect();
			
			List<String> assuntos = new ArrayList<String>();
			while (results.hasNext()) {
				QuerySolution qs = results.next();
				assuntos.add(qs.get("assunto").toString());
			}
			
			ent.setAssuntos(assuntos);
		} catch (Exception e) {
			ent.setAssuntos(new ArrayList<String>());
		} finally {
			qexec.close();
		}
	}
	
	public static Dictionary<String, String> obterResumoInformacao(Entidade ent) {
		
		Dictionary<String, String> dados = new Hashtable<String, String>();
		
		String queryString = String.format("PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "PREFIX dbpedia: <http://dbpedia.org/resource/>"
				+ "PREFIX dbpedia2: <http://dbpedia.org/property/>"
				+ " "
				+ "SELECT ?label ?abstract WHERE {"
				+ "{ <%1$1s> dbpedia-owl:abstract ?abstract ."
				+ "<%1$1s> rdfs:label ?label ."
				+ "FILTER(lang(?label) = \"en\")"
				+ "FILTER(lang(?abstract) = \"en\") }"
				+ "UNION"
				+ "{ <%1$1s> dbpedia-owl:abstract ?abstract ."
				+ "<%1$1s> rdfs:label ?label ."
				+ "FILTER ( lang(?label) = \"pt\" )"
				+ "FILTER ( lang(?abstract) = \"pt\" ) } }", 
				ent.getURI());
		
		Query query = QueryFactory.create(queryString);
		
		QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);
		
		try {
			ResultSet results = qexec.execSelect();
			
			if (results.hasNext()) {
				QuerySolution qs = results.next();
				
				String l = qs.get("label").toString();
				String a = qs.get("abstract").toString();
				
				dados.put("label", l.substring(0, l.length() - 3));
				dados.put("abstract", a.substring(0, a.length() - 3));
			}
				
		} finally {
			qexec.close();
		}
		
		
		return dados;
	}
	
	
	
	

}
