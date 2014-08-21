package br.uniriotec.vitor.padilha.dissertacao.model.dataModel;

import javax.xml.bind.annotation.XmlRootElement;

import br.uniriotec.vitor.padilha.dissertacao.model.ElementValidator;
import br.uniriotec.vitor.padilha.dissertacao.model.constants.DataModelElementType;

@XmlRootElement(name="ilf")
public class ILF extends DataModelElement implements ElementValidator{

	public ILF(){
		setType(DataModelElementType.ILF);
	}
	

}
