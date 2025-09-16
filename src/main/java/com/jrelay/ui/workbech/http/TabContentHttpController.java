package com.jrelay.ui.workbech.http;

import com.jrelay.core.models.response.Response;
import com.jrelay.ui.components.shared.ClosableTab;
import com.jrelay.ui.components.shared.WarningMessagePanel;

public class TabContentHttpController {

    public interface RequestLifecycle {
        void onStart();

        void onFinish();
    }

    private final TabContentHttp tabContentHttp;
    private final RequestPanel requestPanel;
    private final ResponsePanel responsePanel;
    private final ResponsePanelController responsePanelController;
    private final ClosableTab closableTab;

    public TabContentHttpController(TabContentHttp tabContentHttp, ClosableTab closableTab) {
        this.tabContentHttp = tabContentHttp;
        this.closableTab = closableTab;
        this.requestPanel = tabContentHttp.getRequestPanel();
        this.responsePanel = tabContentHttp.getResponsePanel();
        createRequestPanelController();
        this.responsePanelController = new ResponsePanelController(responsePanel);
    }

    private void createRequestPanelController() {
        RequestLifecycle lifecycle = createRequestLifecycle();
        new RequestPanelController(requestPanel, closableTab, this::handleResponse, lifecycle);
    }

    private RequestLifecycle createRequestLifecycle() {
        return new RequestLifecycle() {
            @Override
            public void onStart() {
                tabContentHttp.setRightComponent(tabContentHttp.getWaitingPanel());
                requestPanel.getToolbar().changeLoadingButton();
            }

            @Override
            public void onFinish() {
                tabContentHttp.setRightComponent(responsePanel);
                requestPanel.getToolbar().restoreButton();
            }
        };
    }

    /**
     * Handles the received HTTP response by displaying it in the response panel.
     * <p>
     * This method delegates the response data to the response panel controller
     * to render it in the user interface.
     *
     * @param response the HTTP response to be displayed in the UI panel
     * @author @ASDG14N
     * @since 28-07-2025
     */
    private void handleResponse(Response response) {
        if (response.hasError()) {
            tabContentHttp.setRightComponent(WarningMessagePanel.getInstance(response.errorMessage()));
        } else {
            responsePanelController.show(response);
        }
    }
}
