package model.impl;

import db.DBConnection;
import dto.OrderDto;
import model.CustomerModel;
import model.ItemModel;
import model.OrderDetailsModel;
import model.OrderModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderModelImpl implements OrderModel {
    OrderDetailsModel orderDetailsModel = new OrderDetailsModelImpl();

    @Override
    public boolean saveOrder(OrderDto dto) throws SQLException {
        //Transaction
        Connection connection=null;
        try {
            connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(false); //do not autosave, wait for manual go ahead

            String sql = "INSERT INTO orders VALUES(?,?,?)";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setString(1, dto.getOrderId());
            pstm.setString(2, dto.getDate());
            pstm.setString(3, dto.getCustId());
            //--if order is saved, then save order details
            if (pstm.executeUpdate() > 0) {
                boolean isDetaiedSaved = orderDetailsModel.saveOrderDetails(dto.getList());
                if (isDetaiedSaved) {
                    connection.commit();
                    return true;
                }
            }
        } catch (SQLException |ClassNotFoundException ex) {
            connection.rollback(); //do not save, delete the saved values (if exception occurs)
            ex.printStackTrace();
        }finally {
            connection.setAutoCommit(true); //revert back the settings
        }
        return false;
    }

    public OrderDto lastOrder() throws ClassNotFoundException, SQLException {
        String sql = "SELECT * FROM orders ORDER BY id DESC LIMIT 1";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        ResultSet resultSet = pstm.executeQuery();
        if (resultSet.next()) {
            return new OrderDto(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    null //order details list
            );
        }
        return null;
    }
}
