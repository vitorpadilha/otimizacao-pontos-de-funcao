package jmetal.metaheuristics.randomSearch;

import jmetal.base.Algorithm;
import jmetal.base.Problem;
import jmetal.base.Solution;
import jmetal.base.SolutionSet;
import jmetal.base.visitor.neighborhood.NeighborVisitor;
import jmetal.util.JMException;

public class MonoHillClimbing extends Algorithm
{
	private Problem problem;
	private NeighborVisitor visitor;
	private int maxEvaluations;
	private boolean randomRestart;
	private int restarts;

	/**
	 * Inicializa o hill climbing sem random restart
	 */
	public MonoHillClimbing(Problem problem, NeighborVisitor visitor, int maxEvaluations)
	{
		this.problem = problem;
		this.visitor = visitor;
		this.maxEvaluations = maxEvaluations;
		this.randomRestart = false;
	}
	
	/**
	 *  Inicializa o hill climbing, possivelmente com random restart
	 */
	public MonoHillClimbing(Problem problem, NeighborVisitor visitor, int maxEvaluations, boolean randomRestart)
	{
		this.problem = problem;
		this.visitor = visitor;
		this.maxEvaluations = maxEvaluations;
		this.randomRestart = randomRestart;
	}

	/**
	 * Retorna o número de restarts do processo de cálculo
	 * @return
	 */
	public double getRestartCount()
	{
		return restarts;
	}
	
	/**
	 * Apresenta os dados de uma solução
	 */
	protected void printSolution(Solution solution) throws JMException
	{
		int length = problem.getNumberOfVariables();
		System.out.print("[" + solution.getDecisionVariables()[0].getValue());
		
		for (int i = 1; i < length; i++)
			System.out.print(" " + solution.getDecisionVariables()[i].getValue());
	
		System.out.println("]");
	}
	
	/**
	 * Executa a busca heurística HC
	 */
	public SolutionSet execute() throws JMException, ClassNotFoundException
	{
		Solution bestIndividual = new Solution(problem);
		problem.evaluate(bestIndividual);
		int evaluations = 1;
		
		Solution currentIndividual = bestIndividual;
		boolean restartRequired = false; 
		restarts = 0;

		while (evaluations < maxEvaluations)
		{
			if (restartRequired)
			{
				if (!randomRestart)
					break;
				
				currentIndividual = new Solution(problem);
				problem.evaluate(currentIndividual);
				evaluations++;

				if (currentIndividual.getObjective(0) < bestIndividual.getObjective(0))
					bestIndividual = currentIndividual;
				
				restarts++;
			}

			int neighborCount = visitor.neighborCount(currentIndividual);
			restartRequired = true;

			for (int i = 0; i < neighborCount; i++)
			{
				Solution neighbor = visitor.getNeighbor(currentIndividual, i);
				problem.evaluate(neighbor);
				evaluations++;
				
				if (neighbor.getObjective(0) < bestIndividual.getObjective(0))
				{
					restartRequired = false;
					bestIndividual = neighbor;
				}
			}
		}

		// Return a population with the best individual
		SolutionSet resultPopulation = new SolutionSet(1);
		resultPopulation.add(bestIndividual);
		return resultPopulation;
	}
}