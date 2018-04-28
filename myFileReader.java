import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


/**
 * Function to print values to the console, mainly used for testing purposes
 * 
 * @param height the height of the image in pixels
 * @param width the width of the image in pixels
 * @param whiteCount the total number of white pixels within the image
 * @param blackCount the total number of black pixels within the image
 * 
 * @return returns void
 */

// for parsing the input file produced by the OCR

public class myFileReader {
	
	/**
	  * Main function, just runs the program. Structured in this way for testing,
	  * however the main function runs a sub function called read me, which is 
	  * imported into the ImageReader class so that the text file containing the 
	  * room names read by the OCR is input into an array such that it can be
	  * iterated through to help generate a JSON file containing the necessary
	  * metrics (room nodes, room names) to generate the floor plan in the front end
	  * tool
	  * 
	  */
	
    public static void main(String[] args) {
    	readMe();
    } // end of main
    
    /**
	  * Main function, 
	  * 
	  * @param height the height of the image in pixels
	  * @param width the width of the image in pixels
	  * @param whiteCount the total number of white pixels within the image
	  * @param blackCount the total number of black pixels within the image
	  * 
	  * @return returns void
	  */
    
	public static String[] readMe(){
		
		int max = getLineCount();
		//String roomCenters[] = new String[max];
		String roomNames[] = new String[max];
    	
    		/// FILE READING FOR ROOMS /// 

       	 	String textFile = "inputText.txt";
        	BufferedReader br = null;
        	String line = "";
        	String splitter = " ";
        	int roomCount = 0;
		
        
        
        	try {

           		br = new BufferedReader(new FileReader(textFile));
            	 	while ((line = br.readLine()) != null) {
            
                		// split by space
                		String[] roomName = line.split(splitter);
                
            			roomNames[roomCount] = roomName[2];
    				roomCount++;
                
               	 		// writing to new file all the room names
                		try{
        	        		String path = "output.txt";
        	        		File file2 = new File(path);
        	        		FileWriter fileWriter = new FileWriter(file2,true);
        	        		BufferedWriter bufferFileWriter  = new BufferedWriter(fileWriter);
        	  
        	        		//fileWriter.append(roomName[2] + "\n");
        	        		bufferFileWriter.close();
					
        	 		}catch(Exception ex)
        	    		{
        	        		System.out.println(ex);
        	    		}

                	// gets the second value from the file, which is the room name
                	// print statement is just for testing purposes
                	
			// System.out.println("roomName: " + roomName[2] + "");
            	}

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        //spitOutNames();
        return getRoomNames(roomNames);
    	}

  /**
   * Function to print the room names, mainly used for testing purposes
   *
   * @return returns void
   */
	
   // testing purposes
   private static void spitOutNames() {
		// TODO Auto-generated method stub
		for(int i = 0; i < getLineCount(); i++){
			//System.out.println(roomNames[i]);
		}
	}
   
   /**
    * Function the return the total number of lines in the text file provided  
    * by the OCR output text, used to initialize the size of the array storing
    * the room names
    *
    * @return returns as an integer the total number of lines in the input text file
    */

   // gets the number of lines in an input text file
    public static int getLineCount() {
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("inputText.txt"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int lines = 0;
		try {
			while (reader.readLine() != null) lines++;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// returns the total number of lines in the input text file
		return lines;
	}
    
       /**
	* Function to return the array storing the room names read from the OCR input
	* @param names an array of strings containing the names read by the OCR
	* 
	* @return returns void
	*/
    
	public static String[] getRoomNames(String[] names){
		for ( int i =0; i < getLineCount();i++ ){
			System.out.println(names[i]);
		}
		return names;
	}
}
