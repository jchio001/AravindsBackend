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
			String update_sql = "INSERT INTO profile (name, about_me, village, zip_code, phone_number, email, password) VALUES (?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement stmt = connection.prepareStatement(update_sql, Statement.RETURN_GENERATED_KEYS);

			stmt.setString(1, name);
			stmt.setString(2, about_me);
			stmt.setString(3, village);
			stmt.setInt(4, zip_code);
			stmt.setString(5, phone_number);
			stmt.setString(6, email);
			stmt.setString(7, password);

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
					throw new SQLException();
				}
			}
			stmt.close();

			JSONObject scoreReport = new JSONObject();
			scoreReport.put(Constants.ID, userId);
			resp.getWriter().print(scoreReport.toString());
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
}

