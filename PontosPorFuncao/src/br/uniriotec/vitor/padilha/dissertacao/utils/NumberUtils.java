package br.uniriotec.vitor.padilha.dissertacao.utils;

import java.math.BigDecimal;

public class NumberUtils {

	public static Double formatNumber(Number number, int decimals){
		return new BigDecimal(number.toString()).setScale(decimals,BigDecimal.ROUND_HALF_DOWN).doubleValue();
	}
	
	public static String formatNumberToString(Number number, int decimals){
		return new BigDecimal(number.toString()).setScale(decimals,BigDecimal.ROUND_HALF_DOWN).toString().replace(".", ",");
	}
}
