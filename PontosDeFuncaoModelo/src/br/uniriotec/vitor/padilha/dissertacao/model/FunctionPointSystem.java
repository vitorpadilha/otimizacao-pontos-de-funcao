package br.uniriotec.vitor.padilha.dissertacao.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import br.uniriotec.vitor.padilha.dissertacao.exception.ElementException;
import br.uniriotec.vitor.padilha.dissertacao.model.constants.DataModelElementType;
import br.uniriotec.vitor.padilha.dissertacao.model.constants.TransactionType;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.DET;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.DataModel;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.DataModelElement;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.RET;
import br.uniriotec.vitor.padilha.dissertacao.model.stakeholdersInterests.StakeholderInterests;
import br.uniriotec.vitor.padilha.dissertacao.model.transactionModel.FTR;
import br.uniriotec.vitor.padilha.dissertacao.model.transactionModel.FTRField;
import br.uniriotec.vitor.padilha.dissertacao.model.transactionModel.Transaction;
import br.uniriotec.vitor.padilha.dissertacao.model.transactionModel.TransactionModel;

@XmlRootElement(name="system")
public class FunctionPointSystem extends XmlFunctionPointElement implements ElementValidator{

	private String name;
	
	private DataModel dataModel;
	
	private TransactionModel transactionModel;
	
	private StakeholderInterests stakeholderInterests;
	
	@XmlElement(required=true,name="name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(required=true,name="data-model")
	public DataModel getDataModel() {
		return dataModel;
	}
	
	public void setDataModel(DataModel dataModel) {
		this.dataModel = dataModel;
	}

	@XmlElement(required=true,name="transaction-model")
	public TransactionModel getTransactionModel() {
		return transactionModel;
	}

	public void setTransactionModel(TransactionModel transactionModel) {
		this.transactionModel = transactionModel;
	}

	@Override
	public boolean validate() throws ElementException {
		if(this.getDataModel().validate() && this.getTransactionModel().validate())
			return true;
		return false;
	}
	
	public void charge(){
		this.getDataModel().charge();
		this.getTransactionModel().charge();
	}
	public void clear(boolean eliminateRetsAndDets) {
		
		if(eliminateRetsAndDets) {
			clearNoUsedFields();
			clearNoUsedRets();
			transformILFInEIF();
		}
		else {
			clearNoUsedDataModel();
		}
	}
	
	private void transformILFInEIF(){
		List<String> ilfsInInputTransactions = new ArrayList<String>();
		if(this.transactionModel!=null && this.transactionModel.getTransactions()!=null) {
			for(Transaction transaction:this.transactionModel.getTransactions()) {
				if(transaction.getType().equals(TransactionType.EI)) {
					for(FTR ftr:transaction.getFtrList()) {
						ilfsInInputTransactions.add(ftr.getRetRef().getParent().getName());
					}
				}
			}
		}
		if(this.getDataModel()!=null) {
			for(DataModelElement ilf:this.getDataModel().getDataModelElements()) {
				if(!ilfsInInputTransactions.contains(ilf.getName()))
					ilf.setType(DataModelElementType.EIF);
			}
		}
	}
	
	private void clearNoUsedDataModel() {
		List<DataModelElement> dataModelElements = new ArrayList<DataModelElement>();
		Set<DataModelElement> usedDataModelElements = new HashSet<DataModelElement>();
		for (Transaction transaction : this.transactionModel.getTransactions()) {
			for (FTR ftr : transaction.getFtrList()) {
				if(!usedDataModelElements.contains(ftr.getRetRef().getParent())) {
					usedDataModelElements.add(ftr.getRetRef().getParent());
					dataModelElements.add(ftr.getRetRef().getParent());
				}
			}
		}
		this.dataModel.setDataModelElements(dataModelElements);
	}

	
	private void clearNoUsedRets() {
		List<DataModelElement> dataModelElements = new ArrayList<DataModelElement>(this.dataModel.getDataModelElements());
		for(DataModelElement dataModelElement:this.dataModel.getDataModelElements()) {
			List<RET> rets = new ArrayList<RET>(dataModelElement.getRets());
			for(RET ret:dataModelElement.getRets()) {
				if(ret.getDets().isEmpty()) {
					//System.out.println("RET removido = "+ret.getParent().getName()+"/"+ret.getName());
					rets.remove(ret);
				}
			}
			dataModelElement.setRets(rets);
			if(rets.isEmpty()) {
				//System.out.println("ILF removido = "+ilf.getName());
				dataModelElements.remove(dataModelElement);
			}
		}
		
		this.dataModel.setDataModelElements(dataModelElements);
	}

	protected void clearNoUsedFields() {
		Set<DET> utilsFields = new HashSet<DET>();
		for(Transaction transaction:transactionModel.getTransactions()){
			for(FTR ftr:transaction.getFtrList()) {
				if(ftr.getUseAllDets()!=null && ftr.getUseAllDets()){
					if(ftr.getRetRef().getDets()!=null) {
						for(DET field:ftr.getRetRef().getDets()) {
							utilsFields.add(field);
						}
					}
				}
				else {
					for(FTRField field:ftr.getFields()){
						utilsFields.add(field.getField());
					}
				}
			}
		}
		for(DataModelElement dataModelElement:dataModel.getDataModelElements()) {
			for(RET subset:dataModelElement.getRets()) {
				List<DET> dets = new ArrayList<DET>(subset.getDets());
				for(DET det:subset.getDets()) {
					if(!utilsFields.contains(det)) {
						//System.out.println("Campo removido = "+field.getParent().getName()+"/"+field.getName());
						dets.remove(det);
					}
				}
				subset.setDets(dets);
			}
		}
		
	}

	@XmlTransient
	public StakeholderInterests getStakeholderInterests() {
		return stakeholderInterests;
	}

	public void setStakeholderInterests(StakeholderInterests stakeholderInterests) {
		this.stakeholderInterests = stakeholderInterests;
	}
	
	public String doDot(FunctionPointSystem baseFunctionPointSystem,boolean showDataModel) {
		
		String returnValue = "digraph teste {\n";
		if(showDataModel) {
			returnValue+=this.dataModel.doDot(baseFunctionPointSystem.getDataModel().getDataModelElements());
		}
		returnValue+=this.transactionModel.doDot(baseFunctionPointSystem.getTransactionModel().getTransactions(),showDataModel);
		
		returnValue+="}";
		return returnValue;
	}
}
