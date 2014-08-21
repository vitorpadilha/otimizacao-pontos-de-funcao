package br.uniriotec.vitor.padilha.dissertacao.calculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.uniriotec.vitor.padilha.dissertacao.model.FunctionPointSystem;
import br.uniriotec.vitor.padilha.dissertacao.model.constants.DataModelElementType;
import br.uniriotec.vitor.padilha.dissertacao.model.constants.TransactionType;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.DET;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.DataModel;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.DataModelElement;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.RET;
import br.uniriotec.vitor.padilha.dissertacao.model.stakeholdersInterests.Interest;
import br.uniriotec.vitor.padilha.dissertacao.model.transactionModel.FTR;
import br.uniriotec.vitor.padilha.dissertacao.model.transactionModel.FTRField;
import br.uniriotec.vitor.padilha.dissertacao.model.transactionModel.Transaction;
import br.uniriotec.vitor.padilha.dissertacao.model.transactionModel.TransactionModel;
import br.uniriotec.vitor.padilha.dissertacao.view.IFunctionPointView;

public class FunctionPointCalculator {
	
	//private Set<IFunctionPointView> functionsView;
	
//	public FunctionPointCalculator() {
//		this.functionsView = new HashSet<IFunctionPointView>();
//	}
//	public FunctionPointCalculator(Set<IFunctionPointView> functionsView) {
//		this.functionsView = functionsView;
//	}
	//private static double FATOR_AJUSTE_SISP = 0.75;
	private static Map<DataModelElement, Complexity> complexityForDataModelElement;
	public int calculate(FunctionPointSystem functionPointSystem, int releaseNumber, Set<IFunctionPointView> functionsView, Map<DataModelElement, Complexity> complexityForDataModelElement){
		this.complexityForDataModelElement = complexityForDataModelElement;
		return this.calculate(functionPointSystem, releaseNumber, functionsView);
	}
	public int calculate(FunctionPointSystem functionPointSystem, int releaseNumber, Set<IFunctionPointView> functionsView){
		int totalPontosPorFuncaoNaoAjustada = 0;
		if(functionPointSystem.getDataModel()!=null) {
			totalPontosPorFuncaoNaoAjustada+=calculateDataModel(functionPointSystem.getDataModel(),functionsView, releaseNumber);
		}
		if(functionPointSystem.getTransactionModel()!=null) {
			totalPontosPorFuncaoNaoAjustada+=calculateTransactionModel(functionPointSystem.getTransactionModel(), releaseNumber, functionsView);
		}
		
		return totalPontosPorFuncaoNaoAjustada;
	}
	
	private int calculateDataModel(DataModel dataModel,Set<IFunctionPointView> functionsView, int releaseNumber) {
		int totalFunctionPoint = 0;

		for(DataModelElement dataModelElement:dataModel.getDataModelElements()) {
			int totalFunctionPointRet = calculateDataModelElementValue(dataModelElement,functionsView, releaseNumber);
			totalFunctionPoint+=totalFunctionPointRet;
		}
		if(functionsView!=null) {
			for(IFunctionPointView view :functionsView) {
				view.renderDataModelValue(dataModel, dataModel.getDataModelElements().size(), totalFunctionPoint);
			}
		}
		return totalFunctionPoint;
	}
	public int calculateDataModelElementValue(DataModelElement dataModelElement,Set<IFunctionPointView> functionsView, int releaseNumber) {
		int totalDet = 0;
		int totalRet = 0;
//		boolean hasDETNotImplemented = false;
//		boolean hasDETImplemented = false;
		Set<String> dets = new HashSet<String>();
		Map<String,List<String>> rets = new HashMap<String, List<String>>();
		rets.put("", new ArrayList<String>());
		Set<String> jaExtendidos = new HashSet<String>();
		for(RET ret:dataModelElement.getRets()){
			totalRet++;
			if(ret.getExtendsRet()!=null){					
				if(jaExtendidos.contains(ret.getExtendsRet())) {
					totalRet--;
				}
				else {
					jaExtendidos.add(ret.getExtendsRet());
					rets.put(ret.getExtendsRet(), new ArrayList<String>());
				}
				rets.get(ret.getExtendsRet()).add(ret.getName());					
			}
			else {
				rets.get("").add(ret.getName());
			}
			for(DET det:ret.getDets()) {
				//if(!det.isImplementada()) {
					totalDet++;
				//}
				det.setFlagcanBeDetInTransation(true);
				
				if(det.getHasSemanticMeaning()!=null && !det.getHasSemanticMeaning()) {
					totalDet--;
					continue;
				}
				if(det.getPrimaryKey()!=null && det.getPrimaryKey() && (det.getHasSemanticMeaning()==null || !det.getHasSemanticMeaning())) {
					det.setFlagcanBeDetInTransation(false);
					//if(!det.isImplementada()) { 
						totalDet--;
					//}
					continue;
				}
				if(det.getRetRef()!=null){
					if(det.getRetRef().getParent().getName().equals(ret.getParent().getName())) {
						if(det.getHasSemanticMeaning()==null || !det.getHasSemanticMeaning()) {
							totalDet--;
							continue;
						}
					}
					else if(det.getPrimaryKey()!=null && det.getPrimaryKey()) {
						det.setFlagcanBeDetInTransation(false);
						//if(!det.isImplementada()) { 
							totalDet--;
						//}
						continue;
					}
				}
//				if(det.isImplementada())
//					hasDETImplemented = true;
//				else
//					hasDETNotImplemented = true;
				dets.add(det.getParent().getName()+"."+det.getName());
			}
		}
		List<String[]> retsNorm = new ArrayList<String[]>();
		for(String chave:rets.keySet()){				
			if(chave.equals("")) {					
				for(String ret:rets.get(chave)){
					String[] retGroup = new String[1];
					retGroup[0]=ret;
					retsNorm.add(retGroup);
				}					
			}
			else {
				String[] retGroup = new String[rets.get(chave).size()];
				int cont = 0;
				for(String ret:rets.get(chave)){
					retGroup[cont]=ret;
					cont++;
				}
				retsNorm.add(retGroup);
			}				
			
		}
		String[] detsNorm = new String[dets.size()];
		int cont = 0;
		for(String det:dets){
			detsNorm[cont]=det;
			cont++;
		}
		Complexity complexity = calculateDataModelElementComplexity(totalRet, totalDet);
		int totalFunctionPointRet = calculateFunctionPointDataModelElement(complexity, dataModelElement.getType());
		//int adjustementFactor = 0;
		
//		if(hasDETImplemented && hasDETNotImplemented)
//			adjustementFactor = Double.valueOf(FATOR_AJUSTE_SISP*totalFunctionPointRet).intValue();
//		if(dataModelElement.getAdjustmentsFactors()==null && adjustementFactor>0) {
//			dataModelElement.setAdjustmentsFactors(new HashMap<Integer, Integer>());
//		}
//		if(adjustementFactor>0) {
//			dataModelElement.getAdjustmentsFactors().put(releaseNumber, adjustementFactor);
//		}
		if(complexityForDataModelElement!=null)
			complexityForDataModelElement.put(dataModelElement, complexity);
		if(functionsView!=null) {
			for(IFunctionPointView view :functionsView) {
				//view.addDataModelElementValue(dataModelElement, retsNorm, detsNorm,complexity, totalFunctionPointRet,dataModelElement.getAdjustmentsFactors());
				
				view.addDataModelElementValue(dataModelElement, retsNorm, detsNorm,complexity, totalFunctionPointRet,null);
			}
		}
//		if(dataModelElement.getAdjustmentsFactors()!=null) {
//			for (Integer adjustementFactor2 : dataModelElement.getAdjustmentsFactors().values()) {
//				totalFunctionPointRet+=adjustementFactor2;
//			}
//		}
//		
//		int diferenca = totalFunctionPointRet-dataModelElement.getFunctionsPointValue().intValue();
//		return diferenca<0?0:diferenca;
		return totalFunctionPointRet;
	}
	public Complexity getComplexity(DataModelElement dataModelElement) {
		return complexityForDataModelElement.get(dataModelElement);
	}
	private int calculateTransactionModel(TransactionModel transactionModel,int releaseNumber,Set<IFunctionPointView> functionsView) {
		int totalFunctionPoint = 0;
		for(Transaction transaction:transactionModel.getTransactions()){
			if(transaction.getReleaseImplementation()==0) {
				int totalTransactionFunctionsPoint = calculateTransactionValue(transaction, releaseNumber, functionsView);
				totalFunctionPoint+=totalTransactionFunctionsPoint;
			}
		}
		if(functionsView!=null) {
			for(IFunctionPointView view :functionsView) {
				view.renderTransactionModelValue(transactionModel, transactionModel.getTransactions().size(), totalFunctionPoint);
			}
		}
		return totalFunctionPoint;
	}
	public int calculateTransactionValue(Transaction transaction, int releaseNumber,Set<IFunctionPointView> functionsView) {
		int totalFtr = 0;
		int totalDet = 0;
		Set<String> arquivosLidos = new HashSet<String>();
		List<String> fields = new ArrayList<String>();
		Map<String, String> refFields = new HashMap<String, String>();
		for(FTR ftr:transaction.getFtrList()){
			if(!arquivosLidos.contains(ftr.getDataModelElement())) {
				arquivosLidos.add(ftr.getDataModelElement());
				totalFtr++;
			}
			
			if(ftr.getFields()!=null && (ftr.getUseAllDets()==null || !ftr.getUseAllDets())) {
				for(FTRField field:ftr.getFields()) {
					if(field.getField()!=null && canBeDetForTransaction(field.getField())) {
						if(!fields.contains(field.getParent().getParent().getName()+"."+field.getParent().getName()+"."+field.getField().getName())) {
							totalDet++;
							fields.add(field.getParent().getParent().getName()+"."+field.getParent().getName()+"."+field.getField().getName());
						}
					}
				}
			}
			else if(ftr.getUseAllDets()!=null && ftr.getUseAllDets()){
				for(DET field:ftr.getRetRef().getDets()) {
					if(canBeDetForTransaction(field)){
						String fieldName = "";
						if(field.getRetRef()!=null) {
							if(!refFields.containsKey(field.getRetRef().getParent().getName())) {
								refFields.put(field.getRetRef().getParent().getName(), field.getParent().getParent().getName()+"."+field.getParent().getName()+"."+field.getName()+"("+field.getRetRef().getParent().getName()+")");
							}
						}
						else {
							fieldName = field.getParent().getParent().getName()+"."+field.getParent().getName()+"."+field.getName();
							if(!fields.contains(fieldName)) {
								fields.add(fieldName);
								totalDet++;
							}
						}
						
					}
				}
			}
			
		}
		for (String ftr : refFields.keySet()) {
			if(!arquivosLidos.contains(ftr)) {
				fields.add(refFields.get(ftr));
				totalDet++;
				//arquivosLidos.add(ftr);
				//totalFtr++;
			}
		}
		if(transaction.getErrorMsg()){
			totalDet++;
			fields.add("Mensagens em geral");
		}
		String[] ftrs = new String[arquivosLidos.size()];
		int cont = 0;
		for(String ftr1:arquivosLidos){
			ftrs[cont]=ftr1;
			cont++;
		}
		
		String[] dets;
		cont = 0;
		if(transaction.getExtraDET()!=null) {
			dets = new String[fields.size()+transaction.getExtraDET()];
			for(int i=0;i<transaction.getExtraDET();i++) {
				dets[cont]="ExtraDet"+(i+1);
				cont++;
			}
		}
		else {
			dets = new String[fields.size()];
		}		
		for(String det1:fields){
			dets[cont]=det1;
			cont++;
		}
		Complexity complexity = calculateTransactionComplexity(ftrs.length, dets.length, transaction.getType());
		int totalTransactionFunctionsPoint = calculateFunctionPointTransactionModelElement(complexity, transaction.getType());
		if(functionsView!=null) {
			for(IFunctionPointView view :functionsView) {
				view.addTransactionValue(transaction, ftrs, dets, complexity, totalTransactionFunctionsPoint,releaseNumber);
			}
		}
		
		return totalTransactionFunctionsPoint;
	}

	private boolean canBeDetForTransaction(DET field) {
		return field.getFlagcanBeDetInTransation()!=null && field.getFlagcanBeDetInTransation() && (field.getPrimaryKey()==null || !field.getPrimaryKey() || (field.getHasSemanticMeaning()==null || field.getHasSemanticMeaning()));
	}
	
	private int calculateFunctionPointDataModelElement(Complexity complexity, DataModelElementType dataModelElementType){
		
		if((complexity==Complexity.LOW && dataModelElementType.equals(DataModelElementType.ILF)) ||
				(complexity==Complexity.MEDIUM && dataModelElementType.equals(DataModelElementType.EIF)) 
				)
			return 7;
		else if((complexity==Complexity.MEDIUM && dataModelElementType.equals(DataModelElementType.ILF)) ||
				(complexity==Complexity.HIGH && dataModelElementType.equals(DataModelElementType.EIF)) 
				)
			return 10;
		else if(complexity==Complexity.LOW && dataModelElementType.equals(DataModelElementType.EIF))
			return 5;
		else if(complexity==Complexity.HIGH && dataModelElementType.equals(DataModelElementType.ILF))
			return 15;
		else return 0;
	}
	private Complexity calculateDataModelElementComplexity(int ret, int det) {
		Complexity complexity = null;
		if((ret>=6 && det>=20) || (ret<=5 && ret>=2 && det>=51) ) {
			complexity=Complexity.HIGH;
		}
		else if(ret>=6 || (ret<=5 && ret>=2 && det>=20 && det<=50) || (ret==1 && det>=51)) {
			complexity=Complexity.MEDIUM;
		}
		else if((ret==1 && det<=50) || (ret<=5 && ret>=2 && det<20)) {
			complexity=Complexity.LOW;
		}
		return complexity;
	}
	
	private int calculateFunctionPointTransactionModelElement(Complexity complexity, TransactionType transactionType){
	
		if((complexity==Complexity.LOW && transactionType.equals(TransactionType.EO) ) ||
				(complexity==Complexity.MEDIUM && (transactionType.equals(TransactionType.EI) || transactionType.equals(TransactionType.EQ)) ) 
				)
			
			return 4;
		else if((complexity==Complexity.MEDIUM && transactionType.equals(TransactionType.EO)))
			return 5;
		else if(complexity==Complexity.HIGH && transactionType.equals(TransactionType.EO))
			return 7;
		else if(complexity==Complexity.LOW && (transactionType.equals(TransactionType.EI) || transactionType.equals(TransactionType.EQ)))
			return 3;
		else if(complexity==Complexity.HIGH && (transactionType.equals(TransactionType.EI) || transactionType.equals(TransactionType.EQ)))
			return 6;
		else 
			return 0;
	}
	private Complexity calculateTransactionComplexity(int ftrs, int dets,
			TransactionType transactionType) {
		Complexity complexity = null;
		if(	
				(((ftrs>=3 && dets>=5) || (ftrs==2 && dets>=16)) && transactionType.equals(TransactionType.EI)) 
				||
				(((ftrs>=4 && dets>=6) || (ftrs>=2 && ftrs<=3 && dets>=20)) && (transactionType.equals(TransactionType.EO)||transactionType.equals(TransactionType.EQ))) 
			){
			complexity=Complexity.HIGH;
		}
		else if(	
				(((ftrs>=3 && dets <=4) || (ftrs==2 && dets>=5 && dets<=15)
						|| (ftrs<=1 && dets>=16)) && transactionType.equals(TransactionType.EI)) 
				||
				(((ftrs>=4 && dets <=5) || (ftrs>=2 && ftrs<=3 && dets>=6 && dets<=19)
						|| (ftrs<=1 && dets>=20)) && (transactionType.equals(TransactionType.EO)||transactionType.equals(TransactionType.EQ))) 
			){
			complexity=Complexity.MEDIUM;
		}
		else if(	
				(((ftrs<=1 && dets<=15)
						|| (ftrs==2 && dets<=4)) && transactionType.equals(TransactionType.EI)) 
				||
				(((ftrs<=1 && dets<=19)
						|| (ftrs<=3 && ftrs>=2 && dets<=5)) && (transactionType.equals(TransactionType.EO)||transactionType.equals(TransactionType.EQ))) 
			){
			complexity=Complexity.LOW;
		}
		return complexity;
	}
	public Long calculateUserSatisfaction(FunctionPointSystem functionPointSystem) {
		Long interesseTotal = 0L;
		List<String> transactionsNames = new ArrayList<String>();
		for(Transaction transaction:functionPointSystem.getTransactionModel().getTransactions()) {
			transactionsNames.add(transaction.getName());
		}
		for(Interest interest:functionPointSystem.getStakeholderInterests().getInterests()) {
			if(transactionsNames.contains(interest.getTransaction().getName()))
				interesseTotal+=(interest.getInterest()*interest.getStakeholder().getWeight());
		}
		return interesseTotal;
	}
	
	public Double calculateUserSatisfactionPercent(FunctionPointSystem functionPointSystem, Long baseSatisfaction) {
		
		return (this.calculateUserSatisfaction(functionPointSystem).doubleValue()/baseSatisfaction)*100;
	}
//	public Set<IFunctionPointView> getFunctionsView() {
//		return functionsView;
//	}
//
//	public void setFunctionsView(Set<IFunctionPointView> functionsView) {
//		this.functionsView = functionsView;
//	}
	
}