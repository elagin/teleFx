package ru.crew4dev.telemetry.data;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileModel {

    private List<String> metadata;

    private SimpleIntegerProperty iso;
    private SimpleStringProperty name;
    private SimpleStringProperty resolution;
    private SimpleStringProperty pos;
    private SimpleStringProperty fnumber;
    private SimpleStringProperty exposure;
    private SimpleStringProperty altitude;
    private SimpleLongProperty size;
    private SimpleLongProperty creationTime;
    private SimpleDoubleProperty frameRate;
    private SimpleStringProperty compressionType;
    private SimpleStringProperty duration;

    public FileModel(String name, Long size) {
        this.iso = new SimpleIntegerProperty(0);
        this.name = new SimpleStringProperty(name);
        this.resolution = new SimpleStringProperty("");
        this.pos = new SimpleStringProperty("");
        this.fnumber = new SimpleStringProperty("");
        this.exposure = new SimpleStringProperty("");
        this.altitude = new SimpleStringProperty("");
        this.compressionType = new SimpleStringProperty("");
        this.duration = new SimpleStringProperty("");
        this.size = new SimpleLongProperty(size);
        this.creationTime = new SimpleLongProperty();
        this.frameRate = new SimpleDoubleProperty(0);
        this.metadata = new ArrayList<>();
    }

    public String getCreationTime() {
        if (creationTime.get() > 0) {
            Date date = new Date(creationTime.get());
            SimpleDateFormat dt1 = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            return dt1.format(date);
        } else
            return "";
    }

    public void setCreationTime(long creationTime) {
        this.creationTime.set(creationTime);
    }

    public String getDuration() {
        return duration.get();
    }

    public void setDuration(String duration) {
        this.duration.set(duration);
    }

    public String getCompressionType() {
        return compressionType.get();
    }

    public void setCompressionType(String compressionType) {
        this.compressionType.set(compressionType);
    }

    public String getFrameRate() {
        return String.format(String.format("%2.2f", frameRate.get()));
    }

    public void setFrameRate(double frameRate) {
        this.frameRate.set(frameRate);
    }

    public String getAltitude() {
        return altitude.get();
    }

    public void setAltitude(String altitude) {
        this.altitude.set(altitude);
    }

    public List<String> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<String> metadata) {
        this.metadata = metadata;
    }

    public void addMetadata(List<String> metadata) {
        System.out.println("Old: " + this.metadata.size() + " New: " + metadata.size());
        List<String> toRemove = new ArrayList<>();

        for (String itemOld : this.metadata) {
            for (String itemNew : metadata) {
                if (itemNew.equals(itemOld))
                    toRemove.add(itemNew);
            }
        }

        metadata.removeAll(toRemove);
        if (!metadata.isEmpty()) {
            System.out.println("New now: " + metadata.size());
            this.metadata.addAll(metadata);
            System.out.println("Old now: " + this.metadata.size());
        }
    }


    public String getExposure() {
        return exposure.get();
    }

    public void setExposure(String exposure) {
        this.exposure.set(exposure);
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

    public String getFnumber() {
        return fnumber.get();
    }

    public void setFnumber(String fnumber) {
        this.fnumber.set(fnumber);
    }

    public Long getSize() {
        return size.get();
    }

    public void setSize(Long size) {
        this.size.set(size);
    }
}