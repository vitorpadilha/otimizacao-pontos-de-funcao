package unirio.experiments.multiobjective.execution;

import java.util.TreeMap;
import java.util.Vector;
import jmetal.base.Solution;
import jmetal.base.SolutionSet;
import jmetal.util.Ranking;
import br.uniriotec.vitor.padilha.dissertacao.model.FunctionPointSystem;

public class RunTimeListener
{

	
	private Long nextEvaluation = 0L;
	
	private long lastExecutionTime = System.currentTimeMillis();

	protected TreeMap<Long, MultiExperimentListener> multiExperimentListeners = new TreeMap<Long, MultiExperimentListener>();
	
	Integer cycleNumber;
	
	Integer instanceNumber;
	
	private MultiExperiment<FunctionPointSystem> multiExperiment;
	boolean findLastEvaluation;

	public RunTimeListener(TreeMap<Long, MultiExperimentListener> multiExperimentListeners, int cycleNumber, int instanceNumber, MultiExperiment<FunctionPointSystem> multiExperiment){
		this.nextEvaluation = multiExperimentListeners.keySet().iterator().next().longValue();
		this.multiExperimentListeners = multiExperimentListeners;		
		this.multiExperiment = multiExperiment;
		this.cycleNumber = cycleNumber;
		this.instanceNumber = instanceNumber;
		findLastEvaluation = false;
	}
	
	public void listen(int evaluationNumber, SolutionSet solutionSet) {
		Vector<Solution> instanceFrontier = new Vector<Solution>();
		if(evaluationNumber>=nextEvaluation && !findLastEvaluation) {
			
			long executionTime = System.currentTimeMillis() - lastExecutionTime;
			
			Ranking ranking = new Ranking(solutionSet);			
			Vector<Solution> cycleFrontier = multiExperiment.getUniqueObjectives(ranking.getSubfront(0), 0.001);
			multiExperiment.mergeFrontiers(instanceFrontier, cycleFrontier);
			try
			{
				multiExperimentListeners.get(nextEvaluation).publishCycle(cycleNumber, instanceNumber, executionTime, cycleFrontier, instanceFrontier);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			if(!multiExperimentListeners.lastKey().equals(this.nextEvaluation))			
				this.nextEvaluation = multiExperimentListeners.higherKey(nextEvaluation);
			else 
				findLastEvaluation = true;
			lastExecutionTime =  System.currentTimeMillis();
		}
	}

	public int getCycleNumber()
	{
		return cycleNumber;
	}

	public void setCycleNumber(int cycleNumber)
	{
		this.cycleNumber = cycleNumber;
	}
	
}
