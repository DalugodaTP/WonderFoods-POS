package model.impl;

import com.jfoenix.controls.JFXButton;
import db.DBConnection;
import dto.ItemDto;
import dto.tm.ItemTm;
import model.ItemModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ItemModelImpl implements ItemModel {
    @Override
    public boolean saveItem(ItemDto dto) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO item VALUES('" + dto.getCode() + "','" + dto.getDesc() + "','" + dto.getUnitPrice() + "'," + dto.getQty() + ")";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        //executeUpdate changes the database
        return pstm.executeUpdate(sql)>0;
    }

    @Override
    public boolean updateItem(ItemDto dto) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE item SET code='" + dto.getCode() + "', `description`='" + dto.getDesc() + "', unitPrice='" + dto.getUnitPrice() + "', qtyOnHand='" + dto.getQty() + "' WHERE code = '" + dto.getCode() + "'";
        PreparedStatement stm = DBConnection.getInstance().getConnection().prepareStatement(sql);
         // Execute the prepared statement, not the SQL query
        return stm.executeUpdate()>0;
    }

    @Override
    public boolean deleteItem(String code) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM item WHERE code='" + code + "'";
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        //executeUpdate changes the database
        return pstm.executeUpdate(sql)>0;
    }

    @Override
    public boolean searchItem(String code) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public List<ItemDto> allItems() throws SQLException, ClassNotFoundException {
        //--create a new list
        List<ItemDto> list = new ArrayList<>();

        //create the entry
        String sql = "SELECT * FROM item";
        //--get an instance from the connection
        PreparedStatement pstm = DBConnection.getInstance().getConnection().prepareStatement(sql);
        //--capture the entire list
        ResultSet result = pstm.executeQuery(sql);
        // move the pointer to get all rows and add into the arrayList
        while (result.next()) {
            list.add(new ItemDto(
                    result.getString(1),
                    result.getString(2),
                    result.getDouble(3),
                    result.getInt(4))
            );
        }
        //return the list
        return list;
    }
}
