package br.ufrj.dcc.felipealmeida.fowl;

public class FowlException extends Exception {
	
	String mensagem;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7268316199183601070L;
	
	public FowlException(String erro) {
		this.mensagem = erro;
	}
	
	@Override
	public String getMessage() {
		return this.mensagem;
	}
	
}
