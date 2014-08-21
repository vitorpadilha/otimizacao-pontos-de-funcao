package br.uniriotec.vitor.padilha.dissertacao.marcio;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import jmetal.qualityIndicator.GenerationalDistance;
import jmetal.qualityIndicator.Spread;
import unirio.experiments.multiobjective.analysis.model.MultiExperimentResult;
import unirio.experiments.multiobjective.analysis.model.ParetoFront;
import unirio.experiments.multiobjective.analysis.reader.MultiExperimentFileReader;
import unirio.experiments.statistics.ExperimentalData;
import unirio.experiments.statistics.Script;

@SuppressWarnings("unused")
public class MainProgram
{
	private static final String RESULT_DIRECTORY = "/Users/Marcio Barros/Desktop/Codigos/Pesquisa/GAClustering/";

	private static final String BASE_DIRECTORY = "\\Users\\Marcio Barros\\Desktop\\Resultados Objetivos\\NSGA\\";
	private static final String DIRECTORY_ECA = BASE_DIRECTORY + "eca\\";
	private static final String DIRECTORY_EVM = BASE_DIRECTORY + "evm\\";
	private static final String DIRECTORY_SECA = BASE_DIRECTORY + "seca\\";
	
	private void saveGenerationFile(String filename, double[][] result, int dataCount, int generationCount, int cycleCount) throws Exception
	{
		PrintWriter out = new PrintWriter(new FileWriter(filename));
		
		for (int generation = 0; generation < generationCount; generation++)
		{
			for (int data = 0; data < dataCount; data++)
				for (int cycle = 0; cycle < cycleCount; cycle++)
					out.print(result[cycle][generation * dataCount + data] + "\t");
			
			out.println();
		}
		
		out.close();
	}

	private void publishEvolutionQualityIndicators(String name, ParetoFront bestFrontier) throws Exception
	{
		double[][] eca = new ExperimentGenerationFileReader().execute(DIRECTORY_ECA, name, 3, 200, 30, 5, 3, bestFrontier);
		saveGenerationFile(RESULT_DIRECTORY.replace('/', '\\') + "eca_" + name + ".txt", eca, 3, 200, 30);
		
		double[][] evm = new ExperimentGenerationFileReader().execute(DIRECTORY_EVM, name, 3, 200, 30, 5, 3, bestFrontier);
		saveGenerationFile(RESULT_DIRECTORY.replace('/', '\\') + "evm_" + name + ".txt", evm, 3, 200, 30);
		
		double[][] seca = new ExperimentGenerationFileReader().execute(DIRECTORY_SECA, name, 3, 200, 30, 4, -1, bestFrontier);
		saveGenerationFile(RESULT_DIRECTORY.replace('/', '\\') + "seca_" + name + ".txt", seca, 3, 200, 30);
	}

	private void saveBestFront(String name, String configuration, ParetoFront front) throws Exception
	{
		String filename = RESULT_DIRECTORY.replace('/', '\\') + configuration + "_" + name + ".txt";
		/*front.removeObjective(3);
		front.removeObjective(2);*/
		
		PrintWriter out = new PrintWriter(new FileWriter(filename));
		out.println("cohe\tcoup");
		
		for (int i = 0; i < front.getVertexCount(); i++)
			out.println(-front.getVertex(i, 0) + "\t" + front.getVertex(i, 1));
		
		out.close();
	}
	
	private void loadInstance(List<ExperimentalData> ecaData, List<ExperimentalData> evmData, List<ExperimentalData> secaData, String name, int solutionSize) throws Exception
	{
		System.out.println("Processando '" + name + "' ...");
		
		MultiExperimentFileReader ecaReader = new MultiExperimentFileReader();
		MultiExperimentResult ecaResult = ecaReader.execute(DIRECTORY_ECA + name + "\\" + name + ".txt", 1, 30, 5, solutionSize);
		ecaResult.removeObjective(3);
		
		MultiExperimentFileReader evmReader = new MultiExperimentFileReader();
		MultiExperimentResult evmResult = evmReader.execute(DIRECTORY_EVM + name + "\\" + name + ".txt", 1, 30, 5, solutionSize);
		evmResult.removeObjective(3);
		
		MultiExperimentFileReader secaReader = new MultiExperimentFileReader();
		MultiExperimentResult secaResult = secaReader.execute(DIRECTORY_SECA + name + "\\" + name + ".txt", 1, 30, 4, solutionSize);
		
		ParetoFront bestFront = ecaResult.getInstanceIndex(0).getBestFront().clone();
		bestFront.merge(evmResult.getInstanceIndex(0).getBestFront());
		bestFront.merge(secaResult.getInstanceIndex(0).getBestFront());
		
		/*
		ecaData.add(new ExperimentalData(name, "errt", ecaResult.getInstanceIndex(0).getErrorRatios(bestFront)));
		ecaData.add(new ExperimentalData(name, "gdst", ecaResult.getInstanceIndex(0).getGenerationalDistances(bestFront)));
		ecaData.add(new ExperimentalData(name, "sprd", ecaResult.getInstanceIndex(0).getSpreads(bestFront)));
		
		evmData.add(new ExperimentalData(name, "errt", evmResult.getInstanceIndex(0).getErrorRatios(bestFront)));
		evmData.add(new ExperimentalData(name, "gdst", evmResult.getInstanceIndex(0).getGenerationalDistances(bestFront)));
		evmData.add(new ExperimentalData(name, "sprd", evmResult.getInstanceIndex(0).getSpreads(bestFront)));
		
		secaData.add(new ExperimentalData(name, "errt", secaResult.getInstanceIndex(0).getErrorRatios(bestFront)));
		secaData.add(new ExperimentalData(name, "gdst", secaResult.getInstanceIndex(0).getGenerationalDistances(bestFront)));
		secaData.add(new ExperimentalData(name, "sprd", secaResult.getInstanceIndex(0).getSpreads(bestFront)));
		*/
		
//		ecaData.add(new ExperimentalData(name, "eca_md", ecaResult.getInstanceIndex(0).getMaximumDistances(secaResult.getInstanceIndex(0).getBestFront())));
//		ecaData.add(new ExperimentalData(name, "evm_md", evmResult.getInstanceIndex(0).getMaximumDistances(secaResult.getInstanceIndex(0).getBestFront())));
		
		/*
		saveBestFront(name, "eca", ecaResult.getInstanceIndex(0).getBestFront());
		saveBestFront(name, "evm", evmResult.getInstanceIndex(0).getBestFront());
		saveBestFront(name, "seca", secaResult.getInstanceIndex(0).getBestFront());
		saveBestFront(name, "best", bestFront);
		*/

		//publishEvolutionQualityIndicators(name, bestFrontier);
	}

	public static void main(String[] args) throws Exception
	{
		MainProgram mp = new MainProgram();
		List<ExperimentalData> ecaData = new ArrayList<ExperimentalData>();
		List<ExperimentalData> evmData = new ArrayList<ExperimentalData>();
		List<ExperimentalData> secaData = new ArrayList<ExperimentalData>();

		mp.loadInstance(ecaData, evmData, secaData, "JungGraph", 37);
		mp.loadInstance(ecaData, evmData, secaData, "javacc", 154);
		mp.loadInstance(ecaData, evmData, secaData, "junit", 100);
		mp.loadInstance(ecaData, evmData, secaData, "servlet", 63);
		mp.loadInstance(ecaData, evmData, secaData, "jmetal", 190);
		mp.loadInstance(ecaData, evmData, secaData, "xmlapi", 184);
		mp.loadInstance(ecaData, evmData, secaData, "xmldom", 119);
		mp.loadInstance(ecaData, evmData, secaData, "jxlscore", 83);
		mp.loadInstance(ecaData, evmData, secaData, "javaocr", 59);
		mp.loadInstance(ecaData, evmData, secaData, "jodamoney", 26);
		mp.loadInstance(ecaData, evmData, secaData, "jpassword", 96);
		mp.loadInstance(ecaData, evmData, secaData, "jxlsreader", 27);
		mp.loadInstance(ecaData, evmData, secaData, "seemp", 31);
		mp.loadInstance(ecaData, evmData, secaData, "tinytim", 134);

		Script script = new Script("script.r");
		script.writeDataFile("eca.data", ecaData, 30);
		script.writeDataFile("evm.data", evmData, 30);
		script.writeDataFile("seca.data", secaData, 30);
		
		script.loadFile("eca", RESULT_DIRECTORY + "eca.data");
		script.loadFile("evm", RESULT_DIRECTORY + "evm.data");
		script.loadFile("seca", RESULT_DIRECTORY + "seca.data");

		//script.drawBoxPlot(ecaData, "errt", "eca", "Error Ratio");
		//script.drawBoxPlot(ecaData, "gdst", "eca", "Generational Distance");
		//script.drawBoxPlot(ecaData, "sprd", "eca", "Error Ratio");
		
		script.tableMannWhitney(ecaData, "errt", "eca", "seca");
		script.tableMannWhitney(ecaData, "gdst", "eca", "seca");
		script.tableMannWhitney(ecaData, "sprd", "eca", "seca");
		
		script.tableMannWhitney(ecaData, "errt", "evm", "seca");
		script.tableMannWhitney(ecaData, "gdst", "evm", "seca");
		script.tableMannWhitney(ecaData, "sprd", "evm", "seca");
		
		script.tableMannWhitney(ecaData, "errt", "eca", "evm");
		script.tableMannWhitney(ecaData, "gdst", "eca", "evm");
		script.tableMannWhitney(ecaData, "sprd", "eca", "evm");
		
		script.tableEffectSize(ecaData, "errt", "eca", "seca");
		script.tableEffectSize(ecaData, "gdst", "eca", "seca");
		script.tableEffectSize(ecaData, "sprd", "eca", "seca");

		script.tableEffectSize(ecaData, "errt", "evm", "seca");
		script.tableEffectSize(ecaData, "gdst", "evm", "seca");
		script.tableEffectSize(ecaData, "sprd", "evm", "seca");

		script.tableEffectSize(ecaData, "errt", "eca", "evm");
		script.tableEffectSize(ecaData, "gdst", "eca", "evm");
		script.tableEffectSize(ecaData, "sprd", "eca", "evm");

		script.tableSummary(ecaData, "errt", "eca");
		script.tableSummary(ecaData, "errt", "evm");
		script.tableSummary(ecaData, "errt", "seca");
		
		script.tableSummary(ecaData, "gdst", "eca");
		script.tableSummary(ecaData, "gdst", "evm");
		script.tableSummary(ecaData, "gdst", "seca");
		
		script.tableSummary(ecaData, "sprd", "eca");
		script.tableSummary(ecaData, "sprd", "evm");
		script.tableSummary(ecaData, "sprd", "seca");
		
		script.sendCommand("\npvalues <- data.frame(name=names, timemb=pvalue_eca_seca_time, timeeb=pvalue_evm_seca_time, timeme=pvalue_eca_evm_time, errtmb=pvalue_eca_seca_errt, errteb=pvalue_evm_seca_errt, errtme=pvalue_eca_evm_errt, gdstmb=pvalue_eca_seca_gdst, gdsteb=pvalue_evm_seca_gdst, gdstme=pvalue_eca_evm_gdst)");
		script.sendCommand("write.csv(pvalues, quote=FALSE, file=\"/Users/Marcio Barros/Desktop/Codigos/Pesquisa/GAClustering/pvalues.csv\")");

		script.sendCommand("\neffectsizes <- data.frame(name=names, errtmb=effectsize1_eca_seca_errt, errteb=effectsize1_evm_seca_errt, errtme=effectsize1_eca_evm_errt, gdstmb=effectsize1_eca_seca_gdst, gdsteb=effectsize1_evm_seca_gdst, gdstme=effectsize1_eca_evm_gdst)");
		script.sendCommand("write.csv(effectsizes, quote=FALSE, file=\"/Users/Marcio Barros/Desktop/Codigos/Pesquisa/GAClustering/effectsizes.csv\")");

		script.sendCommand("means <- data.frame(name=names, mtmq=mean_eca_time, mtevm=mean_evm_time, mtbasic=mean_seca_time, memq=mean_eca_errt, meevm=mean_evm_errt, mebasic=mean_seca_errt, mgmq=mean_eca_gdst, mgevm=mean_evm_gdst, mgbasic=mean_seca_gdst)");
		script.sendCommand("write.csv(means, quote=FALSE, file=\"/Users/Marcio Barros/Desktop/Codigos/Pesquisa/GAClustering/means.csv\")");

		script.sendCommand("sds <- data.frame(name=names, stmq=sd_eca_time, stevm=sd_evm_time, stbasic=sd_seca_time, semq=sd_eca_errt, seevm=sd_evm_errt, sebasic=sd_seca_errt, sgmq=sd_eca_gdst, sgevm=sd_evm_gdst, sgbasic=sd_seca_gdst)");
		script.sendCommand("write.csv(sds, quote=FALSE, file=\"/Users/Marcio Barros/Desktop/Codigos/Pesquisa/GAClustering/sds.csv\")");
		
		script.close();
	}
}

class ExperimentGenerationFileReader extends GenerationFileReader
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