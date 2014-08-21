package br.uniriotec.vitor.padilha.dissertacao.model.transactionModel;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import br.uniriotec.vitor.padilha.dissertacao.exception.ElementException;
import br.uniriotec.vitor.padilha.dissertacao.model.ElementValidator;
import br.uniriotec.vitor.padilha.dissertacao.model.XmlFunctionPointElementWithParent;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.DET;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.DataModelElement;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.RET;

@XmlType(name="FTRfield")
public class FTRField extends XmlFunctionPointElementWithParent<FTR> implements ElementValidator{

	private String name;
	
	private DET field;

	@XmlAttribute(required=true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@XmlTransient
	public DET getField() {
		return field;
	}

	public void setField(DET field) {
		this.field = field;
	}

	@Override
	public boolean validate() throws ElementException {
	
		if(getName()==null || getName().equals(""))
		{
			throw new ElementException("Nome obrigatório",this);
		}
		if(getField()==null) {
			throw new ElementException("Campo não encontrado: "+getParent().getName()+" - "+getName(),this);
		}
		return true;
	}

	@Override
	public void charge() {
		for(DataModelElement dataModelElement:getParent().getParent().getParent().getParent().getDataModel().getDataModelElements()){
			if(dataModelElement.getName().equals(getParent().getDataModelElement())) {
				for(RET ret:dataModelElement.getRets()){
					if(ret.getName().equals(getParent().getRet())){
						for(DET field:ret.getDets()){
							if(field.getName().equals(getName())) {
								setField(field);
							}
						}
					}
				}
			}
		}		
	}
}
