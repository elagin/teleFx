package ru.crew4dev.telemetry.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import ru.crew4dev.telemetry.data.FileModel;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import static ru.crew4dev.telemetry.Process.work;

public class MainController implements Initializable {

    private Preferences prefs;

    final String PREF_FOLDER_NAME = "folderName";

    @FXML
    private TextField folderName;

    @FXML
    private Button buttonBrowse;

    @FXML
    private ListView metaData;

    @FXML
    private ImageView imageView;

    @FXML
    private TableView<FileModel> tbData;

    @FXML
    public TableColumn<Object, Object> iso;

    @FXML
    public TableColumn<Object, Object> name;

    @FXML
    public TableColumn<Object, Object> resolution;

    @FXML
    public TableColumn<Object, Object> pos;

    @FXML
    public TableColumn<Object, Object> fnumber;

    @FXML
    public TableColumn<Object, Object> size;

    @FXML
    public TableColumn<Object, Object> exposure;

    @FXML
    public TableColumn<Object, Object> altitude;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //make sure the property value factory should be exactly same as the e.g getIso from your model class
        iso.setCellValueFactory(new PropertyValueFactory<>("iso"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        resolution.setCellValueFactory(new PropertyValueFactory<>("resolution"));
        pos.setCellValueFactory(new PropertyValueFactory<>("pos"));
        fnumber.setCellValueFactory(new PropertyValueFactory<>("fnumber"));
        size.setCellValueFactory(new PropertyValueFactory<>("size"));
        exposure.setCellValueFactory(new PropertyValueFactory<>("exposure"));
        altitude.setCellValueFactory(new PropertyValueFactory<>("altitude"));
        prefs = Preferences.userNodeForPackage(ru.crew4dev.telemetry.App.class);
        String folder = prefs.get(PREF_FOLDER_NAME, "");
        folderName.setText(folder);

        tbData.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    int index = tbData.getSelectionModel().getSelectedIndex();
                    FileModel item = tbData.getItems().get(index);
                    ObservableList<String> oListMeta = FXCollections.observableArrayList(item.getMetadata());
                    metaData.setItems(oListMeta);
                    File file = new File(prefs.get(PREF_FOLDER_NAME, "") + "/" + item.getName());
                    Image image = new Image(file.toURI().toString());
                    double factor = image.getWidth() / image.getHeight();
                    imageView.setPreserveRatio(true);
                    imageView.setSmooth(true);
                    imageView.setCache(true);
                    imageView.setFitWidth(300);
                    imageView.setImage(image);
                }
            }
        });

        load();
    }

    public List<String> search(String folderName) {
        List<String> result = new ArrayList<>();
        if (!folderName.isEmpty()) {
            final String pattern = ".*\\.jpg";
            final File folder = new File(folderName);
            for (final File f : folder.listFiles()) {
                if (f.isFile()) {
                    if (f.getName().toLowerCase().matches(pattern)) {
                        result.add(f.getAbsolutePath());
                    }
                }
            }
        }
        return result;
    }

    private void load() {
        ObservableList<FileModel> data = tbData.getItems();
        data.removeAll(data);

        // Set the value of the preference
        prefs.put(PREF_FOLDER_NAME, folderName.getText());
        List<String> files = search(folderName.getText());
        for (String file : files) {
            data.add(work(file));
        }
    }

    @FXML
    public void onClickMethod() {
        load();
    }

    @FXML
    public void onClickBrowse() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("JavaFX Projects");
        String folder = prefs.get(PREF_FOLDER_NAME, "");
        if (!folder.isEmpty()) {
            chooser.setInitialDirectory(new File(folder));
        }
        File selectedDirectory = chooser.showDialog(buttonBrowse.getScene().getWindow());
        if (selectedDirectory != null) {
            prefs.put(PREF_FOLDER_NAME, selectedDirectory.getAbsolutePath());
            folderName.setText(selectedDirectory.getAbsolutePath());
        }
    }
}