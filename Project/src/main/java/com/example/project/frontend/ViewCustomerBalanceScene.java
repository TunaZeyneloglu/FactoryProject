package com.example.project.frontend;

import com.example.project.backend.CustomerService;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ViewCustomerBalanceScene {
    private VBox layout;

    public ViewCustomerBalanceScene() {
        layout = new VBox(10);

        TextField txtCustomerId = new TextField();
        txtCustomerId.setPromptText("Müşteri ID'sini girin");

        TextArea txtBalance = new TextArea();
        txtBalance.setEditable(false);

        Button btnView = new Button("Bakiyeyi Görüntüle");
        btnView.setOnAction(e -> {
            String customerId = txtCustomerId.getText();
            CustomerService.printCustomerBalance(customerId, txtBalance);
        });

        Button btnBack = new Button("Geri");
        btnBack.setOnAction(e -> MainApp.showMainMenuScene());

        layout.getChildren().addAll(txtCustomerId, txtBalance, btnView, btnBack);
    }

    @SuppressWarnings("exports")
    public VBox getLayout() {
        layout.setAlignment(Pos.CENTER);
        return layout;
    }
}
