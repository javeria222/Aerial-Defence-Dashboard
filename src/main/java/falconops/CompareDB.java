package falconops;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class CompareDB extends DB_GUI {

    //           ====COMPARE AIRCRAFT ID WITH DATABASE====
    // Compare blipID with authorized aircraft IDs from database
    public static void comparision(String blipID, List<String> targetList){
        Connection conn = getConnection();

        if (conn == null) {
            System.out.println("⚠️ Cannot perform comparison without DB connection.");
            return;
        }

        String query = "SELECT Aircraft_ID FROM falconops.aircraft";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            targetList.clear(); // Clear the passed-in list
            while (rs.next()) {
                targetList.add(rs.getString("Aircraft_ID"));
            }

        } catch (SQLException e) {
            System.out.println("❌ Comparison failed: " + e.getMessage());
        }
    }

}
