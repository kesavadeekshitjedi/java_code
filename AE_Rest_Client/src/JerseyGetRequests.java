import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;

import org.apache.commons.ssl.HostnameVerifier;
import org.codehaus.jettison.json.JSONException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;


public class JerseyGetRequests 
{
	public static List<String> getJobDetails(String server, String jobName) throws NoSuchAlgorithmException, KeyManagementException, JSONException
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
			
			return true;
		}

		@Override
		public void check(String arg0, SSLSocket arg1) throws IOException {
			
			
		}

		@Override
		public void check(String arg0, X509Certificate arg1) throws SSLException {
			
			
		}

		@Override
		public void check(String[] arg0, SSLSocket arg1) throws IOException {
			
			
		}

		@Override
		public void check(String[] arg0, X509Certificate arg1) throws SSLException {
			
			
		}

		@Override
		public void check(String arg0, String[] arg1, String[] arg2) throws SSLException {
			
			
		}

		@Override
		public void check(String[] arg0, String[] arg1, String[] arg2) throws SSLException {
			
			
		}
    };

    // Install the all-trusting host verifier
    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

    Client c = Client.create(new DefaultClientConfig());
    c.addFilter(new HTTPBasicAuthFilter("daddepalli", "Deek5581"));	
    WebResource res = c.resource("https://"+server+":9443/AEWS/job/"+jobName+"/");
    String response = res.accept(MediaType.APPLICATION_JSON).get(String.class);
    /*ClientResponse cResp = res.get(ClientResponse.class);
    EntityTag e = cResp.getEntityTag();
    String entity=cResp.getEntity(String.class);
    System.out.println("First One: "+entity);*/
    //System.out.println(response+"\n");
    AEJsonParser aejson = new AEJsonParser();
    List<String> aeJobInfo = aejson.parseAEJsonObject(aejson.returnJsonObject(response));
	return aeJobInfo;
    
    
    
    /*
     * the output from entity and the response objects are exactly similar.
     * Because it is doing the exact same thing!
     */
	}

	public static void getJobDependencies(String server, String jobName) throws NoSuchAlgorithmException, KeyManagementException
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
			
			return true;
		}

		@Override
		public void check(String arg0, SSLSocket arg1) throws IOException {
			
			
		}

		@Override
		public void check(String arg0, X509Certificate arg1) throws SSLException {
			
			
		}

		@Override
		public void check(String[] arg0, SSLSocket arg1) throws IOException {
			
			
		}

		@Override
		public void check(String[] arg0, X509Certificate arg1) throws SSLException {
			
			
		}

		@Override
		public void check(String arg0, String[] arg1, String[] arg2) throws SSLException {
			
			
		}

		@Override
		public void check(String[] arg0, String[] arg1, String[] arg2) throws SSLException {
			
			
		}
    };

    // Install the all-trusting host verifier
    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

    Client c = Client.create(new DefaultClientConfig());
    c.addFilter(new HTTPBasicAuthFilter("daddepalli", "Deek5581"));	
    WebResource res = c.resource("https://"+server+":9443/AEWS/job-dependencies/"+jobName+"/");
    String response = res.accept(MediaType.APPLICATION_XML_TYPE).get(String.class);
    ClientResponse cResp = res.get(ClientResponse.class);
    EntityTag e = cResp.getEntityTag();
    String entity=cResp.getEntity(String.class);
    System.out.println(entity+"\n");
	}
	
	public static void getAllJobs(String server) throws NoSuchAlgorithmException, KeyManagementException
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
			
			return true;
		}

		@Override
		public void check(String arg0, SSLSocket arg1) throws IOException {
			
			
		}

		@Override
		public void check(String arg0, X509Certificate arg1) throws SSLException {
			
			
		}

		@Override
		public void check(String[] arg0, SSLSocket arg1) throws IOException {
			
			
		}

		@Override
		public void check(String[] arg0, X509Certificate arg1) throws SSLException {
			
			
		}

		@Override
		public void check(String arg0, String[] arg1, String[] arg2) throws SSLException {
			
			
		}

		@Override
		public void check(String[] arg0, String[] arg1, String[] arg2) throws SSLException {
			
			
		}
    };

    // Install the all-trusting host verifier
    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

    Client c = Client.create(new DefaultClientConfig());
    c.addFilter(new HTTPBasicAuthFilter("daddepalli", "Deek5581"));	
    WebResource res = c.resource("https://"+server+":9443/AEWS/job/");
    String response = res.accept(MediaType.APPLICATION_XML_TYPE).get(String.class);
    ClientResponse cResp = res.get(ClientResponse.class);
    EntityTag e = cResp.getEntityTag();
    String entity=cResp.getEntity(String.class);
    System.out.println(entity+"\n");
	}
	
	public static void getJobRunInfo(String server, String jobName) throws NoSuchAlgorithmException, KeyManagementException
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
			
			return true;
		}

		@Override
		public void check(String arg0, SSLSocket arg1) throws IOException {
			
			
		}

		@Override
		public void check(String arg0, X509Certificate arg1) throws SSLException {
			
			
		}

		@Override
		public void check(String[] arg0, SSLSocket arg1) throws IOException {
			
			
		}

		@Override
		public void check(String[] arg0, X509Certificate arg1) throws SSLException {
			
			
		}

		@Override
		public void check(String arg0, String[] arg1, String[] arg2) throws SSLException {
			
			
		}

		@Override
		public void check(String[] arg0, String[] arg1, String[] arg2) throws SSLException {
			
			
		}
    };

    // Install the all-trusting host verifier
    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

    Client c = Client.create(new DefaultClientConfig());
    c.addFilter(new HTTPBasicAuthFilter("daddepalli", "Deek5581"));	
    WebResource res = c.resource("https://"+server+":9443/AEWS/job-run-info/"+jobName+"/");
    String response = res.accept(MediaType.APPLICATION_XML_TYPE).get(String.class);
    ClientResponse cResp = res.get(ClientResponse.class);
    EntityTag e = cResp.getEntityTag();
    String entity=cResp.getEntity(String.class);
    System.out.println(entity+"\n");
	}
	public static void getAllJobRunInfo(String server) throws NoSuchAlgorithmException, KeyManagementException
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
			
			return true;
		}

		@Override
		public void check(String arg0, SSLSocket arg1) throws IOException {
			
			
		}

		@Override
		public void check(String arg0, X509Certificate arg1) throws SSLException {
			
			
		}

		@Override
		public void check(String[] arg0, SSLSocket arg1) throws IOException {
			
			
		}

		@Override
		public void check(String[] arg0, X509Certificate arg1) throws SSLException {
			
			
		}

		@Override
		public void check(String arg0, String[] arg1, String[] arg2) throws SSLException {
			
			
		}

		@Override
		public void check(String[] arg0, String[] arg1, String[] arg2) throws SSLException {
			
			
		}
    };

    // Install the all-trusting host verifier
    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

    Client c = Client.create(new DefaultClientConfig());
    c.addFilter(new HTTPBasicAuthFilter("daddepalli", "Deek5581"));	
    WebResource res = c.resource("https://"+server+":9443/AEWS/job-run-info/");
    String response = res.accept(MediaType.APPLICATION_XML_TYPE).get(String.class);
    ClientResponse cResp = res.get(ClientResponse.class);
    EntityTag e = cResp.getEntityTag();
    String entity=cResp.getEntity(String.class);
    System.out.println(entity+"\n");
	}
}
