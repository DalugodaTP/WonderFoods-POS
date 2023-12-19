package controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DashboardFormController {

    public Button btnCustomer;
    public AnchorPane pane;
    public Button btnItemList;
    public Button btnPlaceOrder;
    public Label lblTime;
    public Label lblDate;
    public Button btnClose;

    private volatile boolean stop =false;

    public void initialize(){
        calculateTime();
        calculateDate();
    }

    private void calculateDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE,MMM dd, yyyy"); //yyyy/MM/dd
        String datenow= sdf.format(new Date());
        lblDate.setText(datenow);
    }

    private void calculateTime() {
        Thread thread = new Thread(()->{
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
            while(!stop){
                 try {
                    Thread.sleep(1000);
                 }catch(Exception e){
                     System.out.println(e);
                 }
                 final String timenow = sdf.format(new Date());
                 Platform.runLater(()->{
                     lblTime.setText(timenow);
                 });
            }
        });
        thread.start();
    }

    public void customerButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) pane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/CustomerForm.fxml"))));
            stage.setTitle("CustomerDto Form");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void itemButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) pane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/itemListForm.fxml"))));
            stage.setTitle("Item List Form");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void placeOrderButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) pane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/placeOrderForm.fxml"))));
            stage.setTitle("Place Order");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeButtonOnAction(ActionEvent actionEvent) {
        javafx.application.Platform.exit();
    }
}
