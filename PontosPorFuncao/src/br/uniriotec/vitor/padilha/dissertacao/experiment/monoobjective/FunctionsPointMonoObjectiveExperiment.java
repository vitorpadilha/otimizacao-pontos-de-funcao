package br.uniriotec.vitor.padilha.dissertacao.experiment.monoobjective;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import jmetal.base.Algorithm;
import jmetal.base.Solution;
import jmetal.util.JMException;
import unirio.experiments.monoobjective.execution.MonoExperiment;
import br.uniriotec.vitor.padilha.dissertacao.algorithm.Algorithms;
import br.uniriotec.vitor.padilha.dissertacao.calculator.FunctionPointCalculator;
import br.uniriotec.vitor.padilha.dissertacao.experiment.IFunctionsPointExperiment;
import br.uniriotec.vitor.padilha.dissertacao.factory.AlgorithmFactory;
import br.uniriotec.vitor.padilha.dissertacao.model.FunctionPointSystem;
import br.uniriotec.vitor.padilha.dissertacao.model.ParametersAlgorithm;
import br.uniriotec.vitor.padilha.dissertacao.problem.FunctionsPointProblem;
import br.uniriotec.vitor.padilha.dissertacao.problem.monoobjective.FunctionsPointMonoProblem;

public class FunctionsPointMonoObjectiveExperiment extends MonoExperiment<FunctionPointSystem> implements IFunctionsPointExperiment{

	private FunctionPointCalculator functionPointCalculator;
	private Integer numeroTransacoesFaltantes;
	private int numeroMaximoDePontosDeFuncao;
	private Long baseSatisfaction;
	private Algorithms algoritmo;
	private ParametersAlgorithm parametersAlgorithm;
	private boolean eliminateRetsAndDets;
	private Map<Integer,Map<Integer, Vector<Solution>>> solutions = new HashMap<Integer, Map<Integer, Vector<Solution>>>();
	
	public Map<Integer,Map<Integer, Vector<Solution>>> getSolutions() {
		return solutions;
	}

	public void setSolutions(Map<Integer,Map<Integer, Vector<Solution>>> solutions) {
		this.solutions = solutions;
	}

	@Override
	public Solution runCycle(FunctionPointSystem instance, int instanceNumber) throws ClassNotFoundException, JMException {
		FunctionsPointProblem problem = createProblem(numeroTransacoesFaltantes,instance, functionPointCalculator,numeroMaximoDePontosDeFuncao, this.baseSatisfaction );
		Algorithm algorithm = AlgorithmFactory.getAlgorithm(algoritmo,problem, parametersAlgorithm,numeroMaximoDePontosDeFuncao, instance , eliminateRetsAndDets, functionPointCalculator);
		return algorithm.execute().get(0);
	}
	
	@Override
	protected void publishCycle(int cycleNumber, int instanceNumber,
			long executionTime, Solution cycleSolution) throws Exception {
		super.publishCycle(cycleNumber, instanceNumber, executionTime, cycleSolution);
		if(solutions.get(instanceNumber)==null){
			solutions.put(instanceNumber, new HashMap<Integer, Vector<Solution>>());
		}
		solutions.get(instanceNumber).put(cycleNumber, new Vector<Solution>());
		solutions.get(instanceNumber).get(cycleNumber).add(cycleSolution);
		System.gc();
		System.out.println("Ciclo:"+cycleNumber);
	}

	protected FunctionsPointProblem createProblem(int numeroDeTransacoes,FunctionPointSystem functionPointSystem, FunctionPointCalculator functionPointCalculator,  int numeroMaximoDePontosDeFuncao, Long baseSatisfaction) throws ClassNotFoundException {
		return new FunctionsPointMonoProblem(functionPointSystem,functionPointCalculator, numeroMaximoDePontosDeFuncao, numeroDeTransacoes,baseSatisfaction,eliminateRetsAndDets);
	}
	
	public FunctionPointCalculator getFunctionPointCalculator() {
		return functionPointCalculator;
	}

	public void setFunctionPointCalculator(
			FunctionPointCalculator functionPointCalculator) {
		this.functionPointCalculator = functionPointCalculator;
	}

	public Integer getNumeroTransacoesFaltantes() {
		return numeroTransacoesFaltantes;
	}

	public void setNumeroTransacoesFaltantes(Integer numeroTransacoesFaltantes) {
		this.numeroTransacoesFaltantes = numeroTransacoesFaltantes;
	}

	public int getNumeroMaximoDePontosDeFuncao() {
		return numeroMaximoDePontosDeFuncao;
	}

	public void setNumeroMaximoDePontosDeFuncao(int numeroMaximoDePontosDeFuncao) {
		this.numeroMaximoDePontosDeFuncao = numeroMaximoDePontosDeFuncao;
	}

	public Algorithms getAlgoritmo() {
		return algoritmo;
	}

	public void setAlgoritmo(Algorithms algoritmo) {
		this.algoritmo = algoritmo;
	}

	@Override
	public void run2(Vector instances, int cycles) throws Exception {
		run(instances, cycles);		
	}

	public Long getBaseSatisfaction() {
		return baseSatisfaction;
	}

	public void setBaseSatisfaction(Long baseSatisfaction) {
		this.baseSatisfaction = baseSatisfaction;
	}

	public boolean isEliminateRetsAndDets() {
		return eliminateRetsAndDets;
	}

	public void setEliminateRetsAndDets(boolean eliminateRetsAndDets) {
		this.eliminateRetsAndDets = eliminateRetsAndDets;
	}

	public ParametersAlgorithm getParametersAlgorithm() {
		return parametersAlgorithm;
	}

	public void setParametersAlgorithm(ParametersAlgorithm parametersAlgorithm) {
		this.parametersAlgorithm = parametersAlgorithm;
	}

}