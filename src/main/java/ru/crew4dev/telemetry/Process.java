package ru.crew4dev.telemetry;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.iptc.IptcReader;
import ru.crew4dev.telemetry.data.FileModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Process {

    private static final String DIR_JPEG = "JPEG";
    private static final String HEIGHT = "Image Height";
    private static final String WIDTH = "Image Width";

    private static final String EXIF_SUBIFD = "Exif SubIFD";
    private static final String ISO = "ISO Speed Ratings";
    private static final String F_NUMBER = "F-Number";
    private static final String EXPOSURE = "Exposure Time";

    private static final String GPS = "GPS";
    private static final String LONGITUDE = "GPS Longitude";
    private static final String LATITUDE = "GPS Latitude";
    private static final String ALTITUDE = "GPS Altitude";

    public static String getValue(String data) {
        int pos = data.indexOf(" ");
        return data.substring(0, pos);
    }

    public static FileModel work(String name) {
        File file = new File(name);
        System.out.println(name);
        // There are multiple ways to get a Metadata object for a file

        //
        // SCENARIO 1: UNKNOWN FILE TYPE
        //
        // This is the most generic approach.  It will transparently determine the file type and invoke the appropriate
        // readers.  In most cases, this is the most appropriate usage.  This will handle JPEG, TIFF, GIF, BMP and RAW
        // (CRW/CR2/NEF/RW2/ORF) files and extract whatever metadata is available and understood.
        //

        FileModel result = new FileModel(file.getName(), file.length());
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            StringBuilder resolution = new StringBuilder();
            String lat = "";
            String lon = "";
            for (Directory directory : metadata.getDirectories()) {
                //
                // Each Directory stores values in Tag objects
                //
                for (Tag tag : directory.getTags()) {
                    if (tag.getDirectoryName().equals(DIR_JPEG)) {
                        if (tag.getTagName().equals(HEIGHT)) {
                            String desc = tag.getDescription();
                            if (resolution.length() > 0)
                                resolution.append("x");
                            resolution.append(getValue(desc));
                        } else if (tag.getTagName().equals(WIDTH)) {
                            String desc = tag.getDescription();
                            if (resolution.length() > 0)
                                resolution.append("x");
                            resolution.append(getValue(desc));
                        }
                    }
                    if (tag.getDirectoryName().equals(EXIF_SUBIFD)) {
                        if (tag.getTagName().equals(ISO)) {
                            result.setIso(Integer.valueOf(tag.getDescription()));
                        } else if (tag.getTagName().equals(F_NUMBER)) {
                            result.setFnumber(tag.getDescription());
                        } else if (tag.getTagName().equals(EXPOSURE)) {
                            result.setExposure(tag.getDescription());
                        }
                    }
                    if (tag.getDirectoryName().equals(GPS)) {
                        if (tag.getTagName().equals(LONGITUDE)) {
                            lon = tag.getDescription();
                        } else if (tag.getTagName().equals(LATITUDE)) {
                            lat = tag.getDescription();
                        } else if (tag.getTagName().equals(ALTITUDE)) {
                            result.setAltitude(tag.getDescription());
                        }
                    }
                }
            }
            if (!lat.isEmpty() && !lon.isEmpty())
                result.setPos(lat + "x" + lon);
            result.setResolution(resolution.toString());
            result.setMetadata(print(metadata, "Using ImageMetadataReader"));
        } catch (ImageProcessingException | IOException e) {
            print(e);
        }

        //
        // SCENARIO 2: SPECIFIC FILE TYPE
        //
        // If you know the file to be a JPEG, you may invoke the JpegMetadataReader, rather than the generic reader
        // used in approach 1.  Similarly, if you knew the file to be a TIFF/RAW image you might use TiffMetadataReader,
        // PngMetadataReader for PNG files, BmpMetadataReader for BMP files, or GifMetadataReader for GIF files.
        //
        // Using the specific reader offers a very, very slight performance improvement.
        //

        try {
            Metadata metadata = JpegMetadataReader.readMetadata(file);
            result.addMetadata(print(metadata, "Using JpegMetadataReader"));
        } catch (JpegProcessingException | IOException e) {
            print(e);
        }

        //
        // APPROACH 3: SPECIFIC METADATA TYPE
        //
        // If you only wish to read a subset of the supported metadata types, you can do this by
        // passing the set of readers to use.
        //
        // This currently only applies to JPEG file processing.
        //

        try {
            // We are only interested in handling
            Iterable<JpegSegmentMetadataReader> readers = Arrays.asList(new ExifReader(), new IptcReader());
            Metadata metadata = JpegMetadataReader.readMetadata(file, readers);
            result.addMetadata(print(metadata, "Using JpegMetadataReader for Exif and IPTC only"));
        } catch (JpegProcessingException | IOException e) {
            print(e);
        }
        return result;
    }

    /**
     * Write all extracted values to stdout.
     */
    private static List<String> print(Metadata metadata, String method) {
        //
        // A Metadata object contains multiple Directory objects
        //
        List<String> result = new ArrayList<>();
        for (Directory directory : metadata.getDirectories()) {
            //
            // Each Directory stores values in Tag objects
            //
            for (Tag tag : directory.getTags()) {
                result.add(tag.toString());
            }
            //
            // Each Directory may also contain error messages
            //
            for (String error : directory.getErrors()) {
                System.err.println("ERROR: " + error);
            }
        }
        return result;
    }

    private static void print(Exception exception) {
        System.err.println("EXCEPTION: " + exception);
    }
}
