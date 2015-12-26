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
public class getProfile {
	public static void getProfile(HttpServletRequest req, HttpServletResponse resp, Connection connection, String id) throws IOException {
		try {
			String select_sql = "SELECT * from Profile where user_id = ?";
			PreparedStatement stmt = connection.prepareStatement(select_sql);
			try {
				stmt.setLong(1, Long.parseLong(id));
			}
			catch (NumberFormatException e) {
				resp.setStatus(Constants.BAD_REQUEST);
				return;
			}

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				JSONObject profile = new JSONObject();
				profile.put(Constants.NAME ,rs.getString(Constants.NAME));
				profile.put(Constants.ABOUT_ME ,rs.getString(Constants.ABOUT_ME));
				profile.put(Constants.VILLAGE ,rs.getString(Constants.VILLAGE));
				profile.put(Constants.ZIP_CODE ,rs.getString(Constants.ZIP_CODE));
				profile.put(Constants.PHONE_NUMBER ,rs.getString(Constants.PHONE_NUMBER));
				profile.put(Constants.EMAIL ,rs.getString(Constants.EMAIL));
				resp.getWriter().print(profile.toString());
			}
			else
				resp.setStatus(Constants.BAD_REQUEST);
		}
		catch (SQLException|JSONException e) {
			resp.setStatus(Constants.INTERNAL_SERVER_ERROR);
			resp.getWriter().print(e.getMessage());
		}
	}
}
