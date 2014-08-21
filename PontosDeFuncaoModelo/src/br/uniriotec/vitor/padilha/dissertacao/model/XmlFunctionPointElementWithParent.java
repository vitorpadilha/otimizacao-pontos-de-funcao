package br.uniriotec.vitor.padilha.dissertacao.model;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlTransient;

public abstract class XmlFunctionPointElementWithParent<T extends XmlFunctionPointElement> extends XmlFunctionPointElement{
	
	private T parent;

	@SuppressWarnings("unchecked")
	public void afterUnmarshal(Unmarshaller u, Object a) {
		if(a!=null)
			this.parent = (T) a;
	}

	@XmlTransient
	public T getParent() {
		return parent;
	}

	public void setParent(T parent) {
		this.parent = parent;
	} 
	

}
