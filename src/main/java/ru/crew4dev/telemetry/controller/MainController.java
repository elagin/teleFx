package ru.crew4dev.telemetry.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import ru.crew4dev.telemetry.data.FileModel;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static ru.crew4dev.telemetry.Process.work;

public class MainController implements Initializable {

    @FXML
    private TextField folderName;

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
        folderName.setText("d:/VIDEO/fly/DCIM/100/");
        //add your data to the table here.
        //tbData.setItems(fileModels);
        load();
    }

    public List<String> search(String folderName) {
        final String pattern = ".*\\.jpg";
        final File folder = new File(folderName);
        List<String> result = new ArrayList<>();
        for (final File f : folder.listFiles()) {
            if (f.isFile()) {
                if (f.getName().toLowerCase().matches(pattern)) {
                    result.add(f.getAbsolutePath());
                }
            }
        }
        return result;
    }

    private void load() {
        ObservableList<FileModel> data = tbData.getItems();
        data.removeAll(data);
        List<String> files = search(folderName.getText());
        for (String file : files) {
            data.add(work(file));
        }
    }

    @FXML
    public void onClickMethod() {
        load();
    }
}