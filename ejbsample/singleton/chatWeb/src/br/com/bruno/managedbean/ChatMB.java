package br.com.bruno.managedbean;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;

import br.com.bruno.sessionbean.ChatBean;

@ManagedBean
public class ChatMB {

	@EJB
	private ChatBean chatBean;

	private String sala;

	public void adicionaSala() {
		this.chatBean.criaSala(this.sala);
	}

	public List<String> getSalas() {
		return this.chatBean.listaSalas();
	}

	public void setSala(String sala) {
		this.sala = sala;
	}

	public String getSala() {
		return sala;
	}
}
