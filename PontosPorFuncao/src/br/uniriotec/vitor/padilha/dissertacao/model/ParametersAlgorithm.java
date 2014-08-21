package br.uniriotec.vitor.padilha.dissertacao.model;

public class ParametersAlgorithm {

	double multiplicadorPopulacao;
	long numeroMaximoDeAvaliacoes;
	double crossoverProbability;
	double mutationProbability;
	int numeroDeTransacoes;
	
	
	public long getNumeroMaximoDeAvaliacoes() {
		return numeroMaximoDeAvaliacoes;
	}
	public void setNumeroMaximoDeAvaliacoes(long numeroMaximoDeAvaliacoes) {
		this.numeroMaximoDeAvaliacoes = numeroMaximoDeAvaliacoes;
	}
	public double getCrossoverProbability() {
		return crossoverProbability;
	}
	public void setCrossoverProbability(double crossoverProbability) {
		this.crossoverProbability = crossoverProbability;
	}
	public double getMutationProbability() {
		return mutationProbability;
	}
	public void setMutationProbability(double mutationProbability) {
		this.mutationProbability = mutationProbability;
	}
	public int getNumeroDeTransacoes() {
		return numeroDeTransacoes;
	}
	public void setNumeroDeTransacoes(int numeroDeTransacoes) {
		this.numeroDeTransacoes = numeroDeTransacoes;
	}
	public double getMultiplicadorPopulacao() {
		return multiplicadorPopulacao;
	}
	public void setMultiplicadorPopulacao(double multiplicadorPopulacao) {
		this.multiplicadorPopulacao = multiplicadorPopulacao;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Número Máximo de Avaliações: "+numeroMaximoDeAvaliacoes+" | Multiplicador População (x |T|):"+multiplicadorPopulacao+" | Mutação:"+ mutationProbability+" | CrossOver:"+crossoverProbability;
	}
}
