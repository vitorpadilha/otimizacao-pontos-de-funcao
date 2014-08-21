package br.uniriotec.vitor.padilha.dissertacao.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {
	private static final String VALIDACAO_NUMERO = "[^0-9]";
	
	/**
	 * M�todo gen�rico para validar se um certo campo possui somente caracteres num�ricos.
	 * � n�o � nula ou vazia.
	 * @param campo String a ser validada
	 * @return true/false
	 * @author Vitor Padilha Gon�alves
	 */
	public static boolean validaNumero(final String campo) {
		if (campo == null || campo.trim().length() == 0){
			return false;
		}
		Pattern p = Pattern.compile(VALIDACAO_NUMERO);
		Matcher m = p.matcher(campo);
		if (m.find()) {
			return false;
		}
		return true;
	}
}
