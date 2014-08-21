package br.uniriotec.vitor.padilha.dissertacao.main;

import java.math.BigDecimal;
import java.util.Scanner;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import br.uniriotec.vitor.padilha.dissertacao.algorithm.Algorithms;
import br.uniriotec.vitor.padilha.dissertacao.calculator.FunctionPointCalculator;
import br.uniriotec.vitor.padilha.dissertacao.engine.GenerateReleasesInstancesForExperiment;
import br.uniriotec.vitor.padilha.dissertacao.listener.mono.FunctionsPointDetailsListener;
import br.uniriotec.vitor.padilha.dissertacao.model.FunctionPointSystem;
import br.uniriotec.vitor.padilha.dissertacao.model.ParametersAlgorithm;
import br.uniriotec.vitor.padilha.dissertacao.utils.FunctionsPointReader;

public class MainProgram {
	//private static final Algorithms[] ALGORITHMS = new Algorithms[]{Algorithms.FUNCTIONPOINTGA};
	//private static int NUMBER_OF_PARTITIONS = 3;
	public static void main(String[] args) throws Exception
	{
		int numberOfPartitions = Integer.valueOf(FunctionsPointDetailsListener.getProp().getProperty("numberOfPartitions"));
		String configurations = FunctionsPointDetailsListener.getProp().getProperty("configurations");
		JSONTokener jsonTokener = new JSONTokener(configurations);
		JSONObject configs = new JSONObject( jsonTokener );
		JSONArray arrayConfigs = configs.getJSONArray("configs");
		for (String instance : FunctionsPointDetailsListener.getProp().getProperty("instancias").split(",")) {											
			FunctionsPointReader reader = new FunctionsPointReader(FunctionsPointDetailsListener.getProp().getProperty("diretorio.instancia")+instance+"/functions-point.xml",FunctionsPointDetailsListener.getProp().getProperty("diretorio.instancia")+instance+"/stakeholders-interest.xml");
			FunctionPointSystem functionPointSystem = reader.read();
			FunctionPointCalculator functionPointCalculator = new FunctionPointCalculator();
			GenerateReleasesInstancesForExperiment ga = new GenerateReleasesInstancesForExperiment();
			String[] algorithmsNames = FunctionsPointDetailsListener.getProp().getProperty("algorithms").split(",");
	       	Algorithms[] algorithms = new Algorithms[1];
	       	if(algorithmsNames.length==0) {
		      	System.out.println("Informe o algoritmo que deseja utilizar:");
				int algorithmNumber = 0;
				Scanner in = new Scanner(System.in);
				System.out.print("Digite ");
				for(int i=0;i<Algorithms.values().length;i++){
					System.out.print(" "+(i+1)+" para "+Algorithms.values()[i]+", ");
				}
				System.out.println("");
				while (algorithmNumber<1 || algorithmNumber>Algorithms.values().length){
					System.out.println("Numero inválido, digite novamente: ");
					algorithmNumber = in.nextInt();
					System.out.println("");
				}
				algorithms[0] = Algorithms.values()[algorithmNumber-1];
	       	}
	       	else {
	       		algorithms = new Algorithms[algorithmsNames.length];
	       		for (int i = 0; i < algorithmsNames.length; i++) {
	       			algorithms[i] = Algorithms.valueOf(algorithmsNames[i]);
				}
	       	}
//			
	       	int numeroTransacoes = functionPointSystem.getTransactionModel().getTransactions().size();
	        int	maxEvaluations = numeroTransacoes*2;
			maxEvaluations *= maxEvaluations;
			double partEvaluation = new BigDecimal(maxEvaluations/numberOfPartitions).setScale(0,BigDecimal.ROUND_HALF_DOWN).intValue();
			//int limite = Integer.valueOf(FunctionsPointDetailsListener.getProp().getProperty("limite"));
			int limite = functionPointCalculator.calculate(functionPointSystem, 0, null);
			double limiteValue = new BigDecimal(limite/numberOfPartitions).setScale(0,BigDecimal.ROUND_HALF_DOWN).intValue();
	       	for (int i = 0; i < algorithms.length; i++) {	       		
	       		//ga.setListerner(new FunctionsPointDetailsListener("resources/saida-"+algorithms[i].name()+".txt", true,functionPointCalculator,functionPointSystem, Integer.valueOf(FunctionsPointDetailsListener.getProp().getProperty("ciclos")), algorithms[i]));
		      	Vector<FunctionPointSystem> instancias = new Vector<FunctionPointSystem>();
				instancias.add(functionPointSystem);
				for(int a=0;a<numberOfPartitions;a++){
					int maxEvaluations2 = maxEvaluations;
					int limite2 = limite;
					boolean cria = true;
					for(int k = 0;k<arrayConfigs.length();k++){
						JSONArray mutations = arrayConfigs.getJSONObject(k).getJSONArray("mutations");
						JSONArray crossovers = arrayConfigs.getJSONObject(k).getJSONArray("crossovers");
						JSONArray maxPopulationMultiplications = arrayConfigs.getJSONObject(k).getJSONArray("maxPopulationMultiplications");
						for(int w = 0;w<mutations.length();w++){
							double mutation = mutations.getDouble(w);
							for(int j = 0;j<crossovers.length();j++){
								double crossover = crossovers.getDouble(j);
								for(int l = 0;l<maxPopulationMultiplications.length();l++){
									double maxPopulationMultiplication = maxPopulationMultiplications.getDouble(l);
									ParametersAlgorithm parametersAlgorithm = new ParametersAlgorithm();
									parametersAlgorithm.setCrossoverProbability(crossover);		
									parametersAlgorithm.setNumeroDeTransacoes(functionPointSystem.getTransactionModel().getTransactions().size());
									parametersAlgorithm.setMultiplicadorPopulacao(maxPopulationMultiplication);
							       	// Executa os experimentos com algoritmos genéticos
									parametersAlgorithm.setMutationProbability(Double.valueOf(1)/functionPointSystem.getTransactionModel().getTransactions().size());
									String tituloExecucao ="Processando Algoritmo "+ algorithms[i].name()+" para instância "+ instance+" para proposta";
									if(!algorithms[i].isMonoobjective()) {
										maxEvaluations2 = Double.valueOf((a+1)*partEvaluation).intValue();
										if(a==(numberOfPartitions-1))
											maxEvaluations2 =+ maxEvaluations;						
									}
									else {
										if(a==(numberOfPartitions-1))
											continue;
										limite2 = Double.valueOf((a+1)*limiteValue).intValue();
										System.out.println( tituloExecucao+" NRP-CLS");	
										parametersAlgorithm.setNumeroMaximoDeAvaliacoes(maxEvaluations2);
										ga.run(algorithms[i], instancias, functionPointCalculator, limite2, 1, Integer.valueOf(FunctionsPointDetailsListener.getProp().getProperty("ciclos")), ((a==0)? true : false), false, parametersAlgorithm);
										//ga.setListerner(new FunctionsPointDetailsListener("resources/saida-atual-"+algorithms[i].name()+".txt", true,functionPointCalculator,functionPointSystem, Integer.valueOf(FunctionsPointDetailsListener.getProp().getProperty("ciclos")), algorithms[i]));
										cria = false;
									}
									System.out.println(tituloExecucao+" NRP-APF");
									parametersAlgorithm.setNumeroMaximoDeAvaliacoes(maxEvaluations2);
//									if(a!=0)
//										continue;
									ga.run(algorithms[i], instancias, functionPointCalculator, limite2, 1, Integer.valueOf(FunctionsPointDetailsListener.getProp().getProperty("ciclos")), ((a==0 && cria)? true : false), true, parametersAlgorithm);
								}
							}
					    }		
		
					}
				}
			}
		}
	}
}
