/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package jimagobject.utilities;

import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import jimagobject.utilities.ReadImages;
import jimagobject.utilities.Picture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class WriteObj {

    private final String fObj = "teste.obj";
    private Vector<byte[]> vbytesImages;
    private Vector<int[]> vRowsColumnsImages;
    private final ReadImages read = new ReadImages();

    public void getVertex() {

        // String dirImages = "/home/icaro/Downloads/dicom/ABDOMEN/VOL_ARTERIAL_0004";
        // String dirImages = "/home/icaro/Downloads/dicom/teste/teste2";
        String dirImages = "/home/icaro/Downloads/dicom/teste/teste3";

        File dir = new File(dirImages);
        if(dir.exists()){
            read.read(dir);
            vbytesImages = read.getVbytesImages();
            // vRowsColumnsImages = read.getVRowsColumnsImages();

            // write();

            /**\/ painel de exibição da conversão da imagem; */
            testeInstanciaDICOM();
        }else{
            System.out.println("Diretório de imagens não existe;");
        }

    }

    /** teste em exibir uma instância de imagem dicom convertendo para
     * imagem e aplicando o edge;
     */
    public void testeInstanciaDICOM(){
        int instance = 0;
        Vector<int[]> rowsColumns = read.getVRowsColumnsImages();
        boolean imagesPadroes = read.getImgPadrao();
        int rows = rowsColumns.get(instance)[0];
        int columns = rowsColumns.get(instance)[1];
        Picture pic = new Picture(vbytesImages.get(instance), columns, rows, imagesPadroes);
        pic.display();
    }

    /**\/ transcreve coordenadas de imagens para arquivo objeto; */
    public void write() {
        File logCheck = new File(fObj);
        FileWriter myWriter = null;
        try {
            myWriter = new FileWriter(fObj, false);
        } catch (IOException e) { e.printStackTrace(); }

        applyDimensionsImg(myWriter);

        try {
            myWriter.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void applyDimensionsImg(FileWriter myWriter){
        int z = 0;
        double spaceBetweenLayers = 0.005;
        double xCoordScale = 0.01;
        double yCoordScale = 0.01;
        double sliceThickness = read.getSliceThickness();
        vRowsColumnsImages = read.getVRowsColumnsImages();
        boolean imagesPadroes = read.getImgPadrao();
        EdgeDetector edge = new EdgeDetector();
        for(int i=0; i<vbytesImages.size(); i++){
            byte[] pixels = vbytesImages.get(i);
            /** \/ aplicação do edge detection; */
            int rows = vRowsColumnsImages.get(i)[0];
            int columns = vRowsColumnsImages.get(i)[1];
            Picture picEdgeDetect = edge.apply(pixels, columns, rows, imagesPadroes);
            for (int y = 1; y < picEdgeDetect.height() - 1; y++) {
                for (int x = 1; x < picEdgeDetect.width() - 1; x++) {

                    Color cor = picEdgeDetect.get(x, y);
                    int argb = picEdgeDetect.getRGB(x, y);
                    int alpha =  (argb >> 24) & 0xFF;
                    if (alpha == 255 && cor.equals(Color.black) ){
                        // x y z;
                        if(myWriter != null){
                            try {
                                myWriter.write("v " + (x) + " " + (y) + " " + z + "\n");
                            } catch (IOException e) { e.printStackTrace(); }
                        }
                    }
                }
            }
            z += 1; // sliceThickness
        }
    }

}