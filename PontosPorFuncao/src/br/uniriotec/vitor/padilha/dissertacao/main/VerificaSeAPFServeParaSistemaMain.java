package br.uniriotec.vitor.padilha.dissertacao.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import br.uniriotec.vitor.padilha.dissertacao.calculator.Complexity;
import br.uniriotec.vitor.padilha.dissertacao.calculator.FunctionPointCalculator;
import br.uniriotec.vitor.padilha.dissertacao.listener.mono.FunctionsPointDetailsListener;
import br.uniriotec.vitor.padilha.dissertacao.model.FunctionPointSystem;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.DataModelElement;
import br.uniriotec.vitor.padilha.dissertacao.model.transactionModel.FTR;
import br.uniriotec.vitor.padilha.dissertacao.model.transactionModel.Transaction;
import br.uniriotec.vitor.padilha.dissertacao.utils.FunctionsPointReader;

public class VerificaSeAPFServeParaSistemaMain {
//	public static List<String> getDependencies() {
//		
//	}
	public static void main(String[] args) throws Exception
	{
		int numberOfPartitions = Integer.valueOf(FunctionsPointDetailsListener.getProp().getProperty("numberOfPartitions"));
		String configurations = FunctionsPointDetailsListener.getProp().getProperty("configurations");
		JSONTokener jsonTokener = new JSONTokener(configurations);
		JSONObject configs = new JSONObject( jsonTokener );
		JSONArray arrayConfigs = configs.getJSONArray("configs");
		FunctionPointCalculator functionPointCalculator = new FunctionPointCalculator();
		
		for (String instance : FunctionsPointDetailsListener.getProp().getProperty("instancias").split(",")) {		
			System.out.println("Analisando Instância:"+instance);
			FunctionsPointReader reader = new FunctionsPointReader(FunctionsPointDetailsListener.getProp().getProperty("diretorio.instancia")+instance+"/functions-point.xml",FunctionsPointDetailsListener.getProp().getProperty("diretorio.instancia")+instance+"/stakeholders-interest.xml");
			FunctionPointSystem functionPointSystemReference = reader.read();
			functionPointSystemReference.charge();
			functionPointCalculator.calculate(functionPointSystemReference, 0, null);
			Map<String,Complexity> dataModelElements = new HashMap<String, Complexity>();
			List<Transaction> transactions = new ArrayList<Transaction>();
			List<Transaction> allTransactions = new ArrayList<Transaction>();
			Map<String,List<Transaction>> transactionsIteretad = new HashMap<String, List<Transaction>>();
			for (DataModelElement dataModelElement : functionPointSystemReference.getDataModel().getDataModelElements()) {
				Complexity complexity = functionPointCalculator.getComplexity(dataModelElement);
				if(!complexity.equals(Complexity.LOW))
					dataModelElements.put(dataModelElement.getName(),complexity);
			}
			for (Transaction transaction : functionPointSystemReference.getTransactionModel().getTransactions()) {
				boolean containsMediumOrHighDataModelElement = false;
				if(transaction.getDependencies() == null || transaction.getDependencies().isEmpty()) {
					for (FTR ftr : transaction.getFtrList()) {
						Complexity complexity = functionPointCalculator.getComplexity(ftr.getRetRef().getParent());
						if(!complexity.equals(Complexity.LOW))
							containsMediumOrHighDataModelElement = true;
					}
					if(containsMediumOrHighDataModelElement)
						transactions.add(transaction);
					
				}
				allTransactions.add(transaction);
			}
			for (Transaction transactionReference : transactions) {
				FunctionPointSystem functionPointSystem2 = (FunctionPointSystem) functionPointSystemReference.cloneElement();
				functionPointSystem2.charge();
				List<Transaction> transactions2 = new ArrayList<Transaction>();
				for (Transaction transaction : functionPointSystem2.getTransactionModel().getTransactions()) {
					if(transaction.getName().equals(transactionReference.getName()))
						transactions2.add(transaction);
				}
				functionPointSystem2.getTransactionModel().setTransactions(transactions2);
				functionPointSystem2.clear(true);
				FunctionPointCalculator functionPointCalculator2 = new FunctionPointCalculator();
				functionPointCalculator.calculate(functionPointSystem2, 0, null);
				for (DataModelElement dataModelElement : functionPointSystem2.getDataModel().getDataModelElements()) {
					Complexity complexity = functionPointCalculator2.getComplexity(dataModelElement);
					if(dataModelElements.containsKey(dataModelElement.getName()) && !complexity.equals(dataModelElements.get(dataModelElement.getName())))
						System.out.println("1 - Transação: "+transactionReference.getName()+ " FD: "+dataModelElement.getName()+" Complexidade Inicial:"+dataModelElements.get(dataModelElement.getName())+ " Complexidade Final: "+complexity);
				}
			}
			
			for (Transaction transactionReference : allTransactions) {
				FunctionPointSystem functionPointSystem3 = reader.read();
				//System.out.println(functionPointSystemReference.getTransactionModel().getTransactions().size());
				functionPointSystem3.charge();
				functionPointSystem3.validate();
				Set<Transaction> transactions2 = new HashSet<Transaction>();
				for (Transaction transaction : functionPointSystem3.getTransactionModel().getTransactions()) {
					if(transaction.getName().equals(transactionReference.getName())) {
						transactions2.add(transaction);
						transactions2.addAll(transaction.getDependendyTransactions(transaction, transactions2));
					}
				}
				List<Transaction> transactionsList = new ArrayList<Transaction>();
				transactionsList.addAll(transactions2);
				functionPointSystem3.getTransactionModel().setTransactions(transactionsList);
				functionPointSystem3.clear(true);
				FunctionPointCalculator functionPointCalculator2 = new FunctionPointCalculator();
				int totalPF = functionPointCalculator.calculate(functionPointSystem3, 0, null);
				for (DataModelElement dataModelElement : functionPointSystem3.getDataModel().getDataModelElements()) {
					Complexity complexity = functionPointCalculator2.getComplexity(dataModelElement);
					if(dataModelElements.containsKey(dataModelElement.getName()) && !complexity.equals(dataModelElements.get(dataModelElement.getName()))) {
						String transationsNames = "";
						int i = 0;
						for (Transaction transaction : transactionsList) {
							if(i!=0) transationsNames += ", ";
								transationsNames += transaction.getName();
							i++;
						}
						System.out.println("2 - Transação: "+transationsNames+ " FD: "+dataModelElement.getName()+" Complexidade Inicial:"+dataModelElements.get(dataModelElement.getName())+ " Complexidade Final: "+complexity+" Total em PF: "+totalPF);
					}
				}
			}
			
		}	
	}
}
