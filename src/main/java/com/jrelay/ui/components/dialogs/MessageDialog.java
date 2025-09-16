package com.jrelay.ui.components.dialogs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.jrelay.ui.controllers.AppController;
import com.jrelay.ui.shared.styles.Colors;
import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.shared.utils.animate.Animator;
import com.jrelay.ui.shared.utils.template.Struct;

import net.miginfocom.swing.MigLayout;

public class MessageDialog extends JDialog implements Struct {

    private static final int PADDING = 25;
    private final Dimension dimension;
    private int x = 0;
    private int top = 0;
    private boolean topToBottom = false;

    private final JPanel content = new JPanel();
    private Animator animator;
    private boolean showing;
    private Thread thread;
    private Location location = Location.BOTTOM_CENTER;
    private final Type type;
    private static final int animate = 10;
    private final Frame frame;
    private final JLabel messageLabel = new JLabel();

    public enum Type {
        SUCCESS, ERROR
    }

    public enum Location {
        TOP_CENTER, BOTTOM_CENTER
    }

    public MessageDialog(Frame frame, Type type, String msg) {
        super(frame);
        this.type = type;
        this.messageLabel.setText(msg);
        int textWidth = messageLabel.getFontMetrics(messageLabel.getFont()).stringWidth(msg);
        this.dimension = new Dimension(textWidth + PADDING, 30);
        this.frame = frame;
        this.build();
        initAnimator();
    }

    public MessageDialog(Frame frame, Location location, Type type, String msg) {
        super(frame);
        this.frame = frame;
        this.location = location;
        this.type = type;
        this.messageLabel.setText(msg);
        int textWidth = messageLabel.getFontMetrics(messageLabel.getFont()).stringWidth(msg);
        this.dimension = new Dimension(textWidth + PADDING, 30);
        this.build();
        initAnimator();
    }

    @Override
    public void initComponents() {
        //
    }

    @Override
    public void configureStyle() {
        this.setSize(dimension);
        this.setUndecorated(true);
        this.setFocusableWindowState(false);
        Style.setLayout(content, new MigLayout("fill, aligny 50%"));
        Style.setRoundComponent(content);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        Style.setFontSize(messageLabel, 12.5f);
        if (type == Type.SUCCESS) {
            Style.setBackgroundColor(content, Color.decode("#a9e9a4"));
            Style.setTextColor(messageLabel, Color.decode("#40694f"));
        } else if (type == Type.ERROR) {
            Style.setBackgroundColor(content, Colors.WARNING_COLOR);
            Style.setTextColor(messageLabel, Color.decode("#ffffff"));
        }
    }

    @Override
    public void compose() {
        content.add(messageLabel, "grow");
        this.add(content);
    }

    private void initAnimator() {
        animator = new Animator(500)
                .onTimingEvent((fraction) -> {
                    if (showing) {
                        float alpha = 1f - fraction;
                        int y = (int) ((1f - fraction) * animate);
                        if (topToBottom) {
                            this.setLocation(x, top + y);
                        } else {
                            this.setLocation(x, top - y);
                        }
                        this.setOpacity(alpha);
                    } else {
                        float alpha = fraction;
                        int y = (int) (fraction * animate);
                        if (topToBottom) {
                            this.setLocation(x, top + y);
                        } else {
                            this.setLocation(x, top - y);
                        }
                        this.setOpacity(alpha);
                    }
                })
                .onBegin(() -> {
                    if (!showing) {
                        this.setOpacity(0f);
                        int y = 0;
                        if (location == Location.TOP_CENTER) {
                            x = frame.getX() + ((frame.getWidth() - this.getWidth()) / 2);
                            y = frame.getY();
                            topToBottom = true;
                        } else if (location == Location.BOTTOM_CENTER) {
                            x = frame.getX() + ((frame.getWidth() - this.getWidth()) / 2);
                            y = frame.getY() + frame.getHeight() - this.getHeight();
                            topToBottom = false;
                        }
                        top = y;
                        this.setLocation(x, y);
                        this.setVisible(true);
                    }
                })
                .onEnd(() -> {
                    showing = !showing;
                    if (showing) {
                        thread = new Thread(() -> {
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            closeNotification();
                        });
                        thread.start();
                    } else {
                        this.dispose();
                    }
                });
    }

    private void closeNotification() {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
        if (animator.isRunning()) {
            if (!showing) {
                showing = true;
                animator.start();
            }
        } else {
            showing = true;
            animator.start();
        }
    }

    private void showMessage() {
        animator.start();
    }

    public static void showMessage(Type type, String msg) {
        final var obj = new MessageDialog(AppController.getFrame(), type, msg);
        obj.showMessage();
    }

    public static void showMessage(Location loc, Type type, String msg) {
        final var obj = new MessageDialog(AppController.getFrame(), loc, type, msg);
        obj.showMessage();
    }

}
