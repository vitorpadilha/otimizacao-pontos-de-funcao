package br.uniriotec.vitor.padilha.dissertacao.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.uniriotec.vitor.padilha.dissertacao.algorithm.Algorithms;
import br.uniriotec.vitor.padilha.dissertacao.utils.NumberUtils;

public class CVSReaderMono {
	public static void main(String[] args) throws Exception {
		File dir = new File(MainAnalysisProgram.BASE_DIRECTORY);
		Map<Long,Map<String,Map<String,List<InstancesData>>>> datasSatisfacao = new LinkedHashMap<Long, Map<String,Map<String,List<InstancesData>>>>();
		Map<Long,Map<String,Double>> pValues = new LinkedHashMap<Long, Map<String,Double>>();
		Map<Long,Map<String,Double>> effectSizes = new LinkedHashMap<Long, Map<String,Double>>();
		if(dir.exists()) {
			for(NewInstance instance:MainAnalysisProgram.INSTANCES){
				File dirInstance = new File(MainAnalysisProgram.BASE_DIRECTORY+"\\"+instance.getInstanceDir());
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
							if(!algorithm.isMonoobjective())
								continue;
							for(File dirExecutionNumber:dirInstanceAlgorithm.listFiles()){
								if(!dirExecutionNumber.getName().equals("1"))
									continue;
								for(File fileCSV:dirExecutionNumber.listFiles()) {
									if(!fileCSV.isFile() || !fileCSV.getName().contains("summary-"+algorithm.name()+"-"+instance.getInstanceDir())) {
										continue;
									}
									String[] nameParts = fileCSV.getName().split("-");
									Boolean isNRPAPF = Boolean.valueOf(nameParts[4]);
									String methodInBoolean = nameParts[4];
									Long FP = Long.valueOf(nameParts[nameParts.length-1].split("\\.")[0]);
									BufferedReader reader = new BufferedReader(new FileReader(fileCSV));
									String line = null;
									String method =((isNRPAPF.booleanValue())?"NRP-APF":"NRP-CLS");
									while ((line = reader.readLine()) != null) {
										if(line.startsWith(";")){
											continue;
										}
										String[] datasLine = line.split(";");
										InstancesData instancesData = new InstancesData(instance.getInstanceName(),algorithm.getName(),method, Double.valueOf(datasLine[6].replace(",", ".")), Double.valueOf(datasLine[7].replace(",", ".")), Double.valueOf(datasLine[8].replace(",", ".")), Double.valueOf(datasLine[9].replace(",", ".")),Double.valueOf(datasLine[10].replace(",", ".")));
										
										if(!datasSatisfacao.containsKey(FP)){
											datasSatisfacao.put(FP, new LinkedHashMap<String, Map<String,List<InstancesData>>>());
										}
										if(!datasSatisfacao.get(FP).containsKey(instance.getInstanceName())){
											datasSatisfacao.get(FP).put(instance.getInstanceName(), new LinkedHashMap<String, List<InstancesData>>());
										}
										if(!datasSatisfacao.get(FP).get(instance.getInstanceName()).containsKey(method)){
											datasSatisfacao.get(FP).get(instance.getInstanceName()).put(method,new ArrayList<InstancesData>());
										}
										datasSatisfacao.get(FP).get(instance.getInstanceName()).get(method).add(instancesData);
									}
									File dirCompare = new File(dirInstance.getAbsolutePath()+"\\metodos");
									for(File dirInstanceAlgorithm2:dirCompare.listFiles()){
										if(dirInstanceAlgorithm2.getName().equals(methodInBoolean) || !dirInstanceAlgorithm2.getName().startsWith(methodInBoolean) || !isNRPAPF) {
											continue;
										}
										String[] partNames = dirInstanceAlgorithm2.getName().split("]");
										String temp = partNames[1];
										partNames = temp.split("\\[");
										if(!partNames[1].equals(dirExecutionNumber.getName()))
											continue;
										File fileEffect = new File(dirInstanceAlgorithm2.getAbsolutePath() + "/"+"effectsizes-"+FP+".csv");
										BufferedReader readerEffect = new BufferedReader(new FileReader(fileEffect));
										while ((line = readerEffect.readLine()) != null) {
											if(line.startsWith(";")){
												continue;
											}
											String [] datasLine = line.split(";");
											if(!effectSizes.containsKey(FP))
												effectSizes.put(FP, new LinkedHashMap<String, Double>());
											if(!effectSizes.get(FP).containsKey(instance.getInstanceName()))
												effectSizes.get(FP).put(instance.getInstanceName(), Double.valueOf(datasLine[2].replace(",", ".")));
											else
												System.out.println("ERRO - Effect");
										}
										File filePValue = new File(dirInstanceAlgorithm2.getAbsolutePath() + "/"+"pvalues-"+FP+".csv");
										BufferedReader readerPValue = new BufferedReader(new FileReader(filePValue));
										
										while ((line = readerPValue.readLine()) != null) {
											if(line.startsWith(";")){
												continue;
											}
											String [] datasLine = line.split(";");
											if(!pValues.containsKey(FP))
												pValues.put(FP, new LinkedHashMap<String, Double>());
											if(!pValues.get(FP).containsKey(instance.getInstanceName())) {
												try {
												pValues.get(FP).put(instance.getInstanceName(), Double.valueOf(datasLine[2].replace(",", ".")));
												}
												catch (Exception e) {
													if(datasLine[2].equals("NA")) {
														pValues.get(FP).put(instance.getInstanceName(), 5D);
														
													} else{
														System.out.println("FP:"+FP+" Instancia:"+instance.getInstanceDir()+" Metodo"+isNRPAPF);
														throw e;
													}
												}
											}
											else
												System.out.println("ERRO - P Value");
										}
									}
								}
							}
						}
					}
				}
			}
		}
		escreveArquivosMedias("satisfacao-mono-medias-2", datasSatisfacao);
		escreveArquivosEstatisticasInferenciais("satisfacao-mono-inferencial-2", pValues,effectSizes);
	}
	public static NewInstance getInstanceByDirName(String instanceDir) {
		for (NewInstance instance : MainAnalysisProgram.INSTANCES) {
			if(instance.getInstanceDir().equals(instanceDir))
				return instance;
		}
		return null;
	}
	public static void escreveArquivosMedias(String name, Map<Long,Map<String,Map<String, List<InstancesData>>>> datas) throws IOException{
		FileWriter file = new FileWriter(MainAnalysisProgram.BASE_DIRECTORY+"\\"+name+".csv");
		PrintWriter out = new PrintWriter(file);
		boolean construiuCabecalho = false;
		
		for (Long FPPercent : datas.keySet()) {
			String valores = "";
			String valoresAux = "";
			String cabecalhoInstancias = "";
			String cabecalhoMetodo = "";
			if(!construiuCabecalho) {
				out.print("Percentual FP;");
				out.print("Instâncias;");
				out.print("Instâncias;");
				out.print("Instâncias;");
				out.print("Instâncias;");
				out.print("Instâncias;");
				out.print("Instâncias;");
				out.print("Instâncias;");
				out.print("Instâncias;");
				out.print("Instâncias;");
				out.print("Instâncias;");
				out.print("Instâncias;");
				out.print("Instâncias;");
				out.print("Instâncias;");
				out.print("Instâncias;");
				out.print("Instâncias;");
				out.print("Instâncias;");
				out.println();
				out.print(";");
				out.print("Método;");
				out.print("Método;");
				out.print("Método;");
				out.print("Método;");
				out.print("Método;");
				out.print("Método;");
				out.print("Método;");
				out.print("Método;");
				out.print("Método;");
				out.print("Método;");
				out.print("Método;");
				out.print("Método;");
				out.print("Método;");
				out.print("Método;");
				out.print("Método;");
				out.print("Método\n");
				cabecalhoInstancias += ";";
				cabecalhoMetodo += ";";
			}
			valores+=FPPercent+";";
			valoresAux+=FPPercent+";";
			for (String instanceName : datas.get(FPPercent).keySet()) {
				for (String metodo : datas.get(FPPercent).get(instanceName).keySet()) {
					if(!construiuCabecalho) {
						cabecalhoInstancias += instanceName+";"+instanceName+";";
						cabecalhoMetodo += metodo+";"+metodo+";";
					}
					else {
						valoresAux += instanceName+metodo+";";
					}
					valores+=NumberUtils.formatNumberToString(datas.get(FPPercent).get(instanceName).get(metodo).get(0).getAverage(),1)+" ± "+NumberUtils.formatNumberToString(datas.get(FPPercent).get(instanceName).get(metodo).get(0).getSd(),1)+";"+NumberUtils.formatNumberToString(datas.get(FPPercent).get(instanceName).get(metodo).get(0).getKolmogorovSmirnov(),2)+";";
				}
			}
			
			if(!construiuCabecalho) {
				out.println(cabecalhoInstancias);
				out.println(cabecalhoMetodo);
			}
			//out.println(valoresAux);
			out.println(valores);
			construiuCabecalho = true;
		}
		
		
		file.close();
	}
	public static void escreveArquivosEstatisticasInferenciais(String name, Map<Long,Map<String,Double>> pValues, Map<Long,Map<String,Double>> effectSizes) throws IOException{
		FileWriter file = new FileWriter(MainAnalysisProgram.BASE_DIRECTORY+"\\"+name+".csv");
		PrintWriter out = new PrintWriter(file);
		boolean construiuCabecalho = false;
		
		for (Long FPPercent : pValues.keySet()) {
			String valores = "";
			String valoresAux = "";
			String cabecalhoInstancias = "";
			String cabecalhoMetodo = "";
			if(!construiuCabecalho) {
				out.print("Percentual FP;");
				out.print("Instâncias;");
				out.print("Instâncias;");
				out.print("Instâncias;");
				out.print("Instâncias;");
				out.print("Instâncias;");
				out.print("Instâncias;");
				out.print("Instâncias;");
				out.print("Instâncias;");
				out.println();
				out.print(";");
				out.print("PV;");
				out.print("ES;");
				out.print("PV;");
				out.print("ES;");
				out.print("PV;");
				out.print("ES;");
				out.print("PV;");
				out.print("ES\n");
				cabecalhoInstancias += ";";
				cabecalhoMetodo += ";";
			}
			valores+=FPPercent+";";
			valoresAux+=FPPercent+";";
			for (String instanceName : pValues.get(FPPercent).keySet()) {
				if(!construiuCabecalho) {
					cabecalhoInstancias += instanceName+";";
					cabecalhoInstancias += instanceName+";";
				}
				Double pValue = pValues.get(FPPercent).get(instanceName);
				String pvalueString = "";
				if(pValue<1.1) 
				 pvalueString = (pValue<0.01)?"<0,01":NumberUtils.formatNumberToString(pValues.get(FPPercent).get(instanceName),3);
				else
				 pvalueString = "N\\A";
				valores+=pvalueString+";"+NumberUtils.formatNumberToString(effectSizes.get(FPPercent).get(instanceName)*100,1)+"%;";
			}
			
			if(!construiuCabecalho) {
				out.println(cabecalhoInstancias);
			}
			//out.println(valoresAux);
			out.println(valores);
			construiuCabecalho = true;
		}
		
		
		file.close();
	}
}
