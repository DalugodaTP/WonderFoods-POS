package dao.custom;

import entity.OrderDetails;

import java.sql.SQLException;
import java.util.List;

public interface OrderDetailsDao {
    boolean saveOrderDetails(List<OrderDetails> list) throws SQLException, ClassNotFoundException;
}
