package br.com.bruno.sessionbean;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Remote;
import javax.ejb.Singleton;

@Singleton
@Lock(LockType.READ)
@Remote(Contador.class)
public class ContadorBean implements Contador {

	private int valor;

	public void incrementa() {
		this.valor++;
	}

	public int getValor() {
		return this.valor;
	}
}