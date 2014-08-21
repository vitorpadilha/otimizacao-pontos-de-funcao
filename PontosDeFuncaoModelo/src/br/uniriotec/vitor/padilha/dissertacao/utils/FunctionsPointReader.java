package br.uniriotec.vitor.padilha.dissertacao.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import br.uniriotec.vitor.padilha.dissertacao.exception.CloneException;
import br.uniriotec.vitor.padilha.dissertacao.exception.ElementException;
import br.uniriotec.vitor.padilha.dissertacao.jaxb.MyValidationEventHandler;
import br.uniriotec.vitor.padilha.dissertacao.model.FunctionPointSystem;
import br.uniriotec.vitor.padilha.dissertacao.model.stakeholdersInterests.StakeholderInterests;

public class FunctionsPointReader {
	private String functionsPointFile;
	
	private static String stakeholdersInterestString;
	private static JAXBContext context;
	private static JAXBContext contextInterests;
	private static Unmarshaller unmarshallerFunctionsPoint;
	private static Marshaller marshallerFunctionsPoint;
	private static Unmarshaller unmarshallerStakeholderInterests;
	public FunctionsPointReader(String functionsPointFile, String stakeholdersInterestFile) throws JAXBException, IOException {	
			this.functionsPointFile = functionsPointFile;
			context = JAXBContext.newInstance(FunctionPointSystem.class);
			contextInterests = JAXBContext.newInstance(StakeholderInterests.class);		
			unmarshallerFunctionsPoint = context.createUnmarshaller();
			unmarshallerFunctionsPoint.setEventHandler(new MyValidationEventHandler());
			marshallerFunctionsPoint = context.createMarshaller();
			unmarshallerStakeholderInterests = contextInterests.createUnmarshaller();
			unmarshallerStakeholderInterests.setEventHandler(new MyValidationEventHandler());
			BufferedReader in = new BufferedReader(new FileReader(stakeholdersInterestFile));
			String s, s2 = new String();
		    while ((s = in.readLine()) != null)
		      s2 += s;
		    stakeholdersInterestString = s2;
			
	}
	public FunctionPointSystem read() throws JAXBException, ElementException{		
		StringReader stakeholdersInterestReader = new StringReader(stakeholdersInterestString);
		FunctionPointSystem functionPointSystem = (FunctionPointSystem) unmarshallerFunctionsPoint.unmarshal(new File(functionsPointFile));
		StakeholderInterests stakeholderInterests = (StakeholderInterests) unmarshallerStakeholderInterests.unmarshal(stakeholdersInterestReader);
		functionPointSystem.charge();
		if(functionPointSystem.validate()) {
			stakeholderInterests.validade(functionPointSystem,true);
			functionPointSystem.setStakeholderInterests(stakeholderInterests);
		}
		functionPointSystem.clear(true);
		return functionPointSystem;
	}
	public static FunctionPointSystem clone(FunctionPointSystem functionPointSystem) throws CloneException {
		StringWriter stringWriter = new StringWriter();
		try {
			marshallerFunctionsPoint.marshal(functionPointSystem, stringWriter);
			StringReader stringReader = new StringReader(stringWriter.toString());		
			return (FunctionPointSystem) unmarshallerFunctionsPoint.unmarshal(stringReader);
		} catch (JAXBException e) {
			throw new CloneException();
		}
	}
	public static void putStakeholderInterests(FunctionPointSystem functionPointSystem) throws CloneException, ElementException{
		try {
			StringReader stakeholdersInterestReader = new StringReader(stakeholdersInterestString);
			StakeholderInterests stakeholderInterests = (StakeholderInterests) unmarshallerStakeholderInterests.unmarshal(stakeholdersInterestReader);
			functionPointSystem.setStakeholderInterests(stakeholderInterests);
			stakeholderInterests.validade(functionPointSystem, false);
		} catch (JAXBException e) {
			throw new CloneException();
		} 	
	}
}
