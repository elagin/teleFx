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
import java.util.Arrays;

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
        //File file = new File("d:/VIDEO/fly/DCIM/100/IMG_20191212_010007_0008.JPG");
        //File file = new File("d:/VIDEO/fly/DCIM/100/VID_20191215_150210_0009.MP4");
        //File file = new File("d:/VIDEO/viofo/Movie/20191213211134_000008.MP4");
        //File file = new File("d:/VIDEO/viofo/Photo/20191214113615_000023.JPG");
        File file = new File(name);

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
                        //System.out.println(tag);
                        if (tag.getTagName().equals(HEIGHT)) {
                            String desc = tag.getDescription();
                            if (resolution.length() > 0)
                                resolution.append("x");
                            resolution.append(getValue(desc));
                            //result.setResolution(desc);
                        } else if (tag.getTagName().equals(WIDTH)) {
                            String desc = tag.getDescription();
                            if (resolution.length() > 0)
                                resolution.append("x");
                            resolution.append(getValue(desc));
                        } else {
                            System.out.println(tag.toString());
                        }
                    }
                    if (tag.getDirectoryName().equals(EXIF_SUBIFD)) {
                        if (tag.getTagName().equals(ISO)) {
                            result.setIso(Integer.valueOf(tag.getDescription()));
                            //System.out.println();
                        } else if (tag.getTagName().equals(F_NUMBER)) {
                            result.setFnumber(tag.getDescription());
                        } else if (tag.getTagName().equals(EXPOSURE)) {
                            result.setExposure(tag.getDescription());
                        }

                        //    [Exif SubIFD] Exposure Time - 1/200 sec
                        //GPS Longitude Ref
                        //[GPS] GPS Longitude - 37° 35' 21,88"
                        // if (tag.getTagName().equals(ISO)) { GPS Latitude Ref - tag.getDescription() N
                        // if (tag.getTagName().equals(ISO)) { GPS Latitude - 55° 45' 20,01"

                    }
                    if (tag.getDirectoryName().equals(GPS)) {
                        if (tag.getTagName().equals(LONGITUDE)) {
                            lon = tag.getDescription();
                            //System.out.println(tag.toString());
                            //private static final String LONGITUDE = "Longitude";
                            //private static final String LATITUDE = "Latitude";
                        } else if (tag.getTagName().equals(LATITUDE)) {
                            lat = tag.getDescription();
                        } else if (tag.getTagName().equals(ALTITUDE)) {
                            result.setAltitude(tag.getDescription());
                        }
                        //
                        //    //[GPS] GPS Altitude - 96,06 metres

                    } else {
                        System.out.println(tag.toString());
                    }
                }
                //result.setResolution(resolution.toString());
                //
                // Each Directory may also contain error messages
                //
//                for (String error : directory.getErrors()) {
//                    System.err.println("ERROR: " + error);
//                }
            }
            System.out.println(resolution.toString());
            if (!lat.isEmpty() && !lon.isEmpty())
                result.setPos(lat + "x" + lon);
            result.setResolution(resolution.toString());
            print(metadata, "Using ImageMetadataReader");
        } catch (
                ImageProcessingException | IOException e) {
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
            print(metadata, "Using JpegMetadataReader");
        } catch (
                JpegProcessingException | IOException e) {
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
            print(metadata, "Using JpegMetadataReader for Exif and IPTC only");
        } catch (JpegProcessingException | IOException e) {
            print(e);
        }
        return result;
    }

    /**
     * Write all extracted values to stdout.
     */
    private static void print(Metadata metadata, String method) {
        System.out.println();
        System.out.println("-------------------------------------------------");
        System.out.print(' ');
        System.out.print(method);
        System.out.println("-------------------------------------------------");
        System.out.println();
        //
        // A Metadata object contains multiple Directory objects
        //
        for (Directory directory : metadata.getDirectories()) {
            //
            // Each Directory stores values in Tag objects
            //
            for (Tag tag : directory.getTags()) {
                System.out.println(tag);
            }
            //
            // Each Directory may also contain error messages
            //
            for (String error : directory.getErrors()) {
                System.err.println("ERROR: " + error);
            }
        }
    }

    private static void print(Exception exception) {
        System.err.println("EXCEPTION: " + exception);
    }
}
