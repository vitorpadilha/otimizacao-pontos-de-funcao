package br.uniriotec.vitor.padilha.dissertacao.model.dataModel;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.uniriotec.vitor.padilha.dissertacao.exception.ElementException;
import br.uniriotec.vitor.padilha.dissertacao.model.ElementValidator;
import br.uniriotec.vitor.padilha.dissertacao.model.XmlFunctionPointElementWithParent;
import br.uniriotec.vitor.padilha.dissertacao.model.constants.DataModelElementType;

public abstract class DataModelElement extends XmlFunctionPointElementWithParent<DataModel> implements ElementValidator{
	
	private List<RET> rets;
	
	private String name;
	
	private DataModelElementType type;
	
	private Integer functionsPointValue = 0;
	
	@XmlTransient
	public DataModelElementType getType() {
		return type;
	}
	public void setType(DataModelElementType type){
		this.type = type;
	}
	
	@XmlElement(required=true,name="ret")
	public List<RET> getRets() {
		return rets;
	}

	public void setRets(List<RET> rets) {
		this.rets = rets;
	}

	@XmlAttribute(required=true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlTransient
	public Integer getFunctionsPointValue() {
		return functionsPointValue;
	}
	
	public void setFunctionsPointValue(Integer functionsPointValue) {
		this.functionsPointValue = functionsPointValue;
	}
	
	@Override
	public boolean validate() throws ElementException {
		if(this.getName()==null || this.getName().equals(""))
			throw new ElementException("Nome obrigatório",this);
		for(RET ret:getRets()) {
			if(!ret.validate())
				return false;
		}
		return true;
	}
	@Override
	public void charge() {
		for(RET ret:getRets()) {
			ret.charge();
		}		
	}
}
