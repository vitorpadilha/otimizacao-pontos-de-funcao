package br.uniriotec.vitor.padilha.dissertacao.problem.multiobjective;

import jmetal.base.Solution;
import jmetal.base.solutionType.BinarySolutionType;
import br.uniriotec.vitor.padilha.dissertacao.calculator.FunctionPointCalculator;
import br.uniriotec.vitor.padilha.dissertacao.model.FunctionPointSystem;
import br.uniriotec.vitor.padilha.dissertacao.problem.FunctionsPointProblem;

public class FunctionsPointMultiProblem extends FunctionsPointProblem{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6841189917682752963L;

	public FunctionsPointMultiProblem(FunctionPointSystem functionPointSystem,
			FunctionPointCalculator calculator,
			Integer numeroMaximoDePontosPorFuncao, Integer numeroDeTransacoes, Long baseSatisfaction, boolean eliminateRetsAndDets)
			throws ClassNotFoundException {
		super(functionPointSystem, calculator, numeroMaximoDePontosPorFuncao,numeroDeTransacoes,baseSatisfaction,eliminateRetsAndDets);
		numberOfObjectives_ = 2;
		numberOfVariables_ = 1;
		solutionType_ = new BinarySolutionType(this);
		length_ = new int[numberOfVariables_];
		length_[0] = numeroDeTransacoes;
		 
	}

	@Override
	protected void adicionaObjetivos(Solution solution,
			Integer numeroDePontosDeFuncao, double satisfacaoTotal) {
		solution.setObjective(0, -satisfacaoTotal);
		solution.setObjective(1, numeroDePontosDeFuncao.doubleValue());
	}

}
