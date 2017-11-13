import java.io.IOException;
import java.security.cert.X509Certificate;

import org.apache.commons.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class JerseyPostRequest {

	public static void main(String[] args) throws Exception 
	{
		TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }	
        }
    };

    // Install the all-trusting trust manager
    SSLContext sc = SSLContext.getInstance("SSL");
    sc.init(null, trustAllCerts, new java.security.SecureRandom());
    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

    // Create all-trusting host name verifier
    HostnameVerifier allHostsValid = new HostnameVerifier() {
       

		@Override
		public boolean verify(String arg0, SSLSession arg1) {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public void check(String arg0, SSLSocket arg1) throws IOException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void check(String arg0, X509Certificate arg1) throws SSLException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void check(String[] arg0, SSLSocket arg1) throws IOException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void check(String[] arg0, X509Certificate arg1) throws SSLException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void check(String arg0, String[] arg1, String[] arg2) throws SSLException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void check(String[] arg0, String[] arg1, String[] arg2) throws SSLException {
			// TODO Auto-generated method stub
			
		}
    };

    // Install the all-trusting host verifier
    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		Client c = Client.create(new DefaultClientConfig());
	    c.addFilter(new HTTPBasicAuthFilter("daddepalli", "Deek5581"));
	    WebResource res = c.resource("https://lumos:9443/AEWS/event/force-start-job");
	    String jobString = "{\"jobName\":\"job1\",\"comment\":\"reason for sending event\"}";

	    ClientResponse response = res.type("application/json").post(ClientResponse.class,jobString);
	    if(response.getStatus()!=201)
	    {
	    	throw new Exception("Failed: HTTP error code"+response.getStatus());	
	    }
	    
	    /*System.out.println("Output ------");
	    String output = response.getEntity(String.class);
	    System.out.println(output);*/
	    
	}

}
