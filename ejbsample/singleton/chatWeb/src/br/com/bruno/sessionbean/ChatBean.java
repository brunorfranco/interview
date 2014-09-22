package br.com.bruno.sessionbean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.interceptor.Interceptors;

import br.com.bruno.interceptor.LoggingInterceptor;

@Singleton
@Startup
@Interceptors({LoggingInterceptor.class}) //nao eh obrigatorio, pode ser configurado no ejb-jar.xml
public class ChatBean {

	private Set<String> salas = new HashSet<String>();

	public void criaSala(String sala) {
		this.salas.add(sala);
	}

	@Schedule(second ="*/5 ",minute ="*",hour ="*")
	public List<String> listaSalas() {
		System.out.println("Listando as salas");
		return new ArrayList<String>(this.salas);
	}

	@PostConstruct
	public void postConstruct() {
		System.out.println(" Criando o ChatBean ... ");
	}

	@PreDestroy
	public void preDestroy() {
		System.out.println(" Destruindo o ChatBean ... ");
	}

}