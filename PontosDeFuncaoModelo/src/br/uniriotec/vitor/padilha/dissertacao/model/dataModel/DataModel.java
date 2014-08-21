package br.uniriotec.vitor.padilha.dissertacao.model.dataModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;

import br.uniriotec.vitor.padilha.dissertacao.exception.ElementException;
import br.uniriotec.vitor.padilha.dissertacao.model.ElementValidator;
import br.uniriotec.vitor.padilha.dissertacao.model.FunctionPointSystem;
import br.uniriotec.vitor.padilha.dissertacao.model.XmlFunctionPointElementWithParent;

@XmlType(name="data-model")
public class DataModel extends XmlFunctionPointElementWithParent<FunctionPointSystem> implements ElementValidator{

	private List<DataModelElement> dataModelElements;
	

	@XmlElementRefs({
        @XmlElementRef(name = "eif", type = EIF.class),
        @XmlElementRef(name = "ilf", type = ILF.class)
    })
	public List<DataModelElement> getDataModelElements() {
		return dataModelElements;
	}

	public void setDataModelElements(List<DataModelElement> dataModelElements) {
		this.dataModelElements = dataModelElements;
	}

	@Override
	public boolean validate() throws ElementException {
		for(DataModelElement dataModelElement:getDataModelElements()) {
			if(!dataModelElement.validate())
				return false;
		}
		return true;
	}

	@Override
	public void charge() {
		for(DataModelElement dataModelElement:getDataModelElements()) {
			dataModelElement.charge();
		}		
	}

	public String doDot(List<DataModelElement> baseDataElements) {
		String returnValue="";
		Map<String,DataModelElement> baseNames = new HashMap<String, DataModelElement>();
		for(DataModelElement dataModelElement:baseDataElements) {
			baseNames.put(dataModelElement.getName(),dataModelElement);
		}
		for(DataModelElement dataModelElement:getDataModelElements()) {
			returnValue+=dataModelElement.getName();
			returnValue+="[";
			if(!baseNames.containsKey(dataModelElement.getName())) {
				returnValue+="color=red fontcolor=red";
			}
			else if(!baseNames.get(dataModelElement.getName()).getType().equals(dataModelElement.getType())) {
				returnValue+="color=blue fontcolor=blue";
			}
			returnValue+=" shape=box]\n";
		}
		return returnValue;
	}
}
