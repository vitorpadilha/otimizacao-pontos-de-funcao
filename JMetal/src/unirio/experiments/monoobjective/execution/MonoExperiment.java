package unirio.experiments.monoobjective.execution;

import java.util.Vector;
import jmetal.base.Solution;

public abstract class MonoExperiment<InstanceClass>
{
	protected Vector<MonoExperimentListener> listeners;

	/**
	 * Inicializa um experimento
	 */
	public MonoExperiment()
	{
		this.listeners = new Vector<MonoExperimentListener>();
	}
	
	/**
	 * Adiciona um listener no experimento
	 */
	public void addListerner(MonoExperimentListener listener)
	{
		listeners.add(listener);
	}
	
	/**
	 * Prepara a execu��o do experimento
	 */
	private void prepareExperiment() throws Exception
	{
		for (MonoExperimentListener listener : listeners)
			listener.prepareExperiment();
	}

	/**
	 * Termina a execu��o do experimento
	 */
	private void terminateExperiment() throws Exception
	{
		for (MonoExperimentListener listener : listeners)
			listener.terminateExperiment();
	}

	/**
	 * Prepara a execu��o de uma inst�ncia
	 * 
	 * @param instanceNumber	N�mero da inst�ncia
	 */
	private void prepareInstance(int instanceNumber) throws Exception
	{
		for (MonoExperimentListener listener : listeners)
			listener.prepareInstance(instanceNumber);
	}

	/**
	 * Termina a execu��o de uma inst�ncia
	 * 
	 * @param instanceNumber		N�mero de inst�ncia
	 */
	private void terminateInstance(int instanceNumber) throws Exception
	{
		for (MonoExperimentListener listener : listeners)
			listener.terminateInstance(instanceNumber);
	}

	/**
	 * Retorna os dados de uma determinada solu��o
	 * 
	 * @param solution		Solu��o cujos dados ser�o capturados
	 */
	protected double[] getSolutionData(Solution solution)
	{
		double[] data = new double[1];
		data[0] = solution.getObjective(0);
		return data;
	}
	
	/**
	 * Publica os resultados de um ciclo
	 * 
	 * @param cycleNumber		N�mero do ciclo
	 * @param executionTime		Tempo de execu��o do ciclo
	 * @param cycleSolution		Solu��o encontrada para o ciclo
	 */
	protected void publishCycle(int cycleNumber, int instanceNumber, long executionTime, Solution cycleSolution) throws Exception
	{
		for (MonoExperimentListener listener : listeners)
		{
			double[] data = getSolutionData(cycleSolution);
			listener.publishCycle(cycleNumber, instanceNumber, cycleSolution, executionTime, data);
		}
	}

	/**
	 * Executa um ciclo do algoritmo sobre uma inst�ncia
	 *
	 * @param instance			Inst�ncia que ser� executada
	 * @param instanceNumber	N�mero da inst�ncia que ser� executada
	 */
	protected abstract Solution runCycle(InstanceClass instance, int instanceNumber) throws Exception;
	
	/**
	 * Executa todos os ciclos do experimento para uma inst�ncia
	 * 
	 * @param instance			Inst�ncia que ser� tratada nesta rodada
	 * @param instanceNumber	N�mero da inst�ncia
	 * @param cycles			N�mero de ciclos que ser�o executados
	 */
	private void runCycles(InstanceClass instance, int instanceNumber, int cycles) throws Exception
	{
		prepareInstance(instanceNumber);
		
		for (int i = 0; i < cycles; i++)
		{
			long initTime = System.currentTimeMillis();
			Solution result = runCycle(instance, instanceNumber);
			long executionTime = System.currentTimeMillis() - initTime;
			publishCycle(i, instanceNumber, executionTime, result);
		}
		
		terminateInstance(instanceNumber);
	}

	/**
	 * Roda o experimento para todas as inst�ncias
	 * 
	 * @param instances		Vetor de inst�ncias
	 * @param cycles		N�mero de ciclos que ser�o executados
	 */
	public void run(Vector<InstanceClass> instances, int cycles) throws Exception
	{
		prepareExperiment();

		for (int i = 0; i < instances.size(); i++)
		{
			InstanceClass instance = instances.get(i);
			runCycles(instance, i, cycles);
		}

		terminateExperiment();
	}
}
