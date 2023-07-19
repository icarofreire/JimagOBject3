/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package jimagobject.utilities;

import java.nio.ByteBuffer;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferUShort;
import java.awt.image.Raster;
import java.awt.image.ComponentColorModel;
import java.awt.Transparency;
import java.awt.color.ColorSpace;

import java.io.IOException;
import java.io.InputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import java.awt.image.WritableRaster;
import java.nio.ByteOrder;
import java.awt.image.DataBufferByte;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.SampleModel;

// import jimagobject.utilities.ReadImages;

/*
 * classe para processo de conversão de dados de imagem;
 */
public final class ConvertImg {

    public BufferedImage apply(byte[] bytesImage, int width, int height) {
        BufferedImage image = null;

        // image = byteToBufferedImageIMG(bytesImage);
        // image = byteToBufferedImage(bytesImage, width, height);
        // image = createNoCopy(width, height, bytesImage);
        // image = createRGBImage(bytesImage, width, height);

        // System.out.println("IMG:" + image);
        return image;
    }

    /**[Apenas para formato de imagens não DICOM]; */
    // convert byte[] back to a BufferedImage
    public BufferedImage byteToBufferedImageIMG(byte[] bytes) {
        BufferedImage newBi = null;
        try{
            InputStream is = new ByteArrayInputStream(bytes);
            newBi = ImageIO.read(is);
        }catch(IOException e){
            e.printStackTrace();
        }
        return newBi;
    }

    /**[OK]; */
    // convert byte[] back to a BufferedImage
    public BufferedImage byteToBufferedImage(byte[] bytes, int w, int h) {
        BufferedImage newBi = null;
        newBi = createCopyUsingByteBuffer(w, h, bytes);
        return newBi;
    }

    /**
     * https://stackoverflow.com/questions/42841566/pixel-data-of-a-16-bit-dicom-image-to-bufferedimage
     */
    private static BufferedImage createCopyUsingByteBuffer(int w, int h, byte[] rawBytes) {
        short[] rawShorts = new short[rawBytes.length / 2];

        ByteBuffer.wrap(rawBytes)
                .order(java.nio.ByteOrder.BIG_ENDIAN) // Depending on the data's endianness
                .asShortBuffer()
                .get(rawShorts);

        DataBuffer dataBuffer = new DataBufferUShort(rawShorts, rawShorts.length);
        int stride = 1;
        WritableRaster raster = Raster.createInterleavedRaster(dataBuffer, w, h, w * stride, stride, new int[] {0}, null);
        ColorModel colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY), false, false, Transparency.OPAQUE, DataBuffer.TYPE_USHORT);

        return new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), null);
    }

    /**[TESTAR MAIS]; */
    // private BufferedImage createRGBImage(byte[] bytes, int width, int height) {
    //     DataBufferByte buffer = new DataBufferByte(bytes, bytes.length);
    //     ColorModel cm = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[]{8, 8, 8}, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
    //     return new BufferedImage(cm, Raster.createInterleavedRaster(buffer, width, height, width * 3, 3, new int[]{0, 1, 2}, null), false, null);
    // }

    /**[OK]; */
    private BufferedImage createNoCopy(int w, int h, byte[] rawBytes) {
        DataBuffer dataBuffer = new DataBufferByte(rawBytes, rawBytes.length);

        int stride = 2;
        SampleModel sampleModel = new MyComponentSampleModel(w, h, stride);
        WritableRaster raster = Raster.createWritableRaster(sampleModel, dataBuffer, null);

        ColorModel colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY), false, false, Transparency.OPAQUE, DataBuffer.TYPE_USHORT);

        return new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), null);
    }

    private class MyComponentSampleModel extends ComponentSampleModel {
        public MyComponentSampleModel(int w, int h, int stride) {
            super(DataBuffer.TYPE_USHORT, w, h, stride, w * stride, new int[] {0});
        }

        @Override
        public Object getDataElements(int x, int y, Object obj, DataBuffer data) {
            if ((x < 0) || (y < 0) || (x >= width) || (y >= height)) {
                throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
            }

            // Simplified, as we only support TYPE_USHORT
            int numDataElems = getNumDataElements();
            int pixelOffset = y * scanlineStride + x * pixelStride;

            short[] sdata;

            if (obj == null) {
                sdata = new short[numDataElems];
            }
            else {
                sdata = (short[]) obj;
            }

            for (int i = 0; i < numDataElems; i++) {
                sdata[i] = (short) (data.getElem(0, pixelOffset) << 8 | data.getElem(0, pixelOffset + 1));
                // If little endian, swap the element order, like this:
            //    sdata[i] = (short) (data.getElem(0, pixelOffset + 1) << 8 | data.getElem(0, pixelOffset));
            }

            return sdata;
        }
    }

}