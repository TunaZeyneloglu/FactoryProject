package com.example.project.frontend;

import com.example.project.backend.OrderService;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RecordProductDeliveryScene {
    private VBox layout;
    private TextField txtCustomerId;
    private TextField txtProductId;
    private TextField txtDeliveredAmount;
    private TextArea outputTextArea;

    public RecordProductDeliveryScene(@SuppressWarnings("exports") Stage primaryStage) {
        layout = new VBox(10);
        outputTextArea = new TextArea(); // Create a new TextArea
        outputTextArea.setEditable(false); // Ensure output area is not editable

        txtCustomerId = new TextField();
        txtCustomerId.setPromptText("Customer ID");

        txtProductId = new TextField();
        txtProductId.setPromptText("Product ID");

        txtDeliveredAmount = new TextField();
        txtDeliveredAmount.setPromptText("Delivered Amount");

        Button btnRecordDelivery = new Button("Record Product Delivery");
        btnRecordDelivery.setOnAction(e -> {
            String customerId = txtCustomerId.getText();
            String productId = txtProductId.getText();
            double deliveredAmount = Double.parseDouble(txtDeliveredAmount.getText());

            OrderService.recordProductDelivery(customerId, productId, deliveredAmount, outputTextArea);
        });

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> MainApp.showMainMenuScene());

        layout.getChildren().addAll(txtCustomerId, txtProductId, txtDeliveredAmount, btnRecordDelivery, btnBack, outputTextArea);
    }

    @SuppressWarnings("exports")
    public VBox getLayout() {
        return layout;
    }
}
