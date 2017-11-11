//imports
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageReader {
	
	// main function
	public static void main(String[] args) {
		
		// input image
		// TODO possibly get this to take in multiple image files? PNG format preferred... 
		File input_image = new File("testbox.png"); 
		BufferedImage image = null;
		
		// colored pixel counters
		int white_pixel_Count = 0;
		int black_pixel_Count = 0;
		
		try{
			image = ImageIO.read(input_image);
			
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
			void_space_horizontal(ymin, xmin, ymax, xmax, image.getHeight(), image.getWidth(), image, LL_RL_array );
			void_space_vertical(ymin, xmin, ymax, xmax, image.getHeight(), image.getWidth(), image, UL_LL_array );
			
			//left limit to right limit, then top limit to bottom limit pixel scanning
			int scan = 0;
			for(int i = UL_LL_array[0]; i < UL_LL_array[1]+1; i++ ){
				for(int j = LL_RL_array[scan]; j < LL_RL_array[scan+1]+1; j++){
				
					int pixel = image.getRGB(j,i);
					//TODO A* Algorithm for shortest path (or w/e we decide) since nodes can be represented as 0's and 1's
					
					// black pixel detection
					if((pixel & 0x00FFFFFF) == 0){
						//TODO push these meaningful black pixels into an array, represent as 1's
						black_pixel_Count++;
					}
					
					// white pixel detection
					else if(!((pixel & 0x00FFFFFF) == 0)){
						//TODO push these meaningful white pixels into an array, represent as 0's
						white_pixel_Count++;
					}
				}
			}
		
		// sanity check, can comment this out later or remove it entirely, just left it here for testing purposes
		sanity_check(ymax, xmax, white_pixel_Count, black_pixel_Count);
		
		// exception handling
		}catch (IOException e){
			e.printStackTrace();
		}
	} // end of main function
	
	// determines the start and end black pixel of each horizontal line, to eliminate void space
	// returns an array in the format [[left limit, right limit], [left limit, right limit]... for every line
	public static int[] void_space_horizontal(int ymin, int xmin, int ymax, int xmax, int height, int width, BufferedImage image, int[] LL_RL_array){
		
		int LL_count = 0;
		int RL_count = 1;
		
		// left side (->) scan on a line for first horizontal black pixel
		for(int i = ymin; i < ymax; i++ ){
			for(int j = xmin; j < xmax; j++){
				int pixel = image.getRGB(j,i);
				if((pixel & 0x00FFFFFF) == 0){
					//System.out.println(" LL black pixel found at: " + i + "," + j);
					LL_RL_array[LL_count] = j;
					LL_count+=2;
					break;
				}
			}
		}
		
		// right side (<-) scan on a line for last horizontal black pixel
		for(int k = ymin; k < ymax; k++ ){
			for(int l = xmax-1; l > xmin; l--){
				int pixel = image.getRGB(l,k);
				if((pixel & 0x00FFFFFF) == 0){
					//System.out.println(" RL black pixel found at: " + k + "," + l);
					LL_RL_array[RL_count] = l;
					RL_count+=2;
					break;
					
				}
			}
		}
		
		return LL_RL_array;
	} // end of void_space_horizontal
	
	
	// determines the start and end black pixel of each vertical line, to eliminate void space
	// returns an array in the format [[lower limit, upper limit]]
	public static int[] void_space_vertical(int ymin, int xmin, int ymax, int xmax, int height, int width, BufferedImage image, int[] UL_LL_array){

		boolean first_black_pixel_found = false;
		boolean last_black_pixel_found = false;
		
		// downwards scan of an image to find the line with the first black pixel
		for(int i = ymin; i < ymax; i++ ){
			for(int j = xmin; j < xmax; j++){
			
				int pixel = image.getRGB(j,i);
				
				// black pixel detection
				if((pixel & 0x00FFFFFF) == 0 && first_black_pixel_found == false){
					first_black_pixel_found = true;
					UL_LL_array[0] = i;
					//System.out.println(i);
					break;
				}
			}
		}
		
		// upwards scan of an image to find the line with the first black pixel
		for(int i = ymax-1; i > ymin; i-- ){
			for(int j = xmin; j < xmax; j++){
			
				int pixel = image.getRGB(j,i);
				
				// black pixel detection
				if((pixel & 0x00FFFFFF) == 0 && last_black_pixel_found == false){
					last_black_pixel_found = true;
					UL_LL_array[1] = i;
					//System.out.println(i);
					break;
				}
			}
		}
		
		return UL_LL_array;
	} // end of void space vertical
	
	// sanity check, just to verify that everything works properly
	public static void sanity_check(int height, int width, int whiteCount, int blackCount){
		
		boolean pixel_check = false;
		if((height*width)- (whiteCount + blackCount) == 0){
			pixel_check = true;
		}
		
		System.out.println("image height: " + height);
		System.out.println("image width: " + width);
		System.out.println("total pixels: " + (height * width));
		System.out.println("white pixel count within x & y boundaries: " + whiteCount);
		System.out.println("black pixel count within x & y boundaries: " + blackCount);
		System.out.println("all pixels accounted for? " + pixel_check);
		
	} // end of sanity check 
}


