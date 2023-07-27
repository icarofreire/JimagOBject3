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
import jimagobject.utilities.Marching.MarchingCubes;
import jimagobject.utilities.Marching.Vertex;
import jimagobject.utilities.Marching.VolumeGenerator;

import jimagobject.utilities.Meshcpp.MarchingCubesTransCpp;
// import jimagobject.utilities.Meshcpp.Point;

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
    private final MarchingCubes march = new MarchingCubes();

    private final MarchingCubesTransCpp meshcpp = new MarchingCubesTransCpp();

    public void getVertex() {

        // String dirImages = "/home/icaro/Downloads/dicom/ABDOMEN/VOL_ARTERIAL_0004";
        String dirImages = "/home/icaro/Downloads/dicom/teste/teste2";
        // String dirImages = "/home/icaro/Downloads/dicom/teste/teste3";

        File dir = new File(dirImages);
        if(dir.exists()){
            read.read(dir);
            vbytesImages = read.getVbytesImages();
            // vRowsColumnsImages = read.getVRowsColumnsImages();

            write();
            // teste2();

            /**\/ painel de exibição da conversão da imagem; */
            // testeInstanciaDICOM();
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

        // applyDimensionsImg(myWriter);
        // teste2(myWriter);
        // teste3(myWriter);

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
        int[][][] xpicels = null; /* << para testar outra estratégia de mesh; */
        int i=0;{
        // for(int i=0; i<vbytesImages.size(); i++){
            byte[] pixels = vbytesImages.get(i);
            /** \/ aplicação do edge detection; */
            int rows = vRowsColumnsImages.get(i)[0];
            int columns = vRowsColumnsImages.get(i)[1];
            Picture picEdgeDetect = edge.apply(pixels, columns, rows, imagesPadroes);
            xpicels = new int[picEdgeDetect.width()][picEdgeDetect.width()][picEdgeDetect.width()];
            for (int y = 1; y < picEdgeDetect.height() - 1; y++) {
                for (int x = 1; x < picEdgeDetect.width() - 1; x++) {

                    Color cor = picEdgeDetect.get(x, y);
                    int argb = picEdgeDetect.getRGB(x, y);
                    int alpha =  (argb >> 24) & 0xFF;
                    if (alpha == 255 && cor.equals(Color.black) ){
                        // x y z;
                        if(myWriter != null){
                            try {
                                xpicels[y][x][z] = argb;
                                myWriter.write("v " + (x) + " " + (y) + " " + z + "\n");
                            } catch (IOException e) { e.printStackTrace(); }
                        }
                    }
                }
            }
            z += 1; // sliceThickness

            // teste4(pixels, myWriter);

            /**\/ testar outra estratégia de mesh; */
            if(xpicels != null){
                float isovalue = 72.0f; // << default in project;
                Vector<Vector<Vector<Float>>> scalar = meshcpp.createScalarFunction(xpicels);
                Vector<Vector<Point>> triangles = meshcpp.triangulate_field(scalar, isovalue);
            }
        }
    }

    public void teste2(FileWriter myWriter){
        int z = 1;
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

            int[][] allPoints = new int[picEdgeDetect.height()][picEdgeDetect.width()];

            for (int y = 0; y < picEdgeDetect.height(); y++) {
                for (int x = 0; x < picEdgeDetect.width(); x++) {
                    allPoints[y][x] = picEdgeDetect.getRGB(x, y);
                }
            }

            // march.calculatingFacets(allPoints,  myWriter);
        }

    }

    public void teste3(FileWriter myWriter){
        int z = 1;
        double spaceBetweenLayers = 0.005;
        double xCoordScale = 0.01;
        double yCoordScale = 0.01;
        double sliceThickness = read.getSliceThickness();
        vRowsColumnsImages = read.getVRowsColumnsImages();
        boolean imagesPadroes = read.getImgPadrao();
        EdgeDetector edge = new EdgeDetector();


        // byte[] pixels = vbytesImages.get(i);
        // /** \/ aplicação do edge detection; */
        // int rows = vRowsColumnsImages.get(i)[0];
        // int columns = vRowsColumnsImages.get(i)[1];
        // int[][] yxs = new int[picEdgeDetect.height()][picEdgeDetect.height()];
        // Picture picEdgeDetect = edge.apply(pixels, columns, rows, imagesPadroes);
        // for (int y = 1; y < picEdgeDetect.height() - 1; y++) {
        //     for (int x = 1; x < picEdgeDetect.width() - 1; x++) {
        //         // yxs[y][x] = 
        //         Vertex vert = new Vertex(x, y, z);
        //     }
        // }

        final int H = 1;
        Vector<Vertex> vVert = new Vector<Vertex>();


        // int i=0;
        for(int i=0; i<vbytesImages.size(); i++)
        {
            byte[] pixels = vbytesImages.get(i);
            /** \/ aplicação do edge detection; */
            int rows = vRowsColumnsImages.get(i)[0];
            int columns = vRowsColumnsImages.get(i)[1];
            Picture picEdgeDetect = edge.apply(pixels, columns, rows, imagesPadroes);

            // int[][] allPoints = new int[picEdgeDetect.height()][picEdgeDetect.width()];

            for (int y = 1; y < picEdgeDetect.height() - 1; y++) {
                for (int x = 1; x < picEdgeDetect.width() - 1; x++) {
                    // allPoints[y][x] = picEdgeDetect.getRGB(x, y);
                    Vertex vert = new Vertex(x, y, z);
                    // vVert.add( new Vertex(x - H, y - H, z) );
                    // vVert.add( new Vertex(x + H, y - H, z) );
                    // vVert.add( new Vertex(x - H, y + H, z) );
                    // vVert.add( new Vertex(x + H, y + H, z) );
                    if(!vVert.contains(vert)) vVert.add(vert);
                    // if(myWriter != null){
                    //     try {
                    //         myWriter.write("f " + (x) + " " + (y) + " " + (z) + "\n");
                    //         // myWriter.write("f " + (x + H) + " " + (y - H) + " " + (z) + "\n");
                    //         // myWriter.write("f " + (x - H) + " " + (y + H) + " " + (z) + "\n");
                    //         // myWriter.write("f " + (x + H) + " " + (y + H) + " " + (z) + "\n");
                    //     } catch (IOException e) { e.printStackTrace(); }
                    // }
                }
            }
            z++;
            march.calculatingFacets(vVert,  myWriter);
        }

        System.out.println(">>FIM;");
    }

    public int[] arrayBytesTArrayInt(byte[] gpixels){
        java.nio.IntBuffer intBuf =
        java.nio.ByteBuffer.wrap(gpixels)
            .order(java.nio.ByteOrder.LITTLE_ENDIAN)
            .asIntBuffer();
        int[] arrayPixels = new int[intBuf.remaining()];
        intBuf.get(arrayPixels);
        return arrayPixels;
    }

    public void teste4(byte[] gpixels, FileWriter myWriter){
        int z = 1;
        int[] arrayPixels = arrayBytesTArrayInt(gpixels);

        // arrayPixels = VolumeGenerator.generateScalarFieldInt(arrayPixels);
        // Vector<Integer> vecArrayPixels = VolumeGenerator.generateScalarVolume(arrayPixels);

        int[] size = {64, 64, 64};
        float[] voxSize = {1.0f, 1.0f, 1.0f};
        Vector<float[]> vmarch = march.marchingCubesInt(
            arrayPixels,
            new int[]{size[0], size[1], 2/*paddedSegmentSize*/},
            size[2],
            voxSize,
            5,/*0.5*/
            z
        );
        int id = 0;
        System.out.println("march: " + vmarch.size() + " -> " +  vmarch.get(id).length + " : " + vmarch.get(id)[0] + " : " + vmarch.get(id)[1] + " : " + vmarch.get(id)[2] );
        // for(float[] v: vmarch){
        //     if(myWriter != null){
        //         try {
        //             myWriter.write("f " + (v[0]) + " " + (v[1]) + " " + (v[2]) + "\n");
        //         } catch (IOException e) { e.printStackTrace(); }
        //     }
        // }
    }

}