package unirio.experiments.monoobjective.analysis.model;

import java.util.Vector;


/**
 * Classe que representa o resultado de um experimento mono-objetivo
 * 
 * @author Marcio Barros
 */
public class MonoExperimentResult
{
	private Vector<MonoExperimentInstance> instances;
	private String name;
	
	/**
	 * Inicializa o resultado do experimento, indicando seu nome
	 */
	public MonoExperimentResult(String name)
	{
		this.name = name;
		this.instances = new Vector<MonoExperimentInstance>();
	}

	/**
	 * Retorna o nome do experimento
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Altera o nome do experimento
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Retorna o n�mero de inst�ncias do resultado do experimento
	 */
	public int getInstanceCount()
	{
		return instances.size();
	}
	
	/**
	 * Retorna uma inst�ncia do resultado do experimento, dado seu �ndice
	 */
	public MonoExperimentInstance getInstanceIndex(int index)
	{
		return instances.elementAt(index);
	}
	
	/**
	 * Adiciona uma inst�ncia no resultado do experimento
	 */
	public void addInstance(MonoExperimentInstance item)
	{
		instances.add(item);
	}
	
	/**
	 * Remove uma inst�ncia do resultado do experimento, dado seu �ndice
	 */
	public void removeInstance(int index)
	{
		instances.remove(index);
	}
}