package br.uniriotec.vitor.padilha.dissertacao.algorithm;

import java.math.BigDecimal;

import br.uniriotec.vitor.padilha.dissertacao.calculator.FunctionPointCalculator;
import br.uniriotec.vitor.padilha.dissertacao.engine.FunctionPointFactory;
import br.uniriotec.vitor.padilha.dissertacao.model.FunctionPointSystem;
import jmetal.base.Problem;
import jmetal.base.Solution;
import jmetal.base.Variable;
import jmetal.base.variable.Binary;
import jmetal.metaheuristics.singleObjective.geneticAlgorithm.gGA;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

public class FunctionPointsGA  extends gGA
 {
	private int budget;
	private static double PERCENTUAL_ACEITE = 0.9;
	private FunctionPointSystem functionPointSystem;
	private boolean eliminateRetsAndDets;
	private FunctionPointCalculator calculator;
 	public FunctionPointsGA(Problem problem, int budget, FunctionPointSystem functionPointSystem, boolean eliminateRetsAndDets, FunctionPointCalculator calculator) {
		super(problem);
		this.budget = budget;
		this.functionPointSystem = functionPointSystem;
		this.eliminateRetsAndDets = eliminateRetsAndDets;
		this.calculator = calculator;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1817340406037434059L;

	@Override
 	protected Solution createNewSolution(Problem problem_) throws ClassNotFoundException, JMException
 	{
		
		int budgetForFunction = 0;
		Binary binary = new Binary(problem_.getNumberOfBits());
		for (int i = 0; i < problem_.getNumberOfBits(); i++)
		{
			binary.bits_.set(i, false);
		}
		while(budgetForFunction==0 || budgetForFunction <= budget*PERCENTUAL_ACEITE) {
			int bit = new BigDecimal(PseudoRandom.randDouble()*problem_.getNumberOfBits()).setScale(0,BigDecimal.ROUND_HALF_DOWN).intValue();
			binary.bits_.set(bit, true);
			FunctionPointSystem pontosPorFuncao = FunctionPointFactory.getFunctionPointSystemConstructiveWay(binary, this.functionPointSystem, this.eliminateRetsAndDets);
			budgetForFunction = calculator.calculate(pontosPorFuncao, 0 ,null);
		}
		if(binary.getNumberOfBits()<1) {
			System.out.println("Total de Bits do Algoritmo: "+binary.getNumberOfBits()+" - Total de Transações do Modelo (após adicionar transações dependentes):"+this.functionPointSystem.getTransactionModel().getTransactions().size()+" - ("+binary+ ") - Total de Pontos de Função:0");
		}
		Variable[] variables = new Variable[1];
		variables[0] = binary;
 		return new Solution(problem_,variables);
 	}	
 }