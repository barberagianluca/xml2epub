/* XTM to ePUB converter created with JavaCC 1.5.33 and Java 1.8
 * Converter created by Gianluca Barbera and Marco Placidi
 * Released under license CreativeCommons CC BY-NC-SA
 * https://creativecommons.org/licenses/by-nc-sa/4.0/legalcode
 */ 

package epub.translator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import epub.creator.BookPrinter;

/** Multimedia Handler*/
public abstract class ImageAndVideoHandler {

	/** DEBUG ONLY!!*/
	public static void main(String[] args) throws Exception {
		String imageUrl = "http://67.media.tumblr.com/00610f8b4536124445c7fbbef18884d9/tumblr_nna2rjD83i1utp969o1_1280.png";
		String videoUrl = "https://www.youtube.com/watch?v=QeSk9fSzvaE";		

		saveImage(imageUrl, 5000);
		saveVideo(videoUrl);
	}
    /** Convert an image to qrCode or to PNG
     * @param imageUrl Link of the image
     * @param maxSize Size threshold to decide in Byte
     * @return The new image URL
     */
	public static String saveImage(String imageUrl, int maxSize) throws IOException, WriterException {
		//imageUrl = "http://67.media.tumblr.com/00610f8b4536124445c7fbbef18884d9/tumblr_nna2rjD83i1utp969o1_1280.png";
		URL url = new URL(imageUrl);
		String fileName = url.getFile();
		String destinationFile = "./" + fileName.substring(fileName.lastIndexOf("/"));
		boolean connected=true;
		InputStream is = null;		
		URLConnection conn = url.openConnection();
		int contentLength = conn.getContentLength();
		String filePath="";
		
		//Check connection!
		try {
			is = url.openStream();
		} catch (IOException ioe) {
			//ioe.printStackTrace();
			System.err.println("Impossibile scaricare l'immagine, impossibile risolvere l'host. Controllare connessione internet!");
			connected = false;
		}
		if(contentLength<maxSize && connected){
			filePath = BookPrinter.imagesTempPath+destinationFile;
			OutputStream os = new FileOutputStream(filePath);

			byte[] b = new byte[2048];
			int length;

			while ((length = is.read(b)) != -1) {
				os.write(b, 0, length);
			}

			is.close();
			os.close();
			if(BookPrinter.verboseCreatorEnabled) System.out.println("Immagine scaricata");
		} else {
	        filePath = BookPrinter.imagesTempPath+destinationFile+"QR_ENCODED.png";
	        int size = 125;
	        String fileType = "png";
	        File qrFile = new File(filePath);
	        createQRImage(qrFile, imageUrl, size, fileType);
	        if(BookPrinter.verboseCreatorEnabled) System.out.println("QRCODE Generato");
		}
		
		return filePath;

	}
    
	/** Convert a video to qrCode
     * @param videoUrl Link of the image
     * @return The new video URL
     */
	public static String saveVideo(String videoUrl) throws IOException, WriterException {
		URL url = new URL(videoUrl);
		String fileName = url.getFile();
		String destinationFile;
		if(fileName.lastIndexOf("=")==-1) {
			destinationFile = "VideoURL"+System.currentTimeMillis();
		} else {
			destinationFile = "" + fileName.substring(fileName.lastIndexOf("=")); //common youtube links
		}
		
		String filePath = BookPrinter.imagesTempPath+destinationFile+"_QR_ENCODED.png";
        int size = 125;
        String fileType = "png";
        File qrFile = new File(filePath);
        createQRImage(qrFile, videoUrl, size, fileType);
        if(BookPrinter.verboseCreatorEnabled) System.out.println("QRCODE Generato");
        
        return filePath;
	}
	
	private static void createQRImage(File qrFile, String qrCodeText, int size, String fileType) throws WriterException, IOException {
        // Create the ByteMatrix for the QR-Code that encodes the given String
        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel> ();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeText,
                BarcodeFormat.QR_CODE, size, size, hintMap);
        // Make the BufferedImage that are to hold the QRCode
        int matrixWidth = byteMatrix.getWidth();
        BufferedImage image = new BufferedImage(matrixWidth, matrixWidth,
                BufferedImage.TYPE_INT_RGB);
        image.createGraphics();
 
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, matrixWidth, matrixWidth);
        // Paint and save the image using the ByteMatrix
        graphics.setColor(Color.BLACK);
 
        for (int i = 0; i < matrixWidth; i++) {
            for (int j = 0; j < matrixWidth; j++) {
                if (byteMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }
        ImageIO.write(image, fileType, qrFile);
    }
	
}