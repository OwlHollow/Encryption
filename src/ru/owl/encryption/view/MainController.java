package ru.owl.encryption.view;

import javafx.scene.control.*;
import javafx.stage.FileChooser;
import ru.owl.encryption.Main;
import ru.owl.encryption.model.Crypter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ru.owl.encryption.model.IOWorker;
import ru.owl.encryption.model.Notifier;
import ru.owl.encryption.model.Crypter.Algorithm;
import ru.owl.encryption.model.Crypter.Mode;

public class MainController implements Initializable {
    private Algorithm algorithm = Algorithm.Parallel;

    private File fileToOpen, filetoSave;
    private FileChooser fc = new FileChooser();
    private final Crypter crypter = new Crypter();
	
    @FXML
    private TabPane root;
    @FXML
    private TextField decryptKeyText;
    @FXML
    private TextArea encryptedText;
    @FXML
    private TextArea decryptedText;
    @FXML
    private TextField encryptKeyText;
    @FXML
    private TextArea encryptSourceText;
    @FXML
    private TextArea encryptedSourceText;
    
    @FXML
    private Label encryptSourceStringSize;
    @FXML
    private Label sourceStringSize;
    @FXML
    private Label encryptedStringSize;
    @FXML
    private Label decryptedStringSize;

    @FXML
    private TextArea taSourceFile;
    @FXML
    private TextArea taModifiedFile;
    @FXML
    private TextField tfFileEncryptKey;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        encryptSourceText.textProperty().addListener((event) -> {
            sourceStringSize.setText("String size:" + encryptSourceText.getText().length());
        });
        
        encryptedText.textProperty().addListener((event) -> {
            encryptedStringSize.setText("String size: " + encryptedText.getText().length());
        });

        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
    }
    
    @FXML
    private void changeTabsSide(MouseEvent event){
        if(event.getButton().equals(MouseButton.PRIMARY)){
            if(event.getClickCount() == 2){
                if(root.sideProperty().get().equals(Side.TOP)){
                    root.sideProperty().setValue(Side.LEFT);
                } else {
                    root.sideProperty().setValue(Side.TOP);
                }
            }
        }
    }

    @FXML
    private void keyIconClick(MouseEvent event){
        if(event.getButton().equals(MouseButton.SECONDARY)){
            tfFileEncryptKey.setText(crypter.generateKey(16));
        }
    }

    @FXML
    private void decryptBtnClick(ActionEvent event) {
        decryptedText.clear();
        decryptedText.appendText(
            crypter.decryptText(
                new StringBuilder(encryptedText.getText()), 
                decryptKeyText.getText()));
        
        decryptedStringSize.setText("String size: " + decryptedText.getText().length());
    }

    @FXML
    private void encryptBtnClick(ActionEvent event) {
        encryptedSourceText.clear();
        encryptedSourceText.appendText(
            crypter.encryptText(
                new StringBuilder(encryptSourceText.getText()), 
                encryptKeyText.getText()));
            
        encryptSourceStringSize.setText("String size:" + encryptedSourceText.getText().length());
    }

    @FXML
    private void openFileClick(ActionEvent event) {
        fc.setTitle("Открыть файл");
        fileToOpen = fc.showOpenDialog(Main.getMainStage());
        if (fileToOpen != null) {
            taSourceFile.clear();
            taSourceFile.appendText("Name: " + System.lineSeparator() + '\t' + fileToOpen.getName());
            taSourceFile.appendText(System.lineSeparator());
            taSourceFile.appendText("Path: " + System.lineSeparator() + '\t' + fileToOpen.getPath());
        } else {
            Notifier.callWarningDialog("Отмена", "Не выбран файл для открытия");
        }
    }

    @FXML
    private void encryptFileClick(ActionEvent event) {
        doWork(algorithm, Mode.Encrypt);
    }

    @FXML
    private void decryptFileClick(ActionEvent event) {
        doWork(algorithm, Mode.Decrypt);
    }

    private void doWork(Algorithm alg, Mode mode){
        if(tfFileEncryptKey.getText().isEmpty()) {
            Notifier.callWarningDialog("Внимание", "Не введён ключ шифрования");
            return;
        }
        taModifiedFile.clear();
        if (fileToOpen != null){
            try {
                byte[] data = null;
                switch (alg){
                    case Parallel:
                            data = crypter.doWork(fileToOpen, tfFileEncryptKey.getText(), Algorithm.Parallel, mode);
                        break;
                    case Caesar:
                        try {
                            byte key = Byte.decode(tfFileEncryptKey.getText());
                        } catch (NumberFormatException ex){
                            Notifier.callWarningDialog(
                                    "Не верный формат ключа",
                                    "Для шифра цезаря необходимо ввести число от 0 до 255");
                            return;
                        }
                        data = crypter.doWork(fileToOpen, tfFileEncryptKey.getText(), Algorithm.Caesar, mode);
                        break;
                    case AES:
                        data = crypter.doWork(fileToOpen, tfFileEncryptKey.getText(), Algorithm.AES, mode);
                        break;
                    case RSA:
                        data = crypter.doWork(fileToOpen, tfFileEncryptKey.getText(), Algorithm.RSA, mode);
                        break;
                }

                filetoSave = fc.showSaveDialog(Main.getMainStage());
                if(filetoSave != null){
                    IOWorker.writeBytesToFile(data, filetoSave);
                    taModifiedFile.appendText("Name: " + System.lineSeparator() + '\t' + filetoSave.getName());
                    taModifiedFile.appendText(System.lineSeparator());
                    taModifiedFile.appendText("Path: " + System.lineSeparator() + '\t' + filetoSave.getPath());
                    Notifier.callInformationDialog("Файл успешно сохранён");
                } else {
                    Notifier.callWarningDialog("Отмена", "Не выбран файл для сохранения");
                }
            } catch (IOException e) {
                Notifier.callExceptionDialog("Ошибка", e.getMessage());
                Logger.getLogger(MainController.class.getName())
                        .log(Level.SEVERE, null, e);
            }
        } else {
            Notifier.callWarningDialog("Внимание", "Не указан файл для шифрования");
        }
    }

    @FXML
    private void radioButtonAction(ActionEvent event){
        String alg = ((RadioButton)event.getSource()).getAccessibleText();
        switch(alg){
            case "Caesar":
                algorithm = Algorithm.Caesar;
                break;
            case "Parallel":
                algorithm = Algorithm.Parallel;
                break;
            case "AES":
                algorithm = Algorithm.AES;
                break;
            case "RSA":
                algorithm = Algorithm.RSA;
                break;
        }
    }
}