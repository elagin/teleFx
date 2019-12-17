package ru.crew4dev.telemetry.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
    private TableView<FileModel> tbData;

    @FXML
    public TableColumn<FileModel, Long> iso;

    @FXML
    public TableColumn<FileModel, String> name;

    @FXML
    public TableColumn<FileModel, String> resolution;

    @FXML
    public TableColumn<FileModel, String> pos;

    @FXML
    public TableColumn<FileModel, String> fnumber;

    @FXML
    public TableColumn<FileModel, Long> size;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //make sure the property value factory should be exactly same as the e.g getIso from your model class
        iso.setCellValueFactory(new PropertyValueFactory<>("iso"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        resolution.setCellValueFactory(new PropertyValueFactory<>("resolution"));
        pos.setCellValueFactory(new PropertyValueFactory<>("pos"));
        fnumber.setCellValueFactory(new PropertyValueFactory<>("fnumber"));
        size.setCellValueFactory(new PropertyValueFactory<>("size"));
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
                if (f.getName().matches(pattern)) {
                    result.add(f.getAbsolutePath());
                }
            }
        }
        return result;
    }

    private void load() {
        ObservableList<FileModel> data = tbData.getItems();
        List<String> files = search("c:/");
        for (String file : files) {
            data.add(work(file));
        }
    }

    @FXML
    public void onClickMethod() {
        System.out.println();
    }
}