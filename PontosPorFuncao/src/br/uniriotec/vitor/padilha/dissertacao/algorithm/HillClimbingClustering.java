package br.uniriotec.vitor.padilha.dissertacao.algorithm;

import jmetal.base.Algorithm;
import jmetal.base.Problem;
import jmetal.base.Solution;
import jmetal.base.SolutionSet;
import jmetal.base.visitor.neighborhood.NeighborVisitor;
import jmetal.util.JMException;

public class HillClimbingClustering extends Algorithm
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2599876704582532728L;
	private Problem problem_;
	private NeighborVisitor visitor;
	int evaluations = 0;
	/**
	 * Best solution found by the Hill Climbing search
	 */
	private Solution bestSolution;
	/**
	 * Constructor
	 * 
	 * @param problem Problem to solve
	 */
	public HillClimbingClustering(Problem problem, NeighborVisitor visitor)
	{
		this.problem_ = problem;
		this.visitor = visitor;
	}
	
	/**
	 * Runs the algorithm.
	 */
	public SolutionSet execute() throws JMException, ClassNotFoundException
	{
		this.bestSolution = new Solution(problem_);
		//NonDominatedSolutionList ndl = new NonDominatedSolutionList();

		int maxEvaluations = ((Integer) this.getInputParameter("maxEvaluations")).intValue();
		while (evaluations < maxEvaluations)
		{
			NeighborhoodVisitorStatus neighborhoodVisitorStatus = NeighborhoodVisitorStatus.FOUND_BETTER_NEIGHBOR;
			Solution currentSolution = createNewSolution(problem_);
			problem_.evaluate(currentSolution);
			//System.out.println("Solução Corrente: ("+currentSolution.getDecisionVariables()[0]+"), Valor:"+currentSolution.getObjective(0));
			evaluations++;
			while(neighborhoodVisitorStatus.equals(NeighborhoodVisitorStatus.FOUND_BETTER_NEIGHBOR) && evaluations<maxEvaluations)
			{
				neighborhoodVisitorStatus = localSearch(currentSolution, maxEvaluations);
				//currentSolution = ndl.get(0);
				//System.out.println("Solução Corrente: ("+currentSolution.getDecisionVariables()[0]+"), Valor:"+currentSolution.getObjective(0));
			}
		}

		SolutionSet resultPopulation = new SolutionSet(1);
		resultPopulation.add(bestSolution);

		return resultPopulation;
	}

	private NeighborhoodVisitorStatus localSearch(Solution current, int maxEvaluations) throws ClassNotFoundException, JMException
	{
		int neighborCount = visitor.neighborCount(current);
		NeighborhoodVisitorStatus retorno = NeighborhoodVisitorStatus.NO_BETTER_NEIGHBOR;
		for (int i = 0; i < neighborCount; i++)
		{
			Solution neighbor = visitor.getNeighbor(current, i);
			problem_.evaluate(neighbor);
			//System.out.println("Solução: ("+neighbor.getDecisionVariables()[0]+"), Valor:"+neighbor.getObjective(0));			
			evaluations++;			
			if (current.getObjective(0) > neighbor.getObjective(0))
			{
				current = neighbor;
			    retorno = NeighborhoodVisitorStatus.FOUND_BETTER_NEIGHBOR;
			    if(bestSolution.getObjective(0) > neighbor.getObjective(0)) {
			    	bestSolution = neighbor;
			    }
				//pending.add(neighbor);
			}
			if(evaluations>=maxEvaluations) {
				return NeighborhoodVisitorStatus.SEARCH_EXHAUSTED;
			}
		}
		return retorno;
	}

	protected Solution createNewSolution(Problem problem_) throws ClassNotFoundException, JMException
	{
		return new Solution(problem_);
	}
}

/**
 * Possible results of the local search phase
 */
enum NeighborhoodVisitorStatus
{
	FOUND_BETTER_NEIGHBOR, NO_BETTER_NEIGHBOR, SEARCH_EXHAUSTED
}

/**
 * Class that represents the results of the local search phase
 */
class NeighborhoodVisitorResult
{
	/**
	 * Status in the end of the local search
	 */
	private NeighborhoodVisitorStatus status;
	
	
	
	/**
	 * Initializes a successful local search status
	 * 
	 * @param status Status of the search, quite certainly a successful one
	 * @param fitness Fitness of the best neighbor found
	 */
	public NeighborhoodVisitorResult(NeighborhoodVisitorStatus status, Solution solution)
	{
		this.status = status;
		
	}

	/**
	 * Initializes an unsuccessful local search
	 * 
	 * @param status Status of the search: failure to find a better neighbor or
	 *            exhaustion
	 */
	public NeighborhoodVisitorResult(NeighborhoodVisitorStatus status)
	{
		this.status = status;
	}

	/**
	 * Returns the status of the local search
	 */
	public NeighborhoodVisitorStatus getStatus()
	{
		return status;
	}

}

///**
// * Runs the algorithm.
// */
//public SolutionSet execute() throws JMException, ClassNotFoundException
//{
//	NonDominatedSolutionList ndl = new NonDominatedSolutionList();
//	List<Solution> pending = new ArrayList<Solution>();
//	int pendingCount = 0;
//	int pendingWalker = 0;
//	int evaluations = 0;
//
//	while (evaluations < maxEvaluations)
//	{
//		if (pendingCount <= pendingWalker)
//		{
//			Solution solution = new Solution(problem);
//			problem.evaluate(solution);
//			evaluations++;
//			
//			if (ndl.add(solution))
//			{
//				pending.add(solution);
//				pendingCount++;
//			}
//		}
//		
//		if (pendingCount > pendingWalker)
//		{
//			Solution current = pending.get(pendingWalker);
//			pendingWalker++;
//			
//			int neighborCount = visitor.neighborCount(current);
//
//			for (int i = 0; i < neighborCount; i++)
//			{
//				Solution neighbor = visitor.getNeighbor(current, i);
//				problem.evaluate(neighbor);
//				evaluations++;
//				
//				if (ndl.add(neighbor))
//				{
//					pending.add(neighbor);
//					pendingCount++;
//				}
//			}
//		}
//	}
//
//	return ndl;
//}