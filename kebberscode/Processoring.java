import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


import java.io.File;
import java.io.IOException;

class Processoring {
    public static void main(String[] args) {
        File[] images = {
            //new File("zoom.png"),
            //new File("poo.png"),
            new File("pic.png"),
            // new File("rfrp.png"),
            // new File("unknown.png"),
            // new File("blur.png"),
            // new File("tm.png"),
            // new File("walls.png")
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
            for(int j = 0; j < 20; j ++) {
                rgbs = in[j % images.length];
                newrgbs = out[j % images.length];
                long start_time = System.nanoTime();
                //averageBrightnessMap(rgbs, newrgbs);
                posterizeImage(rgbs, newrgbs);
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
    
    int[] scanLine(int[] rgbArray, int line, int scanwidth) {
        int top = -1;
        int bottom = -1;
        int[] colorSequence = new int[40];
        colorSequence[0] = line *scanwidth;
        int seqIndex = 0;
        for(int i = line * scanwidth + 1; i < (line + 1) * scanwidth; i ++) {
            if(! (rgbArray[i] == colorSequence[seqIndex])) {
                colorSequence[++ seqIndex] = rgbArray[i];
                if(seqIndex == 1) {
                    top = i;
                }
                else if(seqIndex == 2) {
                    bottom = i;
                    break;
                }
            }
        }
        int[] result = {top,bottom};
        return result;
    }

    static int getRed(int rgb) {
        return (rgb >> 16) & 0xFF;
    }

    static int getGreen(int rgb) {
        return (rgb >> 8) & 0xFF;
    }

    static int getBlue(int rgb) {
        return rgb & 0xFF;
    }

    static int combineRGB(int red, int green, int blue) {
        return blue + (green << 8) + (red << 16);
    }
    
    static void greyscaleFromChannel(int[] rgbArray, int[] outArray, int channel) {
        switch(channel) {
            case 1:
                for(int i = 0; i < rgbArray.length; i ++) {
                    outArray[i] = combineRGB(getRed(rgbArray[i]), getRed(rgbArray[i]), getRed(rgbArray[i]));
                }
                break;
            case 2:
                for(int i = 0; i < rgbArray.length; i ++) {
                    outArray[i] = combineRGB(getGreen(rgbArray[i]), getGreen(rgbArray[i]), getGreen(rgbArray[i]));
                }
                break;
            case 3:
                for(int i = 0; i < rgbArray.length; i ++) {
                    outArray[i] = combineRGB(getBlue(rgbArray[i]), getBlue(rgbArray[i]), getBlue(rgbArray[i]));
                }
                break;
        }
    }

    
    static void isolateChannel(int[] rgbArray, int[] outArray, int channel) {
        switch(channel) {
            case 1:
                for(int i = 0; i < rgbArray.length; i ++) {
                    outArray[i] = combineRGB(getRed(rgbArray[i]), 0, 0);
                }
                break;
            case 2:
                for(int i = 0; i < rgbArray.length; i ++) {
                    outArray[i] = combineRGB(0, getGreen(rgbArray[i]), 0);
                }
                break;
            case 3:
                for(int i = 0; i < rgbArray.length; i ++) {
                    outArray[i] = combineRGB(0, 0, getBlue(rgbArray[i]));
                }
                break;
        }
    }

    static int posterizePixel(int rgb) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = (rgb) & 0xFF;
        // int brightness = red * 3 + green * 6 + blue;
        // int sumBrightness = past[0];
        // int pastVariance = past[1];
        // int pastAvgBrightness = past[0] / (index + 1);
        // int variance = pastAvgBrightness - brightness;
        // variance = variance > 0 ? variance : -variance;
        // int brightAvg = (pastAvgBrightness + brightness) / (index + 2);
        // int varAvg = (pastVariance * 3 + variance) >> 2;
        int rg = red - green;
        int rb = red - blue;
        int bg = blue - green;
        int dt = 50;
        // int dt = (varAvg << 1);
        // past[0] = sumBrightness + brightness;
        // past[1] = varAvg;
        if(rg > dt && rb > dt) {
            return 0xFF0000;
        }
        else if (rg < -dt && bg < -dt) {
            return 0x00FF00;
        }
        else if (rb < -dt && bg > dt) {
            return 0x0000FF;
        }
        else if (rg < dt && rg > -dt && rb > dt && bg < -dt) {
            return 0xFFFF00;
        }
        else if (rb < dt && rb > -dt && rg > dt && bg > dt) {
            return 0xFF00FF;
        }
        else if (bg < dt && bg > -dt && rg < -dt && rb < -dt ) {
            return 0x00FFFF;
        }
        else {
            int avg = (red + green + blue + green) >> 2;
            //int change = (brightness >> 4) - 80;
            if(avg < 25) {
                return 0;
            }
            else if(avg < 76) {
                return 0x333333;
            }
            else if(avg < 127) {
                return 0x666666;
            }
            else if(avg < 178) {
                return 0x999999;
            }
            else if(avg < 229) {
                return 0xCCCCCC;
            }
            else {
                return 0xFFFFFF;
            }

        }
    }

    static void posterizeImage(int[] rgbArray, int[] outArray) {
        for(int i = rgbArray.length - 1; i >= 0; i --) {
            outArray[i] = posterizePixel(rgbArray[i]);
        }

    }

    // static void brightnessMap(int[] inArray, int[] outArray) {
    //     for(int i = inArray.length - 1; i >= 0; i --) {
    //         int rgb = inArray[i];
    //         int red = (rgb >> 16) & 0xFF;
    //         int green = (rgb >> 8) & 0xFF;
    //         int blue = (rgb) & 0xFF;
    //         int brightness = red * 3 + green * 6 + blue;
    //         outArray[i] = (brightness / 10) + ((brightness / 10) << 8) + ((brightness / 10) << 16);
    //     }
    // }

    // static void averageBrightnessMap(int[] inArray, int[] outArray) {
    //     int past = 127;
    //     int l = inArray.length;
    //     for(int i = l - 1; i >= 0; i --) {
    //         int rgb = inArray[i];
    //         int red = (rgb >> 16) & 0xFF;
    //         int green = (rgb >> 8) & 0xFF;
    //         int blue = (rgb) & 0xFF;
    //         int brightness = red * 3 + green * 6 + blue;
    //         int pastAvgBrightness = past / (l - i);
    //         past += brightness;
    //         int brightAvg = (past) / (l - i + 2);
    //         if(i % 1000 == 0) System.out.println(brightAvg + ", " + (l - i));
    //         outArray[i] = (brightAvg / 10) + ((brightAvg / 10) << 8) + ((brightAvg / 10) << 16);
    //     }
    // }

    // static void regionAverageBrightnessMap(int[] inArray, int[] outArray) {
    //     int past = 127;
    //     int l = inArray.length;
    //     for(int i = l - 1; i >= 0; i --) {
    //         int rgb = inArray[i];
    //         int red = (rgb >> 16) & 0xFF;
    //         int green = (rgb >> 8) & 0xFF;
    //         int blue = (rgb) & 0xFF;
    //         int brightness = red * 3 + green * 6 + blue;
    //         int pastAvgBrightness = past / (l - i);
    //         past += brightness;
    //         int brightAvg = (past) / (l - i + 2);
    //         if(i % 1000 == 0) System.out.println(brightAvg + ", " + (l - i));
    //         outArray[i] = (brightAvg / 10) + ((brightAvg / 10) << 8) + ((brightAvg / 10) << 16);
    //     }
    // }

}