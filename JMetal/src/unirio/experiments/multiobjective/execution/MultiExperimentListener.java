package unirio.experiments.multiobjective.execution;

import java.util.Vector;

import jmetal.base.Solution;

/**
 * Interface que representa os eventos do ciclo de vida de um experimento
 * 
 * @author Marcio Barros
 */
public interface MultiExperimentListener
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
	 * @param instanceFrontier	Fronteira eficiente da inst�ncia (final)
	 */
	public void terminateInstance(int instanceNumber, Vector<Solution> instanceFrontier) throws Exception;

	/**
	 * Publica os dados de um ciclo de uma inst�ncia
	 * 
	 * @param cycleNumber		N�mero do ciclo
	 * @param executionTime		Tempo de execu��o
	 * @param cycleFrontier		Fronteira eficiente do ciclo
	 * @param instanceFrontier	Fronteira eficiente da inst�ncia (parcial)
	 */
	public void publishCycle(int cycleNumber, int instanceNumber, long executionTime, Vector<Solution> cycleFrontier, Vector<Solution> instanceFrontier) throws Exception;
}