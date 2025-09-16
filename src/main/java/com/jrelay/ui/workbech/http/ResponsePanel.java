package com.jrelay.ui.workbech.http;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.jrelay.core.models.response.ContentDisplayType;
import com.jrelay.core.utils.i18n.LangManager;
import com.jrelay.ui.components.dialogs.FileNativeDialog;
import com.jrelay.ui.components.dialogs.MessageDialog;
import com.jrelay.ui.components.dialogs.FileNativeDialog.Mode;
import com.jrelay.ui.components.dialogs.MessageDialog.Location;
import com.jrelay.ui.components.dialogs.MessageDialog.Type;
import com.jrelay.ui.components.shared.SyntaxEditor;
import com.jrelay.ui.shared.styles.Colors;
import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.shared.utils.StatusCodeUtil;
import com.jrelay.ui.shared.utils.UiUtils;
import com.jrelay.ui.shared.utils.template.Struct;
import com.jrelay.ui.shared.utils.template.Translatable;

import lombok.Getter;
import net.miginfocom.swing.MigLayout;

public class ResponsePanel extends JPanel implements Struct, Translatable {

    @Getter
    private StatusPanel statusPanel;
    @Getter
    private final JTabbedPane tabbedPane = new JTabbedPane();
    @Getter
    private SyntaxEditorPanel syntaxEditorPanel;
    private final BinaryPanel binaryPanel = new BinaryPanel();
    @Getter
    private HeadersTablePanel headersTablePanel;

    public ResponsePanel() {
        this.build();
        LangManager.register(this);
    }

    @Override
    public void initComponents() {
        statusPanel = new StatusPanel();
        syntaxEditorPanel = new SyntaxEditorPanel();
        headersTablePanel = new HeadersTablePanel();
    }

    @Override
    public void configureStyle() {
        Style.setLayout(this, new MigLayout("fill, insets 15 15 10 15", "[grow]", "[]10[grow]"));
        Style.setTabArc(tabbedPane);
    }

    @Override
    public void compose() {
        tabbedPane.addTab(LangManager.text("responsePanel.tabbedPane.tab1.title.text"), syntaxEditorPanel);
        tabbedPane.addTab(LangManager.text("responsePanel.tabbedPane.tab2.title.text"), headersTablePanel);

        this.add(statusPanel, "h 36!, cell 0 0, growx");
        this.add(tabbedPane, "cell 0 1, grow");
    }

    @Override
    public void updateText() {
        tabbedPane.setTitleAt(0, LangManager.text("responsePanel.tabbedPane.tab1.title.text"));
        tabbedPane.setTitleAt(1, LangManager.text("responsePanel.tabbedPane.tab2.title.text"));
    }

    public void setTextContentType(ContentDisplayType contentDisplayType, String text) {
        if (tabbedPane.getComponentAt(0) != syntaxEditorPanel) {
            tabbedPane.setComponentAt(0, syntaxEditorPanel);
        }
        syntaxEditorPanel.getSyntaxEditor().setSyntaxEditingStyle(contentDisplayType);
        syntaxEditorPanel.getSyntaxEditor().setText(text);
    }

    public void setBinaryContentType(ContentDisplayType contentDisplayType, byte[] data) {
        if (tabbedPane.getComponentAt(0) != binaryPanel) {
            tabbedPane.setComponentAt(0, binaryPanel);
        }
        binaryPanel.showContent(contentDisplayType, data);
    }

    public class StatusPanel extends JPanel implements Struct, Translatable {
        private final JPanel cicle = new JPanel();
        private JLabel statusCodeLabel;
        private final JLabel status = new JLabel("");

        private JLabel timeLabel;
        private JLabel time = new JLabel("");

        private JLabel sizeLabel;
        private final JLabel size = new JLabel("");

        private Color color;

        private StatusPanel() {
            this.build();
            LangManager.register(this);
        }

        @Override
        public void initComponents() {
            statusCodeLabel = new JLabel(LangManager.text("responsePanel.statusPanel.statusCodeLabel.text"));
            timeLabel = new JLabel(LangManager.text("responsePanel.statusPanel.timeLabel.text"));
            sizeLabel = new JLabel(LangManager.text("responsePanel.statusPanel.sizeLabel.text"));
        }

        @Override
        public void configureStyle() {
            Style.setLayout(this, new MigLayout("insets 10 0 0 15, fillx", "push[][][][]20[][]20[][]"));
            Style.setTransparent(this);
            cicle.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
            Style.setCursor(status, Cursor.HAND_CURSOR);
        }

        @Override
        public void compose() {
            this.add(new JLabel(UiUtils.GLOBAL_ICON));
            this.add(statusCodeLabel);
            this.add(cicle);
            this.add(status);

            this.add(timeLabel);
            this.add(time, "aligny center");

            this.add(sizeLabel);
            this.add(size, "aligny center");
        }

        @Override
        public void updateText() {
            Style.setLabelText(statusCodeLabel, LangManager.text("responsePanel.statusPanel.statusCodeLabel.text"));
            Style.setLabelText(timeLabel, LangManager.text("responsePanel.statusPanel.timeLabel.text"));
            Style.setLabelText(sizeLabel, LangManager.text("responsePanel.statusPanel.sizeLabel.text"));
        }

        private Color getStatusColor(int code) {
            if (code >= 200 && code < 300)
                return Colors.GET_COLOR;
            if (code >= 300 && code < 400)
                return Colors.POST_COLOR;
            if (code >= 400 && code < 500)
                return Colors.PUT_COLOR;
            if (code >= 500)
                return Colors.PATCH_COLOR;
            return Colors.DELETE_COLOR;
        }

        public void setStatus(int status) {
            this.status.setText("HTTP " + status + " " + StatusCodeUtil.statusSuffix(status));

            this.color = getStatusColor(status);
            this.cicle.setBackground(color);
            this.status.setForeground(color);

            this.status.setToolTipText(StatusCodeUtil.statusInf(status));
        }

        public void setTime(String time) {
            this.time.setText(time);
            this.time.setForeground(color);
        }

        public void setSize(String size) {
            this.size.setText(size);
            this.size.setForeground(color);
        }

    }

    public class SyntaxEditorPanel extends JPanel implements Struct, Translatable {
        @Getter
        private SyntaxEditor syntaxEditor;
        private final JPanel container = new JPanel(new MigLayout("fill, aligny 50%, insets 0 0 0 17", "[]push[][][]"));
        private final JLabel titleLabel = new JLabel(
                LangManager.text("responsePanel.syntaxEditorPanel.titleLabel.text"));
        @Getter
        private final JToggleButton lineWrapButton = new JToggleButton(UiUtils.LINE_WRAP_ICON);
        @Getter
        private final JButton downloadButton = new JButton(UiUtils.DOWNLOAD_ICON);
        @Getter
        private final JButton copyButton = new JButton(UiUtils.COPY_ICON);

        private SyntaxEditorPanel() {
            this.build();
            LangManager.register(this);
        }

        @Override
        public void initComponents() {
            syntaxEditor = new SyntaxEditor(SyntaxConstants.SYNTAX_STYLE_JSON_WITH_COMMENTS);
        }

        @Override
        public void configureStyle() {
            Style.setLayout(this, new MigLayout("fill, insets 5 10 5 10", "[grow]", "[]0[grow]"));
            Style.setCursor(lineWrapButton, Cursor.HAND_CURSOR);
            Style.setBackgroundColor(lineWrapButton, Colors.SECONDARY_COLOR);
            Style.setToolTip(lineWrapButton, LangManager.text("app.panel.lineWrapButton.deactivate.toolTip.text"));
            Style.setUndecoratedButton(lineWrapButton);
            Style.setCursor(downloadButton, Cursor.HAND_CURSOR);
            Style.setBackgroundColor(downloadButton, Colors.SECONDARY_COLOR);
            Style.setToolTip(downloadButton, LangManager.text("app.panel.downloadButton.toolTip.text"));
            Style.setUndecoratedButton(downloadButton);
            Style.setCursor(copyButton, Cursor.HAND_CURSOR);
            Style.setBackgroundColor(copyButton, Colors.SECONDARY_COLOR);
            Style.setToolTip(copyButton, LangManager.text("app.panel.copyButton.toolTip.text"));
            Style.setUndecoratedButton(copyButton);
        }

        @Override
        public void compose() {
            container.add(titleLabel);
            container.add(lineWrapButton, "w 25!, h 25!");
            container.add(downloadButton, "w 25!, h 25!");
            container.add(copyButton, "w 25!, h 25!");
            this.add(container, "h 30!, cell 0 0, growx");
            this.add(syntaxEditor, "cell 0 1, grow");
        }

        @Override
        public void updateText() {
            Style.setLabelText(titleLabel, LangManager.text("responsePanel.syntaxEditorPanel.titleLabel.text"));
            Style.setToolTip(lineWrapButton, LangManager.text("app.panel.lineWrapButton.deactivate.toolTip.text"));
            Style.setToolTip(downloadButton, LangManager.text("app.panel.downloadButton.toolTip.text"));
            Style.setToolTip(copyButton, LangManager.text("app.panel.copyButton.toolTip.text"));
        }

    }

    public class BinaryPanel extends JPanel implements Struct, Translatable {
        private File tempFile;
        private final CardLayout cardLayout = new CardLayout();

        private final JScrollPane imagePanel = new JScrollPane();
        private final JLabel imageLabel = new JLabel("", SwingConstants.CENTER);

        private final JPanel pdfPanel = new JPanel();
        private final JLabel pdfLabel = new JLabel(LangManager.text("responsePanel.pdfLabel.text"));
        private JButton downloadPdfButton = new JButton(LangManager.text("app.panel.downloadButton.toolTip.text"));

        private final JPanel audioPanel = new JPanel();
        private final JLabel audioLabel = new JLabel(LangManager.text("responsePanel.audioLabel.text"));
        private JButton downloadAudioButton = new JButton(LangManager.text("app.panel.downloadButton.toolTip.text"));

        private final JPanel videoPanel = new JPanel();
        private final JLabel videoLabel = new JLabel(LangManager.text("responsePanel.videoLabel.text"));
        private JButton downloadVideoButton = new JButton(LangManager.text("app.panel.downloadButton.toolTip.text"));

        private final JLabel unsupportedLabel = new JLabel(LangManager.text("responsePanel.unsupportedLabel.text"));

        public BinaryPanel() {
            this.build();
            LangManager.register(this);
        }

        @Override
        public void initComponents() {
        }

        @Override
        public void configureStyle() {
            Style.setLayout(this, cardLayout);

            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imagePanel.setViewportView(imageLabel);

            Style.setLayout(pdfPanel, new MigLayout("fillx, insets 10, aligny 50%", "[center]", "[]20[]"));
            Style.setCursor(downloadPdfButton, Cursor.HAND_CURSOR);
            Style.setUndecoratedButton(downloadPdfButton);
            Style.setBackgroundColor(downloadPdfButton, Colors.SECONDARY_COLOR);
            pdfPanel.add(pdfLabel, "center, wrap");
            pdfPanel.add(downloadPdfButton, "center, h 35!");

            Style.setLayout(videoPanel, new MigLayout("fillx, insets 10, aligny 50%", "[center]", "[]20[]"));
            Style.setCursor(downloadVideoButton, Cursor.HAND_CURSOR);
            Style.setUndecoratedButton(downloadVideoButton);
            Style.setBackgroundColor(downloadVideoButton, Colors.SECONDARY_COLOR);
            videoPanel.add(videoLabel, "center, wrap");
            videoPanel.add(downloadVideoButton, "center, h 35!");

            Style.setLayout(audioPanel, new MigLayout("fillx, insets 10, aligny 50%", "[center]", "[]20[]"));
            Style.setCursor(downloadAudioButton, Cursor.HAND_CURSOR);
            Style.setUndecoratedButton(downloadAudioButton);
            Style.setBackgroundColor(downloadAudioButton, Colors.SECONDARY_COLOR);
            audioPanel.add(audioLabel, "center, wrap");
            audioPanel.add(downloadAudioButton, "center, h 35!");
        }

        @Override
        public void attachLogic() {
            downloadPdfButton.addActionListener(e -> downloadCurrentFile());
        }

        @Override
        public void compose() {
            this.add(imagePanel, "IMAGE");
            this.add(pdfPanel, "PDF");
            this.add(videoPanel, "VIDEO");
            this.add(audioPanel, "AUDIO");
            this.add(unsupportedLabel, "UNSUPPORTED");
        }

        @Override
        public void updateText() {
            Style.setButtonText(
                    downloadPdfButton,
                    LangManager.text("app.panel.downloadButton.toolTip.text"));
            Style.setButtonText(
                    downloadAudioButton,
                    LangManager.text("app.panel.downloadButton.toolTip.text"));
            Style.setButtonText(
                    downloadVideoButton,
                    LangManager.text("app.panel.downloadButton.toolTip.text"));
            Style.setLabelText(pdfLabel, LangManager.text("responsePanel.pdfLabel.text"));
            Style.setLabelText(audioLabel, LangManager.text("responsePanel.audioLabel.text"));
            Style.setLabelText(videoLabel, LangManager.text("responsePanel.videoLabel.text"));
            Style.setLabelText(unsupportedLabel, LangManager.text("responsePanel.unsupportedLabel.text"));
        }

        public void showContent(ContentDisplayType type, byte[] data) {
            switch (type) {
                case IMAGE -> {
                    boolean isSvg = new String(data, 0, Math.min(data.length, 200), StandardCharsets.UTF_8)
                            .contains("<svg");

                    if (isSvg) {
                        storeTempFile("image", data);
                        if (tempFile != null) {
                            FlatSVGIcon svgIcon = new FlatSVGIcon(tempFile).derive(4.2f);
                            imageLabel.setIcon(svgIcon);
                        } else {
                            MessageDialog.showMessage(Location.TOP_CENTER, Type.ERROR, "Error rendering SVG");
                        }
                    } else {
                        imageLabel.setIcon(new ImageIcon(data));
                        storeTempFile("image", data);
                    }
                    cardLayout.show(this, "IMAGE");
                }
                case PDF -> {
                    cardLayout.show(this, "PDF");
                    storeTempFile("document.pdf", data);
                }
                case AUDIO -> {
                    cardLayout.show(this, "AUDIO");
                    storeTempFile("audio", data);
                }
                case VIDEO -> {
                    cardLayout.show(this, "VIDEO");
                    storeTempFile("video", data);
                }
                default -> {
                    cardLayout.show(this, "UNSUPPORTED");
                    storeTempFile("file.bin", data);
                }
            }
        }

        private String cleanFileName(File tempFile) {
            String name = tempFile.getName();

            int dotIndex = name.lastIndexOf('.');
            if (dotIndex > 0 && dotIndex < name.length() - 1) {
                String base = name.substring(0, dotIndex);
                String ext = name.substring(dotIndex);

                ext = ext.replaceAll("\\d+$", "");

                return base + ext;
            }

            return name.replaceAll("\\d+$", "");
        }

        private void storeTempFile(String fileName, byte[] data) {
            try {
                tempFile = Files.createTempFile(fileName, "").toFile();
                Files.write(tempFile.toPath(), data);
                tempFile.deleteOnExit();
            } catch (IOException e) {
                MessageDialog.showMessage(Location.TOP_CENTER, Type.ERROR, "Error saving file");
                System.err.println(e.getMessage());
            }
        }

        private void downloadCurrentFile() {
            if (tempFile == null || !tempFile.exists()) {
                MessageDialog.showMessage(Location.TOP_CENTER, Type.ERROR, "No file to download");
                return;
            }

            final var result = FileNativeDialog.show(cleanFileName(tempFile), Mode.SAVE);

            if (result != null) {
                try {
                    File target = new File(result.getFirst(), result.getSecond());
                    Files.copy(tempFile.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    MessageDialog.showMessage(Location.TOP_CENTER, Type.SUCCESS, "File saved successfully");
                } catch (IOException e) {
                    MessageDialog.showMessage(Location.TOP_CENTER, Type.ERROR, "Error saving file");
                    System.err.println(e.getMessage());
                }
            }
        }

    }

    public class HeadersTablePanel extends JPanel implements Struct {
        private JScrollPane scroll;
        private JTable table;
        private DefaultTableModel model = new DefaultTableModel(new String[] { "Header", "Value" }, 0);

        private HeadersTablePanel() {
            this.build();
        }

        @Override
        public void initComponents() {
            table = new JTable(model);
            scroll = new JScrollPane(table);
        }

        @Override
        public void configureStyle() {
            Style.setLayout(this, new MigLayout("fill, insets 10", "[grow]"));

            int totalWidth = table.getPreferredSize().width;

            table.getColumnModel()
                    .getColumn(0)
                    .setPreferredWidth((int) (totalWidth * 0.03));
            table.getColumnModel()
                    .getColumn(1)
                    .setPreferredWidth((int) (totalWidth * 0.97));
            table.setFillsViewportHeight(true);
        }

        @Override
        public void compose() {
            this.add(scroll, "grow");
        }

        public DefaultTableModel getModel() {
            return model;
        }

        public void fillRow(String key, String value) {
            model.addRow(new String[] { key, value });
        }

    }
}
