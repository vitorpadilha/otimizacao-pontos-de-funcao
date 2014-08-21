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

import jmetal.qualityIndicator.GenerationalDistance;
import jmetal.qualityIndicator.Hypervolume;
import jmetal.qualityIndicator.Spread;
import unirio.experiments.multiobjective.analysis.model.MultiExperimentInstance;
import unirio.experiments.multiobjective.analysis.model.MultiExperimentResult;
import unirio.experiments.multiobjective.analysis.model.ParetoFront;
import unirio.experiments.multiobjective.analysis.reader.MultiExperimentFileReader;
import unirio.experiments.statistics.ExperimentalData2;
import unirio.experiments.statistics.ExperimentalData2.CycleInfos;
import unirio.experiments.statistics.Script2;
import unirio.experiments.statistics.Script2.DataSet;
import br.uniriotec.vitor.padilha.dissertacao.algorithm.Algorithms;
import br.uniriotec.vitor.padilha.dissertacao.listener.mono.FunctionsPointDetailsListener;
import br.uniriotec.vitor.padilha.dissertacao.marcio.GenerationFileReader;
import br.uniriotec.vitor.padilha.dissertacao.utils.NumberUtils;

@SuppressWarnings("unused")
public class MainAnalysisProgram
{
	public static final String RESULT_DIRECTORY = FunctionsPointDetailsListener.getResultDir().replace("\\", "/");

	public static final String BASE_DIRECTORY = FunctionsPointDetailsListener.getResultDir();
	private static final String DIRECTORY_NSGAII = BASE_DIRECTORY + Algorithms.NSGAII.name()+ "\\";
	private static final String DIRECTORY_SPEA2 = BASE_DIRECTORY + Algorithms.SPEA2.name()+ "\\";
	private static final String DIRECTORY_MOCELL = BASE_DIRECTORY + Algorithms.MOCELL.name()+"\\";
	private static final Integer SOLUTION_SIZE = 39;
	private static final Integer CYCLES = 50;
	//public static final NewInstance[] INSTANCES = new NewInstance[]{new NewInstance("ACAD","Academico",39,185,5275),new NewInstance("PSOA","Gestao De Pessoas",65,288, 10825),new NewInstance("PARM","Parametros",98, 441,22050),new NewInstance("BOLS","Bolsa De Valores",200, 1131, 61600)};
	public static final NewInstance[] INSTANCES = new NewInstance[]{new NewInstance("ACAD","Academico",39,185,5275),new NewInstance("ACAD2","Academico Alterado",39,169, 5275),new NewInstance("ACAD3","Academico Alterado 2",39,169, 5275),new NewInstance("ACAD4","Academico Alterado 3",39,171, 5275)};
	
	//public static final NewInstance[] INSTANCES = new NewInstance[]{new NewInstance("PSOA","Gestao De Pessoas",65,288, 10825)};
	
	
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
	private void saveGenerationFile(ExperimentalData2 experimentalData, Algorithms algorithm, NewInstance instanceName, Integer executionNumber) throws Exception
	{
		PrintWriter out = new PrintWriter(new FileWriter(BASE_DIRECTORY + instanceName.getInstanceDir() + "\\"+ algorithm.name()+ "\\"+executionNumber+"\\qualityIndicators_"+algorithm.name()+".txt"));
		
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
	private ExperimentalData2 publishFrontier(MultiExperimentResult multiExperimentResult, Algorithms algorithm, String instanceName, Integer executionNumber, Long evaluations) throws Exception
	{
			ExperimentalData2 return1 = new ExperimentalData2("data", algorithm.name(), instanceName, executionNumber, evaluations,"pf");
			for (int i=0;i<multiExperimentResult.getInstanceIndex(0).getCycleCount();i++) {
				Map<String,Double[]> dataInfo = new HashMap<String, Double[]>();
				ParetoFront paretoFront = multiExperimentResult.getInstanceIndex(0).getCycleIndex(i).getFront();
				double[] satistacoes = new double[paretoFront.getValues().length];
				double[] pfs = new double[paretoFront.getValues().length];
				for(int j=0;j<paretoFront.getValues().length;j++){
					satistacoes[j] = paretoFront.getValues()[j][0];
					pfs[j] = paretoFront.getValues()[j][1];
				}
				dataInfo.put("satisfacao", transform(satistacoes));
				dataInfo.put("pf", transform(pfs));
				return1.addData(i, dataInfo);
			}
//			double[] algorithmName = new double[paretoFront.getSolutionSize()];
//			for (int i=0;i<algorithmName.length;i++) {
//				algorithmName[i] = algorithm.
//			}
			return return1;
	}
	
	private ExperimentalData2 publishEvolutionQualityIndicators(MultiExperimentInstance resInstance, ParetoFront bestFront, Algorithms algorithm, NewInstance instance, Integer executionNumber, Long maxEvaluations, Long maxEvaluationsOfInstance) throws Exception
	{
			ExperimentalData2 return1 = new ExperimentalData2("data", algorithm.name(), instance.getInstanceName(), executionNumber, maxEvaluations,"pf");
			for (int i=0;i<resInstance.getCycleCount();i++) {
				ParetoFront front = resInstance.getCycleIndex(i).getFront();
				double[][] bestFrontValues = bestFront.getValues();
				double[][] currentFrontValues = front.getValues();
				BigDecimal tempo = (new BigDecimal(resInstance.getCycleIndex(i).getExecutionTime()/1000));
				tempo.setScale(2, RoundingMode.HALF_UP);
				Map<String,Double[]> dataInfo = new HashMap<String, Double[]>();
				dataInfo.put("errt", new Double[]{1.0 - ((double)bestFront.countCommonVertices(front)) / front.getVertexCount()});
				dataInfo.put("gdst", new Double[]{new GenerationalDistance().generationalDistance(currentFrontValues, bestFrontValues, bestFront.getObjectiveCount())});
				dataInfo.put("sprd", new Double[]{new Spread().spread(currentFrontValues, bestFrontValues, bestFront.getObjectiveCount())});
				dataInfo.put("hpvl", new Double[]{new Hypervolume().hypervolume(currentFrontValues, bestFrontValues, bestFront.getObjectiveCount())});
				dataInfo.put("time", new Double[]{ tempo.doubleValue()});
				return1.addData(i, dataInfo);
			}
			saveGenerationFile(return1, algorithm,  instance,  executionNumber);
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
	private Map<Long,MultiExperimentResult> loadInstance(Algorithms algorithm, int solutionSize, String instanceName, Integer executionNumber) throws Exception
	{
		System.out.println("Processando '" + algorithm.name() + "("+instanceName+", execução = "+executionNumber+")' ...");
		File dir = new File(BASE_DIRECTORY + "\\"+instanceName+ "\\" + algorithm + "\\"+executionNumber+"\\");
		Map<Long,MultiExperimentResult> return1 = new HashMap<Long, MultiExperimentResult>();
		int maxEvaluation = (2*solutionSize) * (2*solutionSize);
		for(File file:dir.listFiles()){
			if(file.isFile() && file.getName().contains("output_"+algorithm.name())) {
				MultiExperimentResult result = (new MultiExperimentFileReader()).execute(file.getAbsolutePath(), 1, CYCLES, 2, solutionSize);
				int tamanho = maxEvaluation;
				String[] names = file.getName().split("-");
				if(names.length>1)
					tamanho = Integer.valueOf(names[1].split("\\.")[0]);
				return1.put(NumberUtils.formatNumber((Double.valueOf(tamanho)/maxEvaluation)*100, 0).longValue(), result);
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
	private Long getMaxEvaluation(Set<Long> keys){
		Long max = 0L;
		for (Long key : keys) {
			if(key>max)
				max = key;
		}
		return max;
	}
	public static void main(String[] args) throws Exception
	{
		MainAnalysisProgram mp = new MainAnalysisProgram();
//		List<ExperimentalData> nSGAIIData = new ArrayList<ExperimentalData>();
//		List<ExperimentalData> spea2Data = new ArrayList<ExperimentalData>();
		
		File dir = new File(BASE_DIRECTORY);
		Map<String, ParetoFront> bestsFronts = new HashMap<String, ParetoFront>();
		Map<NewInstance,Map<Algorithms,Map<Integer,Map<Long,MultiExperimentResult>>>> results = new HashMap<NewInstance, Map<Algorithms,Map<Integer,Map<Long,MultiExperimentResult>>>>();
		Script2 script = new Script2(RESULT_DIRECTORY + "script.r");
		List<String> instancesNames = new ArrayList<String>();
		Set<String> algorithmsNames = new HashSet<String>();
		List<ExperimentalData2> datas = new ArrayList<ExperimentalData2>();
		if(dir.exists()) {
			for(NewInstance instance:INSTANCES){
				ParetoFront bestFront = null;
				File dirInstance = new File(BASE_DIRECTORY+"\\"+instance.getInstanceDir());
				instancesNames.add(instance.getInstanceName());
				if(dirInstance.exists()) {
					if(!results.containsKey(instance))
						results.put(instance, new HashMap<Algorithms, Map<Integer,Map<Long,MultiExperimentResult>>>());
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
							if(algorithm.isMonoobjective()) {
								continue;
							}
							algorithmsNames.add(dirInstanceAlgorithm.getName());
							if(!results.get(instance).containsKey(algorithm)) {
								results.get(instance).put(algorithm, new HashMap<Integer, Map<Long,MultiExperimentResult>>());
							}
							for(File dirExecutionNumber:dirInstanceAlgorithm.listFiles()){
								Integer executionNumber = Integer.valueOf(dirExecutionNumber.getName());
								if(!executionNumber.equals(1)){
									continue;
								}
								Map<Long,MultiExperimentResult> resultsAlgorithm = mp.loadInstance(algorithm, instance.getTransactionsNumber(),instance.getInstanceDir(),executionNumber);
								results.get(instance).get(algorithm).put(executionNumber, resultsAlgorithm);
								for (Long key : resultsAlgorithm.keySet()) {
									if(bestFront==null)
										bestFront = resultsAlgorithm.get(key).getInstanceIndex(0).getBestFront().clone();
									else
										bestFront = mp.getBestFront(bestFront,resultsAlgorithm.get(key).getInstanceIndex(0).getBestFront().clone());
									datas.add(mp.publishFrontier(resultsAlgorithm.get(key), algorithm, instance.getInstanceName(), executionNumber, key));
									
								}
							}
						}
					}
				}
				bestsFronts.put(instance.getInstanceName(), bestFront);
			}
		}
		DataSet dataSet = script.writeDataFile(RESULT_DIRECTORY+"datas.data", datas, "datas");
		script.loadFile(dataSet, RESULT_DIRECTORY+"datas.data");
//		script.drawDispersionPlot(dataSet, "Pontos por Função", "Safisfação", "", instancesNames, algorithmsNames, "pf", "satisfacao", RESULT_DIRECTORY+"grafico-dispersao.jpg");
		List<ExperimentalData2> experimentalDataSet = new ArrayList<ExperimentalData2>();
		Map<String, SortedSet<Long>> evaluationsByInstance = new HashMap<String, SortedSet<Long>>();
		//for (String instanceName : results.keySet()) {
		for (NewInstance instance : results.keySet()) {
			ParetoFront bestFront = bestsFronts.get(instance.getInstanceName());
			for (Algorithms algorithm : results.get(instance).keySet()) {
				
				for (Integer executionNumber :  results.get(instance).get(algorithm).keySet()) {	
					Long maxEvaluations = mp.getMaxEvaluation(results.get(instance).get(algorithm).get(executionNumber).keySet());
					SortedSet<Long> instances = new TreeSet<Long>();
					instances.addAll(results.get(instance).get(algorithm).get(executionNumber).keySet());
					evaluationsByInstance.put(instance.getInstanceName(), instances);
					Long maxEvaluationsOfInstance = mp.getMaxEvaluation(results.get(instance).get(algorithm).get(executionNumber).keySet());
					for (Long evaluations : results.get(instance).get(algorithm).get(executionNumber).keySet()) {
						ExperimentalData2 experimentalData = mp.publishEvolutionQualityIndicators(results.get(instance).get(algorithm).get(executionNumber).get(evaluations).getInstanceIndex(0), bestFront, algorithm, instance, executionNumber,evaluations,maxEvaluationsOfInstance);
						experimentalDataSet.add(experimentalData);
						if(evaluations.equals(maxEvaluations) && true) {
							String dirName = RESULT_DIRECTORY + instance.getInstanceDir() + "/"+ algorithm.name() + "/"+ executionNumber + "/";
							String archiveName = dirName + algorithm.name()+"-"+instance.getInstanceDir()+"-"+executionNumber+"-"+evaluations+".data";
							
							DataSet dataSet1= script.writeDataFile(archiveName, experimentalData, algorithm.name());
							script.loadFile(dataSet1, archiveName);
							
							script.tableSummary(experimentalData, "errt", algorithm.name());
							script.tableSummary(experimentalData, "gdst", algorithm.name());
							script.tableSummary(experimentalData, "sprd", algorithm.name());
							script.tableSummary(experimentalData, "hpvl", algorithm.name());
							script.tableSummary(experimentalData, "time", algorithm.name());
							
							script.tableKolmogorovSmirnov(experimentalData, "errt", algorithm.toString());
							script.tableKolmogorovSmirnov(experimentalData, "gdst", algorithm.toString());
							script.tableKolmogorovSmirnov(experimentalData, "sprd", algorithm.toString());
							script.tableKolmogorovSmirnov(experimentalData, "hpvl", algorithm.toString());
							script.tableKolmogorovSmirnov(experimentalData, "time", algorithm.toString());
							
							//script.drawBoxPlot(experimentalData, "time", algorithm.name(), "Tempo de Execução - "+instance.getInstanceName()+" - "+algorithm.name());
							
							script.sendCommand("summary <- data.frame(meanTime=mean_"+algorithm.name()+"_time, sdTime=sd_"+algorithm.name()+"_time, minTime=min_"+algorithm.name()+"_time, maxTime=max_"+algorithm.name()+"_time, ksTime=ks_pvalue_"+algorithm.toString()+"_time, " +
									"meanErrt=mean_"+algorithm.name()+"_errt, sdErrt=sd_"+algorithm.name()+"_errt,  minErrt=min_"+algorithm.name()+"_errt, maxErrt=max_"+algorithm.name()+"_errt, ksErrt=ks_pvalue_"+algorithm.toString()+"_errt, " +
									"meanGdst=mean_"+algorithm.name()+"_gdst, sdGdst=sd_"+algorithm.name()+"_gdst, minGdst=min_"+algorithm.name()+"_gdst, maxGdst=max_"+algorithm.name()+"_gdst, ksGdst=ks_pvalue_"+algorithm.toString()+"_gdst, " +
									"meanSpread=mean_"+algorithm.name()+"_sprd, sdSpread=sd_"+algorithm.name()+"_sprd, minSpread=min_"+algorithm.name()+"_sprd, maxSpread=max_"+algorithm.name()+"_sprd, ksSpread=ks_pvalue_"+algorithm.toString()+"_sprd, " +
									"meanHpvl=mean_"+algorithm.name()+"_hpvl, sdHpvl=sd_"+algorithm.name()+"_hpvl, minHpvl=min_"+algorithm.name()+"_hpvl, maxHpvl=max_"+algorithm.name()+"_hpvl, ksHpvl=ks_pvalue_"+algorithm.toString()+"_hpvl)");
							script.sendCommand("write.csv2(summary, quote=FALSE, file=\""+dirName+"/summary-"+algorithm.name()+"-"+instance.getInstanceDir()+"-"+executionNumber+"-"+evaluations+".csv\")");
					
							for (Algorithms algorithmComp : results.get(instance).keySet()) {
								if(!algorithmComp.equals(algorithm) && false) {
									for (Integer executionNumberComp :  results.get(instance).get(algorithmComp).keySet()) {
										ExperimentalData2 experimentalDataComp = mp.publishEvolutionQualityIndicators(results.get(instance).get(algorithmComp).get(executionNumberComp).get(evaluations).getInstanceIndex(0), bestFront, algorithmComp, instance, executionNumberComp,evaluations,maxEvaluationsOfInstance);
										String dirNameComp = RESULT_DIRECTORY + instance.getInstanceDir() + "/"+ algorithmComp.name() + "/"+ executionNumberComp + "/";
										String archiveNameComp = dirNameComp + algorithmComp.name()+"-"+instance.getInstanceDir()+"-"+executionNumberComp+"-"+evaluations+".data";
										
										DataSet dataSet2 = script.writeDataFile(archiveNameComp, experimentalDataComp, algorithmComp.name());
										script.loadFile(dataSet2, archiveNameComp);
										script.tableMannWhitney(experimentalData, "errt", algorithm.name(), algorithmComp.name());
										script.tableMannWhitney(experimentalData, "gdst", algorithm.name(), algorithmComp.name());
										script.tableMannWhitney(experimentalData, "sprd", algorithm.name(), algorithmComp.name());
										script.tableMannWhitney(experimentalData, "hpvl", algorithm.name(), algorithmComp.name());
										script.tableMannWhitney(experimentalData, "time", algorithm.name(), algorithmComp.name());
										
										script.tableEffectSize(experimentalData, "errt", algorithm.name(), algorithmComp.name());
										script.tableEffectSize(experimentalData, "gdst", algorithm.name(), algorithmComp.name());
										script.tableEffectSize(experimentalData, "sprd", algorithm.name(), algorithmComp.name());
										script.tableEffectSize(experimentalData, "hpvl", algorithm.name(), algorithmComp.name());
										script.tableEffectSize(experimentalData, "time", algorithm.name(), algorithmComp.name());
										
										
										
										String dirComp = RESULT_DIRECTORY+instance.getInstanceDir()+"/algoritmos/"+algorithm.name()+"["+executionNumber+"]"+algorithmComp.name()+"["+executionNumberComp+"]";
										FunctionsPointDetailsListener.criarDiretorio(instance.getInstanceDir()+"/algoritmos/"+algorithm.name()+"["+executionNumber+"]"+algorithmComp.name()+"["+executionNumberComp+"]");
										script.sendCommand("\npvalues <- data.frame(timee=pvalue_"+algorithm.name()+"_"+algorithmComp.name()+"_time, errt=pvalue_"+algorithm.name()+"_"+algorithmComp.name()+"_errt, gdst=pvalue_"+algorithm.name()+"_"+algorithmComp.name()+"_gdst, sprd=pvalue_"+algorithm.name()+"_"+algorithmComp.name()+"_sprd, hpvl=pvalue_"+algorithm.name()+"_"+algorithmComp.name()+"_hpvl)");
										script.sendCommand("write.csv2(pvalues, quote=FALSE, file=\""+dirComp+"/pvalues-"+evaluations+".csv\")");
										script.sendCommand("\neffectsizes <- data.frame( timee=effectsize1_"+algorithm.name()+"_"+algorithmComp.name()+"_time, errt=effectsize1_"+algorithm.name()+"_"+algorithmComp.name()+"_errt, gdst=effectsize1_"+algorithm.name()+"_"+algorithmComp.name()+"_gdst, sprd=effectsize1_"+algorithm.name()+"_"+algorithmComp.name()+"_sprd, hpvl=effectsize1_"+algorithm.name()+"_"+algorithmComp.name()+"_hpvl)");
										script.sendCommand("write.csv2(effectsizes, quote=FALSE, file=\""+dirComp+"/effectsizes-"+evaluations+".csv\")");
									}
								}
							}
						}
					}
				}
			}
			mp.saveBestFront("best", "best", bestFront, instance.getInstanceDir());
		}
		DataSet dataSet2 = script.writeDataFile(RESULT_DIRECTORY+"datas-quality.data", experimentalDataSet, "datasQuality");
		script.loadFile(dataSet2, RESULT_DIRECTORY+"datas-quality.data");
//		script.drawBoxPlot(dataSet2, "Tempo de Execução", "time",instancesNames, RESULT_DIRECTORY+"boxplot-time.jpg","Instância","Tempo de Execução (seg)","Algorithm");
//		script.drawBoxPlot(dataSet2, "Error Ratio", "errt",instancesNames, RESULT_DIRECTORY+"boxplot-errt.jpg","Instância","Error Ratio","Algorithm");
//		script.drawBoxPlot(dataSet2, "Generational Distance", "gdst", instancesNames, RESULT_DIRECTORY+"boxplot-gdst.jpg","Instância","Generational Distance","Algorithm");
//		script.drawBoxPlot(dataSet2, "Spread", "sprd",instancesNames, RESULT_DIRECTORY+"boxplot-sprd.jpg","Instância","Spread","Algorithm");
//		script.drawBoxPlot(dataSet2, "Hipervolume", "hpvl",instancesNames, RESULT_DIRECTORY+"boxplot-hpvl.jpg","Instância","Hipervolume","Algorithm");

		script.drawLinePlot(dataSet2, "Tempo de Execução", "time","Instance",instancesNames, algorithmsNames, evaluationsByInstance, RESULT_DIRECTORY+"evaluations-time.jpg","% do Max. Avaliações","Tempo de Execução","Algorithm",1);	
//		
		//		
		script.drawLinePlot(dataSet2, "Error Ratio", "errt","Instance",instancesNames, algorithmsNames, evaluationsByInstance, RESULT_DIRECTORY+"evaluations-errt.jpg","% do Max. Avaliações","Error Ratio","Algorithm",1);	
		script.drawLinePlot(dataSet2, "Generational Distance", "gdst","Instance",instancesNames, algorithmsNames, evaluationsByInstance, RESULT_DIRECTORY+"evaluations-gdst.jpg","% do Max. Avaliações","Generational Distance","Algorithm",1);		
		script.drawLinePlot(dataSet2, "Spread", "sprd","Instance",instancesNames, algorithmsNames, evaluationsByInstance, RESULT_DIRECTORY+"evaluations-sprd.jpg","% do Max. Avaliações","Spread","Algorithm",1);		
		script.drawLinePlot(dataSet2, "Hipervolume", "hpvl","Instance",instancesNames, algorithmsNames, evaluationsByInstance, RESULT_DIRECTORY+"evaluations-hpvl.jpg","% do Max. Avaliações","Hipervolume","Algorithm",1);
//		
		script.close();
	}
}

class NewExperimentGenerationFileReader extends GenerationFileReader
{
	@Override
	protected void calculateValue(ParetoFront front, ParetoFront bestFront, double[] data)
	{
		double[][] bestFrontValues = bestFront.getValues();
		double[][] currentFrontValues = front.getValues();

		data[0] = 1.0 - ((double)bestFront.countCommonVertices(front)) / front.getVertexCount();
		data[1] = new GenerationalDistance().generationalDistance(currentFrontValues, bestFrontValues, bestFront.getObjectiveCount());
		data[2] = new Spread().spread(currentFrontValues, bestFrontValues, bestFront.getObjectiveCount());
	}
}

class NewInstance {
	private String instanceName;
	private String instanceDir;
	NewInstance(String instanceName, String instanceDir, Integer transactionsNumber, Integer totalFunctionsPoint, Integer totalSatisfaction) {
		super();
		this.instanceName = instanceName;
		this.instanceDir = instanceDir;
		this.totalFP = totalFunctionsPoint;
		this.transactionsNumber = transactionsNumber;
		this.totalSatisfaction = totalSatisfaction;
	}
	private Integer transactionsNumber;
	private Integer totalFP;
	private Integer totalSatisfaction;
	public Integer getTotalSatisfaction() {
		return totalSatisfaction;
	}
	public void setTotalSatisfaction(Integer totalSatisfaction) {
		this.totalSatisfaction = totalSatisfaction;
	}
	public Integer getTotalFP() {
		return totalFP;
	}
	public void setTotalFP(Integer totalFP) {
		this.totalFP = totalFP;
	}
	public String getInstanceName() {
		return instanceName;
	}
	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}
	public Integer getTransactionsNumber() {
		return transactionsNumber;
	}
	public void setTransactionsNumber(Integer transactionsNumber) {
		this.transactionsNumber = transactionsNumber;
	}
	public String getInstanceDir() {
		return instanceDir;
	}
	public void setInstanceDir(String instanceDir) {
		this.instanceDir = instanceDir;
	}
}