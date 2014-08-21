package br.uniriotec.vitor.padilha.dissertacao.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import br.uniriotec.vitor.padilha.dissertacao.jaxb.MyValidationEventHandler;
import br.uniriotec.vitor.padilha.dissertacao.model.FunctionPointSystem;
import br.uniriotec.vitor.padilha.dissertacao.model.stakeholdersInterests.Interest;
import br.uniriotec.vitor.padilha.dissertacao.model.stakeholdersInterests.Stakeholder;
import br.uniriotec.vitor.padilha.dissertacao.model.stakeholdersInterests.StakeholderInterests;
import br.uniriotec.vitor.padilha.dissertacao.model.transactionModel.Transaction;

public class GeradorDeStakeholder {

	/**
	 * @param args
	 * @throws JAXBException 
	 */
	public static void main(String[] args) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(FunctionPointSystem.class);
		JAXBContext contextInterests = JAXBContext.newInstance(StakeholderInterests.class);
		Unmarshaller um = context.createUnmarshaller();
		Object obj = um.unmarshal(new File("D:/Google Drive/Mestrado/Estudo Dirigido/Instancias/Parametros/functions-point.xml"));
		um.setEventHandler(new MyValidationEventHandler());
		Scanner in = new Scanner(System.in);
		System.out.println("Digite o nome do arquivo de saída:");
		String nomeDeSaida = in.next();
		System.out.println();
		System.out.println("Digite a densidade dos interessados por transação (0-muito baixa [1], 1-baixa[1-5], 2-média[1-10], 3-alta[1-15], 4-muito alta[1-50] ");
		int densidadeDosInteressados = in.nextInt();
		while(densidadeDosInteressados<0 || densidadeDosInteressados>4){
			System.out.println("Numero inválido, digite novamente: ");
			densidadeDosInteressados = in.nextInt();
			System.out.println("");
		}
		System.out.println();
		int numMinInteressados = 1;
		if(densidadeDosInteressados==1){
			numMinInteressados = 5;
		} else if(densidadeDosInteressados==2){
			numMinInteressados = 10;
		} else if(densidadeDosInteressados==3){
			numMinInteressados = 15;
		}else if(densidadeDosInteressados==4){
			numMinInteressados = 50;
		}
		System.out.println("Digite o número de interessados: deve ser maior que "+numMinInteressados);
		int numeroDeInteressados = in.nextInt();
		while(numeroDeInteressados<numMinInteressados){
			System.out.println("Numero inválido, digite novamente: ");
			numeroDeInteressados = in.nextInt();
			System.out.println("");
		}
		FunctionPointSystem functionPointSystem = (FunctionPointSystem) obj;
		List<Stakeholder> stakeholders = new ArrayList<Stakeholder>();
		StakeholderInterests stakeholderInterests = new StakeholderInterests();
		for(int i=0;i<numeroDeInteressados;i++) {
			Stakeholder stakeholder = new Stakeholder();
			stakeholder.setName("Stakeholder "+(i+1));
			stakeholder.setParent(stakeholderInterests);
			stakeholders.add(stakeholder);
		}
		
		List<Stakeholder> stakeholdersAdicionadosTotal = new ArrayList<Stakeholder>();
		List<Interest> interests = new ArrayList<Interest>();
		for(Transaction transaction: functionPointSystem.getTransactionModel().getTransactions()){
			List<Stakeholder> stakeholdersAdicionados = new ArrayList<Stakeholder>();			
			int numeroDeInteressadosNaTransacao = (int) ((int) 1 + (Math.random() * numMinInteressados));
			for(int i=0;i<numeroDeInteressadosNaTransacao;i++) {
				int stakeholderEscolhido = (int)  (Math.random() * stakeholders.size());
				System.out.println(stakeholderEscolhido);
				Stakeholder stakeholder = stakeholders.get(stakeholderEscolhido);
				if(stakeholdersAdicionados.contains(stakeholdersAdicionados)){
					i--;
				}
				else {
					stakeholdersAdicionados.add(stakeholder);
					Interest interest = new Interest();
					interest.setStakeholder(stakeholder);
					interest.setStakeholderXML(stakeholder.getName());
					interest.setTransaction(transaction);
					interest.setTransactionXML(transaction.getName());
					interest.setParent(stakeholderInterests);
					int interesse = (int)  (Math.random() * 3);
					switch (interesse) {
					case 0:
						interest.setInterest(25L);
						break;
					case 1:
						interest.setInterest(50L);
						break;
					case 2:
						interest.setInterest(75L);
						break;
					case 3:
						interest.setInterest(100L);
						break;

					default:
						break;
					}
					interests.add(interest);
					if(!stakeholdersAdicionadosTotal.contains(stakeholder)){
						stakeholdersAdicionadosTotal.add(stakeholder);
					}
				}
			}
		}
		stakeholderInterests.setStakeholders(stakeholdersAdicionadosTotal);
		stakeholderInterests.setInterests(interests);
		
		if(stakeholdersAdicionadosTotal.size()!=numeroDeInteressados){
			for (Stakeholder stakeholder : stakeholders) {
				if(!stakeholdersAdicionadosTotal.contains(stakeholder))
					System.out.println(stakeholder.getName()+" desconsiderado pois não foi associado a nenhuma transação");
			}
			System.out.println("Dos "+numeroDeInteressados+" interessados, somente "+stakeholdersAdicionadosTotal.size()+" foram gerados, pois o restante não foi associado a nenhuma transação.");
		}
		
		Marshaller m = contextInterests.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		m.marshal(stakeholderInterests, System.out);
	}

}
