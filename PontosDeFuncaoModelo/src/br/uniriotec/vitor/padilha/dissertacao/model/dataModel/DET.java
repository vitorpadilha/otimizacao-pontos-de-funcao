package br.uniriotec.vitor.padilha.dissertacao.model.dataModel;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import br.uniriotec.vitor.padilha.dissertacao.exception.ElementException;
import br.uniriotec.vitor.padilha.dissertacao.model.ElementValidator;
import br.uniriotec.vitor.padilha.dissertacao.model.XmlFunctionPointElementWithParent;

@XmlType(name="det")
public class DET extends XmlFunctionPointElementWithParent<RET> implements ElementValidator{

	private String name;
	
	private String ref;
	
	private String description;
	
	private Boolean primaryKey;
	
	private Boolean hasSemanticMeaning;

	private String dataModelElement;
	
	private RET retRef;
	
	private Boolean flagcanBeDetInTransation;
	
	private boolean implementada;
	
	@XmlAttribute(required=true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(required=false)
	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	@XmlAttribute(required=false)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean validate() throws ElementException {
		if(getName()==null || getName().equals(""))
		{
			throw new ElementException("Nome obrigatório",this);
		}
		if(getRef()!=null && !getRef().equals("")) {
			if(getRetRef()==null) {
				throw new ElementException("Elemento: "+getDataModelElement()+"."+getRef()+" não encontrado", this);
			}
		}
	
		return true;
	}

	@XmlAttribute(required=false)
	public String getDataModelElement() {
		return dataModelElement;
	}

	public void setDataModelElement(String dataModelElement) {
		this.dataModelElement = dataModelElement;
	}

	@XmlAttribute(required=false)
	public Boolean getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(Boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	@XmlTransient
	public RET getRetRef() {
		return retRef;
	}

	public void setRetRef(RET retRef) {
		this.retRef = retRef;
	}

	@XmlTransient
	public Boolean getFlagcanBeDetInTransation() {
		return flagcanBeDetInTransation;
	}

	public void setFlagcanBeDetInTransation(Boolean flagcanBeDetInTransation) {
		this.flagcanBeDetInTransation = flagcanBeDetInTransation;
	}
	
	@XmlTransient
	public boolean isImplementada() {
		return implementada;
	}

	public void setImplementada(boolean implementada) {
		this.implementada = implementada;
	}

	@Override
	public void charge() {
		if(getRef()!=null && !getRef().equals("")) {
			if(getDataModelElement()==null || getDataModelElement().equals("")){
				setDataModelElement(getRef());
			}
			for(DataModelElement modelElement:getParent().getParent().getParent().getDataModelElements()){
				if(modelElement.getName()!=null && modelElement.getName().equals(getDataModelElement())) {
					for(RET ret:modelElement.getRets()){
						if(ret.getName().equals(getRef())) {
							setRetRef(ret);
						}
					}
				}
			}
		}		
	}

	@XmlAttribute(required=false)
	public Boolean getHasSemanticMeaning() {
		return hasSemanticMeaning;
	}

	public void setHasSemanticMeaning(Boolean hasSemanticMeaning) {
		this.hasSemanticMeaning = hasSemanticMeaning;
	}
}
