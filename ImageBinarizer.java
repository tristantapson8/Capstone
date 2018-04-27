//imports
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *  This class is a simplified ImageBinarizer, which converts a multi-colored image
 *  into a binary colored image - the colors being black and white. The specific
 *  color values are needed to be exactly black and white (RGB: 255 | 255 | 255
 *  and RGB: 0 | 0 | 0 as white and black respectively, so that the processing can 
 *  determine what is floor space (white pixels) and what are wall and boundaries
 *  (black pixels) 
 *  
 */

public class ImageBinarizer {
	// main function
		public static void main(String[] args) {
			
			// input image
		    File input_image = null;
			BufferedImage image = null;
			
			System.out.println("Binarization done.");
			
			// TODO make this user input... 
			// threshold is basically a slider that determines how much of the picture is to be made visible
			// user must test values with their image to see that the binarized version is sufficient, so this
			// value should be modified by them until a desired result is given
			
			int threshold = 122;
			
			try{
				input_image = new File("input1.png"); 
				image = ImageIO.read(input_image);
				
				final int xmin = image.getMinX(); 
				final int ymin = image.getMinY();

				final int ymax = ymin + image.getHeight();
				final int xmax = xmin + image.getWidth();
				
				// iterate through the image in a left to right, top to bottom fashion
				for(int i = 0; i < image.getHeight(); i++ ){
					for(int j = 0; j < image.getWidth(); j++){
					
						Color pixel = new Color (image.getRGB(j,i));
						
						// creating RBG values from the current pixel
						int red = pixel.getRed();
						int green = pixel.getGreen();
						int blue = pixel.getBlue();
						int alpha = pixel.getAlpha();
						
						// System.out.println ( red + " / " + green + " / " + blue + " / " + alpha);
						
						// Creating black and white colors 
						Color bColor = new Color(0, 0, 0, alpha); // black
						Color wColor = new Color(255, 255, 255, alpha); //white
						
						// pixels within the threshold are set to black
						if(red < threshold && green < threshold && blue < threshold){
							image.setRGB(j, i, bColor.getRGB());
						}
						
						// any pixels not within the threshold are set to white
						else{
							image.setRGB(j, i, wColor.getRGB());
						}
							
					}
				}
			// exception handling
			}catch (IOException e){
				e.printStackTrace();
			}
	
			// write image
		    try{
		      input_image = new File("binarized.png");
		      ImageIO.write(image, "png", input_image);
		    }catch(IOException e){
		      System.out.println(e);
		    }
		} // end of main function
}
