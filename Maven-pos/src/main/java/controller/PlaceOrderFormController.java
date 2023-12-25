package controller;

import bo.custom.CustomerBo;
import bo.custom.ItemBo;
import bo.custom.OrderBo;
import bo.custom.impl.CustomerBoImpl;
import bo.custom.impl.ItemBoImpl;
import bo.custom.impl.OrderBoImpl;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import dao.custom.OrderDao;
import dto.CustomerDto;
import dto.ItemDto;
import dto.OrderDetailsDto;
import dto.OrderDto;
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
import dao.custom.impl.OrderDaoImpl;

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
    private CustomerBo customerBo = new CustomerBoImpl();
    private ItemBo itemDao = new ItemBoImpl();
    private OrderBo orderBo = new OrderBoImpl();

    //--Add to the table
    private ObservableList<OrderTm> tmList = FXCollections.observableArrayList();

    public void initialize() throws SQLException, ClassNotFoundException {
        //------Declare columns and mapping the ItemTm with the columns
        colCode.setCellValueFactory(new TreeItemPropertyValueFactory<>("code"));
        colDEsc.setCellValueFactory(new TreeItemPropertyValueFactory<>("desc"));
        colQty.setCellValueFactory(new TreeItemPropertyValueFactory<>("qty"));
        colAmount.setCellValueFactory(new TreeItemPropertyValueFactory<>("amount"));
        colOption.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));


        loadCustomerId();
        loadItemCodes();
        generateId();
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
    public String generateId() throws SQLException, ClassNotFoundException {
        try {
            lblOrderId.setText(orderBo.generateId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private void loadItemCodes() throws SQLException, ClassNotFoundException {
        items = itemDao.allItems();
        ObservableList list = FXCollections.observableArrayList();
        for (ItemDto dto : items) {
            list.add(dto.getCode());
        }
        cmbItemCode.setItems((ObservableList) list);
    }

    private void loadCustomerId() throws SQLException, ClassNotFoundException {
        customers = customerBo.allCustomers();
        ObservableList list = FXCollections.observableArrayList();
        for (CustomerDto dto : customers) {
            list.add(dto.getId());
        }
        cmbCustId.setItems((ObservableList) list);

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
        //--calculate the Amount using the values in the text fields

        double amount =  Double.parseDouble(txtUnitPrice.getText())* Integer.parseInt(txtQty.getText());


        //--create a button to be added into the table model
        JFXButton btn = new JFXButton("❌");

        //--capture the values from the fields to be added into the table
        OrderTm tm = new OrderTm(
                cmbItemCode.getValue().toString(),
                txtDesc.getText(),
                Integer.parseInt(txtQty.getText()),
                amount,
                btn
        );

        //--Delete action button to remove the entry when button is pressed
        btn.setOnAction(actionEvent1 -> {
            tmList.remove(tm);
            //--update the total and table
            tot -= tm.getAmount();
            tblOrder.refresh();
            lblTotal.setText(String.format("%.2f", tot));
        });

        boolean isExist = false;

        //--for-each for the observableArrayList to avoid adding a new entry if the entry already exists
        //within the table view
        for (OrderTm order : tmList) {
            //--check the code of entry that is equal to the captured code
            if (order.getCode().equals(tm.getCode())) {
                order.setQty(order.getQty() + tm.getQty());
                order.setAmount(order.getAmount() + tm.getAmount());
                isExist = true; //to avoid adding freshly
                tot += tm.getAmount();
            }
        }

        //--if an entry is unavailable, add the entry freshly into observableArrayList
        if (!isExist) {
            tmList.add(tm);
            tot += tm.getAmount();
        }

        //add the observableArrayList into the table view
        TreeItem<OrderTm> treeObject = new RecursiveTreeItem<OrderTm>(tmList, RecursiveTreeObject::getChildren);
        tblOrder.setRoot(treeObject);
        tblOrder.setShowRoot(false);

        //--update the total amount
        lblTotal.setText(String.format("%.2f", tot));
        txtQty.setText("");
    }

    public void placeOrderButtonOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        List<OrderDetailsDto> list = new ArrayList<>();

        //--add the items inside the observableArrayList into the
        for (OrderTm tm : tmList) {
            list.add(new OrderDetailsDto(
                    lblOrderId.getText(),
                    tm.getCode(),
                    tm.getQty(),
                    tm.getAmount() / tm.getQty()
            ));
        }

        //-- To confirm if the entry was added into the dabatabase
        boolean isSaved = false;
        try {
            //test






            //--capture the return from the save method of the implementation
            isSaved = orderBo.saveOrder(new OrderDto(
                    lblOrderId.getText(),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd")),
                    cmbCustId.getValue().toString(),
                    list
            ));

            if (isSaved) {
                new Alert(Alert.AlertType.INFORMATION, "Order Saved !!").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Something went wrong !!").show();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

}
