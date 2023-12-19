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

import java.io.IOException;
import java.sql.*;


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
        if (newValue !=null){
            tblCustomer.refresh();txtId.setEditable(false); //lock the Id so that it cannot be changed
            txtId.setText((newValue.getId()));
            txtName.setText(newValue.getName());
            txtAddress.setText(newValue.getAddress());
            txtSalary.setText(String.valueOf(newValue.getSalary()));
        }
    }

    private void loadCustomerTable() {
        ObservableList<CustomerTm> tmList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM Customer";

        try {
            Statement stm = DBConnection.getInstance().getConnection().createStatement();
            ResultSet result = stm.executeQuery(sql); // just reading the data as a set

            while (result.next()) { //change the row pointer
                Button btn = new Button("Delete");
                CustomerTm c = new CustomerTm(
                        result.getString(1),
                        result.getString(2),
                        result.getString(3),
                        result.getDouble(4),
                        btn
                );
                btn.setOnAction(actionEvent -> { //lamda expression
                    deleteCustomer(c.getId());
                });


                tmList.add(c); //arraylist to store all items
            }

            tblCustomer.setItems(tmList);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteCustomer(String id) {
        String sql = "DELETE from customer WHERE id='" + id + "'";

        try {
            Statement stm = DBConnection.getInstance().getConnection().createStatement();
            int result = stm.executeUpdate(sql); //executeUpdate changes the database
            if (result > 0) {
                new Alert(Alert.AlertType.INFORMATION, "CustomerDto Saved!").show();
                loadCustomerTable();
            } else {
                new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveButtonOnAction(javafx.event.ActionEvent actionEvent) {
        CustomerDto c = new CustomerDto(txtId.getText(),
                txtName.getText(),
                txtAddress.getText(),
                Double.parseDouble(txtSalary.getText())
        );
        String sql = "INSERT INTO customer VALUES('" + c.getId() + "','" + c.getName() + "','" + c.getAddress() + "'," + c.getSalary() + ")";

        try {
            Statement stm = DBConnection.getInstance().getConnection().createStatement();
            int result = stm.executeUpdate(sql); //executeUpdate changes the database
            if (result > 0) {
                new Alert(Alert.AlertType.INFORMATION, "CustomerDto Saved!").show();
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
        CustomerDto c = new CustomerDto(txtId.getText(),
                txtName.getText(),
                txtAddress.getText(),
                Double.parseDouble(txtSalary.getText())
        );
        String sql = "UPDATE customer SET name='" + c.getName() + "', address='" + c.getAddress() + "', salary=" + c.getSalary() + " WHERE id='" + c.getId() + "'";

        try {
            Statement stm = DBConnection.getInstance().getConnection().createStatement();
            int result = stm.executeUpdate(sql); //executeUpdate changes the database
            if (result > 0) {
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

