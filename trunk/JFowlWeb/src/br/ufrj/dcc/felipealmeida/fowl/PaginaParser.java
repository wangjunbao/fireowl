package br.ufrj.dcc.felipealmeida.fowl;

import java.net.MalformedURLException;
import java.net.URL;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.*;

public class PaginaParser {
	
	public String extrairConteudo(String urlStr) {
		String text = "";
		URL url = null;
		
		try {
			url = new URL(urlStr);
			
			String text_ae = ArticleExtractor.INSTANCE.getText(url);
			String text_de = DefaultExtractor.INSTANCE.getText(url);
			
			text = text_ae;
			
			// se o DefaultExtractor extrair um texto com mais que o dobro do tamanho
			// do ArticleExtractor, usa o Default.
			if ((text_de.length() * 1.0) / text_ae.length() > 2) {
				text = text_de;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BoilerpipeProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
			// TODO: handle exception
			// SocketException: pode se causado por Connection Reset
		}
		
		return text;
		
	}

}
