package br.uniriotec.vitor.padilha.dissertacao.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import jmetal.base.Solution;
import jmetal.base.variable.Binary;
import unirio.experiments.monoobjective.execution.StreamMonoExperimentListener;
import unirio.experiments.multiobjective.execution.FileMultiExperimentListener;
import br.uniriotec.vitor.padilha.dissertacao.algorithm.Algorithms;
import br.uniriotec.vitor.padilha.dissertacao.calculator.FunctionPointCalculator;
import br.uniriotec.vitor.padilha.dissertacao.experiment.IFunctionsPointExperiment;
import br.uniriotec.vitor.padilha.dissertacao.experiment.monoobjective.FunctionsPointMonoObjectiveExperiment;
import br.uniriotec.vitor.padilha.dissertacao.experiment.multiobjective.FunctionsPointMultiObjectiveExperiment;
import br.uniriotec.vitor.padilha.dissertacao.listener.mono.FunctionsPointDetailsListener;
import br.uniriotec.vitor.padilha.dissertacao.model.FunctionPointSystem;
import br.uniriotec.vitor.padilha.dissertacao.model.ParametersAlgorithm;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.DET;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.DataModelElement;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.RET;
import br.uniriotec.vitor.padilha.dissertacao.model.transactionModel.Transaction;

public class GenerateReleasesInstancesForExperiment {

	
	private boolean monoObjective;
	
	private FunctionPointSystem functionPointSystemReference;
	
	private FunctionPointCalculator functionPointCalculator;
	
	private Map<String,Transaction> transactionsNamesOfReference;
	
	private Map<String,DET> detsNamesOfReference;
	
	protected FunctionsPointDetailsListener listerner;

	public boolean isMonoObjective() {
		return monoObjective;
	}

	public void setMonoObjective(boolean monoObjective) {
		this.monoObjective = monoObjective;
	}

	@SuppressWarnings("unchecked")
	public void run(Algorithms algorithmNumber, Vector<FunctionPointSystem> instance2, FunctionPointCalculator functionPointCalculator, Integer maxFunctionsPointValue, int releases, int ciclos, boolean novaExecucao, boolean eliminateRetsAndDets, ParametersAlgorithm parametersAlgorithm) throws Exception {
		System.out.println("Parametros do Algoritmo = ("+parametersAlgorithm.toString()+")");
		System.out.println("Limite = ("+maxFunctionsPointValue+")");
		FunctionPointSystem instance = instance2.firstElement();
		this.functionPointCalculator = functionPointCalculator;
		this.functionPointSystemReference = instance;
		configureElementsNames();
		int numeroTransacoes = instance.getTransactionModel().getTransactions().size();
		int numeroTransacoesFaltantes = numeroTransacoes;
		Long baseSatisfaction = functionPointCalculator.calculateUserSatisfaction(instance);
		int releaseNumber = 1;
		Binary conjuntoTestesN = new Binary(numeroTransacoes);		
		for (int i = 0; i < numeroTransacoes; i++) {
			conjuntoTestesN.bits_.set(i, true);
		}
//		publishSolution(0,0, 0, 0, this.functionPointSystemReference, this.functionPointSystemReference, 0, conjuntoTestesN);
		int functionsPointValue = 0;
		this.monoObjective = algorithmNumber.isMonoobjective();
		FunctionsPointDetailsListener.criarDiretorio(functionPointSystemReference.getName());
		FunctionsPointDetailsListener.criarDiretorio(functionPointSystemReference.getName()+"\\"+algorithmNumber.name());
		int executionNumber = FunctionsPointDetailsListener.getLastExecutionNumber(functionPointSystemReference.getName()+"\\"+algorithmNumber.name());
		if(novaExecucao) {
			executionNumber++;
			FunctionsPointDetailsListener.criarDiretorio(functionPointSystemReference.getName()+"\\"+algorithmNumber.name()+"\\"+(executionNumber));
		}
		if (numeroTransacoesFaltantes>0) {		
			IFunctionsPointExperiment<FunctionPointSystem> experiment = null;
			if(monoObjective && numeroTransacoesFaltantes>1)	{
				experiment = new FunctionsPointMonoObjectiveExperiment();
				((FunctionsPointMonoObjectiveExperiment)experiment).setAlgoritmo(algorithmNumber);
				((FunctionsPointMonoObjectiveExperiment)experiment).setFunctionPointCalculator(functionPointCalculator);
				((FunctionsPointMonoObjectiveExperiment)experiment).setNumeroMaximoDePontosDeFuncao(maxFunctionsPointValue);
				((FunctionsPointMonoObjectiveExperiment)experiment).setNumeroTransacoesFaltantes(numeroTransacoesFaltantes);	
				((FunctionsPointMonoObjectiveExperiment)experiment).setBaseSatisfaction(baseSatisfaction);
				((FunctionsPointMonoObjectiveExperiment)experiment).setEliminateRetsAndDets(eliminateRetsAndDets);
				((FunctionsPointMonoObjectiveExperiment)experiment).setParametersAlgorithm(parametersAlgorithm);
				((FunctionsPointMonoObjectiveExperiment)experiment).addListerner(
						new StreamMonoExperimentListener(
								new OutputStreamWriter(
										new FileOutputStream(
												new File(FunctionsPointDetailsListener.getResultDir()+functionPointSystemReference.getName()+"\\"+algorithmNumber.name()+"\\"+(executionNumber)+"\\output_"+algorithmNumber.name()+"-"+eliminateRetsAndDets+"-"+maxFunctionsPointValue+"-"+parametersAlgorithm.getCrossoverProbability()+"-"+parametersAlgorithm.getMutationProbability()+"-"+parametersAlgorithm.getMultiplicadorPopulacao()+".txt")
												)
										), true));
			}
			else if (numeroTransacoesFaltantes>1){
				
				//FunctionsPointDetailsListener.deletar(algorithmNumber.name()+"\\output.txt");
				//FunctionsPointDetailsListener.criarArquivo(algorithmNumber.name()+"\\output.txt");
				experiment = new FunctionsPointMultiObjectiveExperiment(instance, 
						numeroTransacoesFaltantes, maxFunctionsPointValue,  baseSatisfaction, functionPointCalculator
						,algorithmNumber, parametersAlgorithm, 
						FunctionsPointDetailsListener.getResultDir()+functionPointSystemReference.getName()+"\\"+algorithmNumber.name()+"\\"+(executionNumber)+"\\output_"+algorithmNumber.name(), 
						eliminateRetsAndDets);
				((FunctionsPointMultiObjectiveExperiment)experiment).addListerner(new FileMultiExperimentListener(FunctionsPointDetailsListener.getResultDir()+functionPointSystemReference.getName()+"\\"+algorithmNumber.name()+"\\"+(executionNumber)+"\\output_"+algorithmNumber.name()+".txt", true));
				//((FunctionsPointMultiObjectiveExperiment)experiment).addListerner(new FileMultiExperimentListener(new OutputStreamWriter(System.out), true));
			}
			experiment.run2(instance2, ciclos);
			Map<Integer, Map<Integer, Vector<Solution>>> solutionSet = experiment.getSolutions();
			
			for (Integer instanceNumberSolution : solutionSet.keySet()) {
				Map<Integer, Vector<Solution>> solutionsInstance = solutionSet.get(instanceNumberSolution);
				for (Integer cycleNumber : solutionsInstance.keySet()) {
					Vector<Solution> solutions = solutionsInstance.get(cycleNumber);
					int i = 1;
					for (Solution solution : solutions) {

						Binary conjuntoTestes = (Binary) solution.getDecisionVariables()[0];
						FunctionPointSystem pontosPorFuncao = FunctionPointFactory.getFunctionPointSystemConstructiveWay(conjuntoTestes, this.functionPointSystemReference, eliminateRetsAndDets);	
						functionsPointValue = functionPointCalculator.calculate(pontosPorFuncao, releaseNumber,null);
//						setImplementedInElementsOfRelease(pontosPorFuncao, releaseNumber);
//						publishSolution(instanceNumberSolution, cycleNumber+1, releaseNumber, i, pontosPorFuncao, this.functionPointSystemReference, functionsPointValue, conjuntoTestes);
						i++;
					}
				}
			}
			
			releaseNumber++;
		}
		initializeReference();
//		listerner.terminateExperiment();
	}
	
	
	
	
	/**
	 * Configura o nomes das Transações e DET's para posteriormente verificar se já foi implementada. 
	 */
	private void configureElementsNames() {
		transactionsNamesOfReference = new HashMap<String, Transaction>();
		for (Transaction transaction : functionPointSystemReference.getTransactionModel().getTransactions()) {
			transactionsNamesOfReference.put(transaction.getName(), transaction);
		}
		detsNamesOfReference = new HashMap<String, DET>();
		for (DataModelElement dataModelElement : functionPointSystemReference.getDataModel().getDataModelElements()) {
			for(RET ret:dataModelElement.getRets()) {
				for(DET det:ret.getDets()) {
					detsNamesOfReference.put(dataModelElement.getName()+"."+ret.getName()+"."+det.getName(), det);
				}
			}
		}
	}
	private void setImplementedInElementsOfRelease(
			FunctionPointSystem pontosPorFuncao, int releaseNumber) {
		for(Transaction selectedTransaction:pontosPorFuncao.getTransactionModel().getTransactions()){
			if(selectedTransaction.getReleaseImplementation()==0)
				transactionsNamesOfReference.get(selectedTransaction.getName()).setReleaseImplementation(releaseNumber);
		}
		for (DataModelElement dataModelElement : pontosPorFuncao.getDataModel().getDataModelElements()) {
			if(dataModelElement.getFunctionsPointValue()==null || dataModelElement.getFunctionsPointValue()==0) {
				dataModelElement.setFunctionsPointValue(functionPointCalculator.calculateDataModelElementValue(dataModelElement, null, releaseNumber));
			}
			for(RET ret:dataModelElement.getRets()) {
				for(DET det:ret.getDets()) {
					
					
					detsNamesOfReference.get(dataModelElement.getName()+"."+ret.getName()+"."+det.getName()).setImplementada(true);
				}
			}
		}
	}
	
	private void initializeReference() {
		for(Transaction selectedTransaction:this.functionPointSystemReference.getTransactionModel().getTransactions()){
			selectedTransaction.setReleaseImplementation(0);
		}
		for (DataModelElement dataModelElement : this.functionPointSystemReference.getDataModel().getDataModelElements()) {
			dataModelElement.setFunctionsPointValue(0);
			for(RET ret:dataModelElement.getRets()) {
				for(DET det:ret.getDets()) {
					det.setImplementada(false);
				}
			}
		}
	}


	
	
	
	private void publishSolution(int instanceNumber, int cycleNumber, int releaseNumber, int solutionNumber,
			FunctionPointSystem pontosPorFuncao, FunctionPointSystem pontosPorFuncaoReferencia, int functionsPointValue, Binary binary)
			throws IOException, FileNotFoundException {
		
				listerner.publishSolution(instanceNumber, cycleNumber, releaseNumber, solutionNumber, pontosPorFuncao, pontosPorFuncaoReferencia, functionsPointValue, binary);
			
	}

	public FunctionsPointDetailsListener getListerner() {
		return listerner;
	}

	public void setListerner(FunctionsPointDetailsListener listerner) {
		this.listerner = listerner;
	}


}
