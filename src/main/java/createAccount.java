import Misc.Constants;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

/**
 * Created by jman0_000 on 12/24/2015.
 */
public class createAccount {
	public static void createAccount(HttpServletRequest req, HttpServletResponse resp, Connection connection, JSONObject jsonObject) throws IOException{
		try {
			String name = jsonObject.getString(Constants.NAME);
			String about_me = jsonObject.getString(Constants.ABOUT_ME);
			int src_zip = jsonObject.getInt(Constants.SRC_ZIP);
			int dest_zip = jsonObject.getInt(Constants.DEST_ZIP);
			String phone_number = jsonObject.getString(Constants.PHONE_NUMBER);
			String email = jsonObject.getString(Constants.EMAIL);
			String gender = jsonObject.getString(Constants.GENDER);
			String password = jsonObject.getString(Constants.PASSWORD);

			if (doesUserExist(connection, resp, phone_number, email))
				return;

			String insert_sql = "INSERT INTO profile (name, about_me, src_zip, dest_zip, phone_number, email, gender, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement stmt = connection.prepareStatement(insert_sql, Statement.RETURN_GENERATED_KEYS);

			stmt.setString(1, name);
			stmt.setString(2, about_me);
			stmt.setInt(3, src_zip);
			stmt.setInt(4, dest_zip);
			stmt.setString(5, phone_number);
			stmt.setString(6, email);
			stmt.setString(7, gender);
			stmt.setString(8, password);

			returnID(stmt, resp);
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

	public static boolean doesUserExist(Connection connection, HttpServletResponse resp, String phone_number, String email)
			throws SQLException {
		String select_sql;
		PreparedStatement stmt;
		if (phone_number.isEmpty() && !email.isEmpty()) {
			select_sql = "Select * from Profile where email = ?";
			stmt = connection.prepareStatement(select_sql);
			stmt.setString(1, email);
		}
		else if (!phone_number.isEmpty() && email.isEmpty()) {
			select_sql = "Select * from Profile where phone_number = ?";
			stmt = connection.prepareStatement(select_sql);
			stmt.setString(1, phone_number);
		}
		else if (!email.isEmpty() && !phone_number.isEmpty()) {
			select_sql = "Select * from Profile where phone_number = ? or email = ?";
			stmt = connection.prepareStatement(select_sql);
			stmt.setString(1, phone_number);
			stmt.setString(2, email);
		}
		else {
			resp.setStatus(Constants.BAD_REQUEST);
			return true;
		}
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			resp.setStatus(Constants.BAD_REQUEST);
			return true;
		}
		else
			return false;
	}

	public static void returnID(PreparedStatement stmt, HttpServletResponse resp) throws SQLException, JSONException, IOException{
		long userId;
		int affectedRows = stmt.executeUpdate();
		if (affectedRows == 0) {
			throw new SQLException();
		}
		try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
			if (generatedKeys.next()) {
				userId = generatedKeys.getLong(1);
			}
			else {
				resp.setStatus(Constants.INTERNAL_SERVER_ERROR);
				throw new SQLException();
			}
		}
		stmt.close();

		JSONObject scoreReport = new JSONObject();
		scoreReport.put(Constants.ID, userId);
		resp.getWriter().print(scoreReport.toString());
	}

}

