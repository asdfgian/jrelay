package com.jrelay.ui.components.shared;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.TokenTypes;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.jrelay.core.models.response.ContentDisplayType;
import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.shared.utils.template.Struct;

import lombok.Getter;

public class SyntaxEditor extends RTextScrollPane implements Struct {

    @Getter
    private final RSyntaxTextArea syntaxTextArea;
    private final SyntaxScheme scheme;

    public SyntaxEditor(String syntax) {
        super(new RSyntaxTextArea());
        this.syntaxTextArea = (RSyntaxTextArea) this.getTextArea();
        syntaxTextArea.setSyntaxEditingStyle(syntax);
        this.scheme = syntaxTextArea.getSyntaxScheme();
        this.build();
    }

    @Override
    public void initComponents() {
    }

    @Override
    public void configureStyle() {
        scheme.getStyle(TokenTypes.SEPARATOR).foreground = Color.WHITE;
        scheme.getStyle(TokenTypes.IDENTIFIER).foreground = Color.LIGHT_GRAY;
        scheme.getStyle(TokenTypes.LITERAL_STRING_DOUBLE_QUOTE).foreground = Color.decode("#e879f9");
        scheme.getStyle(TokenTypes.LITERAL_NUMBER_DECIMAL_INT).foreground = Color.decode("#9055a1");
        scheme.getStyle(TokenTypes.RESERVED_WORD).foreground = new Color(255, 102, 102);
        scheme.getStyle(TokenTypes.OPERATOR).foreground = new Color(255, 180, 180);
        scheme.getStyle(TokenTypes.COMMENT_EOL).foreground = Color.LIGHT_GRAY;

        Style.setFontSize(syntaxTextArea, 14f);
        Style.setBackgroundColor(syntaxTextArea, new JPanel().getBackground());
        Style.setCursor(syntaxTextArea, Cursor.TEXT_CURSOR);
        syntaxTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        syntaxTextArea.setSyntaxScheme(scheme);
        syntaxTextArea.setAntiAliasingEnabled(true);
        syntaxTextArea.setEditable(false);
        syntaxTextArea.setCodeFoldingEnabled(true);
        syntaxTextArea.setWrapStyleWord(true);
        syntaxTextArea.setCurrentLineHighlightColor(Color.decode("#1c1c1e"));
        syntaxTextArea.setSelectionColor(new JTextArea().getSelectionColor());

        // syntaxTextArea.setCaretColor(Color.WHITE);
        syntaxTextArea.setMarkOccurrences(true);
        syntaxTextArea.setPaintTabLines(true);
        // syntaxTextArea.setHighlightSecondaryLanguages(true);
        // syntaxTextArea.setFadeCurrentLineHighlight(true);

        switch (syntaxTextArea.getSyntaxEditingStyle()) {
            case SyntaxConstants.SYNTAX_STYLE_JSON_WITH_COMMENTS:
                styleJSON();
                break;
            case SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT:
                styleJavaScript();
                break;
            case SyntaxConstants.SYNTAX_STYLE_XML:
                styleXML();
                break;
            case SyntaxConstants.SYNTAX_STYLE_NONE:
                styleCurl();
                break;
        }

        // Gutter
        Gutter gutter = this.getGutter();

        gutter.setBackground(new JPanel().getBackground());
        gutter.setBorderColor(new Color(60, 60, 60));
        gutter.setLineNumberColor(new Color(130, 130, 130));
        gutter.setFoldIndicatorEnabled(true);
        gutter.setLineNumberFont(syntaxTextArea.getFont());

        // indenter
        syntaxTextArea.setTabSize(2);
    }

    private void styleJSON() {
        scheme.getStyle(TokenTypes.LITERAL_BOOLEAN).foreground = new Color(102, 255, 204);
        scheme.getStyle(TokenTypes.VARIABLE).foreground = Color.decode("#60A5FA");
        Style.setFontSize(syntaxTextArea, 14f);
    }

    private void styleXML() {
        scheme.getStyle(TokenTypes.MARKUP_TAG_NAME).foreground = Color.decode("#aa80fa");
        scheme.getStyle(TokenTypes.MARKUP_TAG_ATTRIBUTE).foreground = Color.decode("#749ef9");
        scheme.getStyle(TokenTypes.MARKUP_TAG_ATTRIBUTE_VALUE).foreground = Color.decode("#e879f9");
        scheme.getStyle(TokenTypes.MARKUP_PROCESSING_INSTRUCTION).foreground = Color.decode("#e879f9");
        scheme.getStyle(TokenTypes.MARKUP_COMMENT).foreground = Color.decode("#6a9955");
        scheme.getStyle(TokenTypes.LITERAL_NUMBER_DECIMAL_INT).foreground = Color.WHITE;
        scheme.getStyle(TokenTypes.LITERAL_NUMBER_FLOAT).foreground = Color.WHITE;
        scheme.getStyle(TokenTypes.LITERAL_NUMBER_HEXADECIMAL).foreground = Color.WHITE;
        scheme.getStyle(TokenTypes.MARKUP_TAG_DELIMITER).foreground = Color.WHITE;
        Style.setFontSize(syntaxTextArea, 14f);
    }

    private void styleHTML() {
        scheme.getStyle(TokenTypes.MARKUP_TAG_NAME).foreground = Color.decode("#aa80fa");
        scheme.getStyle(TokenTypes.MARKUP_TAG_ATTRIBUTE).foreground = Color.decode("#749ef9");
        scheme.getStyle(TokenTypes.MARKUP_TAG_ATTRIBUTE_VALUE).foreground = Color.decode("#e879f9");
        scheme.getStyle(TokenTypes.MARKUP_PROCESSING_INSTRUCTION).foreground = Color.decode("#e879f9");
        scheme.getStyle(TokenTypes.MARKUP_TAG_DELIMITER).foreground = Color.WHITE;
        scheme.getStyle(TokenTypes.MARKUP_COMMENT).foreground = Color.decode("#6a9955");
        scheme.getStyle(TokenTypes.LITERAL_STRING_DOUBLE_QUOTE).foreground = Color.decode("#ce9178");
        scheme.getStyle(TokenTypes.LITERAL_STRING_DOUBLE_QUOTE).foreground = Color.decode("#ce9178");
        scheme.getStyle(TokenTypes.LITERAL_NUMBER_DECIMAL_INT).foreground = Color.decode("#b5cea8");
        scheme.getStyle(TokenTypes.LITERAL_NUMBER_FLOAT).foreground = Color.decode("#b5cea8");
        scheme.getStyle(TokenTypes.LITERAL_NUMBER_HEXADECIMAL).foreground = Color.decode("#b5cea8");
        Style.setFontSize(syntaxTextArea, 14f);
    }

    private void styleJavaScript() {
        scheme.getStyle(TokenTypes.FUNCTION).foreground = new Color(255, 255, 153);
        scheme.getStyle(TokenTypes.RESERVED_WORD_2).foreground = new Color(255, 51, 153);
        Style.setFontSize(syntaxTextArea, 14f);
    }

    private void styleCurl() {
        scheme.getStyle(TokenTypes.IDENTIFIER).foreground = new Color(150, 200, 255);
        scheme.getStyle(TokenTypes.RESERVED_WORD).foreground = new Color(255, 105, 180);
        Style.setFontSize(syntaxTextArea, 11.5f);
    }

    @Override
    public void compose() {
    }

    public void setText(String text) {
        syntaxTextArea.setText("");
        syntaxTextArea.setText(text);
        syntaxTextArea.setCaretPosition(0);
    }

    public void setEditable(boolean b) {
        syntaxTextArea.setEditable(b);
    }

    public void setSyntaxEditingStyle(ContentDisplayType contentDisplayType) {
        switch (contentDisplayType) {
            case ContentDisplayType.JSON:
                syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON_WITH_COMMENTS);
                styleJSON();
                break;
            case ContentDisplayType.HTML:
                syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);
                styleHTML();
                break;
            case ContentDisplayType.XML:
                syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
                styleXML();
                break;
            default:
                syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
                styleCurl();
                break;
        }
    }

    public String getText() {
        return syntaxTextArea.getText();
    }

    public void setLineWrap(boolean wrap) {
        syntaxTextArea.setLineWrap(wrap);
        syntaxTextArea.revalidate();
        syntaxTextArea.repaint();
    }

    public boolean getLineWrap() {
        return syntaxTextArea.getLineWrap();
    }
}
