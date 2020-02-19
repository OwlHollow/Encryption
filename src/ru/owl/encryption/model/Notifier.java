package ru.owl.encryption.model;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

public class Notifier {
    
    public static void callExceptionDialog(String header, String content){
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Внимание!");
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        alert.showAndWait();
    }
    
    public static void callWarningDialog(String header, String content){
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Внимание!");
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        alert.showAndWait();
    }
    
    public static void callInformationDialog(String content){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(content);
        
        alert.showAndWait();
    }
    
    public static boolean callConfirmationDialog(String header, String content){
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение действия");
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }
    
    public static String callInputDialog(String header, String content){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Ввод данных");
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        
        Optional<String> result = dialog.showAndWait();
        if(result.isPresent()){
            return result.get();
        } else {
            return null;
        }
    }
}
