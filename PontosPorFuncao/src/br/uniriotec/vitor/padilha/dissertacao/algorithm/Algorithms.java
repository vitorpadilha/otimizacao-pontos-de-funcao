package br.uniriotec.vitor.padilha.dissertacao.algorithm;


public enum Algorithms {
	HILL_CLIMBING(true,"Hill Climbing"),
	GENETIC(true,"Gen�tico"),
	RANDOM(true,"Aleat�rio"),
	NSGAII(false,"NSGAII"),
	MOCELL(false,"MOCELL"),
	SPEA2(false,"SPEA2"),
	PAES(false,"PAES"),
	ALEAT�RIO(false,"Aleat�rio"),
	VARRE_FLAGS(true,"JOFA"),
	FUNCTIONPOINTGA(true,"FuntionPointGA"),
	NEWHC(true,"NEWHC"),
	FUNCTIONPOINTHC(true,"FuntionPointHC");
	
	private Algorithms( boolean monoobjective, String name) {
		this.monoobjective = monoobjective;
		this.name = name;
	}

	private boolean monoobjective;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isMonoobjective() {
		return monoobjective;
	}

	public void setMonoobjective(boolean monoobjective) {
		this.monoobjective = monoobjective;
	}
}
