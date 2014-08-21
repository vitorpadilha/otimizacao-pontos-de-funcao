package br.uniriotec.vitor.padilha.dissertacao.analysis;

import java.util.Map;

public class InstancesData {

	public InstancesData(String instanceName, String algorithmName, String metric, Double average, Double sd,
			Double min, Double max, Double kolmogorovSmirnov) {
		this.metric = metric;
		this.average = average;
		this.sd = sd;
		this.min = min;
		this.max = max;
		this.instanceName = instanceName;
		this.algorithmName = algorithmName;
		this.kolmogorovSmirnov = kolmogorovSmirnov;
	}

	private String metric;
	
	private String instanceName;
	
	private String algorithmName;
	
	private Double average;
	
	private Double kolmogorovSmirnov;
	
	public Double getKolmogorovSmirnov() {
		return kolmogorovSmirnov;
	}

	public void setKolmogorovSmirnov(Double kolmogorovSmirnov) {
		this.kolmogorovSmirnov = kolmogorovSmirnov;
	}

	private Double sd;
	
	private Double min;
	
	private Double max;
	
	private Map<String,Double> effectSizes;
	
	private Map<String,Double> pvalues;

	public Double getAverage() {
		return average;
	}

	public void setAverage(Double average) {
		this.average = average;
	}

	public Double getSd() {
		return sd;
	}

	public void setSd(Double sd) {
		this.sd = sd;
	}

	public Double getMin() {
		return min;
	}

	public void setMin(Double min) {
		this.min = min;
	}

	public Double getMax() {
		return max;
	}

	public void setMax(Double max) {
		this.max = max;
	}

	public Map<String, Double> getEffectSizes() {
		return effectSizes;
	}

	public void setEffectSizes(Map<String, Double> effectSizes) {
		this.effectSizes = effectSizes;
	}

	public Map<String, Double> getPvalues() {
		return pvalues;
	}

	public void setPvalues(Map<String, Double> pvalues) {
		this.pvalues = pvalues;
	}

	public String getMetric() {
		return metric;
	}

	public void setMetric(String metric) {
		this.metric = metric;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public String getAlgorithmName() {
		return algorithmName;
	}

	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}
}
