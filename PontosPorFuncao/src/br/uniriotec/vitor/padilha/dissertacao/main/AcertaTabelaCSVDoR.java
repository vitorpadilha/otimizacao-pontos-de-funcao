package br.uniriotec.vitor.padilha.dissertacao.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.uniriotec.vitor.padilha.dissertacao.utils.NumberUtils;

public class AcertaTabelaCSVDoR {
	private static String CAMINHO_ARQUIVOS = "C://Users//PADILHA//Desktop//resultadoAlgoritmo//BACKUP//Artigo";
	public static void main(String[] args) throws IOException {
		File arquivoSaidaPacotes = new File(CAMINHO_ARQUIVOS);
		File[] arquivos = arquivoSaidaPacotes.listFiles();
		for (File file : arquivos) {
			String nomeArquivo = file.getName();
			if(!file.isDirectory() && file.getName().substring(nomeArquivo.length()-3, nomeArquivo.length()).equals("csv")){
				BufferedReader br = new BufferedReader(new FileReader(file));  
		        String linha;
		        int i=0;
		        boolean arquivoValido = false;
		        Map<String,List<Integer>> valoresMap = new HashMap<String, List<Integer>>();
		        LinkedHashMap<Integer,LinkedHashMap<String,Double>> valoresFinal = new LinkedHashMap<Integer, LinkedHashMap<String,Double>>();
		        while( (linha = br.readLine()) != null ){
		           linha = linha.replace("\"", "");
		           String[] valores = linha.split(";");
		           if(i==0) {
		        	    int a = 1;
						for (String string : valores) {
							
							String[] stringCabecalhoPrincipal = string.split("\\.");
							String cabecalho = string;
							if(cabecalho.isEmpty())
								continue;
							if(string.toUpperCase().equals("MEDIAPP")) {
								arquivoValido=true;
							}
							if(stringCabecalhoPrincipal.length>1){
								cabecalho = stringCabecalhoPrincipal[0];
							}
							if(valoresMap.get(cabecalho)==null){
								valoresMap.put(cabecalho, new ArrayList<Integer>());
							}
							valoresMap.get(cabecalho).add(a);
							a++;
						}
		           }
		           else if (arquivoValido) {
		        	   for(String valor:valoresMap.keySet()){
		        		   List<Integer> inteiros = valoresMap.get(valor);
		        		   int versao = 1;
		        		   for (Integer integer : inteiros) {
		        			   if(valoresFinal.get(Integer.valueOf(versao))==null){
		        				   valoresFinal.put(Integer.valueOf(versao), new LinkedHashMap<String, Double>());
		        			   }
		        			   valoresFinal.get(Integer.valueOf(versao)).put(valor,Double.valueOf(valores[integer.intValue()].replace(",", ".")));
		        			   versao++;
		        		   }
		        	   }
		           }  
		           i++;
		        }		          
			    if(arquivoValido) {
			    	StringBuilder bufSaida = new StringBuilder();
			        for(Integer versao:valoresFinal.keySet()){
			        	String valoresPP = versao.toString().replace(".", ",")+";"+NumberUtils.formatNumber(valoresFinal.get(versao).get("mediaPP"),2).toString().replace(".",",")+" ± "+NumberUtils.formatNumber(valoresFinal.get(versao).get("desvioPP"),2).toString().replace(".",",")+";"+NumberUtils.formatNumber(valoresFinal.get(versao).get("minPP"),2).toString().replace(".",",")+";"+NumberUtils.formatNumber(valoresFinal.get(versao).get("maxPP"),2).toString().replace(".",",")+"\n";
			        	String valoresSatis = versao.toString()+";"+NumberUtils.formatNumber(valoresFinal.get(versao).get("mediaSatis"),2).toString().replace(".",",")+" ± "+NumberUtils.formatNumber(valoresFinal.get(versao).get("desvioSatis"),2).toString().replace(".",",")+";"+NumberUtils.formatNumber(valoresFinal.get(versao).get("minSatis"),2).toString().replace(".",",")+";"+NumberUtils.formatNumber(valoresFinal.get(versao).get("maxSatis"),2).toString().replace(".",",")+"\n";
			        	bufSaida.append(valoresPP+valoresSatis);
			        }
			        File arquivoSaida = new File(file.getAbsolutePath().replace(".csv", "-novo.csv"));
			        FileWriter fileWriter = new FileWriter(arquivoSaida);	
			        fileWriter.append(bufSaida);
			        fileWriter.close();
			        br.close();
			   }
			}
		}
	}
}
