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

//NOTE: for example on how to write a back-end function, check-out Login.java!
public class Main extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
		try {
			//If a GET HTTP request is sent, this function is called.
			Connection connection = getConnection(response);
			if (connection != null) {
				//if a connection is successfully made, parse the URI and call functions based on the parsed URI
				String path = request.getRequestURI();
				String[] pathPieces = path.split("/");
				if (pathPieces[1].equals("profile") && pathPieces.length == 4)
					getProfile.getProfile(request, response, connection, pathPieces[2], pathPieces[3]);
				else if (pathPieces[1].equals("suggestions") && pathPieces.length == 3)
					getSuggestions.getSuggestions(request, response, connection, pathPieces[2]);
				else if (pathPieces[1].equals("pendingconnections") && pathPieces.length == 3)
					getPendingConnCnt.getPendingConnCnt(request, response, connection, pathPieces[2]);
				else if (pathPieces[1].equals("changevisibility") && pathPieces.length == 4)
					changeVisibility.changeVisibility(request, response, connection, pathPieces[2], pathPieces[3]);
				else if (pathPieces[1].equals("connections")) {
					if ((pathPieces[2].equals("sent") || pathPieces[2].equals("received")) && pathPieces.length == 4)
						sentOrRcvdConn.getSentOrRcvdConns(request, response, connection, pathPieces[2], pathPieces[3]);
					else if (pathPieces.length == 3)
						getAcceptedConnections.getAcceptedConnections(request, response, connection, pathPieces[2]);
					else
						response.setStatus(Constants.NOT_FOUND);
				}
				else if (pathPieces[1].equals("search") && pathPieces.length == 6)
					Search.search(request, response, connection, pathPieces[2], pathPieces[3], pathPieces[4], pathPieces[5]);
				else
					response.setStatus(Constants.NOT_FOUND);

				try {
					connection.close();
				}
				catch (SQLException ignored) {}
			}
		}
		catch (Exception e) {
			response.setStatus(Constants.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		//If a POST HTTP request is sent, this function is called.
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
			//If the connection is successfully made, parse URI and call functions based on that,
			try {
				JSONObject jsonObject = new JSONObject(requestBody.toString());
				String path = request.getRequestURI();
				String[] pathPieces = path.split("/");
				if (pathPieces[1].equals("createAccount"))
					createAccount.createAccount(request, response, connection, jsonObject);
				else if (pathPieces[1].equals("updateProfile"))
					updateProfile.update(request, response, connection, jsonObject);
				else if (pathPieces[1].equals("login"))
					Login.login(request, response, connection, jsonObject);
				else if (pathPieces[1].equals("connections")) {
					if (pathPieces[2].equals("request"))
						sendConn.sendConn(request, response, connection, jsonObject);
					else if (pathPieces[2].equals("accept"))
						acceptConn.acceptConn(request, response, connection, jsonObject);
					else if (pathPieces[2].equals("reject"))
						rejectConn.rejectConn(request, response, connection, jsonObject);
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

	//For debugging purposes.
	public static String getStackTrace(Throwable aThrowable) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}

	//Function that does the connecting. This is always called on any clal to the API
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

	//Main function
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
