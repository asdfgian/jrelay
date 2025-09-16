package com.jrelay.ui.workbech;

import java.util.List;

import com.jrelay.core.models.Environment;
import com.jrelay.core.models.Environment.Variable;
import com.jrelay.core.models.request.Request;
import com.jrelay.ui.components.shared.ClosableTab;
import com.jrelay.ui.components.shared.TabGroup;
import com.jrelay.ui.components.shared.ViewerEnvVariable;
import com.jrelay.ui.workbech.http.TabContentHttp;
import com.jrelay.ui.workbech.http.TabContentHttpController;

import lombok.Getter;

public final class WorkbenchHttp extends TabGroup<TabContentHttp> implements Workbench {

    @Getter
    private static Environment selectedEnv = null;
    private final List<Environment> environments;

    public WorkbenchHttp(List<Environment> environments, TabContentHttp tabContentHttp) {
        super(tabContentHttp);
        this.environments = environments;
        this.loadEnvironments(buildEnvironmentNamesArray());
        new TabContentHttpController(tabContentHttp, getSelectedClosableTab());
    }

    public void reloadEnvironments() {
        this.loadEnvironments(buildEnvironmentNamesArray());
    }

    public static boolean isEnvironmentSelected() {
        return selectedEnv != null;
    }

    @Override
    public void attachLogic() {
        selectEnvironment.addActionListener(e -> {
            int index = selectEnvironment.getSelectedIndex();
            selectedEnv = (index <= 0) ? null : environments.get(index - 1);
        });
        viewerEnvVaribleButton.addActionListener(e -> {
            if (selectedEnv != null) {
                ViewerEnvVariable.showView(viewerEnvVaribleButton, selectedEnv);
            }
        });
    }

    public static String[] getSelectedEnvironmentVariableKeys() {
        if (selectedEnv == null) {
            return new String[0];
        }
        return selectedEnv.getVariables().stream()
                .map(Variable::getKey)
                .toArray(String[]::new);
    }

    private String[] buildEnvironmentNamesArray() {
        return environments.stream()
                .map(Environment::getName)
                .toArray(String[]::new);
    }

    @Override
    public TabContentHttp getSelectedTabContent() {
        return (TabContentHttp) getSelectedComponent();
    }

    @Override
    public TabContentHttp getNewInstanceTabContent(ClosableTab closableTab) {
        TabContentHttp tabContentHttp = new TabContentHttp();
        new TabContentHttpController(tabContentHttp, closableTab);
        return tabContentHttp;
    }

    public TabContentHttp getNewInstanceTabContent(Request requestLoaded, ClosableTab closableTab) {
        TabContentHttp tabContentHttp = new TabContentHttp(requestLoaded);
        new TabContentHttpController(tabContentHttp, closableTab);
        return tabContentHttp;
    }

}
