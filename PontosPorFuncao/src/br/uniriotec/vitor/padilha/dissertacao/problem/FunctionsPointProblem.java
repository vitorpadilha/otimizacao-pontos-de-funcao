package br.uniriotec.vitor.padilha.dissertacao.problem;

import jmetal.base.Problem;
import jmetal.base.Solution;
import jmetal.base.variable.Binary;
import br.uniriotec.vitor.padilha.dissertacao.calculator.FunctionPointCalculator;
import br.uniriotec.vitor.padilha.dissertacao.engine.FunctionPointFactory;
import br.uniriotec.vitor.padilha.dissertacao.model.FunctionPointSystem;
import br.uniriotec.vitor.padilha.dissertacao.model.transactionModel.Transaction;

public abstract class FunctionsPointProblem extends Problem {

	/**
	 * 
	 */
	public static final double INFINITO = 100000000000.0;
	protected static final int INFINITO_INTEIRO = 1000000000;
	private static final long serialVersionUID = 8765690254936090478L;
	private FunctionPointSystem functionPointSystem;
	private FunctionPointCalculator calculator;
	protected Integer numeroMaximoDePontosPorFuncao;
	private Long baseSatisfaction;
	private boolean eliminateRetsAndDets;
	//private int pontosPorFuncaoAnterior = 0;
	public FunctionsPointProblem(FunctionPointSystem functionPointSystem, FunctionPointCalculator calculator, Integer numeroMaximoDePontosPorFuncao, Integer numeroDeTransacoes, Long baseSatisfaction, boolean eliminateRetsAndDets) throws ClassNotFoundException {
		this.calculator =calculator;
		this.functionPointSystem = functionPointSystem;
		this.numeroMaximoDePontosPorFuncao = numeroMaximoDePontosPorFuncao;
		this.baseSatisfaction = baseSatisfaction;
		this.eliminateRetsAndDets = eliminateRetsAndDets;
		//this.pontosPorFuncaoAnterior = pontosPorFuncaoAnterior;
	}
	@Override
	public void evaluate(Solution solution) {
		Binary conjuntoTestes = (Binary) solution.getDecisionVariables()[0];
		int totalBitsOn = 0;
		for (int a = 0; a < conjuntoTestes.getNumberOfBits(); a++) {
			if(conjuntoTestes.bits_.get(a)) {
				totalBitsOn++;
			}
		}
		
		FunctionPointSystem pontosPorFuncao=null;
		
		pontosPorFuncao = FunctionPointFactory.getFunctionPointSystemConstructiveWay(conjuntoTestes, this.functionPointSystem, eliminateRetsAndDets);
		
		
		//contador++;
		//if(contador%2000==0)
			//System.out.println("Validacao"+contador);
		Integer totalDePontosPorFuncao = calculator.calculate(pontosPorFuncao, 0 ,null);
//		if(totalBitsOn<4) {
//			System.out.println("Total de Bits do Algoritmo: "+totalBitsOn+" - Total de Transações do Modelo (após adicionar transações dependentes):"+pontosPorFuncao.getTransactionModel().getTransactions().size()+" - ("+conjuntoTestes+ ") - Total de Pontos de Função:"+totalDePontosPorFuncao);
//		}
		//Compara com o tempo máximo informado pelo usuário. Ex: 200,300 e 400
		double coverage = calculator.calculateUserSatisfactionPercent(pontosPorFuncao, baseSatisfaction);
		
		adicionaObjetivos(solution, totalDePontosPorFuncao, coverage);
		
	}
	
	protected abstract void adicionaObjetivos(Solution solution, Integer numeroDePontosDeFuncao, double satisfacaoTotal);

}