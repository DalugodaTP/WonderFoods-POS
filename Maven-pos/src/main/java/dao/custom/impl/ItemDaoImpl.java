package dao.custom.impl;

import dao.CrudDao;
import db.DBConnection;
import dto.ItemDto;
import dao.custom.ItemDao;
import entity.Item;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemDaoImpl implements ItemDao {

    @Override
    public boolean save(Item entity) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO item VALUES('" + entity.getCode() + "','" + entity.getDesc() + "','" + entity.getUnitPrice() + "'," + entity.getQtyOnHand() + ")";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        //executeUpdate changes the database
        return pstm.executeUpdate(sql)>0;
    }

    @Override
    public boolean update(Item entity) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE item SET code='" + entity.getCode() + "', `description`='" + entity.getDesc() + "', unitPrice='" + entity.getUnitPrice() + "', qtyOnHand='" + entity.getQtyOnHand() + "' WHERE code = '" + entity.getCode() + "'";
        PreparedStatement stm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        // Execute the prepared statement, not the SQL query
        return stm.executeUpdate()>0;
    }

    @Override
    public boolean delete(String value) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM item WHERE code='" + value + "'";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        //executeUpdate changes the database
        return pstm.executeUpdate(sql)>0;
    }

    @Override
    public List getAll() throws SQLException, ClassNotFoundException {
        //--create a new list
        List<Item> list = new ArrayList<>();

        //create the entry
        String sql = "SELECT * FROM item";
        //--get an instance from the connection
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        //--capture the entire list
        ResultSet result = pstm.executeQuery(sql);
        // move the pointer to get all rows and add into the arrayList
        while (result.next()) {
            list.add(new Item(
                    result.getString(1),
                    result.getString(2),
                    result.getDouble(3),
                    result.getInt(4))
            );
        }
        //return the list
        return list;
    }

    public ItemDto getItem(String code) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM item WHERE code=?";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        pstm.setString(1, code);
        ResultSet resultSet = pstm.executeQuery();
        if (resultSet.next()) {
            return new ItemDto(resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getDouble(3),
                    resultSet.getInt(4)
            );
        }
        return null;
    }

    @Override
    public ItemDto searchItem(String code) {
        return null;
    }
}
