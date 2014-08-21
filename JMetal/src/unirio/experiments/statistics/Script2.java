package unirio.experiments.statistics;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import unirio.experiments.statistics.ExperimentalData2.CycleInfos;

public class Script2
{
private PrintWriter out;
	
	public Script2()
	{
	}
	
	public Script2(String filename) throws IOException
	{
		out = new PrintWriter(new FileWriter(filename));
		out.println("graphics.off();");
	}
	
	public DataSet writeDataFile(String filename, ExperimentalData2 experimentalData, String dataSetName) throws IOException
	{
		DecimalFormat df4 = new DecimalFormat("0.############");
		FileWriter file = new FileWriter(filename);
		PrintWriter out = new PrintWriter(file);
		
		String[] groups = new String[experimentalData.getData().get(0).getInfoValue().keySet().size()+6];
		int b = 0;
		for (String group : experimentalData.getData().get(0).getInfoValue().keySet())
		{
			if(b!=0)
				out.print("\t");
			out.print(group);
			groups[b] = group;
			b++;
		}
		out.print("\tMaxEvaluations");
		out.print("\tCycle");
		out.print("\tAlgorithm");
		out.print("\tInstance");
		out.print("\tExecution");
		out.print("\tApproach");
		for (CycleInfos cycleInfos : experimentalData.getData())
		{
			out.println();
			int i =0;
			int solutionSize = cycleInfos.getInfoValue().get(groups[0]).length;
			for(int a=0;a<solutionSize;a++) {
				for (String group : groups)
				{
					if(group==null)
						continue;
					if(i!=0)
						out.print("\t");
					out.print(df4.format(cycleInfos.getInfoValue().get(group)[a]).replace(",", "."));
					i++;
				}
				out.print("\t"+experimentalData.getMaxEvaluations());
				out.print("\t"+cycleInfos.getCycleNumber());
				out.print("\t"+experimentalData.getAlgorithmName().replace(" ", ""));
				out.print("\t"+experimentalData.getInstanceName().replace(" ", ""));
				out.print("\t"+experimentalData.getExecutionNumber());
				out.print("\t"+experimentalData.getApproach().replace(" ", ""));
			}			
		}
		groups[b++] = "MaxEvaluations";
		groups[b++] = "Cycle";
		groups[b++] = "Algorithm";
		groups[b++] = "Instance";
		groups[b++] = "Execution";
		groups[b++] = "Approach";		
		out.close();
		DataSet loadedFile = new DataSet();
		loadedFile.setAlias(dataSetName);
		loadedFile.setHeaders(groups);
		return loadedFile;
	}
	
	public DataSet writeDataFile(String filename, List<ExperimentalData2> experimentalDataSet, String dataSetName) throws IOException
	{
		DecimalFormat df4 = new DecimalFormat("0.############");
		FileWriter file = new FileWriter(filename);
		PrintWriter out = new PrintWriter(file);
		
		String[] groups = new String[experimentalDataSet.get(0).getData().get(0).getInfoValue().keySet().size()+6];
		int b = 0;
		for (String group : experimentalDataSet.get(0).getData().get(0).getInfoValue().keySet())
		{
			if(b!=0)
				out.print("\t");
			out.print(group);
			groups[b] = group;
			b++;
		}
		out.print("\tMaxEvaluations");
		out.print("\tCycle");
		out.print("\tAlgorithm");
		out.print("\tInstance");
		out.print("\tExecution");
		out.print("\tApproach");
		for (ExperimentalData2 experimentalData : experimentalDataSet)
		{
			for (CycleInfos cycleInfos : experimentalData.getData())
			{
				int solutionSize = cycleInfos.getInfoValue().get(groups[0]).length;
				for(int a=0;a<solutionSize;a++) {
					int i =0;
					out.println();
					for (String group : groups)
					{
						if(group==null)
							continue;
						if(i!=0)
							out.print("\t");
						out.print(df4.format(cycleInfos.getInfoValue().get(group)[a]).replace(",", "."));
						i++;
					}
					out.print("\t"+experimentalData.getMaxEvaluations());
					out.print("\t"+cycleInfos.getCycleNumber());
					out.print("\t"+experimentalData.getAlgorithmName().replace(" ", ""));
					out.print("\t"+experimentalData.getInstanceName().replace(" ", ""));	
					out.print("\t"+experimentalData.getExecutionNumber());
					out.print("\t"+experimentalData.getApproach().replace(" ", ""));
					
				}
				
			}
		}
		groups[b++] = "MaxEvaluations";
		groups[b++] = "Cycle";
		groups[b++] = "Algorithm";
		groups[b++] = "Instance";
		groups[b++] = "Execution";
		groups[b++] = "Approach";
		out.close();
		DataSet loadedFile = new DataSet();
		loadedFile.setAlias(dataSetName);
		loadedFile.setHeaders(groups);
		return loadedFile;
	}
	
//	public void writeDataFile(String filename, ParetoFront paretoFront) throws IOException
//	{
//		DecimalFormat df4 = new DecimalFormat("0.############");
//		FileWriter file = new FileWriter(filename);
//		PrintWriter out = new PrintWriter(file);
//		
//		for (String group : experimentalData.getData().get(0).getInfoValue().keySet())
//		{
//			out.print("\t" + group);
//		}
//		
//		for (CycleInfos cycleInfos : experimentalData.getData())
//		{
//			out.println();
//			int i =0;
//			for (String group : cycleInfos.getInfoValue().keySet())
//			{
//				if(i!=0)
//					out.print("\t");
//				out.print(df4.format(cycleInfos.getInfoValue().get(group)).replace(",", "."));
//				i++;
//			}
//		}
//		
//		out.close();
//	}


	private String getExperimentalDataVariables(ExperimentalData2 experimentalData, String type, String[] prefix)
	{
		String variables = "";
		
		if (experimentalData.getData().get(0).getInfoValue().containsKey(type))
		{
				for (String p : prefix)
				{
					if (variables.length() > 0)
						variables += ", ";
					
					variables += p + "$" + type;
				}
		}

		return variables;
	}

	public void loadFile(DataSet prefix, String filename)
	{
		out.println(prefix.getAlias() + " <- read.table(\"" + filename + "\", header=TRUE);");
	}

	public void drawDispersionPlot(DataSet dataSet, String xtitle, String ytitle, String title, List<String> instancesName, Set<String> algorithmsName, String xHeader, String yHeader, String file, Integer executionNumber)
	{
		out.println(); 
		out.println("jpeg(filename = \""+file+"\", width = 640, height = "+(int) Math.ceil(instancesName.size() / 2)+" * 320, "+
			     "units = \"px\", pointsize = 12, quality = 180,"+
			     "bg = \"white\",  res = NA)");
		
		out.println("par(mfrow=c("+(int) Math.ceil(instancesName.size() / 2)+",2))");
		for (String instance : instancesName)
		{
			String instanceName = instance;
			instance = instance.replace(" ", "");
			int i = 1;
			out.println(dataSet.getAlias()+instance+"<-subset("+dataSet.getAlias()+","+dataSet.getAlias()+"$Instance==\""+instance+"\" & "+dataSet.getAlias()+"$Cycle==0 & "+dataSet.getAlias()+"$Execution=="+executionNumber+" & " +
					"MaxEvaluations==max(subset("+dataSet.getAlias()+", "+dataSet.getAlias()+"$Execution=="+executionNumber+" & Instance ==\""+instance+"\" & Cycle==0)$MaxEvaluations))");
			Map<String, Integer> charsForAlgorthms = new HashMap<String, Integer>();
			for (String algorithm : algorithmsName)
			{
				algorithm = algorithm.replace(" ", "");
				out.println(dataSet.getAlias()+instance+algorithm+"<-subset("+dataSet.getAlias()+","+dataSet.getAlias()+"$Algorithm==\""+algorithm+"\" & "+dataSet.getAlias()+"$Instance==\""+instance+"\" & "+dataSet.getAlias()+"$Cycle==0 & "+dataSet.getAlias()+"$Execution=="+executionNumber+" & " +
						"MaxEvaluations==max(subset("+dataSet.getAlias()+", "+dataSet.getAlias()+"$Execution=="+executionNumber+" & Instance ==\""+instance+"\" & Cycle==0)$MaxEvaluations))");
				if(i==1) {
					out.println("plot("+dataSet.getAlias()+instance+algorithm+"$"+xHeader+"," +
							dataSet.getAlias()+instance+algorithm+"$"+yHeader+
							",xlab=\""+xtitle+"\",ylab=\""+ytitle+"\", pch="+(i)+", xlim=range(0:max("+dataSet.getAlias()+instance+"$"+xHeader+")),ylim=range(0:max("+dataSet.getAlias()+instance+"$"+yHeader+")))");
				}
				else {
					out.println("points("+dataSet.getAlias()+instance+algorithm+"$"+xHeader+"," +
							dataSet.getAlias()+instance+algorithm+"$"+yHeader+
							", pch="+(i)+")");
				
				}
				charsForAlgorthms.put(algorithm, i);
				i++;
			}
			String c = "c(";
			String pch = "pch = c(";
			int a = 0;
			for (String alg : charsForAlgorthms.keySet())
			{
				if(a!=0) {
					c = c+",";
					pch = pch+",";
				}
				c = c+"\""+alg+"\"";
				pch = pch+charsForAlgorthms.get(alg);
				a++;
			}
			c = c+")";
			pch = pch+")";
			out.println("legend(max("+dataSet.getAlias()+instance+"$"+xHeader+")-(max("+dataSet.getAlias()+instance+"$"+xHeader+")/3), max("+dataSet.getAlias()+instance+"$"+yHeader+")/4,"+c+", "+pch+")");
			out.println("title(main=\"" + instanceName + "\", col.main=\"red\", font.main=4)");
			
		}
		out.println("dev.off()"); 
		
		//out.println("text(0:" + (1-1) + "*2+1, par(\"usr\")[3] - (par(\"usr\")[4] - par(\"usr\")[3]) / 100, srt=45, adj=1, labels=c(" + experimentalData.getAlgorithmName() + "), xpd=T, cex=0.7)"); 
		
	}
	
	public void drawBoxPlot(DataSet dataSet, String title, String type, List<String> instancesName, String file, String xtitle, String ytitle, String dataSetHeaderTo, Integer executionNumber)
	{
		out.println(); 
		out.println("jpeg(filename = \""+file+"\", width = 640, height = "+(int) Math.ceil(instancesName.size() / 2)+" * 320, "+
			     "units = \"px\", pointsize = 12, quality = 180,"+
			     "bg = \"white\",  res = NA)");
		
		out.println("par(mfrow=c("+(int) Math.ceil(instancesName.size() / 2)+",2))");
		
		//out.println("title(main=\"" + title + "\", col.main=\"red\", font.main=4)");
		for (String instance : instancesName)
		{
			String aux = "";
			
			String instanceName = instance;
			instance = instance.replace(" ", "");
			if(dataSetHeaderTo.equals("Algorithm")) {
				aux = " & MaxEvaluations==max(subset("+dataSet.getAlias()+", "+dataSet.getAlias()+"$Execution=="+executionNumber+" & Instance ==\""+instance+"\")$MaxEvaluations)";
			}
			out.println(dataSet.getAlias()+type+"Sub<-subset("+dataSet.getAlias()+", "+dataSet.getAlias()+"$Execution=="+executionNumber+" & Instance ==\""+instance+"\" "+aux+" )");
			out.println("boxplot("+dataSet.getAlias()+type+"Sub$"+type+"~" + dataSet.getAlias()+type+"Sub$"+dataSetHeaderTo+")");
			out.println("title(main=\"" + instanceName + "\", col.main=\"red\", font.main=4)");
			out.println("title(xlab=\"" + xtitle + "\", ylab=\"" + ytitle + "\")");
		}
		//out.println("text(0:" + (1-1) + "*2+1, par(\"usr\")[3] - (par(\"usr\")[4] - par(\"usr\")[3]) / 100, srt=45, adj=1, labels=c(" + experimentalData.getAlgorithmName() + "), xpd=T, cex=0.7)"); 
		
		out.println("dev.off()");	
	}
	
	public void drawLinePlot(DataSet dataSet, String title, String type, String variable, List<String> variablesName, Set<String> fieldsName, Map<String, SortedSet<Long>> evaluationsByInstance, String file, String xtitle, String ytitle, String dataSetHeaderTo, Integer executionNumber)
	{
		out.println(); 
		int total = (new BigDecimal(((double)variablesName.size())/2)).setScale(0,BigDecimal.ROUND_UP).intValue();
		out.println("jpeg(filename = \""+file+"\", width = 640, height = "+total+" * 320, "+
			     "units = \"px\", pointsize = 12, quality = 180,"+
			     "bg = \"white\",  res = NA)");
		
		out.println("par(mfrow=c("+total+",2))");
		//out.println("title(main=\"" + title + "\", col.main=\"red\", font.main=4)");
		String fatorMin = "1";
		if(type.equals("sprd")) {
			fatorMin = "0.8";
		}
		for (String variableName : variablesName)
		{
			String instanceName = variableName;
			variableName = variableName.replace(" ", "");
			out.println(dataSet.getAlias()+type+"Sub<-subset("+dataSet.getAlias()+", "+dataSet.getAlias()+"$Execution=="+executionNumber+" & "+variable+" ==\""+variableName+"\" )");
			out.println("plot(c(min("+dataSet.getAlias()+type+"Sub$MaxEvaluations),max("+dataSet.getAlias()+type+"Sub$MaxEvaluations)),c(min("+dataSet.getAlias()+type+"Sub$"+type+")*"+fatorMin+",max("+dataSet.getAlias()+type+"Sub$"+type+")),type='n',xlab=NA,ylab=NA)");
			String[] colors = new String[]{"black", "black", "black", "black"};
			int a=1;
			String cAlgorithm = "c(";
			String cPch = "c(";
			for (String field : fieldsName)
			{
				if(a!=1) {
					cAlgorithm+=",";
					cPch+=",";
					
				}
				cAlgorithm+="\""+field+ "\"";
				cPch+=a;
				String cEvaluations = "c(";
				String cMean = "c(";
				int i =0;
				for (Long evaluation : evaluationsByInstance.get(instanceName))
				{
					if(i!=0) {
						cEvaluations+=",";
						cMean+=",";
					}
					cEvaluations+=""+evaluation+"";
					cMean+="mean(subset("+dataSet.getAlias()+type+"Sub, "+dataSet.getAlias()+type+"Sub$MaxEvaluations == "+evaluation+" & "+dataSet.getAlias()+type+"Sub$"+dataSetHeaderTo+" == \""+field+"\")$"+type+")";
					i++;
				}
				cEvaluations += ")";
				cMean += ")";
				out.println("lines("+cMean+"~" + cEvaluations + ",type='l',col=\""+colors[a-1]+"\")");			
				out.println("points("+cMean+"~" + cEvaluations+
						", pch="+(a)+")");
				a++;
			}
			cAlgorithm+=")";
			cPch+=")";
			String fatorX = "0.234";
			String fatorY = "1.45";
			
			if(type=="hpvl" && variableName.equals("BolsaDeValores")) {
				fatorX = "0.4";
			}
			else if(type=="gdst") {
				fatorX = "1";
			}
			else if(type=="errt") {
				//fatorX = "0.21";
				fatorY = "9.5";
			}
			else if(type=="sprd") {
				fatorX = "0.175";
				fatorY = "9.9";
			}
			String maxEvaluations = "";
			if(dataSetHeaderTo.equals("Algorithm")) {
				if(variableName.equals("BolsaDeValores")) {
					maxEvaluations="(160.000)";
				}
				else if(variableName.equals("GestaoDePessoas")){
					maxEvaluations="(16.900)";
				}
				else if(variableName.equals("Academico")){
					maxEvaluations="(6.084)";
				}
				else if(variableName.equals("Parametros")){
					maxEvaluations="(38.416)";
				}
			}
			out.println("legend( max("+dataSet.getAlias()+type+"Sub$MaxEvaluations)/"+fatorY+", min("+dataSet.getAlias()+type+"Sub$"+type+") + ((max("+dataSet.getAlias()+type+"Sub$"+type+")-min("+dataSet.getAlias()+type+"Sub$"+type+"))*"+fatorX+"),"+cAlgorithm+", pch="+cPch+")");
			
			out.println("title(main=\"" + instanceName + "\", col.main=\"red\", font.main=4)");
			out.println("title(xlab=\"" + xtitle + " "+maxEvaluations+ "\", ylab=\"" + ytitle + "\")");
		}
		//out.println("text(0:" + (1-1) + "*2+1, par(\"usr\")[3] - (par(\"usr\")[4] - par(\"usr\")[3]) / 100, srt=45, adj=1, labels=c(" + experimentalData.getAlgorithmName() + "), xpd=T, cex=0.7)"); 
		
		out.println("dev.off()");	
	}
	
	
	public void drawLinePlotMono(DataSet dataSet, String title, String type, List<String> instancesName, Set<String> algorithmsName, Map<String, SortedSet<Long>> evaluationsByInstance, String file, String xtitle, String ytitle, Integer executionNumber)
	{
		out.println(); 
		out.println("jpeg(filename = \""+file+"\", width = 640, height = "+(int) Math.ceil(instancesName.size() / 2)+" * 320, "+
			     "units = \"px\", pointsize = 12, quality = 180,"+
			     "bg = \"white\",  res = NA)");
		
		out.println("par(mfrow=c("+(int) Math.ceil(instancesName.size() / 2)+",2))");
		//out.println("title(main=\"" + title + "\", col.main=\"red\", font.main=4)");
		String fatorMin = "1";
		
		for (String instance : instancesName)
		{
			String instanceName = instance;
			instance = instance.replace(" ", "");
			out.println(dataSet.getAlias()+type+"Sub<-subset("+dataSet.getAlias()+", "+dataSet.getAlias()+"$Execution=="+executionNumber+" & Instance ==\""+instance+"\" )");
			out.println("plot(c(min("+dataSet.getAlias()+type+"Sub$MaxEvaluations),max("+dataSet.getAlias()+type+"Sub$MaxEvaluations)),c(min("+dataSet.getAlias()+type+"Sub$"+type+")*"+fatorMin+",max("+dataSet.getAlias()+type+"Sub$"+type+")),type='n',xlab=NA,ylab=NA)");
			String[] colors = new String[]{"black", "black", "black", "black"};
			int a=1;
			String cAlgorithm = "c(";
			String cPch = "c(";
			for (String algorithm : algorithmsName)
			{
				if(a!=1) {
					cAlgorithm+=",";
					cPch+=",";
					
				}
				cAlgorithm+="\""+algorithm+ "\"";
				cPch+=a;
				String cEvaluations = "c(";
				String cMean = "c(";
				int i =0;
				for (Long evaluation : evaluationsByInstance.get(instanceName))
				{
					if(i!=0) {
						cEvaluations+=",";
						cMean+=",";
					}
					cEvaluations+=""+evaluation+"";
					cMean+="mean(subset("+dataSet.getAlias()+type+"Sub, "+dataSet.getAlias()+type+"Sub$MaxEvaluations == "+evaluation+" & "+dataSet.getAlias()+type+"Sub$Algorithm == \""+algorithm+"\")$"+type+")";
					i++;
				}
				cEvaluations += ")";
				cMean += ")";
				out.println("lines("+cMean+"~" + cEvaluations + ",type='l',col=\""+colors[a-1]+"\")");			
				out.println("points("+cMean+"~" + cEvaluations+
						", pch="+(a)+")");
				a++;
			}
			cAlgorithm+=")";
			cPch+=")";
			String fatorX = "0.234";
			String fatorY = "1.45";
			if(type=="hpvl" && instance.equals("BolsaDeValores")) {
				fatorX = "0.4";
			}
			else if(type=="gdst") {
				fatorX = "1";
			}
			else if(type=="errt") {
				//fatorX = "0.21";
				fatorY = "9.5";
			}
			else if(type=="sprd") {
				fatorX = "0.175";
				fatorY = "9.9";
			}
			String maxEvaluations = "";
			if(instance.equals("BolsaDeValores")) {
				maxEvaluations="(160.000)";
			}
			else if(instance.equals("GestaoDePessoas")){
				maxEvaluations="(16.900)";
			}
			else if(instance.equals("Academico")){
				maxEvaluations="(6.084)";
			}
			else if(instance.equals("Parametros")){
				maxEvaluations="(38.416)";
			}
			out.println("legend( max("+dataSet.getAlias()+type+"Sub$MaxEvaluations)/"+fatorY+", min("+dataSet.getAlias()+type+"Sub$"+type+") + ((max("+dataSet.getAlias()+type+"Sub$"+type+")-min("+dataSet.getAlias()+type+"Sub$"+type+"))*"+fatorX+"),"+cAlgorithm+", pch="+cPch+")");
			
			out.println("title(main=\"" + instanceName + "\", col.main=\"red\", font.main=4)");
			out.println("title(xlab=\"" + xtitle + " "+maxEvaluations+ "\", ylab=\"" + ytitle + "\")");
		}
		//out.println("text(0:" + (1-1) + "*2+1, par(\"usr\")[3] - (par(\"usr\")[4] - par(\"usr\")[3]) / 100, srt=45, adj=1, labels=c(" + experimentalData.getAlgorithmName() + "), xpd=T, cex=0.7)"); 
		
		out.println("dev.off()");	
	}

	public void drawBoxPlot(ExperimentalData2 experimentalData, String type, String prefix, String title)
	{
		String[] prefixes = { prefix };
		drawBoxPlot(experimentalData, type, prefixes, title);
	}

	public void drawBoxPlot(ExperimentalData2 experimentalData, String type, String[] prefix, String title)
	{
		String variables = getExperimentalDataVariables(experimentalData, type, prefix);
		out.println(); 
		out.println("windows()"); 
		out.println("boxplot(" + variables + ", xaxt='n')");
		out.println("text(0:" + (1-1) + "*2+1, par(\"usr\")[3] - (par(\"usr\")[4] - par(\"usr\")[3]) / 100, srt=45, adj=1, labels=c(" + experimentalData.getAlgorithmName() + "), xpd=T, cex=0.7)"); 
		out.println("title(main=\"" + title + "\", col.main=\"red\", font.main=4)");
	}
	public void tableKolmogorovSmirnov(ExperimentalData2 experimentalData, String type, String prefix1)
	{
		//String names = getExperimentalDataNames(columns, type);
		int count = 0;
		String result = "";
		
		if (experimentalData.getData().get(0).getInfoValue().containsKey(type))
		{
				result += "ks_p_" + type + count++ + " <- ks.test(" + prefix1 + "$" + type + ", \"pnorm\", mean=mean(" + prefix1 + "$" + type + "), sd=sd(" + prefix1 + "$" + type + "))\n";
				
		}
		//result += "names <- c(" + names + ")\n";
		result += "ks_pvalue_" + prefix1 + "_" + type + " <- c(" + "ks_p_" + type + "0$p.value";
		
		for (int i = 1; i < count; i++)
			result += ", p_" + type + i + "$p.value";
		
		out.println(); 
		out.println(result + ")");
	}
	
	public void tableMannWhitney(ExperimentalData2 experimentalData, String type, String prefix1, String prefix2)
	{
		//String names = getExperimentalDataNames(columns, type);
		int count = 0;
		String result = "";
		
		if (experimentalData.getData().get(0).getInfoValue().containsKey(type))
		{
				result += "p_" + type + count++ + " <- wilcox.test(" + prefix1 + "$" + type + ", " + prefix2 + "$" + type + ")\n";
				
		}
		//result += "names <- c(" + names + ")\n";
		result += "pvalue_" + prefix1 + "_" + prefix2 + "_" + type + " <- c(" + "p_" + type + "0$p.value";
		
		for (int i = 1; i < count; i++)
			result += ", p_" + type + i + "$p.value";
		
		out.println(); 
		out.println(result + ")");
	}

	public void tableEffectSize(ExperimentalData2 experimentalData, String type, String prefix1, String prefix2)
	{
		//String names = getExperimentalDataNames(columns, type);
		String result = "";
		int count = 0;
		
		if (experimentalData.getData().get(0).getInfoValue().containsKey(type))
		{
				String vetor1 = prefix1 + "$" + type;
				String vetor2 = prefix2 + "$" + type;
				
				result += "rx = sum(rank(c(" + vetor1 + ", " + vetor2 + "))[seq_along(" + vetor1 + ")])\n";
				result += "ry = sum(rank(c(" + vetor2 + ", " + vetor1 + "))[seq_along(" + vetor2 + ")])\n";
				result += "eff1_" + type + count + " = (rx / length("+vetor1+") - (length("+vetor1+") + 1) / 2) / length("+vetor2+")\n";
				result += "eff2_" + type + count + " = (ry / length("+vetor2+") - (length("+vetor2+") + 1) / 2) / length("+vetor1+")\n";
				count++;
		}
		
		//result += "names <- c(" + names + ")\n";
		result += "effectsize1_" + prefix1 + "_" + prefix2 + "_" + type + " <- c(" + "eff1_" + type + "0";
		
		for (int i = 1; i < count; i++)
			result += ", eff1_" + type + i;
		
		result += ")\n";

		result += "effectsize2_" + prefix1 + "_" + prefix2 + "_" + type + " <- c(" + "eff2_" + type + "0";
		
		for (int i = 1; i < count; i++)
			result += ", eff2_" + type + i;
		
		out.println(); 
		out.println(result + ")");
	}

	public void tableMean(ExperimentalData2 experimentalData, String type, String prefix)
	{
		//String names = getExperimentalDataNames(columns, type);
		String result = "";
		int count = 0;
		
		if (experimentalData.getData().get(0).getInfoValue().containsKey(type))
		{
			
				String vetor1 = prefix + "$" + type;
				result += "mean_" + prefix + "_" + type + count + " = mean(" + vetor1 + ")\n";
				count++;
			
		}
		
		//result += "names <- c(" + names + ")\n";
		result += "mean_" + prefix + "_" + type + " <- c(mean_" + prefix + "_" + type + "0";
		
		for (int i = 1; i < count; i++) 
			result += ", mean_" + prefix + "_" + type + i;
		
		result += ")\n";

		out.println(); 
		out.println(result);
	}

	public void tableStandardDeviation(ExperimentalData2 experimentalData, String type, String prefix)
	{
		//String names = getExperimentalDataNames(columns, type);
		String result = "";
		int count = 0;
		
		if (experimentalData.getData().get(0).getInfoValue().containsKey(type))
		{
				String vetor1 = prefix + "$" + type;
				result += "sd_" + prefix + "_" + type + count + " = sd(" + vetor1 + ")\n";
				count++;
			
		}
		
		//result += "names <- c(" + names + ")\n";
		result += "sd_" + prefix + "_" + type + " <- c(sd_" + prefix + "_" + type + "0";
		
		for (int i = 1; i < count; i++) 
			result += ", sd_" + prefix + "_" + type + i;
		
		result += ")\n";

		out.println(); 
		out.println(result); 
	}
	
	
	public void tableMin(ExperimentalData2 experimentalData, String type, String prefix)
	{
		//String names = getExperimentalDataNames(columns, type);
		String result = "";
		int count = 0;
		
		if (experimentalData.getData().get(0).getInfoValue().containsKey(type))
		{
				String vetor1 = prefix + "$" + type;
				result += "min_" + prefix + "_" + type + count + " = min(" + vetor1 + ")\n";
				count++;
			
		}
		
		//result += "names <- c(" + names + ")\n";
		result += "min_" + prefix + "_" + type + " <- c(min_" + prefix + "_" + type + "0";
		
		for (int i = 1; i < count; i++) 
			result += ", min_" + prefix + "_" + type + i;
		
		result += ")\n";

		out.println(); 
		out.println(result); 
	}
	
	
	public void tableMax(ExperimentalData2 experimentalData, String type, String prefix)
	{
		//String names = getExperimentalDataNames(columns, type);
		String result = "";
		int count = 0;
		
		if (experimentalData.getData().get(0).getInfoValue().containsKey(type))
		{
				String vetor1 = prefix + "$" + type;
				result += "max_" + prefix + "_" + type + count + " = max(" + vetor1 + ")\n";
				count++;
			
		}
		
		//result += "names <- c(" + names + ")\n";
		result += "max_" + prefix + "_" + type + " <- c(max_" + prefix + "_" + type + "0";
		
		for (int i = 1; i < count; i++) 
			result += ", max_" + prefix + "_" + type + i;
		
		result += ")\n";

		out.println(); 
		out.println(result); 
	}
	
	public void tableSummary(ExperimentalData2 columns, String type, String prefix)
	{
		tableMean(columns, type, prefix);
		tableStandardDeviation(columns, type, prefix);
		tableMin(columns, type, prefix);
		tableMax(columns, type, prefix);
	}

	public void sendCommand(String s)
	{
		out.println(s);
	}
	
	public void close()
	{
		out.close();
	}
	public class DataSet {
		private String alias;
		
		private String name;
		
		private String[] headers;

		public String[] getHeaders()
		{
			return headers;
		}

		public void setHeaders(String[] headers)
		{
			this.headers = headers;
		}

		public String getAlias()
		{
			return alias;
		}

		public void setAlias(String alias)
		{
			this.alias = alias;
		}

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}
	}
}
