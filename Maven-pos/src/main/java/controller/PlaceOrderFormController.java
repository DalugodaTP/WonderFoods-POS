package controller;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.mysql.cj.x.protobuf.MysqlxCrud;
import dto.CustomerDto;
import dto.ItemDto;
import dto.OrderDetailsDto;
import dto.OrderDto;
import dto.tm.ItemTm;
import dto.tm.OrderTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.CustomerModel;
import model.ItemModel;
import model.OrderModel;
import model.impl.CustomerModelImpl;
import model.impl.ItemModelImpl;
import model.impl.OrderModelImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PlaceOrderFormController {
    public AnchorPane placeOrderPane;
    public JFXButton btnBack;
    public JFXComboBox cmbCustId;
    public JFXComboBox cmbItemCode;
    public JFXTextField txtCustomerName;
    public JFXTextField txtDesc;
    public JFXTextField txtUnitPrice;
    public JFXTextField txtQty;
    public JFXTreeTableView<OrderTm> tblOrder;
    public TreeTableColumn colCode;
    public TreeTableColumn colDEsc;
    public TreeTableColumn colQty;
    public TreeTableColumn colAmount;
    public TreeTableColumn colOption;
    public Label lblTotal;
    public Label lblOrderId;

    //--Store within an array without connecting to the database each time
    private List<CustomerDto> customers = new ArrayList();
    private List<ItemDto> items = new ArrayList();
    private double tot = 0;

    //--create objects for customer and items
    private CustomerModel customerModel = new CustomerModelImpl();
    private ItemModel itemModel = new ItemModelImpl();
    private OrderModel orderModel = new OrderModelImpl();

    //--Add to the table
    private ObservableList<OrderTm> tmList = FXCollections.observableArrayList();

    public void initialize() {
        //------Declare columns and mapping the ItemTm with the columns
        colCode.setCellValueFactory(new TreeItemPropertyValueFactory<>("code"));
        colDEsc.setCellValueFactory(new TreeItemPropertyValueFactory<>("desc"));
        colQty.setCellValueFactory(new TreeItemPropertyValueFactory<>("qty"));
        colAmount.setCellValueFactory(new TreeItemPropertyValueFactory<>("qty"));
        colOption.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));

        generateId();
        loadCustomerId();
        loadItemCodes();

        //--add action to the drop lists
        cmbCustId.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, id) -> { //newValue is the customerId
            for (CustomerDto dto : customers) {
                if (dto.getId().equals(id)) {
                    txtCustomerName.setText(dto.getName());
                }
            }
        });

        cmbItemCode.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, code) -> { //newValue is the item code
            for (ItemDto dto : items) {
                if (dto.getCode().equals(code)) {
                    txtDesc.setText(dto.getDesc());
                    txtUnitPrice.setText(String.valueOf(dto.getUnitPrice()));
                }
            }
        });
    }

    private void loadItemCodes() {
        try {
            items = itemModel.allItems();
            ObservableList list = FXCollections.observableArrayList();
            for (ItemDto dto : items) {
                list.add(dto.getCode());
            }
            cmbItemCode.setItems((ObservableList) list);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadCustomerId() {
        try {
            customers = customerModel.allCustomers();
            ObservableList list = FXCollections.observableArrayList();
            for (CustomerDto dto : customers) {
                list.add(dto.getId());
            }
            cmbCustId.setItems((ObservableList) list);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void backButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) placeOrderPane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/DashboardForm.fxml"))));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void addToCartButtonOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        try {
            double amount = itemModel.getItem(cmbItemCode.getValue().toString()).getUnitPrice() * Integer.parseInt(txtQty.getText());
            JFXButton btn = new JFXButton("Delete");

            OrderTm tm = new OrderTm(
                    cmbItemCode.getValue().toString(),
                    txtDesc.getText(),
                    Integer.parseInt(txtQty.getText()),
                    amount,
                    btn
            );

            //--Remove the entry when button is pressed
            btn.setOnAction(actionEvent1 -> {
                tmList.remove(tm);
                //--update the total and table
                tot -= tm.getAmount();
                tblOrder.refresh();
                lblTotal.setText(String.format("%.2f", tot));
            });

            boolean isExist = false;

            for (OrderTm order : tmList) {
                if (order.getCode().equals(tm.getCode())) {
                    order.setQty(order.getQty() + tm.getQty());
                    order.setAmount(order.getAmount() + tm.getAmount());
                    isExist = true;
                    tot += tm.getAmount();
                }
            }

            if (!isExist) {
                tmList.add(tm);
                tot += tm.getAmount();
            }

            TreeItem<OrderTm> treeObject = new RecursiveTreeItem<OrderTm>(tmList, RecursiveTreeObject::getChildren);
            tblOrder.setRoot(treeObject);
            tblOrder.setShowRoot(false);
            //--update the total amount
            lblTotal.setText(String.format("%.2f", tot));
            txtQty.setText("");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public String generateId() {
        try {
            OrderDto dto = orderModel.lastOrder();
            if (dto != null) {
                String id = dto.getOrderId();
                int num = Integer.parseInt(id.split("[D]")[1]);
                num++;
                lblOrderId.setText(String.format("D%03d", num));
            } else {
                lblOrderId.setText("D001");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void placeOrderButtonOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        List<OrderDetailsDto> list = new ArrayList<>();
        for (OrderTm tm : tmList) {
            list.add(new OrderDetailsDto(
                    lblOrderId.getText(),
                    tm.getCode(),
                    tm.getQty(),
                    tm.getAmount() / tm.getQty()
            ));
        }

        //      if (!tmList.isEmpty()){
        boolean isSaved = false;
        try {
            isSaved = orderModel.saveOrder(new OrderDto(
                    lblOrderId.getText(),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd")),
                    cmbCustId.getValue().toString(),
                    list
            ));

            if (isSaved) {
                new Alert(Alert.AlertType.INFORMATION, "Order Saved !!").show();
            }else{
                new Alert(Alert.AlertType.ERROR, "Something went wrong !!").show();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        //      }
    }
}
