package br.com.bruno.managedbeans;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;

import br.com.bruno.sessionbeans.Calculator;

@ManagedBean
public class CalculatorMB {

	@EJB
	private Calculator calculadora;

	private double a;

	private double b;

	private double resultado;

	public void soma() {
		this.resultado = this.calculadora.soma(a, b);
	}

	public double getA() {
		return a;
	}

	public void setA(double a) {
		this.a = a;
	}

	public double getB() {
		return b;
	}

	public void setB(double b) {
		this.b = b;
	}

	public double getResultado() {
		return resultado;
	}

	public void setResultado(double resultado) {
		this.resultado = resultado;
	}

}
