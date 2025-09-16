package com.jrelay.ui.workbech.http;

import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import com.jrelay.core.models.request.Request;
import com.jrelay.ui.components.shared.statics.HttpWelcomePanel;
import com.jrelay.ui.components.shared.statics.WaitingPanel;
import com.jrelay.ui.shared.utils.animate.Animator;
import com.jrelay.ui.shared.utils.template.Struct;

import lombok.Getter;

@Getter
public class TabContentHttp extends JSplitPane implements Struct {

    private final RequestPanel requestPanel;
    private ResponsePanel responsePanel;
    private final WaitingPanel waitingPanel = new WaitingPanel();

    public TabContentHttp() {
        requestPanel = new RequestPanel();
        this.build();
    }

    public TabContentHttp(Request loadedRequest) {
        requestPanel = new RequestPanel(new Request(loadedRequest));
        this.build();
    }

    @Override
    public void initComponents() {
        responsePanel = new ResponsePanel();
    }

    @Override
    public void configureStyle() {
        this.setOrientation(JSplitPane.VERTICAL_SPLIT);
        this.setResizeWeight(0.5);
        this.setDividerSize(5);
    }

    @Override
    public void compose() {
        this.setLeftComponent(requestPanel);
        this.setRightComponent(new HttpWelcomePanel());
    }

    public void setHorizontalOrientation() {
        this.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        this.setResizeWeight(0.5);
        animateDividerTo();
    }

    public void setVerticalOrientation() {
        this.setOrientation(JSplitPane.VERTICAL_SPLIT);
        this.setResizeWeight(0.5);
        animateDividerTo();
    }

    private void animateDividerTo() {
        SwingUtilities.invokeLater(() -> {
            int totalSize = getOrientation() == JSplitPane.HORIZONTAL_SPLIT
                    ? getWidth()
                    : getHeight();

            int currentLocation = getDividerLocation();
            int targetLocation = (int) (totalSize * (float) 0.5);

            Animator animator = new Animator(250)
                    .onTimingEvent(fraction -> {
                        int interpolated = (int) (currentLocation + (targetLocation - currentLocation) * fraction);
                        setDividerLocation(interpolated);
                    });

            animator.start();
        });
    }

}
