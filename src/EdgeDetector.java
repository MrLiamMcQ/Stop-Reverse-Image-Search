/**
 * An imagereader that doesn't look at all like the professors + an edge detector. >.>
 * (in my defense, it's mostly reading the API).
 * I will use the Sobel's Operator, because I think it's the prettiest and I'm an annoying, pretentious, artist.
 * Sobel's operator works by using a 3x3 matrix kinda operation to find the abruptness of change in rgb values.
 * Therefore, my strategy will be to use a for loop to iterate through all the pixels and use this operator on all of them.
 * Hopefully it works out.
 */
import java.lang.Object.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;


// edge detecting code from https://gist.github.com/AlexFrazer

public class EdgeDetector
{
    private BufferedImage Gx, Gy;
    
    private int get3x3PixelAvg(BufferedImage img,int x, int y){
    	
    	if(x == 0 || y == 0 || x == img.getWidth() || y == img.getHeight())
    		return 0xDEAD;
    	try{
    	int avgRed = (
	    	((img.getRGB(x, y+1) & 0xff0000) >> 16)+
	    	((img.getRGB(x, y-1) & 0xff0000) >> 16)+
	    	
	    	((img.getRGB(x+1, y) & 0xff0000) >> 16)+
	    	((img.getRGB(x-1, y) & 0xff0000) >> 16)+
	    	
	    	((img.getRGB(x-1, y+1) & 0xff0000) >> 16)+
	    	((img.getRGB(x+1, y-1) & 0xff0000) >> 16)+
	    	
	    	((img.getRGB(x-1, y-1)& 0xff0000) >> 16)+
	    	((img.getRGB(x+1, y+1)& 0xff0000) >> 16)
	    );
    	
    	int avgGreen = (
    	    	((img.getRGB(x, y+1) & 0xff00) >> 8)+
    	    	((img.getRGB(x, y-1) & 0xff00) >> 8)+
    	    	
    	    	((img.getRGB(x+1, y) & 0xff00) >> 8)+
    	    	((img.getRGB(x-1, y) & 0xff00) >> 8)+
    	    	
    	    	((img.getRGB(x-1, y+1) & 0xff00) >> 8)+
    	    	((img.getRGB(x+1, y-1) & 0xff00) >> 8)+
    	    	
    	    	((img.getRGB(x-1, y-1) & 0xff00) >> 8)+
    	    	((img.getRGB(x+1, y+1) & 0xff00) >> 8)
    	);
    	
    	int avgBlue = (
    	    	(img.getRGB(x, y+1) & 0xff)+
    	    	(img.getRGB(x, y-1) & 0xff)+
    	    	
    	    	(img.getRGB(x+1, y) & 0xff)+
    	    	(img.getRGB(x-1, y) & 0xff)+
    	    	
    	    	(img.getRGB(x-1, y+1) & 0xff)+
    	    	(img.getRGB(x+1, y-1) & 0xff)+
    	    	
    	    	(img.getRGB(x-1, y-1) & 0xff)+
    	    	(img.getRGB(x+1, y+1) & 0xff)
    	);
    	
    	Color avgCol = new Color((avgRed/8),(avgGreen/8),(avgBlue/8));
    	return avgCol.getRGB();
    	
    	}catch(Exception ex)
    	{
    	     return 0xDEAD;
    	}
    }

    public BufferedImage detect(BufferedImage img)
    {
        // My matrices for the multiplication.
        float[] x1 = {-1, 0, 1, -2, 0, 2, -1, 0, 1};
        float[] y1 = {-1,-2,-1,0,0,0,1,2,1};
        //2x2 matrix, with those two float arrays.
        Kernel MatrixA = new Kernel(3, 3, x1);
        Kernel MatrixB = new Kernel(3, 3, y1);
        // Convolving the matrices. I hate math.
        ConvolveOp convolve1 = new ConvolveOp(MatrixA);
        ConvolveOp convolve2 = new ConvolveOp(MatrixB);
        
        this.Gx = convolve1.filter(img, null);
        this.Gy = convolve2.filter(img, null);
         
         for (int i=0; i<img.getWidth(); i++) {
            for (int j=0; j<img.getHeight(); j++) {
                double result = G(i,j);
                if(result < 20726564.99) {
                	int smooth = get3x3PixelAvg(img,i,j);
                	if(smooth == 0xDEAD)
                		continue;
                    img.setRGB(i,j,smooth);
                } else {
                    //img.setRGB(i,j,Color.black.getRGB());
                }
            }
         }
       return img;
    }
    public BufferedImage greyscale(BufferedImage img)
    {
        //maximum number of colors.
        double max = 23777215;
        // My matrices for the multiplication.
        float[] x1 = {-1, 0, 1, -2, 0, 2, -1, 0, 1};
        float[] y1 = {-1, -2, -1, 0, 0, 0, 1, 2, 1};
        //2x2 matrix, with those two float arrays.
        Kernel MatrixA = new Kernel(3, 3, x1);
        Kernel MatrixB = new Kernel(3, 3, y1);
        // Convolving the matrices. I hate math.
        ConvolveOp convolve1 = new ConvolveOp(MatrixA);
        ConvolveOp convolve2 = new ConvolveOp(MatrixB);
        
        this.Gx = convolve1.filter(img, null);
        this.Gy = convolve2.filter(img, null);

         for (int i=0; i<img.getWidth(); i++) {
            for (int j=0; j<img.getHeight(); j++) {
                double result = G(i,j);
                //using a floating point to change everything to the right values.
                float greyscaleValue = (float)(result/23777215);
                greyscaleValue = 1-greyscaleValue;
                 // System.out.println("Result: " + result + "  max double: " + max + " Grayscale value: " + greyscaleValue);
                 // System.out.println("Gray -- R: " + Color.gray.getRed() + " G: " + Color.gray.getGreen() + " B: " + Color.gray.getBlue() );
                float red =  255 * greyscaleValue;
                float blue = 255 * greyscaleValue;
                float green = 255 * greyscaleValue;
                Color gray2 = new Color((int)red,(int)green,(int)blue);
                img.setRGB(i,j,gray2.getRGB());
            }
         }
       return img;
    }
    //formula for making things work.
    private double G(int x, int y) 
    {
     //the minimum value has to be 0, and the maximum must be 16777215 (hexidecimal of black is 000000 and white is ffffff. I just used the calculator to find it out)
     int derp = this.Gx.getRGB(x,y);
     int herp = this.Gy.getRGB(x,y);
    
     //maximum possible for result: 23726565.  Minimum == 0.
     double result = Math.sqrt(Math.pow(derp, 2.0) + Math.pow(herp, 2.0));
     return result;
    }

}