package unirio.experiments.multiobjective.analysis.model;

import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Classe que representa um v�rtice em uma frente de Pareto
 * 
 * @author Marcio Barros
 */
public class ParetoFrontVertex
{
	private int location;
	private double[] objectives;
	private int[] solution;
	
	/**
	 * Inicializa o v�rtice, indicando o n�mero de objetivos e o tamanho da solu��o
	 */
	public ParetoFrontVertex(int objectiveCount, int solutionSize)
	{
		this.location = 0;
		this.objectives = new double[objectiveCount];
		this.solution = new int[solutionSize];
	}
	
	/**
	 * Retorna o n�mero da itera��o em que a solu��o foi encontrada 
	 */
	public int getLocation()
	{
		return location;
	}

	/**
	 * Altera o n�mero da itera��o em que a solu��o foi encontrada 
	 */
	public void setLocation(int location)
	{
		this.location = location;
	}

	/**
	 * Retorna os objetivos registrados no v�rtice
	 */
	public double[] getObjectives()
	{
		return objectives;
	}
	
	/**
	 * Retorna um objetivo registrado no v�rtice, dado seu �ndice
	 */
	public double getObjective(int index)
	{
		return objectives[index];
	}
	
	/**
	 * Altera os objetivos registrados no v�rtice
	 */
	public void setObjectives(double[] values)
	{
		for (int i = 0; i < this.objectives.length; i++)
			this.objectives[i] = values[i];
	}
	
	/**
	 * Altera um objetivo registrado no v�rtice, dado seu �ndice e novo valor
	 */
	public void setObjective(int index, double value)
	{
		this.objectives[index] = value;
	}

	/**
	 * Retorna todos os valores da solu��o representada no v�rtice
	 */
	public int[] getSolution()
	{
		return solution;
	}
	
	/**
	 * Retorna um valor da solu��o representada no v�rtice, dado seu �ndice 
	 */
	public int getSolution(int index)
	{
		return solution[index];
	}

	/**
	 * Altera todos os valores da solu��o representada pelo v�rtice
	 */
	public void setSolution(int[] values)
	{
		for (int i = 0; i < this.solution.length; i++)
			this.solution[i] = values[i];
	}
	
	/**
	 * Altera um valor da solu��o representada pelo v�rtice
	 */
	public void setSolution(int index, int value)
	{
		this.solution[index] = value;
	}
	
	/**
	 * Verifica se um v�rtice � id�ntico a outro recebido como par�metro
	 */
	public boolean sameVertex(ParetoFrontVertex vertex)
	{
		for (int i = 0; i < objectives.length; i++)
			if (Math.abs(objectives[i] - vertex.objectives[i]) > 0.001)
				return false;

		return true;
	}

	/**
	 * Gera uma c�pia do v�rtice
	 */
	public ParetoFrontVertex clone()
	{
		ParetoFrontVertex vertex = new ParetoFrontVertex(objectives.length, solution.length);
		vertex.setObjectives(objectives);
		vertex.setLocation(location);
		vertex.setSolution(solution);
		return vertex;
	}

	/**
	 * Remove um objetivo do v�rtice, dado seu �ndice
	 */
	public void removeObjective(int index)
	{
		double[] localObjectives = new double[objectives.length-1];
		int walker = 0;
		
		for (int i = 0; i < index; i++)
			localObjectives[walker++] = objectives[i];

		for (int i = index+1; i < objectives.length; i++)
			localObjectives[walker++] = objectives[i];
		
		this.objectives = localObjectives;
	}

	/**
	 * Apresenta o v�rtice em um stream
	 */
	public void print (OutputStreamWriter stream) throws IOException
	{
		stream.write("" + objectives[0]);

		for (int i = 1; i < objectives.length; i++)
			stream.write(";" + objectives[i]);
		
		stream.write("\n");
		stream.flush();
	}

	/**
	 * Publica o v�rtice em uma string
	 */
	public String toString()
	{
		String s = "[" + objectives[0];

		for (int i = 1; i < objectives.length; i++)
			s += "; " + objectives[i];
		
		return s + "]";
	}
}