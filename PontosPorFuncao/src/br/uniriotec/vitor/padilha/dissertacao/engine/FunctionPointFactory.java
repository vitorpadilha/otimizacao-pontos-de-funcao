package br.uniriotec.vitor.padilha.dissertacao.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jmetal.base.variable.Binary;
import br.uniriotec.vitor.padilha.dissertacao.exception.CloneException;
import br.uniriotec.vitor.padilha.dissertacao.exception.ElementException;
import br.uniriotec.vitor.padilha.dissertacao.model.FunctionPointSystem;
import br.uniriotec.vitor.padilha.dissertacao.model.XmlFunctionPointElement;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.DataModelElement;
import br.uniriotec.vitor.padilha.dissertacao.model.transactionModel.Transaction;

public class FunctionPointFactory {

	private Map<String,DataModelElement> dataModelElements = new HashMap<String, DataModelElement>();
	
	public Map<String, DataModelElement> getDataModelElements() {
		return dataModelElements;
	}
	public void setDataModelElements(Map<String, DataModelElement> dataModelElements) {
		this.dataModelElements = dataModelElements;
	}
	public static FunctionPointSystem getFunctionPointSystem(Boolean[] representBinary, FunctionPointSystem functionPointSystemReference, boolean eliminateRetsAndDets) {
		Binary binary = new Binary(representBinary.length);
		for(int i=0;i<representBinary.length;i++){
			binary.bits_.set(i, representBinary[i]);
		}
		return FunctionPointFactory.getFunctionPointSystemConstructiveWay(binary, functionPointSystemReference, eliminateRetsAndDets);
	}
	/**
	 * Retorna uma contagem de pontos de função a partir de um BitSet (ex: 010010100111111001)
	 * @param bitset
	 * @param functionPointSystemReference
	 * @return
	 */
	public static FunctionPointSystem getFunctionPointSystem2(Binary binary, FunctionPointSystem functionPointSystemReference, boolean eliminateRetsAndDets) {
		FunctionPointSystem functionPointSystem=null;
		try {
			//functionPointSystem = FunctionsPointReader.clone(functionPointSystemReference);
			XmlFunctionPointElement.clones = new HashMap<Object, Object>();
			functionPointSystem = (FunctionPointSystem) functionPointSystemReference.cloneElement();
			Transaction[] transactionsInArray = new Transaction[binary.getNumberOfBits()];
			int cont = 0;
			for (int a = 0; a < functionPointSystem.getTransactionModel().getTransactions().size(); a++) {
				Transaction transaction = functionPointSystem.getTransactionModel().getTransactions().get(a);
				if(transaction.getReleaseImplementation()==0) {
					transactionsInArray[cont] = functionPointSystem.getTransactionModel().getTransactions().get(a);
					cont++;
				}
			}
			functionPointSystem.charge();
			for (int a = 0; a < transactionsInArray.length; a++) {
				if(!binary.bits_.get(a) && transactionsInArray[a]!=null) {
					Transaction transaction = (Transaction) transactionsInArray[a];
					transaction.eliminateDependendyTransactions(transactionsInArray);
					functionPointSystem.getTransactionModel().getTransactions().remove(transaction);	
					transactionsInArray[a] = null;
				}
			}
			
			functionPointSystem.clear(eliminateRetsAndDets);
			if(functionPointSystem.validate()) {
				functionPointSystem.setStakeholderInterests(functionPointSystemReference.getStakeholderInterests());
				//FunctionsPointReader.putStakeholderInterests(functionPointSystem);
			}
			else {
				System.out.println("Erro de validação");
			}
		} catch (ElementException e) {
			e.printStackTrace();
		} catch (CloneException e) {
			e.printStackTrace();
		}		
		return functionPointSystem;
	}
	
	/**
	 * Retorna uma contagem de pontos de função a partir de um BitSet (ex: 010010100111111001)
	 * @param bitset
	 * @param functionPointSystemReference
	 * @return
	 */
	public static FunctionPointSystem getFunctionPointSystemConstructiveWay(Binary binary, FunctionPointSystem functionPointSystemReference, boolean eliminateRetsAndDets) {
		FunctionPointSystem functionPointSystem=null;
		try {
			//functionPointSystem = FunctionsPointReader.clone(functionPointSystemReference);
			XmlFunctionPointElement.clones = new HashMap<Object, Object>();
			//System.out.println(functionPointSystemReference.getTransactionModel().getTransactions().size());
			functionPointSystem = (FunctionPointSystem) functionPointSystemReference.cloneElement();
			int cont = 0;
//			for (int a = 0; a < functionPointSystem.getTransactionModel().getTransactions().size(); a++) {
//				Transaction transaction = functionPointSystem.getTransactionModel().getTransactions().get(a);
//				if(transaction.getReleaseImplementation()==0) {
//					transactionsInArray[cont] = functionPointSystem.getTransactionModel().getTransactions().get(a);
//					cont++;
//				}
//			}

			functionPointSystem.charge();
			Set<Transaction> addedTransactions = new HashSet<Transaction>();
			for (int a = 0; a < functionPointSystem.getTransactionModel().getTransactions().size(); a++) {
				Transaction transaction = functionPointSystem.getTransactionModel().getTransactions().get(a);
				if(binary.bits_.get(a)) {
					addedTransactions.add(transaction);
					addedTransactions.addAll(transaction.getDependendyTransactions(transaction, addedTransactions));
				}
			}
			List<Transaction> transactions = new ArrayList<Transaction>();
			transactions.addAll(addedTransactions);
			functionPointSystem.getTransactionModel().setTransactions(transactions);
			functionPointSystem.clear(eliminateRetsAndDets);
			if(functionPointSystem.validate()) {
				functionPointSystem.setStakeholderInterests(functionPointSystemReference.getStakeholderInterests());
			}
			else {
				System.out.println("Erro de validação");
			}
		} catch (ElementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CloneException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return functionPointSystem;
	}
}