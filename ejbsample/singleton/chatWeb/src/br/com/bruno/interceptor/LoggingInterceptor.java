package br.com.bruno.interceptor;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

public class LoggingInterceptor {

	@AroundInvoke
	public Object interceptador(InvocationContext ic) throws Exception {
		System.out.println(" CHAMANDO O MÉTODO : " + ic.getMethod());
		
		Object retornoDoMetodoDeNegocio = ic. proceed ();

		System.out.println(" MÉTODO " + ic.getMethod() + " FINALIZADO ");
		return retornoDoMetodoDeNegocio;
	}
}
