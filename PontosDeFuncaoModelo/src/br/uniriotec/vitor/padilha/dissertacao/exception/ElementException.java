package br.uniriotec.vitor.padilha.dissertacao.exception;

import br.uniriotec.vitor.padilha.dissertacao.model.XmlFunctionPointElement;

public class ElementException extends Exception{

	public ElementException(String string, XmlFunctionPointElement object) {
		super(string+". Linha ="+object.getLocation().getLineNumber());

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1989337073693739112L;

}
