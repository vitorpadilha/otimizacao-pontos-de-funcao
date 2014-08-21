package unirio.experiments.monoobjective.execution;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import jmetal.base.Solution;
import jmetal.base.Variable;

/**
 * Interface que publica os eventos do ciclo de vida de um experimento em um arquivo
 * 
 * @author Marcio Barros
 */
public class StreamMonoExperimentListener implements MonoExperimentListener
{
	static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private OutputStreamWriter writer;
	private boolean showSolutionDetails;

	/**
	 * Inicializa o analisador de experimentos que publica resultados em arquivos
	 * 
	 * @param writer		Nome do escritor onde os dados serão gravados
	 */
	public StreamMonoExperimentListener(OutputStreamWriter writer)
	{
		this(writer, false);
	}

	/**
	 * Inicializa o analisador de experimentos que publica resultados em arquivos
	 * 
	 * @param writer		Nome do escritor onde os dados serão gravados
	 */
	public StreamMonoExperimentListener(OutputStreamWriter writer, boolean details)
	{
		this.writer = writer;
		this.showSolutionDetails = details;
	}
	
	/**
	 * Retorna o stream de saída
	 */
	protected OutputStreamWriter getWriter()
	{
		return writer;
	}
	
	/**
	 * Imprime uma string no resultado
	 * 
	 * @param s		String que será impressa
	 */
	protected void print(String s) throws IOException
	{
		writer.write(s);
		writer.flush();
	}

	/**
	 * Imprime uma string no resultado, saltando de linha
	 * 
	 * @param s		String que será impressa
	 */
	protected void println(String s) throws IOException
	{
		print(s + LINE_SEPARATOR);
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
	}

	/**
	 * Prepara uma instância
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
	 * Termina uma instância
	 */
	public void terminateInstance(int instanceNumber) throws Exception
	{
		println("");
	}

	/**
	 * Publica os resultados de um ciclo
	 */
	@Override
	public void publishCycle(int cycleNumber, int instanceNumber, Solution solution, long executionTime, double[] data) throws Exception
	{
		//println("");
		print("Cycle #" + cycleNumber + "; " + executionTime);

		DecimalFormat dc = new DecimalFormat("0.####");
		
		for (int k = 0; k < data.length; k++)
			print("; " + dc.format(data[k]));
		
		if (showSolutionDetails)
		{
			print("; " + solution.getLocation() + "; ");
			printSolution(solution);
		}
		
		println("");
	}
}