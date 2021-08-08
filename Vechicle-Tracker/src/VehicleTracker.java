import java.util.ArrayList;
import java.util.List;
// metadata
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
// zxing (QR code)
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
// images and color
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
// EML parsing
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
// OCR
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

 /*
 * >>> PLEASE READ THE README FILE BEFORE RUNNING THE PROGRAM <<<
 *
 * Objective:
 * Extract data from an EML file (with attached image) to a CSV "log" file
 * The extracting data: Employee Name -> email sender
 *                      Car License Plate Number -> appears as QR code in the image
 *                      Date & time -> metadata of the image
 *                      Odometer Read -> marked in a red rectangle in the image
 *                      Parking Location -> metadata of the image
 *
 * @author: Tal Hazi
 * @version: 1.0
 *
 * Last Updated Date: Aug 5, 2021
 */

public final class VehicleTracker {
    static VehicleInfo information = new VehicleInfo();  //will hold the required data

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Please provide command line argument for EML file path");
            return;
        }
        information.create_csv(information.log_file_name);
        read_eml_files(args);
    }

    private static void read_eml_files(String[] emlPaths) throws Exception {
        for (String emlPath : emlPaths){
            System.out.println(">> " + emlPath);
            if (!emlPath.endsWith(".eml") | !(new File(emlPath).exists())) {
                System.out.println("does not read! you should provide valid EML file path\n");
                continue;
            }
            if (!information.emlAlreadyExist(information.log_file_name ,emlPath)){
                parserFile(emlPath);
                information.update_csv(emlPath);
                System.out.println("Program finished, log file has been updated successfully!\n");
                continue;
            }
            System.out.println("Program finished, this EML file already been read before :)\n");
        }
    }

    private static void parserFile(String emlPath) throws Exception {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);  // included with the JavaMail API
        InputStream inMsg = new FileInputStream(emlPath);
        Message msg = new MimeMessage(session, inMsg);  // Multipurpose Internet Mail Extensions
        parseEml(msg);
    }

    private static void parseEml(Message msg) throws Exception {
        Address[] froms = msg.getFrom();
        if (froms != null) {
            InternetAddress addr = (InternetAddress) froms[0];
            information.employee_name = addr.getPersonal();
        }
        Object o = msg.getContent();
        if (o instanceof Multipart) {
            Multipart multipart = (Multipart) o;
            reMultipart(multipart);
        } else if (o instanceof Part) {
            Part part = (Part) o;
            rePart(part);
        }
        else {
            System.out.println("the EML file doesn't contain an image :(");
        }
    }

    private static void reMultipart(Multipart multipart) throws Exception {
        for (int j = 0, n = multipart.getCount(); j < n; j++) {
            Part part = multipart.getBodyPart(j);  // Unpack, remove the various parts of the MultiPart
            if (part.getContent() instanceof Multipart) {
                Multipart p = (Multipart) part.getContent();
                reMultipart(p);
            } else {
                rePart(part);
            }
        }
    }

    //main extraction function
    private static void rePart(Part part) throws Exception {
        if (part.getDisposition() != null) {
            String strFileName = part.getFileName();
            if (strFileName != null) {
                //MimeUtility.decodeText solves the garbled problem of attachment name
                strFileName = MimeUtility.decodeText(strFileName);
                //Saving image
                saveImage(part, "images/" + strFileName);
                //QR Code
                QRcode("images/" + strFileName);
                //MetaData
                metaData("images/" + strFileName);
                //Odometer
                odometerReading("images/" + strFileName);
            }
        }
    }

    private static void saveImage(Part part, String strFileName) throws Exception {
        InputStream in = part.getInputStream();
        FileOutputStream out = new FileOutputStream(strFileName);
        byte[] bytes = new byte[1024];  //8*1024
        while (in.read(bytes, 0, 1024) != -1) {
            out.write(bytes);
        }
        in.close();
        out.close();
    }

    //read QR code
    private static void QRcode(String strFileName){
        try {
            BufferedImage SubImage = getSubImage(strFileName, 4); //(need to make sure include qr)
            //Attempt to read unclear QR code using: Scaling and Bicubic Interpolation
            AffineTransform xform =  AffineTransform.getScaleInstance(1.4,1.8);
            SubImage = new AffineTransformOp(xform, AffineTransformOp.TYPE_BICUBIC).filter(SubImage, null);

            File outfile = new File("images/croppedForQR_" + strFileName.replace("images/", ""));
            ImageIO.write(SubImage, "jpg", outfile);

            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(
                    new BufferedImageLuminanceSource(SubImage)));
            Result result = new MultiFormatReader().decode(bitmap);
            information.license_plate = result.getText();
        } catch (Exception e) {
            System.err.println("QR Code couldn't be found, please retake a clear picture!");
            information.license_plate = "INVALID QRCODE";
        }
    }

    // @Precondition: ratio > 2
    // Zoom in the image by ratio
    private static BufferedImage getSubImage(String strFileName, int ratio) throws IOException {
        BufferedImage bf = ImageIO.read(new FileInputStream(strFileName));
        int x = bf.getWidth()/ratio, y = bf.getHeight()/ratio;
        return bf.getSubimage(x, y, bf.getWidth()-2*x, bf.getHeight()-2*y); //zoom in
    }

    //get the time and location of the image
    private static void metaData(String strFileName) throws Exception {
        Metadata metadata = ImageMetadataReader.readMetadata(new File(strFileName));
        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                if (directory.getName().equals("Exif SubIFD")) {
                    if (tag.getTagName().equals("Date/Time Original")) {
                        information.date_time = tag.getDescription();
                    }
                }
                if (directory.getName().equals("GPS")) {
                    if (tag.getTagName().equals("GPS Latitude") || tag.getTagName().equals("GPS Longitude")) {
                        information.location += tag.getTagName() + ": " + tag.getDescription() + "; ";
                    }
                }
                //System.out.println(tag);
            }
            if (directory.hasErrors()) {
                for (String error : directory.getErrors()) {
                    System.err.format("ERROR: %s", error);
                }
            }
        }
    }

    private static void odometerReading(String strFileName) throws Exception {
        //finding and cropping red rectangle
        boolean goToOCR = cropRedRectangle(strFileName);
        //run OCR on cropped odometer
        if (goToOCR) runOCR();
    }

    private static boolean cropRedRectangle(String strFileName) throws Exception {
        //finding red rectangle
        BufferedImage SubImage = getSubImage(strFileName, 4); //(need to make sure include odometer)
        List<Integer> x_arr = new ArrayList<>();
        List<Integer> y_arr = new ArrayList<>();
        for (int y = 0; y < SubImage.getHeight(); y++) {
            for (int x = 0; x < SubImage.getWidth(); x++) {
                int pixel = SubImage.getRGB(x, y);             //retrieve contents of a pixel
                Color color = new Color(pixel, true);  //create a Color object
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();
                if (red > 220 && green < 50 && blue < 40) {
                    x_arr.add(x);
                    y_arr.add(y);
                }
            }
        }
        if (x_arr.isEmpty()){
            System.err.println("Odometer should display with red rectangle, please add the sticker to your dashboard");
            information.odometer = "INVALID IMAGE";
            return false;
        }
        Collections.sort(x_arr);
        int x_minimum = x_arr.get(0);
        int x_maximum = x_arr.get(x_arr.size()-1);
        int y_minimum = y_arr.get(0);
        int y_maximum = y_arr.get(y_arr.size()-1);

        //cropping
        BufferedImage odometerOnly = SubImage.getSubimage(x_minimum, y_minimum,
                x_maximum-x_minimum, y_maximum-y_minimum);
        File outfile = new File("images/croppedOdometer.jpg");
        ImageIO.write(odometerOnly, "jpg", outfile);
        return true;
    }

    //run OCR on cropped odometer
    private static void runOCR() {
        Tesseract tesseract = new Tesseract();
        try {
            String text = tesseract.doOCR(new File("images/croppedOdometer.jpg")).replaceAll("\\s+", "");
            if (text.length()>6){
                text = text.substring(0,6);
            }
            information.odometer = text;
        } catch (TesseractException e) {
            System.err.println("Odometer Read couldn't be found, please retake a clear picture!");
            information.odometer = "INVALID IMAGE";
        }
    }

}
