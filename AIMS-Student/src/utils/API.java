package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

import entity.payment.CreditCard;
import entity.payment.PaymentTransaction;

/**
 * @author ThangTV
 *
 */
public class API {
	
	/**
	 * Thuoc tinh nay giup format ngay thang theo dinh dang
	 */
	public static DateFormat DATE_FORMATER = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	/**
	 * Thuoc tinh nay giup log thong tin ra console
	 */
	private static Logger LOGGER = Utils.getLogger(Utils.class.getName());

	/**
	 * Thiet lap ket noi den server
	 * @param url string url den  server
	 * @param method  string giao thuc api
	 * @param token string dung de xac thuc
	 * @return respone HttpURLConnection ket noi toi server
	 * @throws Exception
	 */
	private static HttpURLConnection setupConnection(String url, String method, String token) throws IOException{
		LOGGER.info("Request URL: " + url + "\n");
		URL line_api_url = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) line_api_url.openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Authorization", "Bearer " + token);
		return conn;
	}
	
	
	/**
	 * Doc ket qua tra ve tu server
	 * @param conn HttpURLConnection ket noi den server
	 * @return respone string ket qua tu server
	 * @throws Exception
	 */
	private static String readResponse(HttpURLConnection conn) throws IOException {
		BufferedReader in;
		String inputLine;
		if (conn.getResponseCode() / 100 == 2) {
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} else {
			in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		}
		StringBuilder response = new StringBuilder();
		while ((inputLine = in.readLine()) != null)
			response.append(inputLine);
		in.close();
		LOGGER.info("Respone Info: " + response.toString());
		return response.toString();
	}
	
	
	/**
	 * Phuong thuc nay giup goi cac api dang get
	 * @param url string url den  server
	 * @param token string dung de xac thuc
	 * @return respone string tu server
	 * @throws Exception
	 */
	public static String get(String url, String token) throws Exception {
		HttpURLConnection conn = setupConnection(url, "GET", token);
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuilder respone = new StringBuilder(); // using StringBuilder for the sake of memory and performance
		while ((inputLine = in.readLine()) != null)
			System.out.println(inputLine);
		respone.append(inputLine + "\n");
		in.close();
		LOGGER.info("Respone Info: " + respone.substring(0, respone.length() - 1).toString());
		return respone.substring(0, respone.length() - 1).toString();
	}


	/**
	 * Phuong thuc nay giup goi cac api dang post
	 * @param url string url den  server
	 * @param data string du lieu gui den server
	 * @param token string dung de xac thuc
	 * @return respone string tu server
	 * @throws Exception
	 */
	public static String post(String url, String data, String token) throws IOException {
		allowMethods("PATCH");
		HttpURLConnection conn = setupConnection(url, "GET", token);
		Writer writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
		writer.write(data);
		writer.close();
		
		String response = readResponse(conn);
		return response;
	}

	/**
	 * Phuong thuc cho phep cho goi nhieu loai giao thuc khac nhau
	 * @deprecated chi hoat dong voi java <= 11
	 * @param methods string loai giao thuc cho phep
	 */
	private static void allowMethods(String... methods) {
		try {
			Field methodsField = HttpURLConnection.class.getDeclaredField("methods");
			methodsField.setAccessible(true);

			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(methodsField, methodsField.getModifiers() & ~Modifier.FINAL);

			String[] oldMethods = (String[]) methodsField.get(null);
			Set<String> methodsSet = new LinkedHashSet<>(Arrays.asList(oldMethods));
			methodsSet.addAll(Arrays.asList(methods));
			String[] newMethods = methodsSet.toArray(new String[0]);

			methodsField.set(null/* static field */, newMethods);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

}
