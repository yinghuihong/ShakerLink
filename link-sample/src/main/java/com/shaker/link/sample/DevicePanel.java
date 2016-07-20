package com.shaker.link.sample;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by yinghuihong on 16/7/20.
 */
public class DevicePanel extends JPanel {

    private BufferedImage image;

    public DevicePanel() {
        try {
            image = ImageIO.read(ClassLoader.getSystemResourceAsStream("earthshaker2.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.BLACK);
        g.drawImage(image, 0, 0, null);
    }
}
