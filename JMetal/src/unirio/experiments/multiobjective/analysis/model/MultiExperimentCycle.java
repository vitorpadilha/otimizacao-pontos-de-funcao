package unirio.experiments.multiobjective.analysis.model;


/**
 * Classe que representa um ciclo de uma inst�ncia em um resultado de experimento
 * 
 * @author Marcio Barros
 */
public class MultiExperimentCycle
{
	private ParetoFront front;
	private double executionTime;
	
	/**
	 * Inicializa o ciclo de inst�ncia, dado o n�mero de objetivos e tamanho da solu��o
	 */
	public MultiExperimentCycle(int objectivesCount, int solutionSize)
	{
		this.front = new ParetoFront(objectivesCount, solutionSize);
		this.executionTime = 0.0;
	}
	
	/**
	 * Retorna a frente de Pareto do ciclo
	 */
	public ParetoFront getFront()
	{
		return front;
	}
	
	/**
	 * Retorna o tempo de execu��o do ciclo
	 */
	public double getExecutionTime()
	{
		return executionTime;
	}

	/**
	 * Altera o tempo de execu��o do ciclo
	 */
	public void setExecutionTime(double executionTime)
	{
		this.executionTime = executionTime;
	}

	/**
	 * Retorna o valor m�nimo de um objetivo para o ciclo
	 * 
	 * @param numeroObjetivo		N�mero do objetivo desejado
	 */
	public double getMinimumObjective(int numeroObjetivo)
	{
		return front.getMinimumObjective(numeroObjetivo);
	}

	/**
	 * Remove um objetivo da frente de Pareto do ciclo, dado seu �ndice
	 */
	public void removeObjective(int index)
	{
		front.removeObjective(index);
	}
}