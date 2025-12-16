package com.jrelay.ui.workbech.http;

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import com.jrelay.core.models.request.Request;
import com.jrelay.core.models.request.auth.Auth;
import com.jrelay.core.models.request.auth.BasicAuth;
import com.jrelay.core.models.request.auth.BearerTokenAuth;
import com.jrelay.core.models.request.auth.OAuth1Auth;
import com.jrelay.core.models.request.auth.OAuth1Auth.SignatureMethod;
import com.jrelay.core.models.request.body.Body;
import com.jrelay.core.models.request.body.FormDataBody.FormDataPart;
import com.jrelay.core.models.request.body.FormEncodeBody.FormEncodePart;
import com.jrelay.core.utils.i18n.LangManager;
import com.jrelay.ui.components.shared.KeyValueFileRow;
import com.jrelay.ui.components.shared.KeyValueRow;
import com.jrelay.ui.components.shared.NonePanel;
import com.jrelay.ui.components.shared.PasswordField;
import com.jrelay.ui.components.shared.PlainTextEditor;
import com.jrelay.ui.components.shared.SyntaxEditor;
import com.jrelay.ui.components.shared.TextField;
import com.jrelay.ui.components.shared.TextStyleField;
import com.jrelay.ui.shared.styles.Colors;
import com.jrelay.ui.shared.styles.Style;
import com.jrelay.ui.shared.utils.TextComponentUtils;
import com.jrelay.ui.shared.utils.UiUtils;
import com.jrelay.ui.shared.utils.layout.Layout;
import com.jrelay.ui.shared.utils.mapper.BodyMapper;
import com.jrelay.ui.shared.utils.template.Struct;
import com.jrelay.ui.shared.utils.template.Translatable;

import lombok.Getter;
import lombok.Setter;
import net.miginfocom.swing.MigLayout;

@Getter
public class RequestPanel extends JPanel implements Struct, Translatable {

    private final boolean isNew;
    private Request model;
    private RequestToolbar toolbar;
    private JTabbedPane tabbedPane = new JTabbedPane();
    private ParamsPanel paramsPanel;
    private HeadersPanel headersPanel;
    private AuthPanel authPanel;
    private BodyPanel bodyPanel;

    public RequestPanel() {
        this.model = new Request();
        this.isNew = true;
        this.build();
        LangManager.register(this);
    }

    public RequestPanel(Request model) {
        this.model = model;
        this.isNew = false;
        this.build();
        LangManager.register(this);
    }

    @Override
    public void initComponents() {
        toolbar = new RequestToolbar();
        paramsPanel = new ParamsPanel();
        headersPanel = new HeadersPanel();
        authPanel = new AuthPanel();
        bodyPanel = new BodyPanel();
    }

    @Override
    public void configureStyle() {
        Style.setLayout(this, Layout.requestView());
        Style.setTabArc(tabbedPane);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    }

    @Override
    public void compose() {
        tabbedPane.addTab(LangManager.text("requestPanel.tabbedPane.tab1.title.text"), paramsPanel);
        tabbedPane.addTab(LangManager.text("requestPanel.tabbedPane.tab2.title.text"), headersPanel);
        tabbedPane.addTab(LangManager.text("requestPanel.tabbedPane.tab3.title.text"), authPanel);
        tabbedPane.addTab(LangManager.text("requestPanel.tabbedPane.tab4.title.text"), bodyPanel);

        this.add(toolbar, "cell 0 0, growx");
        this.add(tabbedPane, "cell 0 1, grow");
    }

    @Override
    public void updateText() {
        tabbedPane.setTitleAt(0, LangManager.text("requestPanel.tabbedPane.tab1.title.text"));
        tabbedPane.setTitleAt(1, LangManager.text("requestPanel.tabbedPane.tab2.title.text"));
        tabbedPane.setTitleAt(2, LangManager.text("requestPanel.tabbedPane.tab3.title.text"));
        tabbedPane.setTitleAt(3, LangManager.text("requestPanel.tabbedPane.tab4.title.text"));
    }

    public class RequestToolbar extends JPanel implements Struct, Translatable {
        private final JPanel containerSearch = new JPanel();
        @Getter
        private final JComboBox<String> methodComboBox = new JComboBox<>(new String[] {
                "GET", "POST", "PUT",
                "PATCH", "DELETE", "OPTIONS" });
        @Getter
        private final TextStyleField urlField = new TextStyleField();
        @Getter
        private final JButton sendButton = new JButton(LangManager.text("requestPanel.toolbar.sendButton.text"));
        @Getter
        private JButton saveButton = new JButton(UiUtils.SAVE_ICON);

        private RequestToolbar() {
            this.build();
            LangManager.register(this);
        }

        @Override
        public void initComponents() {
            TextComponentUtils.addDefaultContextMenu(urlField);
        }

        @Override
        public void configureStyle() {
            Style.setLayout(this, new MigLayout("fillx, insets 0", "[grow]5[]5[]"));
            Style.setLayout(containerSearch, new MigLayout("fillx, insets 2", "[]8[420::,grow]"));
            Style.setBackgroundColor(containerSearch, Colors.TEXT_FIELD_COLOR);
            Style.setRoundComponent(containerSearch);
            Style.setBackgroundColor(urlField, Colors.SECONDARY_COLOR);
            Style.setCursor(methodComboBox, Cursor.HAND_CURSOR);
            Style.setFontSize(methodComboBox, 15f);
            Style.setFontSize(sendButton, 15f);
            Style.setCursor(sendButton, Cursor.HAND_CURSOR);
            Style.setCursor(saveButton, Cursor.HAND_CURSOR);
            Style.setBackgroundColor(saveButton, Colors.SECONDARY_COLOR);
            Style.setToolTip(saveButton, LangManager.text("requestPanel.toolbar.saveButton.tooTip.text"));
            Style.setUndecoratedButton(saveButton);
        }

        @Override
        public void compose() {
            containerSearch.add(methodComboBox, "w 95!, h 35!");
            containerSearch.add(urlField, "h 35!, growx");
            this.add(containerSearch, "grow");
            this.add(sendButton, "h 39!, grow");
            this.add(saveButton, "h 39!, w 39!, grow");
        }

        @Override
        public void updateText() {
            Style.setButtonText(sendButton, LangManager.text("requestPanel.toolbar.sendButton.text"));
            Style.setToolTip(saveButton, LangManager.text("requestPanel.toolbar.saveButton.tooTip.text"));
        }

        public String getUrl() {
            return urlField.getText().trim();
        }

        public void changeLoadingButton() {
            sendButton.setText(LangManager.text("requestPanel.toolbar.sendButton.loading.text"));
            sendButton.revalidate();
        }

        public void restoreButton() {
            sendButton.setText(LangManager.text("requestPanel.toolbar.sendButton.text"));
            sendButton.revalidate();
        }
    }

    public class ParamsPanel extends JPanel implements Struct, Translatable {
        private final JLabel titleLabel = new JLabel(LangManager.text("requestPanel.paramsPanel.titleLabel.text"));
        @Getter
        private final JPanel parameterContainer = new JPanel(new MigLayout("fillx, wrap 1", "[grow]"));
        private JScrollPane scrollPane;
        @Getter
        private KeyValueRow firstParameter;

        public ParamsPanel() {
            this.build();
            LangManager.register(this);
        }

        @Override
        public void initComponents() {
            firstParameter = new KeyValueRow(true);
            scrollPane = new JScrollPane(parameterContainer,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        }

        @Override
        public void configureStyle() {
            Style.setLayout(this, new MigLayout("fillx, wrap 1, insets 10", "[grow]"));
            Style.setTransparent(this);
        }

        @Override
        public void compose() {
            parameterContainer.add(firstParameter, "growx");
            this.add(titleLabel, "wrap");
            this.add(scrollPane, "grow, pushy");
        }

        @Override
        public void updateText() {
            Style.setLabelText(titleLabel, LangManager.text("requestPanel.paramsPanel.titleLabel.text"));
            firstParameter.updateText();
        }
    }

    public class HeadersPanel extends JPanel implements Struct, Translatable {
        private final JLabel titleLabel = new JLabel(LangManager.text("requestPanel.headersPanel.titleLabel.text"));
        @Getter
        private final JPanel headerContainer = new JPanel(new MigLayout("fillx, wrap 1", "[grow]"));
        private JScrollPane scrollPane;
        @Getter
        private KeyValueRow firstHeader;

        public HeadersPanel() {
            this.build();
            LangManager.register(this);
        }

        @Override
        public void initComponents() {
            firstHeader = new KeyValueRow(true);
            scrollPane = new JScrollPane(headerContainer,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        }

        @Override
        public void configureStyle() {
            Style.setLayout(this, new MigLayout("fillx, wrap 1, insets 10", "[grow]"));
            Style.setTransparent(this);
        }

        @Override
        public void compose() {
            headerContainer.add(firstHeader, "growx");
            this.add(titleLabel, "wrap");
            this.add(scrollPane, "grow, pushy");
        }

        @Override
        public void updateText() {
            Style.setLabelText(titleLabel, LangManager.text("requestPanel.headersPanel.titleLabel.text"));
            firstHeader.updateText();
        }
    }

    public class AuthPanel extends JPanel implements Struct, Translatable {
        private final JTabbedPane tabbedPane = new JTabbedPane();

        private final NonePanel nonePanel = new NonePanel(
                LangManager.text("requestPanel.authPanel.nonePanel.message.text"));
        @Getter
        private final AuthBasicPanel authBasicPanel = new AuthBasicPanel();
        @Getter
        private final AuthBearerPanel authBearerPanel = new AuthBearerPanel();
        @Getter
        private final AuthOAuth1Panel authOAuth1Panel = new AuthOAuth1Panel();

        private AuthPanel() {
            this.build();
            LangManager.register(this);
        }

        @Override
        public void initComponents() {
        }

        @Override
        public void configureStyle() {
            Style.setTransparent(this);
            this.setLayout(new MigLayout("fill, wrap 1, insets 4 10 4 10", "[grow]"));
            Style.setTabArc(tabbedPane);
            tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
            Style.setTabArc(tabbedPane);
        }

        @Override
        public void compose() {
            tabbedPane.addTab(LangManager.text("requestPanel.authPanel.tabbedPane.tab1.title.text"), nonePanel);
            tabbedPane.addTab(LangManager.text("requestPanel.authPanel.tabbedPane.tab2.title.text"), authBasicPanel);
            tabbedPane.addTab(LangManager.text("requestPanel.authPanel.tabbedPane.tab3.title.text"), authBearerPanel);
            tabbedPane.addTab("OAuth 1.0", authOAuth1Panel);
            this.add(tabbedPane, "grow");
        }

        @Override
        public void updateText() {
            nonePanel.setTitle(LangManager.text("requestPanel.authPanel.nonePanel.message.text"));
            tabbedPane.setTitleAt(0, LangManager.text("requestPanel.authPanel.tabbedPane.tab1.title.text"));
            tabbedPane.setTitleAt(1, LangManager.text("requestPanel.authPanel.tabbedPane.tab2.title.text"));
            tabbedPane.setTitleAt(2, LangManager.text("requestPanel.authPanel.tabbedPane.tab3.title.text"));
        }

        public Auth getAuth() {
            return switch (tabbedPane.getSelectedIndex()) {
                case 0 -> null;
                case 1 -> {
                    if (authBasicPanel.isValidForm()) {
                        yield authBasicPanel.getAuth();
                    }
                    yield null;
                }
                case 2 -> {
                    if (authBearerPanel.isValidForm()) {
                        yield authBearerPanel.getAuth();
                    }
                    yield null;
                }
                case 3 -> {
                    if (authOAuth1Panel.isValidForm()) {
                        yield authOAuth1Panel.getAuth();
                    }
                    yield null;
                }
                default -> null;
            };
        }

        public class AuthBasicPanel extends JPanel implements Struct, Translatable {
            private final JLabel titleLabel = new JLabel(
                    LangManager.text("requestPanel.authPanel.authBasicPanel.titleLabel.text"));
            private JPanel containerUsername;
            private final JLabel usernameLabel = new JLabel(
                    LangManager.text("requestPanel.authPanel.authBasicPanel.containerUsername.label.text"));
            @Getter
            private final TextField usernameField = new TextField();
            private JPanel containerPassword;
            private final JLabel passwordLabel = new JLabel(
                    LangManager.text("requestPanel.authPanel.authBasicPanel.containerPassword.label.text"));
            @Getter
            private final PasswordField passwordField = new PasswordField();

            private AuthBasicPanel() {
                this.build();
                LangManager.register(this);
            }

            @Override
            public void initComponents() {
                containerUsername = new JPanel(new MigLayout("fillx, wrap 1"));
                containerPassword = new JPanel(new MigLayout("fillx, wrap 1"));
            }

            @Override
            public void configureStyle() {
                Style.setTransparent(this);
                this.setLayout(new MigLayout("fillx, wrap 1, insets 10", "[grow]"));
                Style.setTransparent(containerUsername);
                usernameField.setBackgroundColor(Colors.TEXT_FIELD_COLOR);
                Style.setTransparent(containerPassword);
                passwordField.setBackgroundColor(Colors.TEXT_FIELD_COLOR);
            }

            @Override
            public void compose() {
                this.add(titleLabel, "wrap");

                containerUsername.add(usernameLabel, "wrap");
                containerUsername.add(usernameField, "h 30!, growx");
                this.add(containerUsername, "growx");

                containerPassword.add(passwordLabel, "wrap");
                containerPassword.add(passwordField, "h 30!, growx");
                this.add(containerPassword, "growx");
            }

            @Override
            public void updateText() {
                Style.setLabelText(titleLabel,
                        LangManager.text("requestPanel.authPanel.authBasicPanel.titleLabel.text"));
                Style.setLabelText(usernameLabel,
                        LangManager.text("requestPanel.authPanel.authBasicPanel.containerUsername.label.text"));
                Style.setLabelText(passwordLabel,
                        LangManager.text("requestPanel.authPanel.authBasicPanel.containerPassword.label.text"));
            }

            private boolean isValidForm() {
                return !usernameField.getText().isBlank() &&
                        !(new String(passwordField.getPassword()).isBlank());
            }

            private Auth getAuth() {
                return new BasicAuth(
                        usernameField.getText().trim(),
                        new String(passwordField.getPassword()));
            }

        }

        public class AuthBearerPanel extends JPanel implements Struct, Translatable {
            private final JLabel titleLabel = new JLabel(
                    LangManager.text("requestPanel.authPanel.authBearerPanel.titleLabel.text"));
            @Getter
            private final PlainTextEditor tokenField = new PlainTextEditor();
            private JPanel containerPrefix;
            private final TextField prefixField = new TextField();

            private AuthBearerPanel() {
                this.build();
                LangManager.register(this);
            }

            @Override
            public void initComponents() {
                containerPrefix = new JPanel(new MigLayout("fillx", "0[]10[grow]0"));
                prefixField.setText("Bearer");
            }

            @Override
            public void configureStyle() {
                Style.setTransparent(this);
                this.setLayout(new MigLayout("fillx, wrap 1, insets 10", "[grow]"));
                Style.setTransparent(containerPrefix);
            }

            @Override
            public void compose() {
                this.add(titleLabel, "wrap");
                this.add(tokenField, "h 60!, growx, wrap");
                containerPrefix.add(new JLabel("Token Prefix"), "right");
                containerPrefix.add(prefixField, "h 25!, growx");
                this.add(containerPrefix, "growx");
            }

            @Override
            public void updateText() {
                Style.setLabelText(titleLabel,
                        LangManager.text("requestPanel.authPanel.authBearerPanel.titleLabel.text"));
            }

            private boolean isValidForm() {
                String token = tokenField.getText();
                return token != null && !token.isBlank();
            }

            public Auth getAuth() {
                String prefix = prefixField.getText().isEmpty() ? "Bearer" : prefixField.getText().trim();
                return new BearerTokenAuth(prefix + " " + tokenField.getText());
            }

        }

        public class AuthOAuth1Panel extends JPanel implements Struct, Translatable {

            private final JPanel contentPanel = new JPanel();
            private final JScrollPane scrollPane = new JScrollPane(contentPanel);

            private final JLabel descriptionLabel = new JLabel(
                    "The authorization data will be automatically generated when you send the request.");

            private final JLabel dataToLabel = new JLabel("Add auth data to");
            private final JComboBox<String> dataToComboBox = new JComboBox<>(new String[] {
                    "Auto",
                    "Request URL",
                    "Request Body",
                    "Request Headers"
            });

            private final JLabel signatureMethodLabel = new JLabel("Signature Method");
            private final JComboBox<String> signatureMethodComboBox = new JComboBox<>(
                    Arrays.stream(SignatureMethod.values())
                            .map(Enum::name)
                            .toArray(String[]::new));

            private final JLabel consumerKeyLabel = new JLabel("Consumer Key");
            private final PasswordField consumerKeyField = new PasswordField();

            private final JLabel consumerSecretLabel = new JLabel("Consumer Secret");
            private final PasswordField consumerSecretField = new PasswordField();

            private final JLabel accessTokenLabel = new JLabel("Access Token");
            private final PasswordField accessTokenField = new PasswordField();

            private final JLabel tokenSecretLabel = new JLabel("Token Secret");
            private final PasswordField tokenSecretField = new PasswordField();

            private final JLabel callbackUrlLabel = new JLabel("Callback URL");
            private final TextField callbackUrlField = new TextField();

            private final JLabel varifierLabel = new JLabel("Verifier");
            private final PasswordField varifierField = new PasswordField();

            private final JLabel timestampLabel = new JLabel("Timestamp");
            private final TextField timestampField = new TextField();

            private final JLabel nonceLabel = new JLabel("Nonce");
            private final TextField nonceField = new TextField();

            private final JLabel versionLabel = new JLabel("Version");
            private final TextField versionField = new TextField();

            private final JLabel realmLabel = new JLabel("Realm");
            private final TextField realmField = new TextField();

            public AuthOAuth1Panel() {
                this.build();
            }

            @Override
            public void initComponents() {
                versionField.setText("1.0");

                realmField.setPlaceholder("realm@exaple.com");
            }

            @Override
            public void configureStyle() {
                this.setLayout(new MigLayout("fill, insets 0", "[grow]", "[grow]"));

                contentPanel.setLayout(
                        new MigLayout(
                                "fillx, wrap 2, insets 10",
                                "[grow 0][grow, fill]"));

                scrollPane.setBorder(null);

            }

            @Override
            public void compose() {

                contentPanel.add(descriptionLabel, "span 2, growx, gapbottom 15");

                contentPanel.add(dataToLabel);
                contentPanel.add(dataToComboBox, "h 30!, gapleft 50, growx");

                contentPanel.add(new JSeparator(), "span 2, growx, gaptop 10, gapbottom 10");

                contentPanel.add(signatureMethodLabel);
                contentPanel.add(signatureMethodComboBox, "h 30!, gapleft 50, growx");

                contentPanel.add(consumerKeyLabel);
                contentPanel.add(consumerKeyField, "h 30!, gapleft 50, growx");

                contentPanel.add(consumerSecretLabel);
                contentPanel.add(consumerSecretField, "h 30!, gapleft 50, growx");

                contentPanel.add(accessTokenLabel);
                contentPanel.add(accessTokenField, "h 30!, gapleft 50, growx");

                contentPanel.add(tokenSecretLabel);
                contentPanel.add(tokenSecretField, "h 30!, gapleft 50, growx");

                contentPanel.add(new JSeparator(), "h 0, span 2, growx, gaptop 15");

                // Advanced configuration
                contentPanel.add(new JLabel("> Advanced configuration"), "span 2");

                contentPanel.add(callbackUrlLabel);
                contentPanel.add(callbackUrlField, "h 30!, gapleft 50, growx");

                contentPanel.add(varifierLabel);
                contentPanel.add(varifierField, "h 30!, gapleft 50, growx");

                contentPanel.add(timestampLabel);
                contentPanel.add(timestampField, "h 30!, gapleft 50, growx");

                contentPanel.add(nonceLabel);
                contentPanel.add(nonceField, "h 30!, gapleft 50, growx");

                contentPanel.add(versionLabel);
                contentPanel.add(versionField, "h 30!, gapleft 50, growx");

                contentPanel.add(realmLabel);
                contentPanel.add(realmField, "h 30!, gapleft 50, growx");

                contentPanel.add(new JSeparator(), "span 2, growx, gaptop 10");

                // Checkboxes
                contentPanel.add(new JCheckBox("Include body hash"), "span 2");
                contentPanel.add(new JCheckBox("Add empty parameters to signature"), "span 2");

                this.add(scrollPane, "grow");
            }

            @Override
            public void updateText() {
            }

            private boolean isValidForm() {
                if (dataToComboBox.getSelectedIndex() < 0)
                    return false;
                if (signatureMethodComboBox.getSelectedIndex() < 0)
                    return false;

                if (consumerKeyField.getPassword().isEmpty())
                    return false;
                if (consumerSecretField.getPassword().isEmpty())
                    return false;

                return true;
            }

            public Auth getAuth() {
                String consumerKey = consumerKeyField.getPassword();
                String consumerSecret = consumerSecretField.getPassword();
                String accessToken = accessTokenField.getPassword();
                String tokenSecret = tokenSecretField.getPassword();
                String verifier = varifierField.getPassword();
                String realm = realmField.getText();

                return new OAuth1Auth(
                        dataToComboBox.getSelectedIndex(),
                        signatureMethodComboBox.getSelectedIndex(),
                        consumerKey,
                        consumerSecret,
                        accessToken.isEmpty() ? null : accessToken,
                        tokenSecret.isEmpty() ? null : tokenSecret,
                        callbackUrlField.getText().isEmpty() ? null : callbackUrlField.getText(),
                        verifier.isEmpty() ? null : verifier,
                        timestampField.getText().isEmpty() ? null : timestampField.getText(),
                        nonceField.getText().isEmpty() ? null : nonceField.getText(),
                        versionField.getText().isEmpty() ? null : versionField.getText(),
                        realm.isEmpty() ? null : realm, // realm como null si está vacío
                        false,
                        false);
            }

        }

    } // end authpanel

    public class BodyPanel extends JPanel implements Struct, Translatable {
        @Getter
        private final JTabbedPane tabbedPane = new JTabbedPane();
        private final NonePanel nonePanel = new NonePanel(
                LangManager.text("requestPanel.bodyPanel.nonePanel.message.text"));
        @Getter
        private final BodyJsonPanel bodyJsonPanel = new BodyJsonPanel();
        @Getter
        private final BodyXmlPanel bodyXmlPanel = new BodyXmlPanel();
        @Getter
        private final BodyTextPanel bodyTextPanel = new BodyTextPanel();
        @Getter
        private final BodyFormEncodePanel bodyFormEncodePanel = new BodyFormEncodePanel();
        @Getter
        private final BodyFormDataPanel bodyFormDataPanel = new BodyFormDataPanel();
        @Getter
        private final BodyBinaryPanel bodyBinaryPanel = new BodyBinaryPanel();

        private BodyPanel() {
            this.build();
            LangManager.register(this);
        }

        @Override
        public void initComponents() {
        }

        @Override
        public void configureStyle() {
            Style.setTransparent(this);
            Style.setLayout(this, new MigLayout("fill, insets 4 10 4 10", "[grow]"));
            tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
            Style.setTabArc(tabbedPane);
        }

        @Override
        public void compose() {
            tabbedPane.addTab(LangManager.text("requestPanel.bodyPanel.tabbedPane.tab1.title.text"), nonePanel);
            tabbedPane.addTab(LangManager.text("requestPanel.bodyPanel.tabbedPane.tab2.title.text"), bodyJsonPanel);
            tabbedPane.addTab(LangManager.text("requestPanel.bodyPanel.tabbedPane.tab3.title.text"), bodyXmlPanel);
            tabbedPane.addTab(LangManager.text("requestPanel.bodyPanel.tabbedPane.tab4.title.text"), bodyTextPanel);
            tabbedPane.addTab(LangManager.text("requestPanel.bodyPanel.tabbedPane.tab5.title.text"),
                    bodyFormEncodePanel);
            tabbedPane.addTab(LangManager.text("requestPanel.bodyPanel.tabbedPane.tab6.title.text"), bodyFormDataPanel);
            tabbedPane.addTab(LangManager.text("requestPanel.bodyPanel.tabbedPane.tab7.title.text"), bodyBinaryPanel); // application/octet-stream
            this.add(tabbedPane, "grow");
        }

        @Override
        public void updateText() {
            nonePanel.setTitle(LangManager.text("requestPanel.bodyPanel.nonePanel.message.text"));
            tabbedPane.setTitleAt(0, LangManager.text("requestPanel.bodyPanel.tabbedPane.tab1.title.text"));
            tabbedPane.setTitleAt(1, LangManager.text("requestPanel.bodyPanel.tabbedPane.tab2.title.text"));
            tabbedPane.setTitleAt(2, LangManager.text("requestPanel.bodyPanel.tabbedPane.tab3.title.text"));
            tabbedPane.setTitleAt(3, LangManager.text("requestPanel.bodyPanel.tabbedPane.tab4.title.text"));
            tabbedPane.setTitleAt(4, LangManager.text("requestPanel.bodyPanel.tabbedPane.tab5.title.text"));
            tabbedPane.setTitleAt(5, LangManager.text("requestPanel.bodyPanel.tabbedPane.tab6.title.text"));
            tabbedPane.setTitleAt(6, LangManager.text("requestPanel.bodyPanel.tabbedPane.tab7.title.text"));
        }

        public Body getContent() {
            return switch (tabbedPane.getSelectedIndex()) {
                case 0 -> null;
                case 1 -> {
                    if (bodyJsonPanel.isValidForm()) {
                        yield BodyMapper.json(bodyJsonPanel.getContent());
                    }
                    yield null;
                }
                case 2 -> {
                    if (bodyXmlPanel.isValidForm()) {
                        yield BodyMapper.xml(bodyXmlPanel.getContent());
                    }
                    yield null;
                }
                case 3 -> {
                    if (bodyTextPanel.isValidForm()) {
                        yield BodyMapper.plain(bodyTextPanel.getContent());
                    }
                    yield null;
                }
                case 4 -> {
                    yield BodyMapper.formEncode(bodyFormEncodePanel.getContent());
                }
                case 5 -> {
                    yield BodyMapper.formData(bodyFormDataPanel.getContent());
                }
                case 6 -> {
                    yield BodyMapper.binary(bodyBinaryPanel.getContent());
                }
                default -> null;
            };
        }

        public class BodyJsonPanel extends JPanel implements Struct {
            private final JPanel container = new JPanel();
            private final JLabel title = new JLabel("JSON (application/json)");
            @Getter
            private final JToggleButton lineWrapButton = new JToggleButton(UiUtils.LINE_WRAP_ICON);
            @Getter
            private final SyntaxEditor jsonEditor = new SyntaxEditor(
                    SyntaxConstants.SYNTAX_STYLE_JSON_WITH_COMMENTS);

            private BodyJsonPanel() {
                this.build();
            }

            @Override
            public void initComponents() {
            }

            @Override
            public void configureStyle() {
                Style.setTransparent(this);
                Style.setLayout(this, new MigLayout("fill, insets 10", "[grow]", "[]10[grow]"));
                Style.setLayout(container, new MigLayout("fill, aligny 50%, insets 0", "[]push[]"));
                Style.setCursor(lineWrapButton, Cursor.HAND_CURSOR);
                Style.setBackgroundColor(lineWrapButton, Colors.SECONDARY_COLOR);
                Style.setUndecoratedButton(lineWrapButton);
                Style.setToolTip(lineWrapButton, LangManager.text("app.panel.lineWrapButton.deactivate.toolTip.text"));
                jsonEditor.setEditable(true);
            }

            @Override
            public void compose() {
                container.add(title);
                container.add(lineWrapButton, "w 25!, h 25!");
                this.add(container, "wrap, growx");
                this.add(jsonEditor, "grow");
            }

            private boolean isValidForm() {
                return !jsonEditor.getText().isBlank();
            }

            private String getContent() {
                return jsonEditor.getText().trim();
            }
        }

        public class BodyXmlPanel extends JPanel implements Struct {
            private final JPanel container = new JPanel();
            private final JLabel title = new JLabel("XML (application/xml)");
            @Getter
            private final JToggleButton lineWrapButton = new JToggleButton(UiUtils.LINE_WRAP_ICON);
            @Getter
            private final SyntaxEditor xmlEditor = new SyntaxEditor(SyntaxConstants.SYNTAX_STYLE_XML);

            public BodyXmlPanel() {
                this.build();
            }

            @Override
            public void initComponents() {
            }

            @Override
            public void configureStyle() {
                Style.setTransparent(this);
                Style.setLayout(this, new MigLayout("fill, insets 10", "[grow]", "[]10[grow]"));
                Style.setLayout(container, new MigLayout("fill, aligny 50%, insets 0", "[]push[]"));
                Style.setCursor(lineWrapButton, Cursor.HAND_CURSOR);
                Style.setBackgroundColor(lineWrapButton, Colors.SECONDARY_COLOR);
                Style.setUndecoratedButton(lineWrapButton);
                Style.setToolTip(lineWrapButton, LangManager.text("app.panel.lineWrapButton.deactivate.toolTip.text"));
                xmlEditor.setEditable(true);
            }

            @Override
            public void compose() {
                container.add(title);
                container.add(lineWrapButton, "w 25!, h 25!");
                this.add(container, "wrap, growx");
                this.add(xmlEditor, "grow");
            }

            private boolean isValidForm() {
                return !xmlEditor.getText().isBlank();
            }

            private String getContent() {
                return xmlEditor.getText();
            }

        }

        public class BodyTextPanel extends JPanel implements Struct, Translatable {
            private final JLabel titleLabel = new JLabel(
                    LangManager.text("requestPanel.bodyPanel.bodyTextPanel.titleLabel.text"));
            @Getter
            private final PlainTextEditor textEditor = new PlainTextEditor();

            public BodyTextPanel() {
                this.build();
                LangManager.register(this);
            }

            @Override
            public void initComponents() {
            }

            @Override
            public void configureStyle() {
                Style.setTransparent(this);
                Style.setLayout(this, new MigLayout("fill, insets 10", "[grow]", "[]10[grow]"));
            }

            @Override
            public void compose() {
                this.add(titleLabel, "wrap");
                this.add(textEditor, "grow");
            }

            @Override
            public void updateText() {
                Style.setLabelText(titleLabel,
                        LangManager.text("requestPanel.bodyPanel.bodyTextPanel.titleLabel.text"));
            }

            private boolean isValidForm() {
                return !textEditor.getText().isBlank();
            }

            private String getContent() {
                return textEditor.getText().trim();
            }

        }

        public class BodyFormEncodePanel extends JPanel implements Struct, Translatable {
            private final JLabel titleLabel = new JLabel(
                    LangManager.text("requestPanel.bodyPanel.bodyFormEncodePanel.titleLabel.text"));
            private JScrollPane scrollPane;
            @Getter
            private final JPanel formContainer = new JPanel(new MigLayout("fillx, wrap 1", "[grow]"));
            @Getter
            private final List<KeyValueRow> formEncodeList = new ArrayList<>();
            @Getter
            private KeyValueRow firstFormEncode;

            private BodyFormEncodePanel() {
                this.build();
                LangManager.register(this);
            }

            @Override
            public void initComponents() {
                firstFormEncode = new KeyValueRow(true);
                scrollPane = new JScrollPane(formContainer,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            }

            @Override
            public void configureStyle() {
                Style.setLayout(this, Layout.tabContent());
                Style.setTransparent(this);
            }

            @Override
            public void compose() {
                formContainer.add(firstFormEncode, "growx");
                this.add(titleLabel, "wrap");
                this.add(scrollPane, "grow, pushy");
            }

            @Override
            public void updateText() {
                Style.setLabelText(titleLabel,
                        LangManager.text("requestPanel.bodyPanel.bodyFormEncodePanel.titleLabel.text"));
                firstFormEncode.updateText();
            }

            public List<FormEncodePart> getContent() {
                return formEncodeList.stream()
                        .filter(row -> row.getCheck().isSelected())
                        .map(row -> new FormEncodePart(
                                row.getKeyField().getText(),
                                row.getValueField().getText()))
                        .toList();
            }

        }

        public class BodyFormDataPanel extends JPanel implements Struct, Translatable {
            private final JLabel titleLabel = new JLabel(
                    LangManager.text("requestPanel.bodyPanel.bodyFormDataPanel.titleLabel.text"));
            private JScrollPane scrollPane;
            @Getter
            private final JPanel formDataContainer = new JPanel(new MigLayout("fillx, wrap 1", "[grow]"));
            @Getter
            private final List<KeyValueFileRow> formDataList = new ArrayList<>();
            @Getter
            private KeyValueFileRow firstFormData;

            private BodyFormDataPanel() {
                this.build();
                LangManager.register(this);
            }

            @Override
            public void initComponents() {
                firstFormData = new KeyValueFileRow(true);
                scrollPane = new JScrollPane(formDataContainer,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            }

            @Override
            public void configureStyle() {
                Style.setLayout(this, Layout.tabContent());
                Style.setTransparent(this);
            }

            @Override
            public void compose() {
                formDataContainer.add(firstFormData, "growx");
                this.add(titleLabel, "wrap");
                this.add(scrollPane, "grow, pushy");
            }

            @Override
            public void updateText() {
                Style.setLabelText(titleLabel,
                        LangManager.text("requestPanel.bodyPanel.bodyFormDataPanel.titleLabel.text"));
                firstFormData.updateText();
            }

            public List<FormDataPart> getContent() {
                return formDataList.stream()
                        .filter(row -> row.getCheck().isSelected())
                        .map(row -> {
                            if (row.isTypeText()) {
                                return new FormDataPart(
                                        row.getKeyField().getText(),
                                        FormDataPart.PartType.TEXT,
                                        row.getValueField().getText());
                            } else {
                                return new FormDataPart(
                                        row.getKeyField().getText(),
                                        FormDataPart.PartType.FILE,
                                        row.getFilePath());
                            }
                        })
                        .toList();
            }

        }

        public class BodyBinaryPanel extends JPanel implements Struct, Translatable {
            private JPanel container;
            @Getter
            private final JButton selectFileButton = new JButton(
                    LangManager.text("requestPanel.bodyPanel.bodyBinaryPanel.selectFileButton.text"));
            @Getter
            private final JLabel fileNameLabel = new JLabel(
                    LangManager.text("requestPanel.bodyPanel.bodyBinaryPanel.fileNameLabel.text"));
            @Setter
            private String filePath = null;

            public BodyBinaryPanel() {
                this.build();
                LangManager.register(this);
            }

            @Override
            public void initComponents() {
                container = new JPanel(new MigLayout("fill, insets 0, aligny 50%", "[]10[]"));
            }

            @Override
            public void configureStyle() {
                Style.setLayout(this, Layout.tabContent());
                Style.setCursor(selectFileButton, Cursor.HAND_CURSOR);
                Style.setBackgroundColor(selectFileButton, Colors.SECONDARY_COLOR);
                Style.setTextColor(selectFileButton, Colors.ICON_COLOR);
                Style.setUndecoratedButton(selectFileButton);
                Style.setFontSize(fileNameLabel, 13.5f);
            }

            @Override
            public void compose() {
                container.add(selectFileButton, "h 30!");
                container.add(fileNameLabel, "growx");
                this.add(container, "wrap");
            }

            @Override
            public void updateText() {
                Style.setButtonText(
                        selectFileButton,
                        LangManager.text("requestPanel.bodyPanel.bodyBinaryPanel.selectFileButton.text"));
                Style.setLabelText(
                        fileNameLabel,
                        LangManager.text("requestPanel.bodyPanel.bodyBinaryPanel.fileNameLabel.text"));
            }

            public String getContent() {
                return filePath;
            }

        }
    } // end body panel

}
