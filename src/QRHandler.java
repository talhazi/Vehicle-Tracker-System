import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class QRHandler {

    public QRHandler() {
    }

    String readQRcode(String strFileName, ImageHandler imageHandler){
        try {
            //assuming the QRcode is in the middle of the image, Therefore there is no need to scan at the edges
            BufferedImage SubImage = imageHandler.getSubImage(strFileName, 4); //(need to make sure include qr)
            //Attempt to read unclear QR code using: Scaling and Bicubic Interpolation
            AffineTransform xform =  AffineTransform.getScaleInstance(1.4,1.8);
            SubImage = new AffineTransformOp(xform, AffineTransformOp.TYPE_BICUBIC).filter(SubImage, null);

            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(
                    new BufferedImageLuminanceSource(SubImage)));
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText();

        } catch (Exception e) {
            System.err.println("QR Code couldn't be found, please retake a clear picture!");
            return  "INVALID QRCODE";
        }
    }

}
