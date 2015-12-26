import Misc.Constants;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
		try {
			Connection connection = getConnection(response);
			if (connection != null) {
				String path = request.getRequestURI();
				String[] pathPieces = path.split("/");
				if (pathPieces[1].equals("profile") && pathPieces.length == 3)
					getProfile.getProfile(request, response, connection, pathPieces[2]);
				else if (pathPieces[1].equals("suggestions") && pathPieces.length == 3)
					getSuggestions.getSuggestions(request, response, connection, pathPieces[2]);
				else
					response.setStatus(Constants.NOT_FOUND);

				connection.close();
			}
		}
		catch (Exception e) {
			response.setStatus(Constants.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		// Get request body into string
		StringBuilder requestBody = new StringBuilder();
		String line;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null) {
				requestBody.append(line);
			}
		}
		catch (IOException e) {
			response.setStatus(Constants.INTERNAL_SERVER_ERROR);
			response.getWriter().print(Constants.READ_BODY_FAIL);
			return;
		}

		// Get connection to DB
		Connection connection = getConnection(response);

		if (connection != null) {
			// Business logic
			try {
				JSONObject jsonObject = new JSONObject(requestBody.toString());
				String path = request.getRequestURI();
				String[] pathPieces = path.split("/");
				if (pathPieces[1].equals("createAccount"))
					createAccount.createAccount(request, response, connection, jsonObject);
				else if (pathPieces[1].equals("login"))
					Login.login(request, response, connection, jsonObject);
				else if (pathPieces[1].equals("connections")) {
					if (pathPieces[2].equals("request"))
						sendConn.sendConn(request, response, connection, jsonObject);
					else if (pathPieces[2].equals("accept"))
						acceptConn.acceptConn(request, response, connection, jsonObject);
					else
						response.setStatus(Constants.NOT_FOUND);
				}
				else
					response.setStatus(Constants.NOT_FOUND);

                //response.getWriter().print("POST!");
            }
			catch (JSONException e) {
				response.setStatus(Constants.BAD_REQUEST);
				response.getWriter().print(Constants.BAD_BODY_MESSAGE);
			}

			finally {
				try {
					connection.close();
				}
				catch (SQLException ignored) {}
			}
		}
	}

	public static String getStackTrace(Throwable aThrowable) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}

	private static Connection getConnection(HttpServletResponse response) throws IOException {
		try {
			URI dbUri = new URI(System.getenv("DATABASE_URL"));
			String username = dbUri.getUserInfo().split(":")[0];
			String password = dbUri.getUserInfo().split(":")[1];
			String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + dbUri.getPath();
			return DriverManager.getConnection(dbUrl, username, password);
		}
		catch (URISyntaxException|SQLException e) {
			response.setStatus(Constants.INTERNAL_SERVER_ERROR);
			response.getWriter().print(Constants.DB_CONNECTION_FAIL);
			return null;
		}
	}

	public static void main(String[] args) throws Exception {
		Server server = new Server(Integer.valueOf(System.getenv("PORT")));
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		server.setHandler(context);
		context.addServlet(new ServletHolder(new Main()), "/*");
		server.start();
		server.join();
	}
}
