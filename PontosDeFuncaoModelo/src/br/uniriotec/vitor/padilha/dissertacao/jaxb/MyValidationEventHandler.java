package br.uniriotec.vitor.padilha.dissertacao.jaxb;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

public class MyValidationEventHandler implements ValidationEventHandler{

	@Override
	public boolean handleEvent(ValidationEvent arg0) {
		System.out.println("Linha:"+arg0.getLocator().getLineNumber());
		return false;
	}

	
}
