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

    final String PREF_FOLDER_NAME = "folderName";

    private Preferences prefs;
    boolean isImages = false;

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
    public TableColumn<Object, Object> name;

    @FXML
    public TableColumn<Object, Object> size;

    @FXML
    public TableColumn<Object, Object> resolution;

    //Images
    public TableColumn iso;
    public TableColumn pos;
    public TableColumn fnumber;
    public TableColumn exposure;
    public TableColumn altitude;

    //Movies
    public TableColumn frameRate;
    public TableColumn compressionType;
    public TableColumn duration;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (isImages) {
            iso = new TableColumn("ISO");
            pos = new TableColumn("pos");
            altitude = new TableColumn("altitude");
            exposure = new TableColumn("exposure");
            fnumber = new TableColumn("fnumber");

            //make sure the property value factory should be exactly same as the e.g getIso from your model class
            iso.setCellValueFactory(new PropertyValueFactory<>("iso"));
            pos.setCellValueFactory(new PropertyValueFactory<>("pos"));
            fnumber.setCellValueFactory(new PropertyValueFactory<>("fnumber"));
            exposure.setCellValueFactory(new PropertyValueFactory<>("exposure"));
            altitude.setCellValueFactory(new PropertyValueFactory<>("altitude"));
        } else {
            frameRate = new TableColumn("frameRate");
            frameRate.setCellValueFactory(new PropertyValueFactory<>("frameRate"));

            compressionType = new TableColumn("compressionType");
            compressionType.setCellValueFactory(new PropertyValueFactory<>("compressionType"));

            duration = new TableColumn("duration");
            duration.setCellValueFactory(new PropertyValueFactory<>("duration"));
        }

        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        resolution.setCellValueFactory(new PropertyValueFactory<>("resolution"));
        size.setCellValueFactory(new PropertyValueFactory<>("size"));
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

        if (isImages)
            tbData.getColumns().addAll(iso, pos, altitude, exposure, fnumber);
        else
            tbData.getColumns().addAll(frameRate, compressionType, duration);

        load();
    }

    public List<String> search(String folderName) {
        List<String> result = new ArrayList<>();
        if (!folderName.isEmpty()) {
            String pattern = "";
            if (isImages)
                pattern = ".*\\.jpg";
            else
                pattern = ".*\\.mp4";
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