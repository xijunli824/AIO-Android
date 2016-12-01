package org.cmaaio.ssl;

import java.security.KeyStore;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * 
 * @author zl
 *
 */
public class MyX509TrustManager implements X509TrustManager {
	X509TrustManager myJSSEX509TrustManager;

	public MyX509TrustManager() throws Exception {
		KeyStore ks = KeyStore.getInstance("BKS");
		TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
		tmf.init(ks);
		TrustManager tms[] = tmf.getTrustManagers();
		for (int i = 0; i < tms.length; i++) {
			if (tms[i] instanceof X509TrustManager) {
				myJSSEX509TrustManager = (X509TrustManager) tms[i];
				return;
			}
		}
	}

	@Override
	public void checkClientTrusted(java.security.cert.X509Certificate[] arg0,
			String arg1) throws java.security.cert.CertificateException {
		
	}

	@Override
	public void checkServerTrusted(java.security.cert.X509Certificate[] arg0,
			String arg1) throws java.security.cert.CertificateException {
		
	}

	@Override
	public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		return null;
	}

	

}