
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *  This class is a prototype for our completed ImageReader. It takes in an image
 *  as input (in .png format), and flood fills the enclosed components of a
 *  map an alternate color (other than white or black). If a pixel is valid
 *  for flood fill, the x coordinate is then stored in an array - the same is 
 *  also done for the y coordinate. Upon successful completion of iteration 
 *  through the image, the output image resulting is created, showing the valid
 *  floor space as a new alternate color; this was mainly used is an aid in 
 *  developing the full version of ImageReader
 *  
 */

public class ImageReaderPrototype {
	
	 /**
     * the main function of the program, handles the image input and output, and 
     * iteration through each pixel is done here. The enclosed checks are also 
     * done on each pixel during iteration, and modification is done to that pixel
     * if all condition checks are true.
     */
	
	public static void main(String[] args) {
		
		// input image
		// TODO possibly get this to take in multiple image files? PNG format preferred... 
	    File input_image = null;
		BufferedImage image = null;
		
		// colored pixel counters
		int white_pixel_Count = 0;
		int black_pixel_Count = 0;
		int modify_Count = 0;
		
		try{
			input_image = new File("testbox.png"); 
			image = ImageIO.read(input_image);
			
			// modified image
			//BufferedImage modifiedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
			
			final int xmin = image.getMinX(); 
			final int ymin = image.getMinY();

			final int ymax = ymin + image.getHeight();
			final int xmax = xmin + image.getWidth();
			
			// left limit right limit pixel array
			int max_size = 2 * image.getHeight();
			int LL_RL_array[] = new int[max_size];
			
			// upper limit lower limit pixel array
			int UL_LL_array[] = new int[2];
			
			// eliminating useless space by creating array of boundaries to be scanned via pixel looping
			//void_space_horizontal(ymin, xmin, ymax, xmax, image.getHeight(), image.getWidth(), image, LL_RL_array );
			//void_space_vertical(ymin, xmin, ymax, xmax, image.getHeight(), image.getWidth(), image, UL_LL_array );
			
			//left limit to right limit, then top limit to bottom limit pixel scanning
			for(int i = 0; i < image.getHeight(); i++ ){
				for(int j = 0; j < image.getWidth(); j++){
				
					int pixel = image.getRGB(j,i);
					
					int a = (pixel>>24)&0xff;
					int r = (pixel>>16)&0xff;
					int g = (pixel>>8)&0xff;
					int b = pixel&0xff;
					
					//TODO A* Algorithm for shortest path (or w/e we decide) since nodes can be represented as 0's and 1's
					
					// black pixel detection
					if((pixel & 0x00FFFFFF) == 0){
						//TODO push these meaningful black pixels into an array, represent as 1's
						black_pixel_Count++;
					}
					
					// white pixel detection
					 if(!((pixel & 0x00FFFFFF) == 0)){
						//TODO push these meaningful white pixels into an array, represent as 0's
						// pixel is surrounded, change the color (to red) for processing
						
						if(void_space_horizontal(j, i, ymax, xmax, image.getHeight(), image.getWidth(), image, LL_RL_array ) == true  &&
						   void_space_vertical(j, i, ymax, xmax, image.getHeight(), image.getWidth(), image, LL_RL_array ) == true	){
								modify_Count++;
								//check condition
								//System.out.println("Modify Pixel");
									
						        //set new RGB value (changes to BLUE)
								 pixel = 0 | 255 | 0 ;
								 image.setRGB(j, i, pixel);
						}
						
						else{
							// don't change the pixel color
							//System.out.println("DONT change");
						}
						//void_space_vertical(j, i, ymax, xmax, image.getHeight(), image.getWidth(), image, LL_RL_array );
						
					// System.out.println("Change color of " + j + "," + i);	
					white_pixel_Count++;
				}
			}
		}
		
	   
		// sanity check, can comment this out later or remove it entirely, just left it here for testing purposes
		sanity_check(ymax, xmax, white_pixel_Count, black_pixel_Count, modify_Count);
		
		// exception handling
		}catch (IOException e){
			e.printStackTrace();
		}
		
		//write image
	    try{
	      input_image = new File("output.jpg");
	      ImageIO.write(image, "jpg", input_image);
	    }catch(IOException e){
	      System.out.println(e);
	    }
		
		
		
	} // end of main function
	
	 /**
	  * Determines if white pixel is enclosed by black pixel horizontally, to eliminate void space 
	  * 
	  * @param yStart the starting y coordinate of the current pixel
	  * @param xStart the starting x coordinate of the current pixel
	  * @param ymax the maximum y value for the current column
	  * @param xmax the maximum x value for the current row
	  * @param height the height of the input image in pixels
	  * @param width the width of the input image in pixels
	  * @param image the input image to be modified 
	  * @param LL_RR_array an array to hold the x and y values of the pixel to be modified
	  * @param
	  * 
	  * @return returns true if  the pixel is completely enclosed by a wall, false otherwise
	  */ 
	
	// determines if white pixel is enclosed by black pixel horizontally, to eliminate void space
	// returns an array in the format [[left limit, right limit], [left limit, right limit]... for every line
	public static boolean void_space_horizontal(int yStart, int xStart, int ymax, int xmax, int height, int width, BufferedImage image, int[] LL_RL_array){
		
		//System.out.println("X: " + yStart);
		//System.out.println("Y: " + xStart);
		
		int LL_count = 0;
		int RL_count = 1;
		
		boolean leftScan = false;
		boolean rightScan = false;
	
			// start left side (->) scan on a line for first horizontal black pixel
			for(int j = yStart; j < image.getWidth(); j++){
				int pixel = image.getRGB(j, xStart);
				if((pixel & 0x00FFFFFF) == 0){
					//System.out.println(" LL black pixel found at: " + i + "," + j);
					LL_RL_array[LL_count] = j;
					LL_count+=2;
					leftScan = true;
					break;
				}
			}
	
			// start right side (<-) scan on a line for last horizontal black pixel
			for(int l = yStart; l > 0; l--){
				int pixel = image.getRGB(l,xStart);
				if((pixel & 0x00FFFFFF) == 0){
					//System.out.println(" RL black pixel found at: " + k + "," + l);
					LL_RL_array[RL_count] = l;
					RL_count+=2;
					rightScan = true;
					break;
					
				}
			}
	
		// both horizontal conditions met? 
		if(leftScan == true  && rightScan == true){
			return true;
			
		}
		else{
			return false;
		}
	} // end of void_space_horizontal
	
	
	 /**
	  * Determines if white pixel is enclosed by black pixel vertically, to eliminate void space 
	  * 
	  * @param yStart the starting y coordinate of the current pixel
	  * @param xStart the starting x coordinate of the current pixel
	  * @param ymax the maximum y value for the current column
	  * @param xmax the maximum x value for the current row
	  * @param height the height of the input image in pixels
	  * @param width the width of the input image in pixels
	  * @param image the input image to be modified 
	  * @param UL_LL_array an array to hold the x and y values of the pixel to be modified
	  * @param
	  * 
	  * @return returns true if  the pixel is completely enclosed by a wall, false otherwise
	  */ 
	
	// determines if white pixel is enclosed by black pixel vertically, to eliminate void space
	public static boolean void_space_vertical(int yStart, int xStart, int ymax, int xmax, int height, int width, BufferedImage image, int[] UL_LL_array){

		//System.out.println("Y: " + yStart);
		//System.out.println("X: " + xStart);
		
		boolean downScan = false;
		boolean upScan = false;
		
			// downwards scan of an image to find the line with the first black pixel
			for(int j = yStart; j < image.getHeight(); j++){
			
				int pixel = image.getRGB(xStart,j);
				
				// black pixel detection
				if((pixel & 0x00FFFFFF) == 0 && downScan == false){
					downScan = true;
					UL_LL_array[0] = j;
					//System.out.println(i);
					break;
				}
			}
		
			// upwards scan of an image to find the line with the first black pixel
			for(int j = yStart; j > 0; j--){
				int pixel = image.getRGB(j, xStart);
				
				// black pixel detection
				if((pixel & 0x00FFFFFF) == 0 && upScan == false){
					upScan = true;
					UL_LL_array[1] = j;
					//System.out.println(i);
					break;
				}
			}
		
		// both vertical conditions met? 
		if(downScan && upScan == true){
			return true;
		}
		
		else{
			return false;
		}
	} // end of void space vertical
	
	 /**
	  * Sanity check function, just to verify that everything is working properly
	  * 
	  * @param height the height of the input image in pixels
	  * @param width the width of the input image in pixels
	  * @param image the input image to be modified 
	  * @param whiteCount the total number of white pixels found in the image
	  * @param blackCount the total number of black pixels found in the image
	  * @param modCount the total number of pixels that are to be modified
	  * 
	  * @return returns true if  the pixel is completely enclosed by a wall, false otherwise
	  */ 
	
	// sanity check, just to verify that everything works properly
	public static void sanity_check(int height, int width, int whiteCount, int blackCount, int modCount){
		
		boolean pixel_check = false;
		if((height*width)- (whiteCount + blackCount) == 0){
			pixel_check = true;
		}
		
		System.out.println("image height: " + height);
		System.out.println("image width: " + width);
		System.out.println("total pixels: " + (height * width));
		System.out.println("white pixel count within x & y boundaries: " + whiteCount);
		System.out.println("black pixel count within x & y boundaries: " + blackCount);
		System.out.println("pixels to modify: " + modCount);
		System.out.println("all pixels accounted for? " + pixel_check);
		
	} // end of sanity check 
}

