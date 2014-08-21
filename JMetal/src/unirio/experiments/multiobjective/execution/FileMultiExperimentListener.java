package unirio.experiments.multiobjective.execution;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.Vector;

import jmetal.base.Solution;
import jmetal.base.Variable;

/**
 * Interface que representa os eventos do ciclo de vida de um experimento
 * 
 * @author Marcio Barros
 */
public class FileMultiExperimentListener implements MultiExperimentListener
{
	static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	protected OutputStreamWriter writer;
	
	private boolean showSolutionDetails;

	/**
	 * Inicializa o analisador de experimentos que publica resultados em arquivos
	 * 
	 * @param filename		Nome do arquivo que ser� usado como resultado
	 */
	public FileMultiExperimentListener(String filename) throws FileNotFoundException
	{
		FileOutputStream outputStream = new FileOutputStream(filename);
		this.writer = new OutputStreamWriter(outputStream);
		this.showSolutionDetails = false;
	}
	
	/**
	 * Inicializa o analisador de experimentos que publica resultados em arquivos
	 * 
	 * @param writer		Nome do escritor onde os dados ser�o gravados
	 */
	public FileMultiExperimentListener(OutputStreamWriter writer) throws FileNotFoundException
	{
		this.writer = writer;
		this.showSolutionDetails = false;
	}

	/**
	 * Inicializa o analisador de experimentos que publica resultados em arquivos
	 * 
	 * @param filename		Nome do arquivo que ser� usado como resultado
	 */
	public FileMultiExperimentListener(String filename, boolean details) throws FileNotFoundException
	{
		FileOutputStream outputStream = new FileOutputStream(filename);
		this.writer = new OutputStreamWriter(outputStream);
		this.showSolutionDetails = details;
	}
	
	/**
	 * Inicializa o analisador de experimentos que publica resultados em arquivos
	 * 
	 * @param writer		Nome do escritor onde os dados ser�o gravados
	 */
	public FileMultiExperimentListener(OutputStreamWriter writer, boolean details) throws FileNotFoundException
	{
		this.writer = writer;
		this.showSolutionDetails = details;
	}
	
	/**
	 * Imprime uma string no resultado
	 * 
	 * @param s		String que ser� impressa
	 */
	private void print(String s) throws IOException
	{
		writer.write(s);
		writer.flush();
	}

	/**
	 * Imprime uma string no resultado, saltando de linha
	 * 
	 * @param s		String que ser� impressa
	 */
	protected void println(String s) throws IOException
	{
		print(s + LINE_SEPARATOR);
	}
	
	/**
	 * Retorna os dados relevantes de uma solu��o para uma fronteira eficiente
	 * 
	 * @param solution		Solu��o sendo analisada
	 */
	protected double[] getParetoFrontierData(int instanceNumber, Solution solution)
	{
		double[] data = new double[solution.numberOfObjectives()];
		
		for (int i = 0; i < solution.numberOfObjectives(); i++)
			data[i] = solution.getObjective(i);
		
		return data;
	}
	
	/**
	 * Apresenta uma solucao da fronteira eficiente
	 * 
	 * @param s		Solucao que sera apresentada
	 */
	private void printSolution(Solution s) throws Exception
	{
		Variable[] variables = s.getDecisionVariables();
		print(" [");
		
		if (variables.length > 0)
			print(variables[0].toString());
		
		for(int i = 1; i < variables.length; i++)
			print(" " + variables[i].toString());

		print("]");
	}
	
	/**
	 * Apresenta uma fronteira eficiente
	 * 
	 * @param frontier		Lista de solu��es componentes da fronteira
	 */
	private void printParetoFrontier(int instanceNumber, Vector<Solution> frontier) throws Exception
	{
		DecimalFormat dc = new DecimalFormat("0.####");
	
		for (int j = 0; j < frontier.size(); j++)
		{
			Solution solution = frontier.get(j);
			double[] data = getParetoFrontierData(instanceNumber, solution);
			
			if (data != null && data.length > 0)
			{
				print(dc.format (data[0]));
				
				for (int k = 1; k < data.length; k++)
					print("; " + dc.format(data[k]));
			}
			
			if (showSolutionDetails)
			{
				print("; " + solution.getLocation() + "; ");
				printSolution(solution);
			}
			
			println("");
		}
	}

	/**
	 * Prepara o experimento
	 */
	public void prepareExperiment()
	{
	}

	/**
	 * Termina o experimento
	 */
	public void terminateExperiment() throws Exception
	{
		writer.close();
	}

	/**
	 * Prepara uma inst�ncia
	 */
	public void prepareInstance(int instanceNumber) throws IOException
	{
		if (instanceNumber > 0)
			println("");
		
		println("=============================================================");
		println("Instance #" + instanceNumber);
		println("=============================================================");
	}

	/**
	 * Termina uma inst�ncia
	 */
	public void terminateInstance(int instanceNumber, Vector<Solution> instanceFrontier) throws Exception
	{
		println("");
		println("Final Pareto Frontier:");
		printParetoFrontier(instanceNumber, instanceFrontier);
	}

	/**
	 * Publica os resultados de um ciclo
	 */
	public void publishCycle(int cycleNumber, int instanceNumber, long executionTime, Vector<Solution> cycleFrontier, Vector<Solution> instanceFrontier) throws Exception
	{
		println("");
		println("Cycle #" + cycleNumber + " (" + executionTime + " ms; " + instanceFrontier.size() + " best solutions)");
		printParetoFrontier(instanceNumber, cycleFrontier);
	}
}