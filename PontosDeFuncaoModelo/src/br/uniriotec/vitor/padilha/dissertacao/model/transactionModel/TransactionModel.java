package br.uniriotec.vitor.padilha.dissertacao.model.transactionModel;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import br.uniriotec.vitor.padilha.dissertacao.exception.ElementException;
import br.uniriotec.vitor.padilha.dissertacao.model.ElementValidator;
import br.uniriotec.vitor.padilha.dissertacao.model.FunctionPointSystem;
import br.uniriotec.vitor.padilha.dissertacao.model.XmlFunctionPointElementWithParent;

@XmlType(name="transaction-model")
public class TransactionModel extends XmlFunctionPointElementWithParent<FunctionPointSystem> implements ElementValidator{

	
	private List<Transaction> transactions;

	@XmlElement(required=false,name="transaction")
	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	@Override
	public boolean validate() throws ElementException {
		for(Transaction transaction:getTransactions()){
			if(!transaction.validate()){
				return false;
			}
		}
		return true;
	}
	public String doDot(List<Transaction> baseTransaction, boolean showDataModel) {
		List<String> baseNames = new ArrayList<String>();
		for(Transaction transaction:baseTransaction) {
			baseNames.add(transaction.getName());
		}
		String retorno = "";
		if(getTransactions()!=null) {
			for(Transaction transaction:this.getTransactions()){
				boolean present = baseNames.contains(transaction.getName());
				String dotTransaction = transaction.doDot(present);
				if(!dotTransaction.equals(""))
					retorno+=dotTransaction;
				if(showDataModel) {
					for(FTR	ftr:transaction.getFtrList()){
						retorno+=transaction.getName()+"->"+ftr.getName()+"[arrowType=none";
						if(!present)
							retorno+=" color=red";
						retorno+="]\n";
					}
				}
			}
		}
		retorno += "";
		return retorno;
		
	}

	@Override
	public void charge() {
		for(Transaction transaction:getTransactions()){
			transaction.charge();
		}
	}
}
