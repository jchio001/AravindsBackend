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

//table name = conn;
public class sendConn {
	public static void sendConn(HttpServletRequest req, HttpServletResponse resp, Connection connection, JSONObject jsonObject)
	throws IOException{
		try {
			Long req_id = jsonObject.getLong(Constants.REQ_ID);
			Long target_id = jsonObject.getLong(Constants.TARGET_ID);
			String insert_sql = "Insert into Connections (requester_id, target_id, status) VALUES (?, ?, ?)";
			PreparedStatement stmt = connection.prepareStatement(insert_sql);
			stmt.setLong(1, req_id);
			stmt.setLong(2, target_id);
			stmt.setString(3, Constants.PENDING);
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
