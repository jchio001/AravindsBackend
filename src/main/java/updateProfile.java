import Misc.Constants;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
			int src_zip = jsonObject.getInt(Constants.SRC_ZIP);
			int dest_zip = jsonObject.getInt(Constants.DEST_ZIP);
			String phone_number = jsonObject.getString(Constants.PHONE_NUMBER);
			String email = jsonObject.getString(Constants.EMAIL);
			String gender = jsonObject.getString(Constants.GENDER);

			String update_sql = "UPDATE Profile SET name = ?, about_me = ?, src_zip = ?, dest_zip = ?, phone_number = ?, " +
					"email = ?, gender = ?, where user_id = ?";
			PreparedStatement stmt = connection.prepareStatement(update_sql);
			stmt.setString(1, name);
			stmt.setString(2, about_me);
			stmt.setInt(3, src_zip);
			stmt.setInt(4, dest_zip);
			stmt.setString(5 , phone_number);
			stmt.setString(6, email);
			stmt.setString(7, gender);
			stmt.setLong(8, user_id);
			stmt.executeUpdate();
			stmt.close();
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
