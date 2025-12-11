package com.jrelay.ui.views;

import javax.swing.*;

import com.jrelay.ui.components.shared.ConsolePanel;
import com.jrelay.ui.shared.styles.Colors;
import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.shared.utils.UiUtils;
import com.jrelay.ui.shared.utils.template.Struct;
import com.jrelay.ui.workbech.Workbench;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.miginfocom.swing.MigLayout;

@Getter
public class Frame extends JFrame implements Struct {

    private boolean glassPaneActive = false;

    private MigLayout layout;
    private final JPanel mainContainer = new JPanel();
    private final SideBar sideBar;
    @Setter
    private Workbench workbench;
    private final ConsolePanel consolePanel;
    private final Footer footer;

    public Frame(SideBar sideBar, Workbench workbench, Footer footer) {
        super(UiUtils.APP_NAME + " (" + UiUtils.APP_VERSION + ")");
        this.sideBar = sideBar;
        this.workbench = workbench;
        this.consolePanel = new ConsolePanel();
        this.footer = footer;
        this.build();
    }

    @Override
    public void initComponents() {
        layout = new MigLayout("fill, insets 0", "[]1[grow, fill]", "[grow, fill]1[]");
    }

    @Override
    public void configureStyle() {
        Style.setFrameMinSize(this);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setResizable(true);
        this.setLocationRelativeTo(null);
        this.setGlassPane(new GlassPane());
        this.setIconImage(new ImageIcon(this.getClass().getResource("/img/jrelay-500x500.png")).getImage());
        Style.setLayout(mainContainer, layout);
        Style.setBackgroundColor(mainContainer, Colors.SECONDARY_COLOR);
    }

    @Override
    public void compose() {
        mainContainer.add(sideBar, "w " + sideBar.getCloseWidth() + "!, growy");
        mainContainer.add((Component) workbench, "grow, wrap");
        mainContainer.add(footer, "w 100%, h 30!, spanx 2");

        this.add(mainContainer);
    }

    public void showGlassPane() {
        ((GlassPane) getGlassPane()).showBlur();
        this.glassPaneActive = true;
    }

    public void hideGlassPane() {
        ((GlassPane) getGlassPane()).hideBlur();
        this.glassPaneActive = false;
    }

    private class GlassPane extends JPanel {
        private BufferedImage blurredImage;
        private final Robot robot;

        @SneakyThrows
        private GlassPane() {
            robot = new Robot();
            setOpaque(false);
            setVisible(false);
        }

        public void showBlur() {
            try {
                Point location = mainContainer.getLocationOnScreen();
                Rectangle bounds = new Rectangle(location.x, location.y, getWidth(), getHeight());
                BufferedImage screenshot = robot.createScreenCapture(bounds);
                blurredImage = blur(screenshot);
                setVisible(true);
                repaint();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void hideBlur() {
            setVisible(false);
            blurredImage = null;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (blurredImage != null) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.drawImage(blurredImage, 0, 0, null);
                g2d.dispose();
            }
        }

        private BufferedImage blur(BufferedImage image) {
            float[] kernel = {
                    1f / 16f, 2f / 16f, 1f / 16f,
                    2f / 16f, 4f / 16f, 2f / 16f,
                    1f / 16f, 2f / 16f, 1f / 16f
            };
            Kernel gaussianKernel = new Kernel(3, 3, kernel);
            ConvolveOp op = new ConvolveOp(gaussianKernel, ConvolveOp.EDGE_NO_OP, null);

            BufferedImage blurred = image;
            int iterations = 4;

            for (int i = 0; i < iterations; i++) {
                blurred = op.filter(blurred, null);
            }

            return blurred;
        }
    }

}
