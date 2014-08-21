package br.uniriotec.vitor.padilha.dissertacao.listener.mono;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import jmetal.base.Solution;
import jmetal.base.variable.Binary;
import unirio.experiments.monoobjective.execution.StreamMonoExperimentListener;
import unirio.experiments.multiobjective.analysis.model.ParetoFront;
import unirio.experiments.multiobjective.analysis.model.ParetoFrontVertex;
import br.uniriotec.vitor.padilha.dissertacao.algorithm.Algorithms;
import br.uniriotec.vitor.padilha.dissertacao.calculator.FunctionPointCalculator;
import br.uniriotec.vitor.padilha.dissertacao.model.FunctionPointSystem;
import br.uniriotec.vitor.padilha.dissertacao.utils.NumberUtils;
import br.uniriotec.vitor.padilha.dissertacao.view.IFunctionPointView;
import br.uniriotec.vitor.padilha.dissertacao.view.WebFunctionPointView;

public class FunctionsPointDetailsListener extends StreamMonoExperimentListener
{
	private FunctionPointCalculator functionPointCalculator;
	
	private FunctionPointSystem functionPointSystem;
	
	private Long baseSatisfaction;
	
	private int baseFunctionsPointValue;
	
	private Set<IFunctionPointView> views;
	
	private Map<Integer, Map<Integer,List<SolutionData>>> releasesInfo;
	
	private File fileSatisfacaoEPP;
	
	private Algorithms algorithm;
	
	public static Properties properties;
	
	//public static final String BASE_DIRECTORY="C:\\Users\\PADILHA\\Desktop\\resultadoAlgoritmo\\instances\\";
	
	/**
	 * Inicializa o analisador de experimentos que publica resultados em arquivos
	 * 
	 * @param filename		Nome do arquivo que será usado como resultado
	 * @throws IOException 
	 */
	
	public static Properties getProp() throws IOException {
		if(properties==null) {
			Properties props = new Properties();
			FileInputStream file = new FileInputStream("C:/experimento-padilha/config.properties");
			props.load(file);
			properties = props;
		}
		return properties;
	}

	public static String getResultDir(){
		try {
			return FunctionsPointDetailsListener.getProp().getProperty("diretorio.resultado");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	public static String getInstanceDir(){
		try {
			return FunctionsPointDetailsListener.getProp().getProperty("diretorio.instancia");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	public FunctionsPointDetailsListener(String filename, FunctionPointCalculator functionPointCalculator, FunctionPointSystem functionPointSystem, int totalCycles, Algorithms algorithm) throws IOException
	{
		this(filename, false,functionPointCalculator,functionPointSystem, totalCycles, algorithm);
		
	}
	
	public static File criarArquivo(String nomeArquivo) throws IOException {
		File f = new File(".");
		//f  = new File(f.getCanonicalPath()+"\\resources\\"+nomeArquivo);
		f  = new File(FunctionsPointDetailsListener.getResultDir()+nomeArquivo);
		if(!f.exists()) {
			f.createNewFile();
		}
		return f;		
	}
	
	public static File criarDiretorio(String pasta) throws IOException {
		
		File dir = new File(FunctionsPointDetailsListener.getResultDir().replace('/', '\\')+pasta);  
		if(!dir.exists()) {
			boolean ok = dir.mkdirs();  
		}
		return dir;	
	}
	
	public static Integer getLastExecutionNumber(String pasta) throws IOException {
		
		File dir = new File(FunctionsPointDetailsListener.getResultDir()+pasta);  
		if(!dir.exists()) {
			boolean ok = dir.mkdirs();  
		}
		int i =1;
		boolean searched = false;
		while(!searched){
			File dir2 = new File(FunctionsPointDetailsListener.getResultDir()+pasta+"\\"+i);  
			if(!dir2.exists()) {
				return i-1;
			}
			i++;
		}
		return i;	
	}
	
	public static boolean deleteDir(File dir) {
		
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) { 
               boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
    
        // Agora o diretório está vazio, restando apenas deletá-lo.
        return dir.delete();
    }
	
	private void adicionaLinha(String conteudo, File arquivo) {
		FileReader in;
		try {
			in = new FileReader(arquivo);
			BufferedReader buff = new BufferedReader(in);
			String linha = "";
			List<String> linhas = new ArrayList<String>();
			while((linha = buff.readLine())!=null) {
				linhas.add(linha);
			}
			FileWriter wr = new FileWriter(arquivo);
			PrintWriter pw = new PrintWriter(wr);
			for(String linhaA:linhas){
				pw.write(linhaA);
				pw.println();
			}
			pw.write(conteudo);
			wr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static void deletar(String nomeArquivo) throws IOException {
		File f = new File(".");
		//f  = new File(f.getCanonicalPath()+"\\resources\\"+nomeArquivo);
		f  = new File(FunctionsPointDetailsListener.getResultDir()+nomeArquivo);
		
		if(f.exists()) {
			if(f.isFile()) {
				FileWriter wr = new FileWriter(f);
				wr.write("");
			}
			f.delete();
		}
	}
	
//	private void deletar(String nomeArquivo) throws IOException {
//		File f = new File(".");
//		//f  = new File(f.getCanonicalPath()+"\\resources\\"+nomeArquivo);
//		f  = new File("C:\\Users\\PADILHA\\Desktop\\resultadoAlgoritmo\\"+nomeArquivo);
//		
//		if(f.exists()) {
//			FileWriter wr = new FileWriter(f);
//			wr.write("");
//			f.delete();
//		}
//	}

	/**
	 * Inicializa o analisador de experimentos que publica resultados em arquivos
	 * 
	 * @param filename		Nome do arquivo que será usado como resultado
	 * @throws IOException 
	 */
	public FunctionsPointDetailsListener(String filename, boolean details, FunctionPointCalculator functionPointCalculator, FunctionPointSystem functionPointSystem, int totalCycles, Algorithms algorithm) throws IOException
	{
		super(new OutputStreamWriter(new FileOutputStream(filename)), details);
		this.functionPointCalculator=functionPointCalculator;
		this.functionPointSystem=functionPointSystem;
		
		baseSatisfaction = functionPointCalculator.calculateUserSatisfaction(functionPointSystem);
		baseFunctionsPointValue = functionPointCalculator.calculate(functionPointSystem, 0, null);
		views = new HashSet<IFunctionPointView>();
		WebFunctionPointView webFunctionPointView = new WebFunctionPointView(0,functionPointSystem);
		views.add(webFunctionPointView);
		releasesInfo = new HashMap<Integer, Map<Integer,List<SolutionData>>>();
		deleteDir(new File(FunctionsPointDetailsListener.getResultDir()+"\\instances\\"+algorithm.name()));
		this.algorithm = algorithm;
	}

	/**
	 * Verifica as melhores soluções e renderiza em HTML.
	 * Termina o experimento
	 */
	public void terminateExperiment() throws Exception
	{
//		getFunctionPointCalculator().getFunctionsView().add(functionPointView);
//		getFunctionPointCalculator().calculate(this.getFunctionPointSystem(),0);
//		for(FunctionPointSystem functionPointSystem:this.bestSolutions){
//			getFunctionPointCalculator().calculate(functionPointSystem);
//		}		
//		for(IFunctionPointView view:getFunctionPointCalculator().getFunctionsView()){
//			view.render();
//		}
		// TODO Auto-generated method stub
		criarDiretorio(algorithm.name()+"\\instancias");
		
		for (Integer instanceNumber : releasesInfo.keySet()) {
			Map<Integer, List<SolutionData>> infos = releasesInfo.get(instanceNumber);
			
			criarDiretorio(algorithm.name()+"\\instancias\\instancia"+instanceNumber);
			this.fileSatisfacaoEPP = criarArquivo(algorithm.name()+"\\instancias\\instancia"+instanceNumber+"\\ppESatisfacao.txt");
			for (Integer cycleNumber : infos.keySet()) {
				List<SolutionData> infos2 = infos.get(cycleNumber);
				double[][] cycleFrontier = new double[infos2.size()][2];
				int i = 0;
				for (SolutionData releaseInfo : infos2) {
					adicionaLinha(releaseInfo.toString(), this.fileSatisfacaoEPP);
					cycleFrontier[i][1] = releaseInfo.getFunctionsPointValue();
					cycleFrontier[i][0] = releaseInfo.getSatisfaction();
					//Double distance 
				}
				ParetoFront f1 = buildFrontier(cycleFrontier);
			}
		}
		
		
		getWriter().close();
	}
	
	private ParetoFront buildFrontier(double[][] frontier)
	{
		int numObjectives = frontier[0].length;
		ParetoFront f = new ParetoFront(numObjectives, 0);
		
		for (int i = 0; i < frontier.length; i++)
		{
			ParetoFrontVertex v = new ParetoFrontVertex(numObjectives, 0);
			v.setObjectives(frontier[i]);
			f.addVertex(v);
		}
		
		return f;
	}
//	
//	@Override
//	public void terminateInstance(int instanceNumber,
//			Vector<Solution> instanceFrontier) throws Exception {
//		super.terminateInstance(instanceNumber, instanceFrontier);
//		int i = 0;
//		int numeroDePontosDeFuncaoBase = getFunctionPointCalculator().calculate(this.functionPointSystem);
//		Long baseSatisfaction = getFunctionPointCalculator().calculateUserSatisfaction(this.functionPointSystem);
//		IFunctionPointView functionPointView = new WebFunctionPointView();
//		getFunctionPointCalculator().getFunctionsView().add(functionPointView);
//		getFunctionPointCalculator().calculate(this.getFunctionPointSystem());
//		for (Solution solution : instanceFrontier) {
//			Binary conjuntoTestes = (Binary) solution.getDecisionVariables()[0];
//			FunctionPointSystem functionPointSystem = FunctionPointFactory.getFunctionPointSystem(conjuntoTestes.bits_, this.functionPointSystem);
//			Long satisfaction = getFunctionPointCalculator().calculateUserSatisfaction(functionPointSystem);
//			functionPointView.addSatisfactionPercentForFunctionPoint(functionPointSystem, NumberUtils.formatNumber(((satisfaction.doubleValue()/baseSatisfaction)*100),2));
//			println("Satisfação: "+ satisfaction+" de "+baseSatisfaction +" ("+NumberUtils.formatNumber((satisfaction.doubleValue()/baseSatisfaction.doubleValue())*100, 2)+"%)");
//			getFunctionPointCalculator().calculate(functionPointSystem);
//			OutputStreamWriter grafo = new OutputStreamWriter(new FileOutputStream("resources/grafo"+i+".xdot"));
//			OutputStreamWriter grafoOnlyTransactions = new OutputStreamWriter(new FileOutputStream("resources/grafoOnlyTransactions"+i+".xdot"));
//			grafo.write(this.getFunctionPointSystem().doDot(functionPointSystem,true));
//			grafoOnlyTransactions.write(this.getFunctionPointSystem().doDot(functionPointSystem,false));			
//			grafo.close();
//			grafoOnlyTransactions.close();
//			i++;
//		}
//		for(IFunctionPointView view:getFunctionPointCalculator().getFunctionsView()){
//			view.render();
//		}
//	}
	
	
	@Override
	public void publishCycle(int cycleNumber, int instanceNumber,
			Solution solution, long executionTime, double[] data)
			throws Exception {
		for (IFunctionPointView v : views) {
			v.render();
		}
		views.clear();
		WebFunctionPointView webFunctionPointView = new WebFunctionPointView(cycleNumber+1,null);
		views.add(webFunctionPointView);
		//publishSolution(cycleNumber, solution);
	}
	
	
	
	public void publishSolution(int instanceNumber, int cycleNumber, int releaseNumber, int solutionNumber,
			FunctionPointSystem pontosPorFuncao, FunctionPointSystem pontosPorFuncaoReferencia, int functionsPointValue, Binary binary)
			throws IOException, FileNotFoundException {
		
		println("Número de Pontos por função: "+ functionsPointValue);
		
		Long satisfaction = getFunctionPointCalculator().calculateUserSatisfaction(pontosPorFuncao);
		//System.out.println();
		//System.out.println(releaseNumber+";"+functionsPointValue+";"+satisfaction+";"+ NumberUtils.formatNumber(((satisfaction.doubleValue()/baseSatisfaction)*100),2));
		if(!releasesInfo.containsKey(instanceNumber)){
			releasesInfo.put(instanceNumber, new HashMap<Integer,List<SolutionData>>());
		}
		if(!releasesInfo.get(instanceNumber).containsKey(cycleNumber)) {
			releasesInfo.get(instanceNumber).put(cycleNumber, new ArrayList<SolutionData>());
		}
		SolutionData solutionData = new SolutionData(cycleNumber,releaseNumber,solutionNumber,functionsPointValue,satisfaction.intValue(),NumberUtils.formatNumber(((satisfaction.doubleValue()/baseSatisfaction)*100),2),binary.toString());
		releasesInfo.get(instanceNumber).get(cycleNumber).add(solutionData);
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		for (IFunctionPointView v : views) {
			v.addSatisfactionForFunctionPoint(functionPointSystem,satisfaction);
			if(releaseNumber>0)
				v.addSatisfactionPercentForFunctionPoint(functionPointSystem, NumberUtils.formatNumber(((satisfaction.doubleValue()/baseSatisfaction)*100),2));
		}
		getFunctionPointCalculator().calculate(functionPointSystem, releaseNumber, views);
		//OutputStreamWriter grafo = new OutputStreamWriter(new FileOutputStream("resources/grafo-"+calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH)+" "+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND)+".xdot"));
		OutputStreamWriter grafo = new OutputStreamWriter(new FileOutputStream("resources/grafo"+cycleNumber+"-"+releaseNumber+".xdot"));
		OutputStreamWriter grafoOnlyTransactions = new OutputStreamWriter(new FileOutputStream("resources/grafoOnlyTransactions"+cycleNumber+".xdot"));
		//grafo.write(this.getFunctionPointSystem().doDot(functionPointSystem,true));
		//grafoOnlyTransactions.write(this.getFunctionPointSystem().doDot(functionPointSystem,false));
		
		grafo.close();
		grafoOnlyTransactions.close();
		
	}

	public FunctionPointCalculator getFunctionPointCalculator() {
		return functionPointCalculator;
	}

	public void setFunctionPointCalculator(
			FunctionPointCalculator functionPointCalculator) {
		this.functionPointCalculator = functionPointCalculator;
	}

	public FunctionPointSystem getFunctionPointSystem() {
		return functionPointSystem;
	}

	public void setFunctionPointSystem(FunctionPointSystem functionPointSystem) {
		this.functionPointSystem = functionPointSystem;
	}
	
	public class SolutionData {
		private int cycleNumber;
		private int releaseNumber;
		private int solutionNumber;
		
		private int functionsPointValue;
		private int satisfaction;
		private double satistactionPercent;
		private String binaryRepresentation;
		
		private SolutionData(int cycleNumber, int releaseNumber,
				int solutionNumber, int functionsPointValue, int satisfaction,
				double satistactionPercent, String binaryRepresentation) {
			super();
			this.cycleNumber = cycleNumber;
			this.releaseNumber = releaseNumber;
			this.solutionNumber = solutionNumber;
			this.functionsPointValue = functionsPointValue;
			this.satisfaction = satisfaction;
			this.satistactionPercent = satistactionPercent;
			this.binaryRepresentation = binaryRepresentation;
		}
		public int getCycleNumber() {
			return cycleNumber;
		}
		public void setCycleNumber(int cycleNumber) {
			this.cycleNumber = cycleNumber;
		}
		public int getReleaseNumber() {
			return releaseNumber;
		}
		public void setReleaseNumber(int releaseNumber) {
			this.releaseNumber = releaseNumber;
		}
		public int getSolutionNumber() {
			return solutionNumber;
		}
		public void setSolutionNumber(int solutionNumber) {
			this.solutionNumber = solutionNumber;
		}
		public int getFunctionsPointValue() {
			return functionsPointValue;
		}
		public void setFunctionsPointValue(int functionsPointValue) {
			this.functionsPointValue = functionsPointValue;
		}
		public int getSatisfaction() {
			return satisfaction;
		}
		public void setSatisfaction(int satisfaction) {
			this.satisfaction = satisfaction;
		}
		public double getSatistactionPercent() {
			return satistactionPercent;
		}
		public void setSatistactionPercent(double satistactionPercent) {
			this.satistactionPercent = satistactionPercent;
		}
		public String getBinaryRepresentation() {
			return binaryRepresentation;
		}
		public void setBinaryRepresentation(String binaryRepresentation) {
			this.binaryRepresentation = binaryRepresentation;
		}
		public String toString(){
			return "C"+cycleNumber+";R"+releaseNumber+";S"+solutionNumber+";"+functionsPointValue+";"+satisfaction+";"+ satistactionPercent +";"+binaryRepresentation.toString();
		}
	}
}