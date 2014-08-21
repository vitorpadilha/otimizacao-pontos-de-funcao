package jmetal.base.visitor.neighborhood;

import jmetal.base.Problem;
import jmetal.base.Solution;
import jmetal.base.solutionType.IntSolutionType;
import jmetal.base.variable.Int;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

public class ShalowIntegerNeighborVisitor implements NeighborVisitor
{
	private Problem problem;
	
	public ShalowIntegerNeighborVisitor(Problem problem) throws JMException
	{
		if (problem.getSolutionType().getClass() != IntSolutionType.class)
		{
			Configuration.logger_.severe("NeighborVisitor.execute: problems should have a integer variables");
			throw new JMException("NeighborVisitor.execute: problems should have a integer variables");
		}
		
		this.problem = problem;
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
		return problem.getNumberOfVariables(); 
	}
	
	public Solution getNeighbor(Solution solution, int index) throws ClassNotFoundException, JMException
	{
		Int originalVariable = (Int) solution.getDecisionVariables()[index];
		int originalValue = (int) originalVariable.getValue();
		
		Solution neighbor = Solution.getNewSolution(problem);
		Int neighborVariable = (Int) neighbor.getDecisionVariables()[index];

		int length = problem.getNumberOfVariables();

		for (int i = 0; i < length; i++)
		{
			Int currentOriginalVariable = (Int) solution.getDecisionVariables()[i];
			Int currentNeighborVariable = (Int) neighbor.getDecisionVariables()[i];
			currentNeighborVariable.setValue(currentOriginalVariable.getValue());
		}
		
		int newValue = PseudoRandom.randInt((int)originalVariable.getLowerBound(), (int)originalVariable.getUpperBound());
		
		while (newValue == originalValue)
			newValue = PseudoRandom.randInt((int)originalVariable.getLowerBound(), (int)originalVariable.getUpperBound());
		
		neighborVariable.setValue(newValue);
		return neighbor;
	}
}