package br.uniriotec.vitor.padilha.dissertacao.algorithm;

import br.uniriotec.vitor.padilha.dissertacao.problem.FunctionsPointProblem;
import jmetal.base.Algorithm;
import jmetal.base.Problem;
import jmetal.base.Solution;
import jmetal.base.SolutionSet;
import jmetal.base.Variable;
import jmetal.base.variable.Binary;
import jmetal.util.JMException;

public class JustOneFlagAlgorithm extends Algorithm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1740958319458180192L;
	private Problem problem_;
	private int maxEvaluations;

	public JustOneFlagAlgorithm(Problem problem, int maxEvaluations)
	{
		this.problem_ = problem;
		this.maxEvaluations = maxEvaluations;
	}

	public SolutionSet execute() throws JMException, ClassNotFoundException
	{
		Solution worst = null;
		int maxEvaluations = problem_.getNumberOfBits();
		
		int evaluations = 0;

		while (evaluations < maxEvaluations)
		{
			Binary binary = new Binary(maxEvaluations);
			for (int i = 0; i < maxEvaluations; i++) {
				if(i==evaluations) {
					binary.bits_.set(i, true);
				}
				else {
					
					binary.bits_.set(i, false);
				}
			}
			Solution current = new Solution(problem_, new Variable[]{binary});
			problem_.evaluate(current);
			evaluations++;
			if(worst==null) {
				worst = current;
			}

			if (current.getObjective(0) > worst.getObjective(0) && current.getObjective(0) != FunctionsPointProblem.INFINITO)
				worst = current;
		}

		SolutionSet result = new SolutionSet(1);
		result.add(worst);
		return result;
	}
}