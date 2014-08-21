package br.uniriotec.vitor.padilha.dissertacao.analysis;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import jmetal.base.variable.Binary;

import unirio.experiments.monoobjective.analysis.model.MonoExperimentInstance;
import unirio.experiments.monoobjective.analysis.model.MonoExperimentResult;
import unirio.experiments.monoobjective.analysis.reader.MonoExperimentFileReader;
import unirio.experiments.multiobjective.analysis.model.MultiExperimentResult;
import unirio.experiments.multiobjective.analysis.model.ParetoFront;
import unirio.experiments.statistics.ExperimentalData2;
import unirio.experiments.statistics.ExperimentalData2.CycleInfos;
import unirio.experiments.statistics.Script2;
import unirio.experiments.statistics.Script2.DataSet;
import br.uniriotec.vitor.padilha.dissertacao.algorithm.Algorithms;
import br.uniriotec.vitor.padilha.dissertacao.algorithm.FunctionsPointHC;
import br.uniriotec.vitor.padilha.dissertacao.calculator.FunctionPointCalculator;
import br.uniriotec.vitor.padilha.dissertacao.engine.FunctionPointFactory;
import br.uniriotec.vitor.padilha.dissertacao.listener.mono.FunctionsPointDetailsListener;
import br.uniriotec.vitor.padilha.dissertacao.model.FunctionPointSystem;
import br.uniriotec.vitor.padilha.dissertacao.utils.FunctionsPointReader;
import br.uniriotec.vitor.padilha.dissertacao.utils.NumberUtils;
import br.uniriotec.vitor.padilha.dissertacao.view.IFunctionPointView;
import br.uniriotec.vitor.padilha.dissertacao.view.WebFunctionPointView;

@SuppressWarnings("unused")
public class MainAnalysisProgramMono
{
	public static final String RESULT_DIRECTORY = FunctionsPointDetailsListener.getResultDir().replace("\\", "/");

	public static final String BASE_DIRECTORY = FunctionsPointDetailsListener.getResultDir();
	
	public static final String INSTANCE_DIRECTORY = FunctionsPointDetailsListener.getInstanceDir();
	
	private static final String DIRECTORY_NSGAII = BASE_DIRECTORY + Algorithms.NSGAII.name()+ "\\";
	private static final String DIRECTORY_SPEA2 = BASE_DIRECTORY + Algorithms.SPEA2.name()+ "\\";
	private static final String DIRECTORY_MOCELL = BASE_DIRECTORY + Algorithms.MOCELL.name()+"\\";
	private static final Integer SOLUTION_SIZE = 39;
	private static final Integer CYCLES = 30;
	private static final Map<String, FunctionPointSystem> functionPointSystemReferences = new HashMap<String, FunctionPointSystem>();
	
	private static final Integer EXECUTION = 1;
//	private void saveGenerationFile(String filename, double[][] result, int dataCount, int generationCount, int cycleCount) throws Exception
//	{
//		PrintWriter out = new PrintWriter(new FileWriter(filename));
//		
//		for (int generation = 0; generation < generationCount; generation++)
//		{
//			for (int data = 0; data < dataCount; data++)
//				for (int cycle = 0; cycle < cycleCount; cycle++)
//					out.print(result[cycle][generation * dataCount + data] + "\t");
//			
//			out.println();
//		}
//		
//		out.close();
//	}

//	private void publishEvolutionQualityIndicators(String name, ParetoFront bestFrontier) throws Exception
//	{
//		double[][] nsgaII = new ExperimentGenerationFileReader().execute(DIRECTORY_NSGAII, name, 3, 200, 30, 5, 3, bestFrontier);
//		saveGenerationFile(RESULT_DIRECTORY.replace('/', '\\') + "eca_" + name + ".txt", nsgaII, 3, 200, 30);
//		
//		double[][] spea2 = new ExperimentGenerationFileReader().execute(DIRECTORY_SPEA2, name, 3, 200, 30, 5, 3, bestFrontier);
//		saveGenerationFile(RESULT_DIRECTORY.replace('/', '\\') + "evm_" + name + ".txt", spea2, 3, 200, 30);
//		
//		double[][] mocell = new ExperimentGenerationFileReader().execute(DIRECTORY_MOCELL, name, 3, 200, 30, 4, -1, bestFrontier);
//		saveGenerationFile(RESULT_DIRECTORY.replace('/', '\\') + "seca_" + name + ".txt", mocell, 3, 200, 30);
//	}
	private void saveGenerationFile(ExperimentalData2 experimentalData, Algorithms algorithm, String instanceName, Integer executionNumber) throws Exception
	{
		PrintWriter out = new PrintWriter(new FileWriter(BASE_DIRECTORY + instanceName + "\\"+ algorithm.name()+ "\\"+executionNumber+"\\qualityIndicators_"+algorithm.name()+".txt"));
		
		for (CycleInfos cycleInfo : experimentalData.getData()) {
			for (String group : cycleInfo.getInfoValue().keySet()) {
				Double[] value = cycleInfo.getInfoValue().get(group);
				out.print(value[0] + "\t");
			}
			out.println();
		}
		out.println();
		
		
		out.close();
	}
	
	private Double[] transform(double[] init) {
		Double[] return1 = new Double[init.length];
		for(int i=0;i<init.length;i++) {
			if(init[i]<0)
					return1[i] = new Double(-init[i]);
				else
					return1[i] = new Double(init[i]);
		}
		return return1;
	}
	
	private ExperimentalData2 publishHtmlSolution(MonoExperimentInstance resInstance, Algorithms algorithm, NewInstance instance, Integer executionNumber, Long maxFP, boolean method) throws Exception
	{
			String approach = "NRP-APF";
			if(!method)
				approach = "NRP-CLS";
			FunctionPointCalculator functionPointCalculator = new FunctionPointCalculator();
			ExperimentalData2 return1 = new ExperimentalData2("data", algorithm.name(), instance.getInstanceName(), executionNumber, maxFP,approach);
			for (int i=0;i<resInstance.getCycleCount();i++) {
				Binary binary = new Binary(resInstance.getCycleIndex(i).getSolution().length);
				for (int a=0; a < resInstance.getCycleIndex(i).getSolution().length;a++) {
					binary.bits_.set(a, (resInstance.getCycleIndex(i).getSolution()[a]==1)?true:false);
				}
				FunctionPointSystem functionPointSystem = FunctionPointFactory.getFunctionPointSystemConstructiveWay(binary, functionPointSystemReferences.get(instance.getInstanceName()), method);
				String dirResultados = instance.getInstanceDir() + "\\"+ algorithm.name()+ "\\"+executionNumber+"\\resultados";
				FunctionsPointDetailsListener.criarDiretorio(dirResultados);
				dirResultados = dirResultados+"\\"+maxFP;
				FunctionsPointDetailsListener.criarDiretorio(dirResultados);
				IFunctionPointView functionPointView = new WebFunctionPointView(i, functionPointSystem,BASE_DIRECTORY+dirResultados+"\\resultado-"+method+"-"+(i));
				Set<IFunctionPointView> views = new HashSet<IFunctionPointView>();
				views.add(functionPointView);
				functionPointCalculator.calculate(functionPointSystem,0,views);
				functionPointCalculator.calculateUserSatisfaction(functionPointSystem);
				functionPointView.render();				
			}
			return return1;
	}
	
	private ExperimentalData2 publishEvolutionQualityIndicators(MonoExperimentInstance resInstance, Algorithms algorithm, NewInstance instance, Integer executionNumber, Long maxFP, boolean method) throws Exception
	{
			String approach = "NRP-APF";
			if(!method)
				approach = "NRP-CLS";
			ExperimentalData2 return1 = new ExperimentalData2("data", algorithm.name(), instance.getInstanceName(), executionNumber, maxFP,approach);
			for (int i=0;i<resInstance.getCycleCount();i++) {
				BigDecimal tempo = (new BigDecimal(resInstance.getCycleIndex(i).getExecutionTime()/1000));
				tempo.setScale(2, RoundingMode.HALF_UP);
				Map<String,Double[]> dataInfo = new HashMap<String, Double[]>();
				if(resInstance.getCycleIndex(i).getObjective()>0)
					dataInfo.put("satisfacao", new Double[]{ 0D });
				else
					dataInfo.put("satisfacao", new Double[]{ -resInstance.getCycleIndex(i).getObjective() });
				dataInfo.put("time", new Double[]{ tempo.doubleValue() });
				return1.addData(i, dataInfo);
			}
			saveGenerationFile(return1, algorithm,  instance.getInstanceDir(),  executionNumber);
			return return1;
	}
	private void saveBestFront(String name, String configuration, ParetoFront front, String instanceName) throws Exception
	{
		String filename = RESULT_DIRECTORY.replace('/', '\\') + instanceName+ "\\"+ configuration + "_" + name + ".txt";
		/*front.removeObjective(3);
		front.removeObjective(2);*/
		
		PrintWriter out = new PrintWriter(new FileWriter(filename));
		out.println("cohe\tcoup");
		
		for (int i = 0; i < front.getVertexCount(); i++)
			out.println(-front.getVertex(i, 0) + "\t" + front.getVertex(i, 1));
		
		out.close();
	}
	
	//private MultiExperimentResult loadInstance(List<ExperimentalData> ecaData, Algorithms algorithm, int solutionSize) throws Exception
	private Map<Boolean,Map<Long,MonoExperimentResult>> loadInstance(Algorithms algorithm, int solutionSize, String instanceName, Integer executionNumber, Integer totalFP) throws Exception
	{
		System.out.println("Processando '" + algorithm.name() + "("+instanceName+", execução = "+executionNumber+")' ...");
		File dir = new File(BASE_DIRECTORY + "\\"+instanceName+ "\\" + algorithm + "\\"+executionNumber+"\\");
		Map<Boolean,Map<Long,MonoExperimentResult>> return1 = new HashMap<Boolean, Map<Long,MonoExperimentResult>>();
		for(File file:dir.listFiles()){
			if(file.isFile() && file.getName().contains("output_"+algorithm.name())) {
				try{
					MonoExperimentResult result = (new MonoExperimentFileReader()).execute(file.getAbsolutePath(), 1, CYCLES, solutionSize);
					String[] names = file.getName().split("-");
					Boolean method = Boolean.valueOf(names[1]);
					Double maxFP = Double.valueOf(names[2].split("\\.")[0]);
					if(!return1.containsKey(method)) {
						return1.put(method, new HashMap<Long, MonoExperimentResult>());
					}
					
					return1.get(method).put(NumberUtils.formatNumber((maxFP/totalFP)*10, 0).longValue()*10, result);
				}
				catch (Exception e) {
					throw new Exception(file.getAbsolutePath(), e);
				}

			}
		}
		return return1;
		
	}
	private ParetoFront getBestFront(ParetoFront ... paretosFront) {
		ParetoFront bestFront = null;
		for (int i =0; i<paretosFront.length;i++) {
			if(bestFront==null)
				bestFront = paretosFront[i].clone();
			else
				bestFront.merge(paretosFront[i]);
		}
		return bestFront;
	}
	private List<ParetoFront> getParetosFrontsFromExperimentResult(MultiExperimentResult experimentResult, int instanceNumber){
		List<ParetoFront> paretoFronts = new ArrayList<ParetoFront>();
		for(int i=0;i<experimentResult.getInstanceIndex(instanceNumber).getCycleCount();i++){
			paretoFronts.add(experimentResult.getInstanceIndex(instanceNumber).getCycleIndex(i).getFront());
		}
		
		return paretoFronts;
	}
	private NewInstance getInstance(String name){
		for(int i=0;i<MainAnalysisProgram.INSTANCES.length;i++) {
			
		}
		return null;
	}
	public static void main(String[] args) throws Exception
	{
		MainAnalysisProgramMono mp = new MainAnalysisProgramMono();
//		List<ExperimentalData> nSGAIIData = new ArrayList<ExperimentalData>();
//		List<ExperimentalData> spea2Data = new ArrayList<ExperimentalData>();
		
		File dir = new File(BASE_DIRECTORY);
		Map<Algorithms,Map<NewInstance,Map<Integer,Map<Boolean,Map<Long,MonoExperimentResult>>>>> results = new HashMap<Algorithms, Map<NewInstance,Map<Integer,Map<Boolean,Map<Long,MonoExperimentResult>>>>>();
		Script2 script = new Script2(RESULT_DIRECTORY + "script-mono-2.r");
		List<String> instancesNames = new ArrayList<String>();
		Set<String> algorithmsNames = new HashSet<String>();
		List<ExperimentalData2> datas = new ArrayList<ExperimentalData2>();
		List<ExperimentalData2> experimentalDataSet = new ArrayList<ExperimentalData2>();
		Map<String, SortedSet<Long>> maxFPByInstance = new HashMap<String, SortedSet<Long>>();
		Map<String, SortedSet<Long>> maxFPByAlgorithm = new HashMap<String, SortedSet<Long>>();
		Map<Integer,Map<Long,List<ExperimentalData2>>> experimentalDataSetInterno = new HashMap<Integer,Map<Long,List<ExperimentalData2>>>();
		if(dir.exists()) {
			for(NewInstance instance:MainAnalysisProgram.INSTANCES){
				
				FunctionsPointReader reader = new FunctionsPointReader(INSTANCE_DIRECTORY+instance.getInstanceDir().replace(" ", "")+"/functions-point.xml",INSTANCE_DIRECTORY+instance.getInstanceDir().replace(" ", "")+"/stakeholders-interest.xml");
				FunctionPointSystem functionPointSystem = reader.read();
				MainAnalysisProgramMono.functionPointSystemReferences.put(instance.getInstanceName(), functionPointSystem);
				ParetoFront bestFront = null;
				File dirInstance = new File(BASE_DIRECTORY+"\\"+instance.getInstanceDir());
				instancesNames.add(instance.getInstanceName());
				if(dirInstance.exists()) {
					for(File dirInstanceAlgorithm:dirInstance.listFiles()){
						if(dirInstanceAlgorithm.isDirectory()) {
							boolean searchAlgorithm = false;
							for(Algorithms algorithmInt : Algorithms.values()){
								if(algorithmInt.name().equals(dirInstanceAlgorithm.getName()))
									searchAlgorithm = true;
							}
							if(!searchAlgorithm)
								continue;
							
							Algorithms algorithm = Algorithms.valueOf(dirInstanceAlgorithm.getName());
							if(!algorithm.isMonoobjective() || !algorithm.equals(Algorithms.GENETIC)) {
								continue;
							}
							algorithmsNames.add(dirInstanceAlgorithm.getName());
							if(!results.containsKey(algorithm))
								results.put(algorithm, new HashMap<NewInstance, Map<Integer,Map<Boolean,Map<Long,MonoExperimentResult>>>>());
							
							
							if(!results.get(algorithm).containsKey(instance)) {
								results.get(algorithm).put(instance, new HashMap<Integer, Map<Boolean,Map<Long,MonoExperimentResult>>>());
							}
							for(File dirExecutionNumber:dirInstanceAlgorithm.listFiles()){
								
								Integer executionNumber = Integer.valueOf(dirExecutionNumber.getName());
								if(!executionNumber.equals(EXECUTION)){
									continue;
								}
								Map<Boolean,Map<Long,MonoExperimentResult>> resultsAlgorithm = mp.loadInstance(algorithm, instance.getTransactionsNumber(),instance.getInstanceDir(),executionNumber, instance.getTotalFP());
								results.get(algorithm).get(instance).put(executionNumber, resultsAlgorithm);
							}
						}
					}
			}
		}
//		DataSet dataSet = script.writeDataFile(RESULT_DIRECTORY+"datas-mono.data", datas, "datas");
//		script.loadFile(dataSet, RESULT_DIRECTORY+"datas-mono.data");
//		script.drawDispersionPlot(dataSet, "Pontos por Função", "Safisfação", "", instancesNames, algorithmsNames, "pf", "satisfacao", RESULT_DIRECTORY+"grafico-dispersao.jpg");
		
		for (Algorithms algorithm : results.keySet()) {
			List<ExperimentalData2> experimentalDataSetForAlgorithm = new ArrayList<ExperimentalData2>();
			
			for (NewInstance instance : results.get(algorithm).keySet()) {
				for (Integer executionNumber :  results.get(algorithm).get(instance).keySet()) {	
					if(!experimentalDataSetInterno.containsKey(executionNumber))
					{
						experimentalDataSetInterno.put(executionNumber, new HashMap<Long, List<ExperimentalData2>>());
							}
					for (Boolean method : results.get(algorithm).get(instance).get(executionNumber).keySet()) {
						SortedSet<Long> maxFPs = new TreeSet<Long>();
						maxFPs.addAll(results.get(algorithm).get(instance).get(executionNumber).get(method).keySet());
						maxFPByInstance.put(instance.getInstanceName(), maxFPs);
						maxFPByAlgorithm.put(algorithm.name(), maxFPs);
						
						for (Long maxFP : results.get(algorithm).get(instance).get(executionNumber).get(method).keySet()) {
							ExperimentalData2 experimentalData = mp.publishEvolutionQualityIndicators(results.get(algorithm).get(instance).get(executionNumber).get(method).get(maxFP).getInstanceIndex(0), algorithm, instance, executionNumber,maxFP,method);
							mp.publishHtmlSolution(results.get(algorithm).get(instance).get(executionNumber).get(method).get(maxFP).getInstanceIndex(0), algorithm, instance, executionNumber,maxFP,method);
							experimentalDataSet.add(experimentalData);
							experimentalDataSetForAlgorithm.add(experimentalData);
							
							if(!experimentalDataSetInterno.get(executionNumber).containsKey(maxFP))
							{
								experimentalDataSetInterno.get(executionNumber).put(maxFP, new ArrayList<ExperimentalData2>());
							}
							experimentalDataSetInterno.get(executionNumber).get(maxFP).add(experimentalData);
							if(true) {
								String dirName = RESULT_DIRECTORY + instance.getInstanceDir() + "/"+ algorithm.name() + "/"+ executionNumber + "/";
								String archiveName = dirName + algorithm.name()+"-"+instance.getInstanceDir()+"-"+executionNumber+"-"+method+"-"+maxFP+".data";
								
								DataSet dataSet1= script.writeDataFile(archiveName, experimentalData, method.toString());
								script.loadFile(dataSet1, archiveName);
								script.tableSummary(experimentalData, "satisfacao", method.toString());
								script.tableSummary(experimentalData, "time", method.toString());
								script.tableKolmogorovSmirnov(experimentalData, "satisfacao", method.toString());
								script.tableKolmogorovSmirnov(experimentalData, "time", method.toString());
								//script.drawBoxPlot(experimentalData, "time", algorithm.name(), "Tempo de Execução - "+instanceName+" - "+algorithm.name());
								
								script.sendCommand("summary <- data.frame(meanTime=mean_"+method.toString()+"_time, sdTime=sd_"+method.toString()+"_time, minTime=min_"+method.toString()+"_time, maxTime=max_"+method.toString()+"_time, ksTime=ks_pvalue_"+method.toString()+"_time, " +
										"meanSatisfacao=mean_"+method.toString()+"_satisfacao, sdSatisfacao=sd_"+method.toString()+"_satisfacao,  minSatisfacao=min_"+method.toString()+"_satisfacao, maxSatisfacao=max_"+method.toString()+"_satisfacao, ksSatisfacao=ks_pvalue_"+method.toString()+"_satisfacao)");
								script.sendCommand("write.csv2(summary, quote=FALSE, file=\""+dirName+"/summary-"+algorithm.name()+"-"+instance.getInstanceDir()+"-"+executionNumber+"-"+method+"-"+maxFP+".csv\")");
								
								
								for (Integer executionNumberComp :  results.get(algorithm).get(instance).keySet()) {
											ExperimentalData2 experimentalDataComp = mp.publishEvolutionQualityIndicators(results.get(algorithm).get(instance).get(executionNumberComp).get(!method).get(maxFP).getInstanceIndex(0), algorithm, instance, executionNumberComp,maxFP,!method);
											String dirNameComp = RESULT_DIRECTORY + instance.getInstanceDir() + "/"+ algorithm.name() + "/"+ executionNumberComp + "/";
											String archiveNameComp = dirNameComp + algorithm.name()+"-"+instance.getInstanceDir()+"-"+executionNumberComp+"-"+(!method.booleanValue())+"-"+maxFP+".data";
											
											DataSet dataSet2 = script.writeDataFile(archiveNameComp, experimentalDataComp, Boolean.valueOf((!method.booleanValue())).toString());
											script.loadFile(dataSet2, archiveNameComp);
											script.tableMannWhitney(experimentalData, "satisfacao", method.toString(), Boolean.valueOf((!method.booleanValue())).toString());
											script.tableMannWhitney(experimentalData, "time", method.toString(), Boolean.valueOf((!method.booleanValue())).toString());
											
											script.tableEffectSize(experimentalData, "satisfacao", method.toString(), Boolean.valueOf((!method.booleanValue())).toString());
											script.tableEffectSize(experimentalData, "time", method.toString(), Boolean.valueOf((!method.booleanValue())).toString());
											
											String dirComp = RESULT_DIRECTORY+instance.getInstanceDir()+"/metodos/"+method+"["+executionNumber+"]"+Boolean.valueOf((!method.booleanValue())).toString()+"["+executionNumberComp+"]";
											FunctionsPointDetailsListener.criarDiretorio(instance.getInstanceDir()+"/metodos/"+method+"["+executionNumber+"]"+Boolean.valueOf((!method.booleanValue())).toString()+"["+executionNumberComp+"]");
											script.sendCommand("\npvalues <- data.frame(timee=pvalue_"+Boolean.valueOf((method.booleanValue())).toString()+"_"+Boolean.valueOf((!method.booleanValue())).toString()+"_time, satisfacao=pvalue_"+Boolean.valueOf((method.booleanValue())).toString()+"_"+Boolean.valueOf((!method.booleanValue())).toString()+"_satisfacao)");
											script.sendCommand("write.csv2(pvalues, quote=FALSE, file=\""+dirComp+"/pvalues-"+maxFP+".csv\")");
											script.sendCommand("\neffectsizes <- data.frame( timee=effectsize1_"+Boolean.valueOf((method.booleanValue())).toString()+"_"+Boolean.valueOf((!method.booleanValue())).toString()+"_time, satisfacao=effectsize1_"+Boolean.valueOf((method.booleanValue())).toString()+"_"+Boolean.valueOf((!method.booleanValue())).toString()+"_satisfacao)");
											script.sendCommand("write.csv2(effectsizes, quote=FALSE, file=\""+dirComp+"/effectsizes-"+maxFP+".csv\")");
										}
									}
								}
						}
					}
					
				}
				DataSet dataSetAlgorithm = script.writeDataFile(RESULT_DIRECTORY+"datas-mono-"+algorithm.name()+"-2.data", experimentalDataSetForAlgorithm, "datasMono");
				script.loadFile(dataSetAlgorithm, RESULT_DIRECTORY+"datas-mono-"+algorithm.name()+"-2.data");
			
				Set<String> fields = new HashSet<String>();
				fields.add("NRP-APF");
				fields.add("NRP-CLS");
				script.drawLinePlot(dataSetAlgorithm, "Tempo de Execução", "time","Instance",instancesNames, fields, maxFPByInstance, RESULT_DIRECTORY+"maxFP-tempo-"+algorithm.name()+"-2.jpg","Esforço Disponível (%FP)","Satisfação","Approach",EXECUTION);
				script.drawLinePlot(dataSetAlgorithm, "Satisfação", "satisfacao","Instance",instancesNames, fields, maxFPByInstance, RESULT_DIRECTORY+"maxFP-satisfacao-"+algorithm.name()+"-2.jpg","Esforço Disponível (%FP)","Satisfação","Approach",EXECUTION);
		
			}
		
		}
		for (Integer executionNumber :  experimentalDataSetInterno.keySet()) {	
			for (Long maxFP :experimentalDataSetInterno.get(executionNumber).keySet()) {
					DataSet dataSet3 = script.writeDataFile(RESULT_DIRECTORY+"datas-mono-interno"+maxFP+"-2.data", experimentalDataSetInterno.get(executionNumber).get(maxFP), "datasMonoInterno"+maxFP);
					script.loadFile(dataSet3, RESULT_DIRECTORY+"datas-mono-interno"+maxFP+"-2.data");
					//script.drawBoxPlot(dataSet3, "Tempo de Execução", "time",instancesNames, RESULT_DIRECTORY+"boxplot-time-mono-"+maxFP+"("+executionNumber+").jpg","Instância","Tempo de Execução (seg)","Approach");
					script.drawBoxPlot(dataSet3, "Satisfação", "satisfacao",instancesNames, RESULT_DIRECTORY+"boxplot-satisfacao-"+maxFP+"("+executionNumber+").jpg","Instância","Satisfação","Approach",EXECUTION);
			}			
				
		}
		
//		DataSet dataSet2 = script.writeDataFile(RESULT_DIRECTORY+"datas-mono.data", experimentalDataSet, "datasMono");
//		script.loadFile(dataSet2, RESULT_DIRECTORY+"datas-mono.data");
//		script.drawBoxPlot(dataSet2, "Tempo de Execução", "time",instancesNames, RESULT_DIRECTORY+"boxplot-time-mono.jpg","Instância","Tempo de Execução (seg)","Approach");
//		script.drawBoxPlot(dataSet2, "Error Ratio", "errt",instancesNames, RESULT_DIRECTORY+"boxplot-errt.jpg","Instância","Error Ratio");
//		script.drawBoxPlot(dataSet2, "Generational Distance", "gdst", instancesNames, RESULT_DIRECTORY+"boxplot-gdst.jpg","Instância","Generational Distance");
//		script.drawBoxPlot(dataSet2, "Spread", "sprd",instancesNames, RESULT_DIRECTORY+"boxplot-sprd.jpg","Instância","Spread");
//		script.drawBoxPlot(dataSet2, "Satisfação", "satisfacao",instancesNames, RESULT_DIRECTORY+"boxplot-satisfacao.jpg","Instância","Satisfação","Approach");
		
		List<ExperimentalData2> experimentalData2s = new ArrayList<ExperimentalData2>();
		for (ExperimentalData2 experimentalData2 : experimentalDataSet) {
			experimentalData2.setApproach(experimentalData2.getApproach()+"_"+experimentalData2.getMaxEvaluations());
		}
		
		DataSet dataSet3 = script.writeDataFile(RESULT_DIRECTORY+"datas-mono-concat-2.data", experimentalDataSet, "datasMonoConcat");
		Set<String> fields = new HashSet<String>();
		fields.add("NRP-APF");
		fields.add("NRP-CLS");
		List<String> algorithmsNames2 = new ArrayList<String>();
		for (String string : algorithmsNames) {
			algorithmsNames2.add(string);
		}
		script.drawLinePlot(dataSet3, "Satisfação", "satisfacao","Algorithm",algorithmsNames2, fields, maxFPByAlgorithm, RESULT_DIRECTORY+"maxFP-satisfacao-gestao-pessoas.jpg","Esforço Disponível (%FP)","Satisfação","Approach",EXECUTION);
		
		script.loadFile(dataSet3, RESULT_DIRECTORY+"datas-mono-concat-2.data");
		script.drawBoxPlot(dataSet3, "Satisfação", "satisfacao",instancesNames, RESULT_DIRECTORY+"boxplot-satisfacao-2.jpg","Instância","Satisfação","Approach",EXECUTION);
		script.drawBoxPlot(dataSet3, "Tempo", "time",instancesNames, RESULT_DIRECTORY+"boxplot-time-2.jpg","Instância","Satisfação","Approach",EXECUTION);
		
		script.close();
	}
}
