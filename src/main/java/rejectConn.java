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
public class rejectConn {
	public static void rejectConn(HttpServletRequest req, HttpServletResponse resp, Connection connection, JSONObject jsonObject)
			throws IOException {
		try {
			Long req_id = jsonObject.getLong(Constants.REQ_ID);
			Long target_id = jsonObject.getLong(Constants.TARGET_ID);
			String insert_sql = "UPDATE Connections SET status = ? where requester_id = ? and target_id = ?";
			PreparedStatement stmt = connection.prepareStatement(insert_sql);
			stmt.setString(1, Constants.REJECTED);
			stmt.setLong(2, req_id);
			stmt.setLong(3, target_id);
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
