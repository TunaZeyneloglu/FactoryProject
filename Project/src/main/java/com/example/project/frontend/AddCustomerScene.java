package com.example.project.frontend;

import com.example.project.backend.CustomerService;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class AddCustomerScene {
    private VBox layout;

    public AddCustomerScene() {
        layout = new VBox(10);

        TextField txtCustomerId = new TextField();
        txtCustomerId.setPromptText("Yeni müşteri ID'si girin");

        TextField txtCustomerName = new TextField();
        txtCustomerName.setPromptText("Yeni müşteri ismi girin");

        TextField txtCustomerEmail = new TextField();
        txtCustomerEmail.setPromptText("Yeni müşteri email'i girin");

        TextField txtInitialBalance = new TextField();
        txtInitialBalance.setPromptText("Varsa başlangıç bakiyesi girin, (eğer yoksa 0 girin)");

        Button btnAdd = new Button("Müşteri Ekle");
        btnAdd.setOnAction(e -> {
            String customerId = txtCustomerId.getText();
            String customerName = txtCustomerName.getText();
            String customerEmail = txtCustomerEmail.getText();
            double initialBalance = Double.parseDouble(txtInitialBalance.getText());

            CustomerService.addNewCustomer(customerId, customerName, customerEmail, initialBalance);
        });

        Button btnBack = new Button("Geri");
        btnBack.setOnAction(e -> MainApp.showMainMenuScene());

        layout.getChildren().addAll(txtCustomerId, txtCustomerName, txtCustomerEmail, txtInitialBalance, btnAdd, btnBack);
    }

    @SuppressWarnings("exports")
    public VBox getLayout() {
        layout.setAlignment(Pos.CENTER);
        return layout;
    }
}
