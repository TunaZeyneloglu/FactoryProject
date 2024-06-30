package com.example.project.frontend;

import com.example.project.backend.CustomerService;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class ViewAllBalancesScene {
    private VBox layout;

    public ViewAllBalancesScene() {
        layout = new VBox(10);

        TextArea txtBalances = new TextArea();
        txtBalances.setEditable(false);

        CustomerService.printAllBalances(txtBalances);

        Button btnBack = new Button("Geri");
        btnBack.setOnAction(e -> MainApp.showMainMenuScene());

        layout.getChildren().addAll(txtBalances, btnBack);
    }

    @SuppressWarnings("exports")
    public VBox getLayout() {
        layout.setAlignment(Pos.CENTER);
        return layout;
    }
}
