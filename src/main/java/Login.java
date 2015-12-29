import Misc.Constants;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by jman0_000 on 12/24/2015.
 */
//This class is built to only contain a function that does 1 thing: logs in.
public class Login {
	public static void login(HttpServletRequest req, HttpServletResponse resp, Connection connection, JSONObject jsonObject) throws IOException{
		try {
			//Call function getProfile, which will format the SQL statement using the info from the JSON and execute it
			//A result set is returned from this call, think of it as a 2D-Array of my result
			ResultSet rs = getProfile(connection, jsonObject);
			if (rs.next()) {
				//if login is valid, sent the profile information to output.
				resp.getWriter().print(makeProfileJSON(rs).toString());
			}
			else //else you entered false login information
				resp.setStatus(Constants.UNATHORIZED);
		}
		catch (JSONException e) {
			resp.setStatus(Constants.BAD_REQUEST);
			resp.getWriter().print(e.getMessage());
		}
		catch (SQLException exception) {
			resp.setStatus(Constants.INTERNAL_SERVER_ERROR);
			resp.getWriter().print(Main.getStackTrace(exception));
		}
	}

	public static ResultSet getProfile(Connection connection, JSONObject jsonObject) throws SQLException, JSONException{
		//Parse JSON
		String email = jsonObject.getString(Constants.EMAIL);
		String phone_num = jsonObject.getString(Constants.PHONE_NUMBER);
		String password = jsonObject.getString(Constants.PASSWORD);

		//Check for valid input
		if (email.isEmpty() && phone_num.isEmpty())
			throw new JSONException("");

		//Set up string that's the SQL stmt to be execute. ?'s are wildcards for the PrepareStatement.
		String select_sql;
		PreparedStatement stmt;
		//2 possible ways to login => 2 possible SQL statements
		if (email.isEmpty()) {
			select_sql = "Select * from Profile where phone_number = ? and password  = ?";
			stmt = connection.prepareStatement(select_sql);
			stmt.setString(1, phone_num);
		}
		else {
			select_sql = "Select * from Profile where email = ? and password  = ?";
			stmt = connection.prepareStatement(select_sql);
			stmt.setString(1, email);
		}
		//PrepareStatements restrict what goes in the ?'s, preventing SQL injections (ex: I can wipe out tables.)
		stmt.setString(2, password);
		ResultSet rs = stmt.executeQuery();
		return rs;
	}

	public static JSONObject makeProfileJSON(ResultSet rs) throws SQLException, JSONException{
		//Users are unique, so rs will only have 1 item in it at most.
		//If there's nothing in rs and we try to remove values from it, a SQL exception is thrown.
		JSONObject id = new JSONObject();
		id.put(Constants.USER_ID, rs.getInt(Constants.USER_ID));
		id.put(Constants.NAME, rs.getString(Constants.NAME));
		id.put(Constants.ABOUT_ME, rs.getString(Constants.ABOUT_ME));
		id.put(Constants.VILLAGE, rs.getString(Constants.VILLAGE));
		id.put(Constants.ZIP_CODE, rs.getInt(Constants.ZIP_CODE));
		id.put(Constants.PHONE_NUMBER, rs.getString(Constants.PHONE_NUMBER));
		id.put(Constants.EMAIL, rs.getString(Constants.EMAIL));
		return id;
	}

}
