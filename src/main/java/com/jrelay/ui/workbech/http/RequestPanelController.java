package com.jrelay.ui.workbech.http;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import com.jrelay.core.builder.HttpClient;
import com.jrelay.core.models.request.HttpHeader;
import com.jrelay.core.models.request.QueryParameter;
import com.jrelay.core.models.request.Request;
import com.jrelay.core.models.request.auth.BasicAuth;
import com.jrelay.core.models.request.auth.BearerTokenAuth;
import com.jrelay.core.models.request.body.FormEncodeBody;
import com.jrelay.core.models.request.body.JsonBody;
import com.jrelay.core.models.request.body.PlainTextBody;
import com.jrelay.core.models.request.body.XmlBody;
import com.jrelay.core.models.response.Response;
import com.jrelay.core.service.HttpClientService;
import com.jrelay.core.service.HttpClientServiceImpl;
import com.jrelay.core.utils.StringUtils;
import com.jrelay.core.utils.i18n.LangManager;
import com.jrelay.ui.components.dialogs.FileNativeDialog;
import com.jrelay.ui.components.dialogs.MessageDialog;
import com.jrelay.ui.components.dialogs.SaveRequestDialog;
import com.jrelay.ui.components.dialogs.FileNativeDialog.Mode;
import com.jrelay.ui.components.dialogs.MessageDialog.Location;
import com.jrelay.ui.components.dialogs.MessageDialog.Type;
import com.jrelay.ui.components.shared.ClosableTab;
import com.jrelay.ui.components.shared.KeyValueFileRow;
import com.jrelay.ui.components.shared.KeyValueRow;
import com.jrelay.ui.components.shared.TextStyleField;
import com.jrelay.ui.controllers.AppController;
import com.jrelay.ui.shared.utils.mapper.Mapper;
import com.jrelay.ui.workbech.WorkbenchHttp;
import com.jrelay.ui.workbech.http.RequestPanel.AuthPanel;
import com.jrelay.ui.workbech.http.RequestPanel.BodyPanel;
import com.jrelay.ui.workbech.http.RequestPanel.HeadersPanel;
import com.jrelay.ui.workbech.http.RequestPanel.ParamsPanel;
import com.jrelay.ui.workbech.http.RequestPanel.RequestToolbar;
import com.jrelay.ui.workbech.http.RequestPanel.BodyPanel.BodyBinaryPanel;
import com.jrelay.ui.workbech.http.RequestPanel.BodyPanel.BodyFormDataPanel;
import com.jrelay.ui.workbech.http.RequestPanel.BodyPanel.BodyFormEncodePanel;
import com.jrelay.ui.workbech.http.RequestPanel.BodyPanel.BodyJsonPanel;
import com.jrelay.ui.workbech.http.RequestPanel.BodyPanel.BodyTextPanel;
import com.jrelay.ui.workbech.http.RequestPanel.BodyPanel.BodyXmlPanel;
import com.jrelay.ui.workbech.http.TabContentHttpController.RequestLifecycle;

public class RequestPanelController {

    private Request model;
    private final RequestPanel requestPanel;

    private final ClosableTab closableTab;
    private final Consumer<Response> fn;
    private final RequestLifecycle lifecycle;

    private final HttpClientService httpService;

    private final RequestToolbarController requestToolbarController;
    private final ParamsPanelController paramsPanelController;
    private final HeadersPanelController headersPanelController;
    private final AuthPanelController authPanelController;
    private final BodyPanelController bodyPanelController;

    /**
     * Constructs a {@code RequestPanelController} with the specified components and
     * dependencies.
     * <p>
     * This constructor initializes the request panel controller with the given
     * request panel,
     * response consumer, and lifecycle hooks. It also sets up the HTTP client
     * service,
     * retrieves the URL text field, initializes the request model, and configures
     * the necessary listeners.
     * <p>
     * This setup ensures the controller is ready to handle both new and existing
     * requests,
     * manage user interactions, and process HTTP responses.
     *
     * @param requestPanel the UI panel for building HTTP requests
     * @param fn           the callback function to handle received responses
     * @param lifecycle    the lifecycle hooks for request execution (start/finish
     *                     events)
     * @author @ASDG14N
     * @since 28-07-2025
     */
    public RequestPanelController(
            RequestPanel requestPanel,
            ClosableTab closableTab,
            Consumer<Response> fn,
            RequestLifecycle lifecycle) {
        this.requestPanel = requestPanel;
        this.closableTab = closableTab;
        this.fn = fn;
        this.lifecycle = lifecycle;
        this.httpService = new HttpClientServiceImpl(HttpClient.getInstance());

        this.requestToolbarController = new RequestToolbarController(requestPanel.getToolbar());
        this.paramsPanelController = new ParamsPanelController(requestPanel.getParamsPanel());
        this.headersPanelController = new HeadersPanelController(requestPanel.getHeadersPanel());
        this.authPanelController = new AuthPanelController(requestPanel.getAuthPanel());
        this.bodyPanelController = new BodyPanelController(requestPanel.getBodyPanel());
        initModel();
    }

    private class RequestToolbarController {
        private final JComboBox<?> methodComboBox;
        private final TextStyleField urlField;
        private final JButton sendButton;
        private final JButton saveButton;

        private RequestToolbarController(RequestToolbar requestToolbar) {
            this.methodComboBox = requestToolbar.getMethodComboBox();
            this.urlField = requestToolbar.getUrlField();
            this.sendButton = requestToolbar.getSendButton();
            this.saveButton = requestToolbar.getSaveButton();
            setupMethodChangeListener();
            setupSendButtonListener();
            setupUrlFieldKeyListener();
            attachTextChangeListener(this.urlField);
            setupSaveButtonListener();
            setupChangeMethodListener();
        }

        private void initializeWhenIsNew() {
            methodComboBox.setSelectedIndex(model.getMethod().ordinal());
            urlField.setText(model.getUrl());
        }

        public void initializeWhenIsNotNew() {
            methodComboBox.setSelectedIndex(model.getMethod().ordinal());
            urlField.setText(model.getUrl());
        }

        /**
         * Sets up a listener to handle changes in the HTTP method selection.
         * <p>
         * This method listens for actions on the method combo box and updates
         * the method label on the corresponding closable tab accordingly.
         *
         * @author @ASDG14N
         * @since 28-07-2025
         */
        private void setupChangeMethodListener() {
            this.methodComboBox.addActionListener(e -> {
                closableTab.setMethodLabel(Objects.requireNonNull(this.methodComboBox.getSelectedItem()).toString());
            });
        }

        /**
         * Registers a change listener for the HTTP method combo box.
         * <p>
         * This listener is triggered whenever the selected method changes,
         * allowing for future integration with change tracking or analytics.
         * Currently, it prints a message to the console for debugging.
         *
         * @author @ASDG14N
         * @since 04-08-2025
         */
        private void setupMethodChangeListener() {
            this.methodComboBox.addActionListener(e -> manageChanges());
        }

        /**
         * Sets up a key listener on the URL input field to trigger the Send button when
         * Enter is pressed.
         * <p>
         * This method enhances user experience by allowing quick submission of requests
         * via the Enter key while focused on the URL field, mimicking browser-like
         * behavior.
         * <p>
         * The listener simulates a click on the Send button when the Enter key is
         * detected.
         *
         * @author @ASDG14N
         * @since 31-07-2025
         */
        private void setupUrlFieldKeyListener() {
            this.urlField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        sendButton.doClick();
                    }
                }
            });
        }

        /**
         * Sets up a listener for the Send button to initiate an asynchronous HTTP
         * request.
         * <p>
         * This method registers an action listener on the Send button, which triggers
         * the request sending process when clicked.
         *
         * @author @ASDG14N
         * @since 28-07-2025
         */
        private void setupSendButtonListener() {
            this.sendButton.addActionListener(e -> {
                sendRequestAsync();
                AppController.requestHistoryController.add(new Request(getRequest()));
            });
        }

        /**
         * Sets up a listener for the Save button to handle request persistence.
         * <p>
         * This method registers an action listener that triggers the save logic
         * when the user clicks the Save button in the request panel toolbar.
         * <p>
         * The save operation is delegated to {@code handleSaveAction()}.
         *
         * @author @ASDG14N
         * @since 31-07-2025
         */
        private void setupSaveButtonListener() {
            this.saveButton.addActionListener(e -> handleSaveAction());
        }

        /**
         * Handles the logic for saving the current HTTP request.
         * <p>
         * If the request is not yet associated with a collection, a save dialog is
         * shown to prompt user input.
         * Otherwise, it checks if the request has changed compared to the original
         * version stored in the collection.
         * If changes are detected, the request is updated in the collection storage and
         * a success message is shown.
         * <p>
         * No action is taken if the request is unchanged or the original request cannot
         * be found.
         *
         * @author @ASDG14N
         * @since 31-07-2025
         */
        private void handleSaveAction() {
            Request currentRequest = getRequest();

            if (currentRequest.getIdCollection() == null) {
                SaveRequestDialog.showDialog(currentRequest, closableTab);
                return;
            }

            String idCollection = currentRequest.getIdCollection();
            String idReq = currentRequest.getIdRequest();
            Request originalRequest = AppController.collectionController.findRequestById(idCollection, idReq);

            if (originalRequest == null) {
                return;
            }

            if (!areRequestsEqual(originalRequest, currentRequest)) {
                AppController.collectionController.updateRequestById(idCollection, idReq, currentRequest);
                AppController.renderNodesTreeCollections();
                closableTab.restore();

                MessageDialog.showMessage(
                        Type.SUCCESS,
                        LangManager.text("app.messageDialog.saved.text"));
            }
        }

        /**
         * Sends the HTTP request asynchronously and updates the UI with the response.
         * <p>
         * This method retrieves the current request from the UI, displays its cURL
         * representation,
         * and uses a {@link SwingWorker} to perform the network operation in a
         * background thread.
         * It invokes lifecycle hooks before and after the request execution and passes
         * the response
         * to a callback for handling.
         * <p>
         * Exceptions during the request are caught and printed to the standard error
         * stream.
         *
         * @author @ASDG14N
         * @since 30-07-2025
         */
        private void sendRequestAsync() {
            Request request = getRequest();

            AppController.setCode(request.toCurl());

            SwingWorker<Response, Void> worker = new SwingWorker<>() {
                @Override
                protected Response doInBackground() throws InterruptedException, ExecutionException {
                    lifecycle.onStart();
                    return httpService.sendAsync(request).get();
                }

                @Override
                protected void done() {
                    lifecycle.onFinish();
                    try {
                        Response response = get();
                        AppController.appendRequestEntry(request, response);
                        fn.accept(response);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };
            worker.execute();
        }
    }

    private class ParamsPanelController {

        private final ParamsPanel paramsPanel;
        private final JPanel container;
        private KeyValueRow firstParam = null;
        private final List<KeyValueRow> paramsList = new ArrayList<>();

        private ParamsPanelController(ParamsPanel paramsPanel) {
            this.paramsPanel = paramsPanel;
            this.container = this.paramsPanel.getParameterContainer();
        }

        private void initializeWhenIsNew() {
            firstParam = this.paramsPanel.getFirstParameter();
            paramsList.add(firstParam);
            setupKeyValueKeyCheckBoxListener(true, firstParam);
            setupParamsListeners();
        }

        /**
         * Loads and initializes query parameters into the request panel.
         * <p>
         * This method retrieves query parameters from the loaded request model,
         * populates the first row with the initial values, and appends additional
         * rows if more parameters exist. It also sets up listeners for key events
         * and checkbox interactions to ensure real-time updates and validation.
         * <p>
         * If no parameters are loaded, only the first row is initialized and added.
         *
         * @author @ASDG14N
         * @since 31-07-2025
         */
        private void initializeWhenIsNotNew() {
            final List<KeyValueRow> loadedParamsList = Mapper.toRowsFromKeyValue(
                    model.getParams(),
                    QueryParameter::selected,
                    QueryParameter::key,
                    QueryParameter::value);

            if (!loadedParamsList.isEmpty()) {
                firstParam = this.paramsPanel.getFirstParameter();
                KeyValueRow first = loadedParamsList.getFirst();
                firstParam.getCheck().setSelected(first.getCheck().isSelected());
                firstParam.getKeyField().setText(first.getKeyField().getText());
                firstParam.getValueField().setText(first.getValueField().getText());
                paramsList.add(firstParam);
                setupKeyValueKeyCheckBoxListener(true, firstParam);

                for (int i = 1; i < loadedParamsList.size(); i++) {
                    addLoadedKeyValueRowTo(true, loadedParamsList.get(i), container, paramsList,
                            this::updateTabQueryTitle);
                }
            } else {
                firstParam = this.paramsPanel.getFirstParameter();
                paramsList.add(firstParam);
                setupKeyValueKeyCheckBoxListener(true, firstParam);
            }

            setupKeyValueKeyReleasedListener(true, container, firstParam, paramsList, this::updateTabQueryTitle);
        }

        /**
         * Sets up listeners for the parameters panel within the request panel.
         * <p>
         * Configures key release and remove button listeners for managing parameter
         * key-value pairs. These listeners automatically trigger updates to the tab
         * title when parameters are modified or removed.
         *
         * @author ASDFG14N
         * @since 08-08-2025
         */
        private void setupParamsListeners() {
            setupKeyValueKeyReleasedListener(true, container, firstParam, paramsList, this::updateTabQueryTitle);
            setupKeyValueRemoveButtonListener(true, container, firstParam, paramsList, this::updateTabQueryTitle);
        }

        private void updateTabQueryTitle(int n) {
            var tabbedPane = requestPanel.getTabbedPane();
            tabbedPane.setTitleAt(0, LangManager.text("requestPanel.tabbedPane.tab1.title.text") + "   (" + n + ")");
            tabbedPane.revalidate();
        }
    }

    private class HeadersPanelController {

        private final HeadersPanel headersPanel;
        private final JPanel container;
        private KeyValueRow firstHeader = null;
        private final List<KeyValueRow> headersList = new ArrayList<>();

        private HeadersPanelController(HeadersPanel headersPanel) {
            this.headersPanel = headersPanel;
            this.container = headersPanel.getHeaderContainer();
        }

        private void initializeWhenIsNew() {
            firstHeader = headersPanel.getFirstHeader();
            headersList.add(firstHeader);
            setupKeyValueKeyCheckBoxListener(false, firstHeader);
            setupHeadersListeners();
        }

        private void initializeWhenIsNotNew() {
            final var loadedHeadersList = Mapper.toRowsFromKeyValue(
                    model.getHeaders(),
                    HttpHeader::selected,
                    HttpHeader::key,
                    HttpHeader::value);

            if (!loadedHeadersList.isEmpty()) {
                final var first = loadedHeadersList.getFirst();
                firstHeader.getCheck().setSelected(first.getCheck().isSelected());
                firstHeader.getKeyField().setText(first.getKeyField().getText());
                firstHeader.getValueField().setText(first.getValueField().getText());
                headersList.add(firstHeader);
                setupKeyValueKeyCheckBoxListener(false, firstHeader);
                for (int i = 1; i < loadedHeadersList.size(); i++) {
                    addLoadedKeyValueRowTo(false, loadedHeadersList.get(i), container, headersList,
                            this::updateTabHeadersTitle);
                }
            } else {
                firstHeader = this.headersPanel.getFirstHeader();
                headersList.add(firstHeader);
                setupKeyValueKeyCheckBoxListener(false, firstHeader);
            }

            setupKeyValueKeyReleasedListener(false, container, firstHeader, headersList, this::updateTabHeadersTitle);
        }

        /**
         * Sets up listeners for the headers panel within the request panel.
         * <p>
         * Configures key release and remove button listeners for managing header
         * key-value pairs. These listeners automatically trigger updates to the tab
         * title when headers are modified or removed.
         *
         * @author ASDFG14N
         * @since 08-08-2025
         */
        private void setupHeadersListeners() {
            setupKeyValueKeyReleasedListener(false, container, firstHeader, headersList, this::updateTabHeadersTitle);
            setupKeyValueRemoveButtonListener(false, container, firstHeader, headersList, this::updateTabHeadersTitle);
        }

        private void updateTabHeadersTitle(int n) {
            var tabbedPane = requestPanel.getTabbedPane();
            tabbedPane.setTitleAt(1, LangManager.text("requestPanel.tabbedPane.tab2.title.text") + "   (" + n + ")");
            tabbedPane.revalidate();
        }

    }

    private class AuthPanelController {

        private final AuthPanel authPanel;

        private AuthPanelController(AuthPanel authPanel) {
            this.authPanel = authPanel;
            setupAuthPanelChangeListeners();
        }

        /**
         * Loads authentication data into the appropriate authentication panel.
         * <p>
         * This method checks the type of authentication used in the loaded request
         * model
         * and populates the corresponding UI fields. Currently supports loading of
         * {@link BasicAuth} credentials and logs {@link BearerTokenAuth} if present.
         * <p>
         * Additional authentication types can be handled by extending this method.
         *
         * @author @ASDG14N
         * @since 31-07-2025
         */
        private void initializeWhenIsNotNew() {
            final var auth = model.getAuth();
            if (auth instanceof BasicAuth(String username, String password)) {
                final var basicPanel = authPanel.getAuthBasicPanel();
                basicPanel.getUsernameField().setText(username);
                basicPanel.getPasswordField().setText(password);
            } else if (auth instanceof BearerTokenAuth(String token)) {
                //final var bearer = authPanel.getAuthBearerPanel();
                System.out.println(token);
            }
        }

        /**
         * Sets up listeners for detecting changes in the authentication panel fields.
         * <p>
         * This method attaches text change listeners to the username and password
         * fields
         * for basic authentication, and to the token field for bearer authentication.
         * <p>
         * These listeners are useful for tracking user input and validation.
         *
         * @author @ASDG14N
         * @since 04-08-2025
         */
        private void setupAuthPanelChangeListeners() {
            final var authBasic = authPanel.getAuthBasicPanel();
            attachTextChangeListener(authBasic.getUsernameField().getTextField());
            attachTextChangeListener(authBasic.getPasswordField().getPasswordField());

            final var authBearer = authPanel.getAuthBearerPanel();
            attachTextChangeListener(authBearer.getTokenField().getTextArea());
        }
    }

    private class BodyPanelController {
        private final BodyPanel bodyPanel;

        private final BodyJsonPanelController bodyJsonPanelController;
        private final BodyXmlPanelController bodyXmlPanelController;
        private final BodyTextPanelController bodyTextPanelController;
        private final BodyFormEncodePanelController bodyFormEncodePanelController;
        private final BodyFormDataPanelController bodyFormDataPanelController;
        private final BodyBinaryPanelController bodyBinaryPanelController;

        private BodyPanelController(BodyPanel bodyPanel) {
            this.bodyPanel = bodyPanel;
            this.bodyJsonPanelController = new BodyJsonPanelController(bodyPanel.getBodyJsonPanel());
            this.bodyXmlPanelController = new BodyXmlPanelController(bodyPanel.getBodyXmlPanel());
            this.bodyTextPanelController = new BodyTextPanelController(bodyPanel.getBodyTextPanel());
            this.bodyFormEncodePanelController = new BodyFormEncodePanelController(bodyPanel.getBodyFormEncodePanel());
            this.bodyFormDataPanelController = new BodyFormDataPanelController(bodyPanel.getBodyFormDataPanel());
            this.bodyBinaryPanelController = new BodyBinaryPanelController(bodyPanel.getBodyBinaryPanel());
        }

        public void initializeWhenIsNew() {
            bodyJsonPanelController.initializeWhenIsNew();
            bodyXmlPanelController.initializeWhenIsNew();
            bodyTextPanelController.initializeWhenIsNew();
            bodyFormEncodePanelController.initializeWhenIsNew();
            bodyFormDataPanelController.initializeWhenIsNew();
            bodyBinaryPanelController.initializeWhenIsNew();
        }

        /**
         * Loads the request body content into the corresponding body panel.
         * <p>
         * This method inspects the type of body used in the loaded request model and
         * updates
         * the appropriate UI panel. Currently, only {@link JsonBody} content is
         * rendered in the UI,
         * while other types ({@link XmlBody}, {@link PlainTextBody}, and
         * {@link FormEncodeBody}) are logged
         * to the console for debugging purposes.
         * <p>
         * Extend this method to support full UI integration of additional body types.
         *
         * @author @ASDG14N
         * @since 31-07-2025
         */
        public void initializeWhenIsNotNew() {
            final var body = model.getBody();
            if (body instanceof JsonBody json) {
                final var jsonPanel = bodyPanel.getBodyJsonPanel();
                jsonPanel.getJsonEditor().setText(json.content());
            }
            if (body instanceof XmlBody xml) {
                System.out.println(xml.content());
            }
            if (body instanceof PlainTextBody text) {
                System.out.println(text.content());
            }
            if (body instanceof FormEncodeBody form) {
                System.out.println(form.content());
            }
        }

        private class BodyJsonPanelController {
            private final BodyJsonPanel bodyJsonPanel;

            private BodyJsonPanelController(BodyJsonPanel bodyJsonPanel) {
                this.bodyJsonPanel = bodyJsonPanel;
            }

            private void initializeWhenIsNew() {
                attachTextChangeListener(bodyJsonPanel.getJsonEditor().getSyntaxTextArea());
            }
        }

        private class BodyXmlPanelController {
            private final BodyXmlPanel bodyXmlPanel;

            private BodyXmlPanelController(BodyXmlPanel bodyXmlPanel) {
                this.bodyXmlPanel = bodyXmlPanel;
            }

            private void initializeWhenIsNew() {
                attachTextChangeListener(this.bodyXmlPanel.getXmlEditor().getSyntaxTextArea());
            }
        }

        private class BodyTextPanelController {
            private final BodyTextPanel bodyTextPanel;

            private BodyTextPanelController(BodyTextPanel bodyTextPanel) {
                this.bodyTextPanel = bodyTextPanel;
            }

            private void initializeWhenIsNew() {
                attachTextChangeListener(bodyTextPanel.getTextEditor().getTextArea());
            }

        }

        private class BodyFormEncodePanelController {
            private final BodyFormEncodePanel bodyFormEncodePanel;
            private final JPanel container;
            private KeyValueRow firstFormEncode = null;
            private List<KeyValueRow> formEncodeList = null;

            private BodyFormEncodePanelController(BodyFormEncodePanel bodyFormEncodePanel) {
                this.bodyFormEncodePanel = bodyFormEncodePanel;
                this.container = bodyFormEncodePanel.getFormContainer();
            }

            public void initializeWhenIsNew() {
                firstFormEncode = bodyFormEncodePanel.getFirstFormEncode();
                formEncodeList = bodyFormEncodePanel.getFormEncodeList();
                formEncodeList.add(firstFormEncode);
                setupKeyValueKeyCheckBoxListener(false, firstFormEncode);
                setupFormEncodeListeners();
            }

            private void setupFormEncodeListeners() {
                setupKeyValueKeyReleasedListener(false, container, firstFormEncode, formEncodeList,
                        this::updateTabFormEncodeTitle);
                setupKeyValueRemoveButtonListener(false, container, firstFormEncode, formEncodeList,
                        this::updateTabFormEncodeTitle);
            }

            private void updateTabFormEncodeTitle(int n) {
                final var tabbedPane = bodyPanel.getTabbedPane();
                tabbedPane.setTitleAt(4,
                        LangManager.text("requestPanel.bodyPanel.tabbedPane.tab5.title.text") + "   (" + n + ")");
                tabbedPane.revalidate();
            }
        }

        private class BodyFormDataPanelController {
            private final BodyFormDataPanel bodyFormDataPanel;
            private final JPanel container;

            private KeyValueFileRow firstFormData = null;
            private List<KeyValueFileRow> formDataList = null;

            private BodyFormDataPanelController(BodyFormDataPanel bodyFormDataPanel) {
                this.bodyFormDataPanel = bodyFormDataPanel;
                this.container = bodyFormDataPanel.getFormDataContainer();
            }

            public void initializeWhenIsNew() {
                firstFormData = bodyFormDataPanel.getFirstFormData();
                formDataList = bodyFormDataPanel.getFormDataList();
                formDataList.add(firstFormData);
                setupKeyValueFileKeyCheckBoxListener(firstFormData);
                setupSelectFileButtonListener(firstFormData);
                setupFormDataListeners();
            }

            private void setupFormDataListeners() {
                setupKeyValueFileKeyReleasedListener(container, firstFormData, formDataList,
                        this::updateTabFormDataTitle);
            }

            private void updateTabFormDataTitle(int n) {
                var tabbedPane = bodyPanel.getTabbedPane();
                tabbedPane.setTitleAt(5,
                        LangManager.text("requestPanel.bodyPanel.tabbedPane.tab6.title.text") + "   (" + n + ")");
                tabbedPane.revalidate();
            }
        }

        private class BodyBinaryPanelController {
            private final BodyBinaryPanel bodyBinaryPanel;

            private BodyBinaryPanelController(BodyBinaryPanel bodyBinaryPanel) {
                this.bodyBinaryPanel = bodyBinaryPanel;
            }

            private void initializeWhenIsNew() {
                setupSelectBinaryFile();
            }

            private void setupSelectBinaryFile() {
                final var button = bodyBinaryPanel.getSelectFileButton();
                final var fileNameLabel = bodyBinaryPanel.getFileNameLabel();
                button.addActionListener(e -> {
                    final var result = FileNativeDialog.show(null, Mode.LOAD);

                    if (result != null) {
                        File selectedFile = new File(result.getFirst(), result.getSecond());
                        try {
                            String mimeType = Files.probeContentType(selectedFile.toPath());
                            System.out.println("Tipo MIME detectado: " + (mimeType != null ? mimeType : "desconocido"));
                            fileNameLabel.setText(selectedFile.getName());
                            bodyBinaryPanel.setFilePath(selectedFile.getAbsolutePath());
                        } catch (IOException ioException) {
                            MessageDialog.showMessage(
                                    Type.ERROR,
                                    LangManager
                                            .text("requestPanelController.setupSelectBinaryFile.messageDialog.message1.text"));
                            System.err.println(ioException.getMessage());
                        }
                    }
                });
            }
        }
    }

    private boolean areRequestsEqual(Request original, Request current) {
        return original.equals(current);
    }

    /**
     * Initializes the request model based on the state of the request panel.
     * <p>
     * If the panel represents a new request, a fresh model is initialized and
     * prepared.
     * If it's an existing request, the saved model is loaded and the UI is
     * populated accordingly.
     * <p>
     * This method ensures proper setup for both new and previously saved requests.
     *
     * @author @ASDG14N
     * @since 03-08-2025
     */
    private void initModel() {
        if (requestPanel.isNew()) {
            this.model = requestPanel.getModel();
            initializeWhenIsNew();
        } else {
            this.model = requestPanel.getModel();
            initializeWhenIsNotNew();
        }
    }

    /**
     * Initializes the request panel state when creating a new request.
     * <p>
     * This method retrieves the first parameter and header input rows
     * from the panel and adds them to their respective lists for tracking.
     * <p>
     * It ensures the UI is ready with default editable fields for a new request.
     *
     * @author @ASDG14N
     * @since 30-07-2025
     */
    private void initializeWhenIsNew() {
        requestToolbarController.initializeWhenIsNew();
        paramsPanelController.initializeWhenIsNew();
        headersPanelController.initializeWhenIsNew();
        bodyPanelController.initializeWhenIsNew();
    }

    /**
     * Initializes the request panel state when editing an existing request.
     * <p>
     * This method loads the toolbar settings, query parameters, headers,
     * and body content from the existing request model into the UI components.
     * <p>
     * It prepares the interface to reflect the saved request for viewing or
     * modification.
     *
     * @author @ASDG14N
     * @since 30-07-2025
     */
    private void initializeWhenIsNotNew() {
        this.requestToolbarController.initializeWhenIsNotNew();
        this.paramsPanelController.initializeWhenIsNotNew();
        this.headersPanelController.initializeWhenIsNotNew();
        this.authPanelController.initializeWhenIsNotNew();
        this.bodyPanelController.initializeWhenIsNotNew();
    }

    /**
     * Sets up a key release listener for a key-value row to handle dynamic row
     * creation.
     * <p>
     * When the user types in the last row's key field and it is not empty,
     * a new key-value row is automatically added to the specified container.
     * The provided consumer function is then invoked with the index of the newly
     * added row.
     *
     * @param isQueryParams {@code true} if the row belongs to query parameters;
     *                      {@code false} if it belongs to headers
     * @param container     the {@link JPanel} container holding the key-value rows
     * @param row           the current {@link KeyValueRow} to attach the listener
     *                      to
     * @param ls            the list of all key-value rows in the container
     * @param fun           a {@link Consumer} that accepts the index of the newly
     *                      added row
     * @author ASDFG14N
     * @since 08-08-2025
     */
    private void setupKeyValueKeyReleasedListener(
            boolean isQueryParams,
            JPanel container,
            KeyValueRow row,
            List<KeyValueRow> ls,
            Consumer<Integer> fun) {
        row.getKeyField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                boolean isLast = ls.indexOf(row) == ls.size() - 1;
                boolean hasText = !row.getKeyField().getText().trim().isEmpty();

                if (isLast && hasText) {
                    addKeyValueRowTo(isQueryParams, container, ls, fun);
                    fun.accept(ls.size() - 1);
                }
            }
        });
    }

    private void setupKeyValueFileKeyReleasedListener(
            JPanel container,
            KeyValueFileRow row,
            List<KeyValueFileRow> ls,
            Consumer<Integer> fun) {
        row.getKeyField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                boolean isLast = ls.indexOf(row) == ls.size() - 1;
                boolean hasText = !row.getKeyField().getText().trim().isEmpty();

                if (isLast && hasText) {
                    addKeyValueFileRowTo(container, ls, fun);
                    fun.accept(ls.size() - 1);
                }
            }
        });
    }

    /**
     * Sets up a remove button listener for a key-value row to handle row deletion.
     * <p>
     * When triggered, the specified row is removed from both the UI container and
     * the underlying list.
     * After removal, the provided consumer function is called with the updated list
     * size.
     * If the row belongs to query parameters, the request URL is updated
     * accordingly.
     * Finally, the container is revalidated and repainted to reflect the changes.
     *
     * @param isQueryParams {@code true} if the row belongs to query parameters;
     *                      {@code false} if it belongs to headers
     * @param container     the {@link JPanel} container holding the key-value rows
     * @param row           the {@link KeyValueRow} to be removed
     * @param ls            the list of all key-value rows in the container
     * @param fun           a {@link Consumer} that accepts the updated number of
     *                      rows
     * @author ASDFG14N
     * @since 08-08-2025
     */
    private void setupKeyValueRemoveButtonListener(
            boolean isQueryParams,
            JPanel container,
            KeyValueRow row,
            List<KeyValueRow> ls,
            Consumer<Integer> fun) {
        row.getRemoveRowBtn().addActionListener(e -> {
            container.remove(row);
            ls.remove(row);
            fun.accept(ls.size());
            if (isQueryParams) {
                updateUrl(ls, this.requestToolbarController.urlField);
            }
            container.revalidate();
            container.repaint();
        });
    }

    /**
     * Sets up a remove button listener for a {@link KeyValueFileRow} to handle its
     * deletion.
     * <p>
     * When triggered, the specified row is removed from both the UI container and
     * the underlying list.
     * After removal, the provided consumer function is called with the updated list
     * size.
     * Finally, the container is revalidated and repainted to reflect the changes.
     *
     * @param container the {@link JPanel} container holding the file key-value rows
     * @param row       the {@link KeyValueFileRow} to be removed
     * @param ls        the list of all {@link KeyValueFileRow} objects in the
     *                  container
     * @param fun       a {@link Consumer} that accepts the updated number of rows
     * @author ASDFG14N
     * @since 11-08-2025
     */
    private void setupKeyValueFileRemoveButtonListener(
            JPanel container,
            KeyValueFileRow row,
            List<KeyValueFileRow> ls,
            Consumer<Integer> fun) {
        row.getRemoveRowBtn().addActionListener(e -> {
            container.remove(row);
            ls.remove(row);
            fun.accept(ls.size());
            container.revalidate();
            container.repaint();
        });
    }

    /**
     * Adds a new {@link KeyValueRow} to the specified container and sets up its
     * listeners.
     * <p>
     * Configures the checkbox listener, key release listener, and remove button
     * listener
     * for the new row. If the row is for query parameters, also sets up a listener
     * to update the URL when parameters change. The row is then added to both the
     * container
     * and the underlying list, and the container is revalidated to reflect the
     * change.
     *
     * @param isQueryParams {@code true} if the row is for query parameters;
     *                      {@code false} if it is for headers
     * @param container     the {@link JPanel} container to which the new row will
     *                      be added
     * @param ls            the list maintaining all key-value rows
     * @param fun           a {@link Consumer} that accepts the index of the newly
     *                      added row
     * @author ASDFG14N
     * @since 08-08-2025
     */
    private void addKeyValueRowTo(
            boolean isQueryParams,
            JPanel container,
            List<KeyValueRow> ls,
            Consumer<Integer> fun) {
        final var row = new KeyValueRow(false);
        setupKeyValueKeyCheckBoxListener(isQueryParams, row);
        setupKeyValueKeyReleasedListener(isQueryParams, container, row, ls, fun);
        setupKeyValueRemoveButtonListener(isQueryParams, container, row, ls, fun);
        if (isQueryParams)
            setupKeyValueParamsListener(ls, this.requestToolbarController.urlField);
        container.add(row, "growx");
        ls.add(row);
        container.revalidate();
    }

    /**
     * Adds a new {@link KeyValueFileRow} to the specified container and sets up its
     * listeners.
     * <p>
     * Configures the checkbox listener, key release listener, and remove button
     * listener
     * for the newly created row. The row is then added to both the container and
     * the
     * underlying list, and the container is revalidated to update the UI.
     *
     * @param container the {@link JPanel} container to which the new file key-value
     *                  row will be added
     * @param ls        the list maintaining all {@link KeyValueFileRow} objects
     * @param fun       a {@link Consumer} that accepts the index of the newly added
     *                  row
     * @author ASDFG14N
     * @since 11-08-2025
     */
    private void addKeyValueFileRowTo(
            JPanel container,
            List<KeyValueFileRow> ls,
            Consumer<Integer> fun) {
        final var row = new KeyValueFileRow(false);
        setupKeyValueFileKeyCheckBoxListener(row);
        setupKeyValueFileKeyReleasedListener(container, row, ls, fun);
        setupKeyValueFileRemoveButtonListener(container, row, ls, fun);
        setupSelectFileButtonListener(row);
        container.add(row, "growx");
        ls.add(row);
        container.revalidate();
    }

    /**
     * Adds an existing (pre-loaded) {@link KeyValueRow} to the specified container
     * and sets up its listeners.
     * <p>
     * Configures the checkbox listener, key release listener, and remove button
     * listener
     * for the provided row. If the row is for query parameters, also sets up a
     * listener
     * to update the URL when parameters change. The row is then added to both the
     * container
     * and the underlying list, and the container is revalidated to update the UI.
     *
     * @param isQueryParams {@code true} if the row is for query parameters;
     *                      {@code false} if it is for headers
     * @param row           the {@link KeyValueRow} to be added
     * @param container     the {@link JPanel} container to which the row will be
     *                      added
     * @param ls            the list maintaining all key-value rows
     * @param fun           a {@link Consumer} that accepts the index of the newly
     *                      added row
     * @author ASDFG14N
     * @since 11-08-2025
     */
    private void addLoadedKeyValueRowTo(
            boolean isQueryParams,
            KeyValueRow row,
            JPanel container,
            List<KeyValueRow> ls,
            Consumer<Integer> fun) {
        setupKeyValueKeyCheckBoxListener(isQueryParams, row);
        setupKeyValueKeyReleasedListener(isQueryParams, container, row, ls, fun);
        setupKeyValueRemoveButtonListener(isQueryParams, container, row, ls, fun);
        if (isQueryParams) {
            setupKeyValueParamsListener(ls, this.requestToolbarController.urlField);
        }
        container.add(row, "growx");
        ls.add(row);
        container.revalidate();
    }

    /**
     * Sets up listeners on key-value parameter fields to dynamically update the
     * URL.
     * <p>
     * This method registers key listeners on both the key and value text fields of
     * each row.
     * Whenever a key is released, the URL field is updated to reflect the current
     * query parameters.
     * <p>
     * It ensures that changes to query parameters are immediately reflected in the
     * request URL.
     *
     * @param ls        the list of key-value rows representing query parameters
     * @param textField the URL text field to be updated in real time
     * @author @ASDG14N
     * @since 31-07-2025
     */
    private void setupKeyValueParamsListener(List<KeyValueRow> ls, JTextComponent textField) {
        for (KeyValueRow row : ls) {
            KeyAdapter listener = new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    updateUrl(getSelectedRows(ls), textField);
                }
            };
            row.getKeyField().addKeyListener(listener);
            row.getValueField().addKeyListener(listener);
        }
    }

    /**
     * Updates the URL field based on the provided list of query parameter rows.
     * <p>
     * This method constructs a new URL by appending encoded query parameters
     * to the base URL extracted from the current text field value. The updated
     * URL is then set back into the text field.
     * <p>
     * Empty keys are ignored during query string construction.
     *
     * @param rows      the list of key-value input rows representing query
     *                  parameters
     * @param textField the text field containing the base URL to be updated
     * @author @ASDG14N
     * @since 31-07-2025
     */
    private void updateUrl(List<KeyValueRow> rows, JTextComponent textField) {
        String baseUrl = StringUtils.extractBaseUrl(textField.getText().trim());

        Map<String, String> params = rows.stream()
                .filter(row -> !row.getKeyField().getText().trim().isEmpty())
                .collect(Collectors.toMap(
                        row -> row.getKeyField().getText().trim(),
                        row -> row.getValueField().getText().trim()));

        String fullUrl = StringUtils.buildUrl(baseUrl, params);
        textField.setText(fullUrl);
    }

    /**
     * Sets up a listener for the checkbox in a key-value row to manage query
     * parameter inclusion.
     * <p>
     * This method ensures that when the checkbox is toggled, it validates the
     * presence of a key.
     * If the checkbox is selected but the key field is empty, an error message is
     * shown and
     * the checkbox is reverted. If the key is valid, the URL is updated with
     * selected parameters.
     * <p>
     * Additionally, it enables auto-checking when the key field is edited.
     * This listener is only applied if the row represents query parameters.
     *
     * @param isQueryParams flag indicating whether the row belongs to query
     *                      parameters
     * @param row           the {@link KeyValueRow} to attach the listener to
     * @author @ASDG14N
     * @since 31-07-2025
     */
    private void setupKeyValueKeyCheckBoxListener(boolean isQueryParams, KeyValueRow row) {
        final var checkBox = row.getCheck();
        checkBox.addActionListener(e -> {
            String key = row.getKeyField().getText().trim();
            if (checkBox.isSelected() && key.isEmpty()) {
                MessageDialog.showMessage(
                        Location.TOP_CENTER,
                        Type.ERROR,
                        LangManager.text(
                                "requestPanelController.setupKeyValueKeyCheckBoxListener.messageDialog.message.text"));
                checkBox.setSelected(false);
                return;
            }
            if (isQueryParams) {
                updateUrl(getSelectedRows(this.paramsPanelController.paramsList),
                        this.requestToolbarController.urlField);
            }
        });
    }

    /**
     * Sets up a checkbox listener for a {@link KeyValueFileRow} to handle
     * validation when toggled.
     * <p>
     * Also enables auto-checking when the row is edited. If the checkbox is
     * selected while the
     * key field is empty, an error message is displayed, and the checkbox is
     * automatically unchecked.
     *
     * @param row the {@link KeyValueFileRow} for which the checkbox listener will
     *            be configured
     * @author ASDFG14N
     * @since 11-08-2025
     */
    private void setupKeyValueFileKeyCheckBoxListener(KeyValueFileRow row) {
        final var checkBox = row.getCheck();
        checkBox.addActionListener(e -> {
            String key = row.getKeyField().getText().trim();
            if (checkBox.isSelected() && key.isEmpty()) {
                MessageDialog.showMessage(
                        Location.TOP_CENTER,
                        Type.ERROR,
                        LangManager.text(
                                "requestPanelController.setupKeyValueFileKeyCheckBoxListener.messageDialog.message.text"));
                checkBox.setSelected(false);
                return;
            }
        });
    }

    private void setupSelectFileButtonListener(KeyValueFileRow row) {
        final var selectFileButton = row.getSelectFileButton();
        final var fileNameLabel = row.getFileNameLabel();
        final var contentTypeLabel = row.getContentTypeLabel();
        selectFileButton.addActionListener(e -> {
            final var result = FileNativeDialog.show(null, Mode.LOAD);

            if (result != null) {
                File selectedFile = new File(result.getFirst(), result.getSecond());
                try {
                    String mimeType = Files.probeContentType(selectedFile.toPath());
                    contentTypeLabel.setText(mimeType != null ? mimeType : "unknown");
                    fileNameLabel.setText(selectedFile.getName());
                    row.setFilePath(selectedFile.getAbsolutePath());
                } catch (IOException ioException) {
                    MessageDialog.showMessage(
                            Type.ERROR,
                            LangManager.text(
                                    "requestPanelController.setupSelectFileButtonListener.messageDialog.message1.text"));
                    System.err.println(ioException.getMessage());
                }
            }
        });

    }

    /**
     * Retrieves all key-value rows from the given list that are marked as selected.
     * <p>
     * Selection is determined by checking whether the associated checkbox of each
     * row
     * is selected.
     *
     * @param allRows the list of all {@link KeyValueRow} objects to filter
     * @return a list containing only the selected {@link KeyValueRow} objects
     * @author ASDFG14N
     * @since 08-08-2025
     */
    private List<KeyValueRow> getSelectedRows(List<KeyValueRow> allRows) {
        return allRows.stream()
                .filter(r -> r.getCheck().isSelected())
                .toList();
    }

    /**
     * Constructs and returns a {@link Request} object based on the current UI
     * state.
     * <p>
     * This method gathers data from various request panel components—such as the
     * method,
     * URL, parameters, headers, authentication, and body—and maps them into a
     * {@code Request} model.
     *
     * @return a populated Request object reflecting the current UI input
     * state
     * @author @ASDG14N
     * @since 28-07-2025
     */
    public Request getRequest() {
        if (WorkbenchHttp.isEnvironmentSelected()) {
            Request model = new Request(requestPanel.getModel());
            model.setMethod(Mapper.fromComboBox(requestPanel.getToolbar().getMethodComboBox()));
            var replacements = AppController.environmentController
                    .findVariablesByEnvironment(WorkbenchHttp.getSelectedEnv());
            String url = StringUtils.replaceVariables(requestPanel.getToolbar().getUrl(), replacements);
            model.setUrl(url);
            model.setParams(Mapper.fromRowsQueryParameters(this.paramsPanelController.paramsList));
            model.setHeaders(Mapper.fromRowsHttpHeaders(this.headersPanelController.headersList));
            model.setAuth(requestPanel.getAuthPanel().getAuth());
            model.setBody(requestPanel.getBodyPanel().getContent());
            return model;
        } else {
            Request model = new Request(requestPanel.getModel());
            model.setMethod(Mapper.fromComboBox(requestPanel.getToolbar().getMethodComboBox()));
            model.setUrl(requestPanel.getToolbar().getUrl());
            model.setParams(Mapper.fromRowsQueryParameters(this.paramsPanelController.paramsList));
            model.setHeaders(Mapper.fromRowsHttpHeaders(this.headersPanelController.headersList));
            model.setAuth(requestPanel.getAuthPanel().getAuth());
            model.setBody(requestPanel.getBodyPanel().getContent());
            return model;
        }
    }

    /**
     * Manages the state of the current tab based on changes in the request.
     * <p>
     * Compares the current request model with the latest request data. If
     * differences
     * are detected, marks the tab as having unsaved changes; otherwise, restores it
     * to the saved state.
     *
     * @author ASDFG14N
     * @since 11-08-2025
     */
    private void manageChanges() {
        if (!areRequestsEqual(model, getRequest())) {
            closableTab.changeDetected();
        } else {
            closableTab.restore();
        }
    }

    private void attachTextChangeListener(JTextComponent component) {
        component.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                //
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                manageChanges();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                manageChanges();
            }
        });
    }
}
