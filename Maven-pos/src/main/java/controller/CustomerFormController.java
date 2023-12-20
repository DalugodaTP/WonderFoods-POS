package controller;


import db.DBConnection;
import dto.CustomerDto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import dto.tm.CustomerTm;
import model.CustomerModel;
import model.impl.CustomerModelImpl;

import java.io.IOException;
import java.sql.*;
import java.util.List;


public class CustomerFormController {

    public Button btnSave;
    public Button btnReload;
    public Button btnUpdate;
    public AnchorPane customerPane;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtAddress;

    @FXML
    private TextField txtSalary;

    @FXML
    private TableView<CustomerTm> tblCustomer;

    @FXML
    private TableColumn colId;

    @FXML
    private TableColumn colName;

    @FXML
    private TableColumn colAddress;

    @FXML
    private TableColumn colSalary;

    @FXML
    private TableColumn colOPtion;

    private CustomerModel customerModel = new CustomerModelImpl();//loose coupling

    public void initialize() { //similar to constructor when the form loads
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        colOPtion.setCellValueFactory(new PropertyValueFactory<>("btn"));
        loadCustomerTable();

        tblCustomer.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            setData(newValue);
        });

    }

    private void setData(CustomerTm newValue) {
        if (newValue != null) {
            tblCustomer.refresh();
            txtId.setEditable(false); //lock the Id so that it cannot be changed
            txtId.setText((newValue.getId()));
            txtName.setText(newValue.getName());
            txtAddress.setText(newValue.getAddress());
            txtSalary.setText(String.valueOf(newValue.getSalary()));
        }
    }

    private void loadCustomerTable() {
        ObservableList<CustomerTm> tmList = FXCollections.observableArrayList();
        try {
            //get a list of customers from the Model
            List<CustomerDto> dtoList = customerModel.allCustomers();

            for (CustomerDto dto : dtoList) { // for each assign values
                Button btn = new Button("Delete");
                CustomerTm c = new CustomerTm(
                        dto.getId(),
                        dto.getName(),
                        dto.getAddress(),
                        dto.getSalary(),
                        btn
                );
                btn.setOnAction(actionEvent -> { //lamda expression
                    try {
                        deleteCustomer(c.getId());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
                tmList.add(c); //arraylist to store all items
            }
            tblCustomer.setItems(tmList);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void deleteCustomer(String id) throws SQLException, ClassNotFoundException {

        //-- pass the id to the model method
        boolean isDeleted = customerModel.deleteCustomer(id);
        //--- If deleted display confirmations
        if (isDeleted) {
            new Alert(Alert.AlertType.INFORMATION, id+" is Deleted!").show();
            loadCustomerTable();
        } else {
            new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
        }
    }

    public void saveButtonOnAction(javafx.event.ActionEvent actionEvent) {

        try {
            boolean isSaved = customerModel.saveCustomer(new CustomerDto(
                    txtId.getText(),
                    txtName.getText(),
                    txtAddress.getText(),
                    Double.parseDouble(txtSalary.getText())));
            if (isSaved) {
                new Alert(Alert.AlertType.INFORMATION, txtName.getText()+" is Saved!").show();
                loadCustomerTable();
                clearFields();
            }

        } catch (NullPointerException ex) {
            new Alert(Alert.AlertType.ERROR, "Duplicate Entry").show();
        } catch (SQLIntegrityConstraintViolationException ex) {
            new Alert(Alert.AlertType.ERROR, "Duplicate Entry").show();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        clearFields();
    }

    private void clearFields() {
        tblCustomer.refresh();
        txtName.clear();
        txtAddress.clear();
        txtId.clear();
        txtSalary.clear();
    }

    public void relaodButtonOnAction(javafx.event.ActionEvent actionEvent) {
        tblCustomer.refresh(); //reduces the time to load (use a static variable to store data)
    }

    public void updateButtonOnAction(javafx.event.ActionEvent actionEvent) {

        try {
            boolean isUpdated = customerModel.updateCustomer(new CustomerDto(
                    txtId.getText(),
                    txtName.getText(),
                    txtAddress.getText(),
                    Double.parseDouble(txtSalary.getText())));
            if (isUpdated) {
                new Alert(Alert.AlertType.INFORMATION, "CustomerDto Updated!").show();
            }

        } catch (NullPointerException ex) {
            new Alert(Alert.AlertType.ERROR, "Duplicate Entry").show();
        } catch (SQLIntegrityConstraintViolationException ex) {
            new Alert(Alert.AlertType.ERROR, "Duplicate Entry").show();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        loadCustomerTable();
        clearFields();
    }

    public void backButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) customerPane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/DashboardForm.fxml"))));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

