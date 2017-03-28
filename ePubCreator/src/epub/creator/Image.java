/* XTM to ePUB converter created with JavaCC 1.5.33 and Java 1.8
 * Converter created by Gianluca Barbera and Marco Placidi
 * Released under license CreativeCommons CC BY-NC-SA
 * https://creativecommons.org/licenses/by-nc-sa/4.0/legalcode
 */ 

package epub.creator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
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

/** Do not directly use this class, QR Code creator/Image Downloader
*/
public abstract class Image
implements Runnable {
	
	@Override
	public void run() {
		String imageUrl = "http://67.media.tumblr.com/00610f8b4536124445c7fbbef18884d9/tumblr_nna2rjD83i1utp969o1_1280.png";
		
		URL url=null;
		try {
			url = new URL(imageUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		String fileName = url.getFile();
		String destinationFile = "./" + fileName.substring(fileName.lastIndexOf("/"));
		try {
			saveImage(imageUrl, destinationFile);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

	//FOR TEST ONLY...
	public static void main(String[] args) throws Exception {
		
		String imageUrl = "http://67.media.tumblr.com/00610f8b4536124445c7fbbef18884d9/tumblr_nna2rjD83i1utp969o1_1280.png";
		URL url = new URL(imageUrl);
		String fileName = url.getFile();
		String destinationFile = "./" + fileName.substring(fileName.lastIndexOf("/"));

		saveImage(imageUrl, destinationFile);
	}

	/** Saves an URL image <p>
	 * @param imageUrl The image URL
	 * @param destinationFile The file name in the epub structure
	 */
	public static void saveImage(String imageUrl, String destinationFile) throws IOException, WriterException {
		URL urlz = new URL(imageUrl);
		String imageUrlz = imageUrl;
		URLConnection conn = urlz.openConnection();
		long contentLength = conn.getContentLengthLong();
		if(contentLength<20L){
			InputStream is = urlz.openStream();
			OutputStream os = new FileOutputStream(destinationFile);

			byte[] b = new byte[2048];
			int length;

			while ((length = is.read(b)) != -1) {
				os.write(b, 0, length);
			}

			is.close();
			os.close();
			System.out.println("Immagine scaricata");
		}else {
			System.out.println("Immagine troppo grande, QRCODE necessario");
	        String filePath = "putin.png";
	        int size = 250;
	        String fileType = "png";
	        File qrFile = new File(filePath);
	        createQRImage(qrFile, imageUrlz, size, fileType);
	        System.out.println("QRCODE Generato");
			}

	}
	
	/** Creates a QR image
	 * @param qrFile The generated file name
	 * @param qrCodeText Text to encode
	 * @param size Pixel size of the QR Code Image 
	 * @param fileType Extensions of the file
	 */
    private static void createQRImage(File qrFile, String qrCodeText, int size,
            String fileType) throws WriterException, IOException {
        // Create the ByteMatrix for the QR-Code that encodes the given String
        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
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
