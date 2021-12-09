import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class EMLHandler {

    public EMLHandler() {
    }

    void readEmlFiles(String[] emlPaths, CSVHandler csvHandler) throws Exception {
        for (String emlPath : emlPaths){
            System.out.println(">> " + emlPath);
            if (!emlPath.endsWith(".eml") | !(new File(emlPath).exists())) {
                System.out.println("Doesn't read! you should provide valid EML file path\n");
                continue;
            }
            if (!csvHandler.emlAlreadyExist(csvHandler.getFileName() ,emlPath)){
                VehicleInfo vehicleInfo = new VehicleInfo();
                parseFile(emlPath, vehicleInfo);
                csvHandler.updateCsv(emlPath, vehicleInfo);
                System.out.println("Program finished, log file has been updated successfully!\n");
                continue;
            }
            System.out.println("Program finished, this EML file already been read before :)\n");
        }
    }

    void parseFile(String emlPath, VehicleInfo vehicleInfo) throws Exception {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);  // included with the JavaMail API
        InputStream inMsg = new FileInputStream(emlPath);
        Message msg = new MimeMessage(session, inMsg);  // Multipurpose Internet Mail Extensions
        parseEml(msg, vehicleInfo);
    }

    private void parseEml(Message msg, VehicleInfo vehicleInfo) throws Exception {
        Address[] froms = msg.getFrom();
        if (froms != null) {
            InternetAddress addr = (InternetAddress) froms[0];
            vehicleInfo.setEmployeeName(addr.getPersonal());
        }
        Object content = msg.getContent();
        if (content instanceof Multipart) {
            Multipart multipart = (Multipart) content;
            reMultipart(multipart, vehicleInfo);
        } else if (content instanceof Part) {
            Part part = (Part) content;
            rePart(part, vehicleInfo);
        }
        else {
            System.out.println("the EML file doesn't contain an image :(");
        }
    }

    private void reMultipart(Multipart multipart, VehicleInfo vehicleInfo) throws Exception {
        for (int i = 0, n = multipart.getCount(); i < n; i++) {
            Part part = multipart.getBodyPart(i);  // Unpack, remove the various parts of the MultiPart
            if (part.getContent() instanceof Multipart) {
                Multipart p = (Multipart) part.getContent();
                reMultipart(p, vehicleInfo);
            } else {
                rePart(part, vehicleInfo);
            }
        }
    }

    // main extraction function
    private void rePart(Part part, VehicleInfo vehicleInfo) throws Exception {
        if (part.getDisposition() != null) {
            String strFileName = part.getFileName();
            if (strFileName != null) {
                strFileName = MimeUtility.decodeText(strFileName);  //MimeUtility.decodeText solves the garbled problem of attachment name
                ImageHandler imageHandler = new ImageHandler();
                //Saving image
                imageHandler.saveImage(part, "images/" + strFileName);
                //QR Code
                QRHandler qrHandler = new QRHandler();
                vehicleInfo.setLicensePlate(qrHandler.getQRcodeReading("images/" + strFileName, imageHandler));
                //MetaData
                vehicleInfo.setDateAndTime(imageHandler.getImageTime("images/" + strFileName));
                vehicleInfo.setLocation(imageHandler.getImageLocation("images/" + strFileName));
                //Odometer
                OdometerHandler odometerHandler = new OdometerHandler();
                vehicleInfo.setOdometer(odometerHandler.getOdometerReading("images/" + strFileName, imageHandler));
            }
        }
    }

}
