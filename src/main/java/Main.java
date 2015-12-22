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
import java.sql.*;

public class Main extends HttpServlet{

    private static String TABLE_CREATION = "CREATE TABLE IF NOT EXISTS profile (user_id SERIAL, name varchar(32), " +
            "about_me varchar(1024), village varchar(32), zip_code int, phone_number varchar(16), email varchar(32));";
    private static String TABLE_CREATION_2 = "CREATE TABLE IF NOT EXISTS Connections (requester_id int, target_id int, status varchar(32));";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Connection connection = null;
		try {
			connection = getConnection();
			Statement stmt = connection.createStatement();
			stmt.executeUpdate(TABLE_CREATION);
		}
		catch (Exception e) {
			resp.setStatus(500);
			resp.getWriter().print("Table creation error: " + e.getMessage());
		}

		finally {
			try {
				connection.close();
			}
			catch (SQLException e) {
				resp.getWriter().print("Failed to close connection: " + getStackTrace(e));
			}
		}
	}

  	@Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      Connection connection = null;
      try {
          connection = getConnection();
          Statement stmt = connection.createStatement();
          stmt.executeUpdate(TABLE_CREATION);
          stmt.executeUpdate(TABLE_CREATION_2);
      }
      catch (Exception e) {
          resp.setStatus(500);
          resp.getWriter().print("Table creation error: " + e.getMessage());
      }

      StringBuffer jb = new StringBuffer();
      String line;
      try {
          BufferedReader reader = req.getReader();
          while ((line = reader.readLine()) != null)
              jb.append(line);
      }
      catch (IOException e) {
          resp.setStatus(400);
          resp.getWriter().print("Couldn't read in request body: " + getStackTrace(e));
      }

	  try {
		  JSONObject jsonObject = new JSONObject(jb.toString());
		  if (req.getRequestURI().endsWith("/createAccount")) {
			  resp.setStatus(200);
			  String name = jsonObject.getString("name");
			  String about_me = jsonObject.getString("about_me");
			  String village = jsonObject.getString("village");
			  String zip_code = jsonObject.getString("zip_code");
			  String phone_number = jsonObject.getString("phone_number");
			  String email = jsonObject.getString("email");
			  String update_sql = "INSERT INTO profile (name, about_me, village, zip_code, phone_number, email) VALUES (?, ?, ?, ?, ?, ?)";
			  try {
				  PreparedStatement stmt = connection.prepareStatement(update_sql);
				  stmt.setString(1, name);
				  stmt.setString(2, about_me);
				  stmt.setString(3, village);
				  stmt.setString(4, zip_code);
				  stmt.setString(5, phone_number);
				  stmt.setString(6, email);
				  stmt.executeUpdate();
				  stmt.close();
				  resp.getWriter().print("Created!");
			  }
			  catch (SQLException e) {
				  resp.getWriter().print("SQL ERROR @POST: " + getStackTrace(e));
			  }
		  }
		  else {
			  resp.setStatus(404);
		  }
	  }
	  catch (JSONException e1) {
		  resp.setStatus(400);
		  resp.getWriter().print("Error parsing request JSON: " + getStackTrace(e1));
	  }

	  finally {
		  try {
			  connection.close();
		  }
		  catch (SQLException e) {
			  resp.getWriter().print("Failed to close connection: " + getStackTrace(e));
		  }
	  }

  }

    private static Connection getConnection() throws URISyntaxException, SQLException {
      URI dbUri = new URI(System.getenv("DATABASE_URL"));

      String username = dbUri.getUserInfo().split(":")[0];
      String password = dbUri.getUserInfo().split(":")[1];
      String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + dbUri.getPath();

      return DriverManager.getConnection(dbUrl, username, password);
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

    public static String getStackTrace(Throwable aThrowable) {
      final Writer result = new StringWriter();
      final PrintWriter printWriter = new PrintWriter(result);
      aThrowable.printStackTrace(printWriter);
      return result.toString();
    }

}
