package com.jrelay.ui.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;

import com.jrelay.core.models.Collection;
import com.jrelay.core.models.Preference.AccentColor;
import com.jrelay.core.models.Preference.Lang;
import com.jrelay.core.models.Preference.Theme;
import com.jrelay.core.utils.i18n.LangManager;
import com.jrelay.ui.components.dialogs.InputDialog;
import com.jrelay.ui.components.dialogs.MessageDialog;
import com.jrelay.ui.components.dialogs.SaveEnvDialog;
import com.jrelay.ui.components.dialogs.MessageDialog.Type;
import com.jrelay.ui.shared.styles.Colors;
import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.views.SideBar;

public class SideBarController {
    private final SideBar sideBar;

    public SideBarController(SideBar sideBar) {
        this.sideBar = sideBar;
        initAllListeners();
    }

    /**
     * Initializes all event listeners required for the application.
     * <p>
     * This method delegates the initialization process to specific listener
     * setup methods for the HTTP panel and the settings panel.
     * 
     * @author ASDFG14N
     * @since 15-08-2025
     */
    private void initAllListeners() {
        initListenersHttpPanel();
        initListenersSettingsPanel();
    }

    /**
     * Initializes event listeners for components within the HTTP panel.
     * <p>
     * Sets up listeners for the search functionality, the "New Environment" button,
     * and the copy-to-clipboard action.
     * 
     * @author ASDFG14N
     * @since 15-08-2025
     */
    private void initListenersHttpPanel() {
        setupSearch();
        setupNewCollectionButtonListener();
        setupNewEnvButtonListener();
        setupCopyButtonListener();
    }

    /**
     * Initializes event listeners for components within the settings panel.
     * <p>
     * Sets up listeners for language selection, theme background changes,
     * and accent color adjustments.
     * 
     * @author ASDFG14N
     * @since 15-08-2025
     */
    private void initListenersSettingsPanel() {
        setupSelectLangListener();
        setupThemeBackgroundListener();
        setupAccentColorListener();
    }

    /**
     * Sets up the listener for the language selection component in the settings
     * panel.
     * <p>
     * Configures the behavior of the language selector and synchronizes the current
     * selection with the application's active language settings.
     * 
     * @author ASDFG14N
     * @since 15-08-2025
     */
    private void setupSelectLangListener() {
        final var selectLanguage = sideBar.getSettingsPanel().getSelectLanguage();
        configureLanguageSelector(selectLanguage);
        syncLanguageSelection(selectLanguage);
    }

    /**
     * Configures the language selector {@link JComboBox} to update the
     * application's language preference.
     * <p>
     * When a new language is selected, the corresponding {@link Lang} enum value is
     * stored in the application's preference controller.
     *
     * @param selectLanguage the {@link JComboBox} representing the language
     *                       selector
     * 
     * @author ASDFG14N
     * @since 15-08-2025
     */
    private void configureLanguageSelector(JComboBox<String> selectLanguage) {
        selectLanguage.addActionListener(e -> {
            final var lang = Lang.values()[selectLanguage.getSelectedIndex()];
            Style.updateFont(lang);
            AppController.prefController.setLanguage(lang);
            LangManager.loadBundle(lang);
        });
    }

    /**
     * Synchronizes the language selector {@link JComboBox} with the currently
     * active application language.
     * <p>
     * Sets the selected index of the combo box to match the ordinal value of the
     * current
     * {@link Lang} returned by {@link LangManager#getLang()}.
     *
     * @param selectLanguage the {@link JComboBox} representing the language
     *                       selector
     * 
     * @author ASDFG14N
     * @since 15-08-2025
     */
    private void syncLanguageSelection(JComboBox<String> selectLanguage) {
        selectLanguage.setSelectedIndex(LangManager.getLang().ordinal());
    }

    /**
     * Sets up the listener for the "New Environment" button in the HTTP panel.
     * <p>
     * When the button is clicked, opens the {@link SaveEnvDialog} to allow the user
     * to create and save a new environment configuration.
     * 
     * @author ASDFG14N
     * @since 15-08-2025
     */
    private void setupNewEnvButtonListener() {
        final var newEnvButton = sideBar.getHttpPanel().getTreeEnviroment().getNewEnvButton();
        newEnvButton.addActionListener(e -> {
            SaveEnvDialog.showNewEnvDialog();
        });
    }

    private void setupNewCollectionButtonListener() {
        final var newButton = sideBar.getHttpPanel().getTreeCollections().getNewButton();
        newButton.addActionListener(e -> {
            String name = InputDialog.showDialog(LangManager.text("app.inputDialog.newCollection.title.text"));
            if (name == null)
                return;
            AppController.collectionController.saveCollection(new Collection(name, new ArrayList<>()));
            AppController.renderNodesTreeCollections();
        });
    }

    /**
     * Sets up listeners for the theme background selection buttons in the settings
     * panel.
     * <p>
     * Configures the buttons to allow users to switch between different application
     * themes.
     * 
     * @author ASDFG14N
     * @since 15-08-2025
     */
    private void setupThemeBackgroundListener() {
        final var themeButtons = sideBar.getSettingsPanel().getThemeButtons();
        configureThemeButtons(themeButtons);
    }

    /**
     * Configures the theme selection buttons to apply the corresponding theme when
     * clicked.
     * <p>
     * Iterates through the provided list of {@link JButton} components and assigns
     * an action
     * listener to each button that applies the theme associated with its index.
     *
     * @param themeButtons a list of {@link JButton} components representing theme
     *                     options
     * 
     * @author ASDFG14N
     * @since 15-08-2025
     */
    private void configureThemeButtons(List<JButton> themeButtons) {
        for (int i = 0; i < themeButtons.size(); i++) {
            final int index = i;
            themeButtons.get(i).addActionListener(e -> applyTheme(index));
        }
    }

    /**
     * Applies the selected theme to the application based on the given index.
     * <p>
     * Updates the theme preference in the application's preference controller
     * and applies the corresponding visual styles using the {@link Style} utility.
     *
     * @param index the index of the theme to apply, corresponding to {@link Theme}
     *              enum values
     * 
     * @author ASDFG14N
     * @since 15-08-2025
     */
    private void applyTheme(int index) {
        AppController.prefController.setTheme(Theme.values()[index]);
        Style.updateTheme(index);
    }

    /**
     * Sets up listeners for the accent color selection radio buttons in the
     * settings panel.
     * <p>
     * Configures the buttons to apply the selected accent color and synchronizes
     * the
     * selection with the currently active accent color preference.
     * 
     * @author ASDFG14N
     * @since 15-08-2025
     */
    private void setupAccentColorListener() {
        final var listRadioButton = sideBar.getSettingsPanel().getAccentColoRadioButtons();
        configureAccentColorButtons(listRadioButton);
        syncAccentColorSelection(listRadioButton);
    }

    /**
     * Configures the accent color radio buttons to apply the selected color when
     * chosen.
     * <p>
     * Groups the provided {@link JRadioButton} components to ensure only one can be
     * selected at a time. Each button is assigned an action listener that updates
     * the application's accent color using the {@link Style} utility.
     *
     * @param radioButtons a list of {@link JRadioButton} components representing
     *                     accent color options
     * 
     * @author ASDFG14N
     * @since 15-08-2025
     */
    private void configureAccentColorButtons(List<JRadioButton> radioButtons) {
        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < radioButtons.size(); i++) {
            final int index = i;
            JRadioButton radioButton = radioButtons.get(i);

            group.add(radioButton);
            radioButton.addActionListener(e -> {
                AccentColor selectedColor = AccentColor.values()[index];
                Style.updateAccentColor(selectedColor);
            });
        }
    }

    /**
     * Synchronizes the accent color radio buttons with the currently active
     * application accent color.
     * <p>
     * Sets the selected state of the radio button corresponding to the ordinal
     * value
     * of the current {@link Colors#ACCENT_COLOR}.
     *
     * @param radioButtons a list of {@link JRadioButton} components representing
     *                     accent color options
     * 
     * @author ASDFG14N
     * @since 15-08-2025
     */
    private void syncAccentColorSelection(List<JRadioButton> radioButtons) {
        int selectedIndex = Colors.ACCENT_COLOR.ordinal();
        if (selectedIndex >= 0 && selectedIndex < radioButtons.size()) {
            radioButtons.get(selectedIndex).setSelected(true);
        }
    }

    /**
     * Sets up the listener for the "Copy to Clipboard" button in the HTTP panel.
     * <p>
     * When clicked, retrieves the text content from the code editor, copies it
     * to the system clipboard, and displays a success message if the content is not
     * empty.
     * 
     * @author ASDFG14N
     * @since 15-08-2025
     */
    private void setupCopyButtonListener() {
        sideBar.getHttpPanel().getCodePanel().getCopyButton().addActionListener(e -> {
            final String curl = sideBar.getHttpPanel().getCodePanel().getEditor().getText();
            if (!curl.isEmpty()) {
                AppController.osManager.putInClipboard(curl);

                MessageDialog.showMessage(
                        Type.SUCCESS,
                        LangManager.text("app.messageDialog.copied.text"));
            }
        });
    }

    /**
     * Sets up the search functionality in the HTTP panel.
     * <p>
     * Adds a listener to the search field that triggers the
     * {@link #handleSearch(String)}
     * method whenever the text changes, using a trimmed and lowercased version of
     * the input.
     * 
     * @author ASDFG14N
     * @since 15-08-2025
     */
    private void setupSearch() {
        sideBar.getHttpPanel()
                .getSearchField()
                .onChange(text -> handleSearch(text.trim().toLowerCase()));
    }

    /**
     * Handles the search query by updating the displayed collections.
     * <p>
     * If the query is empty, all collections are rendered. Otherwise, only
     * collections
     * matching the query are displayed by invoking
     * {@link #renderFilteredCollections(String)}.
     *
     * @param query the search query string, expected to be trimmed and lowercased
     * 
     * @author ASDFG14N
     * @since 15-08-2025
     */
    private void handleSearch(String query) {
        final int idx = sideBar.getHttpPanel().getTabbedPane().getSelectedIndex();
        if (idx == 0) {
            if (query.isEmpty()) {
                sideBar.getHttpPanel().getTreeCollections().renderNodes();
                return;
            }
            renderFilteredCollections(query);
        } else if (idx == 1) {
            if (query.isEmpty()) {
                sideBar.getHttpPanel().getTreeEnviroment().renderNodes();
                return;
            }
            renderFilteredEnvironments(query);
        }
    }

    /**
     * Renders only the collection and request nodes that match the given search
     * query.
     * <p>
     * Iterates through all collections and their requests, adding any items whose
     * names match the query to a filtered list. The filtered nodes are then
     * rendered
     * in the HTTP panel's collection tree.
     *
     * @param query the search query string used to filter collections and requests
     * 
     * @author ASDFG14N
     * @since 06-08-2025
     */
    private void renderFilteredCollections(String query) {
        List<Object> results = new ArrayList<>();

        for (var collection : AppController.collectionController.findAll()) {
            if (matchesQuery(collection.getName(), query)) {
                results.add(collection);
            }
            for (var request : collection.getRequests()) {
                if (matchesQuery(request.getName(), query)) {
                    results.add(request);
                }
            }
        }

        sideBar.getHttpPanel()
                .getTreeCollections()
                .renderFilteredNodes(results);
    }

    private void renderFilteredEnvironments(String query) {
        List<Object> results = new ArrayList<>();

        for (var environmet : AppController.environmentController.findAll()) {
            if (matchesQuery(environmet.getName(), query)) {
                results.add(environmet);
            }
        }

        sideBar.getHttpPanel()
                .getTreeEnviroment()
                .renderFilteredNodes(results);
    }

    /**
     * Checks if the given name matches the search query.
     * <p>
     * Performs a case-insensitive containment check, returning {@code true} if the
     * name is not null and contains the query string.
     *
     * @param name  the name to check
     * @param query the search query string
     * @return {@code true} if the name contains the query; {@code false} otherwise
     * 
     * @author ASDFG14N
     * @since 15-08-2025
     */
    private boolean matchesQuery(String name, String query) {
        return name != null && name.toLowerCase().contains(query);
    }
}
