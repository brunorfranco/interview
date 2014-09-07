package br.com.bruno.sessionbeans;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.ejb.Remote;
import javax.ejb.Remove;
import javax.ejb.Stateful;

@Stateful
@Remote(Carrinho.class)
public class CarrinhoBean implements Carrinho {

	private Set<String> produtos = new HashSet<String>();
	private static int contadorTotal;
	private static int contadorAtivos;
	private int id;

	public void adiciona(String produto) {
		this.produtos.add(produto);
	}

	public void remove(String produto) {
		this.produtos.remove(produto);
	}

	public Set<String> getProdutos() {
		return produtos;
	}

	@Remove
	public void finalizaCompra() {
		// lógica para finalizar compra
	}

	@PostConstruct
	public void inicializando() {
		synchronized (CarrinhoBean.class) {
			CarrinhoBean.contadorTotal++;
			CarrinhoBean.contadorAtivos++;
			this.id = CarrinhoBean.contadorTotal;

			System.out.println(" PostConstruct ");
			System.out.println("ID: " + this.id);
			System.out
					.println(" ContatorTotal : " + CarrinhoBean.contadorTotal);
			System.out.println(" ContatorAtivos : "
					+ CarrinhoBean.contadorAtivos);
		}
	}

	@PreDestroy
	public void destruindo() {
		synchronized (CarrinhoBean.class) {
			CarrinhoBean.contadorAtivos--;

			System.out.println(" PreDestroy ");
			System.out.println("ID: " + this.id);
			System.out
					.println(" ContatorTotal : " + CarrinhoBean.contadorTotal);
			System.out.println(" ContatorAtivos : "
					+ CarrinhoBean.contadorAtivos);
		}
	}

	@PrePassivate
	public void passivando() {
		synchronized (CarrinhoBean.class) {
			CarrinhoBean.contadorAtivos--;

			System.out.println(" PrePassivate ");
			System.out.println("ID: " + this.id);
			System.out
					.println(" ContatorTotal : " + CarrinhoBean.contadorTotal);
			System.out.println(" ContatorAtivos : "
					+ CarrinhoBean.contadorAtivos);
		}
	}

	@PostActivate
	public void ativando() {
		synchronized (CarrinhoBean.class) {
			CarrinhoBean.contadorAtivos++;

			System.out.println(" PostActivate ");
			System.out.println("ID: " + this.id);
			System.out
					.println(" ContatorTotal : " + CarrinhoBean.contadorTotal);
			System.out.println(" ContatorAtivos : "
					+ CarrinhoBean.contadorAtivos);
		}
	}
}
