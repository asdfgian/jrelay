package com.jrelay.ui.components.shared;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

import com.jrelay.core.utils.StringUtils;
import com.jrelay.ui.shared.utils.template.Struct;
import com.jrelay.ui.workbech.WorkbenchHttp;

public class TextStyleField extends JTextField implements Struct {

    private ObserverHighlight observerHighlight;
    private ObserverEnv observerEnv;
    private Highlighter.HighlightPainter painter;

    public TextStyleField() {
        this.build();
    }

    public TextStyleField(String text) {
        super(text);
        this.build();
    }

    public TextStyleField(int columns) {
        super(columns);
        this.build();
    }

    @Override
    public void initComponents() {
        observerHighlight = new ObserverHighlight();
        observerEnv = new ObserverEnv();
        painter = new RoundedHighlightPainter(Color.decode("#48c95d"), Color.BLACK);

        this.getDocument().addDocumentListener(observerHighlight);
        this.getDocument().addDocumentListener(observerEnv);
    }

    @Override
    public void configureStyle() {
    }

    @Override
    public void compose() {
    }

    private void detectCallVariable() {
        SwingUtilities.invokeLater(() -> {
            try {
                if (WorkbenchHttp.isEnvironmentSelected()) {
                    int caretPos = this.getCaretPosition();
                    String text = this.getText(0, caretPos);
                    if (text.endsWith("{{")) {
                        showVariablePopup(this, caretPos);
                    }
                }
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void showVariablePopup(JTextComponent component, int caretPos) {
        JPopupMenu popup = new JPopupMenu();

        for (String var : WorkbenchHttp.getSelectedEnvironmentVariableKeys()) {
            JMenuItem item = new JMenuItem(var);
            item.addActionListener(e -> insertVariableAtCaret(component, var));
            popup.add(item);
        }

        Rectangle2D caretCoords = null;
        try {
            caretCoords = component.modelToView2D(caretPos);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        popup.show(component, (int) caretCoords.getX(), (int) (caretCoords.getY() + caretCoords.getHeight()));
    }

    private void insertVariableAtCaret(JTextComponent component, String variable) {
        String text = component.getText();
        int pos = component.getCaretPosition();

        String newText = StringUtils.insertVariable(text, pos, variable);

        if (!newText.equals(text)) {
            component.setText(newText);
            component.setCaretPosition(newText.indexOf(variable, pos) + variable.length() + 2);
        }
    }

    private void highlightVariable() {
        if (WorkbenchHttp.isEnvironmentSelected()) {
            Highlighter highlighter = getHighlighter();
            highlighter.removeAllHighlights();

            String text = getText();
            if (text == null || text.isEmpty())
                return;

            Pattern pattern = Pattern.compile("\\{\\{([^}]*)\\}\\}");
            Matcher matcher = pattern.matcher(text);

            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                try {
                    highlighter.addHighlight(start, end, painter);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class ObserverHighlight implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            highlightVariable();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            highlightVariable();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            highlightVariable();
        }

    }

    private class ObserverEnv implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            detectCallVariable();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }
    }

    private class RoundedHighlightPainter implements Highlighter.HighlightPainter {
        private final Color background;
        private final Color foreground;
        private final int arc = 8;
        private final int paddingY = 3;

        public RoundedHighlightPainter(Color background, Color foreground) {
            this.background = background;
            this.foreground = foreground;
        }

        @Override
        public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
            try {
                Rectangle2D start = c.modelToView2D(p0);
                Rectangle2D end = c.modelToView2D(p1);

                if (start == null || end == null)
                    return;

                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                double x = start.getX();
                double y = start.getY() - paddingY;
                double width = end.getX() - start.getX();
                double height = start.getHeight() + (paddingY * 2);

                g2.setColor(background);
                g2.fillRoundRect((int) x, (int) y, (int) width, (int) height, arc, arc);

                if (foreground != null) {
                    String highlightedText = c.getText().substring(p0, p1);
                    g2.setColor(foreground);
                    g2.setFont(c.getFont());
                    FontMetrics fm = c.getFontMetrics(c.getFont());
                    g2.drawString(highlightedText, (int) start.getX(), (int) (start.getY() + fm.getAscent()));
                }

            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

}
