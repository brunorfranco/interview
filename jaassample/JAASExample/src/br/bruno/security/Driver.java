package br.bruno.security;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

public class Driver {

	private static LoginContext loginContext;

	public static void main(String[] args) {
		System.setProperty("java.security.auth.login.config", "security.config");

		try {
			loginContext = new LoginContext("SecurityTest",
					new SecurityHandler());
		} catch (LoginException e) {
			e.printStackTrace();
			System.exit(0);
		}

		try {
			loginContext.login();
			System.exit(-1);
		} catch (LoginException e) {
			System.out.println("Exceção!!");
			e.printStackTrace();
		}
	}
}
