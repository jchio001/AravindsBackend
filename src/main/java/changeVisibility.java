import Misc.Constants;
import org.json.JSONException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by jman0_000 on 12/28/2015.
 */
public class changeVisibility {
	public static void changeVisibility(HttpServletRequest req, HttpServletResponse resp, Connection connection, String mode, String id)
	throws IOException {
		try {
			long src_id;
			try {
				src_id = Long.parseLong(id);
			}
			catch (NumberFormatException e) {
				resp.setStatus(Constants.BAD_REQUEST);
				resp.getWriter().print(e.getMessage());
				return;
			}

			String update_sql = "UPDATE Profile SET visible = ? where user_id = ?";
			PreparedStatement stmt = connection.prepareStatement(update_sql);

			if (mode.equals("true"))
				stmt.setBoolean(1, true);
			else if (mode.equals("false"))
				stmt.setBoolean(1, false);
			else {
				resp.setStatus(Constants.BAD_REQUEST);
				return;
			}

			stmt.setLong(2, src_id);
			stmt.executeUpdate();
		}
		catch (SQLException exception) {
			resp.setStatus(Constants.INTERNAL_SERVER_ERROR);
			resp.getWriter().print(Main.getStackTrace(exception));
		}

	}

}
