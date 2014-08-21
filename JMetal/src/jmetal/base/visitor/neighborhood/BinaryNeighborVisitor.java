package jmetal.base.visitor.neighborhood;

import jmetal.base.Problem;
import jmetal.base.Solution;
import jmetal.base.Variable;
import jmetal.base.variable.Binary;
import jmetal.util.Configuration;
import jmetal.util.JMException;

public class BinaryNeighborVisitor implements NeighborVisitor
{
	private Problem problem;
	private int numberOfBits;
	
	public BinaryNeighborVisitor(Problem problem) throws JMException
	{
		if (problem.variableType_.length != 1)
		{
			Configuration.logger_.severe("NeighborVisitor.execute: problems should have a single variable");
			throw new JMException("NeighborVisitor.execute: problems should have a single variable");
		}

		if (problem.variableType_[0] != Binary.class)
		{
			Configuration.logger_.severe("NeighborVisitor.execute: problems should have a single binary variable");
			throw new JMException("NeighborVisitor.execute: problems should have a single binary variable");
		}
		
		this.problem = problem;
		problem.getLength(0);
		this.numberOfBits = problem.getLength(0);
	}

	@Override
	public int neighborCount(Solution solution)
	{
 		return numberOfBits;
	}

	@Override
	public Solution getNeighbor(Solution solution, int index) throws ClassNotFoundException, JMException
	{
		Binary variable = (Binary) solution.getDecisionVariables()[0];
		Binary neighborVariable = new Binary(variable);
		neighborVariable.flip(index);
		Variable[] variables = { neighborVariable };
		return new Solution(problem, variables);
	}
}