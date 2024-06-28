package com.example.project.frontend;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainMenuScene {
    @SuppressWarnings("unused")
    private Stage stage;
    private VBox layout;

    @SuppressWarnings("exports")
    public MainMenuScene(Stage stage) {
        this.stage = stage;
        initialize();
    }

    private void initialize() {
        layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Button addProductButton = new Button("Add Product");
        addProductButton.setOnAction(e -> MainApp.showAddProductScene());
        
        Button addCustomerButton = new Button("Add Customer");
        addCustomerButton.setOnAction(e -> MainApp.showAddCustomerScene());

        Button viewAllBalancesButton = new Button("View All Balances");
        viewAllBalancesButton.setOnAction(e -> MainApp.showViewAllBalancesScene());

        Button viewCustomerBalanceButton = new Button("View Customer Balance");
        viewCustomerBalanceButton.setOnAction(e -> MainApp.showViewCustomerBalanceScene());

        Button orderProductButton = new Button("Order Product");
        orderProductButton.setOnAction(e -> MainApp.showOrderProductScene());

        Button recordProductDeliveryButton = new Button("Record Product Delivery");
        recordProductDeliveryButton.setOnAction(e -> MainApp.showRecordProductDeliveryScene());

        layout.getChildren().addAll(
            addProductButton,
            addCustomerButton,
            viewCustomerBalanceButton,
            viewAllBalancesButton,
            orderProductButton,
            recordProductDeliveryButton
        );
    }

    @SuppressWarnings("exports")
    public Scene getScene() {
        layout.setAlignment(Pos.CENTER);
        return new Scene(layout, 600, 400);
    }
}
