package br.uniriotec.vitor.padilha.dissertacao.utils;

import java.text.DecimalFormat;

public class FormatUtils {

	/**
	 * Método que formata o numero colocando zeros a esquerda.
	 * @param Numero a ser formatado
	 * @param numero de casas decimais
	 * @return String formatada
	 * @author Vitor Padilha
	 */
	public static String formatNumber(Number number, Integer length ){
		if (number==null){
			return "";
		}
		StringBuilder pattern = new StringBuilder();
		for(int i = 0; i<length; i++ ){
			pattern.append("0");
		}
		DecimalFormat formatter = (DecimalFormat) DecimalFormat.getIntegerInstance();
		formatter.applyPattern(pattern.toString());
		return formatter.format(number);
	}
	
	/**
	 * Método que formata o tamanho da string inserindo espaços a direita.
	 * @param String descricao
	 * @return String formatada
	 * @author Vitor Padilha
	 */
	public static String formatStringLength (String descricao, Integer length, String tipoMascara){
		if (descricao.length() >= length) {
			descricao = descricao.substring(0, length);
		}
		else {
			do {
				descricao = tipoMascara+descricao ;
			} while(descricao.length() < length);
		}
		return descricao;
	}
}
