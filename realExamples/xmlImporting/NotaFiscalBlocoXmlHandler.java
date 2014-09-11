package br.com.megasoft.arrecadanet.bean;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import br.com.megasoft.arrecadanet.entity.Contribuinte;
import br.com.megasoft.arrecadanet.entity.Economico;
import br.com.megasoft.arrecadanet.entity.NotaFiscalEco;
import br.com.megasoft.arrecadanet.entity.enumerated.NotaFiscalBlocoXmlEnum;
import br.com.megasoft.core.util.DateUtil;

public class NotaFiscalBlocoXmlHandler extends DefaultHandler {

	private List<NotaFiscalEco> listNotasEco =  new ArrayList<>();
	private StringBuffer value = new StringBuffer();
	private NotaFiscalEco notaFiscalEco;
	private Economico economicoXml = new Economico(new Contribuinte());
	
	public List<NotaFiscalEco> getListNotas(){
		return listNotasEco;
	}
	
	public void parse(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser parser = spf.newSAXParser();
		parser.parse(inputStream, this);
	}
	
	@Override
	public void startElement(String uri, String localName, String tag, Attributes attributes) throws SAXException {
		if(tag.equalsIgnoreCase(NotaFiscalBlocoXmlEnum.NOTA.name())){
			this.notaFiscalEco = new NotaFiscalEco();
			notaFiscalEco.setEconomico(economicoXml);
		}
		
	}
	
	@Override
	public void endElement(String uri, String localName, String tag) throws SAXException {
		
		if(tag.equalsIgnoreCase(NotaFiscalBlocoXmlEnum.NOTA.getNome())){
			this.listNotasEco.add(notaFiscalEco);
		}
		
		if(tag.equalsIgnoreCase(NotaFiscalBlocoXmlEnum.CNPJ.getNome())){
			economicoXml.getContribuinte().setCpfCnpj(this.value.toString().trim());
		}
		
		if(tag.equalsIgnoreCase(NotaFiscalBlocoXmlEnum.INSCRICAOMUNICIPAL.getNome())){
			economicoXml.setInscMunicipal(Long.parseLong(this.value.toString().trim()));
		}
		
		if(tag.equalsIgnoreCase(NotaFiscalBlocoXmlEnum.NUMERO.getNome())){
			this.notaFiscalEco.setNumero(Long.parseLong(this.value.toString().trim()));
		}
		
		if(tag.equalsIgnoreCase(NotaFiscalBlocoXmlEnum.DATAEMISSAO.getNome())){
			this.notaFiscalEco.setDtEmissao(DateUtil.dtTimeXml(this.value.toString().trim()));
		}
		
		if(tag.equalsIgnoreCase(NotaFiscalBlocoXmlEnum.ANO.getNome())){
			this.notaFiscalEco.setAno(Integer.parseInt(this.value.toString().trim()));
		}
		
		if(tag.equalsIgnoreCase(NotaFiscalBlocoXmlEnum.MES.getNome())){
			this.notaFiscalEco.setMes(Integer.parseInt(this.value.toString().trim()));
		}
		
		if(tag.equalsIgnoreCase(NotaFiscalBlocoXmlEnum.VALORNOTA.getNome())){
			this.notaFiscalEco.setVlrNota(new BigDecimal(this.value.toString().trim()));
		}
		
		if(tag.equalsIgnoreCase(NotaFiscalBlocoXmlEnum.ISSQN.getNome())){
			this.notaFiscalEco.setIssqnRetido(new BigDecimal(this.value.toString().trim()));
		}
		
		if(tag.equalsIgnoreCase(NotaFiscalBlocoXmlEnum.DATACANCELAMENTO.getNome())){
			this.notaFiscalEco.setDtCancelado(DateUtil.dtTimeXml(this.value.toString().trim()));
		}

		if(tag.equalsIgnoreCase(NotaFiscalBlocoXmlEnum.VALORCANCELAMENTO.getNome())){
			this.notaFiscalEco.setVlrCancelado(new BigDecimal(this.value.toString().trim()));
		}
		
		if(tag.equalsIgnoreCase(NotaFiscalBlocoXmlEnum.FLAGRISSRETIDO.getNome())){
			BigDecimal flag = new BigDecimal(this.value.toString().trim());
			if(flag != null && flag.intValue() == 0) {
				this.notaFiscalEco.setIsISSRetido(false);
			} else{
				this.notaFiscalEco.setIsISSRetido(true);
			}
		}
		
		if(tag.equalsIgnoreCase(NotaFiscalBlocoXmlEnum.VALORALIQUOTA.getNome())){
			this.notaFiscalEco.setVlrAliquota(new BigDecimal(this.value.toString().trim()));
		}
		// Limpa o valor atual
		this.value.delete(0, this.value.length());
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		this.value.append(ch, start, length);
	}
	
}
