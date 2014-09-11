package br.com.megasoft.arrecadanet.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;

import br.com.megasoft.arrecadanet.ArrecadaNetKey;
import br.com.megasoft.arrecadanet.SessionApp;
import br.com.megasoft.arrecadanet.business.EconomicoBussines;
import br.com.megasoft.arrecadanet.business.NotaFiscalEcoBusiness;
import br.com.megasoft.arrecadanet.dao.NotaFiscalEcoDao;
import br.com.megasoft.arrecadanet.entity.Contador;
import br.com.megasoft.arrecadanet.entity.Economico;
import br.com.megasoft.arrecadanet.entity.NotaFiscalEco;
import br.com.megasoft.arrecadanet.entity.Prefeitura;
import br.com.megasoft.arrecadanet.report.NotaFiscalEcoReport;
import br.com.megasoft.core.business.MegaBusinessException;
import br.com.megasoft.core.faces.MegaFaces;
import br.com.megasoft.core.report.MegaJasperFaces;
import br.com.megasoft.core.util.DateUtil;

@ViewScoped
@ManagedBean(name = "notaFiscalEco")
public class NotaFiscalEcoController implements Serializable {

	private static final long serialVersionUID = 3886595218743315754L;

	private NotaFiscalEcoReport 	report = new NotaFiscalEcoReport();
	private Contador				contador = 	(Contador) MegaFaces.getSession(ArrecadaNetKey.SESSION_CONTADOR);
	private NotaFiscalEcoBusiness	business = 	new NotaFiscalEcoBusiness();
	private NotaFiscalEco			entity = 	new NotaFiscalEco();
	public List<NotaFiscalEco> 		lista;
	public List<Economico> 			listEconomico;
	public List<String> 			listaErros = new ArrayList<String>();

	public Economico 				economico;

	public Date					relInicio,	relFim;
	private int 				idEconomico;
	
	private List<NotaFiscalEco> list;

	public List<NotaFiscalEco> getList() {
		return list;
	}
	
	public List<String> getListaErros() {
		return listaErros;
	}

	public int getIdEconomico() {
		return idEconomico;
	}
	
	public void setIdEconomico(int idEconomico) {
		this.idEconomico = idEconomico;
	}
	
	public Date getRelInicio() {
		return relInicio;
	}

	public void setRelInicio(Date relInicio) {
		this.relInicio = relInicio;
	}

	public Date getRelFim() {
		return relFim;
	}

	public void setRelFim(Date relFim) {
		this.relFim = relFim;
	}
	
	
	public NotaFiscalEcoReport getReport() {
		return report;
	}

	public NotaFiscalEco getEntity() {
		return entity;
	}

	public void setEntity(NotaFiscalEco notaFiscalEco) {
		this.entity = notaFiscalEco;
	}

	public List<NotaFiscalEco> getLista() {
		return lista;
	}

	public void setLista(List<NotaFiscalEco> lista) {
		this.lista = lista;
	}

	public Economico getEconomico() {
		return economico;
	}

	public void setEconomico(Economico economico) {
		this.economico = economico;
	}

	public List<Economico> getListEconomico() {
		return listEconomico;
	}

	public void setListEconomico(List<Economico> listEconomico) {
		this.listEconomico = listEconomico;
	}

	

	@PostConstruct
	public void init() {
		Economico eco =  (Economico) MegaFaces.getSession(ArrecadaNetKey.SESSION_NOTA_FISCAL_ECONOMICO);
		if (eco != null) {
			this.entity.setEconomico(eco);
		}
		Date date = new Date();
		this.entity.setMes(DateUtil.month(date));
		this.entity.setAno(DateUtil.year(date));
		this.entity.setContador(this.contador);
	}

	public void loadForm() {
		if (MegaFaces.getParam().get("id") != null) {
			NotaFiscalEcoDao nfDao = new NotaFiscalEcoDao();
			this.entity = nfDao.find(MegaFaces.getParam().getLong("id"));
		}
	}

	/**
	 * Carrega Economicos
	 */
	public void loadEconomico() {
		try {
			if (this.listEconomico == null)
				this.listEconomico = new EconomicoBussines().pesquisaContador(contador.getId());
		} catch (MegaBusinessException e) {
			MegaFaces.getMessage().error(e.getMessage());
		}
	}

	public List<SelectItem> getComboBox() {
		this.listEconomico = new EconomicoBussines().listAllEconomico();
		
		  List<SelectItem> itens = new ArrayList<SelectItem>(
		    this.listEconomico.size());

		  for (Economico p : this.listEconomico) {
		   itens.add(new SelectItem(p.getId(), p.getContribuinte().getNome()));
		  }
		  return itens;
	}

	/**
	 * Verifica se o economico esta selecionado
	 *
	 */
	public void loadVerificaEconomicoSelecionado() {
		if (MegaFaces.getSession().getAttribute(ArrecadaNetKey.SESSION_NOTA_FISCAL_ECONOMICO) == null) {
			FacesContext cx = MegaFaces.getContext();

			HttpServletRequest request	= (HttpServletRequest) cx.getExternalContext().getRequest();
			cx.getApplication().getNavigationHandler().handleNavigation(cx, null, String.format("/nota-fiscal/economico?faces-redirect=true&url=%s&%s",
								request.getRequestURI().replace(request.getContextPath(), ""), request.getQueryString()));
		}
	}

	public String pesquisar() {
		return "pesquisa";
	}

	public String novo() {
		this.entity = new NotaFiscalEco();
		return "form";
	}

	public String alterar() {
		return "form";
	}

	public String doCadastrar() {
		try {
			if(entity.getVlrAliquota().doubleValue() > 5.00){
				MegaFaces.getMessage().error("Valor da alíquota não pode ultrapassar 5%");
				return "";
			}
			business.validaValorIss(entity);
			business.salvar(this.entity);
			MegaFaces.getMessage().info(String.format("Nota Fiscal '%d' adicionada com sucesso", entity.getNumero()));
			this.entity = new NotaFiscalEco();
			this.init();
		} catch (MegaBusinessException e) {
			MegaFaces.getMessage().error(e.getMessage());
		}
		return null;
	}

	public void doPesquisar() {
		try {
			this.lista = this.business.pesquisarInscMunicipalMesAno(this.entity.getEconomico().getInscMunicipal(), this.entity.getMes(), this.entity.getAno());
		} catch (MegaBusinessException e) {
			MegaFaces.getMessage().error(e.getMessage());
		}
	}

	/**
	 * Seleciona Economico
	 * 
	 * @return {@link String}
	 */
	public String doSelecionarEconomico() {
		MegaFaces.getSession().setAttribute(ArrecadaNetKey.SESSION_NOTA_FISCAL_ECONOMICO, this.economico);
		MegaFaces.getMessage().info("Empresa selecionada com sucesso.");

		String url = MegaFaces.getParam().get("url");
		if ((url == null) || ("".equals(url)))
			url = "/nota-fiscal/index";

		FacesContext cx = MegaFaces.getContext();
		cx.getApplication().getNavigationHandler().handleNavigation(cx, null, String.format("%s%sfaces-redirect=true", url, (url.contains("?"))? "&" : "?"));

		return null;
	}

	public void relNotaFiscalSintetico() {
		Long economicoId = ((Economico) MegaFaces.getSession().getAttribute(ArrecadaNetKey.SESSION_NOTA_FISCAL_ECONOMICO)).getId();
		Prefeitura prefeitura = ArrecadaNetController.getInstance().getPrefeitura();

		MegaJasperFaces.pdf(report.notaFiscalSintetico(economicoId, MegaFaces.getPath("/upload/cliente/logo.jpg"),
													   prefeitura.getNome(), prefeitura.getSecretaria()));
	}

	public void relNotaFiscalPorPeriodo() {
		Prefeitura prefeitura = ArrecadaNetController.getInstance().getPrefeitura();

		MegaJasperFaces.pdf(report.notasPorPeriodo(prefeitura.getNome(), prefeitura.getSecretaria(),MegaFaces.getPath("/upload/cliente/logo.jpg"), relInicio, relFim));
	}
	
	public void relNotaFiscalPorEmpresa() {
		Prefeitura prefeitura = ArrecadaNetController.getInstance().getPrefeitura();
		
		MegaJasperFaces.pdf(report.notasPorEmpresa(prefeitura.getNome(), prefeitura.getSecretaria(), this.idEconomico, relInicio, relFim));
	}
	
	public void uploadXml(FileUploadEvent event) {
		try {
			this.list = null;
			byte[] arquivo = IOUtils.toByteArray(event.getFile().getInputstream());
			this.list = business.importarXml(arquivo, SessionApp.getNfeUsuario(), entity.getEconomico());
			listaErros = business.getListaErros();
			if(listaErros != null && !listaErros.isEmpty()){
				MegaFaces.getMessage().error("Existem erros no XML. Não é possivel importar o XML");
			}
		} catch (IOException e) {
			MegaFaces.getMessage().warn("Falha ao validar o arquivo.");
		} catch (MegaBusinessException e) {
			MegaFaces.getMessage().warn("Falha ao validar o arquivo.");
		}
	}
	
	public void salvarNotaFiscalBlocoXml() {
		if(listaErros != null && !listaErros.isEmpty()){
			MegaFaces.getMessage().error("Existem erros impeditivos no XML.");
			return;
		} else {
			salvaNotas();
		}
	}
	
	private void salvaNotas(){
		business.salvaListaNotas(list, contador, entity);
		MegaFaces.getMessage().info("Salvo com sucesso.");
		list.clear();
		listaErros.clear();
	}
}