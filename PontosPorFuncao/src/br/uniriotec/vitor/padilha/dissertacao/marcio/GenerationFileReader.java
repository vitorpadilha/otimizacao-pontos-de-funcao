package br.uniriotec.vitor.padilha.dissertacao.marcio;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;

import unirio.experiments.multiobjective.analysis.model.ParetoFront;
import unirio.experiments.multiobjective.analysis.model.ParetoFrontVertex;

public abstract class GenerationFileReader
{
	public double[][] execute (String directory, String instanceName, int dataCount, int generationCount, int cycleCount, int objectiveCount, int removeObjective, ParetoFront bestFront) throws Exception
	{
		double[][] result = new double[cycleCount][generationCount * dataCount];
		System.out.print("Processando " + instanceName + " ");
		
		for (int i = 0; i < cycleCount; i++)
		{
			String filename = directory + instanceName + "\\" + instanceName + "_" + i + ".txt";
			System.out.print(i % 10);
			
			FileInputStream fis = new FileInputStream(filename);
			execute(fis, dataCount, generationCount, i, objectiveCount, removeObjective, bestFront, result);
			fis.close();
		}

		System.out.println();
		return result;
	}

	public void execute (InputStream stream, int dataCount, int generationCount, int cycle, int objectiveCount, int removeObjective, ParetoFront bestFront, double[][] result) throws Exception
	{
		Scanner scanner = new Scanner(stream);
		
		try
		{
			readLine(scanner);
			
			for (int i = 0; i < generationCount; i++)
			{			
				String line = readLine(scanner);
				
				if (!line.startsWith("Generation #"))
					throw new Exception("expected Generation #");
				
				ParetoFront front = new ParetoFront(objectiveCount, 0);
				readParetoFrontier(front, objectiveCount, scanner);
				
				if (removeObjective != -1)
					front.removeObjective(removeObjective);
				
				double[] data = new double[dataCount];
				calculateValue(front, bestFront, data);

				for (int j = 0; j < dataCount; j++)
					result[cycle][i * dataCount + j] = data[j];
			}
		}
		finally
		{
			scanner.close();
		}
	}

	protected abstract void calculateValue(ParetoFront front, ParetoFront bestFront, double[] data);
	
	private void readParetoFrontier(ParetoFront front, int objectiveCount, Scanner scanner) throws Exception
	{
		String s = readLine(scanner);
		
		while (s.length() > 0)
		{
			ParetoFrontVertex vertex = new ParetoFrontVertex(objectiveCount, 0);

			String[] tokens = s.split(";");
			
			if (tokens.length != objectiveCount+2)
				throw new Exception("number of tokens in Pareto frontier entry different from expected");
			
			for (int i = 0; i < objectiveCount; i++)
			{
				double value = parseDouble(tokens[i]);
				vertex.setObjective(i, value);
			}

			front.addVertex(vertex);
			s = (scanner.hasNextLine()) ? readLine(scanner) : "";
		}
	}

	private String readLine (Scanner scanner)
	{
		String result = scanner.nextLine();
		return result;
	}

	private double parseDouble (String s) throws Exception
	{
		try
		{
			s = s.replace(',', '.');
			return Double.parseDouble(s.trim());
		}
		catch(Exception e)
		{
			throw new Exception ("invalid double value");
		}
	}
}