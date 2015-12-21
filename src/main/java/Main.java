import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

import java.net.URI;
import java.net.URISyntaxException;

import static spark.Spark.*;

import com.sun.net.httpserver.HttpServer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import spark.template.freemarker.FreeMarkerEngine;
import spark.ModelAndView;
import static spark.Spark.get;

import com.heroku.sdk.jdbc.DatabaseUrl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Main extends HttpServlet{

    private static String TABLE_CREATION = "CREATE TABLE USER (user_id SERIAL, name varchar(32), " +
          "about_me varchar(1024), num_wrong int, run_type varchar(32), time_occurred bigint)";

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
