package com.example.project.frontend;

import com.example.project.backend.OrderService;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class OrderProductScene {
    private VBox layout;
    private TextArea outputTextArea;

    @SuppressWarnings("exports")
    public OrderProductScene(Stage primaryStage) {
        layout = new VBox(10);
        outputTextArea = new TextArea(); // Create a new TextArea
        outputTextArea.setEditable(false); // Ensure output area is not editable

        TextField txtCustomerId = new TextField();
        txtCustomerId.setPromptText("Enter Customer ID");

        TextField txtProductId = new TextField();
        txtProductId.setPromptText("Enter Product ID");

        TextField txtOrderAmount = new TextField();
        txtOrderAmount.setPromptText("Enter Order Amount");

        Button btnOrderProduct = new Button("Order Product");
        btnOrderProduct.setOnAction(e -> {
            String customerId = txtCustomerId.getText();
            String productId = txtProductId.getText();
            double orderAmount = Double.parseDouble(txtOrderAmount.getText());

            OrderService.handleProductOrder(customerId, productId, orderAmount, outputTextArea);
        });

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> MainApp.showMainMenuScene());

        layout.getChildren().addAll(txtCustomerId, txtProductId, txtOrderAmount, btnOrderProduct, btnBack, outputTextArea);
    }

    @SuppressWarnings("exports")
    public VBox getLayout() {
        layout.setAlignment(Pos.CENTER);
        return layout;
    }
}
