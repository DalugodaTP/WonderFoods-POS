package dao.custom.impl;

import db.DBConnection;
import dto.OrderDetailsDto;
import dao.custom.OrderDetailsDao;
import entity.OrderDetails;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class OrderDetailsDaoImpl implements OrderDetailsDao {
    @Override
    public boolean saveOrderDetails(List<OrderDetails> list) throws SQLException, ClassNotFoundException {
        boolean isDetailsSaved = true;
        for (OrderDetails dto: list) {
            String sql = "INSERT INTO orderdetail VALUES(?,?,?,?)";
            PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
            pstm.setString(1,dto.getOrderId());
            pstm.setString(2,dto.getItemCode());
            pstm.setInt(3, dto.getQty());
            pstm.setDouble(4, dto.getUnitPrice());

            if (!(pstm.executeUpdate() >0)){
                isDetailsSaved =false;
            }
        }
        return isDetailsSaved;
    }
}
