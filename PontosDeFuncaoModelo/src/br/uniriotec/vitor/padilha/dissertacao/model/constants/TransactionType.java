package br.uniriotec.vitor.padilha.dissertacao.model.constants;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "transactionType")
@XmlEnum
public enum TransactionType {
	@XmlEnumValue(value="EO")
	EO,
	@XmlEnumValue(value="EI")
	EI,
	@XmlEnumValue(value="EQ")
	EQ
}
