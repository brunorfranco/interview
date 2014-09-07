package br.com.bruno.sessionbeans;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Local;
import javax.ejb.Stateless;

@Stateless
@Local(Calculator.class)
public class CalculatorBean implements Calculator {
	public double soma(double a, double b) {
		return a + b;
	}
	
	@PostConstruct
	public void iniating(){
		System.out.println("Iniating a calculator object");
	}
	
	@PreDestroy
	public void destroying(){
		System.out.println("Destroying a calculator object");
	}
}
