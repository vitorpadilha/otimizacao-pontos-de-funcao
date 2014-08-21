package unirio.experiments.multiobjective.analysis.model;

import java.util.Vector;

import jmetal.qualityIndicator.GenerationalDistance;
import jmetal.qualityIndicator.Hypervolume;
import jmetal.qualityIndicator.Spread;

/**
 * Classe que representa uma inst�ncia do resultado de um experimento
 * 
 * @author Marcio Barros
 */
public class MultiExperimentInstance
{
	private Vector<MultiExperimentCycle> cycles;
	private ParetoFront bestFront;
	private int objectiveCount;
	private int solutionSize;
	private String name;
	
	/**
	 * Inicializa a inst�ncia, indicando o n�mero de objetivos e o tamanho da solu��o
	 */
	public MultiExperimentInstance(int objectivesCount, int solutionSize)
	{
		this.name = "";
		this.objectiveCount = objectivesCount;
		this.solutionSize = solutionSize;
		this.cycles = new Vector<MultiExperimentCycle>();
		this.bestFront = new ParetoFront(objectivesCount, solutionSize);
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
	 * Retorna a melhor frente de Pareto da inst�ncia
	 */
	public ParetoFront getBestFront()
	{
		return bestFront;
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
	public MultiExperimentCycle getCycleIndex(int index)
	{
		return cycles.elementAt(index);
	}
	
	/**
	 * Adiciona um ciclo na inst�ncia
	 */
	public void addCycle(MultiExperimentCycle item)
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
	 * Calcula a melhor frente de Pareto da inst�ncia a partir dos seus ciclos
	 */
	public ParetoFront calculateBestFront()
	{
		ParetoFront front = new ParetoFront(objectiveCount, solutionSize);
		
		for (int i = 0; i < cycles.size(); i++)
			front.merge(getCycleIndex(i).getFront());
		
		return front;
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
	 * Retorna o n�mero de solu��es de cada ciclo da inst�ncia
	 */
	public double[] getSolutionCount()
	{
		double[] results = new double[cycles.size()];
		
		for (int i = 0; i < cycles.size(); i++)
			results[i] = getCycleIndex(i).getFront().getVertexCount();
		
		return results;
	}
	
	/**
	 * Retorna o n�mero de solu��es �timas de cada ciclo da inst�ncia, dada a melhor frente de Pareto
	 */
	public double[] getBestSolutions(ParetoFront bestFront)
	{
		double[] results = new double[cycles.size()];
		
		for (int i = 0; i < cycles.size(); i++)
			results[i] = bestFront.countCommonVertices(getCycleIndex(i).getFront());
		
		return results;
	}
	
	/**
	 * Retorna o spread de cada ciclo da inst�ncia, dada a melhor frente de Pareto
	 */
	public double[] getSpreads(ParetoFront bestFront)
	{
		double[] results = new double[cycles.size()];
		double[][] bestFrontValues = bestFront.getValues();
		Spread spread = new Spread();
		
		for (int i = 0; i < cycles.size(); i++)
		{
			MultiExperimentCycle cycle = getCycleIndex(i);
			double[][] currentFrontValues = cycle.getFront().getValues();
			results[i] = spread.spread(currentFrontValues, bestFrontValues, bestFront.getObjectiveCount());
		}
		
		return results;
	}
	
	/**
	 * Retorna o hipervolume de cada ciclo da inst�ncia, dada a melhor frente de Pareto
	 */
	public double[] getHypervolumes(ParetoFront bestFront)
	{
		double[] results = new double[cycles.size()];
		double[][] bestFrontValues = bestFront.getValues();
		Hypervolume hv = new Hypervolume();
		
		for (int i = 0; i < cycles.size(); i++)
		{
			MultiExperimentCycle cycle = getCycleIndex(i);
			double[][] currentFrontValues = cycle.getFront().getValues();
			results[i] = hv.hypervolume(currentFrontValues, bestFrontValues, bestFront.getObjectiveCount());
		}
		
		return results;
	}
	
	/**
	 * Retorna a dist�ncia geracional de cada ciclo da inst�ncia, dada a melhor frente de Pareto
	 */
	public double[] getGenerationalDistances(ParetoFront bestFront)
	{
		double[] results = new double[cycles.size()];
		double[][] bestFrontValues = bestFront.getValues();
		GenerationalDistance gd = new GenerationalDistance();
		
		for (int i = 0; i < cycles.size(); i++)
		{
			MultiExperimentCycle cycle = getCycleIndex(i);
			double[][] currentFrontValues = cycle.getFront().getValues();
			results[i] = gd.generationalDistance(currentFrontValues, bestFrontValues, bestFront.getObjectiveCount());
		}
		
		return results;
	}
	
	/**
	 * Retorna o error ratio de cada ciclo da inst�ncia, dada a melhor frente de Pareto
	 */
	public double[] getErrorRatios(ParetoFront bestFront)
	{
		double[] results = getBestSolutions(bestFront);
		
		for (int i = 0; i < cycles.size(); i++)
		{
			MultiExperimentCycle cycle = getCycleIndex(i);
			results[i] = 1.0 - results[i] / cycle.getFront().getVertexCount();
		}
		
		return results;
	}

	/**
	 * Retorna o valor m�nimo de um objetivo para cada ciclo da inst�ncia
	 * 
	 * @param numeroObjetivo		N�mero do objetivo desejado
	 * @return
	 */
	public double[] getMinimumObjective(int numeroObjetivo)
	{
		double[] results = new double[cycles.size()];
		
		for (int i = 0; i < cycles.size(); i++)
		{
			MultiExperimentCycle cycle = getCycleIndex(i);
			results[i] = cycle.getMinimumObjective(numeroObjetivo);
		}
		
		return results;
	}

	/**
	 * Remove um objetivo das frentes de Pareto da inst�ncia
	 */
	public void removeObjective(int index)
	{
		for (MultiExperimentCycle cycle : cycles)
			cycle.removeObjective(index);
				
		bestFront.removeObjective(index);
	}
}