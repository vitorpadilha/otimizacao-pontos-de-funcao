package jmetal.base.visitor.neighborhood;

import jmetal.base.Solution;
import jmetal.util.JMException;

public interface NeighborVisitor
{
	int neighborCount(Solution solution);
	
	Solution getNeighbor(Solution solution, int index) throws ClassNotFoundException, JMException;
}