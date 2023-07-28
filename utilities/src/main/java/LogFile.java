/*
 * 
 */
package jimagobject.utilities;

import java.io.File;
import java.io.FileWriter;
import java.util.Vector;
import java.util.Scanner;
// import java.util.logging.FileHandler;
// import java.util.logging.Logger;
// import java.util.logging.SimpleFormatter;
import java.io.FileNotFoundException;
import java.io.IOException;

import jimagobject.utilities.Meshcpp.Point;

/**
 * classe para escrita de log no sistema;
 */
public final class LogFile {

    private final String nameFileLog = "points";

    /*\/ criar log; */
    public void createPointsInFile(int x, int y, int z, int v, boolean append) {
        File logCheck = new File(nameFileLog);
        // boolean append = logCheck.exists();
        try {
            FileWriter myWriter = new FileWriter(nameFileLog, append);
            myWriter.write( x + " " + y + " " + z + " " + v + "\n");
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**\/ ler log de dados por páginas de números de linhas; */
    public Vector<Vector<Point>> readFileByPages(int nPages) {
        Vector<Vector<Point>> vPagesPoints = new Vector<Vector<Point>>();
        int nByPage = 100;
        int minp = 1;
        int maxp = nByPage;
        for(int i=0; i<nPages; i++){
            // System.out.println("Page: " + minp + " -> " + maxp);
            Vector<Point> pointsFile = readFilePoints(minp, maxp);
            vPagesPoints.add(pointsFile);
            // System.out.println("Points:" + pointsFile.size());

            minp += maxp;
            maxp *= 2;
        }
        return vPagesPoints;
    }

    /*\/ ler log; */
    public Vector<Point> readFilePoints(int minLinhasLer, int maxLinhasLer) {
        Vector<Point> vp = new Vector<Point>();
        File file = new File(nameFileLog);
        if(file.exists()){
            try{
                //Creating Scanner instance to read File in Java
                Scanner scnr = new Scanner(file);
            
                //Reading each line of the file using Scanner class
                int lineNumber = 1;
                while(scnr.hasNextLine() && (lineNumber <= maxLinhasLer)){
                    String line = scnr.nextLine();
                    if(lineNumber >= minLinhasLer){
                        String[] part = line.split(" ");
                        Point point = new Point( Float.parseFloat(part[0]), Float.parseFloat(part[1]), Float.parseFloat(part[2]), Float.parseFloat(part[3]) );
                        vp.add(point);
                        // System.out.println("line " + lineNumber + " :" + line );
                    }
                    lineNumber++;
                }
            }catch(FileNotFoundException e){
                e.printStackTrace();
            }
        }
        return vp;
    }

    public void deletarLog() {
        File file = new File(nameFileLog);
        if(file.exists()){
            file.delete();
        }
    }

}