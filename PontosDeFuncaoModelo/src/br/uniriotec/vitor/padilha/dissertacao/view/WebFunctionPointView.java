package br.uniriotec.vitor.padilha.dissertacao.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.uniriotec.vitor.padilha.dissertacao.PontosPorFuncaoMain;
import br.uniriotec.vitor.padilha.dissertacao.calculator.Complexity;
import br.uniriotec.vitor.padilha.dissertacao.model.FunctionPointSystem;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.DET;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.DataModel;
import br.uniriotec.vitor.padilha.dissertacao.model.dataModel.DataModelElement;
import br.uniriotec.vitor.padilha.dissertacao.model.transactionModel.Transaction;
import br.uniriotec.vitor.padilha.dissertacao.model.transactionModel.TransactionModel;

public class WebFunctionPointView extends GenericFunctionPointView {

	private Map<FunctionPointSystem,List<TransactionValues>> transactions;
	
	private Map<FunctionPointSystem,List<DataElementValues>> dataElements;
	
	private Map<FunctionPointSystem, DataModelValue> dataModelValues;
	
	private Map<FunctionPointSystem, TransactionModelValue> transactionModelValues;
	
	private Map<FunctionPointSystem, Double> satisfactionPercent;
	
	private Map<FunctionPointSystem, Long> satisfaction;
	
	private File file;
	@Override
	public void renderTransactionModelValue(TransactionModel transactionModel,
			int totalTransations, int totalFunctionsPoint) {
		TransactionModelValue transactionModelValue = new TransactionModelValue();
		transactionModelValue.setTransactions(totalTransations);
		transactionModelValue.setTransactionModel(transactionModel);
		transactionModelValue.setFunctionsPointValue(totalFunctionsPoint);
		if(this.transactionModelValues.get(transactionModel.getParent())==null){
			this.transactionModelValues.put(transactionModel.getParent(), transactionModelValue);
		}
//			adicionaLinha("### Total de pontos por função não ajustado para transações: "+totalFunctionsPoint+" para "+totalTransations+" transações");
	}
	
	public void addSatisfactionPercentForFunctionPoint(FunctionPointSystem functionPointSystem, Double percent) {
		satisfactionPercent.put(functionPointSystem, percent);
	}
	public WebFunctionPointView(int cycleNumber, FunctionPointSystem functionPointSystem) throws IOException {
		this(cycleNumber,functionPointSystem,"resultado-"+cycleNumber);
	}
	public WebFunctionPointView(int cycleNumber, FunctionPointSystem functionPointSystem, String arquivoName) throws IOException {
		super();
		transactions = new LinkedHashMap<FunctionPointSystem, List<TransactionValues>>();
		dataElements = new LinkedHashMap<FunctionPointSystem, List<DataElementValues>>();
		dataModelValues = new LinkedHashMap<FunctionPointSystem, DataModelValue>();
		transactionModelValues = new LinkedHashMap<FunctionPointSystem, TransactionModelValue>();
		satisfactionPercent = new LinkedHashMap<FunctionPointSystem, Double>();
		satisfaction = new LinkedHashMap<FunctionPointSystem, Long>();
		if(functionPointSystem!=null) {
			deletar(arquivoName+".html");
			criarArquivo(arquivoName+".html");
		}
		else {
			deletar(arquivoName+".html");
			criarArquivo(arquivoName+".html");
		}
	}
	
	private void criarArquivo(String nomeArquivo) throws IOException {
		File f  = new File(nomeArquivo);
		if(!f.exists()) {
			f.createNewFile();
		}
		this.file=f;		
	}
	
	private void adicionaLinha(String conteudo) {
		FileReader in;
		try {
			in = new FileReader(this.file);
			BufferedReader buff = new BufferedReader(in);
			String linha = "";
			List<String> linhas = new ArrayList<String>();
			while((linha = buff.readLine())!=null) {
				linhas.add(linha);
			}
			FileWriter wr = new FileWriter(this.file);
			PrintWriter pw = new PrintWriter(wr);
			for(String linhaA:linhas){
				pw.write(linhaA);
				pw.println();
			}
			pw.write(conteudo);
			wr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void deletar(String nomeArquivo) throws IOException {
		File f = new File(".");
		//f  = new File(f.getCanonicalPath()+"\\resources\\"+nomeArquivo);
		f  = new File(nomeArquivo);
		
		if(f.exists()) {
			FileWriter wr = new FileWriter(f);
			wr.write("");
			f.delete();
		}
	}

	@Override
	public void addTransactionValue(Transaction transaction, String[] ftrs,
			String[] dets, Complexity complexity, int totalFunctionsPoint, int releaseNumber) {
			if(this.transactions.get(transaction.getParent().getParent())==null){
				this.transactions.put(transaction.getParent().getParent(), new ArrayList<WebFunctionPointView.TransactionValues>());
			}
			TransactionValues transactionValues = new TransactionValues();
			transactionValues.setTransaction(transaction);
			transactionValues.setDets(dets);
			transactionValues.setFtrs(ftrs);
			transactionValues.setFunctionsPointValue(totalFunctionsPoint);
			transactionValues.setComplexity(complexity);
			transactionValues.setReleaseNumber(releaseNumber);
			this.transactions.get(transaction.getParent().getParent()).add(transactionValues);
	}

	@Override
	public void renderDataModelValue(DataModel dataModel,
			int totalDataModelElement, int totalFunctionsPoint) {
			DataModelValue dataModelValue = new DataModelValue();
			dataModelValue.setDataModel(dataModel);
			dataModelValue.setDataModelElements(totalDataModelElement);
			dataModelValue.setFunctionsPointValue(totalFunctionsPoint);
			if(this.dataModelValues.get(dataModel.getParent())==null){
				this.dataModelValues.put(dataModel.getParent(), dataModelValue);
			}
	}

	@Override
	public void addDataModelElementValue(DataModelElement dataModelElement,
			List<String[]> rets, String[] dets, Complexity complexity, int totalFunctionsPoint, Map<Integer,Integer> adjustmentsFactors) {
			//adicionaLinha("### "+dataModelElement.getType().name()+" - "+dataModelElement.getName()+" (DETs: "+dets.length+", RETs: "+rets.size()+"). Pontos por Função: "+totalFunctionsPoint);
			if(this.dataElements.get(dataModelElement.getParent().getParent())==null){
				this.dataElements.put(dataModelElement.getParent().getParent(), new ArrayList<WebFunctionPointView.DataElementValues>());
			}
			DataElementValues dataElementValues = new DataElementValues();
			dataElementValues.setDataModelElement(dataModelElement);
			dataElementValues.setDets(dets);
			dataElementValues.setAdjustmentsFactors(adjustmentsFactors);
			dataElementValues.setRets(rets);
			dataElementValues.setFunctionsPointValue(totalFunctionsPoint);
			dataElementValues.setComplexity(complexity);
			this.dataElements.get(dataModelElement.getParent().getParent()).add(dataElementValues);
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public void renderNoUsedField(DET field) {
//		adicionaLinha("Campo removido = "+field.getParent().getName()+"/"+field.getName());		
	}
	
	protected class DataElementValues{
		private DataModelElement dataModelElement;
		private List<String[]> rets;
		private String[] dets;
		private Complexity complexity;
		private Integer functionsPointValue;
		private Map<Integer,Integer> adjustmentsFactors;
		
		public Map<Integer, Integer> getAdjustmentsFactors() {
			return adjustmentsFactors;
		}
		public void setAdjustmentsFactors(Map<Integer, Integer> adjustmentsFactors) {
			this.adjustmentsFactors = adjustmentsFactors;
		}
		public Complexity getComplexity() {
			return complexity;
		}
		public void setComplexity(Complexity complexity) {
			this.complexity = complexity;
		}
		public String[] getDets() {
			return dets;
		}
		public void setDets(String[] dets) {
			this.dets = dets;
		}
		public Integer getFunctionsPointValue() {
			return functionsPointValue;
		}
		public void setFunctionsPointValue(Integer functionsPointValue) {
			this.functionsPointValue = functionsPointValue;
		}
		public DataModelElement getDataModelElement() {
			return dataModelElement;
		}
		public void setDataModelElement(DataModelElement dataModelElement) {
			this.dataModelElement = dataModelElement;
		}
		public List<String[]> getRets() {
			return rets;
		}
		public void setRets(List<String[]> rets) {
			this.rets = rets;
		}
	}
	protected class DataModelValue{
		private Integer functionsPointValue;
		private Integer dataModelElements;
		private DataModel dataModel;
		public Integer getFunctionsPointValue() {
			return functionsPointValue;
		}
		public void setFunctionsPointValue(Integer functionsPointValue) {
			this.functionsPointValue = functionsPointValue;
		}
		public DataModel getDataModel() {
			return dataModel;
		}
		public void setDataModel(DataModel dataModel) {
			this.dataModel = dataModel;
		}
		public Integer getDataModelElements() {
			return dataModelElements;
		}
		public void setDataModelElements(Integer dataModelElements) {
			this.dataModelElements = dataModelElements;
		}
	}
	protected class TransactionModelValue{
		private Integer functionsPointValue;
		private Integer transactions;
		private TransactionModel transactionModel;
		public Integer getFunctionsPointValue() {
			return functionsPointValue;
		}
		public void setFunctionsPointValue(Integer functionsPointValue) {
			this.functionsPointValue = functionsPointValue;
		}
		public Integer getTransactions() {
			return transactions;
		}
		public void setTransactions(Integer transactions) {
			this.transactions = transactions;
		}
		public TransactionModel getTransactionModel() {
			return transactionModel;
		}
		public void setTransactionModel(TransactionModel transactionModel) {
			this.transactionModel = transactionModel;
		}
	}
	protected class TransactionValues{
		private Transaction transaction;
		private String[] ftrs;
		private String[] dets;
		private Complexity complexity;
		private Integer functionsPointValue;
		private Integer releaseNumber;
		
		public Integer getReleaseNumber() {
			return releaseNumber;
		}
		public void setReleaseNumber(Integer releaseNumber) {
			this.releaseNumber = releaseNumber;
		}
		public Complexity getComplexity() {
			return complexity;
		}
		public void setComplexity(Complexity complexity) {
			this.complexity = complexity;
		}
		public String[] getFtrs() {
			return ftrs;
		}
		public void setFtrs(String[] ftrs) {
			this.ftrs = ftrs;
		}
		public Transaction getTransaction() {
			return transaction;
		}
		public void setTransaction(Transaction transaction) {
			this.transaction = transaction;
		}
		public String[] getDets() {
			return dets;
		}
		public void setDets(String[] dets) {
			this.dets = dets;
		}
		public Integer getFunctionsPointValue() {
			return functionsPointValue;
		}
		public void setFunctionsPointValue(Integer functionsPointValue) {
			this.functionsPointValue = functionsPointValue;
		}
	}

	@Override
	public void render() {
		String html = "<html>";
		html += "<script type=\"text/javascript\" src=\"../overlib.js\"></script>" +
				"<style> body {" +
				"margin:0; " +
				"padding:0; " +
				"}</style>";
		html += "<body>" +
				"<div id=\"overDiv\" style=\"position:absolute; visibility:hidden; z-index:1000;\"></div>" +
				"<div sytle=' margin-left: auto;margin-right: auto;'>";
		html += "<h2>Dados</h2><table id='dataModel' border='1'>";
		html += "<tr><th>&nbsp;</th>";
		Set<String> dataElementNames = new HashSet<String>();
		int cont=0;
		for(FunctionPointSystem functionPointSystem:this.dataElements.keySet()){
			if(cont==0)
				html += "<th colspan=5>Referência</th>";
			else 
				html += "<th colspan=5>Escopo "+cont+"</th>";
			cont++;
			for(DataElementValues dataElementValues:this.dataElements.get(functionPointSystem)) {
				dataElementNames.add(dataElementValues.getDataModelElement().getName());
			}
			
		}
		html += "</tr>";
		html += "<tr><th>&nbsp;</th>";
		for(int i =0;i<this.dataElements.keySet().size();i++){
			html += "<th>Tipo</th><th>RETs</th><th>DETs</th><th>Complexidade</th><th>P.F.</th>";
		}
		html += "</tr>";
		List<String> ordenadedDataElementNames = new ArrayList<String>(dataElementNames);
		Collections.sort(ordenadedDataElementNames);
		for(String nameDataElement:ordenadedDataElementNames){
			html += "<tr><td>"+nameDataElement;
			html += "</td>";
			for(FunctionPointSystem functionPointSystem:this.dataElements.keySet()){
				boolean find= false;
				for(DataElementValues dataElementValues:this.dataElements.get(functionPointSystem)){					
					if(dataElementValues.getDataModelElement().getName().equals(nameDataElement)){
						String rets = "";
						String dets = "";
						for(String[] ret: dataElementValues.getRets()){
							rets+="<p>";
							for(int i=0;i<ret.length;i++){
								rets+=""+ret[i]+";&nbsp;";
							}
							rets+="</p>";
						}
						for(int i=0;i<dataElementValues.getDets().length;i++){
							dets+="<p>"+dataElementValues.getDets()[i]+"</p>";
						}
						
						
						html += "<td>"+dataElementValues.getDataModelElement().getType().name()+"</td>" +
								"<td><a href=\"javascript:void(0);\" onmouseover=\"return overlib('"+rets+"');\" onmouseout=\"return nd();\">"+dataElementValues.getRets().size()+"</a></td>" +
								"<td><a href=\"javascript:void(0);\" onmouseover=\"return overlib('"+dets+"');\" onmouseout=\"return nd();\">"+dataElementValues.getDets().length+"</a></td>" +
								"<td>"+dataElementValues.getComplexity().name()+"</td>" +
								"<td>"+dataElementValues.getFunctionsPointValue();
						if(dataElementValues.getAdjustmentsFactors()!=null) {
							html += "(";
							int countFactors = 0;
							for (Integer adjustmentFactor : dataElementValues.getAdjustmentsFactors().values()) {
								if(countFactors>0)
									html+=", ";
								html += adjustmentFactor;
								countFactors++;
							}
							html += ")";
						}
						html+="</td>";
						find = true;
					}
				}
				if(!find)
					html += "<td class='dataNotFind'>-</td><td class='dataNotFind'>0</td><td class='dataNotFind'>0</td><td class='dataNotFind'>-</td><td class='dataNotFind'>0</td>";
			}
			html += "</tr>";
		}
		html += "<tr class='footer'><th>&nbsp;</th>";
		for(FunctionPointSystem functionPointSystem:this.dataModelValues.keySet()){
			html += "<td colspan='4'>"+this.dataModelValues.get(functionPointSystem).getDataModelElements()+"</td><td>"+this.dataModelValues.get(functionPointSystem).getFunctionsPointValue()+"</td>";
		}
		html += "</tr>";
		html += "</table>";
		
		
		html += "<h2>Transações</h2><table id='transactionModel' border='1'>";
		html += "<tr><th>&nbsp;</th><th>&nbsp;</th>";
		Set<String> transactionElementNames = new HashSet<String>();
		cont = 0;
		for(FunctionPointSystem functionPointSystem:this.transactions.keySet()){
			if(cont==0)
				html += "<th colspan=5>Referência</th>";
			else 
				html += "<th colspan=5>Escopo "+cont+"</th>";
			cont++;
			for(TransactionValues transactionValues:this.transactions.get(functionPointSystem)) {
				transactionElementNames.add(transactionValues.getTransaction().getName());
			}
		}
		html += "</tr>";
		html += "<tr><th>&nbsp;</th><th>&nbsp;</th>";
		for(int i =0;i<this.transactions.keySet().size();i++){
			html += "<th>Tipo</th><th>FTRs</th><th>DETs</th><th>Complexidade</th><th>P.F.</th>";
		}
		html += "</tr>";
		List<String> ordenadedTransactionsNames = new ArrayList<String>(transactionElementNames);
		Collections.sort(ordenadedTransactionsNames);
		int countTransactions=0;
		for(String nameTransaction:ordenadedTransactionsNames){
			countTransactions++;
			html += "<tr><td>"+countTransactions+"</td><td>"+nameTransaction+"</td>";
			for(FunctionPointSystem functionPointSystem:this.transactions.keySet()){
				boolean find= false;
				for(TransactionValues transactionValues:this.transactions.get(functionPointSystem)){
					if(transactionValues.getTransaction().getName().equals(nameTransaction)){
						String ftrs = "";
						String dets = "";
						for(int i=0;i<transactionValues.getFtrs().length;i++){
							ftrs+="<p>"+transactionValues.getFtrs()[i]+"</p>";
						}
						for(int i=0;i<transactionValues.getDets().length;i++){
							dets+="<p>"+transactionValues.getDets()[i]+"</p>";
						}
						if(transactionValues.getComplexity()==null) {
							System.out.print(transactionValues.getTransaction().getName());
						}
						html += "<td>"+transactionValues.getTransaction().getType().name()+"</td>" +
								"<td><a href=\"javascript:void(0);\" onmouseover=\"return overlib('"+ftrs+"');\" onmouseout=\"return nd();\">"+transactionValues.getFtrs().length+"</a></td>" +
								"<td><a href=\"javascript:void(0);\" onmouseover=\"return overlib('"+dets+"');\" onmouseout=\"return nd();\">"+transactionValues.getDets().length+"</a></td>" +
								"<td>"+transactionValues.getComplexity().name()+"</td>" +
								"<td>"+transactionValues.getFunctionsPointValue()+"</td>";
						find = true;
					}
				}
				if(!find)
					html += "<td class='transactionNotFind'>-</td><td class='transactionNotFind'>0</td><td class='transactionNotFind'>0</td><td class='transactionNotFind'>-</td><td class='transactionNotFind'>0</td>";
			}
			html += "</tr>";
		}
		html += "<tr class='footer'><th>&nbsp;</th><th>&nbsp;</th>";
		
		for(FunctionPointSystem functionPointSystem:this.transactionModelValues.keySet()){
			html += "<td  colspan='4'>"+this.transactionModelValues.get(functionPointSystem).getTransactions()+"</td><td>"+this.transactionModelValues.get(functionPointSystem).getFunctionsPointValue()+"</td>";
		}
		html += "</tr>";		
		html += "</table>";
		
		html += "<h2>Total</h2><table id='total' border='1'>";
		html += "<tr><th></th>";
		for(int i =0;i<this.transactionModelValues.size();i++){
			if(i==0)
				html += "<th>Referência</th>";
			else 
				html += "<th>Escopo "+i+"</th>";
		}
		html += "</tr><tr><th>P.F</th>";
		for(FunctionPointSystem functionPointSystem:this.transactionModelValues.keySet()){
			html += "<td>"+(this.transactionModelValues.get(functionPointSystem).getFunctionsPointValue()+this.dataModelValues.get(functionPointSystem).getFunctionsPointValue())+"</td>";
		}
		html += "</tr>";
		html += "<tr><th>Satisfação</th>";
		for(FunctionPointSystem functionPointSystem:this.satisfaction.keySet()){
			html += "<td>"+(this.satisfaction.get(functionPointSystem))+"</td>";
		}
		html += "</tr>";
		html += "<tr><th>Satisfação(%)</th><td>&nbsp;</td>";
		for(FunctionPointSystem functionPointSystem:this.satisfactionPercent.keySet()){
			html += "<td>"+(this.satisfactionPercent.get(functionPointSystem))+"%</td>";
		}
		html += "</tr>";
		html += "</table>";
		html += "</div></body>";
		html+="</html>";
		adicionaLinha(html);
	}

	@Override
	public void addSatisfactionForFunctionPoint(
			FunctionPointSystem functionPointSystem, long percent) {
		this.satisfaction.put(functionPointSystem, percent);
		
	}
}
