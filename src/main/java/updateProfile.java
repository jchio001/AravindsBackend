import Misc.Constants;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by jman0_000 on 12/25/2015.
 */
public class updateProfile {
	public static void update(HttpServletRequest req, HttpServletResponse resp, Connection connection, JSONObject jsonObject)
			throws IOException {
		try {
			Long user_id = jsonObject.getLong(Constants.USER_ID);
			String name = jsonObject.getString(Constants.NAME);
			String about_me = jsonObject.getString(Constants.ABOUT_ME);
			String village = jsonObject.getString(Constants.VILLAGE);
			int zip_code = jsonObject.getInt(Constants.ZIP_CODE);
			String phone_number = jsonObject.getString(Constants.PHONE_NUMBER);
			String email = jsonObject.getString(Constants.EMAIL);

			String update_sql = "UPDATE Profile SET name = ?, about_me = ?, village = ?, zip_code = ?, phone_number = ?, email = ? where user_id = ?";
			PreparedStatement stmt = connection.prepareStatement(update_sql);
			stmt.setString(1, name);
			stmt.setString(2, about_me);
			stmt.setString(3, village);
			stmt.setInt(4, zip_code);
			stmt.setString(5 , phone_number);
			stmt.setString(6, email);
			stmt.setLong(7, user_id);
			stmt.executeUpdate();
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
