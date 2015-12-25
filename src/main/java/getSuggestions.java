import org.json.JSONArray;
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
 * Created by jman0_000 on 12/25/2015.
 */
public class getSuggestions {
	public static void getSuggestions(HttpServletRequest req, HttpServletResponse resp, Connection connection, String id) throws IOException {
		try {
			String select_sql = "SELECT village, zip_code FROM Profile where user_id = ?";
			PreparedStatement stmt = connection.prepareStatement(select_sql);
			try {
				stmt.setLong(1, Long.parseLong(id));
			}
			catch (NumberFormatException e) {
				resp.setStatus(Constants.BAD_REQUEST);
				return;
			}
			String village;
			int zip_code;
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				village = rs.getString(Constants.VILLAGE);
				zip_code = rs.getInt(Constants.ZIP_CODE);
			}
			else {
				resp.setStatus(Constants.BAD_REQUEST);
				return;
			}

			int min_zip = zip_code - 2;
			int max_zip = zip_code + 2;
			select_sql = "Select user_id, name, village, zip_code FROM Profile where village = ? and zip_code BETWEEN " +
					"? and ?";
			stmt = connection.prepareStatement(select_sql);
			stmt.setString(1, village);
			stmt.setInt(1, min_zip);
			stmt.setInt(1, max_zip);
			rs = stmt.executeQuery();

			JSONArray suggestionArr = new JSONArray();
			JSONObject user;
			while (rs.next()) {
				user = new JSONObject();
				user.put(Constants.USER_ID, rs.getInt(Constants.USER_ID));
				user.put(Constants.NAME, rs.getInt(Constants.NAME));
				user.put(Constants.VILLAGE, rs.getInt(Constants.VILLAGE));
				user.put(Constants.ZIP_CODE, rs.getInt(Constants.ZIP_CODE));
				suggestionArr.put(user);
			}
			resp.getWriter().print(suggestionArr.toString());
		}
		catch (SQLException|JSONException e){
			resp.setStatus(Constants.INTERNAL_SERVER_ERROR);
			resp.getWriter().print(e.getMessage());
		}
	}
}
