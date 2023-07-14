/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package jimagobject.utilities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;

import jimagobject.utilities.ReadImages;

public final class GUIPanel {

    private Graphics2D g2d;
    private BufferedImage image;

    // public GUIPanel(Graphics2D g2d){
    //     this.g2d = g2d;
    // }

    // public void display() {
    //     display(this.g2d);
    // }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public void display(Graphics2D g2d) {
        this.g2d = g2d;
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame();
                frame.add(new TestPane());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public class TestPane extends JPanel {

        public TestPane() {
            setBackground(Color.BLACK);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(1080, 1920);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Graphics2D g2d = (Graphics2D) g.create();
            // g2d.setPaint(Color.WHITE);
            // g2d.drawLine(500, 500, 500, 800);
            g2d.drawImage(image, 0, 0, this);
            g2d.dispose();
        }

    }

}