package dao.util;

import db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CrudUtil {
    //--execute will return a result set
    //--executeUpdate will return a boolean
    //--execute will act as a common for both
    //-(<T>T  - cast the return from the type T
    public static <T>T execute(String sql, Object...args) throws SQLException, ClassNotFoundException {
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        if (sql.startsWith("SELECT") || sql.startsWith("select")){
            return (T)pstm.executeQuery();
        }

        for (int i = 0; i < args.length; i++) {
            pstm.setObject((i+1), args[i]);
        }
        return (T)(Boolean)(pstm.executeUpdate()>0); //cast by both T and boolean
    }
}
