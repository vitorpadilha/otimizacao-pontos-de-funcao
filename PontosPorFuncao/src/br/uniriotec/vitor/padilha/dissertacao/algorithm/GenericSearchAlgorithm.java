package br.uniriotec.vitor.padilha.dissertacao.algorithm;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.bind.JAXBException;

import unirio.experiments.monoobjective.execution.MonoExperimentListener;
import br.uniriotec.vitor.padilha.dissertacao.calculator.FunctionPointCalculator;
import br.uniriotec.vitor.padilha.dissertacao.engine.FunctionPointFactory;
import br.uniriotec.vitor.padilha.dissertacao.exception.ElementException;
import br.uniriotec.vitor.padilha.dissertacao.model.FunctionPointSystem;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.DET;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.DataModelElement;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.RET;
import br.uniriotec.vitor.padilha.dissertacao.model.transactionModel.Transaction;
import br.uniriotec.vitor.padilha.dissertacao.utils.FormatUtils;
import br.uniriotec.vitor.padilha.dissertacao.utils.FunctionsPointReader;

public abstract class GenericSearchAlgorithm {
	
	
	private int criterio;
	
	protected Vector<MonoExperimentListener> listeners;
	
	protected FunctionPointSystem currentFunctionPointSystem;
	
	private Map<String,Transaction> transactionsNamesOfReference;
	
	private Map<String,DET> detsNamesOfReference;
	
	protected Long currentSatisfaction;
	
	protected FunctionPointSystem  functionPointSystemReference;
	
	private FunctionPointCalculator calculator;
	
	private int pontosDeFuncaoPorRelease = 0;
	
	private File archivePath;
	
	public GenericSearchAlgorithm() {
		this.listeners = new Vector<MonoExperimentListener>();
	}
	
	/**
	 * Adiciona um listener no experimento
	 */
	public void addListerner(MonoExperimentListener listener)
	{
		listeners.add(listener);
	}
	
	/**
	 * Configura o nomes das Transações e DET's para posteriormente verificar se já foi implementada. 
	 */
	private void configureElementsNames() {
		transactionsNamesOfReference = new HashMap<String, Transaction>();
		for (Transaction transaction : functionPointSystemReference.getTransactionModel().getTransactions()) {
			transactionsNamesOfReference.put(transaction.getName(), transaction);
		}
		detsNamesOfReference = new HashMap<String, DET>();
		for (DataModelElement dataModelElement : functionPointSystemReference.getDataModel().getDataModelElements()) {
			for(RET ret:dataModelElement.getRets()) {
				for(DET det:ret.getDets()) {
					detsNamesOfReference.put(dataModelElement.getName()+"."+ret.getName()+"."+det.getName(), det);
				}
			}
		}
	}
	
	public int getCriterio() {
		return criterio;
	}

	public void setCriterio(int criterio) {
		this.criterio = criterio;
	}
	
	/**
	 * Carrega os casos de testes
	 * @throws IOException
	 * @throws JAXBException 
	 * @throws ElementException 
	 */
	public final void initialize() throws IOException, JAXBException, ElementException{
		FunctionsPointReader reader = new FunctionsPointReader("resources/teste.xml","resources/grauInteresse.xml");
		functionPointSystemReference = reader.read();
		calculator = new FunctionPointCalculator();
		initializeReference();
		configureElementsNames();
	}
	
	public abstract void execute() throws Exception;
	
	
	private void setImplementedInElementsOfRelease(
			FunctionPointSystem pontosPorFuncao, int releaseNumber) {
		for(Transaction selectedTransaction:pontosPorFuncao.getTransactionModel().getTransactions()){
			if(selectedTransaction.getReleaseImplementation()==0)
				transactionsNamesOfReference.get(selectedTransaction.getName()).setReleaseImplementation(releaseNumber);
		}
		for (DataModelElement dataModelElement : pontosPorFuncao.getDataModel().getDataModelElements()) {
			for(RET ret:dataModelElement.getRets()) {
				for(DET det:ret.getDets()) {
					detsNamesOfReference.get(dataModelElement.getName()+"."+ret.getName()+"."+det.getName()).setImplementada(true);
				}
			}
		}
	}
	
	public void publishRelease(){
		
	}
	
	private void initializeReference() {
		for(Transaction selectedTransaction:this.functionPointSystemReference.getTransactionModel().getTransactions()){
			selectedTransaction.setReleaseImplementation(0);
		}
		for (DataModelElement dataModelElement : this.functionPointSystemReference.getDataModel().getDataModelElements()) {
			for(RET ret:dataModelElement.getRets()) {
				for(DET det:ret.getDets()) {
					det.setImplementada(false);
				}
			}
		}
	}
//	
//	public final void showResult(){
//		//System.out.println("O melhor conjunto encontrado para os parametros são:");
//		if(currentSatisfaction!=null){
//			List<TestCase> listaResultado= new ArrayList<TestCase>(currentFunctionPointSystem.getTests());
//			Collections.sort(listaResultado);
//			Long sumExecutionTime = 0L;
//			Double sumCoverage = 0D;
//			if(currentTestCaseSet!=null) {
//				System.out.println("");
//				for(TestCase testCase:listaResultado) {
//					System.out.println(testCase.getName());
//					sumExecutionTime+=testCase.getExecutionTime();	
//					for(String funcionalidade:testCase.getCoverage().keySet()) {
//						sumCoverage+=testCase.getCoverage().get(funcionalidade);
//					}
//				}
//				System.out.println("Total  Conjunto:"+sumExecutionTime+" de 847");
//				System.out.println("Cobertura :"+sumCoverage+"");
//			}
//			else {
//				System.out.println("Não encontrado!");
//			}
//		}
//	}
//	
//	public final void showResult(DefaultTableModel dm){
//		if(currentTestCaseSet!=null) {
//			List<TestCase> listaResultado= new ArrayList<TestCase>(currentTestCaseSet.getTests());
//			Collections.sort(listaResultado);
//			
//				Long sumExecutionTime = 0L;
//				Double sumCoverage = 0D;
//				System.out.println("");
//				for(TestCase testCase:listaResultado) {
//					dm.addRow(new Object[] { testCase.getName(), testCase.getExecutionTime() });
//					sumExecutionTime+=testCase.getExecutionTime();		
//					Long coverage = 0L;
//					for(String funcionalidade:testCase.getCoverage().keySet()) {
//						coverage+=testCase.getCoverage().get(funcionalidade);
//					}
//					//coverage=coverage/testCase.getCoverage().size();
//					sumCoverage+=coverage;
//				}
//				dm.addRow(new Object[] { "Tempo total", sumExecutionTime });
//				dm.addRow(new Object[] { "Cobertura total", sumCoverage });
//		}
//		else {
//			System.out.println("Não encontrado!");
//		}
//		
//	}
	/**
	 * Retorna um representação para um conjunto de casos de testes a serem testados
	 * @param elementNumber Numero a ser convertido, vai de 0 à ((2 elevado a N elementos)-1)
	 * @return
	 */
	public Boolean[] getSetRepresentation(Long elementNumber) {
		String by = FormatUtils.formatStringLength(Long.toBinaryString(elementNumber),functionPointSystemReference.getTransactionModel().getTransactions().size(),"0");
		Boolean[] setRepresentation = new Boolean[functionPointSystemReference.getTransactionModel().getTransactions().size()];
		for(int a=0;a<by.length();a++) {
			switch (by.charAt(a)) {
				case '1':
					setRepresentation[a]=true;
					break;
				default:
					setRepresentation[a]=false;
					break;
			}
		}
		return setRepresentation;		
	}
	
	//protected boolean
	
	
	
	/**
	 * Retorna um representação para um conjunto de casos de testes a serem testados
	 * @param elementNumber Conjunto de testes que precisa ser alterado.
	 * @return
	 */
	public Boolean[] getSetRepresentation(FunctionPointSystem functionPointSystem) {
		Boolean[] setRepresentation = new Boolean[this.functionPointSystemReference.getTransactionModel().getTransactions().size()];
		Set<String> transactionsName = new HashSet<String>();
		for(Transaction transaction:this.functionPointSystemReference.getTransactionModel().getTransactions()){
			transactionsName.add(transaction.getName());
		}
		for(int a=0;a<setRepresentation.length;a++) {
			setRepresentation[a] = false;
			for(Transaction transaction:functionPointSystem.getTransactionModel().getTransactions()){
				if(transactionsName.contains(transaction.getName())) {
					setRepresentation[a] = true;
				}
			}
		}
		return setRepresentation;		
	}
	
	
	/**
	 * Retorna um conjunto de casos de testes a serem testados
	 * @param elementNumber
	 * @return
	 */
	public FunctionPointSystem getFunctionPointSystem(Boolean[] setRepresetation) {
		FunctionPointSystem retorno = FunctionPointFactory.getFunctionPointSystem(setRepresetation, this.functionPointSystemReference, true);	
		return retorno;
	}
	
	
//	public Double getCoverageOfSet(TestCaseSet testCaseSet) {
//		
//		return this.getCoverageOfSet(testCaseSet.getTests());
//	}
	
	public Integer getFunctionPointValue(FunctionPointSystem functionPointSystem, int releaseNumber) {
		
		return calculator.calculate(functionPointSystem, releaseNumber, null);
	}
	/**
	 * Retorna o tempo de execução dos conjunto de casos de teste
	 * @param testCaseSet
	 * @return
	 */
	public Long getSatisfacaoPatrocinadores(FunctionPointSystem functionPointSystem) {
		return calculator.calculateUserSatisfaction(functionPointSystem);
	}
	
	/**
	 * Retorna o número de possibilidades de conjuntos a serem criados
	 * @return
	 */
	protected Long getSetSize() {
		return Math.round(Math.pow(Double.valueOf(2), this.functionPointSystemReference.getTransactionModel().getTransactions().size()));
	}

	public File getArchivePath() {
		return archivePath;
	}

	public void setArchivePath(File archivePath) {
		this.archivePath = archivePath;
	}

	public int getPontosDeFuncaoPorRelease() {
		return pontosDeFuncaoPorRelease;
	}

	public void setPontosDeFuncaoPorRelease(int pontosDeFuncaoPorRelease) {
		this.pontosDeFuncaoPorRelease = pontosDeFuncaoPorRelease;
	}
}
