package com.jrelay.ui.workbech.http;

import java.util.Map;

import javax.swing.JToggleButton;

import java.awt.Color;
import java.io.File;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.jrelay.core.models.response.Response;
import com.jrelay.core.utils.i18n.LangManager;
import com.jrelay.ui.components.dialogs.FileNativeDialog;
import com.jrelay.ui.components.dialogs.MessageDialog;
import com.jrelay.ui.components.dialogs.FileNativeDialog.Mode;
import com.jrelay.ui.components.dialogs.MessageDialog.Type;
import com.jrelay.ui.controllers.AppController;
import com.jrelay.ui.shared.styles.Colors;
import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.workbech.http.ResponsePanel.SyntaxEditorPanel;

/**
 * Controller responsible for managing the response panel behavior and UI
 * updates.
 * <p>
 * This class handles the display logic of HTTP responses, including setting the
 * status, time, size, headers, and response body in the appropriate view
 * components.
 * It also updates the tab title with the number of response headers.
 *
 * @author @ASDG14N
 * @since 28-07-2025
 */
public class ResponsePanelController {

    private final ResponsePanel responsePanel;
    private final SyntaxEditorPanel syntaxEditorPanel;

    /**
     * Constructs a controller for managing interactions within a
     * {@link ResponsePanel}.
     * <p>
     * Initializes internal references to the response panel and its associated
     * {@link SyntaxEditorPanel}, and sets up event listeners to handle user
     * interactions.
     *
     * @param responsePanel the {@link ResponsePanel} instance to be managed by this
     *                      controller
     * @author ASDFG14N
     * @since 07-08-2025
     */

    public ResponsePanelController(ResponsePanel responsePanel) {
        this.responsePanel = responsePanel;
        this.syntaxEditorPanel = responsePanel.getSyntaxEditorPanel();
        setupListeners();
    }

    /**
     * Registers all necessary event listeners for the response panel.
     * <p>
     * This includes listeners for toggling line wrap, downloading the response as a
     * file,
     * and copying the response content to the clipboard.
     *
     * @author ASDFG14N
     * @since 07-08-2025
     */

    private void setupListeners() {
        setupLineWrapToggle();
        setupDownloadFileListener();
        setupCopyListener();
    }

    /**
     * Configures the line wrap toggle functionality for the syntax editor.
     * <p>
     * Initializes the toggle button state based on the current line wrap setting of
     * the editor,
     * applies the corresponding visual style, and sets up a listener to update both
     * the editor and
     * button style when the toggle is changed by the user.
     *
     * @author ASDFG14N
     * @since 07-08-2025
     */

    private void setupLineWrapToggle() {
        final var lineWrapButton = syntaxEditorPanel.getLineWrapButton();
        final var editor = syntaxEditorPanel.getSyntaxEditor();

        boolean isWrapped = editor.getLineWrap();
        lineWrapButton.setSelected(isWrapped);
        applyLineWrapStyle(lineWrapButton, isWrapped);

        lineWrapButton.addActionListener(e -> {
            boolean newState = lineWrapButton.isSelected();
            editor.setLineWrap(newState);
            applyLineWrapStyle(lineWrapButton, newState);
        });
    }

    /**
     * Applies visual styling to the line wrap toggle button based on its active
     * state.
     * <p>
     * Changes the icon color using a {@link FlatSVGIcon.ColorFilter} to reflect
     * whether
     * line wrap is currently enabled or not. Also updates the tooltip text
     * accordingly.
     *
     * @param button   the {@link JToggleButton} representing the line wrap toggle
     * @param isActive {@code true} if line wrap is enabled; {@code false} otherwise
     * @author ASDFG14N
     * @since 07-08-2025
     */

    private void applyLineWrapStyle(JToggleButton button, boolean isActive) {
        FlatSVGIcon icon = (FlatSVGIcon) button.getIcon();
        icon.setColorFilter(new FlatSVGIcon.ColorFilter(
                color -> isActive ? Color.decode(Colors.ACCENT_COLOR.getColor()) : Colors.ICON_COLOR));
        button.setIcon(icon);

        String tooltip = isActive ? LangManager.text("app.panel.lineWrapButton.activate.toolTip.text")
                : LangManager.text("app.panel.lineWrapButton.deactivate.toolTip.text");
        Style.setToolTip(button, tooltip);
    }

    /**
     * Sets up the listener for the download button within the syntax editor panel.
     * <p>
     * When triggered, it retrieves the current response body, opens a native save
     * dialog,
     * and writes the content to the selected file in JSON format. Displays a
     * success or error
     * message depending on whether the file was successfully saved or the operation
     * was cancelled.
     *
     * @author ASDFG14N
     * @since 07-08-2025
     */

    private void setupDownloadFileListener() {
        final var button = syntaxEditorPanel.getDownloadButton();

        button.addActionListener(e -> {
            String body = syntaxEditorPanel.getSyntaxEditor().getText();
            final var result = FileNativeDialog.show("response", Mode.SAVE);
            if (result != null) {
                String directory = result.getFirst();
                String filename = result.getSecond();
                File selectedFile = new File(directory, filename);
                if (!selectedFile.getName().toLowerCase().endsWith(".json")) {
                    selectedFile = new File(selectedFile.getAbsolutePath() + ".json");
                    AppController.osManager.writeJsonToFile(body, selectedFile.getAbsolutePath());
                    MessageDialog.showMessage(
                            Type.SUCCESS,
                            LangManager.text("app.messageDialog.saved.text"));
                }
            }
        });
    }

    private void setupCopyListener() {
        final var button = syntaxEditorPanel.getCopyButton();
        button.addActionListener(e -> {
            final var body = syntaxEditorPanel.getSyntaxEditor().getText();
            AppController.osManager.putInClipboard(body);
            MessageDialog.showMessage(
                    Type.SUCCESS,
                    LangManager.text("app.messageDialog.copied.text"));
        });
    }

    /**
     * Displays the given HTTP response in the associated {@link ResponsePanel}.
     * <p>
     * Clears existing header rows, sets the status code, response time, and size,
     * and populates the headers table with sorted header entries. Also updates the
     * tab
     * title to reflect the number of headers and sets the response body in the
     * syntax editor.
     *
     * @param response the {@link Response} object containing status, headers, body,
     *                 duration, and size
     * @author ASDFG14N
     * @since 07-08-2025
     */
    public void show(Response response) {
        responsePanel.getHeadersTablePanel().getModel().setRowCount(0);

        responsePanel.getStatusPanel().setStatus(response.status());
        responsePanel.getStatusPanel().setTime(response.duration());
        responsePanel.getStatusPanel().setSize(response.size());

        response.headers().entrySet().stream()
                .sorted(Map.Entry.comparingByKey(String.CASE_INSENSITIVE_ORDER))
                .forEach(entry -> {
                    String key = entry.getKey();
                    for (var value : entry.getValue()) {
                        responsePanel.getHeadersTablePanel().fillRow(key, value);
                    }
                });

        updateTabQueryTitle(response.headers().size());

        switch (response.displayType()) {
            case JSON, XML, YAML, HTML, TEXT, FORM_URL_ENCODED, CSV ->
                    responsePanel.setTextContentType(response.displayType(), response.body());
            case IMAGE, PDF, AUDIO, VIDEO ->
                    responsePanel.setBinaryContentType(response.displayType(), response.bodyBytes());
            default -> {
                //
            }

        }

    }

    private void updateTabQueryTitle(int n) {
        responsePanel.getTabbedPane().setTitleAt(1,
                LangManager.text("responsePanel.tabbedPane.tab2.title.text") + "   (" + n + ")");
        responsePanel.getTabbedPane().revalidate();
    }

}
