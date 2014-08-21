package unirio.experiments.multiobjective.execution;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.Vector;

import jmetal.base.Algorithm;
import jmetal.base.Solution;
import jmetal.base.SolutionSet;
import jmetal.base.Variable;
import jmetal.util.JMException;

@SuppressWarnings("unused")
public abstract class MultiExperiment<InstanceClass>
{
	private static int NON_DOMINATING = 0;
	private static int DOMINATED = 1;
	private static int DOMINATOR = 2;
	
	protected Vector<MultiExperimentListener> listeners;

	/**
	 * Inicializa um experimento
	 */
	public MultiExperiment()
	{
		this.listeners = new Vector<MultiExperimentListener>();
	}
	
	/**
	 * Adiciona um listener no experimento
	 */
	public void addListerner(MultiExperimentListener listener)
	{
		listeners.add(listener);
	}
	
	/**
	 * Prepara a execu��o do experimento
	 */
	public void prepareExperiment() throws Exception
	{
		for (MultiExperimentListener listener : listeners)
			listener.prepareExperiment();
	}

	/**
	 * Termina a execu��o do experimento
	 */
	public void terminateExperiment() throws Exception
	{
		for (MultiExperimentListener listener : listeners)
			listener.terminateExperiment();
	}

	/**
	 * Prepara a execu��o de uma inst�ncia
	 * 
	 * @param instanceNumber	N�mero da inst�ncia
	 */
	public void prepareInstance(int instanceNumber) throws Exception
	{
		for (MultiExperimentListener listener : listeners)
			listener.prepareInstance(instanceNumber);
	}

	/**
	 * Termina a execu��o de uma inst�ncia
	 * 
	 * @param instanceNumber		N�mero de inst�ncia
	 * @param instanceFrontier		Fronteira eficiente da inst�ncia (final)
	 */
	public void terminateInstance(int instanceNumber, Vector<Solution> instanceFrontier) throws Exception
	{
		for (MultiExperimentListener listener : listeners)
			listener.terminateInstance(instanceNumber, instanceFrontier);
	}

	/**
	 * Publica os resultados de um ciclo
	 * 
	 * @param cycleNumber		N�mero do ciclo
	 * @param executionTime		Tempo de execu��o do ciclo
	 * @param cycleFrontier		Fronteira eficiente do ciclo
	 * @param instanceFrontier	Fronteira eficiente da inst�ncia (atual)
	 */
	public void publishCycle(int cycleNumber, int instanceNumber, long executionTime, Vector<Solution> cycleFrontier, Vector<Solution> instanceFrontier) throws Exception
	{
		for (MultiExperimentListener listener : listeners)
			listener.publishCycle(cycleNumber, instanceNumber, executionTime, cycleFrontier, instanceFrontier);
	}
	
	/**
	 * Verifica se duas solu��es s�o id�nticas
	 * 
	 * @param solution1		Primeira solu��o
	 * @param solution2		Segunda solu��o
	 */
	private boolean identicalSolutions(Solution solution1, Solution solution2) throws JMException
	{
		Variable[] variables1 = solution1.getDecisionVariables();
		Variable[] variables2 = solution2.getDecisionVariables();
		
		if (variables1.length != variables2.length)
			return false;
		
		for (int i = 0; i < variables1.length; i++)
		{
			Variable v1 = variables1[i];
			Variable v2 = variables2[i];
			
			if (v1.getValue() != v2.getValue())
				return false;
		}
		
		return true;
	}

	/**
	 * Verifica se duas solu��es tem os mesmos objetivos
	 * 
	 * @param solution1		Primeira solu��o
	 * @param solution2		Segunda solu��o
	 * @param tolerance		Diferen�a toler�vel entre os requisitos
	 */
	private boolean identicalObjectives(Solution solution1, Solution solution2, double tolerance)
	{
		if (solution1.numberOfObjectives() != solution2.numberOfObjectives())
			return false;
		
		for (int i = 0; i < solution1.numberOfObjectives(); i++)
		{
			double objective1 = solution1.getObjective(i);
			double objective2 = solution2.getObjective(i);
			
			if (Math.abs(objective1 - objective2) > tolerance)
				return false;
		}
		
		return true;
	}
	
	/**
	 * Verifica se uma lista de solu��es cont�m uma nova solu��o
	 * 
	 * @param solutions		Lista de solu��es
	 * @param newSolution	Nova solu��o que ser� verificada
	 */
	private boolean containsIdenticalSolution(Vector<Solution> solutions, Solution newSolution) throws JMException
	{
		for (int i = 0; i < solutions.size(); i++)
		{
			Solution current = solutions.get(i);
			
			if (identicalSolutions(current, newSolution))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Verifica se uma lista de solu��es cont�m uma solu��o com os mesmos objetivos de uma nova
	 * 
	 * @param solutions		Lista de solu��es
	 * @param newSolution	Nova solu��o que ser� verificada
	 * @param tolerance		Diferen�a toler�vel entre os requisitos
	 */
	private boolean checkDuplicateObjectives(Vector<Solution> solutions, Solution newSolution, double tolerance)
	{
		for (int i = 0; i < solutions.size(); i++)
		{
			Solution current = solutions.get(i);
			
			if (identicalObjectives(current, newSolution, tolerance))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Retorna o conjunto de solu��es �nicas de um resultado
	 * 
	 * @param result		Conjunto de solu��es
	 */
	private Vector<Solution> getUniqueSolutions(SolutionSet result) throws JMException
	{
		Vector<Solution> uniqueSolutions = new Vector<Solution>();

		for (int i = 0; i < result.size(); i++)
		{
			Solution solution = result.get(i);
			
			if (!containsIdenticalSolution(uniqueSolutions, solution))
				uniqueSolutions.add(solution);
		}
		
		return uniqueSolutions;
	}
	
	/**
	 * Retorna o conjunto de solu��es com objetivos �nicos
	 * 
	 * @param result		Conjunto de solu��es
	 * @param tolerance		Diferen�a toler�vel entre os requisitos
	 */
	public Vector<Solution> getUniqueObjectives(SolutionSet result, double tolerance)
	{
		Vector<Solution> unique = new Vector<Solution>();

		for (int i = 0; i < result.size(); i++)
		{
			Solution solution = result.get(i);
			
			if (!checkDuplicateObjectives(unique, solution, tolerance))
				unique.add(solution);
		}
		
		return unique;
	}

	/**
	 * Verifica a domina��o entre duas solu��es
	 * 
	 * @param current		Solu��o atual
	 * @param newSolution	Nova solu��o
	 */
	private int checkDomination (Solution current, Solution newSolution)
	{
		boolean currentAlwaysDominated = true;
		boolean newAlwaysDominated = true;
		
		for (int i = 0; i < current.numberOfObjectives(); i++)
		{
			double currentObjective = current.getObjective(i);
			double newObjective = newSolution.getObjective(i);
			double difference = currentObjective - newObjective;
		
			if (difference < -0.001)
				newAlwaysDominated = false;

			if (difference > 0.001)
				currentAlwaysDominated = false;
				
			if (!currentAlwaysDominated && !newAlwaysDominated)
				return NON_DOMINATING;
		}
				
		if (currentAlwaysDominated)
			return DOMINATED;
				
		return DOMINATOR;
	} 

	/**
	 * Agrupa duas fronteiras eficientes, segundo seus objetivos
	 * 
	 * @param frontier1		Primeira fronteira eficiente
	 * @param frontier2		Segunda fronteira eficiente
	 */
	public void mergeFrontiers(Vector<Solution> frontier1, Vector<Solution> frontier2)
	{
		for (int i = 0; i < frontier2.size(); i++)
		{
			Solution newSolution = frontier2.get(i);
			int newStatus = NON_DOMINATING;
			
			for (int j = frontier1.size()-1; j >= 0; j--)
			{
				Solution current = frontier1.get(j);
				int status = checkDomination(current, newSolution);
				
				if (status == DOMINATOR)
					frontier1.remove(j);
				
				if (status == DOMINATED)
					newStatus = DOMINATED;
			}
				
			if (newStatus != DOMINATED)
				frontier1.add(newSolution);
		}
	}

	/**
	 * Executa um ciclo do algoritmo sobre uma inst�ncia
	 *
	 * @param instance			Inst�ncia que ser� executada
	 * @param instanceNumber	N�mero da inst�ncia que ser� executada
	 */
	public abstract SolutionSet runCycle(InstanceClass instance, int instanceNumber) throws Exception;
	
	/**
	 * Executa todos os ciclos do experimento para uma inst�ncia
	 * 
	 * @param instance			Inst�ncia que ser� tratada nesta rodada
	 * @param instanceNumber	N�mero da inst�ncia
	 * @param cycles			N�mero de ciclos que ser�o executados
	 */
	public void runCycles(InstanceClass instance, int instanceNumber, int cycles) throws Exception
	{
		prepareInstance(instanceNumber);
		Vector<Solution> instanceFrontier = new Vector<Solution>();
		
		for (int i = 0; i < cycles; i++)
		{
			long initTime = System.currentTimeMillis();
			SolutionSet result = runCycle(instance, instanceNumber);
			long executionTime = System.currentTimeMillis() - initTime;
			
			Vector<Solution> cycleFrontier = getUniqueObjectives(result, 0.001);
			mergeFrontiers(instanceFrontier, cycleFrontier);
			publishCycle(i, instanceNumber, executionTime, cycleFrontier, instanceFrontier);
		}
		
		terminateInstance(instanceNumber, instanceFrontier);
	}

	/**
	 * Roda o experimento para todas as inst�ncias
	 * 
	 * @param instances		Vetor de inst�ncias
	 * @param cycles		N�mero de ciclos que ser�o executados
	 */
	public void run(Vector<InstanceClass> instances, int cycles) throws Exception
	{
		prepareExperiment();

		for (int i = 0; i < instances.size(); i++)
		{
			InstanceClass instance = instances.get(i);
			runCycles(instance, i, cycles);
		}

		terminateExperiment();
	}
}