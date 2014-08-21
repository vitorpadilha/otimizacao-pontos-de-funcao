package br.uniriotec.vitor.padilha.dissertacao.model.transactionModel;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import br.uniriotec.vitor.padilha.dissertacao.exception.ElementException;
import br.uniriotec.vitor.padilha.dissertacao.model.ElementValidator;
import br.uniriotec.vitor.padilha.dissertacao.model.XmlFunctionPointElementWithParent;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.DataModelElement;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.RET;

@XmlType(name="ftr")
public class FTR extends XmlFunctionPointElementWithParent<Transaction> implements ElementValidator{

	private List<FTRField> fields;
	
	private String name;  
	private String ret; 
	private String dataModelElement;
	private Boolean useAllDets;
	private RET retRef;

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@XmlAttribute
	public String getRet() {
		return ret;
	}

	public void setRet(String ret) {
		this.ret = ret;
	}
	@XmlAttribute
	public String getDataModelElement() {
		return dataModelElement;
	}

	public void setDataModelElement(String dataModelElement) {
		this.dataModelElement = dataModelElement;
	}
	@XmlAttribute
	public Boolean getUseAllDets() {
		return useAllDets;
	}

	public void setUseAllDets(Boolean useAllFields) {
		this.useAllDets = useAllFields;
	}

	@XmlElement(name="det")
	public List<FTRField> getFields() {
		return fields;
	}

	public void setFields(List<FTRField> fields) {
		this.fields = fields;
	}

	@XmlTransient
	public RET getRetRef() {
		return retRef;
	}

	public void setSubsetRef(RET subsetRef) {
		this.retRef = subsetRef;
	}

	@Override
	public boolean validate() throws ElementException {
		if(getName()==null || getName().equals(""))
		{
			throw new ElementException("Nome obrigatório",this);
		}		
		if(this.retRef==null) {
			throw new ElementException("Elemento: "+getDataModelElement()+"."+getRet()+" não encontrado", this);
		}
		
		if(getFields()==null && (getUseAllDets()==null || !getUseAllDets())){
			throw new ElementException("FTR sem campos: "+getDataModelElement()+"."+getRet()+"", this);
		}
		if(getFields()!=null) {
			for (FTRField ftrField : this.getFields()) {
				if(!ftrField.validate()) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void charge() {
		String referencia = getName();
		if(getRet()!=null && !getRet().equals("")) {
			referencia=getRet();
		}
		
		if(getRet()==null || getRet().equals("")){
			setRet(getName());
		}
		if(getDataModelElement()==null || getDataModelElement().equals("")){
			setDataModelElement(getName());
		}
		for(DataModelElement modelElement:getParent().getParent().getParent().getDataModel().getDataModelElements()){
			if(modelElement.getName()!=null && modelElement.getName().equals(getDataModelElement())) {
				for(RET subset:modelElement.getRets()){
					if(subset.getName().equals(referencia)) {
						this.retRef = subset;
					}
				}
			}
		}
		if(getFields()!=null) {
			for(FTRField ftrField:getFields()) {
				ftrField.charge();
			}
		}		
	}
}
