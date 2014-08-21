package unirio.experiments.monoobjective.execution;

import jmetal.base.Solution;

/**
 * Interface que representa os eventos do ciclo de vida de um experimento
 * 
 * @author Marcio Barros
 */
public interface MonoExperimentListener
{
	/**
	 * Prepara a execu��o do experimento
	 */
	public void prepareExperiment() throws Exception;

	/**
	 * Encerra a execu��o do experimento
	 */
	public void terminateExperiment() throws Exception;
	
	/**
	 * Prepara a execu��o de uma inst�ncia
	 * 
	 * @param instanceNumber	N�mero da inst�ncia
	 */
	public void prepareInstance(int instanceNumber) throws Exception;

	/**
	 * Encerra a execu��o de uma inst�ncia
	 * 
	 * @param instanceNumber	N�mero da inst�ncia
	 */
	public void terminateInstance(int instanceNumber) throws Exception;

	/**
	 * Publica os dados de um ciclo de uma inst�ncia
	 * 
	 * @param cycleNumber		N�mero do ciclo
	 * @param instanceNumber	N�mero da inst�ncia
	 * @param Solution			Solu��o encontrada no ciclo
	 * @param executionTime		Tempo de execu��o
	 * @param data				Dados relevantes sobre a solu��o
	 */
	public void publishCycle(int cycleNumber, int instanceNumber, Solution solution, long executionTime, double[] data) throws Exception;
}