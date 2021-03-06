package at.spengergasse.hbgm.GUI;

import at.spengergasse.hbgm.interfaces.IImagePanel;
import at.spengergasse.hbgm.interfaces.IObservable;
import at.spengergasse.hbgm.interfaces.IObserver;
import at.spengergasse.hbgm.interfaces.IPatientBrowser;
import at.spengergasse.hbgm.tools.Builder;
import at.spengergasse.hbgm.tools.LookupTable;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.io.DicomInputStream;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
public class ImagePanel extends JPanel implements IImagePanel {

    private BufferedImage image;
    private File dcmFile;
    private IPatientBrowser browser;

    private void LoadImage(){
        try {
            dcmFile = new File("images/ABF2AD3A.dcm");
            LookupTable lt = new LookupTable();
            DicomInputStream dcmIn = new DicomInputStream(dcmFile);
            DicomObject dcmObj = dcmIn.readDicomObject();
            short[] pixelData = dcmObj.getShorts(Tag.PixelData);
            short maxValue = pixelData[0];
            short minValue = pixelData[0];
            int columns = dcmObj.getInt(Tag.Columns);
            int rows = dcmObj.getInt(Tag.Rows);
            int pixels = columns * rows;
            int meanValue;
            int sum = 0;

            for(int i = 0; i<pixelData.length; i++){
                if(pixelData[i] > maxValue){
                    maxValue = pixelData[i];
                }
            }
            for(int i = 0; i<pixelData.length; i++){
                if(pixelData[i] < minValue){
                    minValue = pixelData[i];
                }
            }
            for(int i = 0; i<pixelData.length; i++){
                sum+=pixelData[i];
            }
            meanValue = sum/pixels;
            image = new BufferedImage(columns,rows,BufferedImage.TYPE_INT_ARGB);
            lt.setAlpha(255);
            lt.setCentre(meanValue);
            lt.setWidth(maxValue);

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < columns; c++) {
                    image.setRGB(c, r, lt.Argb(pixelData[r * columns + c]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ImagePanel(){
        LoadImage();
        this.setPreferredSize(new Dimension(image.getWidth(),image.getHeight()));
    }

    @Override
    protected void paintComponent(Graphics graphics){
        super.paintComponent(graphics);
        if(image != null){
            graphics.drawImage(image,0,0,this);
        }
    }

    @Override
    public void Configure(Builder builder) {
        browser = builder.getPatientBrowser();
        browser.registerObserver(this);
    }

    @Override
    public JComponent UIComponent() {
        return this;
    }

    @Override
    public void registerObserver(IObserver o) {

    }

    @Override
    public void removeObserver(IObserver o) {

    }

    @Override
    public void changed(IObservable o) {
        browser.GewaehlteInstanz();
    }
}
