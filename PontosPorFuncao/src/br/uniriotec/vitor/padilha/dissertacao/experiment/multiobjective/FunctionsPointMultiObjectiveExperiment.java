package br.uniriotec.vitor.padilha.dissertacao.experiment.multiobjective;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import jmetal.base.Algorithm;
import jmetal.base.Solution;
import jmetal.base.SolutionSet;
import jmetal.util.JMException;
import unirio.experiments.multiobjective.execution.FileMultiExperimentListener;
import unirio.experiments.multiobjective.execution.MultiExperiment;
import unirio.experiments.multiobjective.execution.MultiExperimentListener;
import unirio.experiments.multiobjective.execution.RunTimeListener;
import br.uniriotec.vitor.padilha.dissertacao.algorithm.Algorithms;
import br.uniriotec.vitor.padilha.dissertacao.calculator.FunctionPointCalculator;
import br.uniriotec.vitor.padilha.dissertacao.experiment.IFunctionsPointExperiment;
import br.uniriotec.vitor.padilha.dissertacao.factory.AlgorithmFactory;
import br.uniriotec.vitor.padilha.dissertacao.model.FunctionPointSystem;
import br.uniriotec.vitor.padilha.dissertacao.model.ParametersAlgorithm;
import br.uniriotec.vitor.padilha.dissertacao.problem.FunctionsPointProblem;
import br.uniriotec.vitor.padilha.dissertacao.problem.multiobjective.FunctionsPointMultiProblem;

public class FunctionsPointMultiObjectiveExperiment extends MultiExperiment<FunctionPointSystem> implements IFunctionsPointExperiment<FunctionPointSystem>{



	private Map<Integer,Map<Integer, Vector<Solution>>> solutions = new HashMap<Integer, Map<Integer,Vector<Solution>>>();
	int cycleNumber = 0;
	private RunTimeListener runTimeListener;
	private Vector<Solution> instanceFrontier;
	
	private TreeMap<Long, MultiExperimentListener> multiExperimentListeners = new TreeMap<Long, MultiExperimentListener>();		
	private Algorithm algorithm;
	

	public FunctionsPointMultiObjectiveExperiment(FunctionPointSystem instance, 
			Integer numeroTransacoesFaltantes, int numeroMaximoDePontosDeFuncao,  Long baseSatisfaction, FunctionPointCalculator functionPointCalculator
			,Algorithms algoritmo, ParametersAlgorithm parametersAlgorithm, String baseDirName, boolean eliminateRetsAndDets) throws JMException, ClassNotFoundException {
		
		FunctionsPointProblem problem = createProblem(numeroTransacoesFaltantes,instance, functionPointCalculator,numeroMaximoDePontosDeFuncao, baseSatisfaction, eliminateRetsAndDets );
		this.algorithm = AlgorithmFactory.getAlgorithm(algoritmo,problem, parametersAlgorithm,numeroMaximoDePontosDeFuncao, instance , eliminateRetsAndDets, functionPointCalculator);
		int maxEvaluations = (Integer) algorithm.getInputParameter("maxEvaluations");	

		int fractions = (new BigDecimal(Double.valueOf(maxEvaluations)/10)).setScale(0, RoundingMode.UP).intValue();
		for (int i = fractions; i < maxEvaluations; i+=fractions) {
			MultiExperimentListener multiExperimentListener2;
			try {
				multiExperimentListener2 = new FileMultiExperimentListener(baseDirName+"-"+i+".txt", true);
				multiExperimentListener2.prepareInstance(0);
				multiExperimentListeners.put(Long.valueOf(i), multiExperimentListener2);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	@Override
	public void terminateExperiment() throws Exception {
		// TODO Auto-generated method stub
		
		super.terminateExperiment();
		
		for (MultiExperimentListener multiExperimentListener2 : multiExperimentListeners.values()) {
			try {
				multiExperimentListener2.terminateInstance(0, instanceFrontier);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	@Override
	public SolutionSet runCycle(FunctionPointSystem instance, int instanceNumber) throws ClassNotFoundException, JMException {
			
		runTimeListener = new RunTimeListener(multiExperimentListeners, this.cycleNumber, instanceNumber, this);
		SolutionSet solutionSet = algorithm.execute(runTimeListener);
	
		return solutionSet;
	}
	@Override
	public void publishCycle(int cycleNumber, int instanceNumber,
			long executionTime, Vector<Solution> cycleFrontier,
			Vector<Solution> instanceFrontier) throws Exception {
		// TODO Auto-generated method stub
		super.publishCycle(cycleNumber, instanceNumber, executionTime, cycleFrontier,
				instanceFrontier);
		
		if(solutions.get(instanceNumber)==null){
			solutions.put(instanceNumber, new HashMap<Integer,  Vector<Solution>>());
		}
		solutions.get(instanceNumber).put(cycleNumber, cycleFrontier);
		System.gc();
		System.out.println("Ciclo:"+cycleNumber);
		this.cycleNumber = cycleNumber+1;
		this.instanceFrontier = instanceFrontier;
	}

	protected FunctionsPointProblem createProblem(int numeroDeTransacoes,FunctionPointSystem functionPointSystem, FunctionPointCalculator functionPointCalculator,  int numeroMaximoDePontosDeFuncao, Long baseSatisfaction, boolean eliminateRetsAndDets) throws ClassNotFoundException {
		return new FunctionsPointMultiProblem(functionPointSystem,functionPointCalculator, numeroMaximoDePontosDeFuncao, numeroDeTransacoes, baseSatisfaction, eliminateRetsAndDets);
	}


	public Map<Integer, Map<Integer, Vector<Solution>>> getSolutions() {
		return solutions;
	}
	public void setSolutions(Map<Integer, Map<Integer, Vector<Solution>>> solutions) {
		this.solutions = solutions;
	}
	@Override
	public void run2(Vector<FunctionPointSystem> instances, int cycles) throws Exception {
		run(instances, cycles);		
	}

}