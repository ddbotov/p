package ru.botov.poker.image;

import javafx.util.Pair;
import ru.botov.poker.action.Preflop;
import ru.botov.poker.model.Card;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.Robot;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ImageSearcher {

    private static final int WHITE = -1;

    public static void main(String[] args) {
        try {
            fillCache("suits/CLUB.png");
            fillCache("suits/HEART.png");
            fillCache("suits/SPADE.png");
            fillCache("suits/DIAMOND.png");
            fillCache("cards/2.bmp");
            fillCache("cards/3.bmp");
            fillCache("cards/4.bmp");
            fillCache("cards/5.bmp");
            fillCache("cards/6.bmp");
            fillCache("cards/7.bmp");
            fillCache("cards/8.bmp");
            fillCache("cards/9.bmp");
            fillCache("cards/10.bmp");
            fillCache("cards/J.bmp");
            fillCache("cards/Q.bmp");
            fillCache("cards/K.bmp");
            fillCache("cards/A.bmp");

            System.out.println("start");
            long start = System.currentTimeMillis();
            BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            //int imageWidth = image.getWidth();
            //int imageHeight = image.getHeight();
            //System.out.println(imageWidth);
            //System.out.println(imageHeight);

            //findImage(image, "cards/6.bmp");
            //findImage(image, "suits/CLUB.png");
            //findImage(image, "suits/HEART.png");
            //findImage(image, "suits/SPADE.png");
            //findImage(image, "suits/DIAMOND.png");

            Set<Card> cards = getMyCards(image);

            System.out.println("end = " + (System.currentTimeMillis() - start));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Set<Card> getMyCards(BufferedImage image) {
        Set<Card> myCards = new HashSet<>();
        Set<Pair<Integer, Integer>> clubs = findImage(image, "suits/CLUB.png");
        Set<Pair<Integer, Integer>> hearts = findImage(image, "suits/HEART.png");
        Set<Pair<Integer, Integer>> spades = findImage(image, "suits/SPADE.png");
        Set<Pair<Integer, Integer>> diamonds = findImage(image, "suits/DIAMOND.png");

        Set<Pair<Integer, Integer>> allSuits = new HashSet<>();
        allSuits.addAll(clubs);
        allSuits.addAll(hearts);
        allSuits.addAll(spades);
        allSuits.addAll(diamonds);
        return myCards;
    }

    private static HashMap<String, BufferedImage> imageCache = new HashMap<>();

    private static void fillCache(String searchImageName) {
        try {
            BufferedImage searchImage = ImageIO.read(Preflop.class.getClassLoader().getResourceAsStream(searchImageName));
            imageCache.put(searchImageName, searchImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Set<Pair<Integer, Integer>> findImage(BufferedImage image, String searchImageName) {
        Set<Pair<Integer, Integer>> result = new HashSet<>();
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        try {

            BufferedImage searchImage = imageCache.get(searchImageName);
            //BufferedImage searchImage = ImageIO.read(Preflop.class.getClassLoader().getResourceAsStream(searchImageName));
            int searchWidth = searchImage.getWidth();
            int searchHeight = searchImage.getHeight();

            for (int x=0; x<imageWidth-searchWidth; x++) {
                search: for (int y=0; y<imageHeight-searchHeight; y++) {
                    if (image.getRGB(x, y) == searchImage.getRGB(0, 0)) {
                        for (int x2 = 0; x2 < searchWidth; x2++) {
                            for (int y2 = 0; y2 < searchHeight; y2++) {
                                int imageRGB = image.getRGB(x2 + x, y2 + y);
                                int searchImageRGB = searchImage.getRGB(x2, y2);
                                //if ((imageRGB!=searchImageRGB)) {
                                if ((imageRGB!=WHITE && searchImageRGB==WHITE)
                                        || (imageRGB==WHITE && searchImageRGB!=WHITE)) {
                                    continue search;
                                }
                            }
                        }
                        System.out.println("WOW! x=" + x + " y=" + y);
                        result.add(new Pair<>(x,y));
                        //throw new RuntimeException();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

/*    private static boolean bufferedImagesEqual(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight()) {
            for (int x = 0; x < img1.getWidth(); x++) {
                for (int y = 0; y < img1.getHeight(); y++) {
                    if (img1.getRGB(x, y) != img2.getRGB(x, y)) {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }

    private static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }*/

}
