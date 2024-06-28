package com.example.project.frontend;

import com.example.project.backend.ProductService;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AddProductScene {
    private VBox layout;

    public AddProductScene(@SuppressWarnings("exports") Stage primaryStage) {
        layout = new VBox(10);

        TextField txtProductId = new TextField();
        txtProductId.setPromptText("Enter Product ID");

        TextField txtProductName = new TextField();
        txtProductName.setPromptText("Enter Product Name");

        TextField txtProductPrice = new TextField();
        txtProductPrice.setPromptText("Enter Product Price");

        Button btnAddProduct = new Button("Add Product");
        btnAddProduct.setOnAction(e -> {
            String productId = txtProductId.getText();
            String productName = txtProductName.getText();
            double productPrice = Double.parseDouble(txtProductPrice.getText());

            ProductService.addNewProduct(productId, productName, productPrice);
        });

        Button btnBack = new Button("Back");
        btnBack.setOnAction(e -> MainApp.showMainMenuScene());

        layout.getChildren().addAll(txtProductId, txtProductName, txtProductPrice, btnAddProduct, btnBack);
    }

    @SuppressWarnings("exports")
    public VBox getLayout() {
        return layout;
    }
}
