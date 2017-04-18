package lesson22.practical;

// Class EmployeeList displays Employees from the table EMP
// using JDBC drivers of type 4

import java.sql.*;

class EmployeeList {

    public static void main(String argv[]) {

        String urlDerbyDB = "jdbc:derby://localhost:1527/C:/Program Files/Java/jdk1.8.0_102/db/bin/Lesson22";
        //String urlJDBC = "org.apache.derby.jdbc.ClientDriver";
        //Class.forName(urlJDBC);
        // Build an SQL String
        String sqlQuery = "SELECT * from Employee";

        try (Connection connection = DriverManager.getConnection(urlDerbyDB);
             // Create a Statement object
             PreparedStatement prstmt = connection.prepareStatement(sqlQuery);
             // Execute SQL and get obtain the ResultSet object
             ResultSet rs = prstmt.executeQuery()) {

            // Process the result set - print Employees
            while (rs.next()) {
                int empNo = rs.getInt("EMPNO");
                String eName = rs.getString("ENAME");
                String job = rs.getString("JOB_TITLE");
                System.out.println(empNo + ", " + eName + ", " + job);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}

