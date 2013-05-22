package eu.cassandra.training.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class APIUtilities
{

  static {
    // for localhost testing only
    javax.net.ssl.HttpsURLConnection
            .setDefaultHostnameVerifier(new javax.net.ssl.HostnameVerifier() {

              public boolean verify (String hostname,
                                     javax.net.ssl.SSLSession sslSession)
              {
                return true;
              }
            });
  }

  private static String userID = "";
  private static String url;

  private static DefaultHttpClient httpclient = new DefaultHttpClient();

  private static SSLSocketFactory sf = null;
  private static SSLContext sslContext = null;
  private static StringWriter writer;
  // Add AuthCache to the execution context
  private static BasicHttpContext localcontext = new BasicHttpContext();

  public static void setUrl (String URLString) throws MalformedURLException
  {
    url = URLString;
  }

  public static String getUserID ()
  {

    return userID;

  }

  public static String sendEntity (String message, String suffix)
    throws IOException, AuthenticationException, NoSuchAlgorithmException
  {

    System.out.println(message);
    HttpPost httppost = new HttpPost(url + suffix);
    // httpget.addHeader(new BasicScheme()
    // .authenticate(usernamePasswordCredentials, httpget, localcontext));
    StringEntity entity = new StringEntity(message, "UTF-8");
    entity.setContentType("application/json");
    httppost.setEntity(entity);
    System.out.println("executing request: " + httppost.getRequestLine());

    HttpResponse response = httpclient.execute(httppost, localcontext);
    HttpEntity responseEntity = response.getEntity();
    String responseString = EntityUtils.toString(responseEntity, "UTF-8");
    System.out.println(responseString);

    DBObject dbo = (DBObject) JSON.parse(responseString);

    DBObject dataObj = (DBObject) dbo.get("data");

    return dataObj.get("_id").toString();

  }

  public static void sendUserCredentials (String username, char[] password)
    throws IOException, NoSuchAlgorithmException, AuthenticationException
  {

    try {
      sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, null, null);
    }
    catch (NoSuchAlgorithmException e) {
      // <YourErrorHandling>
    }
    catch (KeyManagementException e) {
      // <YourErrorHandling>
    }

    try {
      sf =
        new SSLSocketFactory(sslContext,
                             SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

    }
    catch (Exception e) {
      // <YourErrorHandling>

    }

    Scheme scheme = new Scheme("https", 8443, sf);
    httpclient.getConnectionManager().getSchemeRegistry().register(scheme);

    try {

      UsernamePasswordCredentials usernamePasswordCredentials =
        new UsernamePasswordCredentials("antonis", "lala123");

      HttpGet httpget = new HttpGet(url + "/usr");
      httpget.addHeader(new BasicScheme()
              .authenticate(usernamePasswordCredentials, httpget, localcontext));

      System.out.println("executing request: " + httpget.getRequestLine());

      HttpResponse response = httpclient.execute(httpget, localcontext);
      HttpEntity entity = response.getEntity();
      String responseString = EntityUtils.toString(entity, "UTF-8");
      System.out.println(responseString);

      DBObject dbo = (DBObject) JSON.parse(responseString);

      BasicDBList dataObj = (BasicDBList) dbo.get("data");

      DBObject dbo2 = (DBObject) dataObj.get(0);

      userID = dbo2.get("usr_id").toString();

      System.out.println("userId: " + userID);

    }
    finally {
      // When HttpClient instance is no longer needed,
      // shut down the connection manager to ensure
      // immediate deallocation of all system resources
      // httpclient.getConnectionManager().shutdown();
    }

    // String message = username + ":" + password;
    // // Set up the connection
    // connection = (HttpURLConnection) url.openConnection();
    // connection.setDoOutput(true);
    // connection.setRequestMethod("GET");
    // connection.setReadTimeout(10000);
    // connection.setRequestProperty("charset", "utf-8");
    // connection.setRequestProperty("Content-Type",
    // "application/x-www-form-urlencoded");
    // connection.setRequestProperty("user", message);
    //
    // connection.connect();
    // InputStream in = connection.getInputStream();
    // BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    // String text = reader.readLine();
    // System.out.println(text);
    //
    // connection.disconnect();
  }

  public static void getUserID (String username, char[] password)
    throws Exception
  {

    sendUserCredentials(username, password);

    // // Parse salt from answer
    // //JSONObject json = new JSONObject(answer);
    // String salt = json.get("salt").toString();
    // // Add salt to password and get MD5 hash
    // String prehash = password + salt;
    // String hash = Utilities.md5hash(prehash);
    // // Same thing for hash and unsername
    // hash = username + hash;
    // String auth_token = Utilities.md5hash(hash);
    // String tmp =
    // "format=RFC4627&method=auth.getSessionKey&username=" + username
    // + "&auth_token=" + auth_token;
    // send(tmp);
    // answer = read();
    // json = new JSONObject(answer);

    // sessionId = json.get("session_key").toString();

    // System.out.println(sessionId);
  }

}
