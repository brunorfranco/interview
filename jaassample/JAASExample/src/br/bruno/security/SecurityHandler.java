package br.bruno.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class SecurityHandler implements CallbackHandler {

	@Override
	public void handle(Callback[] callbacks) throws IOException,
			UnsupportedCallbackException {
		NameCallback nameCallBack = null;
		PasswordCallback passwordCallBack = null;
		int counter = 0;
		while(counter < callbacks.length){
			if(callbacks[counter] instanceof NameCallback){
				nameCallBack = (NameCallback) callbacks[counter++];
				System.out.println(nameCallBack.getPrompt());
				nameCallBack.setName(new BufferedReader( new InputStreamReader(System.in)).readLine());
			} else if(callbacks[counter] instanceof PasswordCallback){
				passwordCallBack = (PasswordCallback) callbacks[counter++];
				System.out.println(passwordCallBack.getPrompt());
				passwordCallBack.setPassword(new BufferedReader( new InputStreamReader(System.in)).readLine().toCharArray());
			}
		}
	}
}
