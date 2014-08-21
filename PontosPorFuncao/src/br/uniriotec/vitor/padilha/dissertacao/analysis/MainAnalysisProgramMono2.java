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
import br.uniriotec.vitor.padilha.dissertacao.calculator.FunctionPointCalculator;
import br.uniriotec.vitor.padilha.dissertacao.listener.mono.FunctionsPointDetailsListener;
import br.uniriotec.vitor.padilha.dissertacao.utils.NumberUtils;

@SuppressWarnings("unused")
public class MainAnalysisProgramMono2
{
	public static final String RESULT_DIRECTORY = FunctionsPointDetailsListener.getResultDir().replace("\\", "/");

	public static final String BASE_DIRECTORY = FunctionsPointDetailsListener.getResultDir();
	private static final String DIRECTORY_NSGAII = BASE_DIRECTORY + Algorithms.NSGAII.name()+ "\\";
	private static final String DIRECTORY_SPEA2 = BASE_DIRECTORY + Algorithms.SPEA2.name()+ "\\";
	private static final String DIRECTORY_MOCELL = BASE_DIRECTORY + Algorithms.MOCELL.name()+"\\";
	private static final Integer SOLUTION_SIZE = 39;
	private static final Integer CYCLES = 50;
	//private static final NewInstance[] INSTANCES = new NewInstance[]{new NewInstance("Gestao De Pessoas",65),new NewInstance("Academico",39)};
	
	
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
	
	private ExperimentalData2 publishEvolutionQualityIndicators(MonoExperimentInstance resInstance, Algorithms algorithm, NewInstance instance, Integer executionNumber, Long maxEvaluations, boolean method, double crossover, double mutation, double population) throws Exception
	{
			String approach = "NRP_APF";
			if(!method)
				approach = "NRP_Clássico";
			ExperimentalData2 return1 = new ExperimentalData2("data", algorithm.name(), instance.getInstanceName(), executionNumber, maxEvaluations,approach);
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
			saveGenerationFile(return1, algorithm,  instance.getInstanceName(),  executionNumber);
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
	private Map<Boolean,Map<Long,Map<Double,Map<Double,Map<Double,MonoExperimentResult>>>>> loadInstance(Algorithms algorithm, int solutionSize, String instanceName, Integer executionNumber, Integer totalFP) throws Exception
	{
		System.out.println("Processando '" + algorithm.name() + "("+instanceName+", execução = "+executionNumber+")' ...");
		File dir = new File(BASE_DIRECTORY + "\\"+instanceName+ "\\" + algorithm + "\\"+executionNumber+"\\");
		Map<Boolean,Map<Long,Map<Double,Map<Double,Map<Double,MonoExperimentResult>>>>> return1 = new HashMap<Boolean,Map<Long,Map<Double,Map<Double,Map<Double,MonoExperimentResult>>>>>();
		for(File file:dir.listFiles()){
			if(file.isFile() && file.getName().contains("output_"+algorithm.name())) {
				try{
					MonoExperimentResult result = (new MonoExperimentFileReader()).execute(file.getAbsolutePath(), 1, CYCLES, solutionSize);
					String[] names = file.getName().split("-");
					Boolean method = Boolean.valueOf(names[2]);
					Double maxFP = Double.valueOf(names[3].split("\\.")[0]);
					Double crossover = Double.valueOf(names[4]);
					Double mutation = Double.valueOf(names[5]);
					Double multiplicadorPopulacao = Double.valueOf(names[6].split("\\.")[0]);
					if(!return1.containsKey(method)) {
						return1.put(method, new HashMap<Long, Map<Double,Map<Double,Map<Double,MonoExperimentResult>>>>());
					}
					Long functionsPointMax = NumberUtils.formatNumber((maxFP/totalFP)*10, 0).longValue()*10;
					if(!return1.get(method).containsKey(functionsPointMax)) {
						return1.get(method).put(functionsPointMax, new HashMap<Double, Map<Double,Map<Double,MonoExperimentResult>>>());
					}
					
					if(!return1.get(method).get(functionsPointMax).containsKey(crossover)) {
						return1.get(method).get(functionsPointMax).put(crossover, new HashMap<Double, Map<Double,MonoExperimentResult>>());
					}
					
					if(!return1.get(method).get(functionsPointMax).get(crossover).containsKey(mutation)) {
						return1.get(method).get(functionsPointMax).get(crossover).put(mutation, new HashMap<Double, MonoExperimentResult>());
					}
					
					return1.get(method).get(functionsPointMax).get(crossover).get(mutation).put(multiplicadorPopulacao, result);
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
		MainAnalysisProgramMono2 mp = new MainAnalysisProgramMono2();
//		List<ExperimentalData> nSGAIIData = new ArrayList<ExperimentalData>();
//		List<ExperimentalData> spea2Data = new ArrayList<ExperimentalData>();
		
		File dir = new File(BASE_DIRECTORY);
		Map<NewInstance,Map<Algorithms,Map<Integer,Map<Boolean,Map<Long,Map<Double,Map<Double,Map<Double,MonoExperimentResult>>>>>>>> results = new HashMap<NewInstance, Map<Algorithms,Map<Integer,Map<Boolean,Map<Long,Map<Double,Map<Double,Map<Double,MonoExperimentResult>>>>>>>>();
		Script2 script = new Script2(RESULT_DIRECTORY + "script-mono-completo.r");
		List<String> instancesNames = new ArrayList<String>();
		Set<String> algorithmsNames = new HashSet<String>();
		List<ExperimentalData2> datas = new ArrayList<ExperimentalData2>();
		List<ExperimentalData2> experimentalDataSet = new ArrayList<ExperimentalData2>();
		Map<String, SortedSet<Long>> maxFPByInstance = new HashMap<String, SortedSet<Long>>();
		Map<Integer,Map<Long,List<ExperimentalData2>>> experimentalDataSetInterno = new HashMap<Integer,Map<Long,List<ExperimentalData2>>>();
		
		if(dir.exists()) {
			for(NewInstance instance:MainAnalysisProgram.INSTANCES){
				ParetoFront bestFront = null;
				File dirInstance = new File(BASE_DIRECTORY+"\\"+instance.getInstanceName());
				instancesNames.add(instance.getInstanceName());
				if(dirInstance.exists()) {
					if(!results.containsKey(instance))
						results.put(instance, new HashMap<Algorithms, Map<Integer,Map<Boolean,Map<Long,Map<Double,Map<Double,Map<Double,MonoExperimentResult>>>>>>>());
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
							if(!algorithm.isMonoobjective()) {
								continue;
							}
							algorithmsNames.add(dirInstanceAlgorithm.getName());
							if(!results.get(instance).containsKey(algorithm)) {
								results.get(instance).put(algorithm, new HashMap<Integer, Map<Boolean,Map<Long,Map<Double,Map<Double,Map<Double,MonoExperimentResult>>>>>>());
							}
							for(File dirExecutionNumber:dirInstanceAlgorithm.listFiles()){
								
								Integer executionNumber = Integer.valueOf(dirExecutionNumber.getName());
								if(executionNumber.intValue()!=2) {
									
									continue;
								}
								Map<Boolean,Map<Long,Map<Double,Map<Double,Map<Double,MonoExperimentResult>>>>> resultsAlgorithm = mp.loadInstance(algorithm, instance.getTransactionsNumber(),instance.getInstanceName(),executionNumber, instance.getTotalFP());
								results.get(instance).get(algorithm).put(executionNumber, resultsAlgorithm);
								
							}
						}
					}
			}
		}
//		DataSet dataSet = script.writeDataFile(RESULT_DIRECTORY+"datas-mono.data", datas, "datas");
//		script.loadFile(dataSet, RESULT_DIRECTORY+"datas-mono.data");
//		script.drawDispersionPlot(dataSet, "Pontos por Função", "Safisfação", "", instancesNames, algorithmsNames, "pf", "satisfacao", RESULT_DIRECTORY+"grafico-dispersao.jpg");
		
		for (NewInstance instance : results.keySet()) {
			for (Algorithms algorithm : results.get(instance).keySet()) {
				
				for (Integer executionNumber :  results.get(instance).get(algorithm).keySet()) {	
					
					if(!experimentalDataSetInterno.containsKey(executionNumber))
					{
						experimentalDataSetInterno.put(executionNumber, new HashMap<Long, List<ExperimentalData2>>());
					}
					Set<String> listSummary = new HashSet<String>();
					
					for (Boolean method : results.get(instance).get(algorithm).get(executionNumber).keySet()) {
						SortedSet<Long> maxFPs = new TreeSet<Long>();
						maxFPs.addAll(results.get(instance).get(algorithm).get(executionNumber).get(method).keySet());
						maxFPByInstance.put(instance.getInstanceName(), maxFPs);
						
						for (Long maxFP : results.get(instance).get(algorithm).get(executionNumber).get(method).keySet()) {
							for (Double crossover : results.get(instance).get(algorithm).get(executionNumber).get(method).get(maxFP).keySet()) {
								for (Double mutation : results.get(instance).get(algorithm).get(executionNumber).get(method).get(maxFP).get(crossover).keySet()) {
									for (Double multiplicadorPopulacao : results.get(instance).get(algorithm).get(executionNumber).get(method).get(maxFP).get(crossover).get(mutation).keySet()) {
										ExperimentalData2 experimentalData = mp.publishEvolutionQualityIndicators(results.get(instance).get(algorithm).get(executionNumber).get(method).get(maxFP).get(crossover).get(mutation).get(multiplicadorPopulacao).getInstanceIndex(0), algorithm, instance, executionNumber,maxFP,method, crossover, mutation, multiplicadorPopulacao);
										experimentalDataSet.add(experimentalData);
										if(!experimentalDataSetInterno.get(executionNumber).containsKey(maxFP))
										{
											experimentalDataSetInterno.get(executionNumber).put(maxFP, new ArrayList<ExperimentalData2>());
										}
										experimentalDataSetInterno.get(executionNumber).get(maxFP).add(experimentalData);
										if(true) {
											String dirName = RESULT_DIRECTORY + instance.getInstanceName() + "/"+ algorithm.name() + "/"+ executionNumber + "/";
											String archiveName = dirName + algorithm.name()+"-"+instance.getInstanceName()+"-"+executionNumber+"-"+method+"-"+maxFP+".data";
											String alias = method.toString()+"_"+maxFP+"_"+crossover.toString().replace(".","")+"_"+mutation.toString().replace(".", "")+"_"+multiplicadorPopulacao.toString().replace(".", "") ;
											if(!listSummary.contains(alias))
												listSummary.add(alias);
											
											DataSet dataSet1= script.writeDataFile(archiveName, experimentalData, alias);
											script.loadFile(dataSet1, archiveName);
											
											script.tableSummary(experimentalData, "satisfacao", alias);
											script.tableSummary(experimentalData, "time", alias);
											
											//script.drawBoxPlot(experimentalData, "time", algorithm.name(), "Tempo de Execução - "+instanceName+" - "+algorithm.name());
											
											script.sendCommand("summary"+instance+alias+" <- data.frame(meanTime=mean_"+alias+"_time, sdTime=sd_"+alias+"_time, minTime=min_"+alias+"_time, maxTime=max_"+alias+"_time, " +
													"meanSatisfacao=mean_"+alias+"_satisfacao, sdSatisfacao=sd_"+alias+"_satisfacao,  minSatisfacao=min_"+alias+"_satisfacao, maxSatisfacao=max_"+alias+"_satisfacao)");
											script.sendCommand("write.csv2(summary, quote=FALSE, file=\""+dirName+"/summary-"+algorithm.name()+"-"+instance.getInstanceName()+"-"+executionNumber+"-"+method+"-"+maxFP+"-"+crossover+"-"+mutation+"-"+multiplicadorPopulacao+".csv\")");
//											
												if(false)
												for (Integer executionNumberComp :  results.get(instance).get(algorithm).keySet()) {
													ExperimentalData2 experimentalDataComp = mp.publishEvolutionQualityIndicators(results.get(instance).get(algorithm).get(executionNumberComp).get(!method).get(maxFP).get(crossover).get(mutation).get(multiplicadorPopulacao).getInstanceIndex(0), algorithm, instance, executionNumberComp,maxFP,!method, crossover,mutation,multiplicadorPopulacao);
													String dirNameComp = RESULT_DIRECTORY + instance.getInstanceName() + "/"+ algorithm.name() + "/"+ executionNumberComp + "/";
													String archiveNameComp = dirNameComp + algorithm.name()+"-"+instance.getInstanceName()+"-"+executionNumberComp+"-"+(!method.booleanValue())+"-"+maxFP+".data";
													
													DataSet dataSet2 = script.writeDataFile(archiveNameComp, experimentalDataComp, Boolean.valueOf((!method.booleanValue())).toString());
													script.loadFile(dataSet2, archiveNameComp);
													script.tableMannWhitney(experimentalData, "satisfacao", method.toString(), Boolean.valueOf((!method.booleanValue())).toString());
													script.tableMannWhitney(experimentalData, "time", method.toString(), Boolean.valueOf((!method.booleanValue())).toString());
													
													script.tableEffectSize(experimentalData, "satisfacao", method.toString(), Boolean.valueOf((!method.booleanValue())).toString());
													script.tableEffectSize(experimentalData, "time", method.toString(), Boolean.valueOf((!method.booleanValue())).toString());
													
													String dirComp = RESULT_DIRECTORY+instance.getInstanceName()+"/metodos/"+method+"["+executionNumber+"]"+Boolean.valueOf((!method.booleanValue())).toString()+"["+executionNumberComp+"]";
													FunctionsPointDetailsListener.criarDiretorio(instance.getInstanceName()+"/metodos/"+method+"["+executionNumber+"]"+Boolean.valueOf((!method.booleanValue())).toString()+"["+executionNumberComp+"]");
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
						}
						String summary = "summary <- data.frame(";
						int a=0;
						for (String alias : listSummary) {
							if(a!=0) {
								summary+=", ";
							}
//							summary+="mean"+alias+"Time=mean_"+alias+"_time, sd"+alias+"Time=sd_"+alias+"_time, min"+alias+"Time=min_"+alias+"_time, max"+alias+"Time=max_"+alias+"_time, " +
//								"mean"+alias+"Satisfacao=mean_"+alias+"_satisfacao, sd"+alias+"Satisfacao=sd_"+alias+"_satisfacao,  min"+alias+"Satisfacao=min_"+alias+"_satisfacao, max"+alias+"Satisfacao=max_"+alias+"_satisfacao";
							summary+=alias+" = summary"+instance+alias;
							if(a%20==0) {
								summary+="\n";
							}
							a++;
						}
						summary+=")";
						script.sendCommand(summary);
						script.sendCommand("write.csv2(summary, quote=FALSE, file=\""+RESULT_DIRECTORY+"/summary-completo-"+algorithm.name()+"-"+instance.getInstanceName()+"-"+executionNumber+".csv\")");
					
					}
					
				}
			}
		}
//		for (Integer executionNumber :  experimentalDataSetInterno.keySet()) {	
//			for (Long maxFP :experimentalDataSetInterno.get(executionNumber).keySet()) {
//					DataSet dataSet3 = script.writeDataFile(RESULT_DIRECTORY+"datas-mono-interno"+maxFP+".data", experimentalDataSetInterno.get(executionNumber).get(maxFP), "datasMonoInterno"+maxFP);
//					script.loadFile(dataSet3, RESULT_DIRECTORY+"datas-mono-interno"+maxFP+".data");
//					//script.drawBoxPlot(dataSet3, "Tempo de Execução", "time",instancesNames, RESULT_DIRECTORY+"boxplot-time-mono-"+maxFP+"("+executionNumber+").jpg","Instância","Tempo de Execução (seg)","Approach");
//					script.drawBoxPlot(dataSet3, "Satisfação", "satisfacao",instancesNames, RESULT_DIRECTORY+"boxplot-satisfacao-"+maxFP+"("+executionNumber+").jpg","Instância","Satisfação","Approach");
//			}			
//		}
//		DataSet dataSet2 = script.writeDataFile(RESULT_DIRECTORY+"datas-mono.data", experimentalDataSet, "datasMono");
//		script.loadFile(dataSet2, RESULT_DIRECTORY+"datas-mono.data");
//		
//		Set<String> fields = new HashSet<String>();
//		fields.add("NRP_APF");
//		fields.add("NRP_Clássico");
//		script.drawLinePlot(dataSet2, "Tempo de Execução", "time",instancesNames, fields, maxFPByInstance, RESULT_DIRECTORY+"maxFP-tempo.jpg","% do Pontos de Função Total da Instância","Satisfação","Approach");
//		script.drawLinePlot(dataSet2, "Satisfação", "satisfacao",instancesNames, fields, maxFPByInstance, RESULT_DIRECTORY+"maxFP-satisfacao.jpg","% do Pontos de Função Total da Instância","Satisfação","Approach");
//		List<ExperimentalData2> experimentalData2s = new ArrayList<ExperimentalData2>();
//		for (ExperimentalData2 experimentalData2 : experimentalDataSet) {
//			experimentalData2.setApproach(experimentalData2.getApproach()+"_"+experimentalData2.getMaxEvaluations());
//		}
//		DataSet dataSet3 = script.writeDataFile(RESULT_DIRECTORY+"datas-mono-concat.data", experimentalDataSet, "datasMonoConcat");
//		script.loadFile(dataSet3, RESULT_DIRECTORY+"datas-mono-concat.data");
//		script.drawBoxPlot(dataSet3, "Satisfação", "satisfacao",instancesNames, RESULT_DIRECTORY+"boxplot-satisfacao.jpg","Instância","Satisfação","Approach");
//		
		script.close();
	}
}
