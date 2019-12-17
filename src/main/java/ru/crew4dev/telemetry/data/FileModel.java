package ru.crew4dev.telemetry.data;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class FileModel {

    private SimpleIntegerProperty iso;
    private SimpleStringProperty name;
    private SimpleStringProperty resolution;
    private SimpleStringProperty pos;
    private SimpleStringProperty fnumber;
    private SimpleLongProperty size;

    public FileModel(String name, Long size) {
        this.iso = new SimpleIntegerProperty(0);
        this.name = new SimpleStringProperty(name);
        this.resolution = new SimpleStringProperty("");
        this.pos = new SimpleStringProperty("");
        this.fnumber = new SimpleStringProperty("F");
        this.size = new SimpleLongProperty(size);
    }

    public int getIso() {
        return iso.get();
    }

    public void setIso(int iso) {
        this.iso = new SimpleIntegerProperty(iso);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name = new SimpleStringProperty(name);
    }

    public String getResolution() {
        return resolution.get();
    }

    public void setResolution(String resolution) {
        this.resolution = new SimpleStringProperty(resolution);
    }

    public String getPos() {
        return pos.get();
    }

    public void setPos(String pos) {
        this.pos.set(pos);
    }

    public String getfNumber() {
        return fnumber.get();
    }

    public void setfNumber(String fnumber) {
        this.fnumber.set(fnumber);
    }

    public Long getSize() {
        return size.get();
    }

    public void setSize(Long size) {
        this.size.set(size);
    }
}