package jmetal.base.visitor.neighborhood;

import jmetal.base.Problem;
import jmetal.base.Solution;
import jmetal.base.solutionType.IntSolutionType;
import jmetal.base.variable.Int;
import jmetal.util.Configuration;
import jmetal.util.JMException;

public class DeepIntegerNeighborVisitor implements NeighborVisitor
{
	private Problem problem;
	private int maxValue;
	
	public DeepIntegerNeighborVisitor(Problem problem, int maxValue) throws JMException
	{
		if (problem.getSolutionType().getClass() != IntSolutionType.class)
		{
			Configuration.logger_.severe("NeighborVisitor.execute: problems should have a integer variables");
			throw new JMException("NeighborVisitor.execute: problems should have a integer variables");
		}
		
		this.problem = problem;
		this.maxValue = maxValue;
	}
	
	protected void printSolution(Solution solution) throws JMException
	{
		int length = problem.getNumberOfVariables();
		System.out.print("[" + solution.getDecisionVariables()[0].getValue());
		
		for (int i = 1; i < length; i++)
			System.out.print(" " + solution.getDecisionVariables()[i].getValue());
	
		System.out.println("]");
	}
	
	public int neighborCount(Solution solution)
	{
		int length = problem.getNumberOfVariables();
		return length * maxValue; 
	}
	
	public Solution getNeighbor(Solution solution, int index) throws ClassNotFoundException, JMException
	{
		int variable = index / maxValue;
		int offset = index - variable * maxValue;
		int length = problem.getNumberOfVariables();
		
		int originalValue = (int) solution.getDecisionVariables()[variable].getValue();
		
		for (int i = 0; i <= maxValue; i++)
		{
			if (originalValue != i)
			{
				if (offset == 0)
				{
					Solution neighbor = Solution.getNewSolution(problem);
					Int neighborVariable = (Int) neighbor.getDecisionVariables()[variable];
	
					for (int j = 0; j < length; j++)
						neighbor.getDecisionVariables()[j].setValue(solution.getDecisionVariables()[j].getValue());
					
					neighborVariable.setValue(i);
					return neighbor;
				}
				
				offset--;
			}
		}

		return null;
	}
}