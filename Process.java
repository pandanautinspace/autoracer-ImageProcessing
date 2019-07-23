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
            new File ("carvroom2.png")
            
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
                outFiles[i] = new File("out"+i+".png");
                i++;
            }
            for(int j = 0; j < images.length; j ++) {
                rgbs = in[j % images.length];
                newrgbs = out[j % images.length];
                long start_time = System.nanoTime();
                posterizeImageInt(rgbs, newrgbs, 65);
                int[][] tb = scanImage(newrgbs, outImages[j % images.length].getWidth(), outImages[j % images.length].getHeight(), WallColorSeqs);
                // String csv = imageToCSV(newrgbs, outImages[j % images.length].getWidth(), outImages[j % images.length].getHeight());
                // try (PrintStream outCSV = new PrintStream(new FileOutputStream("image" + j  + ".csv"))) {
                //     outCSV.print(csv);
                // }
                codeToRGB(newrgbs, newrgbs);
                for(int k = 0; k < tb[0].length; k++) {
                    if(tb[0][k] > 0 && tb[1][k] > 0) {
                        for(int m = tb[1][k]; m < tb[0][k]; m ++) {
                            newrgbs[k + tb[0].length * m] = ColorArr[4];
                        }
                    }
                }
                out[j % images.length] = newrgbs;
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
                    if(codeArray[j*width + i] == 6){
                        wallTops[i] = currTop;
                        wallBottoms[i] = j;
                        break;
                    }
                }else{
                    currTop = -1;
                }
                currColor = codeArray[j*width + i]; 
                if(j == height -1){
                    wallTops[i] = -1;
                    wallBottoms[i] = -1;
                }
            }
        }
        int[] newWallTops = new int[width];
        int[] newWallBottoms = new int[width];
        removeOutliers(wallTops, wallBottoms, newWallTops, newWallBottoms);
        int[][] out = {newWallBottoms, newWallTops};
        return out;
    }

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

}