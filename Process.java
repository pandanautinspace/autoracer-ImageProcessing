import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Scanner;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.IOException;

class Process {

    static final int[] ColorArr = {
            0xFF0000, //RED     0
            0x00FF00, //GREEN   1
            0x0000FF, //BLUE    2    
            0x00FFFF, //CYAN    3
            0xFF00FF, //MAGENTA 4
            0xFFFF00, //YELLOW  5
            0,        //BLACK   6
            0x333333, //GREY1   7
            0x666666, //GREY2   8
            0x999999, //GREY3   9
            0xCCCCCC, //GREY4   10
            0xFFFFFF  //WHITE   11
    };

    static final int[][] WallColorSeqs = {
        {10,11,10,9,10,9,8,6},
        {10,11,10,9,10,9,8,6},
        {10,11,10,9,8,9,8,9,10,9,8,6},
        {10,9,10,9,8,6},
        {10,9,10,9,8,6},
        {10,11,10,9,10,11,10,9,8,6},
        {10,11,10,9,10,11,10,9,8,6},
        {10,11,10,9,8,9,8,9,10,11,10,9,8,6},
        {10,9,10,11,10,9,8,6},
        {10,9,10,11,10,9,8,6},
        {10,11,10,9,8,9,8,6},
        {10,11,10,9,8,9,10,9,8,6},
        {10,11,10,9,10,9,8,6},
        {10,11,10,9,10,9,8,6},
        {10,11,10,9,8,9,8,9,10,9,8,6},
        {10,9,10,9,8,6},
        {10,9,10,9,8,6},
        {10,11,10,9,10,11,10,11,10,9,8,6},
        {10,11,10,9,10,11,10,11,10,9,8,6},
        {10,11,10,9,8,9,8,9,10,11,10,11,10,9,8,6},
        {10,9,10,11,10,11,10,9,8,6},
        {10,9,10,11,10,11,10,9,8,6},
        {10,11,10,9,8,9,8,6},
        {10,11,10,9,8,9,10,11,10,9,8,6},
        {10,11,10,9,10,9,8,6},
        {10,11,10,9,10,9,8,6},
        {10,11,10,9,8,9,8,9,10,9,8,6},
        {10,9,10,9,10,9,8,6},
        {10,9,10,9,10,9,8,6},
        {10,11,10,9,10,11,10,9,10,9,8,6},
        {10,11,10,9,10,11,10,9,10,9,8,6},
        {10,11,10,9,8,9,8,9,10,11,10,9,10,9,8,6},
        {10,9,10,11,10,9,10,9,8,6},
        {10,9,10,11,10,9,10,9,8,6},
        {10,11,10,9,8,9,10,9,8,6},
        {10,11,10,9,8,9,10,9,10,9,8,6},
        {11,10,9,10,9,8,7},
        {10,11,10,9,10,9,8,7}
    };

    public static void main(String[] args) {
        File[] images = {
            new File ("wow.jpg"),
            new File ("blur.png"),
            new File ("vroom1.png"),
            new File ("vroom2.png"),
            new File ("vroom3.png"),
            new File ("vroom4.png"),
            new File ("vroom5.png"),
            new File ("vroom6.png"),
            new File ("vroom7.png"),
            new File ("vroom8.png"),
            new File ("lightvroom4.png"),
            new File ("lightvroom2.png"),
            new File ("carvroom1.png"),
            new File ("carvroom2.png"),
            new File("babyseaturtle.jpg")
        };
        try {
            int[][] in = new int[images.length][];
            int[][] out = new int[images.length][];
            File[] outFiles = new File[images.length];
            BufferedImage[] outImages = new BufferedImage[images.length];
            int i = 0;
            int[] rgbs;
            int[] newrgbs;
            int[] newerrgbs;
            for(File imageFile : images) {
                System.out.println("Processing " + imageFile.getName());
                BufferedImage image = ImageIO.read(imageFile);
                in[i] = new int[image.getWidth()*image.getHeight()];
                image.getRGB(0,0,image.getWidth(),image.getHeight(), in[i], 0, image.getWidth());
                out[i] = in[i].clone();
                outImages[i] = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
                outFiles[i] = new File("out"+i+".png");
                i++;
            }
            for(int j = 0; j < images.length; j ++) {
                rgbs = in[j % images.length];
                newrgbs = out[j % images.length];
                newerrgbs = out[j % images.length];
                long start_time = System.nanoTime();
                rgbs = fastBoxBlur(rgbs, outImages[j % images.length].getWidth(), outImages[j % images.length].getHeight());
                newrgbs = convertToMono(rgbs);
                posterizeImageInt(rgbs, newerrgbs, 65);
                convertToRobertsCrossRaster(newrgbs, rgbs, outImages[j % images.length].getHeight(), outImages[j % images.length].getWidth());
                int[][] tb = scanImage(newerrgbs, outImages[j % images.length].getWidth(), outImages[j % images.length].getHeight(), WallColorSeqs);
                //writeCSV(heightsToCSV(tb),  j + ".csv");
                // String csv = imageToCSV(newrgbs, outImages[j % images.length].getWidth(), outImages[j % images.length].getHeight());
                // try (PrintStream outCSV = new PrintStream(new FileOutputStream("image" + j  + ".csv"))) {
                //     outCSV.print(csv);
                // }
                //edgeRemoveOutlier(tb, newrgbs);
                fillEmptySpaces(tb);
                codeToRGB(newerrgbs, newerrgbs);
                // for(int k = 0; k < tb[0].length; k++) {
                //     if(tb[0][k] > 0 && tb[1][k] > 0) {
                //         for(int m = tb[1][k]; m < tb[0][k]; m ++) {
                //             newerrgbs[k + tb[0].length * m] = ColorArr[4];
                //         }
                //     }
                // }
                out[j % images.length] = newerrgbs;
                System.out.println();
                long end_time = System.nanoTime();
                double difference = (end_time - start_time) / 1e6;
                System.out.println(difference);
            }
            for(int k = 0; k < images.length; k ++) {
                outImages[k].setRGB(0, 0, outImages[k].getWidth(), outImages[k].getHeight(), out[k], 0, outImages[k].getWidth());
                ImageIO.write(outImages[k], "png", outFiles[k]);
            }
        } catch (IOException e) {
            System.out.println("Java Sux Because " + e);
        }
    }

    static void averageByRadius(int[] inArray, int[] outArray, int radius) {
        int[] combinees = new int[radius];
        for(int i = 0; i < inArray.length; i ++) {
            for(int j = 0; j < radius; j ++ ) {
                try {
                    combinees[j] = inArray[i+j];
                } catch(ArrayIndexOutOfBoundsException e) {
                    combinees[j] = inArray[inArray.length - 1];
                }
            }
            outArray[i] = splitAndCombine(combinees);
        }
    }
    
    static void blurAverage2(int[] inArray, int[] outArray) {
        int sumr = (inArray[0] >> 16 & 0xFF) << 3;
        int sumg = (inArray[0] >> 8 & 0xFF) << 3;
        int sumb = (inArray[0] & 0xFF) << 3;
        for(int i = 0; i < inArray.length; i ++) {
            if(i + 4 < inArray.length) {
                sumr += 0xFF & inArray[i + 4] >> 16;
                sumg += 0xFF & inArray[i + 4] >> 8;
                sumb += 0xFF & inArray[i + 4];
            } else {
                sumr += 0xFF & inArray[inArray.length - 1] >> 16;
                sumg += 0xFF & inArray[inArray.length - 1] >> 8;
                sumb += 0xFF & inArray[inArray.length - 1];
            }
            if(i - 4 >= 0) {
                sumr -= 0xFF & inArray[i - 4] >> 16;
                sumg -= 0xFF & inArray[i - 4] >> 8;
                sumb -= 0xFF & inArray[i - 4]; 
            } else {
                sumr -= 0xFF & inArray[0] >> 16;
                sumg -= 0xFF & inArray[0] >> 8;
                sumb -= 0xFF & inArray[0]; 
            }
            int finr = sumr >> 3;
            int fing = sumg >> 3;
            int finb = sumb >> 3;
            if(fing < 0) {
                 System.out.println((i % 1920) + "," + (i / 1920)); 
                 System.out.println("Ahead Channels: RED " + (0xFF & inArray[i + 4] >> 16) +", GREEN " + (0xFF & inArray[i + 4] >> 8) +", BLUE " + (0xFF & inArray[i + 4]));
                 System.out.println("Behind Channels: RED " + (0xFF & inArray[i - 4] >> 16) +", GREEN " + (0xFF & inArray[i - 4] >> 8) +", BLUE " + (0xFF & inArray[i - 4]));
                 System.out.println("Behind Channels: RED " + (0xFF & inArray[i - 5] >> 16) +", GREEN " + (0xFF & inArray[i - 5] >> 8) +", BLUE " + (0xFF & inArray[i - 5]));
                 System.out.println("Behind Channels: RED " + (0xFF & inArray[i - 6] >> 16) +", GREEN " + (0xFF & inArray[i - 6] >> 8) +", BLUE " + (0xFF & inArray[i - 6]));
                 System.out.println("Behind Channels: RED " + (0xFF & inArray[i - 7] >> 16) +", GREEN " + (0xFF & inArray[i - 7] >> 8) +", BLUE " + (0xFF & inArray[i - 7]));
                }
            outArray[i] = fing << 16 | fing << 8 | fing;
        }
    }

    static int splitAndCombine(int... rgbs) {
        int racc = 0, gacc = 0, bacc = 0;
        for(int rgb: rgbs) {
            racc += (rgb >> 16) & 0xFF;
            gacc += (rgb >> 8) & 0xFF;
            bacc += rgb & 0xFF;
        }
        racc /= rgbs.length;
        gacc /= rgbs.length;
        bacc /= rgbs.length;
        return racc << 16 | gacc << 8 | bacc;
    }


    static int p(int rgb, int n) {
        int r = (256 / n) * (((rgb >> 16) & 0xFF) / (256 / n));
        int g = (256 / n) * (((rgb >> 8) & 0xFF) / (256 / n));
        int b = (256 / n) * (((rgb) & 0xFF) / (256 / n));
        return r << 16 | g << 8 | b;
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

    static int[][] scanImage(int[] codeArray, int width, int height, int[][] wallColors) {
        int[] wallBottoms = new int[width];
        int[] wallTops = new int[width];
        for(int i = 0; i < width; i++){
            int currColor = 0;
            int currTop = -1;
            int numColor = 0;
            for(int j = 0; j < height; j++){
                if((codeArray[j*width + i] == 10 || codeArray[j*width + i] == 11) && currTop == -1){
                     currTop = j;
                }else if((codeArray[j*width + i] == currColor || codeArray[j*width + i] == currColor + 1 || codeArray[j*width + i] == currColor -1) && currTop != -1){
                    if(codeArray[j*width + i] != currColor){
                        numColor ++;
                        if(numColor > 20){
                            currTop = 0;
                            numColor = 0;
                        }
                    }
                    if(codeArray[j*width + i] == 7){
                        wallTops[i] = currTop;
                        wallBottoms[i] = j;
                        break;
                    }
                }else{
                    currTop = -1;
                }
                currColor = codeArray[j*width + i]; 
            }
        }
        int[] newWallTops = new int[width];
        int[] newWallBottoms = new int[width];
        int[] wallTypes = new int[width];
        for(int i = 0; i < width; i++){
            wallTypes[i] = 1;
        }
        removeOutliers(wallTops, wallBottoms, newWallTops, newWallBottoms, wallTypes);
        int[][] out = {newWallBottoms, newWallTops};
        return out;
    }

    /*
    static int[][] scanImageTree(int[] codeArray, int width, int height, int[][] wallColors) {
        int[] wallBottoms = new int[width];
        int[] wallTops = new int[width];
        Scanner sc = new Scanner(System.in);
        ColorSequenceTree colTree = new ColorSequenceTree(wallColors);
        //System.out.println(colTree);
        for(int x = 0; x < width; x ++) {
            int cbottom = -1;
            int ctop = -1;
            colTree.reset();
            for(int yd = 0; yd < height; yd ++) {
                int cpos = x + yd * width;
                System.out.println("checking: " + yd + ", current top is: " + ctop + ", current color is:" + codeArray[cpos]);
                if(colTree.hasNext(codeArray[cpos])) {
                    colTree.progress(codeArray[cpos]);
                    if(ctop == -1) {
                        ctop = yd;
                    }
                } else {
                    if(colTree.reachedEnd()) {
                        cbottom = yd;
                        break;
                    } else {
                        colTree.reset();
                        ctop = -1;
                    }
                }
            }
            // String useless = sc.nextLine();
            wallBottoms[x] = cbottom;
            wallTops[x] = ctop;
        }
        int[] newWallTops = new int[width];
        int[] newWallBottoms = new int[width];
        removeOutliers(wallTops, wallBottoms, newWallTops, newWallBottoms);
        int[][] out = {newWallBottoms, newWallTops};
        return out;
    }
    */

    static void removeOutliers(int[] inArrayTop, int[] inArrayBottom, int[] outArrayTop, int[] outArrayBottom, int[]wallTypes){
        int wallNum = 1;
        double topMean[] = new double[wallNum + 1], bottomMean[] = new double[wallNum + 1];
        int topCount[] = new int[wallNum + 1], bottomCount[] = new int [wallNum + 1];
        //Calculate the mean for the top and bottom coordinates
        for(int i = 0; i < inArrayTop.length; i++){
            if(inArrayTop[i] != 0){
                topMean[wallTypes[i]] += inArrayTop[i];
                topCount[wallTypes[i]]++;
            }
            if(inArrayBottom[i] != 0){
                bottomMean[wallTypes[i]] += inArrayBottom[i];
                bottomCount[wallTypes[i]]++;
            }
        }
        topMean[1] /= topCount[1];
        bottomMean[1] /= bottomCount[1];
        //Calculate the standard deviation for top and bottom coordinates
        int[][] topVariance = new int[wallNum + 1][inArrayTop.length];
        int[][] bottomVariance = new int[wallNum + 1][inArrayTop.length];
        double topStddev[] = new double[wallNum+1], bottomStddev[] = new double[wallNum+1];
        for(int i = 0; i < inArrayTop.length; i++){
            if(inArrayTop[i] != 0){
                topVariance[wallTypes[i]][i] = (inArrayTop[i] - (int)topMean[wallTypes[i]]) * (inArrayTop[i] - (int)topMean[wallTypes[i]]);
            }else{
                topVariance[wallTypes[i]][i] = 0;
            }
            topStddev[wallTypes[i]] += topVariance[wallTypes[i]][i];
            if(inArrayBottom[i] != 0){
                bottomVariance[wallTypes[i]][i] = (inArrayBottom[i] - (int)bottomMean[wallTypes[i]]) * (inArrayBottom[i] - (int)bottomMean[wallTypes[i]]);
            }else{
                bottomVariance[wallTypes[i]][i] = 0;
            }
            bottomStddev[wallTypes[i]] += bottomVariance[wallTypes[i]][i];

        }
        topStddev[1] /= topCount[1];
        topStddev[1] = Math.sqrt(topStddev[1]);
        bottomStddev[1] /= bottomCount[1];
        bottomStddev[1] = Math.sqrt(bottomStddev[1]);
        //Finds outliers based on one standard deviation away from the mean
        for(int i = 0; i < inArrayTop.length; i++){
            if(inArrayTop[i] > topMean[wallTypes[i]] + topStddev[wallTypes[i]] || inArrayTop[i] < topMean[wallTypes[i]] - topStddev[wallTypes[i]] || inArrayBottom[i] > bottomMean[wallTypes[i]] + bottomStddev[wallTypes[i]] || inArrayBottom[i] < bottomMean[wallTypes[i]] - bottomStddev[wallTypes[i]]){
                outArrayTop[i] = 0;
                outArrayBottom[i] = 0;
            }else{
                outArrayTop[i] = inArrayTop[i];
                outArrayBottom[i] = inArrayBottom[i];
            }
        }
    }


    static void fillEmptySpaces(int[][] arr){
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
                    //drawPixel(a, x1, y1, width, 3);
                   // System.out.println("x1, y1 =" + x1 + " " + y1 + " x2, y2 =" + x2 + " " + y2);

                }
            }
        }
    }

    public static void convertToRobertsCrossRaster(int[] input, int[] output, int nrows, int ncols) {
		for (int r = 0; r < nrows - 1; r++) {
			for(int c = 0; c < ncols - 1; c++) {
				output[r * ncols + c] = Math.abs(input[r * ncols + c] - input[(r+1) * ncols + (c+1)])
                        + Math.abs(input[r * ncols + (c+1)] - input[(r+1) * ncols + c]);
                output[r * ncols + c] |= output[r * ncols + c] << 16 | output[r * ncols + c] << 8;

			}
		}
	}


    static void posterizeImageInt(int[] rgbArray, int[] outArray, int diffThreshold) {
        
        for(int i = rgbArray.length - 1; i >= 0; i --) {
            outArray[i] = posterizePixelInt(rgbArray[i], diffThreshold);
        }

    }

    static void codeToRGB(int[] codeArray, int[] outArray) {
        for(int i = codeArray.length - 1; i >= 0; i --) {
            outArray[i] = ColorArr[codeArray[i]];
        }
    }

    static String imageToCSV(int[] imageArray, int width, int height) {
        String out = "";
        for(int i = 0; i < width; i ++) {
            for(int j = 0; j < height - 1; j ++) {
                out += imageArray[j*width+i] + ",";
            }
            out += imageArray[height + i] + "\n";
        }
        return out;
    }

    static int intensityFromRGB(int rgb) {
        return (((rgb >> 16) & 0xFF) + ((rgb >> 8) & 0xFF) + (rgb & 0xFF))/3;
    }

    static int[] intensityImage(int[] imageArray, int width, int height) {
        int[] out = new int[width * height];
        for(int i = 0; i < width; i ++) {
            for(int j = 0; j < height; j ++) {
                out[j * width + i] = intensityFromRGB(imageArray[j * width + i]);
            }
        }
        return out;
    }

    /* static int[] booleanEdgeDetection(int[] intensityArray, int width, int height, int constant) {
        int[] out = new int[width * height];
        for(int i = 1; i < width - 1; i ++) {
            for(int j = 1; j < height - 1; j ++) {
                int T_L = -constant;
                int sum = 0;
                for(int x = -1; x <= 1; x ++) {
                    for(int y = -1; y <= 1; y ++) {
                        int pos = (j + y) * width + i + x;
                        sum += intensityArray[pos];
                    }
                }
                T_L += sum / 9;

            }
        }
        return out;
    } */



    static int[] robertsCrossEdgeDetection(int[] intensityArray, int width, int height) {
        int[] out = new int[width * height];
        for(int i = 1; i < width - 1; i ++) {
            for(int j = 1; j < height - 1; j ++) {
                out[j * width + i] = Math.abs(intensityArray[j * width + i] - intensityArray[(j+1) * width + (i+1)])
                        + Math.abs(intensityArray[j * width + (i+1)] - intensityArray[(j+1) * width + i]);
                // out[j * width + i] = out[j * width + i] > 0 ? 256 : 0;
                out[j * width + i] |= out[j * width + i] << 16 | out[j * width + i] << 8;
            }
        }
        return out;    
    }

    static int[] sobelEdgeDetection(int[] intensityArray, int width, int height) {
        int[] out = new int[width * height];
        for(int i = 1; i < width - 1; i ++) {
            for(int j = 1; j < height - 1; j ++) {
                int P1 = intensityArray[(j - 1) * width + i - 1];
                int P2 = intensityArray[(j - 1) * width + i];
                int P3 = intensityArray[(j - 1) * width + i + 1];
                int P4 = intensityArray[j * width + i - 1];
                int P6 = intensityArray[j * width + i + 1];
                int P7 = intensityArray[(j + 1) * width + i - 1];
                int P8 = intensityArray[(j + 1) * width + i];
                int P9 = intensityArray[(j + 1) * width + i + 1];
                out[j * width + i] = Math.abs((P1 + 2 * P2 + P3) - (P7 + 2 * P8 + P9))
                                     + Math.abs((P3 + 2 * P6 + P9) - (P1 + 2 * P4 + P7));
                out[j * width + i] |= out[j * width + i] << 16 | out[j * width + i] << 8;
            }
        }
        return out;    
    }

    static int posterizePixelHSL(int rgb, int dt) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = (rgb) & 0xFF;
        int max = red > blue ? red > green ? red : green : blue > green ? blue : green;
        int min = red < blue ? red < green ? red : green : blue < green ? blue : green;
        int delta = max - min;
        int h = 0;
        if(delta == 0){
            h = 0;
        }else if(max == red){
            h = ((green-blue)/delta) % 6;
        }else if(max == green){
            h = (blue - red)/delta + 2;
        }else{
            h = (red - green)/delta + 4;
        }
        h *= 60;
        int l = (max + min) >> 1;
        if(delta > dt){
            if(h > 330 || h < 30){
                return 0;
            }else if(h > 30 && h < 90){
                return 5;
            }else if( h > 90 && h < 150){
                return 1;
            }else if(h > 150 && h < 210){
                return 3;
            }else if(h > 210 && h < 270){
                return 2;
            }else if(h > 270 && h < 330){
                return 4;
            }
        }else{
            if(l < 43){
                return 7;
            }else if(l > 43 && l < 86){
                return  7;
            }else if(l > 86 && l < 129){
                return 8;
            }else if(l > 129 && l < 152){
                return 9;
            }else if(l > 152 && l < 195){
                return 10;
            }else{
                return 11;
            }
        }
        return 6;
    }

    static int[] fastBoxBlur(int[] inArray, int width, int height){
        int[] outArray = new int[height*width];
        for(int i = 2; i < height-2; i++){
            for(int j = 2; j < width-2; j++){
                int rsum = 0, gsum = 0, bsum = 0;
                for(int k = -2; k < 3; k++){
                    for(int l = -2; l < 3; l++){
                        if((k > -2 && k < 2) && (l > -2 && l  < 2) && (k != 0 && j != 0)){
                            rsum += (inArray[(i+k)*width+j+l] >> 16 & 0xFF) << 1;
                            gsum += (inArray[(i+k)*width+j+l] >> 8 & 0xFF) << 1;
                            bsum += (inArray[(i+k)*width+j+l] & 0xFF) << 1;
                        }else{
                            rsum += (inArray[(i+k)*width+j+l] >> 16 & 0xFF);
                            gsum += (inArray[(i+k)*width+j+l] >> 8 & 0xFF);
                            bsum += (inArray[(i+k)*width+j+l] & 0xFF);
                        }
                    }
                }
                rsum >>= 5;
                gsum >>= 5;
                bsum >>= 5;
                outArray[i*width+j] = rsum << 16 | gsum << 8 | bsum;
                
                
            }
        }
        return outArray;
    }
    
    static void edgeRemoveOutlier(int[][] inArray, int[] edgeDetect){
        int width = inArray[0].length;
        for(int i = 0; i < width; i++){
            boolean flag = false;
            for(int k = -2; k < 3; k++){
                for(int l = -2; l < 3; l++){
                   try{
                       if(edgeDetect[(inArray[0][i] + k) * width + i + l] > 32){
                            flag = true;
                       }
                   } catch(IndexOutOfBoundsException e){

                   }
                }
            }
            if(!flag){
                inArray[0][i] = -1;
                inArray[1][i] = -1;
            }
        }
    }

    static int[] convertToMono(int[] inArray){
        int[] outArray = new int[inArray.length];
        for(int i = 0; i < inArray.length; i++){
           outArray[i] = ((inArray[i] >> 16 & 0xFF) + (inArray[i] >> 8 & 0xFF) + (inArray[i] & 0xFF)) / 3;
        }
        return outArray;
    }

    static String heightsToCSV(int[][] heightsArray) {
        String out = "";
        for(int i = 0; i < heightsArray[0].length; i ++) {
            out += i + ", " + heightsArray[0][i] + ", " + heightsArray[1][i] + "\n";
        }
        return out;
    }

    static void writeCSV(String toWrite, String fileName) {
        try (PrintStream outCSV = new PrintStream(new FileOutputStream(fileName))) {
            outCSV.print(toWrite);
        } catch(FileNotFoundException e) {

        }
    }
}

