/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package jimagobject.utilities;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import jimagobject.utilities.GUIPanel;
import jimagobject.utilities.ConvertImg;

/*
 * ;
 */
public final class Picture {

    private final ConvertImg conv = new ConvertImg();
    private BufferedImage image;
    private boolean isOriginUpperLeft = true;  // location of origin
    private int width, height;           // width and height


    public Picture(byte[] bytesImage, int width, int height) {
        image = conv.apply(bytesImage, width, height);
        if(image != null){
            this.width  = image.getWidth();
            this.height = image.getHeight();
        }
    }

    public Picture(byte[] bytesImage) {
        image = conv.byteToBufferedImageIMG(bytesImage);
        if(image != null){
            this.width  = image.getWidth();
            this.height = image.getHeight();
        }
    }

    public Picture(BufferedImage image) {
        this.image = image;
        if(image != null){
            this.width  = image.getWidth();
            this.height = image.getHeight();
        }
    }

    /**
     * Creates a {@code width}-by-{@code height} picture, with {@code width} columns
     * and {@code height} rows, where each pixel is black.
     *
     * @param width the width of the picture
     * @param height the height of the picture
     * @throws IllegalArgumentException if {@code width} is negative or zero
     * @throws IllegalArgumentException if {@code height} is negative or zero
     */
    public Picture(int width, int height) {
        if (width  <= 0) throw new IllegalArgumentException("width must be positive");
        if (height <= 0) throw new IllegalArgumentException("height must be positive");
        this.width  = width;
        this.height = height;
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    public BufferedImage getImage() {
        return image;
    }

    /**
     * Returns the height of the picture.
     *
     * @return the height of the picture (in pixels)
     */
    public int height() {
        return height;
    }

   /**
     * Returns the width of the picture.
     *
     * @return the width of the picture (in pixels)
     */
    public int width() {
        return width;
    }

    private void validateRowIndex(int row) {
        if (row < 0 || row >= height())
            throw new IllegalArgumentException("row index must be between 0 and " + (height() - 1) + ": " + row);
    }

    private void validateColumnIndex(int col) {
        if (col < 0 || col >= width())
            throw new IllegalArgumentException("column index must be between 0 and " + (width() - 1) + ": " + col);
    }

    /**
     * Returns the color of pixel ({@code col}, {@code row}) as a {@link java.awt.Color}.
     *
     * @param col the column index
     * @param row the row index
     * @return the color of pixel ({@code col}, {@code row})
     * @throws IllegalArgumentException unless both {@code 0 <= col < width} and {@code 0 <= row < height}
     */
    public Color get(int col, int row) {
        validateColumnIndex(col);
        validateRowIndex(row);
        int argb = getRGB(col, row);
        return new Color(argb, true);
    }

   /**
     * Returns the color of pixel ({@code col}, {@code row}) as an {@code int}.
     * Using this method can be more efficient than {@link #get(int, int)} because
     * it does not create a {@code Color} object.
     *
     * @param col the column index
     * @param row the row index
     * @return the integer representation of the color of pixel ({@code col}, {@code row})
     * @throws IllegalArgumentException unless both {@code 0 <= col < width} and {@code 0 <= row < height}
     */
    public int getRGB(int col, int row) {
        validateColumnIndex(col);
        validateRowIndex(row);
        if (isOriginUpperLeft) return image.getRGB(col, row);
        else                   return image.getRGB(col, height - row - 1);
    }

   /**
     * Sets the color of pixel ({@code col}, {@code row}) to given color.
     *
     * @param col the column index
     * @param row the row index
     * @param color the color
     * @throws IllegalArgumentException unless both {@code 0 <= col < width} and {@code 0 <= row < height}
     * @throws IllegalArgumentException if {@code color} is {@code null}
     */
    public void set(int col, int row, Color color) {
        validateColumnIndex(col);
        validateRowIndex(row);
        if (color == null) throw new IllegalArgumentException("color argument is null");
        int rgb = color.getRGB();
        setRGB(col, row, rgb);
    }

   /**
     * Sets the color of pixel ({@code col}, {@code row}) to given color.
     *
     * @param col the column index
     * @param row the row index
     * @param rgb the integer representation of the color
     * @throws IllegalArgumentException unless both {@code 0 <= col < width} and {@code 0 <= row < height}
     */
    public void setRGB(int col, int row, int rgb) {
        validateColumnIndex(col);
        validateRowIndex(row);
        if (isOriginUpperLeft) image.setRGB(col, row, rgb);
        else                   image.setRGB(col, height - row - 1, rgb);
    }

    public void display(){
        /*\/ exibir imagem em painel gráfico; */
        GUIPanel guip = new GUIPanel();
        if(image != null){
            guip.setImage(image);
            guip.display();
        }
    }

    public void toFile(BufferedImage newBi, String nameFile, String extFile){
        // save it
        try{
            Path target = Paths.get(nameFile);
            ImageIO.write(newBi, extFile/* "png"*/, target.toFile());
        }catch(IOException e){}
    }

}