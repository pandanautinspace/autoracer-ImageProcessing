import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;
import java.awt.image.WritableRaster;
import java.lang.Math;
//TODO comment code

class Counter{
    public static void main(String[] args){
        //Loading image
        File imageFile = new File("pic.PNG"); 
        try{
            BufferedImage img = ImageIO.read(imageFile);
            int width = img.getWidth(); int height = img.getHeight();
            int[] t = getPixelArray(img, width, height); //Turning image into array of RGB pixels (dec)
            t = blurImage(t, width, height); //blurring image
            exportImage(t, width, height); // exporting image
        } catch(IOException e) {
            System.out.println("errorito " + e);
        }
    }

    

    static int[] getPixelArray(BufferedImage img, int width, int height){

            System.out.println("image found");

            int[] pixels = new int[width * height];
            int k = 0;
            for(int i=0; i<width; i++){
                for(int j=0; j<height; j++){
                    pixels[k] = img.getRGB(i,j);
                    k++;
                }
            }

            return pixels;
    }

    static int[] blurImage(int[] pixels, int width, int height) {
        int radius = 10;
        int[] blurredPixels = new int[pixels.length];
        System.arraycopy(pixels, 0, blurredPixels, 0, blurredPixels.length);
        
        BBHoriz(pixels, blurredPixels, width, height, radius);
        return blurredPixels;
    }

    static void BBHoriz(int[] pixels, int[] blurredPixels, int width, int height, int radius){
        int a = 1 / (radius + radius + 1);
        for(int i = 0; i < height; i++){
            int pindex = i * width; // index of the current pixel, will change
            int fpindex = pindex; // index o/f first pixel
            int lpindex = pindex + radius; //index of last pixel

            int firstPixel = pixels[pindex];
            int lastPixel = pixels[lpindex];

            int val = (radius + 1) * firstPixel; //create accumulator with overflow pixels

            for(int j = 0; j <= radius; j++){
                val += pixels[pindex + j];
            }

            for(int j = 0; j <= radius; j++){
                val += pixels[lpindex++] - firstPixel;
                blurredPixels[pindex++] = Math.round(val * a);
            }

            for(int j = width - radius; j < width; j++){
                val += lastPixel - pixels[fpindex++];
                blurredPixels[pindex++] = Math.round(val * a);
            }
        }
    }

    static void BBVert(int[] pixels, int[] blurredPixels, int width, int height, int radius){
        int a = 1 / (radius + radius + 1);
        for(int i = 0; i < width; i++){
            int pindex = i * width; // index of the current pixel, will change
            int fpindex = pindex; // index o/f first pixel
            int lpindex = pindex + radius; //index of last pixel

            int firstPixel = pixels[pindex];
            int lastPixel = pixels[lpindex];

            int val = (radius + 1) * firstPixel; //create accumulator with overflow pixels

            for(int j = 0; j <= radius; j++){
                val += pixels[pindex + j];
            }

            for(int j = 0; j <= radius; j++){
                val += pixels[lpindex++] - firstPixel;
                blurredPixels[pindex++] = Math.round(val * a);
            }

            for(int j = width - radius; j < width; j++){
                val += lastPixel - pixels[fpindex++];
                blurredPixels[pindex++] = Math.round(val * a);
            }
        }
    }
    
    static void exportImage(int[] pixels, int width, int height){
        try{
            BufferedImage finImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            
            int k = 0;
            for(int i = 0; i<width; i++){
                for(int j = 0; j<height; j++){
                    finImg.setRGB(i, j, pixels[k]);
                    k++;
                }
            }
            
            ImageIO.write(finImg, "PNG", new File("output.PNG"));
            System.out.println("Output file created");
        } catch(IOException e) {
            System.out.println(e);
        }
    }
}