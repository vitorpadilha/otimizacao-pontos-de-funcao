package br.uniriotec.vitor.padilha.dissertacao.view;

import java.util.List;
import java.util.Map;

import br.uniriotec.vitor.padilha.dissertacao.calculator.Complexity;
import br.uniriotec.vitor.padilha.dissertacao.model.FunctionPointSystem;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.DET;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.DataModel;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.DataModelElement;
import br.uniriotec.vitor.padilha.dissertacao.model.transactionModel.Transaction;
import br.uniriotec.vitor.padilha.dissertacao.model.transactionModel.TransactionModel;

public interface IFunctionPointView {

	public void renderTransactionModelValue(TransactionModel transactionModel, int totalTransations, int totalFunctionsPoint);
	
	public void addTransactionValue(Transaction transaction, String[] ftrs, String[] dets, Complexity complexity, int totalFunctionsPoint, int releaseNumber);
	
	public void renderDataModelValue(DataModel dataModel, int totalDataModelElement, int totalFunctionsPoint);
	
	public void addDataModelElementValue(DataModelElement dataModelElement, List<String[]> rets, String[] dets, Complexity complexity,int totalFunctionsPoint, Map<Integer,Integer> adjustmentsFactors);

	public void renderNoUsedField(DET field);
	
	public void render();
	
	public void addSatisfactionPercentForFunctionPoint(FunctionPointSystem functionPointSystem, Double percent);
	public void addSatisfactionForFunctionPoint(FunctionPointSystem functionPointSystem, long percent);
}
