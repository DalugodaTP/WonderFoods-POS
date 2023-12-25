package bo.custom.impl;

import bo.custom.OrderBo;
import dao.custom.OrderDao;
import dao.custom.impl.OrderDaoImpl;
import dto.OrderDetailsDto;
import dto.OrderDto;
import entity.OrderDetails;
import entity.Orders;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderBoImpl implements OrderBo {
    private final OrderDao orderDao = new OrderDaoImpl();

    @Override
    public boolean saveOrder(OrderDto dto) throws SQLException, ClassNotFoundException {
        List<OrderDetails> orderDetailList = new ArrayList<>();
        for (OrderDetailsDto orderDetail : dto.getList()){
            orderDetailList.add(new OrderDetails(
                    orderDetail.getOrderId(),
                    orderDetail.getItemCode(),
                    orderDetail.getQty(),
                    orderDetail.getUnitPrice()));
        }
        return orderDao.save(new Orders(dto.getOrderId(),dto.getDate(),dto.getCustId()),orderDetailList);
    }

    @Override
    public String generateId() throws SQLException, ClassNotFoundException {
        String id = orderDao.getLastOrder().getId();
        if (id!=null){
            int num = Integer.parseInt(id.split("[D]")[1]);
            num++;
            id = String.format("D%03d",num);
        }
        return id;
    }
}
