package com.jrelay.ui.controllers;

import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import com.jrelay.core.controller.CollectionController;
import com.jrelay.core.controller.EnvironmentController;
import com.jrelay.core.controller.PreferenceController;
import com.jrelay.core.controller.RequestHistoryController;
import com.jrelay.core.models.Preference.Theme;
import com.jrelay.core.models.request.Request;
import com.jrelay.core.models.response.Response;
import com.jrelay.core.os.OsManager;
import com.jrelay.core.utils.i18n.LangManager;
import com.jrelay.ui.components.dialogs.ConfirmDialog;
import com.jrelay.ui.components.dialogs.ConfirmDialog.Option;
import com.jrelay.ui.components.shared.ClosableTab;
import com.jrelay.ui.components.shared.TabGroup;
import com.jrelay.ui.shared.styles.Breakpoint;
import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.shared.utils.animate.Animator;
import com.jrelay.ui.views.Footer;
import com.jrelay.ui.views.Frame;
import com.jrelay.ui.views.SideBar;
import com.jrelay.ui.workbech.Workbench;
import com.jrelay.ui.workbech.WorkbenchGraphQl;
import com.jrelay.ui.workbech.WorkbenchHttp;
import com.jrelay.ui.workbech.WorkbenchMcp;
import com.jrelay.ui.workbech.WorkbenchWebSocket;
import com.jrelay.ui.workbech.http.TabContentHttp;

import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import lombok.Getter;

public class AppController {

    public static OsManager osManager;
    private static SideBar sideBar;
    private static List<Workbench> workbenchs;
    private Workbench workbench;
    private static WorkbenchHttp workbenchHttp;
    private WorkbenchGraphQl workbenchGraphQl;
    private WorkbenchWebSocket workbenchWebSocket;
    private WorkbenchMcp workbenchMcp;
    private Footer footer;
    @Getter
    private static Frame frame;

    public static PreferenceController prefController;
    public static EnvironmentController environmentController;
    public static CollectionController collectionController;
    public static RequestHistoryController requestHistoryController;

    public AppController() {
        setupAppEnvironment();
        initUIComponents();
        setupMainFrame();
        initControllers();
        setupInteractions();
        setupActionBeforeClosing();
    }

    /**
     * Initializes the core application environment and dependencies.
     * <p>
     * This method sets up key components including the OS manager, user preference
     * storage,
     * collection storage, and request history storage. It also applies the detected
     * UI theme
     * based on the current operating system environment.
     * <p>
     * This is typically called during application startup to prepare essential
     * services
     * and visual settings.
     *
     * @author @ASDG14N
     * @since 03-08-2025
     */
    private void setupAppEnvironment() {
        osManager = OsManager.getInstance();
        prefController = new PreferenceController();
        environmentController = new EnvironmentController();
        collectionController = new CollectionController();
        requestHistoryController = new RequestHistoryController();

        installTheme(prefController.getTheme());
    }

    /**
     * Applies the specified UI theme to the application.
     * <p>
     * Installs either a dark or light theme based on the given {@link Theme} enum
     * value
     * by delegating to the {@link Style} utility class.
     * <p>
     * This method ensures the visual appearance of the application matches user or
     * system preferences.
     *
     * @param theme the theme to be applied (DARK or LIGHT)
     * @author @ASDG14N
     * @since 03-08-2025
     */
    private void installTheme(Theme theme) {
        switch (theme) {
            case DARK -> Style.installDarkTheme();
            case LIGHT -> Style.installLightTheme();
            case SYS -> {
                Theme osTheme = osManager.getTheme();
                if (osTheme == Theme.DARK) {
                    Style.installDarkTheme();
                } else {
                    Style.installLightTheme();
                }
            }
        }
    }

    private void initWorkbenches() {
        workbenchHttp = new WorkbenchHttp(environmentController.findAll(), new TabContentHttp());
        workbenchGraphQl = new WorkbenchGraphQl();
        workbenchWebSocket = new WorkbenchWebSocket();
        workbenchMcp = new WorkbenchMcp();
    }

    /**
     * Initializes the main UI components of the application.
     * <p>
     * This method sets up the sidebar, footer, and various workbenches including
     * HTTP, GraphQL, WebSocket, and MCP. It also establishes the default workbench
     * and links the sidebar to the history storage controller for later updates.
     * <p>
     * Called during application startup to prepare all interactive UI elements.
     *
     * @author @ASDG14N
     * @since 03-08-2025
     */
    private void initUIComponents() {
        sideBar = new SideBar();
        requestHistoryController.setSideBar(sideBar);
        initWorkbenches();
        workbenchs = List.of(workbenchHttp, workbenchGraphQl, workbenchWebSocket, workbenchMcp);
        footer = new Footer();
        workbench = workbenchs.get(0);
    }

    private void setupMainFrame() {
        frame = new Frame(sideBar, workbench, footer);
        setupResizedFrameListener();
    }

    private void initControllers() {
        new SideBarController(sideBar);
        new FooterController(frame, footer, sideBar);
    }

    private void setupInteractions() {
        setupOnClickTabItem();
        setupShortcuts();
    }

    /**
     * Sets up keyboard shortcuts for the user interface.
     * <p>
     * This method registers key bindings to enable quick access to various actions
     * within the application, enhancing user productivity.
     *
     * @author @ASDG14N
     * @since 28-07-2025
     */
    private void setupShortcuts() {
        Shortcut shortcuts = new Shortcut(frame.getMainContainer());

        shortcuts.registerShortcut("control N", "newTab", () -> {
            WorkbenchHttp workbenchHttp = (WorkbenchHttp) workbenchs.get(0);
            workbenchHttp.getAddTabButton().doClick();
        });
        shortcuts.registerShortcut("control ENTER", "sendRequest", () -> {
            WorkbenchHttp workbenchHttp = (WorkbenchHttp) workbenchs.get(0);
            workbenchHttp.getSelectedTabContent()
                    .getRequestPanel()
                    .getToolbar()
                    .getSendButton()
                    .doClick();
        });
        shortcuts.registerShortcut("control S", "saveRequest", () -> {
            WorkbenchHttp workbenchHttp = (WorkbenchHttp) workbenchs.get(0);
            workbenchHttp.getSelectedTabContent()
                    .getRequestPanel()
                    .getToolbar()
                    .getSaveButton()
                    .doClick();
        });
        shortcuts.registerShortcut("control B", "openSideBar", () -> {
            footer.getToggleSideBarButton().doClick();
        });
        shortcuts.registerShortcut("control W", "closeTab", () -> {
            WorkbenchHttp workbenchHttp = (WorkbenchHttp) workbenchs.get(0);
            ((ClosableTab) workbenchHttp.getTabComponentAt(workbenchHttp.getSelectedIndex()))
                    .getCloseButton()
                    .doClick();
        });
        shortcuts.registerShortcut("alt R", "goToRest", () -> {
            sideBar.getTabbedPane().setSelectedIndex(0);
            replaceBy(workbenchs.get(0));
        });
        shortcuts.registerShortcut("alt Q", "goToGraphQl", () -> {
            sideBar.getTabbedPane().setSelectedIndex(1);
            replaceBy(workbenchs.get(1));
        });
        shortcuts.registerShortcut("alt W", "goToSocket", () -> {
            sideBar.getTabbedPane().setSelectedIndex(2);
            replaceBy(workbenchs.get(2));
        });
        shortcuts.registerShortcut("alt M", "goToMcp", () -> {
            sideBar.getTabbedPane().setSelectedIndex(3);
            replaceBy(workbenchs.get(3));
        });
        shortcuts.registerShortcut("alt S", "goToSettings", () -> {
            sideBar.getTabbedPane().setSelectedIndex(4);
            setupOnClickTabItemSettings();
        });
    }

    /**
     * Configures a listener to monitor frame resize events and trigger
     * responsive layout adjustments based on the current {@link Breakpoint}.
     * <p>
     * Whenever the window is resized, the listener determines the breakpoint
     * corresponding to the new width and invokes
     * {@link #handleBreakpointChange(Breakpoint)}
     * only if the breakpoint has actually changed since the last resize event.
     * </p>
     *
     * <p>
     * <b>Behavior:</b>
     * </p>
     * <ul>
     * <li>Maps the frame width to a {@link Breakpoint} using
     * {@link Breakpoint#fromWidth(int)}.</li>
     * <li>Prevents redundant calls by comparing with the previously stored
     * breakpoint.</li>
     * <li>Calls {@link #handleBreakpointChange(Breakpoint)} only when a new
     * breakpoint is detected.</li>
     * </ul>
     *
     * @author @ASDG14N
     * @since 16-07-2025
     */
    private void setupResizedFrameListener() {
        frame.addComponentListener(new ComponentAdapter() {
            private Breakpoint lastBreakpoint = null;

            @Override
            public void componentResized(ComponentEvent e) {
                Breakpoint current = Breakpoint.fromWidth(frame.getWidth());

                if (current != lastBreakpoint) {
                    lastBreakpoint = current;
                    handleBreakpointChange(current);
                }
            }
        });
    }

    private void handleBreakpointChange(Breakpoint breakpoint) {
        if (!(frame.getWorkbench() instanceof WorkbenchHttp workbenchHttp))
            return;

        var tabContentHttp = workbenchHttp.getSelectedTabContent();
        if (tabContentHttp == null)
            return;
        switch (breakpoint) {
            case MD, LG -> {
                if (tabContentHttp.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
                    tabContentHttp.setVerticalOrientation();
                }
            }
            case XL, XXL -> {
                if (tabContentHttp.getOrientation() == JSplitPane.VERTICAL_SPLIT) {
                    tabContentHttp.setHorizontalOrientation();
                }
            }
            default -> {
            }
        }
    }

    /**
     * Opens a new HTTP tab and loads the specified request into it.
     * <p>
     * This method creates and displays a new tab within the application's
     * interface,
     * initializing it with the provided HTTP request data.
     *
     * @param request the HTTP request to be displayed in the new tab
     * @author @ASDG14N
     * @since 28-07-2025
     */
    public static void openTabContentHttp(Request request) {
        WorkbenchHttp workbenchHttp = (WorkbenchHttp) workbenchs.get(0);

        for (int i = 0; i < workbenchHttp.getTabCount(); i++) {
            var tabComponent = workbenchHttp.getTabComponentAt(i);
            if (tabComponent instanceof ClosableTab closableTab) {
                boolean sameMethod = request.getMethod().toString().equals(closableTab.getMethodName());
                boolean sameTitle = request.getName().equals(closableTab.getTitle());

                if (sameMethod && sameTitle) {
                    workbenchHttp.setSelectedIndex(i);
                    return;
                }
            }
        }
        if ((workbenchHttp.getTabCount() - 1) < TabGroup.TAB_LIMIT) {

            ClosableTab closableTab = new ClosableTab(workbenchHttp, request.getName());
            closableTab.setMethodLabel(request.getMethod().toString());
            TabContentHttp tabContentHttp = workbenchHttp.getNewInstanceTabContent(request, closableTab);

            int insertIndex = workbenchHttp.getTabCount() - 1;

            workbenchHttp.insertTab("", null, tabContentHttp, null, insertIndex);
            workbenchHttp.setTabComponentAt(insertIndex, closableTab);
            workbenchHttp.setSelectedIndex(insertIndex);
        }
    }

    /**
     * Adds a new HTTP tab to the workbench and initializes it with the given
     * collection ID and request name.
     * <p>
     * This method creates a new tab with a closable component, assigns the request
     * data,
     * saves it to the collection storage, and inserts it into the tabbed interface.
     * If the tab limit is reached, no new tab is added.
     *
     * @param idCollection the ID of the collection to associate the new request
     *                     with
     * @param requestName  the name of the new request
     * @author @ASDG14N
     * @since 28-07-2025
     */
    public static void addTabContentHttp(String idCollection, String requestName) {
        WorkbenchHttp workbenchHttp = (WorkbenchHttp) workbenchs.get(0);

        if ((workbenchHttp.getTabCount() - 1) < TabGroup.TAB_LIMIT) {

            ClosableTab closableTab = new ClosableTab(workbenchHttp, requestName);
            TabContentHttp tabContentHttp = workbenchHttp.getNewInstanceTabContent(closableTab);

            final Request requestModel = tabContentHttp.getRequestPanel().getModel();
            requestModel.setIdCollection(idCollection);
            requestModel.setName(requestName);
            collectionController.saveRequest(requestModel);

            int insertIndex = workbenchHttp.getTabCount() - 1;

            workbenchHttp.insertTab("", null, tabContentHttp, null, insertIndex);
            workbenchHttp.setTabComponentAt(insertIndex, closableTab);
            workbenchHttp.setSelectedIndex(insertIndex);
        }
    }

    private void setupOnClickTabItem() {
        final var tabbedPaneLeft = sideBar.getTabbedPane();
        tabbedPaneLeft.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int tabIndex = sideBar.getTabbedPane().indexAtLocation(e.getX(), e.getY());
                switch (tabIndex) {
                    case 0 -> replaceBy(workbenchs.get(0));
                    case 1 -> replaceBy(workbenchs.get(1));
                    case 2 -> replaceBy(workbenchs.get(2));
                    case 3 -> replaceBy(workbenchs.get(3));
                    default -> setupOnClickTabItemSettings();
                }
            }
        });
    }

    private void replaceBy(Workbench newWorkbench) {
        final var mainContainer = frame.getMainContainer();
        Component[] components = mainContainer.getComponents();
        for (Component comp : components) {
            if (comp instanceof Workbench) {
                mainContainer.remove(comp);
                break;
            }
        }

        frame.setWorkbench(newWorkbench);
        mainContainer.add((Component) newWorkbench, "cell 1 0, grow, wrap", 1);
        mainContainer.revalidate();
        mainContainer.repaint();
    }

    public void setupOnClickTabItemSettings() {
        if (sideBar.isOpen())
            return;

        final int openWidth = sideBar.getOpenWidth();
        final int closeWidth = sideBar.getCloseWidth();
        final int duration = 300;

        if (sideBar.getAnimator() != null && sideBar.getAnimator().isRunning())
            return;

        int start = closeWidth;
        int end = openWidth;

        sideBar.setOpen(false);

        Animator animator = new Animator(duration)
                .onTimingEvent(fraction -> {
                    int width = (int) (start + (end - start) * fraction);
                    frame.getLayout().setComponentConstraints(sideBar, "w " + width + "!, h 100%");
                    sideBar.revalidate();
                })
                .onEnd(() -> {
                    sideBar.setOpen(true);
                    Style.setToolTip(footer.getToggleSideBarButton(),
                            LangManager.text("footer.toggleSideBarButton.collapse.placeholder.text"));
                });
        sideBar.setAnimator(animator);
        animator.start();
    }

    /**
     * Sets up an action to be executed before the main application window is
     * closed.
     * <p>
     * This method adds a {@link java.awt.event.WindowListener} to the main frame
     * to intercept the window closing event. If the current workbench is an
     * instance of
     * {@link WorkbenchHttp}, it checks whether there are any unsaved tabs (i.e.,
     * tabs without a close icon).
     * If such tabs exist, a confirmation dialog is shown to the user before
     * exiting.
     * Otherwise, the application is terminated directly.
     * <p>
     * For other types of workbenches ({@link WorkbenchGraphQl},
     * {@link WorkbenchWebSocket}, {@link WorkbenchMcp}),
     * diagnostic messages are printed to the console.
     *
     * @author ASDFG14N
     * @since 06-08-2025
     */

    private void setupActionBeforeClosing() {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                final var workbench = frame.getWorkbench();
                if (workbench instanceof WorkbenchHttp workbenchHttp) {
                    final var closableTabs = workbenchHttp.getAllClosableTabs();
                    if (hasNonCloseIcon(closableTabs)) {
                        final Option result = ConfirmDialog
                                .show(LangManager.text("setupActionBeforeClosing.confirmDialog.messageDialog.text"));
                        if (result == Option.YES) {
                            exitApplication();
                        }
                    } else {
                        exitApplication();
                    }

                }
                if (workbench instanceof WorkbenchGraphQl) {
                    System.out.println("setupActionBeforeClosing");
                    exitApplication();
                }
                if (workbench instanceof WorkbenchWebSocket) {
                    System.out.println("setupActionBeforeClosing");
                    exitApplication();
                }
                if (workbench instanceof WorkbenchMcp) {
                    System.out.println("setupActionBeforeClosing");
                    exitApplication();
                }
            }
        });
    }

    public static void appendRequestEntry(Request request, Response response) {
        frame.getConsolePanel().appendRequestEntry(request, response);
    }

    /**
     * Checks if there is at least one tab in the list that does not have a close
     * icon.
     *
     * @param tabs A list of {@link ClosableTab} objects representing the tabs.
     * @return {@code true} if any tab does not have a close icon; {@code false}
     * otherwise.
     * @author ASDFG14N
     * @since 06-08-2025
     */
    public boolean hasNonCloseIcon(List<ClosableTab> tabs) {
        return tabs.stream().anyMatch(tab -> !tab.isCloseIcon());
    }

    public static void setCode(String code) {
        sideBar.getHttpPanel()
                .getCodePanel()
                .getEditor()
                .setText(code);
    }

    public static void renderNodesTreeCollections() {
        sideBar.getHttpPanel()
                .getTreeCollections()
                .renderNodes();
    }

    public static void renderNodesTreePanelEnviroment() {
        sideBar.getHttpPanel()
                .getTreeEnviroment()
                .renderNodes();
    }

    public static void reloadComboBoxEnvironmentItems() {
        workbenchHttp.reloadEnvironments();
    }

    public static boolean isGlassPaneActive() {
        return frame.isGlassPaneActive();
    }

    public static void hideGlassPane() {
        frame.hideGlassPane();
    }

    public static void showGlassPane() {
        frame.showGlassPane();
    }

    /**
     * Terminates the application immediately.
     * This method calls {@link System#exit(int)} with a status code of 0,
     * indicating normal termination.
     *
     * @author ASDFG14N
     * @since 06-08-2025.
     */
    private void exitApplication() {
        frame.dispose();
        System.exit(0);
    }

    /**
     * Launches the main application window.
     * <p>
     * This method makes the primary frame of the application visible,
     * effectively starting the graphical user interface.
     *
     * @author @ASDG14N
     * @since 28-07-2025
     */
    public void run() {
        frame.setVisible(true);
    }

    /**
     * Utility class for registering global keyboard shortcuts within a Swing
     * application.
     * <p>
     * The {@code Shortcut} class allows binding key combinations to specific
     * actions
     * within the root pane of a given component. It provides overloaded methods for
     * registering shortcuts using either {@link KeyStroke} objects or string key
     * combinations.
     * <p>
     * Shortcuts are registered to the root pane's {@link InputMap} and
     * {@link ActionMap}
     * under the {@code WHEN_IN_FOCUSED_WINDOW} condition, enabling them to work
     * regardless
     * of the focused component.
     * <p>
     * Throws an {@link IllegalStateException} if no root pane is found for the
     * provided component.
     *
     * @author @ASDG14N
     * @since 03-08-2025
     */
    private record Shortcut(JComponent root) {

        /**
         * Constructs a {@code Shortcut} instance tied to the root pane of the given
         * component.
         *
         * @param root the component whose root pane is used for shortcut
         *             registration
         * @throws IllegalStateException if the root pane cannot be determined
         */
        private Shortcut(JComponent root) {
            this.root = SwingUtilities.getRootPane(root);
            if (this.root == null) {
                throw new IllegalStateException("No RootPane found for the given component.");
            }
        }

        /**
         * Registers a keyboard shortcut using a {@link KeyStroke}.
         *
         * @param keyStroke  the key stroke to trigger the action
         * @param actionName a unique name for the action
         * @param action     the action to execute when the shortcut is triggered
         */
        private void registerShortcut(KeyStroke keyStroke, String actionName, Runnable action) {
            InputMap inputMap = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            ActionMap actionMap = root.getActionMap();

            inputMap.put(keyStroke, actionName);
            actionMap.put(actionName, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    action.run();
                }
            });
        }

        /**
         * Registers a keyboard shortcut using a key combination string.
         *
         * @param keyCombo   the key combination string (e.g. "ctrl S")
         * @param actionName a unique name for the action
         * @param action     the action to execute when the shortcut is triggered
         */
        private void registerShortcut(String keyCombo, String actionName, Runnable action) {
            KeyStroke keyStroke = KeyStroke.getKeyStroke(keyCombo);
            registerShortcut(keyStroke, actionName, action);
        }
    }

}
