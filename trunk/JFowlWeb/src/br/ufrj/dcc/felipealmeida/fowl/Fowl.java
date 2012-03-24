package br.ufrj.dcc.felipealmeida.fowl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;

public class Fowl {

	public static Dictionary<String, String> obterConhecimento(String url) throws FowlException {
		// extração de conteúdo
		String conteudo = "";
		try {
		PaginaParser pp = new PaginaParser();
		conteudo = pp.extrairConteudo(url);
		if (conteudo.length() == 0) throw new Exception();
		}
		catch (Exception e) {
			throw new FowlException("Não foi possível extrair o conteúdo desta página.");
		}
		
		// configuracao da confidence
		double conf = 0.0;
		String [] tokens_conteudo = conteudo.split(" "); 
		System.out.println("tamanho do texto: " + tokens_conteudo.length);
		if (tokens_conteudo.length > 200 /*&& tokens_conteudo.length <= 700*/)
			conf = 0.7;
		else
			conf = 0.2;
			
		// identificação de entidades
		List<Entidade> entidades = new ArrayList<Entidade>();
		try {
		EntidadesMgr em = new EntidadesMgr(conf, 0);
		entidades = em.identificarEntidades(conteudo);
		if (entidades.size() == 0) throw new Exception();
		}
		catch (Exception e) {
			if (e instanceof FowlException) {
				throw (FowlException) e;
			}
			else {
				throw new FowlException("Não foi possível identificar as entidades desta página.");
			}
		}

		// criação dos relacionamentos entre entidades
		try {
			EntidadesMgr.definirRelacionamentos(entidades);
		} catch (Exception e) {
			throw new FowlException("Não foi possível definir os relacionamentos entre as entidades desta página.");
		}
		
		// descoberta da principal entidade
		Collections.sort(entidades);
		Entidade entidade_principal = entidades.get(0);

		// obtenção de conteúdo relacionado a entidade principal
		Dictionary<String, String> dados = null;
		try {
		dados = EntidadesMgr.obterResumoInformacao(entidade_principal);
		} catch (Exception e) {
			throw new FowlException("Não foi possível identificar a entidade principal desta página.");
		}

		return dados;
	}

}
