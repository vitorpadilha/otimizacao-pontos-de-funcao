package br.uniriotec.vitor.padilha.dissertacao.factory;

import jmetal.base.Algorithm;
import jmetal.base.Operator;
import jmetal.base.Problem;
import jmetal.base.operator.crossover.SinglePointCrossover;
import jmetal.base.operator.mutation.BitFlipMutation;
import jmetal.base.operator.selection.BinaryTournament;
import jmetal.base.visitor.neighborhood.BinaryNeighborVisitor;
import jmetal.base.visitor.neighborhood.NeighborVisitor;
import jmetal.metaheuristics.mocell.MOCell;
import jmetal.metaheuristics.nsgaII.NSGAII;
import jmetal.metaheuristics.paes.PAES;
import jmetal.metaheuristics.randomSearch.HillClimbing;
import jmetal.metaheuristics.randomSearch.MonoRandomSearch;
import jmetal.metaheuristics.randomSearch.RandomSearch;
import jmetal.metaheuristics.singleObjective.geneticAlgorithm.gGA;
import jmetal.metaheuristics.spea2.SPEA2;
import jmetal.util.JMException;
import br.uniriotec.vitor.padilha.dissertacao.algorithm.Algorithms;
import br.uniriotec.vitor.padilha.dissertacao.algorithm.FunctionPointsGA;
import br.uniriotec.vitor.padilha.dissertacao.algorithm.FunctionsPointHC;
import br.uniriotec.vitor.padilha.dissertacao.algorithm.HillClimbingClustering;
import br.uniriotec.vitor.padilha.dissertacao.algorithm.JustOneFlagAlgorithm;
import br.uniriotec.vitor.padilha.dissertacao.calculator.FunctionPointCalculator;
import br.uniriotec.vitor.padilha.dissertacao.model.FunctionPointSystem;
import br.uniriotec.vitor.padilha.dissertacao.model.ParametersAlgorithm;

public class AlgorithmFactory {

	public static final Algorithm getAlgorithm(Algorithms algorithmEnum, Problem problem, ParametersAlgorithm parametersAlgorithm, int budget, FunctionPointSystem functionPointSystem, boolean eliminateRetsAndDets, FunctionPointCalculator calculator) throws JMException {
		Algorithm algorithm = null;
		if(parametersAlgorithm.getNumeroDeTransacoes()==0) {
			parametersAlgorithm.setNumeroMaximoDeAvaliacoes(parametersAlgorithm.getNumeroDeTransacoes()*2);
			parametersAlgorithm.setNumeroMaximoDeAvaliacoes(parametersAlgorithm.getNumeroMaximoDeAvaliacoes()*parametersAlgorithm.getNumeroMaximoDeAvaliacoes());
		}
		if(parametersAlgorithm.getNumeroMaximoDeAvaliacoes()<20)
			parametersAlgorithm.setNumeroMaximoDeAvaliacoes(20);
		if(algorithmEnum==Algorithms.GENETIC){
			algorithm = new gGA(problem);
			AlgorithmFactory.configureGA(algorithm, parametersAlgorithm);
		}
		if(algorithmEnum==Algorithms.FUNCTIONPOINTGA){
			algorithm = new FunctionPointsGA(problem, budget, functionPointSystem, eliminateRetsAndDets, calculator);
			AlgorithmFactory.configureGA(algorithm, parametersAlgorithm);
		}
		else if (algorithmEnum==Algorithms.HILL_CLIMBING) {			
			NeighborVisitor neighborVisitor = new BinaryNeighborVisitor(problem);
			algorithm = new HillClimbing(problem,neighborVisitor,(int)parametersAlgorithm.getNumeroMaximoDeAvaliacoes());
		}
		
		if(algorithmEnum==Algorithms.FUNCTIONPOINTHC){
			NeighborVisitor neighborVisitor = new BinaryNeighborVisitor(problem);
			algorithm = new FunctionsPointHC(problem,neighborVisitor, budget, functionPointSystem, eliminateRetsAndDets, calculator);
			AlgorithmFactory.configureGA(algorithm, parametersAlgorithm);
		}
		else if (algorithmEnum==Algorithms.NEWHC) {			
			NeighborVisitor neighborVisitor = new BinaryNeighborVisitor(problem);
			algorithm = new HillClimbingClustering(problem,neighborVisitor);
			AlgorithmFactory.configureGA(algorithm, parametersAlgorithm);
		}
		
		else if (algorithmEnum==Algorithms.VARRE_FLAGS) {			
			algorithm = new JustOneFlagAlgorithm(problem, (int)parametersAlgorithm.getNumeroMaximoDeAvaliacoes());
		}
		else if (algorithmEnum==Algorithms.RANDOM) {			
			algorithm = new MonoRandomSearch(problem, (int)parametersAlgorithm.getNumeroMaximoDeAvaliacoes());
			algorithm.setInputParameter("maxEvaluations", (int)parametersAlgorithm.getNumeroMaximoDeAvaliacoes());			
		}
		else if (algorithmEnum==Algorithms.ALEATÓRIO) {			
			algorithm = new RandomSearch(problem);
			algorithm.setInputParameter("maxEvaluations", (int)parametersAlgorithm.getNumeroMaximoDeAvaliacoes());			
		}
		else if(algorithmEnum==Algorithms.NSGAII){
			algorithm = new NSGAII(problem);
			AlgorithmFactory.configureGA(algorithm, parametersAlgorithm);
		}
		else if(algorithmEnum==Algorithms.MOCELL){
			algorithm = new MOCell(problem);
			AlgorithmFactory.configureGA(algorithm, parametersAlgorithm);
			//AlgorithmFactory.configureMOCell(algorithm, parametersAlgorithm);
		}
		else if(algorithmEnum==Algorithms.PAES){
			algorithm = new PAES(problem);
			AlgorithmFactory.configureGA(algorithm, parametersAlgorithm);
		}
		else if(algorithmEnum==Algorithms.SPEA2){
			algorithm = new SPEA2(problem);
			AlgorithmFactory.configureGA(algorithm, parametersAlgorithm);
			AlgorithmFactory.configureSPEA2(algorithm, parametersAlgorithm);
		}
		return algorithm;
	}
	
	private static void configureSPEA2(Algorithm ga, ParametersAlgorithm parametersAlgorithm) {
		int tamanhoPopulacao =  Double.valueOf(parametersAlgorithm.getMultiplicadorPopulacao() * parametersAlgorithm.getNumeroDeTransacoes()).intValue();
		ga.setInputParameter("archiveSize", tamanhoPopulacao);
	}
	
	private static void configureGA(Algorithm ga, ParametersAlgorithm parametersAlgorithm) {
		//int tamanhoPopulacao = 4 * numeroTransacoes;
		Operator selection = new BinaryTournament();		
		Operator crossover = new SinglePointCrossover();
		//crossover.setParameter("probability", 0.8);
		crossover.setParameter("probability", parametersAlgorithm.getCrossoverProbability());	
		Operator mutation = new BitFlipMutation();
		//mutation.setParameter("probability", 0.02);
		mutation.setParameter("probability", parametersAlgorithm.getMutationProbability());
		int populationSize = Double.valueOf(parametersAlgorithm.getMultiplicadorPopulacao()*parametersAlgorithm.getNumeroDeTransacoes()).intValue();
		if((populationSize%2)==1) {
			populationSize++;
		}
		ga.setInputParameter("populationSize", populationSize);
		ga.setInputParameter("maxEvaluations", (int)parametersAlgorithm.getNumeroMaximoDeAvaliacoes());
		ga.addOperator("crossover", crossover);
		ga.addOperator("mutation", mutation);
		ga.addOperator("selection", selection);
		//System.out.println("Parametros do algoritmo: Crossover = "+parametersAlgorithm.getCrossoverProbability()+", Mutation = "+ parametersAlgorithm.getMutationProbability() +", Tamanho da População = "+populationSize);
	}
}
