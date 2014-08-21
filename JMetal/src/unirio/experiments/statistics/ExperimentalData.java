package unirio.experiments.statistics;

public class ExperimentalData
{
	private String name;
	private String group;
	private double[] data;
	
	public ExperimentalData(String name, String group, double[] data)
	{
		this.name = name;
		this.group = group;
		this.data = data;
	}
	
	public String getName()
	{
		return name;
	}

	public String getGroup()
	{
		return group;
	}
	
	public double[] getData()
	{
		return data;
	}
}