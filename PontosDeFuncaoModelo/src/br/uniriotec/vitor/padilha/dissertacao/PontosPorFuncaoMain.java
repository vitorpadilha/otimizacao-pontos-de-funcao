package br.uniriotec.vitor.padilha.dissertacao;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import br.uniriotec.vitor.padilha.dissertacao.calculator.FunctionPointCalculator;
import br.uniriotec.vitor.padilha.dissertacao.exception.ElementException;
import br.uniriotec.vitor.padilha.dissertacao.jaxb.MyValidationEventHandler;
import br.uniriotec.vitor.padilha.dissertacao.model.FunctionPointSystem;
import br.uniriotec.vitor.padilha.dissertacao.model.stakeholdersInterests.StakeholderInterests;
import br.uniriotec.vitor.padilha.dissertacao.utils.FunctionsPointReader;
import br.uniriotec.vitor.padilha.dissertacao.view.IFunctionPointView;
import br.uniriotec.vitor.padilha.dissertacao.view.WebFunctionPointView;

public class PontosPorFuncaoMain {

	/**
	 * @param args
	 * @throws JAXBException 
	 * @throws ElementException 
	 */
	public static final String INSTANCE_PATH = "ACAD3";
	public static final String HTML_RESULT_PATH = (new File("")).getAbsolutePath()+"\\instancias\\";
	public static void main(String[] args) throws JAXBException, IOException, ElementException {
		JAXBContext context = JAXBContext.newInstance(FunctionPointSystem.class);
		JAXBContext contextInterests = JAXBContext.newInstance(StakeholderInterests.class);
		

		Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		Unmarshaller um = context.createUnmarshaller();
		um.setEventHandler(new MyValidationEventHandler());
		Unmarshaller um2 = contextInterests.createUnmarshaller();
		um2.setEventHandler(new MyValidationEventHandler());
		File file = new File("");
		FunctionsPointReader reader = new FunctionsPointReader(file.getAbsolutePath()+"\\instancias\\"+INSTANCE_PATH+"\\functions-point.xml",file.getAbsolutePath()+"\\instancias\\"+INSTANCE_PATH+"\\stakeholders-interest.xml");
		FunctionPointSystem functionPointSystem = reader.read();
		

		IFunctionPointView functionPointView = new WebFunctionPointView(0, functionPointSystem,file.getAbsolutePath()+"\\instancias\\"+INSTANCE_PATH+"\\resultado-0");
		Set<IFunctionPointView> views = new HashSet<IFunctionPointView>();
		views.add(functionPointView);
		
		FunctionPointCalculator functionPointCalculator = new FunctionPointCalculator();
		
		try {
			functionPointSystem.charge();
			if(functionPointSystem.validate()) {
//				List<Transaction> transactions= new ArrayList<Transaction>(functionPointSystem.getTransactionModel().getTransactions());
				//Transaction[] transactionsInArray = (Transaction[]) transactions.toArray();
//				for(Transaction transaction:transactions){
//					if(transaction.getName().equals("IncluirMotivoDeTransferencia") 
//							|| 	transaction.getName().equals("RemoverMotivoDeTransferencia")
//							|| 	transaction.getName().equals("AlterarMotivoDeTransferencia")
//							|| 	transaction.getName().equals("ConsultarMotivosDeTransferencia")
//							) {
//						functionPointSystem.getTransactionModel().getTransactions().remove(transaction);
//					}
//				}
				functionPointSystem.clear(true);
				
				System.out.println("Total:"+functionPointCalculator.calculate(functionPointSystem,0,views));
				System.out.println("Satis:"+functionPointCalculator.calculateUserSatisfaction(functionPointSystem));
				System.out.println(functionPointSystem.doDot(functionPointSystem, true));
				functionPointView.render();
			}
		} catch (ElementException e) {
			System.out.print(e.getMessage());
			e.printStackTrace();
		}
	}
	

}
