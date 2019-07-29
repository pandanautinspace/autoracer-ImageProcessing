import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Scanner;


import java.io.File;
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

    static final ColorSequenceTree WallSeqs = new ColorSequenceTree(WallColorSeqs);

    public static void main(String[] args) {
        File[] images = {
            /* new File("blur.png"),
            new File("wow.jpg"), */
            new File ("vroom1.png")/* ,
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
            new File ("carvroom2.png") */
        };
        try {
            int[][] in = new int[images.length][];
            int[][] out = new int[images.length][];
            File[] outFiles = new File[images.length];
            BufferedImage[] outImages = new BufferedImage[images.length];
            int i = 0;
            int[] rgbs;
            int[] newrgbs;
            for(File imageFile : images) {
                System.out.println("Processing " + imageFile.getName());
                BufferedImage image = ImageIO.read(imageFile);
                in[i] = new int[image.getWidth()*image.getHeight()];
                image.getRGB(0,0,image.getWidth(),image.getHeight(), in[i], 0, image.getWidth());
                out[i] = in[i].clone();
                outImages[i] = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
                outFiles[i] = new File("out"+i+1+".png");
                i++;
            }
            for(int j = 0; j < images.length; j ++) {
                rgbs = in[j % images.length];
                newrgbs = out[j % images.length];
                long start_time = System.nanoTime();
                // blurrifyImage(rgbs, newrgbs, outImages[j % images.length].getWidth(), outImages[j % images.length].getHeight());
                averageByRadius(rgbs, newrgbs, 5);
                posterizeImageInt(newrgbs, newrgbs, 65);
                int[][] tb = scanImage(newrgbs, outImages[j % images.length].getWidth(), outImages[j % images.length].getHeight(), WallColorSeqs);
                // removeOutliers(tb[0], tb[1], tb[0], tb[1]);
                // // // String csv = imageToCSV(newrgbs, outImages[j % images.length].getWidth(), outImages[j % images.length].getHeight());
                // // // try (PrintStream outCSV = new PrintStream(new FileOutputStream("image" + j  + ".csv"))) {
                // // //     outCSV.print(csv);
                // // // }
                // interpolate(tb[0], outImages[j % images.length].getHeight());
                // interpolate(tb[1], outImages[j % images.length].getHeight());
                // // // for(int t: tb[0]) {
                // // //     System.out.println(t);
                // // // }
                codeToRGB(newrgbs, newrgbs);
                for(int k = 0; k < tb[0].length; k++) {
                    if(tb[0][k] > 0 && tb[1][k] > 0) {
                        // System.out.println("Bottom at " + tb[1][k] + " and top at " + tb[0][k]);
                        for(int m = tb[1][k]; m < tb[0][k]; m ++) {
                            // System.out.println("Accessing Index: " + (k + tb[0].length * m));
                            newrgbs[k + tb[0].length * m] = ColorArr[4];
                        }
                    }
                }
                out[j % images.length] = newrgbs;
                // System.out.println();
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

    static int posterizePixelV2(int r, int g, int b, int divides) {
        int inc = 256 / divides;
        int rn = 0, gn = 0, bn = 0;
        for(int i = 0; i < 256; i += inc) {
            if(r > i && r < i + inc) {
                rn = i;
            }
            if(g > i && g < i + inc) {
                gn = i;
            }
            if(b > i && b < i + inc) {
                bn = i;
            }
        }
        return (rn << 16) | (gn << 8) | bn;

    }

    static void posterizeImageV2(int[] rgbarray, int[] outArray, int div) {
        for(int i = 0; i < rgbarray.length; i ++) {
            int c = rgbarray[i];
            outArray[i] =  posterizePixelV2(c >> 16 & 0xFF, c >> 8 & 0xFF, c & 0xFF, div);
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

    static void averageBy2(int[] inArray, int[] outArray){
        for(int i = 0; i < inArray.length - 1; i ++) {
            outArray[i] = splitAndCombine(inArray[i], inArray[i + 1]);
        }
        outArray[inArray.length - 1] = inArray[inArray.length - 1];
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

    

    static void blurrifyImage(int[] inArray, int[] outArray, int width, int height) {
        int u = inArray[0];
        int d = inArray[width];
        int l = inArray[0];
        int r = inArray[1];
        outArray[0] = splitAndCombine(u, d, l, r);
        for(int i = 1; i < inArray.length - 2; i ++) {
            outArray[i] = splitAndCombine(u, d, l, r);
            if(i == width - 1 || i == height * (width - 1) + 1) {
                i ++;
            }
            switch(i + 1 % width) {
                case 1:
                    r = inArray[i + 1];
                    l = inArray[i];
                    d = inArray[width + i];
                    u = inArray[i - width];
                    break;
                case 0:
                    r = inArray[i];
                    l = inArray[i - 1];
                    d = inArray[width + i];
                    u = inArray[i - width];
                    break;
                default:
                    try {
                        if(i < width || i > (height - 1) * width) {
                            r = inArray[i - 1];
                            l = inArray[i + 1];
                            u = r;
                            d = l;
                        } else {
                            r = inArray[i + 1];
                            l = inArray[i - 1];
                            d = inArray[width + i];
                            u = inArray[-width + i];
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println(i + 1 % width);
                        System.out.println(e);
                    }
            }
            
        }
        
    }

    static void removeOutliers2(int[] inArrayTop, int[] inArrayBottom, int[] outArrayTop, int[] outArrayBottom){
        double topMean = 0, bottomMean = 0;
        int topCount = 0, bottomCount = 0;
        for(int i = 0; i < inArrayTop.length; i++){
            if(inArrayTop[i] != 0){
                topMean += inArrayTop[i];
                topCount++;
            }
            if(inArrayBottom[i] != 0){
                bottomMean += inArrayBottom[i];
                bottomCount++;
            }
        }
        topMean /= topCount;
        bottomMean /= bottomCount;
        int[] topVariance = new int[inArrayTop.length];
        int[] bottomVariance = new int[inArrayTop.length];
        double topStddev = 0, bottomStddev = 0;
        for(int i = 0; i < inArrayTop.length; i++){
            if(inArrayTop[i] != 0){
                topVariance[i] = (inArrayTop[i] - (int)topMean) * (inArrayTop[i] - (int)topMean);
            }else{
                topVariance[i] = 0;
            }
            topStddev += topVariance[i];
            if(inArrayBottom[i] != 0){
                bottomVariance[i] = (inArrayBottom[i] - (int)bottomMean) * (inArrayBottom[i] - (int)bottomMean);
            }else{
                bottomVariance[i] = 0;
            }
            bottomStddev += bottomVariance[i];

        }
        topStddev /= topCount;
        topStddev = Math.sqrt(topStddev);
        bottomStddev /= bottomCount;
        bottomStddev = Math.sqrt(bottomStddev);
        for(int i = 0; i < inArrayTop.length; i++){
            if(inArrayTop[i] > topMean + topStddev || inArrayTop[i] < topMean - topStddev || inArrayBottom[i] > bottomMean + bottomStddev || inArrayBottom[i] < bottomMean - bottomStddev){
                outArrayTop[i] = 0;
                outArrayBottom[i] = 0;
            }else{
                outArrayTop[i] = inArrayTop[i];
                outArrayBottom[i] = inArrayBottom[i]; 
            }
        }
    } 

    static void interpolate(int[] inArray, int height) {
        int x1 = -1, y1 = -1, x2 = -1, y2 = -1;
        float m = 481;
        for(int i = 0; i < inArray.length; i ++) {
            if(inArray[i] > 0) {
                if(x1 < 0) {
                    x1 = i;
                    y1 = inArray[i];
                } else if(x2 < 0) {
                    x2 = i;
                    y2 = inArray[i];
                    m = (float)(y2 - y1) / (float)(x2 - x1);
                } else {
                    if(m < 481) m = (float)0.5 * (((float)(y2 - y1) / (float)(x2 - x1)) + m);
                    else m = (float)(y2 - y1) / (float)(x2 - x1);
                    x1 = x2;
                    y1 = y2;
                    x2 = i;
                    y2 = inArray[i];
                }
            } else {
                if(m < 481) inArray[i] = (int)(m * (i - x2) + y2);
                inArray[i] = inArray[i] > height ? height : (inArray[i] < 0 ? 0 : inArray[i]);
            }
        }
    }

    static int[] RGBArrayFromImage(BufferedImage image) {
        int height = image.getHeight();
        int width = image.getWidth();
        int[] out = new int[width*height];
        image.getRGB(0,0,image.getWidth(),image.getHeight(), out, 0, image.getWidth());
        return out;
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
        } else if (rg < dt * 2 && rg > -dt * 2 && rb > dt * 2 && bg < -dt) {
            return 5;
        } else if (rb < dt * 2 && rb > -dt * 2 && rg > dt * 2 && bg > dt) {
            return 4;
        } else if (bg < dt * 2 && bg > -dt * 2 && rg < -dt * 2 && rb < -dt ) {
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
                    if(codeArray[j*width + i] == 6){
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
        
        removeOutliersPredictive(wallTops, 40, 1, height);
        removeOutliersPredictive(wallBottoms, 40, 1, height);
        // removeOutliers2(wallTops, wallBottoms, newWallTops, newWallBottoms);
        // System.out.println("Wall Tops");
        // for(int top : wallTops) {
        //     System.out.println(top);
        // }
        // System.out.println("Wall Bots");
        // for(int bot : wallBottoms) {
            // System.out.println(bot);
        // }
        // interpolate(newWallBottoms, height);
        // interpolate(newWallTops, height);
        int[][] out = {wallBottoms, wallTops};
        fillEmptySpaces(out);
        return out;
    }

    static void removeOutliersPredictive(int[] inArray, int diffThreshold, int exceptions, int height) {
        int x1 = -1, y1 = -1, x2 = -1, y2 = -1;
        float m = 481;
        int prediction = -1;
        int es = 0;
        for(int i = 0; i < inArray.length; i ++) {
            if(inArray[i] > 0) {
                if(m < 481) prediction = (int)(m * (i - x2) + y2);
                prediction = prediction > height ? height : prediction < 0 ? 0 : prediction;
                if(prediction != -1) {
                    // System.out.println("Prediction was "+ prediction +", but actual was" + inArray[i]);
                    if((prediction - inArray[i] > diffThreshold || inArray[i] - prediction < -diffThreshold) && es++ >= exceptions)  {
                            // System.out.println("Thats a bad...");
                            inArray[i] = -1;
                            continue;
                    }
                    // System.out.println("At position " + i + ", " + es + " exceptions.");
                }
                if(x1 < 0) {
                    x1 = i;
                    y1 = inArray[i];
                } else if(x2 < 0) {
                    x2 = i;
                    y2 = inArray[i];
                    m = (float)(y2 - y1) / (float)(x2 - x1);
                } else {
                    if(m < 481) m = (float)0.5 * (((float)(y2 - y1) / (float)(x2 - x1)) + m);
                    else m = (float)(y2 - y1) / (float)(x2 - x1);
                    x1 = x2;
                    y1 = y2;
                    x2 = i;
                    y2 = inArray[i];
                }
            }
        }
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

    static void removeOutliers(int[] inArrayTop, int[] inArrayBottom, int[] outArrayTop, int[] outArrayBottom){
        double mean = 0;
        int numCount = 0;
        for(int i = 0; i < inArrayTop.length; i++){
            if(inArrayBottom[i] - inArrayTop[i] > 0){
                mean += inArrayBottom[i] - inArrayTop[i];
                numCount++;
            }
        }
        mean /= numCount;
        int[] variance = new int[inArrayTop.length];
        double stddev = 0;
        for(int i = 0; i < inArrayTop.length; i++){
            if(inArrayBottom[i] - inArrayTop[i] > 0){
                variance[i] = (inArrayBottom[i] - inArrayTop[i] - (int)mean) * (inArrayBottom[i] - inArrayTop[i] - (int)mean);
            }else{
                variance[i] = 0;
            }
            stddev += variance[i];
        }
        stddev /= numCount;
        stddev = Math.sqrt(stddev);
        for(int i = 0; i < inArrayTop.length; i++){
            if(inArrayBottom[i] - inArrayTop[i] > mean + stddev || inArrayBottom[i] - inArrayTop[i] < mean - stddev){
                outArrayTop[i] = -1;
                outArrayBottom[i] = -1;
            }else{
                outArrayTop[i] = inArrayTop[i];
                outArrayBottom[i] = inArrayBottom[i]; 
            }
        }
    }

    static void fillEmptySpaces(int[][] arr){
        int x1 = 0; int x2 = 0; int y1 = 0; int y2 = 0;
        for(int i = 1; i < arr[0].length - 2; i++){
            if(arr[0][i] <= 0){
                if(arr[0][i-1] <= 0){
                    x1 = i-1;
                    y1 = arr[0][i-1];
                    int j = i + 1;
                    while(arr[0][j] <= 0 && j < arr[0].length - 1){
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
                    System.out.println("x1, y1 =" + x1 + " " + y1 + " x2, y2 =" + x2 + " " + y2);

                }
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
}

class ColorSequenceTree {
    private ColorSequenceTreeNode rootNode;
    private ColorSequenceTreeNode currentNode;

    ColorSequenceTree() {
        rootNode = new ColorSequenceTreeNode();
        currentNode = rootNode;
    }

    ColorSequenceTree(int[][] colorSequenceTable) {
        rootNode = new ColorSequenceTreeNode();
        currentNode = rootNode;
        int depth = 0;
        ColorSequenceTreeNode curNode;
        for(int[] colorSequence : colorSequenceTable) {
            // System.out.println(depth++);
            curNode = rootNode;
            // System.out.println(curNode.isFinal);
            for(int code : colorSequence) {
                if(curNode.hasChild(code)) {
                    curNode = curNode.getBranch(code);
                } else {
                    ColorSequenceTreeNode node = new ColorSequenceTreeNode();
                    curNode.setBranch(code, node);
                    curNode = node;
                }
                curNode.setSelf(code);
            }
            curNode.isFinal = true;
        }
    }

    ColorSequenceTree(int[] colorSequence) {
        rootNode = new ColorSequenceTreeNode();
        currentNode = rootNode;
        ColorSequenceTreeNode curNode = rootNode;
        for(int code : colorSequence) {
            ColorSequenceTreeNode node = new ColorSequenceTreeNode();
            curNode.setBranch(code, node);
            curNode = node;
        }
        curNode.isFinal = true;
    }

    void addColorSequence(int[] colorSequence) {
        ColorSequenceTreeNode curNode = rootNode;
        for(int code : colorSequence) {
            if(curNode.hasChild(code)) {
                curNode = curNode.getBranch(code);
            } else {
                ColorSequenceTreeNode node = new ColorSequenceTreeNode();
                curNode.setBranch(code, node);
                curNode = node;
            }
        }
    }

    void reset() {
        currentNode = rootNode;
    }

    void progress(int code) {
        if(currentNode.hasChild(code)) {
            currentNode = currentNode.getBranch(code);
        }
    }

    boolean hasNext(int code) {
        return currentNode.hasChild(code);
    }

    boolean reachedEnd() {
        return currentNode.isFinal;
    }

    // @Override
    // public String toString() {
    //     return rootNode.toString();
    // }

}

class ColorSequenceTreeNode {
    private ColorSequenceTreeNode[] branches;
    boolean isFinal = true;

    ColorSequenceTreeNode() {
        this.branches = new ColorSequenceTreeNode[12];
    }

    ColorSequenceTreeNode(ColorSequenceTreeNode ... nodes) {
        int index = 0;
        isFinal = false;
        if(nodes.length > branches.length ) {
            //raise error
        }
        for(ColorSequenceTreeNode node : nodes) {
            branches[index] = node;
            index++;
        }
    }

    ColorSequenceTreeNode(int startCode, ColorSequenceTreeNode... nodes) {
        int index = startCode;
        isFinal = false;
        if(nodes.length + startCode > branches.length ) {
            //raise error
        }
        for(ColorSequenceTreeNode node : nodes) {
            branches[index] = node;
            index++;
        }
    }

    public boolean hasChild(int code) {
        return branches[code] != null;
    }

    public ColorSequenceTreeNode getBranch(int code) {
        return branches[code];
    }

    public void setBranch(int code, ColorSequenceTreeNode node) {
        isFinal = false;
        branches[code] = node;
    }

    public void setSelf(int code) {
        branches[code] = this;
    }

    // @Override
    // public String toString() {
    //     String out = "(\n";
    //     int i = 0;
    //     for(ColorSequenceTreeNode node : branches) {
    //         if(node == null) {
    //             out += "/\n";
    //         } else {
    //             out += i + "|" + node.toString() + "\n";
    //         }
    //         i ++;
    //     }
    //     out += ")";
    //     return out;
    // }


    /**
     * @return ColorSequenceTreeNode[] return the branches
     */
    public ColorSequenceTreeNode[] getBranches() {
        return branches;
    }

    /**
     * @param branches the branches to set
     */
    public void setBranches(ColorSequenceTreeNode[] branches) {
        this.branches = branches;
    }

}