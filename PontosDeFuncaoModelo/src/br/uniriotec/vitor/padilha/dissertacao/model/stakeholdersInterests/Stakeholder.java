package br.uniriotec.vitor.padilha.dissertacao.model.stakeholdersInterests;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import br.uniriotec.vitor.padilha.dissertacao.model.XmlFunctionPointElementWithParent;

@XmlType(name="stakeholder")
public class Stakeholder extends XmlFunctionPointElementWithParent<StakeholderInterests>{
	private String name;
	
	private Integer weight;

	@XmlAttribute(name="name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(name="weight")
	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}
}
