import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import javax.imageio.ImageIO;
import javax.mail.Part;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageHandler {

    public ImageHandler() {
    }

    void saveImage(Part part, String strFileName) throws Exception {
        InputStream in = part.getInputStream();
        FileOutputStream out = new FileOutputStream(strFileName);
        byte[] bytes = new byte[1024];
        while (in.read(bytes, 0, 1024) != -1) {
            out.write(bytes);
        }
        in.close();
        out.close();
    }

    // @Precondition: ratio > 2
    // Zoom in the image by ratio
     BufferedImage getSubImage(String strFileName, int ratio) throws IOException {
        BufferedImage bf = ImageIO.read(new FileInputStream(strFileName));
        int x = bf.getWidth()/ratio, y = bf.getHeight()/ratio;
        return bf.getSubimage(x, y, bf.getWidth()-2*x, bf.getHeight()-2*y); //zoom in
    }

    //get the time of the image
     String getImageTime(String strFileName) throws Exception {
        String time = null;
        Metadata metadata = ImageMetadataReader.readMetadata(new File(strFileName));
        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                if (directory.getName().equals("Exif SubIFD")) {
                    if (tag.getTagName().equals("Date/Time Original")) {
                        time = tag.getDescription();
                    }
                }
            }
            if (directory.hasErrors()) {
                for (String error : directory.getErrors()) {
                    System.err.format("ERROR: %s", error);
                }
            }
        }
        return time;
    }

    //get the location of the image
     String getImageLocation(String strFileName) throws Exception {
        String location = null;
        Metadata metadata = ImageMetadataReader.readMetadata(new File(strFileName));
        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                if (directory.getName().equals("GPS")) {
                    if (tag.getTagName().equals("GPS Latitude")) {
                        location = tag.getTagName() + ": " + tag.getDescription() + "; ";
                    }
                    if (tag.getTagName().equals("GPS Longitude")) {
                        location += tag.getTagName() + ": " + tag.getDescription() + ";";
                    }
                }
            }
            if (directory.hasErrors()) {
                for (String error : directory.getErrors()) {
                    System.err.format("ERROR: %s", error);
                }
            }
        }
        return location;
    }



}
