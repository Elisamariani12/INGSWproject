package it.polimi.ingsw.client.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/** Contains static methods for Image Manipulation */
public class ImageUtils
{
    /** Converts a given Image into a BufferedImage
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    public static BufferedImage toBufferedImage(Image img)
    {
        //No need to convert to BufferedImage if img is already a BufferedImage
        if (img instanceof BufferedImage) return (BufferedImage) img;

        BufferedImage buffer = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = buffer.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return buffer;
    }

    /** Resizes an Image with given width and height
     * @param img Input image
     * @param width Output width
     * @param height Output height
     * @return Resized image
     */
    public static Image resizeImage(Image img, int width, int height)
    {
        return img.getScaledInstance(width, height, Image.SCALE_DEFAULT);
    }

    /** Changes the brightness of an image
     * @param img Input image
     * @param brightness Brightness value (0.8f and 1.0f are ideal values for normal and lit states)
     * @return Image with changed brightness
     */
    public static Image brightenImage(Image img, float brightness)
    {
        BufferedImage buffer = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        int width, height;
        width = buffer.getWidth();
        height = buffer.getHeight();
        buffer.getGraphics().drawImage(img, 0, 0, null);

        int[] pixelBufferArray = new int[4];
        float[] hsbValues = new float[3];

        //Calculate pixel color and set new color value on buffer
        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                WritableRaster bufferRaster = buffer.getRaster();

                //Writes pixel data onto pixelBufferArray as Red, Green, Blue, Alpha
                bufferRaster.getPixel( j, i, pixelBufferArray);

                int red = pixelBufferArray[0];
                int green = pixelBufferArray[1];
                int blue = pixelBufferArray[2];
                int alpha = pixelBufferArray[3];

                //Convert color mode from RGB to HSB
                Color.RGBtoHSB(red, green, blue, hsbValues);

                // create a new color with the changed brightness
                float hue = hsbValues[0];
                float saturation = hsbValues[1];
                float bufferBrightness = hsbValues[2];
                Color c = new Color(Color.HSBtoRGB(hue, saturation, bufferBrightness * brightness));

                int[] newColorValues = new int[4];
                newColorValues[0] = c.getRed();
                newColorValues[1] = c.getGreen();
                newColorValues[2] = c.getBlue();
                newColorValues[3] = alpha;

                // set the new pixel
                bufferRaster.setPixel( j, i, newColorValues);

            }

        }

        return buffer;
    }

    /** Returns the aspect ratio of an image
     * @param img Input image
     * @return Aspect ratio
     */
    public static float getAspectRatio(Image img)
    {
        return (float)img.getWidth(null) / (float)img.getHeight(null);
    }

    /** Returns the vertically flipped equivalent of the input image
     * @param img Input image
     * @return Flipped image
     */
    public static Image flipVertical(Image img)
    {
        BufferedImage buffer = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        buffer.getGraphics().drawImage(img, 0, 0, null);

        for(int y = 0; y < buffer.getHeight() / 2; y++)
        {
            for(int x = 0; x < buffer.getWidth(); x++)
            {
                int pixelVal = buffer.getRGB(x, y);
                buffer.setRGB(x, y, buffer.getRGB(x, buffer.getHeight() - y - 1));
                buffer.setRGB(x, buffer.getHeight() - y - 1, pixelVal);
            }
        }

        return buffer;
    }
}
