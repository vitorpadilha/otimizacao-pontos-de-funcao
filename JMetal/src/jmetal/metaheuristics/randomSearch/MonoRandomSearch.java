package jmetal.metaheuristics.randomSearch;

import jmetal.base.Algorithm;
import jmetal.base.Problem;
import jmetal.base.Solution;
import jmetal.base.SolutionSet;
import jmetal.util.JMException;

public class MonoRandomSearch extends Algorithm
{
	private Problem problem_;
	private int maxEvaluations;

	public MonoRandomSearch(Problem problem, int maxEvaluations)
	{
		this.problem_ = problem;
		this.maxEvaluations = maxEvaluations;
	}

	public SolutionSet execute() throws JMException, ClassNotFoundException
	{
		Solution best = new Solution(problem_);
		problem_.evaluate(best);
		int evaluations = 1;

		while (evaluations < maxEvaluations)
		{
			Solution current = new Solution(problem_);
			problem_.evaluate(current);
			evaluations++;

			if (current.getObjective(0) < best.getObjective(0))
				best = current;
			
//			if(runTimeListener!=null) {
//				runTimeListener.listen(evaluations, solutionSet);
//			}
		}

		SolutionSet result = new SolutionSet(1);
		result.add(best);
		return result;
	}
}