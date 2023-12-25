package bo.custom;

import dto.OrderDto;

import java.sql.SQLException;

public interface OrderBo {
    boolean saveOrder(OrderDto dto)throws SQLException, ClassNotFoundException;
    String generateId() throws SQLException, ClassNotFoundException;
}
