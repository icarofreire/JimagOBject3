/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package jimagobject.utilities;

import java.io.File;
import java.nio.file.Files;
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

/*\/ parse dicom; */
import AC_DicomIO.AC_DcmStructure;
import AC_DicomIO.AC_DicomReader;

public final class ReadImages {

    private final HashMap<Long, byte[]> instanceNumberFileBytes = new HashMap<Long, byte[]>();
    private final Vector<byte[]> vbytesImages = new Vector<byte[]>();
    private final TreeMap<Long, byte[]> treeMapInstanceNumberFileBytes = new TreeMap<>();
    private final HashMap<String, byte[]> imagePixelData = new HashMap<String, byte[]>();
    private final String extFile = ".jpg";

    // (0018,0050) Slice Thickness;
    private String sliceThickness = null;

    // (0028,0010) Number of rows in the image;
    private String rows = null;
    // (0028,0011) Number of columns in the image;
    private String columns = null;

    public void read(File dirImages) {
        if(dirImages.exists()){
            File[] files = dirImages.listFiles();
            long index = 0;
            for(File img: files){
                index++;
                if(img.getName().indexOf(extFile) != -1 || img.getName().indexOf(extFile.toUpperCase()) != -1){
                    LinkedHashMap<Integer, String[]> atributesDicom = parseDicom(img);
                    if(atributesDicom != null){
                        String instanceNumber = (atributesDicom.containsKey((0x0020 << 16 | 0x0013))) ? (atributesDicom.get((0x0020 << 16 | 0x0013))[1]) : (null);

                        if(rows == null) rows = (atributesDicom.containsKey((0x0028 << 16 | 0x0010))) ? (atributesDicom.get((0x0028 << 16 | 0x0010))[1]) : (null);
                        if(columns == null) columns = (atributesDicom.containsKey((0x0028 << 16 | 0x0011))) ? (atributesDicom.get((0x0028 << 16 | 0x0011))[1]) : (null);
                        if(sliceThickness == null) sliceThickness = (atributesDicom.containsKey((0x0018 << 16 | 0x0050))) ? (atributesDicom.get((0x0018 << 16 | 0x0050))[1]) : (null);

                        if(imagePixelData.containsKey(img.getName())){
                            instanceNumberFileBytes.put(Long.parseLong(instanceNumber),
                            imagePixelData.get(img.getName()));
                        }
                    }else{
                        /** não dicom; */
                        try{
                            instanceNumberFileBytes.put(index, Files.readAllBytes(img.toPath()));
                            /*\/ dados dedutivos para fins de teste; */
                            rows = "500";
                            columns = "500";
                            sliceThickness = "0.5";
                        }catch(IOException e){}
                    }
                }
            }
            hashSetToTreeMap();
            readSortedDicoms();
        }
    }

    public Vector<byte[]> getVbytesImages() {
        return vbytesImages;
    }

    public long getRows() {
        return Long.parseLong(rows);
    }
    
    public long getColumns() {
        return Long.parseLong(columns);
    }

    public double getSliceThickness() {
        return Double.parseDouble(sliceThickness);
    }

    public void hashSetToTreeMap() {
        treeMapInstanceNumberFileBytes.putAll(instanceNumberFileBytes);
    }

    public void readSortedDicoms() {
        System.out.println("images:");
        for (Map.Entry<Long, byte[]> entry : treeMapInstanceNumberFileBytes.entrySet()){
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue()); 
            byte[] bytes = entry.getValue();
            vbytesImages.add(bytes);
        }
    }

    private InputStream fileToInputStream(File initialFile){
        InputStream targetStream = null;
        try{
            targetStream = new FileInputStream(initialFile);
        }catch(IOException e){}
        return targetStream;
    }

    private final LinkedHashMap<Integer, String[]> parseDicom(File dicom) {
        LinkedHashMap<Integer, String[]> attr = null;
        final AC_DicomReader dicomReader = new AC_DicomReader();
        dicomReader.readDCMFile(dicom.getAbsolutePath());
        try {
            final AC_DcmStructure dcmStructure = dicomReader.getAttirbutes();
            if(dcmStructure != null){
                attr = dcmStructure.getAttributes();
                byte[] pixels = dcmStructure.getPixelData();
                // System.out.println(">>" + pixels.length);
                imagePixelData.put(dicom.getName(), pixels);
            }else{
                /*\/ not dicom(.dcm/.ima) file; */
                // System.out.println(">> [NULL];");
            }
        } catch (java.io.IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return attr;
    }

    private final LinkedHashMap<Integer, String[]> parseDicomBreak(File dicom) {
        LinkedHashMap<Integer, String[]> attr = null;
        final AC_DicomReader dicomReader = new AC_DicomReader();
        dicomReader.setTagBreak(0x0020, 0x0013);
        dicomReader.readDCMFile(dicom.getAbsolutePath());
        try {
            final AC_DcmStructure dcmStructure = dicomReader.getAttirbutes();
            if(dcmStructure != null){
                attr = dcmStructure.getAttributes();
            }else{
                /*\/ not dicom(.dcm/.ima) file; */
                // System.out.println(">> [NULL];");
            }
        } catch (java.io.IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return attr;
    }

}