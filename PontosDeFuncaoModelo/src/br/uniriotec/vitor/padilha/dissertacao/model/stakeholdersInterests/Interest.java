package br.uniriotec.vitor.padilha.dissertacao.model.stakeholdersInterests;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import br.uniriotec.vitor.padilha.dissertacao.model.XmlFunctionPointElementWithParent;
import br.uniriotec.vitor.padilha.dissertacao.model.transactionModel.Transaction;

@XmlType(name="interest")
public class Interest extends XmlFunctionPointElementWithParent<StakeholderInterests>{

	private String stakeholderXML;
	
	private String transactionXML;
	
	private Stakeholder stakeholder;
	
	private Transaction transaction;
	
	private Long interest;

	@XmlAttribute(name="stakeholder")
	public String getStakeholderXML() {
		return stakeholderXML;
	}

	public void setStakeholderXML(String stakeholderXML) {
		this.stakeholderXML = stakeholderXML;
	}

	@XmlAttribute(name="transactionRef")
	public String getTransactionXML() {
		return transactionXML;
	}

	public void setTransactionXML(String transactionXML) {
		this.transactionXML = transactionXML;
	}

	@XmlTransient
	public Stakeholder getStakeholder() {
		return stakeholder;
	}

	public void setStakeholder(Stakeholder stakeholder) {
		this.stakeholder = stakeholder;
	}

	@XmlTransient
	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	@XmlAttribute(name="value")
	public Long getInterest() {
		return interest;
	}

	public void setInterest(Long interest) {
		this.interest = interest;
	}
}
