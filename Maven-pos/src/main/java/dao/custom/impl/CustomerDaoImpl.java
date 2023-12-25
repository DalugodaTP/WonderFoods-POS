package dao.custom.impl;

import dao.util.CrudUtil;
import dto.CustomerDto;
import dao.custom.CustomerDao;
import entity.Customer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDaoImpl implements CustomerDao {
    @Override
    public boolean save(Customer entity) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO customer VALUES(?,?,?,?)";

        return CrudUtil.execute(
                sql,
                entity.getId(),
                entity.getName(),
                entity.getAddress(),
                String.valueOf(entity.getSalary()));
    }

    @Override
    public boolean update(Customer entity) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE customer SET name=?, address=?, salary=? WHERE id=?";
        return CrudUtil.execute(
                sql,
                entity.getName(),
                entity.getAddress(),
                entity.getSalary(),
                entity.getId());
    }

    @Override
    public boolean delete(String value) throws SQLException, ClassNotFoundException {
        String sql = "DELETE from customer WHERE id=?";
        return CrudUtil.execute(sql,value);
    }

    @Override
    public List<Customer> getAll() throws SQLException, ClassNotFoundException {
        //interacts with the database from here
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM Customer";
        ResultSet resultSet = CrudUtil.execute(sql);
        while(resultSet.next()){
            list.add(new Customer(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getDouble(4)
            ));
        }
        return list; //return a list of customers
    }

    @Override
    public CustomerDto searchCustomer(String id) {
        return null;
    }
}
