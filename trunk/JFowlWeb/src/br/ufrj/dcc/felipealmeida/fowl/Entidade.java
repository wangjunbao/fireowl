package br.ufrj.dcc.felipealmeida.fowl;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

public class Entidade implements Comparable<Entidade> {
	
	private String URI;
	private int hits;
	private Set<Entidade> relacionados;
	private Dictionary<Entidade, Integer> qtdRelacionamentos;
	private List<String> assuntos;
	
	// construtores
	
	public Entidade(String URI) {
		this.URI = URI;
		
		this.hits = 0;
		this.relacionados = new HashSet<Entidade>();
		this.qtdRelacionamentos = new Hashtable<Entidade, Integer>();
	}
	
	// URI
	
	public String getURI() {
		return URI;
	}

	public void setURI(String uri) {
		URI = uri;
	}
	
	// Hits
	
	public void hit() {
		this.hits++;
	}
	
	public Integer getHits() {
		return hits;
	}

	// Relacionamentos e Grau
	
	public void criarLigacao(Entidade outraEntidade, int qtd) {
		if (!relacionados.contains(outraEntidade)) {
			relacionados.add(outraEntidade);
			qtdRelacionamentos.put(outraEntidade, new Integer(qtd));
		}
	}
	
	public Set<Entidade> getRelacionados() {
		return relacionados;
	}

	public Integer getGrau() {
		int grau = 0;
		
		for (Entidade e : relacionados) {
			grau += qtdRelacionamentos.get(e);
		}
		
		return grau;
	}
	
	// assuntos 
	
	public List<String> getAssuntos() {
		return assuntos;
	}

	public void setAssuntos(List<String> assuntos) {
		this.assuntos = assuntos;
	}
	
	// metodos sobrescritos
	
	@Override
	public boolean equals(Object ent) {
		boolean igual = false;
		
		Entidade outraEntidade = (Entidade) ent;
		
		if (this.getURI().equals(outraEntidade.getURI())) {
			igual = true;
		}
		
		return igual;
	}
	
	@Override
	public String toString() {
		return URI;
	}

	@Override
	public int compareTo(Entidade e) {
		
		Integer meuTotal = this.getHits() + this.getGrau();
		
		Integer outroTotal = e.getHits() + e.getGrau();
		
		if (-meuTotal.compareTo(outroTotal) == 0)
			return -this.getHits().compareTo(e.getHits());
	
		return -meuTotal.compareTo(outroTotal);
	}


}
