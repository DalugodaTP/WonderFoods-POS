package dao.custom;

import dao.CrudDao;
import dto.OrderDto;
import entity.Item;
import entity.OrderDetails;
import entity.Orders;

import java.sql.SQLException;
import java.util.List;

public interface OrderDao extends CrudDao<Item> {
    boolean save(Orders entity, List<OrderDetails> orderDetailList) throws SQLException, ClassNotFoundException;
    Orders getLastOrder() throws SQLException, ClassNotFoundException;

}
