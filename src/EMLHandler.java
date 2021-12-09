import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class EMLHandler {

    public EMLHandler() {
    }

    void parseFile(String emlPath) throws Exception {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);  // included with the JavaMail API
        InputStream inMsg = new FileInputStream(emlPath);
        Message msg = new MimeMessage(session, inMsg);  // Multipurpose Internet Mail Extensions
        parseEml(msg);
    }

    private void parseEml(Message msg) throws Exception {
        Address[] froms = msg.getFrom();
        if (froms != null) {
            InternetAddress addr = (InternetAddress) froms[0];
            VehicleTracker.info.employee_name = addr.getPersonal();
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

    private void reMultipart(Multipart multipart) throws Exception {
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
    private void rePart(Part part) throws Exception {
        if (part.getDisposition() != null) {
            String strFileName = part.getFileName();
            if (strFileName != null) {
                strFileName = MimeUtility.decodeText(strFileName);  //MimeUtility.decodeText solves the garbled problem of attachment name
                //Saving image
                ImageHandler imageHandler = new ImageHandler();
                imageHandler.saveImage(part, "images/" + strFileName);
                //QR Code
                QRHandler qrHandler = new QRHandler();
                VehicleTracker.info.license_plate = qrHandler.readQRcode("images/" + strFileName, imageHandler);
                //MetaData
                VehicleTracker.info.date_time = imageHandler.getTime("images/" + strFileName);
                VehicleTracker.info.location = imageHandler.getLocation("images/" + strFileName);
                //Odometer
                OdometerHandler odometerHandler = new OdometerHandler();
                VehicleTracker.info.odometer = odometerHandler.getOdometerReading("images/" + strFileName, imageHandler);
            }
        }
    }

}
