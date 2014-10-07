package br.bruno.security;

import java.io.IOException;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

public class SecureLoginModule implements LoginModule{
	
	public static final String USERNAME = "bruno";
	public static final String PASSWORD = "pass";
	private CallbackHandler callbackHandler = null;
	Boolean sucessAuth = false;

	@Override
	public boolean abort() throws LoginException {
		System.out.println("Called abort method");
		System.exit(-1);
		return false;
	}

	@Override
	public boolean commit() throws LoginException {
		System.out.println("Called commit method");
		return false;
	}

	@Override
	public void initialize(Subject arg0, CallbackHandler callBackHandler,
			Map<String, ?> arg2, Map<String, ?> arg3) {
		System.out.println("Called initialize method");
		callbackHandler = callBackHandler;
		
	}

	@Override
	public boolean login() throws LoginException {
		System.out.println("Called login method");
		Callback[] callBackArray = new Callback[2];
		callBackArray[0] = new NameCallback("User Name:");
		callBackArray[1] = new PasswordCallback("Password:", false);
		
		try {
			callbackHandler.handle(callBackArray);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedCallbackException e) {
			e.printStackTrace();
		}
		
		String name = ((NameCallback) callBackArray[0]).getName();
		String password = new String(((PasswordCallback) callBackArray[1]).getPassword());
		
		if(USERNAME.equals(name) && PASSWORD.equals(password)){
			System.out.println("Authentication sucess!");
			sucessAuth = true;
		} else {
			sucessAuth = false;
			throw new FailedLoginException("Authentication failed!");
		}
			
		return sucessAuth;
	}

	@Override
	public boolean logout() throws LoginException {
		System.out.println("Called logout method");
		return false;
	}

}
