package il.ac.shenkar.todos.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetUrlFromTomer {
	private URL url;

	public GetUrlFromTomer() {
	}

	public GetUrlFromTomer(URL url) {
		super();
		this.url = url;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	@SuppressWarnings("finally")
	public String readTodo() {
		String response = null;
		try {
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			InputStream in = new BufferedInputStream(
					urlConnection.getInputStream());

			InputStreamReader inReader = new InputStreamReader(in);
			BufferedReader bufferedReader = new BufferedReader(inReader);
			StringBuilder responseBuilder = new StringBuilder();
			for (String line = bufferedReader.readLine(); line != null; line = bufferedReader
					.readLine()) {
				responseBuilder.append(line);
			}
			response = responseBuilder.toString();

		} catch (Exception e) {
			e.printStackTrace();
			response = null;
		} finally {
			return response;
		}

	}

}
