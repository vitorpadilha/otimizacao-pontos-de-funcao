package unirio.experiments.monoobjective.analysis.model;

import java.util.Vector;


/**
 * Classe que representa uma inst�ncia do resultado de um experimento
 * 
 * @author Marcio Barros
 */
public class MonoExperimentInstance
{
	private Vector<MonoExperimentCycle> cycles;
	private String name;
	
	/**
	 * Inicializa a inst�ncia
	 */
	public MonoExperimentInstance()
	{
		this.name = "";
		this.cycles = new Vector<MonoExperimentCycle>();
	}

	/**
	 * Retorna o nome da inst�ncia
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * Altera o nome da inst�ncia
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Retorna o n�mero de ciclos calculados para a inst�ncia
	 */
	public int getCycleCount()
	{
		return cycles.size();
	}
	
	/**
	 * Retorna um ciclo calculado para a inst�ncia, dado seu �ndice
	 */
	public MonoExperimentCycle getCycleIndex(int index)
	{
		return cycles.elementAt(index);
	}
	
	/**
	 * Adiciona um ciclo na inst�ncia
	 */
	public void addCycle(MonoExperimentCycle item)
	{
		cycles.add(item);
	}
	
	/**
	 * Remove um ciclo da inst�ncia, dado seu �ndice
	 */
	public void removeCycle(int index)
	{
		cycles.remove(index);
	}
	
	/**
	 * Retorna o tempo de execu��o de cada ciclo da inst�ncia
	 */
	public double[] getExecutionTimes()
	{
		double[] results = new double[cycles.size()];
		
		for (int i = 0; i < cycles.size(); i++)
			results[i] = getCycleIndex(i).getExecutionTime();
		
		return results;
	}
	
	/**
	 * Retorna o objetivo de cada ciclo da inst�ncia
	 */
	public double[] getObjectives()
	{
		double[] results = new double[cycles.size()];
		
		for (int i = 0; i < cycles.size(); i++)
			results[i] = getCycleIndex(i).getObjective();
		
		return results;
	}
	
	/**
	 * Retorna o tempo de identifica��o da melhor solu��o de cada ciclo da inst�ncia
	 */
	public double[] getLocation()
	{
		double[] results = new double[cycles.size()];
		
		for (int i = 0; i < cycles.size(); i++)
			results[i] = getCycleIndex(i).getLocation();
		
		return results;
	}
}