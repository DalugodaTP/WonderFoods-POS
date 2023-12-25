package dao.custom.impl;

import dao.custom.ItemDao;
import dao.util.CrudUtil;
import dao.custom.OrderDetailsDao;
import dao.custom.OrderDao;
import db.DBConnection;
import dto.OrderDetailsDto;
import entity.Item;
import entity.OrderDetails;
import entity.Orders;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

//--Change the data from the dto to entity objects
public class OrderDaoImpl implements OrderDao {
    private final OrderDetailsDao orderDetailDao = new OrderDetailsDaoImpl();
    private final ItemDao itemDao = new ItemDaoImpl();

    @Override
    public boolean save(Item entity) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean update(Item entity) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean delete(String value) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public List<Item> getAll() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public boolean save(Orders entity, List<OrderDetails> orderDetailList) throws SQLException, ClassNotFoundException {
        OrderDetailsDao orderDetailsDao = new OrderDetailsDaoImpl();
        //Transaction
        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(false); //do not autosave, wait for manual go ahead

            String sql = "INSERT INTO orders VALUES(?,?,?)";
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setString(1, entity.getId());
            pstm.setString(2, entity.getDate());
            pstm.setString(3, entity.getCustomerId());
            //--if order is saved, then save order details
            if (pstm.executeUpdate() > 0) {
                boolean isDetaiedSaved = orderDetailsDao.saveOrderDetails(orderDetailList);
                isDetaiedSaved = true;
                if (isDetaiedSaved) {
                    connection.commit();
                    return true;
                }
            }
        } catch (SQLException | ClassNotFoundException ex) {
            connection.rollback(); //do not save, delete the saved values (if exception occurs)
            ex.printStackTrace();
        } finally {
            connection.setAutoCommit(true); //revert back the settings
        }
        return false;
    }


//    @Override
//    public boolean save(Orders entity, List<OrderDetailsDto> orderDetailList) throws SQLException, ClassNotFoundException {
//        String sql = "INSERT INTO orders VALUES(?,?,?)";
//
//        if (CrudUtil.execute(sql, entity.getId(), entity.getDate(), entity.getCustomerId())) {
//            boolean isDetailsSaved = orderDetailDao.saveOrderDetails(orderDetailList);
//            boolean isItemsUpdated = itemDao.update(orderDetailList);
//            if (isDetailsSaved && isItemsUpdated) {
//                return true;
//            }
//        }
//        return false;
//    }

    @Override
    public Orders getLastOrder() throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM orders ORDER BY id DESC LIMIT 1";
        ResultSet resultSet = CrudUtil.execute(sql);
        if (resultSet.next()) {
            return new Orders(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3)
            );
        }
        return null;
    }
}
