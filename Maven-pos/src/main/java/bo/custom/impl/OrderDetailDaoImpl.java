package bo.custom.impl;

import dao.custom.OrderDetailsDao;
import dao.util.CrudUtil;
import db.DBConnection;
import dto.OrderDetailsDto;
import entity.OrderDetails;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class OrderDetailDaoImpl implements OrderDetailsDao {
    @Override
    public boolean saveOrderDetails(List<OrderDetailsDto> list) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO orderdetail VALUES(?,?,?,?)";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);

        for (OrderDetailsDto entity:list) {
            if (!(Boolean) CrudUtil.execute(sql,entity.getOrderId(),entity.getItemCode(),entity.getQty(),entity.getUnitPrice())){
                return false;
            }
        }
        return true;
    }
}
