import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OdometerHandler {

    public OdometerHandler() {
    }

    String getOdometerReading(String strFileName, ImageHandler imageHandler) throws Exception {
        //finding and cropping red rectangle
        boolean goToOCR = cropRedRectangle(strFileName, imageHandler);
        //run OCR on cropped odometer
        if (goToOCR) return runOCR();
        else return "INVALID IMAGE";
    }

    private boolean cropRedRectangle(String strFileName, ImageHandler imageHandler) throws Exception {
        //finding red rectangle
        //assuming the Odometer is in the middle of the image, Therefore there is no need to scan at the edges
        BufferedImage SubImage = imageHandler.getSubImage(strFileName, 4); //(need to make sure include odometer)
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
    private String runOCR() {
        Tesseract tesseract = new Tesseract();
        try {
            String text = tesseract.doOCR(new File("images/croppedOdometer.jpg")).replaceAll("\\s+", "");
            if (text.length()>6){
                text = text.substring(0,6);
            }
            return text;
        } catch (TesseractException e) {
            System.err.println("Odometer Read couldn't be found, please retake a clear picture!");
            return "INVALID IMAGE";
        }
    }

}
