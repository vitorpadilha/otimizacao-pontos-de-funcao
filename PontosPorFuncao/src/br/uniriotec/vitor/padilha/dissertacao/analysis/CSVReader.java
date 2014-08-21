package br.uniriotec.vitor.padilha.dissertacao.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import unirio.experiments.multiobjective.analysis.model.MultiExperimentResult;
import br.uniriotec.vitor.padilha.dissertacao.algorithm.Algorithms;
import br.uniriotec.vitor.padilha.dissertacao.utils.NumberUtils;

public class CSVReader {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		File dir = new File(MainAnalysisProgram.BASE_DIRECTORY);
		Map<String,Map<Algorithms,Map<Integer,MultiExperimentResult>>> results = new HashMap<String, Map<Algorithms,Map<Integer,MultiExperimentResult>>>();
		List<String> instancesNames = new ArrayList<String>();
		Set<String> algorithmsNames = new HashSet<String>();
		Map<String,Map<String,Map<Long,List<InstancesData>>>> datasTime = new LinkedHashMap<String, Map<String,Map<Long,List<InstancesData>>>>();
		Map<String,Map<String,Map<Long,List<InstancesData>>>> datasHpvl = new LinkedHashMap<String, Map<String,Map<Long,List<InstancesData>>>>();
		Map<String,Map<String,Map<Long,List<InstancesData>>>> datasSpread = new LinkedHashMap<String, Map<String,Map<Long,List<InstancesData>>>>();
		Map<String,Map<String,Map<Long,List<InstancesData>>>> datasErrt = new LinkedHashMap<String, Map<String,Map<Long,List<InstancesData>>>>();
		Map<String,Map<String,Map<Long,List<InstancesData>>>> datasGdst = new LinkedHashMap<String, Map<String,Map<Long,List<InstancesData>>>>();
		Set<String> algorithms = new HashSet<String>();
		Set<Long> evaluationsSet = new HashSet<Long>();
		if(dir.exists()) {
			for(NewInstance instance:MainAnalysisProgram.INSTANCES){
				File dirInstance = new File(MainAnalysisProgram.BASE_DIRECTORY+"\\"+instance.getInstanceDir());
				instancesNames.add(instance.getInstanceName());
				int maxEvaluation = (4*instance.getTransactionsNumber()) * (4*instance.getTransactionsNumber());
				if(dirInstance.exists()) {
					if(!datasErrt.containsKey(instance.getInstanceName())) {
						datasTime.put(instance.getInstanceName(), new LinkedHashMap<String, Map<Long,List<InstancesData>>>());
						datasErrt.put(instance.getInstanceName(), new LinkedHashMap<String, Map<Long,List<InstancesData>>>());
						datasGdst.put(instance.getInstanceName(), new LinkedHashMap<String, Map<Long,List<InstancesData>>>());
						datasSpread.put(instance.getInstanceName(), new LinkedHashMap<String, Map<Long,List<InstancesData>>>());
						datasHpvl.put(instance.getInstanceName(), new LinkedHashMap<String, Map<Long,List<InstancesData>>>());
					}
					if(!results.containsKey(instance.getInstanceName()))
						results.put(instance.getInstanceName(), new HashMap<Algorithms, Map<Integer,MultiExperimentResult>>());
					for(File dirInstanceAlgorithm:dirInstance.listFiles()){
						if(dirInstanceAlgorithm.isDirectory()) {
							boolean searchAlgorithm = false;
							for(Algorithms algorithmInt : Algorithms.values()){
								if(algorithmInt.name().equals(dirInstanceAlgorithm.getName()))
									searchAlgorithm = true;
							}
							if(!searchAlgorithm)
								continue;
							algorithmsNames.add(dirInstanceAlgorithm.getName());
							Algorithms algorithm = Algorithms.valueOf(dirInstanceAlgorithm.getName());
							if(algorithm.isMonoobjective())
								continue;
							if(!results.get(instance.getInstanceName()).containsKey(algorithm)) {
								results.get(instance.getInstanceName()).put(algorithm, new HashMap<Integer, MultiExperimentResult>());
							}
							algorithms.add(algorithm.name());
							if(!datasErrt.get(instance.getInstanceName()).containsKey(algorithm.name())) {
								datasTime.get(instance.getInstanceName()).put(algorithm.name(),new LinkedHashMap<Long, List<InstancesData>>());
								datasErrt.get(instance.getInstanceName()).put(algorithm.name(),new LinkedHashMap<Long, List<InstancesData>>());
								datasGdst.get(instance.getInstanceName()).put(algorithm.name(),new LinkedHashMap<Long, List<InstancesData>>());
								datasSpread.get(instance.getInstanceName()).put(algorithm.name(),new LinkedHashMap<Long, List<InstancesData>>());
								datasHpvl.get(instance.getInstanceName()).put(algorithm.name(),new LinkedHashMap<Long, List<InstancesData>>());
							}
							for(File dirExecutionNumber:dirInstanceAlgorithm.listFiles()){
								for(File fileCSV:dirExecutionNumber.listFiles()) {
									if(!fileCSV.isFile() || !fileCSV.getName().contains("summary-"+algorithm.name()+"-"+instance.getInstanceDir())) {
										continue;
									}
									//File fileCSV = new File(dirName+"/summary-"+algorithm.name()+"-"+instance.getInstanceName()+"-"+dirExecutionNumber.getName()+".csv");
									String[] nameParts = fileCSV.getName().split("-");
									nameParts = nameParts[nameParts.length-1].split("\\.");
									Long evaluations = Long.valueOf(nameParts[0]);
									
									Long evaluationsPercent = NumberUtils.formatNumber((Double.valueOf(evaluations)/maxEvaluation)*100, 0).longValue();
									evaluationsSet.add(evaluationsPercent);
									BufferedReader reader = new BufferedReader(new FileReader(fileCSV));
									String line = null;
									if(!datasErrt.get(instance.getInstanceName()).get(algorithm.name()).containsKey(evaluationsPercent)) {
										datasTime.get(instance.getInstanceName()).get(algorithm.name()).put(evaluationsPercent, new ArrayList<InstancesData>());
										datasErrt.get(instance.getInstanceName()).get(algorithm.name()).put(evaluationsPercent, new ArrayList<InstancesData>());
										datasGdst.get(instance.getInstanceName()).get(algorithm.name()).put(evaluationsPercent, new ArrayList<InstancesData>());
										datasSpread.get(instance.getInstanceName()).get(algorithm.name()).put(evaluationsPercent, new ArrayList<InstancesData>());
										datasHpvl.get(instance.getInstanceName()).get(algorithm.name()).put(evaluationsPercent, new ArrayList<InstancesData>());
									}
									while ((line = reader.readLine()) != null) {
									    // ...
										/*
										 * meanTime
										 * sdTime
										 * minTime
										 * maxTime
										 * meanErrt
										 * sdErrt
										 * minErrt
										 * maxErrt
										 * meanGdst
										 * sdGdst
										 * minGdst
										 * maxGdst
										 * meanSpread
										 * sdSpread
										 * minSpread
										 * maxSpread
										 * meanHpvl
										 * sdHpvl
										 * minHpvl
										 * maxHpvl
										 */
										if(line.startsWith(";")){
											continue;
										}
										String[] datasLine = line.split(";");
										InstancesData instancesTime = new InstancesData(instance.getInstanceName(),algorithm.getName(),"time", Double.valueOf(datasLine[1].replace(",", ".")), Double.valueOf(datasLine[2].replace(",", ".")), Double.valueOf(datasLine[3].replace(",", ".")), Double.valueOf(datasLine[4].replace(",", ".")), Double.valueOf(datasLine[5].replace(",", ".")));
										InstancesData instancesErrt = new InstancesData(instance.getInstanceName(),algorithm.getName(),"errt", Double.valueOf(datasLine[6].replace(",", ".")), Double.valueOf(datasLine[7].replace(",", ".")), Double.valueOf(datasLine[8].replace(",", ".")), Double.valueOf(datasLine[9].replace(",", ".")), Double.valueOf(datasLine[10].replace(",", ".")));
										InstancesData instancesGdst = new InstancesData(instance.getInstanceName(),algorithm.getName(),"gdst", Double.valueOf(datasLine[11].replace(",", ".")), Double.valueOf(datasLine[12].replace(",", ".")), Double.valueOf(datasLine[13].replace(",", ".")), Double.valueOf(datasLine[14].replace(",", ".")), Double.valueOf(datasLine[15].replace(",", ".")));
										InstancesData instancesSprd = new InstancesData(instance.getInstanceName(),algorithm.getName(),"sprd", Double.valueOf(datasLine[16].replace(",", ".")), Double.valueOf(datasLine[17].replace(",", ".")), Double.valueOf(datasLine[18].replace(",", ".")), Double.valueOf(datasLine[19].replace(",", ".")), Double.valueOf(datasLine[20].replace(",", ".")));
										InstancesData instancesHpvl = new InstancesData(instance.getInstanceName(),algorithm.getName(),"hpvl", Double.valueOf(datasLine[21].replace(",", ".")), Double.valueOf(datasLine[22].replace(",", ".")), Double.valueOf(datasLine[23].replace(",", ".")), Double.valueOf(datasLine[24].replace(",", ".")), Double.valueOf(datasLine[25].replace(",", ".")));
										Map<String,Double> effectSizesTime = new HashMap<String, Double>();
										Map<String,Double> effectSizesErrt = new HashMap<String, Double>();
										Map<String,Double> effectSizesGdst = new HashMap<String, Double>();
										Map<String,Double> effectSizesSprd = new HashMap<String, Double>();
										Map<String,Double> effectSizesHpvl = new HashMap<String, Double>();
										
										Map<String,Double> pValuesTime = new HashMap<String, Double>();
										Map<String,Double> pValuesErrt = new HashMap<String, Double>();
										Map<String,Double> pValuesGdst = new HashMap<String, Double>();
										Map<String,Double> pValuesSprd = new HashMap<String, Double>();
										Map<String,Double> pValuesHpvl = new HashMap<String, Double>();
										File dirCompare = new File(dirInstance.getAbsolutePath()+"\\algoritmos");
										for(File dirInstanceAlgorithm2:dirCompare.listFiles()){
											if(dirInstanceAlgorithm2.getName().equals(algorithm.name()) || !dirInstanceAlgorithm2.getName().startsWith(algorithm.name()) ) {
												continue;
											}
											
											String[] partNames = dirInstanceAlgorithm2.getName().split("]");
											String temp = partNames[1];
											partNames = temp.split("\\[");
											
											File fileEffect = new File(dirInstanceAlgorithm2.getAbsolutePath() + "/"+"effectsizes-"+evaluations+".csv");
											BufferedReader readerEffect = new BufferedReader(new FileReader(fileEffect));
											while ((line = readerEffect.readLine()) != null) {
												if(line.startsWith(";")){
													continue;
												}
												datasLine = line.split(";");
												effectSizesTime.put(partNames[0], Double.valueOf(datasLine[1].replace(",", ".")));
												effectSizesErrt.put(partNames[0], Double.valueOf(datasLine[2].replace(",", ".")));
												effectSizesGdst.put(partNames[0], Double.valueOf(datasLine[3].replace(",", ".")));
												effectSizesSprd.put(partNames[0], Double.valueOf(datasLine[4].replace(",", ".")));
												effectSizesHpvl.put(partNames[0], Double.valueOf(datasLine[5].replace(",", ".")));
											}
											
											File filePvalues = new File(dirInstanceAlgorithm2.getAbsolutePath() + "/"+"pvalues-"+evaluations+".csv");
											BufferedReader readerPvalues = new BufferedReader(new FileReader(filePvalues));
											while ((line = readerPvalues.readLine()) != null) {
												if(line.startsWith(";")){
													continue;
												}
												datasLine = line.split(";");
												try {
													pValuesTime.put(partNames[0], CSVReader.adicionaEffectSize(datasLine[1]));
													pValuesErrt.put(partNames[0], CSVReader.adicionaEffectSize(datasLine[2]));
													pValuesGdst.put(partNames[0], CSVReader.adicionaEffectSize(datasLine[3]));
													pValuesSprd.put(partNames[0], CSVReader.adicionaEffectSize(datasLine[4]));
													pValuesHpvl.put(partNames[0], CSVReader.adicionaEffectSize(datasLine[5]));
												}
												catch (Exception e) {
													System.out.println("Instância: "+instance.getInstanceName()+", validacao: "+evaluations+", Algoritmo:"+ algorithm.name()+", Execucao: ");
													throw new Exception(e);
												}
											}
										}
										instancesTime.setEffectSizes(effectSizesTime);
										instancesErrt.setEffectSizes(effectSizesErrt);
										instancesGdst.setEffectSizes(effectSizesGdst);
										instancesSprd.setEffectSizes(effectSizesSprd);
										instancesHpvl.setEffectSizes(effectSizesHpvl);
										
										instancesTime.setPvalues(pValuesTime);
										instancesErrt.setPvalues(pValuesErrt);
										instancesGdst.setPvalues(pValuesGdst);
										instancesSprd.setPvalues(pValuesSprd);
										instancesHpvl.setPvalues(pValuesHpvl);
										
										datasTime.get(instance.getInstanceName()).get(algorithm.name()).get(evaluationsPercent).add(instancesTime);
										datasErrt.get(instance.getInstanceName()).get(algorithm.name()).get(evaluationsPercent).add(instancesErrt);
										datasGdst.get(instance.getInstanceName()).get(algorithm.name()).get(evaluationsPercent).add(instancesGdst);
										datasSpread.get(instance.getInstanceName()).get(algorithm.name()).get(evaluationsPercent).add(instancesSprd);
										datasHpvl.get(instance.getInstanceName()).get(algorithm.name()).get(evaluationsPercent).add(instancesHpvl);
									}
									reader.close();
								}
							}
						}
					}
				}
			}
		}
		CSVReader.escreveArquivos("time", datasTime, algorithms, evaluationsSet);
		CSVReader.escreveArquivos("errt", datasErrt, algorithms, evaluationsSet);
		CSVReader.escreveArquivos("gdst", datasGdst, algorithms, evaluationsSet);
		CSVReader.escreveArquivos("sprd", datasSpread, algorithms, evaluationsSet);
		CSVReader.escreveArquivos("hpvl", datasHpvl, algorithms, evaluationsSet);
	}
	public static Double adicionaEffectSize(String valor) {
		if(valor.equalsIgnoreCase("NA")) {
			return 1D;
		}
		return Double.valueOf(valor.replace(",", "."));
	}
	public static void escreveArquivos(String name, Map<String,Map<String,Map<Long, List<InstancesData>>>> datas, Set<String> algorithms, Set<Long> evaluationsSet) throws IOException{
		
		List<String> algorithms2 = new ArrayList<String>();
		algorithms2.addAll(algorithms);
		int decimais = 2;
		if(name.equals("gdst"))
		{
			decimais = 3;
		}
		for (Long evaluations : evaluationsSet) {
			FileWriter file = new FileWriter(MainAnalysisProgram.BASE_DIRECTORY+"\\medias-"+name+"-"+evaluations+".csv");
			PrintWriter out = new PrintWriter(file);
			//String cabecalho = "";
			//String dados = "";
			boolean imprimiuCabecalho = false;
			String dados = "";
			String cabecalho = "";
			for (String instance : datas.keySet()) {
				String dados2 = "";
				String cabecalho2 = "Instancia;";				
				dados+= instance+";";			
				for (String algorithm : algorithms) {				
					if(!imprimiuCabecalho) {
						cabecalho+=algorithm+";";
						for (String algorithm2 : algorithms2) {
							if(algorithm.equals(algorithm2))
								continue;
							cabecalho2+="ES:"+algorithm.charAt(0)+">"+algorithm2.charAt(0)+";";
								
						}	
						for (String algorithm2 : algorithms2) {
							if(algorithm.equals(algorithm2))
								continue;
							cabecalho2+="PV:"+algorithm.charAt(0)+" vs "+algorithm2.charAt(0)+";";
						}	
						
					}
					if(datas.get(instance).get(algorithm).containsKey(evaluations)) {
						for (InstancesData instanceDatas : datas.get(instance).get(algorithm).get(evaluations)) {
								dados+=NumberUtils.formatNumberToString(instanceDatas.getAverage(),decimais)+" ± "+NumberUtils.formatNumberToString(instanceDatas.getSd(),decimais+1)+";";
								for (String algorithm2 : algorithms2) {
									if(algorithm.equals(algorithm2))
										continue;
									if(instanceDatas.getEffectSizes().containsKey(algorithm2))
										dados2+=NumberUtils.formatNumberToString(instanceDatas.getEffectSizes().get(algorithm2),2)+";";
									else 
										dados2+=";";
								}		
								
								for (String algorithm2 : algorithms2) {			
									if(algorithm.equals(algorithm2))
										continue;
									if(instanceDatas.getPvalues().containsKey(algorithm2))
										if(instanceDatas.getPvalues().get(algorithm2)<0.01)							
											dados2+="<0,01;";
										else if(instanceDatas.getPvalues().get(algorithm2)==1D)							
											dados2+="NA";
										else
											dados2+=NumberUtils.formatNumberToString(instanceDatas.getPvalues().get(algorithm2),2)+";";
									else 
										dados2+=";";
								}	
						}
					}
				}
				dados+=dados2;
				cabecalho+=cabecalho2;
				dados+="\n";
				imprimiuCabecalho = true;
			}
			out.print(cabecalho);
			out.println();
			out.print(dados);
			file.close();
		}
	}
}
