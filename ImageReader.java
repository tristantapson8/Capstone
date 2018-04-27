//imports
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;



//VERTEX STUFF
class Vertex
{
	public List<Vertex> neighbors = new ArrayList<>();
	public int x;
	public int y;
}

class Room
{
	public List<Vertex> vertices = new ArrayList<>();
	public List<Door> doors = new ArrayList<>();
}

class Door
{
	public List<Room> rooms = new ArrayList<>();
	public int x;
	public int y;
}


public class ImageReader {
	

	
	enum PixelType
	{
		White, Black, Outline, Checked, ToCheck, Vertex, Probed, ProbedVertex, DoorVertex, DoorFlood, WaterProbed, WaterProbing, DoorOverflow, DoorOverflowPending, DoorFinal
	};
	
	static PixelType[][] pixelArray;
	static int[][] waterLevel;
	
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
			
			pixelArray = new PixelType[image.getWidth()][image.getHeight()];
			waterLevel = new int[image.getWidth()][image.getHeight()];
			
			for(int x = 0; x < pixelArray.length; x++)
			{
				for(int y = 0; y < pixelArray[x].length; y++)
				{
					int pixel = image.getRGB(x,y);
					if((pixel & 0x00FFFFFF) == 0)
					{
						pixelArray[x][y] = PixelType.Black;
						black_pixel_Count++;
					}
					else
					{
						pixelArray[x][y] = PixelType.White;
						black_pixel_Count++;						
					}
				}				
			}
					
		// sanity check, can comment this out later or remove it entirely, just left it here for testing purposes
		sanity_check(ymax, xmax, white_pixel_Count, black_pixel_Count);
			
		int seedSpacing = 20;
		int xBuffer = 100;
		int yBuffer = 60;
		
		for(int x = xBuffer; x < image.getWidth() - xBuffer; x += seedSpacing)
		{
			for(int y = yBuffer; y < image.getHeight() - yBuffer; y +=seedSpacing)
			{
				if(pixelArray[x][y] == PixelType.White)
				{
					pixelArray[x][y] = PixelType.ToCheck;
				}
			}
		}
			
		
		FloodFillOutline();
		System.out.println("Vectorize");
		//VertexSet();
		Vectorize();
		Doors();
		
		//ResetEVERYTHING(image);
				
		Color white = Color.white;
		Color black = Color.black;
		Color checked =  Color.orange;
		Color outline =  Color.green;
		Color vertex =  Color.blue;
		Color probed =  Color.GRAY;
		Color probedV =  Color.orange;
		Color vertexD =  Color.GREEN;
		Color vertexDF =  Color.yellow;
		Color vDO = Color.RED;
		
		white = Color.white;
		black = Color.white;
		vertexD =  Color.RED;

		
		for(int x = 0; x < pixelArray.length; x++)
		{
			for(int y = 0; y < pixelArray[x].length; y++)
			{
				Color colorToUse =  Color.blue;
				if(pixelArray[x][y] == PixelType.White)
				{
					colorToUse = white;
				}
				else if(pixelArray[x][y] == PixelType.Black)
				{
					colorToUse = black;
				}
				else if(pixelArray[x][y] == PixelType.Checked)
				{
					colorToUse = checked;
					//colorToUse = GetWaterColour(waterLevel[x][y]);
				}
				else if(pixelArray[x][y] == PixelType.Outline)
				{
					colorToUse = outline;
				}
				else if(pixelArray[x][y] == PixelType.Vertex)
				{
					colorToUse = vertex;
				}
				else if(pixelArray[x][y] == PixelType.Probed)
				{
					colorToUse = probed;
				}
				else if(pixelArray[x][y] == PixelType.ProbedVertex)
				{
					colorToUse = probedV;
				}
				else if(pixelArray[x][y] == PixelType.DoorVertex)
				{
					colorToUse = vertexD;
				}
				else if(pixelArray[x][y] == PixelType.DoorFlood)
				{
					colorToUse = vertexDF;
				}
				else if(pixelArray[x][y] == PixelType.WaterProbed)
				{
					colorToUse = Color.GREEN;
				}
				else if(pixelArray[x][y] == PixelType.DoorOverflow)
				{
					colorToUse = vDO;
				}
				else if(pixelArray[x][y] == PixelType.DoorFinal)
				{
					colorToUse = Color.CYAN;
				}
				
				image.setRGB(x, y, colorToUse.getRGB());
			}				
		}
		
		
		Graphics2D g = image.createGraphics();
		for(int i = 0; i < vertices.size(); i++)
		{
			for(int j = 0; j < vertices.get(i).neighbors.size(); j++)
			{
				DrawLine(vertices.get(i), vertices.get(i).neighbors.get(j), image, g);
			}
		}
		
		File outFile = new File("outputImage.png");
		ImageIO.write(image, "png", outFile);
		System.out.println("Done.");
		
		OutputTxt();
		
		// exception handling
		}catch (IOException e){
			e.printStackTrace();
		}
	} // end of main function
	
	static void OutputTxt()
	{
		final myFileReader fr = new myFileReader();
		System.out.println("OUTPUT");
		String roomVertices[] = new String[numberOfRooms];
		for(int i = 0; i < roomVertices.length; i++)
			{
				roomVertices[i] = "";
				System.out.println(i);
				System.out.println(rooms.size());
				for(int j = 0; j < rooms.get(i).vertices.size(); j++)
				{
					roomVertices[i] = roomVertices[i] + rooms.get(i).vertices.get(j).x;
					roomVertices[i] = roomVertices[i] + rooms.get(i).vertices.get(j).y;
					if(i < rooms.get(i).vertices.size() - 1)
					{
						roomVertices[i] = roomVertices[i] + ",";
					}
				}
				System.out.println(roomVertices[i]);
			}
		
		// fileReading importing functions from fileReader class
		int roomCount = fr.getLineCount();
		String roomNames[] = new String[roomCount];
		
		for (int i = 0; i < roomCount; i ++){
			// print just for testing purposes
			//System.out.println(fr.readMe()[i]);
			roomNames[i] = fr.readMe()[i];
		}
		
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("file.txt"));
			out.write("{\"floors\":[");
			out.write("{\"features\":[");
			for(int i = 0; i < roomVertices.length; i++)
			{
				out.write("{");
				out.write("{\"bounds\":[");
				out.write(roomVertices[i]);
				out.write("],");
				out.write("\"connections\":[],");
				out.write("\"name\": ");
				// conditional, since not all rooms are marked with a number or name
				if(i < roomCount){
					out.write(roomNames[i]);
				}
				out.write(",");
				out.write("\"type\": \"Room\"");
				out.write("}");
				if(i < roomVertices.length - 1)
				{
					out.write(",");
				}				
			}

			
			
			out.close();
		}
		catch (IOException e) {}
	}
	
	 /**
	  * Use the water level of the watershed algorithm to determine the colour of the output pixel for debugging purposes
	  * 
	  * @param level the water level of the pixel
	  * 
	  * @return returns RGB Colour
	  */ 
	
	static Color GetWaterColour(int level)
	{
		level *= 6;
		int Red = (level << 16) & 0x00FF0000;
		int Gre = (level << 8) & 0x0000FF00;
		int Blu = (level) & 0x000000FF;
		
		return new Color(0xFF000000 | Red | Gre | Blu);
	}
	
	 /**
	  * Draw a line between two connected vertices
	  * 
	  * @param v1 the vfirst vertex
	  * @param v2 the second vertex
	  * @param image the output buffered image
	  * @param g the Graphics2D used for drawing
	  * 
	  * @return returns RGB Colour
	  */ 
	static void DrawLine(Vertex v1, Vertex v2, BufferedImage image, Graphics2D g)
	{
		if(v1.neighbors.size() != 2)
		{
			System.out.println("--------------------");

			for(int i = 0; i < v1.neighbors.size(); i++)
			{
				System.out.println(v1.neighbors.get(i));
			}
		}
		else
		{		
			g.setBackground(Color.WHITE);
			g.setColor(Color.gray);
			BasicStroke bs = new BasicStroke(1);
			g.setStroke(bs);
			
			g.drawLine(v1.x, v1.y, v2.x, v2.y);
		}
	}
	
	//RESET
	 /**
	  * Determine whether or not a pixel is part of the outline
	  * 
	  * @param x the x coord
	  * @param y the y coord
	  * 
	  * @return returns RGB Colour
	  */ 
	static boolean isOnEdge(int x, int y)
	{
		return pixelArray[x][y] == PixelType.Outline;
	}
	
	 /**
	  * Legacy function
	  * 
	  * @return nothing
	  */
	static void VertexSet()
	{	
		for(int x = 0; x < pixelArray.length; x++)
		{
			for(int y = 0; y < pixelArray[x].length; y++)
			{
				if(pixelArray[x][y] == PixelType.Outline) {
					//VertexInACircle(x, y);
				}
			}				
		}
	}
	
	//DOORS
	 /**
	  * Determine which vertices are part of a door
	  * 
	  * @return returns void
	  */
	static void DoorFlooder()
	{
		int doorFloodSize = 15;
		int doorInsetSize = 10;
		int doorProtrusionAmount = 5;
		

		for(int i = 0; i < vertices.size(); i++)
		{
			Vertex vNow = vertices.get(i);
			if(pixelArray[vNow.x][vNow.y] == PixelType.DoorVertex)
			{
				for(int x = -1; x <= 1; x++)
				{
					for(int y = -1; y <= 1; y++)
					{
						if(pixelArray[vNow.x + x][vNow.y + y] == PixelType.Checked)
						{
							pixelArray[vNow.x + x][vNow.y + y] = PixelType.DoorOverflow;
						}
					}
				}
				
				float totalRelativeX = 0;
				float totalRelativeY = 0;	
				float totalPixelsChecked = 0;
				float totalPixelsAvailable = doorFloodSize * 2 * doorFloodSize * 2;
				int maxDistFromOriginX = 0;
				int maxDistFromOriginY = 0;
				
				for(int j = 0; j < doorFloodSize; j++)
				{
					for(int x = vNow.x - doorFloodSize; x < vNow.x + doorFloodSize; x++)
					{
						for(int y = vNow.y - doorFloodSize; y < vNow.y + doorFloodSize; y++)
						{
							for(int xN = -1; xN <= 1; xN++)
							{
								for(int yN = -1; yN <= 1; yN++)
								{
									if(pixelArray[x + xN][y + yN] == PixelType.DoorOverflow && pixelArray[x][y] == PixelType.Checked)
									{
										pixelArray[x][y] = PixelType.DoorOverflow;
										totalRelativeX += x - vNow.x;
										totalRelativeY += y - vNow.y;	
										int distFromOrgNowX = Math.abs(x - vNow.x);
										int distFromOrgNowY = Math.abs(y - vNow.y);
										
										if(distFromOrgNowX > maxDistFromOriginX)
										{
											maxDistFromOriginX = distFromOrgNowX;
										}
										if(distFromOrgNowY > maxDistFromOriginY)
										{
											maxDistFromOriginY = distFromOrgNowY;
										}										
									}
								}
							}
						}
					}
				}		
				
				for(int x = vNow.x - doorFloodSize; x < vNow.x + doorFloodSize; x++)
				{
					for(int y = vNow.y - doorFloodSize; y < vNow.y + doorFloodSize; y++)
					{
						if(pixelArray[x][y] == PixelType.DoorOverflow)
						{
							pixelArray[x][y] = PixelType.Checked;
							totalPixelsChecked++;
						}
					}
				}
				
				
				float minChecked = totalPixelsAvailable * 0.2f;
				float minFromOrg = 11;
				
				//System.out.println("totC " + totalPixelsChecked);
				//System.out.println("minC " + minChecked);
				//System.out.println("minOX " + maxDistFromOriginX);
				//System.out.println("minOY " + maxDistFromOriginY);
				
				if(totalPixelsChecked > minChecked &&
						maxDistFromOriginY	>= minFromOrg &&
						maxDistFromOriginX	>= minFromOrg 
						)
				{		
					//System.out.println("We;re IN!!!!!!!!!!)");
					boolean shouldAddInsert = false;
					
					totalRelativeX/=totalPixelsChecked;
					totalRelativeY/=totalPixelsChecked;

					float minRelativeAmount = 3.0f;
					
					if(Math.abs(totalRelativeX) > minRelativeAmount 
					&& Math.abs(totalRelativeY) > minRelativeAmount
					)
					{
						shouldAddInsert = true;
					}
					
					boolean slideRight = true;
					if(Math.abs(totalRelativeX) >= Math.abs(totalRelativeY))
					{
						slideRight = false;
					}
					
					if(totalRelativeX > 0){
						totalRelativeX = 1;
					}
					else if(totalRelativeX < 0){
						totalRelativeX = -1;
					}
					
					if(totalRelativeY > 0){
						totalRelativeY = 1;
					}
					else if(totalRelativeY < 0)
					{
						totalRelativeY = -1;
					}
					
					
					if(shouldAddInsert)
					{						
						int doorInsetCenterX = (int)(vNow.x + totalRelativeX*doorInsetSize);
						int doorInsetCenterY = (int)(vNow.y + totalRelativeY*doorInsetSize);
						
						if(!slideRight)
						{
							doorInsetCenterX -= doorProtrusionAmount * totalRelativeX;
						}
						else
						{
							doorInsetCenterY -= doorProtrusionAmount * totalRelativeY;
						}
						
						
						for(int x = doorInsetCenterX - doorInsetSize; x < doorInsetCenterX + doorInsetSize; x++)
						{
							for(int y = doorInsetCenterY - doorInsetSize; y < doorInsetCenterY + doorInsetSize; y++)
							{
								pixelArray[x][y] = PixelType.DoorFinal;
							}
						}
						
						Door doorNow = new Door();
						doorNow.x = doorInsetCenterX;
						doorNow.y = doorInsetCenterY;
						doors.add(doorNow);
					}
				}
			}
		}
	}

	 /**
	  * Start the algorithm which finds door vertices
	  * 
	  * 
	  * @return returns void
	  */
	static void ActuallyFindTheDoors()
	{
		int doorCheckRadius = 10;
		int maxWaterLevelForDoor = 170;
		int minVertsRequiredInRadius = 8;
		for(int i = 0; i < vertices.size(); i++)
		{
			Vertex vNow = vertices.get(i);
			
			for(int k = -1; k <= 1; k++)
			{
				for(int j = -1; j <= 1; j++)
				{
					if(pixelArray[vNow.x+k][vNow.y+j] == PixelType.Checked)
					{
						pixelArray[vNow.x+k][vNow.y+j] = PixelType.WaterProbing;
					}
				}						
			}
			
			int localWaterLevel = GetLocalWaterLevel(doorCheckRadius, doorCheckRadius, vNow.x, vNow.y);
			//System.out.println(localWaterLevel);
			
			if(localWaterLevel < maxWaterLevelForDoor)
			{
				int localVerts = 0;
				for(int x = vNow.x - doorCheckRadius; x < vNow.x + doorCheckRadius; x++)
				{
					for(int y = vNow.y - doorCheckRadius; y < vNow.y + doorCheckRadius; y++)
					{
						for(int k = -1; k <= 1; k++)
						{
							for(int j = -1; j <= 1; j++)
							{
								if(pixelArray[x+k][y+j] == PixelType.Vertex)
								{
									localVerts++;
								}
							}						
						}
					}
				}
				if(localVerts > minVertsRequiredInRadius)
				{
					
					pixelArray[vNow.x][vNow.y] = PixelType.DoorVertex;
				}
			}
		}
		
		for(int x = 0; x < pixelArray.length; x++)
		{
			for(int y = 0; y < pixelArray[x].length; y++)
			{
				if(pixelArray[x][y] == PixelType.WaterProbed)
				{
					pixelArray[x][y] = PixelType.Checked;
				}
			}
		}
		
		DoorFlooder();
	}
	
	//WATERSHED
	
	
	 /**
	  * Extract local distance from vertices using watershed algorithm
	  * 
	  * @param waterLevels total amount expected
	  * @param waterRadius maximum radius of the affected area
	  * @param initX the initial X coordinate of the vertex we are checking
	  * @param initY the initial Y coordinate of the vertex we are checking

	  * 
	  * @return returns void
	  */
	static int GetLocalWaterLevel(int waterLevels, int waterRadius, int initX, int initY)
	{
		int result = 0;
		int numberConverted = 0;
		float minNumberToConvert = (waterLevels * waterLevels) / (1.5f); 
		
		while(waterLevels > 0)
		{
			for(int x = initX - waterRadius; x < initX + waterRadius; x++)
			{
				for(int y = initY - waterRadius; y < initY + waterRadius; y++)
				{
					if(pixelArray[x][y] == PixelType.Checked)
					{
						for(int i = -1; i <= 1; i++)
						{
							for(int j = -1; j <= 1; j++)
							{
								if(pixelArray[x+i][y+j] == PixelType.WaterProbed)
								{
									pixelArray[x][y] = PixelType.WaterProbing;
								}
							}						
						}
					}
				}
			}
			
			for(int x = initX - waterRadius; x < initX + waterRadius; x++)
			{
				for(int y = initY - waterRadius; y < initY + waterRadius; y++)
				{
					if(pixelArray[x][y] == PixelType.WaterProbing)
					{
						pixelArray[x][y] = PixelType.WaterProbed;
						result += waterLevel[x][y];
						numberConverted++;
					}
				}
			}
			
			//System.out.println(waterLevels);
			waterLevels--;
		}
		
		for(int x = initX - waterRadius; x < initX + waterRadius; x++)
		{
			for(int y = initY - waterRadius; y < initY + waterRadius; y++)
			{
				if(pixelArray[x][y] == PixelType.WaterProbed)
				{
					pixelArray[x][y] = PixelType.Checked;
				}
			}
		}
		
		
		if(numberConverted > minNumberToConvert)
		{
			return result;
		}
		else
		{
			return 1000;
		}
	}
	
	 /**
	  * See if we can flood a vertex on this iteration of the watershed algorithm, and if so, change it's level
	  * 
	  * @param xN the initial X coordinate of the vertex we are checking
	  * @param xY the initial Y coordinate of the vertex we are checking
	  * @param level the current water level we are looking to be adjacent to

	  * 
	  * @return returns void
	  */
	static boolean AdjacentOfLevel(int xN, int yN, int level)
	{
		for(int x = -1; x <= 1; x++)
		{
			for(int y = -1; y <= 1; y++)
			{
				if(!(x ==0 && y ==0))
				{
					if(waterLevel[xN+x][yN+y] == level)
					{
						return true;
					}
				}
			}		
		}
		return false;
	}
	
	 /**
	  * Start of the door finding process. Initiates watershed
	  * 
	  * 
	  * @return returns void
	  */
	static void Doors()
	{
		for(int x = 0; x < pixelArray.length; x++)
		{
			for(int y = 0; y < pixelArray[x].length; y++)
			{
				if(pixelArray[x][y] == PixelType.Checked)
				{
					waterLevel[x][y] = -2;
				}
				else
				{
					waterLevel[x][y] = -1;
				}
			}
		}
		
		boolean foundClear = true;
		int currentLevel = 0;
		while (foundClear)
		{
			foundClear = false;
			
			for(int x = 1; x < pixelArray.length-1; x++)
			{
				for(int y = 1; y < pixelArray[x].length-1; y++)
				{
					if(waterLevel[x][y] == -2)
					{
						if(AdjacentOfLevel(x,y,currentLevel-1))
						{
							waterLevel[x][y] = currentLevel;
							foundClear = true;
						}
					}
				}
			}
			currentLevel ++;
		}
		
		ActuallyFindTheDoors();
	}

	//START VERTEX STUFF	
	static int doorSize = 10;
	
	 /**
	  * Get distance between two points.
	  * 
	  * @param x1
	  * @param y1
	  * @param x2
	  * @param y2
	  * 
	  * @return returns void
	  */
	static float Vec2Distance(int x1, int y1, int x2, int y2)
	{
		int xDist = Math.abs(x2 - x1);
		int yDist = Math.abs(y2 - y1);
		return (float) Math.sqrt(xDist*xDist + yDist*yDist);
	}
	
	 /**
	  * Returns the vertex at a given x y coordinate
	  * 
	  * @param x the x position to check
	  * @param y the y position to check
	  * 
	  * @return returns Vertex
	  */
	static Vertex VertexAt (int x, int y)
	{
		for(int i = 0; i < vertices.size(); i++)
		{
			if(vertices.get(i).x == x && vertices.get(i).y == y)
			{
				return vertices.get(i);
			}			
		}		
		return null;
	}
	
	
	static List<Vertex> vertices = new ArrayList<>();
	static List<Vertex> vertexSet = new ArrayList<>();
	static List<Door> doors = new ArrayList<>();


	static Vertex firstVert;
	static Vertex lastVert;
	
	 /**
	  * Returns whether a pixel is part of the outline
	  * 
	  * @param x position to check
	  * @param y position to check
	  * 
	  * @return returns boolean
	  */
	static boolean isOut(int x, int y)
	{
		return pixelArray[x][y] == PixelType.Outline || pixelArray[x][y] == PixelType.Vertex || pixelArray[x][y] == PixelType.Probed;
	}
	 /**
	  * Returns whether or not a pixel is on a corner
	  * 
	  * @param x position to check
	  * @param y position to check
	  * 
	  * @return returns boolean
	  */
	static boolean isCornerVert(int x, int y)
	{
		int joiningDirections = 0;
		if(isOut(x+1,y) || isOut(x-1,y))
		{
			joiningDirections++;
		}
		if(isOut(x,y+1) || isOut(x,y-1))
		{
			joiningDirections++;
		}
		if(isOut(x+1,y+1) || isOut(x-1,y-1))
		{
			joiningDirections++;
		}
		if(isOut(x+1,y-1) || isOut(x-1,y+1))
		{
			joiningDirections++;
		}
				
		return joiningDirections >= 2;
	}
	
	 /**
	  * Iterates around the outline, adding vertices and connecting them.  Recursivly calls the next vertex
	  * 
	  * @param x position to check now
	  * @param y position to check now
	  * @param the last direction we traveled in
	  * @param the last vertex we added (so we can connect it)
	  * 
	  * @return returns void
	  */
	//0 = N, 1 = NE, 2 = E, etc
	static void VectorizeLoop(int x, int y, int prevDirection, Vertex lastVertex)
	{
		pixelArray[x][y] = PixelType.Probed;	
		
		if(isCornerVert(x,y))
		{
			pixelArray[x][y] = PixelType.Vertex;

			Vertex nextVertex =  new Vertex();
			nextVertex.x = x;
			nextVertex.y = y;
			vertices.add(nextVertex);
			
			if(lastVertex != null)
			{
				if(nextVertex.neighbors.size() < 2)
					nextVertex.neighbors.add(lastVertex);
				if(lastVertex.neighbors.size() < 2)
					lastVertex.neighbors.add(nextVertex);
			}
			else
			{
				firstVert = nextVertex;
			}
			lastVertex = nextVertex;
			lastVert = lastVertex;
		}
		
		for(int xNext = -1; xNext <= 1; xNext++)
		{
			for(int yNext = -1; yNext <= 1; yNext++)
			{
				if(!(xNext == 0 && yNext == 0))
				{
					if(pixelArray[x+xNext][y+yNext] == PixelType.Outline)
					{
						VectorizeLoop(x+xNext, y+yNext, 0, lastVertex);
					}
					else if(pixelArray[x+xNext][y+yNext] == PixelType.Vertex)
					{
						Vertex nV = VertexAt(x+xNext, y+yNext);
						if(nV != lastVertex)
						{
							if(nV.neighbors.size() == 1)
							{
								if(nV.neighbors.size() < 2)
									nV.neighbors.add(lastVertex);
								if(lastVertex.neighbors.size() < 2)
									lastVertex.neighbors.add(nV);
							}
						}
					}
				}
			}
		}
	}
	
	static List<Integer> indicesAtEndOfRoom = new ArrayList<>();
	static List<Room> rooms = new ArrayList<>();
	static int numberOfRooms;
	
	 /**
	  * Newest vectorization algorithm.  Starts the process by finding unvectorized outlines
	  * 
	  * 
	  * @return returns void
	  */
	
	static void NewVectorize()
	{
		numberOfRooms = 0;
		for(int x = 0; x < pixelArray.length; x++)
		{
			for(int y = 0; y < pixelArray[x].length; y++)
			{
				if(pixelArray[x][y] == PixelType.Outline)
				{
					//lastVert = null;
					//firstVert = null;
					int sizeBefore = vertices.size();
					VectorizeLoop(x,y, -1, null);
					if(lastVert!= null && firstVert != null)
					{
						//firstVert.neighbors.add(lastVert);
						//lastVert.neighbors.add(firstVert);
					}
					indicesAtEndOfRoom.add(vertices.size() - sizeBefore);
					numberOfRooms++;
					System.out.println("ITERATION!: " + numberOfRooms);
					System.out.println("SIZE!: " + indicesAtEndOfRoom.get(numberOfRooms-1));
					Room rNew = new Room();
					for(int i = sizeBefore; i < vertices.size(); i++)
					{
						rNew.vertices.add(vertices.get(i));
					}
					rooms.add(rNew);
				}
			}
		}
	}
	
	//END NEW VECTORIZE
	 /**
	  * Maintains legacy compatability.
	  * 
	  * @return returns void
	  */
	static void Vectorize()
	{
		NewVectorize();
	}
	
	//END VERTEX STUFF
	 /**
	  * Expands the radius of filled in areas
	  * 
	  * @param xN x coordinate to check now
	  * @param yN y coordinate to check now
	  * 
	  * @return returns void
	  */
	static void ExpandOutline(int xN, int yN)
	{
		for(int x = -1; x <= 1; x++)
		{
			for(int y = -1; y <= 1; y++)
			{
				if(! (x == 0 && y == 0))
				{
					int xNew = x + xN;
					int yNew = x + yN;
					if(pixelArray[xNew][yNew] == PixelType.Black)
					{
						pixelArray[xNew][yNew] = PixelType.ProbedVertex;
					}
				}
			}
		}
	}
	
	 /**
	  * Implements non-recursive flood fill.  When it can't progress further, mark as outline pixel
	  * 
	  * 
	  * @return returns void
	  */
	static void FloodFillOutline()
	{
		boolean shouldContinue = true;
		while(shouldContinue)
		{
			shouldContinue = false;
			for(int x = 0; x < pixelArray.length; x++)
			{
				for(int y = 0; y < pixelArray[x].length; y++)
				{
					if(pixelArray[x][y] == PixelType.ToCheck)
					{
						CheckAPixel(x, y);
						shouldContinue = true;
					}
				}
			}
		}					
	}
	
	 /**
	  * Checks whether or not a pixel is part of the outline or now
	  * 
	  * @param x x coordinate of th pixel
	  * @param y coordinate of the pixel
	  * 
	  * @return returns void
	  */
	static void CheckAPixel(int x, int y)
	{
		pixelArray[x][y] = PixelType.Checked;

		for(int xPlus = -1; xPlus <= 1; xPlus++)
		{
			for(int yPlus = -1; yPlus <= 1; yPlus++)
			{
				if((xPlus != 0 || yPlus != 0) && (xPlus == 0 || yPlus == 0))
				{
					int nextX = x + xPlus;
					int nextY = y + yPlus;
					if(pixelArray[nextX][nextY] == PixelType.Black)
					{
						pixelArray[x][y] = PixelType.Outline;
					}
					else if(pixelArray[nextX][nextY] == PixelType.White)
					{
						pixelArray[nextX][nextY] = PixelType.ToCheck;
					}
				}
			}			
		}
	}
		
	 /**
	  * Returns the vertex at a given x y coordinate
	  * 
	  * @param waterLevels total amount expected
	  * 
	  * @return returns void
	  */
	
	
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


