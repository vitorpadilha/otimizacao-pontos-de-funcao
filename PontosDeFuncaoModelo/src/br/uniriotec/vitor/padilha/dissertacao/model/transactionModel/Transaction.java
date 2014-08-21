package br.uniriotec.vitor.padilha.dissertacao.model.transactionModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import br.uniriotec.vitor.padilha.dissertacao.exception.ElementException;
import br.uniriotec.vitor.padilha.dissertacao.model.ElementValidator;
import br.uniriotec.vitor.padilha.dissertacao.model.XmlFunctionPointElementWithParent;
import br.uniriotec.vitor.padilha.dissertacao.model.constants.TransactionType;

@XmlType(name="transaction",propOrder={"ftrList","dependencies"})
public class Transaction extends XmlFunctionPointElementWithParent<TransactionModel> implements ElementValidator{
	
	private String name;
	
	private Boolean errorMsg;
	
	private TransactionType type;
	
	private List<Dependency> dependencies;
	
	private List<FTR> ftrList;
	
	private Integer extraDET;
	
	private int releaseImplementation;

	@XmlAttribute(required=true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(required=false)
	public Boolean getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(Boolean errorMsg) {
		this.errorMsg = errorMsg;
	}

	@XmlAttribute(required=true)
	public TransactionType getType() {
		return type;
	}

	public void setType(TransactionType type) {
		this.type = type;
	}

	@XmlElementWrapper(name = "dependencies") 
	@XmlElement(name="dependency")
	public List<Dependency> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<Dependency> dependencies) {
		this.dependencies = dependencies;
	}

	@XmlElementWrapper(name = "ftr-list") 
	@XmlElement(name="ftr")
	public List<FTR> getFtrList() {
		return ftrList;
	}

	public void setFtrList(List<FTR> ftrList) {
		this.ftrList = ftrList;
	}

	public void eliminateDependendyTransactions(Transaction[] transactions) {			
		for(int i=0;i<transactions.length;i++){
			Transaction transaction = transactions[i];
			if(transaction!=null && transaction.getDependencies()!=null) {
				for(Dependency dependency:transaction.getDependencies()) {
					if(dependency.getTransactionDependency().equals(this)){
						this.getParent().getTransactions().remove(transaction);
						transactions[i] = null;
						transaction.eliminateDependendyTransactions(transactions);
						//System.out.println("Excluída transacao:"+transaction.getName()+" para: "+this.getName());
					}
				}
			}			
		}
	}
	
	public Set<Transaction> getDependendyTransactions(Transaction transaction, Set<Transaction> addedTransactions) {
		if( addedTransactions == null)
			addedTransactions = new HashSet<Transaction>();		
		if(transaction.getDependencies()!=null) {
			for(Dependency dependency:transaction.getDependencies()){
				if(!dependency.getTransactionDependency().getName().equals(transaction.getName()) && !addedTransactions.contains(dependency.getTransactionDependency())){
					addedTransactions.add(dependency.getTransactionDependency());
					addedTransactions.addAll(getDependendyTransactions(dependency.getTransactionDependency(),addedTransactions));
				}
			}
		}
		return addedTransactions;
	}
	@Override
	public boolean validate() throws ElementException {
		if(getName()==null || getName().equals(""))
		{
			throw new ElementException("Nome obrigatório!",this);
		}
		if(getType()==null || getType().equals("") || (!getType().equals(TransactionType.EI) && !getType().equals(TransactionType.EO) && !getType().equals(TransactionType.EQ)))
		{
			throw new ElementException("Tipo de transação inválida!",this);
		}
		if(getFtrList()!=null)
			for(FTR ftr:getFtrList()) {
				if(!ftr.validate()) {
					return false;
				}
			}
		if(getDependencies()!=null)
			for(Dependency dependency:getDependencies()) {
				if(!dependency.validate()) {
					return false;
				}
			}
		return true;
	}
	public String doDot(boolean present) {
		String retorno = "";
		if(!present)
			retorno+=getName()+"[color=red fontcolor=red]\n";
		if(this.getReleaseImplementation()>0){
			switch (this.getReleaseImplementation()) {
			case 1:
				retorno+=getName()+"[color=blue fontcolor=blue]\n";
				break;
			case 2:
				retorno+=getName()+"[color=yellow fontcolor=yellow]\n";
				break;
			case 3:
				retorno+=getName()+"[color=green fontcolor=green]\n";
				break;
			case 4:
				retorno+=getName()+"[color=orange fontcolor=orange]\n";
				break;
			case 5:
				retorno+=getName()+"[color=purple fontcolor=purple]\n";
				break;
			case 6:
				retorno+=getName()+"[color=gray fontcolor=gray]\n";
				break;
			default:
				break;
			}
		}
		if(getDependencies()!=null) {	
			for(Dependency dependency:getDependencies()) {
				retorno+=getName()+"->"+dependency.getRef();
				retorno+="[";
				if(dependency.getCanBeWeak())
					retorno+="style=dotted ";
				retorno+=((present)?"":"color=red");
				retorno+="]";
				retorno+="\n";
			}
		}
		return retorno;
	}

	@Override
	public void charge() {
		if(getFtrList()!=null)
			for(FTR ftr:getFtrList()) {
				ftr.charge();
			}
	
		if(getDependencies()!=null)
			for(Dependency dependency:getDependencies()) {
				dependency.charge();
			}		
	}

	@XmlTransient
	public int getReleaseImplementation() {
		return releaseImplementation;
	}


	public void setReleaseImplementation(int releaseImplementation) {
		this.releaseImplementation = releaseImplementation;
	}

	@XmlAttribute(required=false)
	public Integer getExtraDET() {
		return extraDET;
	}

	public void setExtraDET(Integer extraDET) {
		this.extraDET = extraDET;
	}
	

}
