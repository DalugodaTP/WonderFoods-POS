package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import db.DBConnection;
import dto.ItemDto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import dto.tm.ItemTm;
import model.ItemModel;
import model.impl.ItemModelImpl;

import java.io.IOException;
import java.sql.*;
import java.util.List;

public class ItemListFormController {
    public BorderPane borderItemPane;
    public JFXButton btnSave;
    public JFXButton btnUpdate;
    public JFXTreeTableView<ItemTm> itemTableView;
    @FXML
    private JFXTextField txtCode;

    @FXML
    private JFXTextField txtDesc;

    @FXML
    private JFXTextField txtUnitPrice;

    @FXML
    private JFXTextField txtQty;

    @FXML
    private JFXTextField txtSearch;

    @FXML
    private TreeTableColumn colCode;

    @FXML
    private TreeTableColumn colDesc;

    @FXML
    private TreeTableColumn colUnitPrice;

    @FXML
    private TreeTableColumn colQty;

    @FXML
    private TreeTableColumn colOption;

    private ItemModel itemModel = new ItemModelImpl();//loose coupling

    public void initialize() throws SQLException, ClassNotFoundException {
        //------Declare columns and mapping the ItemTm with the columns
        colCode.setCellValueFactory(new TreeItemPropertyValueFactory<>("code"));
        colDesc.setCellValueFactory(new TreeItemPropertyValueFactory<>("desc"));
        colUnitPrice.setCellValueFactory(new TreeItemPropertyValueFactory<>("unitPrice"));
        colQty.setCellValueFactory(new TreeItemPropertyValueFactory<>("qty"));
        colOption.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));

        //--Initialize table loading
        loadItemTable();

        //------Adding selection event to the table
        itemTableView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            setData(newValue);
        });

        //---Search Table
        txtSearch.textProperty().addListener((observableValue, oldValue, newValue) -> itemTableView.setPredicate(itemTmTreeItem -> {
            String code = itemTmTreeItem.getValue().getCode().toLowerCase();
            String desc = itemTmTreeItem.getValue().getDesc().toLowerCase();

            return code.contains(newValue.toLowerCase()) || desc.contains(newValue.toLowerCase());
        }));

    }

    private void setData(TreeItem<ItemTm> newValue) { // set data into fields
        if (newValue != null) {
            //tblItem.refresh();txtCode.setEditable(false); //lock the code so that it cannot be changed
            txtCode.setText((newValue.getValue().getCode()));
            txtDesc.setText(newValue.getValue().getDesc());
            txtUnitPrice.setText(String.valueOf(newValue.getValue().getUnitPrice()));
            txtQty.setText(String.valueOf(newValue.getValue().getQty()));
        }
    }

    public void saveButtonOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        ItemDto dto = new ItemDto(
                txtCode.getText(),
                txtDesc.getText(),
                Double.parseDouble(txtUnitPrice.getText()),
                Integer.parseInt(txtQty.getText())
        );
        boolean isSaved = itemModel.saveItem(dto);
        try {
            if (isSaved) {
                new Alert(Alert.AlertType.INFORMATION, "Item Saved!").show();
                loadItemTable();
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


    private void loadItemTable() throws SQLException, ClassNotFoundException {
        ObservableList<ItemTm> tmList = FXCollections.observableArrayList();

        try {
            //-- get the list of items from the Model
            List<ItemDto> dtoList = itemModel.allItems();

            for (ItemDto dto : dtoList) { // add a button to each item
                Button btn = new Button("Delete");
                ItemTm c = new ItemTm(
                        dto.getCode(),
                        dto.getDesc(),
                        dto.getUnitPrice(),
                        dto.getQty(),
                        btn
                );
                btn.setOnAction(actionEvent -> { //lamda expression
                    try {
                        deleteItem(c.getCode());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
                tmList.add(c); //arraylist to store all items
            }
            TreeItem<ItemTm> treeItem = new RecursiveTreeItem<>(tmList, RecursiveTreeObject::getChildren);
            itemTableView.setRoot(treeItem);
            itemTableView.setShowRoot(false);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }


    private void deleteItem(String code) throws SQLException, ClassNotFoundException {

        boolean isDelete = itemModel.deleteItem(code);
        try {
            if (isDelete) {
                loadItemTable();
                new Alert(Alert.AlertType.INFORMATION, " " + code + " was deleted!").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Something went wrong!").show();
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        txtCode.clear();
        txtDesc.clear();
        txtUnitPrice.clear();
        txtQty.clear();
    }

    public void updateButtonOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        ItemDto c = new ItemDto(
                txtCode.getText(),
                txtDesc.getText(),
                Double.parseDouble(txtUnitPrice.getText()),
                Integer.parseInt(txtQty.getText())
        );

        try {
            boolean isUpdate = itemModel.updateItem(c);
            if (isUpdate) {
                new Alert(Alert.AlertType.INFORMATION, "Item Updated!").show();
            }
        } catch (SQLIntegrityConstraintViolationException ex) {
            new Alert(Alert.AlertType.ERROR, "Duplicate Entry").show();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        loadItemTable();
        clearFields();
    }


    public void backButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) borderItemPane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/DashboardForm.fxml"))));
            stage.setTitle("Dashboard");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
