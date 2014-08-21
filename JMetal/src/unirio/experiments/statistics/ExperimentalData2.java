package unirio.experiments.statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExperimentalData2
{
	private String name;
	private String algorithmName;
	private String instanceName;
	private Integer executionNumber;
	private Long maxEvaluations;
	private String approach;
	private Double crossover;
	private Double mutation;
	private Double population;
	private List<CycleInfos> data;
	

	public List<CycleInfos> getData()
	{
		return data;
	}

	public String getApproach()
	{
		return approach;
	}

	public void setApproach(String approach)
	{
		this.approach = approach;
	}

	public ExperimentalData2(String name,  String algorithmName, String instanceName, Integer executionNumber, Long maxEvaluations, String approach)
	{
		this.name = name;
		this.instanceName = instanceName;
		this.algorithmName = algorithmName;
		this.data = new ArrayList<CycleInfos>();
		this.executionNumber = executionNumber;
		this.maxEvaluations = maxEvaluations;
		this.approach = approach;
	}

	public void addData(int cycleNumber, Map<String, Double[]> data) {
		this.data.add(new CycleInfos(cycleNumber, data));
	}
	
	public String getAlgorithmName()
	{
		return algorithmName;
	}

	public String getInstanceName()
	{
		return instanceName;
	}
	
	public class CycleInfos {
		private CycleInfos(int cycleNumber, Map<String, Double[]> infoValue)
		{
			super();
			this.cycleNumber = cycleNumber;
			this.infoValue = infoValue;
		}

		private int cycleNumber;
		
		private Map<String, Double[]> infoValue;

		public int getCycleNumber()
		{
			return cycleNumber;
		}

		public void setCycleNumber(int cycleNumber)
		{
			this.cycleNumber = cycleNumber;
		}

		public Map<String, Double[]> getInfoValue()
		{
			return infoValue;
		}

		public void setInfoValue(Map<String, Double[]> infoValue)
		{
			this.infoValue = infoValue;
		}
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Integer getExecutionNumber()
	{
		return executionNumber;
	}

	public void setExecutionNumber(Integer executionNumber)
	{
		this.executionNumber = executionNumber;
	}

	public Long getMaxEvaluations()
	{
		return maxEvaluations;
	}

	public void setMaxEvaluations(Long maxEvaluations)
	{
		this.maxEvaluations = maxEvaluations;
	}

	public Double getCrossover()
	{
		return crossover;
	}

	public void setCrossover(Double crossover)
	{
		this.crossover = crossover;
	}

	public Double getMutation()
	{
		return mutation;
	}

	public void setMutation(Double mutation)
	{
		this.mutation = mutation;
	}

	public Double getPopulation()
	{
		return population;
	}

	public void setPopulation(Double population)
	{
		this.population = population;
	}
}