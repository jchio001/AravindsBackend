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
			String village = jsonObject.getString(Constants.VILLAGE);
			int zip_code = jsonObject.getInt(Constants.ZIP_CODE);
			String phone_number = jsonObject.getString(Constants.PHONE_NUMBER);
			String email = jsonObject.getString(Constants.EMAIL);
			String password = jsonObject.getString(Constants.PASSWORD);

			if (userExists(connection, email, phone_number)) {
				resp.setStatus(Constants.BAD_REQUEST);
				return;
			}

			String insert_sql = "INSERT INTO profile (name, about_me, village, zip_code, phone_number, email, password) VALUES (?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement stmt = connection.prepareStatement(insert_sql, Statement.RETURN_GENERATED_KEYS);

			stmt.setString(1, name);
			stmt.setString(2, about_me);
			stmt.setString(3, village);
			stmt.setInt(4, zip_code);
			stmt.setString(5, phone_number);
			stmt.setString(6, email);
			stmt.setString(7, password);

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

	public static boolean userExists(Connection connection, String email, String phone_number)
	throws SQLException{
		String select_sql = "Select * from Profile where email = ? or phone_number = ?";
		PreparedStatement stmt = connection.prepareStatement(select_sql);
		stmt.setString(1, email);
		stmt.setString(2, phone_number);
		ResultSet rs = stmt.executeQuery();
		return rs.next();
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

