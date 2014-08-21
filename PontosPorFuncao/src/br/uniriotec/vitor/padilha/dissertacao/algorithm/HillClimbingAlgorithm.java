package br.uniriotec.vitor.padilha.dissertacao.algorithm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import unirio.experiments.monoobjective.execution.MonoExperimentListener;

import br.uniriotec.vitor.padilha.dissertacao.listener.mono.FunctionsPointDetailsListener;
import br.uniriotec.vitor.padilha.dissertacao.model.FunctionPointSystem;

public class HillClimbingAlgorithm extends GenericSearchAlgorithm{
	private int NUMERO_REINICIALIZACOES = 30;

	@Override
	public void execute() throws FileNotFoundException, IOException {
		long setSize = getSetSize();
		int numeroTransacoesFaltantes = functionPointSystemReference.getTransactionModel().getTransactions().size();
		int numeroTransacoes = functionPointSystemReference.getTransactionModel().getTransactions().size();
		
		int contadorReinicializacao = 0;
		int releaseNumber = 1;
		while (numeroTransacoesFaltantes>0) {
			while(contadorReinicializacao<NUMERO_REINICIALIZACOES) {
				long currentValue = Math.round((Math.random() * (setSize-1)));
				Boolean[] currentSetRepresentation = getSetRepresentation(currentValue);
				FunctionPointSystem currentFunctionPointSystemLocal= getFunctionPointSystem(currentSetRepresentation); 
				Long currentSatisfactionLocal = getSatisfacaoPatrocinadores(currentFunctionPointSystemLocal);
				while(getFunctionPointValue(currentFunctionPointSystemLocal,0)>getPontosDeFuncaoPorRelease()){
					currentValue = Math.round((Math.random() * (setSize-1)));
					currentSetRepresentation = getSetRepresentation(currentValue);
					currentFunctionPointSystemLocal = getFunctionPointSystem(currentSetRepresentation); 
					currentSatisfactionLocal = getSatisfacaoPatrocinadores(currentFunctionPointSystemLocal);
				}
				
				boolean findNext = true;
				Set<FunctionPointSystem> neighbors = getNeighbors(currentSetRepresentation);
				while(findNext) {
					findNext = false;
					neighbors = getNeighbors(getSetRepresentation(currentFunctionPointSystemLocal));
					for(FunctionPointSystem functionPointSystem:neighbors){
						Long satisfacaoAux = getSatisfacaoPatrocinadores(functionPointSystem);
						if( satisfacaoAux > currentSatisfactionLocal && getFunctionPointValue(functionPointSystem,0)<=getPontosDeFuncaoPorRelease()) {
							currentFunctionPointSystemLocal = functionPointSystem;
							currentSatisfactionLocal = satisfacaoAux;
							findNext = true;
						}
					}
				}
				if(this.currentSatisfaction==null || this.currentSatisfaction>currentSatisfactionLocal){
					this.currentSatisfaction = currentSatisfactionLocal;
					this.currentFunctionPointSystem = currentFunctionPointSystemLocal;
				}
				contadorReinicializacao++;
			}
			numeroTransacoesFaltantes = numeroTransacoes - this.currentFunctionPointSystem.getTransactionModel().getTransactions().size();
			publishRelease(releaseNumber, this.currentFunctionPointSystem, getFunctionPointValue(this.currentFunctionPointSystem,0));
			releaseNumber++;
		}
	}
	
	private void publishRelease(int releaseNumber,
			FunctionPointSystem pontosPorFuncao, int functionsPointValue)
			throws IOException, FileNotFoundException {
		for (MonoExperimentListener listener : this.listeners) {
			if(listener.getClass().equals(FunctionsPointDetailsListener.class)) {
				FunctionsPointDetailsListener view = (FunctionsPointDetailsListener) listener;
				//view.publishRelease(releaseNumber,pontosPorFuncao,functionsPointValue);
			}
		}
	}
	/**
	 * Retorna os vizinhos de um conjunto de casos de teste atuais
	 * @param currentSetRepresatation
	 * @return
	 */
	protected Set<FunctionPointSystem> getNeighbors(Boolean[] currentSetRepresatation) {
		Set<FunctionPointSystem> auxReturn = new HashSet<FunctionPointSystem>();
		for(int a = 0; a<currentSetRepresatation.length;a++) {
			Boolean[] setRepresatation = currentSetRepresatation.clone();
			setRepresatation[a] = !setRepresatation[a];
			auxReturn.add(getFunctionPointSystem(setRepresatation));
		}
		return auxReturn;
	}
}
