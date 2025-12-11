package com.jrelay.ui.components.shared;

import javax.swing.JPanel;

import com.jrelay.core.utils.i18n.LangManager;
import com.jrelay.ui.components.dialogs.ConfirmDialog;
import com.jrelay.ui.components.dialogs.ConfirmDialog.Option;
import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.shared.utils.UiUtils;
import com.jrelay.ui.shared.utils.animate.Animator;
import com.jrelay.ui.shared.utils.template.Struct;
import com.jrelay.ui.workbech.WorkbenchHttp;

import java.awt.AlphaComposite;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import lombok.Getter;
import net.miginfocom.swing.MigLayout;

public class ClosableTab extends JPanel implements Struct {

    private boolean changes = false;
    @Getter
    private boolean closeIcon = true;
    private final JLabel methodLabel = new JLabel("GET");
    private String title = LangManager.text("model.request.name");
    private JLabel titleLabel;
    @Getter
    private final JButton closeButton = new JButton(UiUtils.CLOSE_ICON);
    private TabGroup<?> tabGroup;

    public ClosableTab(TabGroup<?> tabGroup) {
        this.tabGroup = tabGroup;
        this.build();
    }

    public ClosableTab(TabGroup<?> tabGroup, String title) {
        this.title = title;
        this.tabGroup = tabGroup;
        this.build();
    }

    @Override
    public void initComponents() {
        titleLabel = new JLabel(title);
    }

    @Override
    public void configureStyle() {
        Style.setLayout(this, new MigLayout("fill", "[]10[]3[]"));
        Style.setCursor(this, Cursor.HAND_CURSOR);
        Style.setInsets(this, 6, 15, 7, 10);
        Style.setTransparent(this);
        Style.setFontSize(methodLabel, 12.5f);
        Style.setTextColor(methodLabel, Style.getColorByMethod("GET"));
        Style.setTransparent(closeButton);
        Style.setFontSize(titleLabel, 13.5f);
    }

    @Override
    public void attachLogic() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == 2) {
                    closeButton.doClick();
                }
            }
        });
        closeButton.addActionListener(e -> {
            if (!(tabGroup instanceof WorkbenchHttp workbenchHttp)) {
                return;
            }
            int index = workbenchHttp.indexOfTabComponent(this);
            if (index == -1) {
                return;
            }
            boolean hasAtLeastTwoTabs = tabGroup.getTabCount() - 1 >= 2;
            if (!hasAtLeastTwoTabs) {
                return;
            }
            if (changes) {
                Option option = ConfirmDialog.show(LangManager.text("closableTab.confirmDialog.message.text"));
                if (option == Option.YES) {
                    workbenchHttp.getSelectedTabContent()
                            .getRequestPanel()
                            .getToolbar()
                            .getSaveButton()
                            .doClick();
                } else if (option == Option.NO) {
                    tabGroup.remove(index);
                }
            } else {
                tabGroup.remove(index);
            }

        });
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!closeIcon) {
                    animateIconTransition(UiUtils.DOT_ICON, UiUtils.CLOSE_ICON);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!closeIcon) {
                    animateIconTransition(UiUtils.CLOSE_ICON, UiUtils.DOT_ICON);
                }
            }

            private void animateIconTransition(Icon fromIcon, Icon toIcon) {
                final float[] alphas = new float[] { 1f };

                Animator animator = new Animator(150)
                        .onTimingEvent(fraction -> {
                            alphas[0] = 1 - fraction;
                            Icon blended = blendIcons(fromIcon, toIcon, fraction);
                            closeButton.setIcon(blended);
                        })
                        .onEnd(() -> closeButton.setIcon(toIcon));
                animator.start();
            }

            private Icon blendIcons(Icon from, Icon to, float progress) {
                int w = Math.max(from.getIconWidth(), to.getIconWidth());
                int h = Math.max(from.getIconHeight(), to.getIconHeight());
                BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = image.createGraphics();
                g.setComposite(AlphaComposite.SrcOver.derive(1 - progress));
                from.paintIcon(null, g, 0, 0);
                g.setComposite(AlphaComposite.SrcOver.derive(progress));
                to.paintIcon(null, g, 0, 0);
                g.dispose();
                return new ImageIcon(image);
            }
        });

    }

    @Override
    public void compose() {
        this.add(methodLabel);
        this.add(titleLabel, "w 80!");
        this.add(closeButton);
    }

    public void changeDetected() {
        closeIcon = false;
        changes = true;
        closeButton.setIcon(UiUtils.DOT_ICON);
    }

    public void setTitle(String title) {
        this.title = title;
        titleLabel.setText(title);
    }

    public void restore() {
        closeIcon = true;
        changes = false;
        closeButton.setIcon(UiUtils.CLOSE_ICON);
    }

    public void setMethodLabel(String method) {
        Style.setTextColor(methodLabel, Style.getColorByMethod(method));
        this.methodLabel.setText(method);
    }

    public String getMethodName() {
        return methodLabel.getText();
    }

    public String getTitle() {
        return titleLabel.getText();
    }

}
