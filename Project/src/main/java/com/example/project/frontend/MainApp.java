package com.example.project.frontend;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(@SuppressWarnings("exports") Stage primaryStage) {
        MainApp.primaryStage = primaryStage;
        primaryStage.setTitle("Foundry Factory Customer Balance Manager");
        showMainMenuScene();
        primaryStage.show();
    }

    public static void showMainMenuScene() {
        MainMenuScene mainMenu = new MainMenuScene(primaryStage);
        Scene scene = mainMenu.getScene(); // Use getScene() directly to get the Scene object
        primaryStage.setScene(scene);
    }

    public static void showViewAllBalancesScene() {
        ViewAllBalancesScene viewAllBalancesScene = new ViewAllBalancesScene();
        Scene scene = new Scene(viewAllBalancesScene.getLayout(), 600, 400); // Adjust scene dimensions as needed
        primaryStage.setScene(scene);
    }

    public static void showViewCustomerBalanceScene() {
        ViewCustomerBalanceScene viewCustomerBalanceScene = new ViewCustomerBalanceScene();
        Scene scene = new Scene(viewCustomerBalanceScene.getLayout(), 600, 400); // Adjust scene dimensions as needed
        primaryStage.setScene(scene);
    }

    public static void showAddCustomerScene() {
        AddCustomerScene addCustomerScene = new AddCustomerScene();
        Scene scene = new Scene(addCustomerScene.getLayout(), 600, 400); // Adjust scene dimensions as needed
        primaryStage.setScene(scene);
    }
    
    public static void showOrderProductScene() {
        OrderProductScene orderProductScene = new OrderProductScene(primaryStage);
        Scene scene = new Scene(orderProductScene.getLayout(), 600, 400); // Adjust scene dimensions as needed
        primaryStage.setScene(scene);
    }
    
    public static void showRecordProductDeliveryScene() {
        RecordProductDeliveryScene recordProductDeliveryScene = new RecordProductDeliveryScene(primaryStage);
        Scene scene = new Scene(recordProductDeliveryScene.getLayout(), 600, 400); // Adjust scene dimensions as needed
        primaryStage.setScene(scene);
    }
    
    public static void showAddProductScene() {
        AddProductScene addProductScene = new AddProductScene(primaryStage);
        Scene scene = new Scene(addProductScene.getLayout(), 600, 400); // Adjust scene dimensions as needed
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
