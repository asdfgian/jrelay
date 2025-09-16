package com.jrelay.ui.controllers;

import java.awt.Desktop;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JLayeredPane;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;

import com.jrelay.core.utils.i18n.LangManager;
import com.jrelay.ui.components.dialogs.MessageDialog;
import com.jrelay.ui.components.dialogs.MessageDialog.Location;
import com.jrelay.ui.components.dialogs.MessageDialog.Type;
import com.jrelay.ui.components.shared.ConsolePanel;
import com.jrelay.ui.shared.styles.Breakpoint;
import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.shared.utils.animate.Animator;
import com.jrelay.ui.views.Footer;
import com.jrelay.ui.views.Frame;
import com.jrelay.ui.views.SideBar;
import com.jrelay.ui.workbech.WorkbenchHttp;

public class FooterController {
    private final Frame frame;
    private final Footer footer;
    private final SideBar sideBar;

    public FooterController(Frame frame, Footer footer, SideBar sideBar) {
        this.frame = frame;
        this.footer = footer;
        this.sideBar = sideBar;
        initListeners();
    }

    /**
     * Initializes all UI listeners required for the component.
     * <p>
     * This method delegates the setup of specific listeners to
     * dedicated helper methods:
     * <ul>
     * <li>{@link #setupToggleButton()} – Initializes the toggle button
     * listener</li>
     * <li>{@link #setupDebugConsoleButtonListener()} – Initializes the debug
     * console button listener</li>
     * <li>{@link #setupContributeButton()} – Initializes the contribute button
     * listener</li>
     * <li>{@link #setupOrientationButton()} – Initializes the orientation button
     * listener</li>
     * </ul>
     * </p>
     *
     * @since 22-08-2025
     * @author ASDFG14N
     */
    private void initListeners() {
        setupToggleButton();
        setupDebugConsoleButtonListener();
        setupContributeButton();
        setupOrientationButton();
    }

    /**
     * Configures the toggle button that controls the sidebar's open and close
     * behavior.
     * <p>
     * When the button is clicked:
     * <ul>
     * <li>Checks if an existing animation is running to prevent overlapping
     * animations.</li>
     * <li>Determines whether the sidebar is opening or closing.</li>
     * <li>Animates the sidebar width transition smoothly from its current state to
     * the target width.</li>
     * <li>Updates the tooltip text to reflect the new state (expand or
     * collapse).</li>
     * </ul>
     * </p>
     *
     * <p>
     * The animation is performed using an {@link Animator} with a duration of 300
     * ms.
     * </p>
     *
     * @since 22-08-2025
     * @author ASDFG14N
     */
    private void setupToggleButton() {
        final JToggleButton toggleButton = footer.getToggleSideBarButton();

        final int openWidth = sideBar.getOpenWidth();
        final int closeWidth = sideBar.getCloseWidth();

        toggleButton.addActionListener(e -> {
            if (sideBar.getAnimator() != null && sideBar.getAnimator().isRunning())
                return;

            boolean opening = !sideBar.isOpen();
            int start = opening ? closeWidth : openWidth;
            int end = opening ? openWidth : closeWidth;

            sideBar.setOpen(false);

            Animator animator = new Animator(300)
                    .onTimingEvent(fraction -> {
                        int width = (int) (start + (end - start) * fraction);
                        frame.getLayout().setComponentConstraints(sideBar, "w " + width + "!, h 100%");
                        sideBar.revalidate();
                    })
                    .onEnd(() -> {
                        sideBar.setOpen(opening);
                        Style.setToolTip(toggleButton,
                                opening ? LangManager.text("footer.toggleSideBarButton.collapse.placeholder.text")
                                        : LangManager.text("footer.toggleSideBarButton.expand.placeholder.text"));
                    });

            sideBar.setAnimator(animator);
            animator.start();
        });
    }

    private void setupDebugConsoleButtonListener() {
        final var openConsoleButton = footer.getConsoleButton();
        final var consolePanel = frame.getConsolePanel();
        final var closeConsoleButton = consolePanel.getCloseButon();

        JLayeredPane layeredPane = frame.getLayeredPane();
        layeredPane.add(consolePanel, JLayeredPane.POPUP_LAYER);
        consolePanel.setVisible(false);

        Runnable repositionConsole = () -> {
            int footerHeight = footer.getHeight();
            int contentHeight = frame.getContentPane().getHeight();
            int contentWidth = frame.getContentPane().getWidth();

            consolePanel.setBounds(
                    0,
                    contentHeight - footerHeight - consolePanel.getHeight(),
                    contentWidth,
                    consolePanel.getHeight());
            consolePanel.revalidate();
        };

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                repositionConsole.run();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                repositionConsole.run();
            }
        });

        openConsoleButton.addActionListener(e -> {
            if (!consolePanel.isVisible()) {
                consolePanel.setVisible(true);
                Animator animator = new Animator(300)
                        .onTimingEvent(fraction -> {
                            int h = (int) (ConsolePanel.HEIGHT * fraction);
                            consolePanel.setSize(frame.getWidth(), h);
                            repositionConsole.run();
                        });
                animator.start();
            }
        });

        closeConsoleButton.addActionListener(e -> {
            Animator animator = new Animator(300)
                    .onTimingEvent(fraction -> {
                        int h = (int) (ConsolePanel.HEIGHT * (1 - fraction));
                        consolePanel.setSize(frame.getWidth(), h);
                        repositionConsole.run();
                    })
                    .onEnd(() -> consolePanel.setVisible(false));
            animator.start();
        });
    }

    private void setupOrientationButton() {
        footer.getOrientationButton().addActionListener(e -> {
            if (frame.getWorkbench() instanceof WorkbenchHttp workbenchHttp) {
                final var tabContentHttp = workbenchHttp.getSelectedTabContent();
                if (tabContentHttp.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
                    tabContentHttp.setVerticalOrientation();
                } else {
                    Breakpoint breakpoint = Breakpoint.fromWidth(frame.getWidth());

                    switch (breakpoint) {
                        case MD, LG -> {
                            MessageDialog.showMessage(
                                    Location.BOTTOM_CENTER,
                                    Type.ERROR,
                                    LangManager.text(
                                            "footerController.setupOrientationButton.messageDialog.message.text"));
                        }
                        case XL, XXL -> {
                            tabContentHttp.setHorizontalOrientation();
                        }
                        default -> {
                        }
                    }
                }
            }
        });
    }

    private void setupContributeButton() {
        footer.getContributeButton().addActionListener(e -> {
            String url = "https://github.com/ASDFG14N/backend-spring";
            var os = AppController.osManager.getOperatingSystem();
            if (os.toString().equals("LINUX")) {
                try {
                    Runtime.getRuntime().exec(new String[] { "xdg-open", url });
                } catch (IOException err) {
                    err.printStackTrace();
                }
            }
            if (os.toString().equals("WIN")) {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    try {
                        Desktop.getDesktop().browse(new URI(url));
                    } catch (IOException | URISyntaxException err) {
                        err.printStackTrace();
                    }
                }
            }
        });
    }

}
