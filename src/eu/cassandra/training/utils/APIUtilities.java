/*
Copyright 2011-2013 The Cassandra Consortium (cassandra-fp7.eu)


Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package eu.cassandra.training.utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
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

/**
 * This class contains static functions that are used for the communication of
 * the Training Module with the Cassandra Server and more specifically with the
 * User's Library in the main Cassandra Platform. Mostly, they have to do with
 * sending messages and models through the API of the platform.
 * 
 * @author Antonios Chrysopoulos
 * @version 0.9, Date: 29.07.2013
 */

public class APIUtilities
{

  static {
    javax.net.ssl.HttpsURLConnection
            .setDefaultHostnameVerifier(new javax.net.ssl.HostnameVerifier() {

              public boolean verify (String hostname,
                                     javax.net.ssl.SSLSession sslSession)
              {
                return true;
              }
            });
  }

  /**
   * This variable represents the user ID that is return after successfully
   * connecting to the Cassandra Server.
   */
  private static String userID = "";

  /**
   * This variable sets the server url that the Training Module is connecting.
   */
  private static String url;

  /**
   * This variable is the http client that is used for the exchange of messages
   * between the Cassandra Platform and the Training Module.
   */
  private static DefaultHttpClient httpclient = new DefaultHttpClient();

  /**
   * This variable is the ssl socket factory used for the connection and the
   * exchange of user credentials on a secure specific socket of the server.
   */
  private static SSLSocketFactory sf = null;

  /**
   * This variable is the ssl context used for the connection and the
   * exchange of user credentials on a secure specific socket of the server.
   */
  private static SSLContext sslContext = null;

  /**
   * This is the context variable context during each connection with the
   * Cassandra server.
   */
  private static BasicHttpContext localcontext = new BasicHttpContext();

  /**
   * This function is used to set up the server url as provided by the user.
   * 
   * @param URLString
   *          The string of the server url.
   * @throws MalformedURLException
   */
  public static void setUrl (String URLString) throws MalformedURLException
  {
    url = URLString;
  }

  /**
   * This function is used as a getter to the user's ID.
   * 
   * @return the user id as provided by the server after the successful
   *         connection.
   * @throws MalformedURLException
   */
  public static String getUserID ()
  {
    return userID;
  }

  /**
   * This function is used to send the entity models to the Cassandra Server,
   * specifically on the connected user's Library.
   * 
   * @param message
   *          The JSON schema of the entity.
   * @param suffix
   *          The library the model must be sent to.
   * @return the id of the entity model provided by the server.
   * @throws IOException
   * @throws AuthenticationException
   * @throws NoSuchAlgorithmException
   */
  public static String sendEntity (String message, String suffix)
    throws IOException, AuthenticationException, NoSuchAlgorithmException
  {

    System.out.println(message);
    HttpPost httppost = new HttpPost(url + suffix);

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

  /**
   * This function is used to send the entity models to the Cassandra Server,
   * specifically on the connected user's Library.
   * 
   * @param message
   *          The JSON schema of the entity.
   * @param suffix
   *          The library the model must be sent to.
   * @param id
   *          The id of the entity model in the Cassandra server.
   * @return a simple string of success or failure.
   * @throws IOException
   * @throws AuthenticationException
   * @throws NoSuchAlgorithmException
   */
  public static String updateEntity (String message, String suffix, String id)
    throws IOException, AuthenticationException, NoSuchAlgorithmException
  {

    System.out.println(message);
    HttpPut httpput = new HttpPut(url + suffix + "/" + id);

    StringEntity entity = new StringEntity(message, "UTF-8");
    entity.setContentType("application/json");
    httpput.setEntity(entity);
    System.out.println("executing request: " + httpput.getRequestLine());

    HttpResponse response = httpclient.execute(httpput, localcontext);
    HttpEntity responseEntity = response.getEntity();
    String responseString = EntityUtils.toString(responseEntity, "UTF-8");
    System.out.println(responseString);

    return "Done";

  }

  /**
   * This function is used to send the user's credentials to the Cassandra
   * Server.
   * 
   * @param username
   *          The username of the user in the server.
   * @param password
   *          The password of the user in the server.
   * @return true if connected, else false.
   * @throws Exception
   */
  public static boolean sendUserCredentials (String username, char[] password)
    throws Exception
  {

    try {
      sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, null, null);
      sf =
        new SSLSocketFactory(sslContext,
                             SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
    }
    catch (Exception e1) {
    }

    Scheme scheme = new Scheme("https", 8443, sf);
    httpclient.getConnectionManager().getSchemeRegistry().register(scheme);

    String pass = String.valueOf(password);

    try {
      UsernamePasswordCredentials usernamePasswordCredentials =
        new UsernamePasswordCredentials(username, pass);

      HttpGet httpget = new HttpGet(url + "/usr");
      httpget.addHeader(new BasicScheme()
              .authenticate(usernamePasswordCredentials, httpget, localcontext));

      System.out.println("executing request: " + httpget.getRequestLine());

      char SEP = File.separatorChar;
      File dir =
        new File(System.getProperty("java.home") + SEP + "lib" + SEP
                 + "security");
      File file = new File(dir, "jssecacerts");
      if (file.isFile() == false) {
        InstallCert.createCertificate("160.40.50.233", 8443);
        JFrame success = new JFrame();

        JOptionPane
                .showMessageDialog(success,
                                   "Certificate was created for user "
                                           + username
                                           + ". Now the connection will start",
                                   "Response Model Exported",
                                   JOptionPane.INFORMATION_MESSAGE);
      }

      HttpResponse response = httpclient.execute(httpget, localcontext);
      HttpEntity entity = response.getEntity();
      String responseString = EntityUtils.toString(entity, "UTF-8");
      System.out.println(responseString);

      DBObject dbo = (DBObject) JSON.parse(responseString);

      if (dbo.get("success").toString().equalsIgnoreCase("true")) {

        BasicDBList dataObj = (BasicDBList) dbo.get("data");

        DBObject dbo2 = (DBObject) dataObj.get(0);

        userID = dbo2.get("usr_id").toString();

        System.out.println("userId: " + userID);

        return true;
      }
      else {
        System.out.println(false);
        return false;
      }

    }
    finally {
    }

  }

}
