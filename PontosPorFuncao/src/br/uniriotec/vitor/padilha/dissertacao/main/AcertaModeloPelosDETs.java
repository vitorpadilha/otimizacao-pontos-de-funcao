package br.uniriotec.vitor.padilha.dissertacao.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import br.uniriotec.vitor.padilha.dissertacao.calculator.FunctionPointCalculator;
import br.uniriotec.vitor.padilha.dissertacao.jaxb.MyValidationEventHandler;
import br.uniriotec.vitor.padilha.dissertacao.model.FunctionPointSystem;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.DET;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.DataModelElement;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.RET;
import br.uniriotec.vitor.padilha.dissertacao.model.stakeholdersInterests.StakeholderInterests;
import br.uniriotec.vitor.padilha.dissertacao.model.transactionModel.FTR;
import br.uniriotec.vitor.padilha.dissertacao.model.transactionModel.FTRField;
import br.uniriotec.vitor.padilha.dissertacao.model.transactionModel.Transaction;
import br.uniriotec.vitor.padilha.dissertacao.view.IFunctionPointView;
import br.uniriotec.vitor.padilha.dissertacao.view.WebFunctionPointView;

public class AcertaModeloPelosDETs {
	/**
	 * @param args
	 * @throws JAXBException 
	 */
	public static void main(String[] args) throws JAXBException, IOException {
		JAXBContext context = JAXBContext.newInstance(FunctionPointSystem.class);
		JAXBContext contextInterests = JAXBContext.newInstance(StakeholderInterests.class);
		

		Unmarshaller um = context.createUnmarshaller();
		um.setEventHandler(new MyValidationEventHandler());
		Unmarshaller um2 = contextInterests.createUnmarshaller();
		um2.setEventHandler(new MyValidationEventHandler());

		Object obj = um.unmarshal(new File("D:/Google Drive/Mestrado/Estudo Dirigido/Instancias/InstanciaDoMarcio/functions-point-new.xml"));
	
		FunctionPointSystem functionPointSystem = (FunctionPointSystem) obj;
		IFunctionPointView functionPointView = new WebFunctionPointView(0, functionPointSystem);
		Set<IFunctionPointView> views = new HashSet<IFunctionPointView>();
		views.add(functionPointView);
		
		FunctionPointCalculator functionPointCalculator = new FunctionPointCalculator();
	
		for(Transaction transaction:functionPointSystem.getTransactionModel().getTransactions()){
			
			//System.out.println("Lendo Transação "+transaction.getName());
			List<FTR> ftrList = new ArrayList<FTR>();
			if(transaction.getFtrList()!=null) {
				Map<String, Map<String, List<DET>>> listaDeDets = new HashMap<String,Map<String, List<DET>>>();
				
				for(FTR ftr:transaction.getFtrList()) {
					boolean encontrouDataModel = false;
					if(!listaDeDets.containsKey(ftr.getName())) {
						listaDeDets.put(ftr.getName(), new HashMap<String, List<DET>>());
					}
					for(DataModelElement dataModelElement:functionPointSystem.getDataModel().getDataModelElements()){
						if(dataModelElement.getName().equals(ftr.getName())) {
							encontrouDataModel = true;
							for(FTRField ftrField:ftr.getFields()) {
								boolean encontrouRETDoDet = false;
								for(RET ret:dataModelElement.getRets()){
									for(DET det:ret.getDets()){
										if(det.getName().equals(ftrField.getName())){
											if(!listaDeDets.get(ftr.getName()).containsKey(ret.getName())) {
												listaDeDets.get(ftr.getName()).put(ret.getName(), new ArrayList<DET>());
											}
											listaDeDets.get(ftr.getName()).get(ret.getName()).add(det);
											encontrouRETDoDet = true;
										}
									}
								}
								if(!encontrouRETDoDet)
									System.out.println("Det não encontrado. - "+ftrField.getName());
							}
						}
					}
					if(!encontrouDataModel)
						System.out.println("FTR não encontrado. - "+ftr.getName());
				}
				for (String ftrName : listaDeDets.keySet()) {
					for (String retName : listaDeDets.get(ftrName).keySet()) {
						FTR ftr = new FTR();
						ftr.setName(retName);
						ftr.setRet(retName);
						ftr.setDataModelElement(ftrName);
						ftr.setFields(new ArrayList<FTRField>());
						for (DET det : listaDeDets.get(ftrName).get(retName)) {
							FTRField ftrField = new FTRField();
							ftrField.setName(det.getName());
							ftr.getFields().add(ftrField);
						}
						ftr.setUseAllDets(false);
						ftrList.add(ftr);
					}
				}
				
			}
			transaction.setFtrList(ftrList);
			transaction.setErrorMsg(true);
		}
		
		Marshaller m = context.createMarshaller();
		
	    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	    m.marshal(obj, new File("D:/Google Drive/Mestrado/Estudo Dirigido/Instancias/InstanciaDoMarcio/functions-point.xml"));
//		
	}
}
