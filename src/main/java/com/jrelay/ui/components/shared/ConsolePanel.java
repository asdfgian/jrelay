package com.jrelay.ui.components.shared;

import java.awt.Color;
import java.awt.Cursor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.jrelay.core.models.request.Request;
import com.jrelay.core.models.response.Response;
import com.jrelay.ui.shared.styles.Colors;
import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.shared.utils.UiUtils;
import com.jrelay.ui.shared.utils.template.Struct;

import lombok.Getter;
import net.miginfocom.swing.MigLayout;

public class ConsolePanel extends JPanel implements Struct {

    private final float FONT_SIZE = 12.4f;
    public final static int HEIGHT = 400;
    private final JPanel header = new JPanel();
    private final JComboBox<String> comboBox = new JComboBox<>(
            new String[] { "All logs", "Log", "Info", "Warning", "Error" });
    private final JButton clearButton = new JButton("Clear");
    private final JButton copyButton = new JButton(UiUtils.COPY_ICON);
    @Getter
    private final JButton closeButon = new JButton();
    private final JScrollPane scrollConsole = new JScrollPane();
    private final JPanel containerConsole = new JPanel();

    public ConsolePanel() {
        this.build();
    }

    @Override
    public void initComponents() {
    }

    @Override
    public void configureStyle() {
        Style.setLayout(this, new MigLayout("fill, insets 1 0 1 0", "[grow]", "[]0[grow]"));
        Style.setLayout(header, new MigLayout("fill, aligny 50%", "push[]8[]8[]10[]"));
        Style.setLayout(containerConsole, new MigLayout("fillx, wrap, insets 0, gap 0 0"));
        Style.setFontSize(comboBox, FONT_SIZE);
        Style.setCursor(comboBox, Cursor.HAND_CURSOR);
        Style.setUndecoratedButton(clearButton);
        Style.setBackgroundColor(clearButton, Colors.SECONDARY_COLOR);
        Style.setFontSize(clearButton, FONT_SIZE);
        Style.setTextColor(clearButton, Colors.ICON_COLOR);
        Style.setCursor(clearButton, Cursor.HAND_CURSOR);
        Style.setUndecoratedButton(copyButton);
        Style.setBackgroundColor(copyButton, Colors.SECONDARY_COLOR);
        Style.setCursor(copyButton, Cursor.HAND_CURSOR);
        Style.setUndecoratedButton(closeButon);
        Style.setBackgroundColor(closeButon, Colors.SECONDARY_COLOR);
        Style.setIcon(closeButon, new FlatSVGIcon("icon/close.svg", 0.75f)
                .setColorFilter(new FlatSVGIcon.ColorFilter(color -> Colors.ICON_COLOR)));
        Style.setCursor(closeButon, Cursor.HAND_CURSOR);
        Style.setBackgroundColor(this, Colors.SECONDARY_COLOR);
    }

    @Override
    public void attachLogic() {
        clearButton.addActionListener(e -> {
            containerConsole.removeAll();
            containerConsole.revalidate();
            containerConsole.repaint();
        });
    }

    @Override
    public void compose() {
        header.add(comboBox, "h 28!");
        header.add(clearButton, "h 28!");
        header.add(copyButton, "w 28!, h 28!");
        header.add(closeButon, "w 28!, h 28!");
        scrollConsole.setViewportView(containerConsole);
        this.add(header, "h 40!, cell 0 0, growx");
        this.add(scrollConsole, "cell 0 1, grow");
    }

    public void appendRequestEntry(Request request, Response response) {
        var entry = new RequestEntry(request, response);
        containerConsole.add(entry, "growx, wrap");
        containerConsole.add(new JSeparator(), "growx, wrap");
        containerConsole.revalidate();
    }

    public class RequestEntry extends JPanel implements Struct {
        private final JPanel header = new JPanel();
        private final JToggleButton collapseButton = new JToggleButton(UiUtils.ARROW_RIGHT);
        private final JLabel method = new JLabel();
        private final JLabel url = new JLabel();

        private Color color;
        private final JLabel status = new JLabel();
        private final JLabel time = new JLabel();

        private final JPanel body = new JPanel();

        private Request request;
        private Response response;

        public RequestEntry(Request request, Response response) {
            this.request = request;
            this.response = response;
            this.build();
        }

        @Override
        public void initComponents() {
            Style.setLabelText(method, request.getMethod().name());
            Style.setLabelText(url, request.getUrl());
            this.color = getStatusColor(response.status());
            status.setForeground(color);
            Style.setLabelText(status, response.status().toString());
            time.setForeground(color);
            Style.setLabelText(time, response.duration());
        }

        @Override
        public void configureStyle() {
            Style.setLayout(this, new MigLayout("fillx, insets 0, wrap", "[grow]", "[]0[grow]"));
            Style.setLayout(header, new MigLayout("fillx, insets 5 7 5 15", "[]0[][grow][][]", "[]"));
            Style.setUndecoratedButton(collapseButton);
            Style.setCursor(collapseButton, Cursor.HAND_CURSOR);
            Style.setTransparent(collapseButton);
            Style.setBackgroundColor(collapseButton, Colors.SECONDARY_COLOR);
            Style.setFontSize(method, FONT_SIZE);
            Style.setFontSize(url, FONT_SIZE);
            Style.setFontSize(status, FONT_SIZE);
            Style.setFontSize(time, FONT_SIZE);
            Style.setFontSize(body, FONT_SIZE);
            Style.setLayout(body, new MigLayout("fillx, wrap, insets 0, gap 0 0"));
        }

        @Override
        public void attachLogic() {
            collapseButton.addActionListener(e -> {
                boolean expanded = collapseButton.isSelected();
                if (expanded) {
                    this.add(body, "grow, wrap");
                } else {
                    this.remove(body);
                }
                collapseButton.setIcon(expanded ? UiUtils.ARROW_DOWN : UiUtils.ARROW_RIGHT);
                this.revalidate();
                this.repaint();
            });
        }

        @Override
        public void compose() {
            header.add(collapseButton);
            header.add(method);
            header.add(url);
            header.add(status);
            header.add(time);

            body.add(new PanelCollapse("Network"), "growx, wrap");
            body.add(new PanelCollapse("Request Headers", request.headersToString()), "grow, wrap");
            if (request.getBody() != null) {
                body.add(new PanelCollapse("Request Body", request.getBody().content()), "growx, wrap");
            }
            body.add(new PanelCollapse("Response Headers", response.headersToString()), "growx, wrap");
            if (response.isText()) {
                body.add(new PanelCollapse("Response Body", response.body()), "growx, wrap");
            } else if (response.isBinary()) {
                body.add(new PanelCollapse("Response Body", response.bodyBytes().toString()), "growx, wrap");
            }
            this.add(header, "growx, wrap");
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

    }

    public class PanelCollapse extends JPanel implements Struct {
        private final JPanel header = new JPanel();
        private final JToggleButton collapseButton = new JToggleButton(UiUtils.ARROW_RIGHT);
        private String titleStr = "";
        private final JLabel title = new JLabel();
        private final JPanel containerTextPane = new JPanel();
        private final JTextPane textPane = new JTextPane();

        public PanelCollapse(String title) {
            this.titleStr = title;
            this.build();
        }

        public PanelCollapse(String title, String text) {
            this.titleStr = title;
            setTextToTextPane(text);
            this.build();
        }

        @Override
        public void initComponents() {
            Style.setLabelText(title, titleStr);
        }

        @Override
        public void configureStyle() {
            Style.setLayout(this, new MigLayout("fillx, insets 0 18 5 18, wrap", "[grow]", "[]0[grow]"));
            Style.setLayout(containerTextPane, new MigLayout("fill, insets 0 25 0 0"));
            Style.setLayout(header, new MigLayout("fillx, insets 0", "[]0[]push", "[]"));
            Style.setUndecoratedButton(collapseButton);
            Style.setCursor(collapseButton, Cursor.HAND_CURSOR);
            Style.setTransparent(collapseButton);
            Style.setBackgroundColor(collapseButton, Colors.SECONDARY_COLOR);
            Style.setFontSize(title, FONT_SIZE);
            Style.setFontSize(textPane, FONT_SIZE);
            Style.setBackgroundColor(textPane, Colors.SECONDARY_COLOR);
        }

        @Override
        public void attachLogic() {
            collapseButton.addActionListener(e -> {
                boolean expanded = collapseButton.isSelected();
                containerTextPane.add(textPane, "grow");
                if (expanded) {
                    this.add(containerTextPane, "grow, wrap");
                } else {
                    this.remove(containerTextPane);
                }
                collapseButton.setIcon(expanded ? UiUtils.ARROW_DOWN : UiUtils.ARROW_RIGHT);
                this.revalidate();
                this.repaint();
            });
        }

        @Override
        public void compose() {
            header.add(collapseButton);
            header.add(title);
            this.add(header, "grow, wrap");
        }

        private void setTextToTextPane(String text) {
            StyledDocument doc = textPane.getStyledDocument();
            doc.removeUndoableEditListener(null);
            try {
                doc.remove(0, doc.getLength());
            } catch (BadLocationException e) {
                e.printStackTrace();
            }

            StyleContext sc = StyleContext.getDefaultStyleContext();
            AttributeSet keyStyle = sc.addAttribute(
                    SimpleAttributeSet.EMPTY,
                    StyleConstants.Foreground,
                    Color.decode("#60A5FA"));
            AttributeSet valueStyle = sc.addAttribute(
                    SimpleAttributeSet.EMPTY,
                    StyleConstants.Foreground,
                    Colors.ICON_COLOR);

            for (String line : text.split("\n")) {
                int idx = line.indexOf(":");
                if (idx != -1) {
                    String key = line.substring(0, idx).trim();
                    String value = line.substring(idx + 1).trim();

                    try {
                        doc.insertString(doc.getLength(), key, keyStyle);
                        doc.insertString(doc.getLength(), ": " + value + "\n", valueStyle);
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        doc.insertString(doc.getLength(), line + "\n", valueStyle);
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
