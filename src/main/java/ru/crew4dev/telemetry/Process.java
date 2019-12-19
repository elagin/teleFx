package ru.crew4dev.telemetry;

import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.imaging.mp4.Mp4MetadataReader;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.iptc.IptcReader;
import com.drew.metadata.mp4.Mp4Directory;
import com.drew.metadata.mp4.media.Mp4VideoDirectory;
import ru.crew4dev.telemetry.data.FileModel;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Process {

    public static FileModel work(String name) {
        File file = new File(name);
        // There are multiple ways to get a Metadata object for a file

        //
        // SCENARIO 1: UNKNOWN FILE TYPE
        //
        // This is the most generic approach.  It will transparently determine the file type and invoke the appropriate
        // readers.  In most cases, this is the most appropriate usage.  This will handle JPEG, TIFF, GIF, BMP and RAW
        // (CRW/CR2/NEF/RW2/ORF) files and extract whatever metadata is available and understood.
        //ImageMetadataReader

        FileModel result = new FileModel(file.getName(), file.length());
        if (name.toLowerCase().endsWith(".jpg")) {
            //
            // SCENARIO 2: SPECIFIC FILE TYPE
            //
            // If you know the file to be a JPEG, you may invoke the JpegMetadataReader, rather than the generic reader
            // used in approach 1.  Similarly, if you knew the file to be a TIFF/RAW image you might use TiffMetadataReader,
            // PngMetadataReader for PNG files, BmpMetadataReader for BMP files, or GifMetadataReader for GIF files.
            //
            // Using the specific reader offers a very, very slight performance improvement.

            try {
                Metadata metadata = JpegMetadataReader.readMetadata(file);
                parseImageMetadata(metadata, result);
                //result.addMetadata(print(metadata, "Using JpegMetadataReader"));
            } catch (JpegProcessingException | IOException e) {
                print(e);
            }
        } else if (name.toLowerCase().endsWith(".mp4")) {
            try {
                Metadata metadata = Mp4MetadataReader.readMetadata(file);
                parseMovieMetadata(metadata, result);
                //result.addMetadata(print(metadata, "Using JpegMetadataReader"));
            } catch (ImageProcessingException | IOException e) {
                print(e);
            }
        } else {
/*
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            parseMetadata(metadata, result);
        } catch (ImageProcessingException | IOException e) {
            print(e);
        }
*/
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
        }
        return result;
    }

    private static Double getDiv(String value) {
        String[] divs = value.split("/");
        if (divs.length == 2) {
            return (double) Integer.parseInt(divs[0]) / Integer.parseInt(divs[1]);
        }
        return null;
    }

    private static void parseImageMetadata(Metadata metadata, FileModel result) {
        StringBuilder resolution = new StringBuilder();
        Directory directoryExifSub = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        if (directoryExifSub != null) {
            result.setIso(Integer.parseInt(directoryExifSub.getString(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT)));
            result.setFnumber(directoryExifSub.getString(ExifSubIFDDirectory.TAG_FNUMBER));

            String exposure = directoryExifSub.getString(ExifSubIFDDirectory.TAG_EXPOSURE_TIME);
            if (exposure != null) {
                String[] divs = exposure.split("/");
                if (divs.length == 2 && divs[0].length() > 1) {
                    Double dExp = (double) Integer.parseInt(divs[1]) / Integer.parseInt(divs[0]);
                    result.setExposure("1/" + Math.round(dExp));
                } else {
                    result.setExposure(exposure);
                }
            }

            resolution.append(directoryExifSub.getString(ExifSubIFDDirectory.TAG_EXIF_IMAGE_WIDTH));
            resolution.append("x");
            resolution.append(directoryExifSub.getString(ExifSubIFDDirectory.TAG_EXIF_IMAGE_HEIGHT));
            result.setResolution(resolution.toString());
        }

        GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
        if (gpsDirectory != null) {
            String altString = gpsDirectory.getString(GpsDirectory.TAG_ALTITUDE);
            if (altString != null) {
                Double alt = getDiv(altString);
                if (alt != null)
                    result.setAltitude(String.valueOf(alt));
//            for (int i = GpsDirectory.TAG_VERSION_ID; i < GpsDirectory.TAG_H_POSITIONING_ERROR; i++) {
//                String xx = gpsDirectory.getString(i);
//                if (xx != null) {
//                    System.out.println(i + " : " + xx);
//                }
//            }
            }
            GeoLocation geoLocation = gpsDirectory.getGeoLocation();
            if (geoLocation != null) {
                Double lat = geoLocation.getLatitude();
                Double lon = geoLocation.getLongitude();
                result.setPos(lat + "x" + lon);
            }
            //assertEquals(1277374641000L, gpsDirectory.getGpsDate().getTime());
        }
        result.setMetadata(print(metadata, "Using ImageMetadataReader"));
    }

    private static void parseMovieMetadata(Metadata metadata, FileModel result) {
        StringBuilder resolution = new StringBuilder();
        Directory directoryMp4 = metadata.getFirstDirectoryOfType(Mp4Directory.class);
        if (directoryMp4 != null) {
            final Integer seconds = directoryMp4.getInteger(Mp4Directory.TAG_DURATION_SECONDS);
            int p1 = seconds % 60;
            int p2 = seconds / 60;
            int p3 = p2 % 60;
            p2 = p2 / 60;
            result.setDuration(String.format("%02d:%02d:%02d", p2, p3, p1));
        }

        Directory mp4VideoDirectory = metadata.getFirstDirectoryOfType(Mp4VideoDirectory.class);
        if (mp4VideoDirectory != null) {
            resolution.append(mp4VideoDirectory.getString(Mp4VideoDirectory.TAG_WIDTH));
            resolution.append("x");
            resolution.append(mp4VideoDirectory.getString(Mp4VideoDirectory.TAG_HEIGHT));
            result.setResolution(resolution.toString());
            result.setFrameRate(Integer.parseInt(mp4VideoDirectory.getString(Mp4VideoDirectory.TAG_FRAME_RATE)));
            //result.setFrameRate(Integer.parseInt(mp4VideoDirectory.getString(Mp4VideoDirectory.TAG_COMPRESSOR_NAME)));
            //System.out.println(mp4VideoDirectory.getString(Mp4VideoDirectory.TAG_COMPRESSOR_NAME));
            result.setCompressionType(mp4VideoDirectory.getString(Mp4VideoDirectory.TAG_COMPRESSION_TYPE));
        }
        result.setMetadata(print(metadata, "Using MP4MetadataReader"));
    }

    /**
     * Write all extracted values to stdout.
     */
    private static List<String> print(Metadata metadata, String method) {
        // A Metadata object contains multiple Directory objects
        List<String> result = new ArrayList<>();
        for (Directory directory : metadata.getDirectories()) {
            // Each Directory stores values in Tag objects
            for (Tag tag : directory.getTags()) {
                result.add(tag.toString());
            }
            // Each Directory may also contain error messages
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
