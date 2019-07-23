import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.lang.Math;
import java.io.File;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

class PTest {
    static final int[] ColorArr = {
        0xFF0000, //RED     0
        0x00FF00, //GREEN   1
        0x0000FF, //BLUE    2
        0x00FFFF, //CYAN    3
        0xFF00FF, //MAGENTA 4
        0xFFFF00, //YELLOW  5
        0x000000, //BLACK   6
        0x333333, //GREY1   7
        0x666666, //GREY2   8
        0x999999, //GREY3   9
        0xCCCCCC, //GREY4   10
        0xFFFFFF  //WHITE   11
    };

    static String[] files = {
        "vroom1.png",
        "vroom2.png",
        "vroom3.png",
        "vroom4.png",
        "vroom5.png",
        "vroom6.png",
        "vroom7.png",
        "lightvroom1.png",
        "lightvroom2.png",
        "lightvroom3.png",
        "lightvroom4.png",
        "carvroom1.png",
        "carvroom2.png",
        "carvroom3.png",
        "carvroom4.png",
        "carvroom5.png"
    };


    public static void main(String[] args) {
        for(int p = 0; p < files.length; p++){
            File imageFile = new File("walls/" + files[p]); //special line
            //System.out.println("Loading file " + files[p]);

            try {
                BufferedImage image = ImageIO.read(imageFile);
                int width = image.getWidth(); int height = image.getHeight();
                int[] rgbs = new int[width * height];
                int[] rgbs2 = new int[width * height];
                image.getRGB(0,0,image.getWidth(),image.getHeight(), rgbs, 0, image.getWidth());
            
                long startTime = System.currentTimeMillis();
                posterizeImageInt(rgbs, rgbs2, 65);
                

                int[][] a = printColors2(rgbs2, height, width);
                // for(int i : a[0]) System.out.print(i + " ");
                // System.out.println();
                // for(int i : a[1]) System.out.print(i + " ");
                fillEmptySpaces(a, width, rgbs2);

                long endTime = System.currentTimeMillis();
                // System.out.println("-------------------------------------------------------------------------");
                // for(int i : a[0]) System.out.print(i + " ");
                // System.out.println();
                // for(int i : a[1]) System.out.print(i + " ");

                for(int i = 0; i < a[0].length; i++){
                    if(a[0][i] != 0){
                        for(int k = a[1][i]; k < a[0][i]; k++){
                           drawPixel(rgbs2, i, k, width, 1);
                        }
                        
                        drawPixel(rgbs2, i, a[1][i], width, 3);
                        drawPixel(rgbs2, i, a[0][i], width, 4);
                    }
                }

                // System.out.print("\n{");
                // for(int j = 0; j < width - 1; j++){
                //     System.out.print(a[0][j] + ", ");
                // }
                // System.out.print("\n{");
                // for(int j = 0; j < width - 1; j++){
                //     System.out.print(a[1][j] + ", ");
                // }
                
                System.out.println("That took " + (endTime - startTime) + " milliseconds!!");

                colorArrToCode(rgbs2);
         

                BufferedImage outImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
                File out = new File("output/out" + p + ".png"); 
                outImage.setRGB(0, 0, outImage.getWidth(), outImage.getHeight(), rgbs2, 0, outImage.getWidth());
                ImageIO.write(outImage, "png", out);
            } catch (IOException e) {
                System.out.println("Java Sucks because " + e);
            }
        }
    }

    static void fillEmptySpaces(int[][] arr, int width, int[] a){
        int x1 = 0; int x2 = 0; int y1 = 0; int y2 = 0;
        for(int i = 1; i < arr[0].length - 2; i++){
            if(arr[0][i] == 0){
                if(arr[0][i-1] != 0){
                    x1 = i-1;
                    y1 = arr[0][i-1];
                    int j = i + 1;
                    while(arr[0][j] == 0 && j < arr[0].length - 1){
                        j++;
                    }
                    x2 = j;
                    y2 = arr[0][j];

                    for(int k = x1; k < x2; k++){
                        arr[0][k] = ((y1-y2)/(x1-x2)) * (k - x1) + y1;
                    }   
                    
                    y1 = arr[1][i-1];
                    y2 = arr[1][j];

                    for(int k = x1; k < x2; k++){
                        arr[1][k] = ((y1-y2)/(x1-x2)) * (k - x1) + y1;
                    }   
                    i = x2;
                    drawPixel(a, x1, y1, width, 3);
                    //System.out.println("x1, y1 =" + x1 + " " + y1 + " x2, y2 =" + x2 + " " + y2);

                }
            }
        }
    }

    static int[][] printColors2(int[] arr, int height, int width){
        int[] wallBottoms = new int[width];
        int[] wallTops = new int[width];
        for(int i = 0; i < width; i++){
            int currColor = 0;
            int currTop = -1;
            for(int j = 0; j < height; j++){
                if((arr[j*width + i] == 10 || arr[j*width + i] == 11) && currTop == -1){
                    currTop = j;
                }else if((arr[j*width + i] == currColor || arr[j*width + i] == currColor + 1 || arr[j*width + i] == currColor -1) && currTop != -1){
                    if(arr[j*width + i] == 6){
                        wallTops[i] = currTop;
                        wallBottoms[i] = j;
                        
                        break;
                    }
                }else{
                    currTop = -1;
                }
                currColor = arr[j*width + i];
            }
        }
        int[][] out = {wallBottoms, wallTops};
        return out;
    }

    static void printColors(int[] arr, int height, int width){
        int bottom = 0; int curr;
        int[] bottomArr = new int[width]; int[] topArr = new int[width];
        
        for(int x = 0; x < width; x++) {
            int state = 0;
            System.out.println("Now testing x = " + x);
            
            bottomArr[x] = -1; topArr[x] = -1;

            //STATES = 7 8 9 10 9 8
            for(int y = height - 1; y > 0; y--) {
                curr = y * width + x;
                if(state == 0) {
                    if(arr[curr] == 7) {
                        // System.out.println("entering fsm with color " + arr[curr] + "at y=" + y);
                        state = 1; //enter fsm
                        bottom = y;
                    }
                } else if(state == 1) {
                    if(arr[curr] == 8) {
                        // System.out.println("entering state 2 with color " + arr[curr]);
                        state = 2;
                    } else if(arr[curr] !=7) {
                        // System.out.println("exiting state 1 with color " + arr[curr]);
                        state = 0; //exit fsm
                    }
                } else if(state == 2) {
                    if(arr[curr] == 9) {
                        // System.out.println("entering state 3 with color " + arr[curr]);
                        state = 3;
                    } else if(arr[curr] !=8) {
                        // System.out.println("exiting state 2 with color " + arr[curr]);
                        state = 0; //exit fsm
                    }
                } else if(state == 3) {
                    if(arr[curr] == 10) {
                        // System.out.println("entering state 4 with color " + arr[curr]);
                        state = 4;
                    } else if(arr[curr] == 8) {
                        // System.out.println("going back to state 2 with color " + arr[curr]);
                        state = 2;
                    } else if(arr[curr] !=9) {
                        //System.out.println("exiting state 3 with color " + arr[curr]);
                        state = 0; //exit fsm
                    }
                } else if(state == 4) {
                    if(arr[curr] == 9) {
                        state = 5;
                        //System.out.println("entering state 5 with color " + arr[curr]);
                    } else if(arr[curr] !=10 && arr[curr] != 11) {
                        //System.out.println("exiting state 4 with color " + arr[curr]);
                        state = 0; //exit fsm
                    }
                } else if(state == 5) {
                    if(arr[curr] == 10) {
                        //System.out.println("entering state 6 with color " + arr[curr]);
                        state = 6;
                    } else if(arr[curr] != 9) {
                        //System.out.println("exiting state 5 with color " + arr[curr]);
                        state = 0; //exit fsm
                    }
                } else if(state == 6){
                    if(arr[curr] != 11 && arr[curr] != 10) {
                        //return result!
                        bottomArr[x] = bottom;
                        topArr[x] = y;
                        
                        System.out.println("Found wall for x=" + x +" at t=" + y + " b=" + bottom + " with state " + state);
                        state = 0;
                    }
                } else {
                    System.out.println("How did I get here?");
                }
                //System.out.println("color:" + arr[curr] + "y:" + y);
            }
            if(topArr[x] != -1 && bottomArr[x] != -1){
                drawPixel(arr, x, topArr[x], width, 4);
                drawPixel(arr, x, bottomArr[x], width, 3);
            }
        }
    }

    static void colorArrToCode(int[] a1){
        for(int i=0; i<a1.length; i++) {
            a1[i] = ColorArr[a1[i]];
        }
    }

    static int posterizePixelInt(int rgb, int dt) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = (rgb) & 0xFF;
        int rg = red - green;
        int rb = red - blue;
        int bg = blue - green;
        if(rg > dt && rb > dt) {
            return 0;
        } else if (rg < -dt && bg < -dt) {
            return 1;
        } else if (rb < -dt && bg > dt) {
            return 2;
        } else if (rg < dt && rg > -dt && rb > dt && bg < -dt) {
            return 5;
        } else if (rb < dt && rb > -dt && rg > dt && bg > dt) {
            return 4;
        } else if (bg < dt && bg > -dt && rg < -dt && rb < -dt ) {
            return 3;
        } else {
            int avg = (red + green + blue + green) >> 2;
            if(avg < 25) {
                return 6;
            } else if(avg < 76) {
                return 7;
            } else if(avg < 127) {
                return 8;
            } else if(avg < 178) {
                return 9;
            } else if(avg < 229) {
                return 10;
            } else {
                return 11;
            }

        }
    }

    static void drawPixel(int[] arr, int x, int y, int width, int color){
        arr[(y * width) + x] = color;
    }

    static void posterizeImageInt(int[] rgbArray, int[] outArray, int diffThreshold) {
        for(int i = rgbArray.length - 1; i >= 0; i --) {
            outArray[i] = posterizePixelInt(rgbArray[i], diffThreshold);
        }
    }
}
