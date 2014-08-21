package br.uniriotec.vitor.padilha.dissertacao.experiment;

import java.util.Map;
import java.util.Vector;

import jmetal.base.Solution;

public interface IFunctionsPointExperiment<InstanceClass> {

	Map<Integer, Map<Integer, Vector<Solution>>> getSolutions();
	
	void run2(Vector<InstanceClass> instances, int cycles) throws Exception;
}
