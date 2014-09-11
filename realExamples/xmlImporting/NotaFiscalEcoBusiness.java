package br.com.megasoft.arrecadanet.business;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import br.com.megasoft.arrecadanet.bean.NotaFiscalBlocoXmlHandler;
import br.com.megasoft.arrecadanet.dao.NotaFiscalEcoDao;
import br.com.megasoft.arrecadanet.entity.Contador;
import br.com.megasoft.arrecadanet.entity.Economico;
import br.com.megasoft.arrecadanet.entity.NfeMunicipalUsuario;
import br.com.megasoft.arrecadanet.entity.NotaFiscalEco;
import br.com.megasoft.arrecadanet.entity.enumerated.TipoEntradaEnum;
import br.com.megasoft.core.business.MegaBusinessException;
import br.com.megasoft.core.util.DateUtil;
import br.com.megasoft.core.util.StringUtil;

public class NotaFiscalEcoBusiness implements Serializable {

	private static final long serialVersionUID = 7024603754730398771L;

	private NotaFiscalEcoDao nfDao = new NotaFiscalEcoDao();
	
	private final String XSD = "/../xsd/notafiscalbloco_01.xsd"; 

	private List<String> listaErros = new ArrayList<String>();
	
	public List<String> getListaErros() {
		return listaErros;
	}

	/**
	 * Carrega Nota Fiscal pelo Id
	 * 
	 * @param id {@link Long}
	 * @return {@link NotaFiscalEco}
	 */
	public NotaFiscalEco carregar(Long id) {
		try {
			return nfDao.find(id);
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 * Salva nota fiscal
	 * 
	 * @param entity {@link NotaFiscalEco}
	 * @throws MegaBusinessException
	 */
	public void salvar(NotaFiscalEco entity) throws MegaBusinessException {

		// Atribui Economico a Nota Fiscal
		Economico economico = new EconomicoBussines().carregarInscMunicipal(entity.getEconomico().getInscMunicipal());

		if(entity.getEconomico() == null)
			throw new MegaBusinessException("Inscrição Municipal não Encontrada");
		
		if (new NotaFiscalEcoFechamentoBusiness().verificaFechamento(economico.getId(), DateUtil.month(entity.getDtEmissao()), DateUtil.year(entity.getDtEmissao())) == true) 
			throw new MegaBusinessException(String.format("Fechada a emissão de nota fiscal para o mês %02d/%d ", DateUtil.month(entity.getDtEmissao()), 
																											      DateUtil.year(entity.getDtEmissao())));

		if (this.nfDao.consultaNumeroEconomico(economico.getId(), entity.getNumero()) !=  null)
			throw new MegaBusinessException(String.format("Já existe nota fiscal com o número %d cadastrada", entity.getNumero()));

		entity.setEconomico(economico);
		entity.setMes(DateUtil.month(entity.getDtEmissao()));
		entity.setAno(DateUtil.year(entity.getDtEmissao()));
		this.nfDao.salvar(entity);
	}
	
	public List<NotaFiscalEco> pesquisarInscMunicipalMesAno(Long inscMunicipal, Integer mes, Integer ano) throws MegaBusinessException {

		if (inscMunicipal == null)
			throw new MegaBusinessException("Inscrição Municipal obrigatória");
		
		if ((mes == null) || (mes == 0))
			throw new MegaBusinessException("Mês obrigatório");

		if ((ano == null) || (ano == 0))
			throw new MegaBusinessException("Ano obrigatório");

		return this.nfDao.pesquisaInscMunicipalMesAno(inscMunicipal, mes, ano);
	}

	public Map<String, Object> consultaMinMaxQtdNota(Long economicoId, Integer mes,	Integer ano) throws MegaBusinessException {
		
		if (economicoId == null)
			throw new MegaBusinessException("Econômico obrigatório");
		
		if (mes == null)
			throw new MegaBusinessException("Mês obrigatório");
		
		if (ano == null)
			throw new MegaBusinessException("Ano obrigatório");

		return this.nfDao.consultaMinMaxQtdNota(economicoId, mes, ano);
	}
	
	public List<NotaFiscalEco> importarXml(byte[] xml, NfeMunicipalUsuario usuario, Economico economico) throws MegaBusinessException {

		this._validaXmlXsd(xml);
		return this._processarXml(xml, usuario, economico);
	}

	private void _validaXmlXsd(byte[] xml) throws MegaBusinessException {
		try {
	        File xsdFile = new File(this.getClass().getResource(XSD).getFile()); // read XSD file
	        Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(xsdFile);
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(new ByteArrayInputStream(xml)));
		} catch (SAXException | IOException e) {
			throw new MegaBusinessException(e);
		}
	}
	
	private List<NotaFiscalEco> _processarXml(byte[] xml, NfeMunicipalUsuario usuario, Economico economico) throws MegaBusinessException {
		listaErros.clear();
		NotaFiscalBlocoXmlHandler handler = new NotaFiscalBlocoXmlHandler();
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();
			parser.parse(new ByteArrayInputStream(xml), handler);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new MegaBusinessException(e);
		}

		List<NotaFiscalEco> notasValidas = validaNotas(handler.getListNotas(), economico);

		return notasValidas;
	}
	
	private List<NotaFiscalEco> validaNotas(List<NotaFiscalEco> listaCompletaNotas, Economico economico){
		List<NotaFiscalEco> listaNotas = new ArrayList<NotaFiscalEco>();
		
		for (NotaFiscalEco nota : listaCompletaNotas) {
			try{
				validaInscricaoMunicipal(nota, economico);
				validaCnpjEconomico(nota, economico);
				validaMesAnoReferenciaNota(nota);
				validaFechamentoDeMes(nota, economico);
				validaNumeroNota(nota, economico);
				validaDataCancelamentoNota(nota);
				validaValorCancelamentoNota(nota);
				validaValoresNota(nota);
				validaValorMaximoAliquota(nota);
				//não validar o valor do iss por enquanto
				//validaValorIss(nota);
				
				if(validaNumerosRepetidosDeNotas(listaNotas, nota)){
					nota.setPossuiErroImportacao(false);
					listaNotas.add(nota);
				} else {
					listaErros.add("Nota " +nota.getNumero()+ " está duplicada no XML.");
					nota.setPossuiErroImportacao(true);
					nota.setDescricaoErroImportacao("Nota " +nota.getNumero()+ " está duplicada no XML.");
					listaNotas.add(nota);
				}
				
			} catch(MegaBusinessException e){
				listaErros.add(e.getMessage());
				nota.setPossuiErroImportacao(true);
				nota.setDescricaoErroImportacao(e.getMessage());
				listaNotas.add(nota);
			}
		}
		
		return listaNotas;
	}
	
	private Boolean validaNumerosRepetidosDeNotas(List<NotaFiscalEco> listaNotasValidas, NotaFiscalEco nota){
		for (NotaFiscalEco notaFiscalEco : listaNotasValidas) {
			if(nota.getNumero().intValue() == notaFiscalEco.getNumero().intValue()){
				return false;
			}
		}
		return true;
	}
	
	private void validaMesAnoReferenciaNota(NotaFiscalEco notaEco) throws MegaBusinessException{
		Calendar cal = Calendar.getInstance();
		cal.setTime(notaEco.getDtEmissao());
		int mesEmissao = cal.get(Calendar.MONTH) + 1;
		int anoEmissao = cal.get(Calendar.YEAR);
		
		if(notaEco.getMes().intValue() != mesEmissao || notaEco.getAno() != anoEmissao){
			throw new MegaBusinessException("Nota "+ notaEco.getNumero()+ " inválida, mês ou ano de referencia não correspondem à data de emissão.");
		}
	}
	
	private void validaInscricaoMunicipal(NotaFiscalEco notaEco, Economico economico) throws MegaBusinessException{
		if(notaEco.getEconomico().getInscMunicipal().longValue() != economico.getInscMunicipal().longValue()){
			throw new MegaBusinessException("Número de inscrição municipal não coincide com o número do XML.");
		}
	}
	
	private void validaCnpjEconomico(NotaFiscalEco notaEco, Economico economico) throws MegaBusinessException{
		String cnpjXml = notaEco.getEconomico().getContribuinte().getCpfCnpj().trim().toString();
		String cnpjEconomicoLogado = StringUtil.clean(economico.getContribuinte().getCpfCnpj());
		if(!cnpjXml.equalsIgnoreCase(cnpjEconomicoLogado)){
			throw new MegaBusinessException("Número de CNPJ do econômico não coincide com o número do XML.");
		}
	}
	
	private void validaNumeroNota(NotaFiscalEco notaEco, Economico economico) throws MegaBusinessException{
		NotaFiscalEco nfe = nfDao.consultaNumeroEconomico(economico.getId(), notaEco.getNumero());
		if(nfe != null){
			throw new MegaBusinessException("Nota "+ notaEco.getNumero()+ " já lançada para esse econômico.");
		}
	}
	
	private void validaDataCancelamentoNota(NotaFiscalEco notaEco) throws MegaBusinessException{
		if(notaEco.getDtCancelado() != null && notaEco.getDtCancelado().before(notaEco.getDtEmissao())){
			throw new MegaBusinessException("Nota "+ notaEco.getNumero()+ " com data de cancelamento anterior a data de emissão.");
		}
	}
	
	private void validaValoresNota(NotaFiscalEco notaEco) throws MegaBusinessException{
		if(notaEco.getVlrCancelado() != null && (notaEco.getVlrNota().doubleValue() < 0 || notaEco.getVlrCancelado().doubleValue() < 0)){
			throw new MegaBusinessException("Nota "+ notaEco.getNumero()+ " com valores negativos.");
		}
	}
	
	private void validaValorCancelamentoNota(NotaFiscalEco notaEco) throws MegaBusinessException{
		if(notaEco.getVlrCancelado() != null && notaEco.getVlrNota().doubleValue() < notaEco.getVlrCancelado().doubleValue()){
			throw new MegaBusinessException("Nota "+ notaEco.getNumero()+ " com valor de cancelamento maior que o valor total da nota.");
		}
	}
	
	private void validaFechamentoDeMes(NotaFiscalEco entity, Economico economico) throws MegaBusinessException{
		if (new NotaFiscalEcoFechamentoBusiness().verificaFechamento(economico.getId(), DateUtil.month(entity.getDtEmissao()), DateUtil.year(entity.getDtEmissao())) == true) {
			throw new MegaBusinessException(String.format("Fechada a emissão de nota fiscal para o mês %02d/%d ", DateUtil.month(entity.getDtEmissao()), 
																											      DateUtil.year(entity.getDtEmissao())));
		}
	}
	
	private void validaValorMaximoAliquota(NotaFiscalEco notaEco) throws MegaBusinessException{
		if(notaEco.getVlrAliquota() != null && notaEco.getVlrAliquota().doubleValue() > 5.00){
			throw new MegaBusinessException("Nota "+ notaEco.getNumero()+ " com valor de alíquota maior que 5%.");
		}
	}
	
	public void validaValorIss(NotaFiscalEco notaEco) throws MegaBusinessException{
		Double valorNota = notaEco.getVlrNota().doubleValue();
		Double aliquotaPorcentagem = notaEco.getVlrAliquota().doubleValue();
		Double valorIssRetido = notaEco.getIssqnRetido().doubleValue();
		Double valorCorretoIss = valorNota * (aliquotaPorcentagem/100);
		if(valorCorretoIss.doubleValue() != valorIssRetido){
			throw new MegaBusinessException("Nota "+ notaEco.getNumero()+ " com valor de ISS diferente da alíquota sobre o valor da nota.");
		}
	}
	
	public void salvaListaNotas(List<NotaFiscalEco> list, Contador contador, NotaFiscalEco notaFiscalEco){
		NotaFiscalEcoDao dao = new NotaFiscalEcoDao();
		for (NotaFiscalEco notaEco : list) {
			notaEco.setContador(contador);
			notaEco.setEconomico(notaFiscalEco.getEconomico());
			notaEco.setTipoEntrada(TipoEntradaEnum.ARQUIVO.getNome());
			dao.salvar(notaEco);
		}
	}
	
}