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
public class Login {
	public static void login(HttpServletRequest req, HttpServletResponse resp, Connection connection, JSONObject jsonObject) throws IOException{
		try {
			ResultSet rs = getProfile(connection, jsonObject);
			int user_id;
			if (rs.next()) {
				resp.getWriter().print(makeProfileJSON(rs).toString());
			}
			else
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
		String email = jsonObject.getString(Constants.EMAIL);
		String phone_num = jsonObject.getString(Constants.PHONE_NUMBER);
		String password = jsonObject.getString(Constants.PASSWORD);

		if (email.isEmpty() && phone_num.isEmpty())
			throw new JSONException("");

		String select_sql;
		PreparedStatement stmt;
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
		stmt.setString(2, password);
		return stmt.executeQuery();
	}

	public static JSONObject makeProfileJSON(ResultSet rs) throws SQLException, JSONException{
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
