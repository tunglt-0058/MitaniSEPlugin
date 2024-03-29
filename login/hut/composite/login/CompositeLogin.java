package hut.composite.login;

import java.util.ArrayList;
import java.util.List;

import mintani.valueconst.ConsistentOntology;
import mitani.activator.Activator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import service.Service;
import ws.owl.ArrayListData;
import ws.owl.ClassData;
import ws.owl.Message;
import ws.owl.WebServiceDelegate;

public class CompositeLogin extends Composite {

	private Text txtServer;
	private Combo cmbProjectTeam;
	private Combo cmbType;
	private Text txtPassCreat;
	private Text txtNameCreat;
	private Text txtPassNewChange;
	private Text txtPassCurChange;
	private Text txtNameChange;
	private Text txtNameRemove;
	private Text txtPassLogin;
	private Text txtNameLogin;

	/**
	 * Luu uri tuong ung cua nguoi login
	 */
	private String uriUser;

	private final Link btnLogout;
	private Button btnLogin;
	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());

	private final String SERVER = "SERVER";
	private final String SERVER_URI = "http://localhost:8080/";

	/**
	 * Create the composite
	 * @param parent
	 * @param style
	 */
	public CompositeLogin(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout());
		toolkit.adapt(this);
		toolkit.paintBordersFor(this);

		final ExpandBar expandBar = new ExpandBar(this, SWT.NONE);
		toolkit.adapt(expandBar, true, true);

		final ExpandItem newItemExpandItem = new ExpandItem(expandBar, SWT.NONE);
		newItemExpandItem.setExpanded(true);
		newItemExpandItem.setText("User's Tasks");

		final Composite composite = new Composite(expandBar, SWT.NONE);
		composite.setLayout(new FormLayout());
		toolkit.adapt(composite);
		newItemExpandItem.setHeight(305);
		newItemExpandItem.setControl(composite);

		final Group loginGroup = new Group(composite, SWT.NONE);
		loginGroup.setLayout(new FormLayout());
		final FormData fd_loginGroup = new FormData();
		fd_loginGroup.right = new FormAttachment(100, -50);
		fd_loginGroup.bottom = new FormAttachment(0, 150);
		fd_loginGroup.top = new FormAttachment(0, 0);
		fd_loginGroup.left = new FormAttachment(0, 50);
		loginGroup.setLayoutData(fd_loginGroup);
		loginGroup.setText("Login");
		toolkit.adapt(loginGroup);

		txtNameLogin = new Text(loginGroup, SWT.BORDER);
		final FormData fd_txtUserLogin = new FormData();
		fd_txtUserLogin.right = new FormAttachment(100, -10);
		fd_txtUserLogin.bottom = new FormAttachment(0, 60);
		fd_txtUserLogin.top = new FormAttachment(0, 40);
		fd_txtUserLogin.left = new FormAttachment(0, 100);
		txtNameLogin.setLayoutData(fd_txtUserLogin);
		toolkit.adapt(txtNameLogin, true, true);

		final Label accountLabel = new Label(loginGroup, SWT.NONE);
		final FormData fd_accountLabel = new FormData();
		fd_accountLabel.right = new FormAttachment(0, 100);
		fd_accountLabel.bottom = new FormAttachment(0, 60);
		fd_accountLabel.top = new FormAttachment(0, 40);
		fd_accountLabel.left = new FormAttachment(0, 10);
		accountLabel.setLayoutData(fd_accountLabel);
		toolkit.adapt(accountLabel, true, true);
		accountLabel.setText("User name");

		txtPassLogin = new Text(loginGroup, SWT.PASSWORD | SWT.BORDER);
		final FormData fd_txtPassLogin = new FormData();
		fd_txtPassLogin.right = new FormAttachment(100, -150);
		fd_txtPassLogin.bottom = new FormAttachment(0, 90);
		fd_txtPassLogin.top = new FormAttachment(0, 70);
		fd_txtPassLogin.left = new FormAttachment(0, 100);
		txtPassLogin.setLayoutData(fd_txtPassLogin);
		toolkit.adapt(txtPassLogin, true, true);

		final Label passwordLabel = new Label(loginGroup, SWT.NONE);
		final FormData fd_passwordLabel = new FormData();
		fd_passwordLabel.right = new FormAttachment(0, 100);
		fd_passwordLabel.bottom = new FormAttachment(0, 90);
		fd_passwordLabel.top = new FormAttachment(0, 70);
		fd_passwordLabel.left = new FormAttachment(0, 10);
		passwordLabel.setLayoutData(fd_passwordLabel);
		toolkit.adapt(passwordLabel, true, true);
		passwordLabel.setText("Password");

		btnLogin = new Button(loginGroup, SWT.NONE);
		btnLogin.addMouseListener(new MouseAdapter() {
			public void mouseDown(final MouseEvent e) {
				btnLogin.setEnabled(false);//Tranh bam nhieu lan

				String wsdlLocation = txtServer.getText();
				WebServiceDelegate delegate = Service.createWebServiceDelegate(wsdlLocation);
				if (delegate == null)
				{
					MessageDialog.openError(btnLogin.getShell(), "Error", "Can not find SERVER or SERVER has not deployed SERVICES!");
					return;
				}
				Message message = delegate.validateUser(txtNameLogin.getText(), txtPassLogin.getText());

				if (message.isSuccess())
				{
					// message.getMessage() chinh la uri
					uriUser = message.getMessage();
					Service.logOn(txtNameLogin.getText(), txtPassLogin.getText(), uriUser, wsdlLocation);
					MessageDialog.openInformation(btnLogin.getShell(), "Message", "Login successfully!");
					addDataToComboProjectTeam(uriUser);

					addDataToComboUserType();

					txtNameLogin.setEnabled(false);
					txtPassLogin.setEnabled(false);
					btnLogout.setEnabled(true);
					btnLogin.setEnabled(false);
				}
				else
				{
					uriUser = "";

					btnLogout.setEnabled(false);
					Service.logOut();
					MessageDialog.openError(btnLogin.getShell(), "Message", "Login fail!");
					cmbProjectTeam.removeAll();

					btnLogin.setEnabled(true);//Chi khi sai moi cho login lai, con khong disable, de logout active
				}
				txtNameLogin.setText("");
				txtPassLogin.setText("");
			}
		});
		final FormData fd_btnLogin = new FormData();
		fd_btnLogin.bottom = new FormAttachment(0, 90);
		fd_btnLogin.top = new FormAttachment(0, 70);
		fd_btnLogin.right = new FormAttachment(100, -80);
		fd_btnLogin.left = new FormAttachment(100, -140);
		btnLogin.setLayoutData(fd_btnLogin);
		toolkit.adapt(btnLogin, true, true);
		btnLogin.setText("Login");

		btnLogout = new Link(loginGroup, SWT.NONE);
		btnLogout.addMouseListener(new MouseAdapter() {
			public void mouseDown(final MouseEvent e) {
				btnLogout.setEnabled(false);
				btnLogin.setEnabled(true);
				txtNameLogin.setEnabled(true);
				txtPassLogin.setEnabled(true);


				Service.logOut();
				MessageDialog.openInformation(btnLogin.getShell(), "Message", "Signout Successfully!");
				cmbProjectTeam.removeAll();
			}
		});
		btnLogout.setEnabled(false);
		final FormData fd_btnLogout = new FormData();
		fd_btnLogout.bottom = new FormAttachment(0, 90);
		fd_btnLogout.top = new FormAttachment(0, 70);
		fd_btnLogout.right = new FormAttachment(100, -10);
		fd_btnLogout.left = new FormAttachment(100, -70);
		btnLogout.setLayoutData(fd_btnLogout);
		toolkit.adapt(btnLogout, true, true);
		btnLogout.setText("<a href=\"http://www.eclipse.org\">Log out</a>");

		cmbProjectTeam = new Combo(loginGroup, SWT.NONE);
		cmbProjectTeam.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent arg0) {
				Service.uriProjectTeam = (String) cmbProjectTeam.getData(cmbProjectTeam.getText());
				System.out.println(Service.uriProjectTeam);
			}
		});
		final FormData fd_combo = new FormData();
		fd_combo.right = new FormAttachment(100, -80);
		fd_combo.left = new FormAttachment(0, 100);
		fd_combo.bottom = new FormAttachment(0, 120);
		fd_combo.top = new FormAttachment(0, 100);
		cmbProjectTeam.setLayoutData(fd_combo);
		toolkit.adapt(cmbProjectTeam, true, true);

		final Label projectTeamLabel = new Label(loginGroup, SWT.NONE);
		final FormData fd_projectTeamLabel = new FormData();
		fd_projectTeamLabel.right = new FormAttachment(0, 90);
		fd_projectTeamLabel.bottom = new FormAttachment(0, 120);
		fd_projectTeamLabel.top = new FormAttachment(0, 100);
		fd_projectTeamLabel.left = new FormAttachment(0, 10);
		projectTeamLabel.setLayoutData(fd_projectTeamLabel);
		toolkit.adapt(projectTeamLabel, true, true);
		projectTeamLabel.setText("Project Team");

		txtServer = new Text(loginGroup, SWT.BORDER);
		final FormData fd_txtServer = new FormData();
		fd_txtServer.left = new FormAttachment(0, 100);
		fd_txtServer.bottom = new FormAttachment(0, 30);
		fd_txtServer.right = new FormAttachment(100, -150);
		fd_txtServer.top = new FormAttachment(0, 10);
		txtServer.setLayoutData(fd_txtServer);
		toolkit.adapt(txtServer, true, true);

		final Label serverLabel = new Label(loginGroup, SWT.NONE);
		final FormData fd_serverLabel = new FormData();
		fd_serverLabel.bottom = new FormAttachment(0, 30);
		fd_serverLabel.left = new FormAttachment(0, 10);
		fd_serverLabel.right = new FormAttachment(0, 100);
		fd_serverLabel.top = new FormAttachment(0, 10);
		serverLabel.setLayoutData(fd_serverLabel);
		toolkit.adapt(serverLabel, true, true);
		serverLabel.setText("Server");

		final Button btnSave = new Button(loginGroup, SWT.NONE);
		btnSave.addMouseListener(new MouseAdapter() {
			public void mouseDown(final MouseEvent e) {
				String s = txtServer.getText();
				if (s!= "")
				{
					Activator.getDefault().getPreferenceStore().setValue(SERVER, s);
					MessageDialog.openInformation(btnLogin.getShell(), "Message", "Save server configure successfully!");
				}
			}
		});
		final FormData fd_btnSave = new FormData();
		fd_btnSave.bottom = new FormAttachment(0, 30);
		fd_btnSave.top = new FormAttachment(0, 10);
		fd_btnSave.left = new FormAttachment(100, -70);
		fd_btnSave.right = new FormAttachment(100, -10);
		btnSave.setLayoutData(fd_btnSave);
		toolkit.adapt(btnSave, true, true);
		btnSave.setText("Store URI");

		final Button btnTest = new Button(loginGroup, SWT.NONE);
		btnTest.addMouseListener(new MouseAdapter() {
			public void mouseDown(final MouseEvent e) {
				btnTest.setEnabled(false);

				String wsdlLocation = txtServer.getText();
				WebServiceDelegate delegate = Service.createWebServiceDelegate(wsdlLocation);
				if (delegate == null)
					MessageDialog.openError(btnLogin.getShell(), "Error", "Can not find SERVER or SERVER has not deployed SERVICES!");
				else
					MessageDialog.openInformation(btnLogin.getShell(), "Message", "Connect successfully to server!");

				btnTest.setEnabled(true);
			}
		});
		final FormData fd_btnTest = new FormData();
		fd_btnTest.bottom = new FormAttachment(0, 30);
		fd_btnTest.right = new FormAttachment(100, -80);
		fd_btnTest.top = new FormAttachment(0, 10);
		fd_btnTest.left = new FormAttachment(100, -140);
		btnTest.setLayoutData(fd_btnTest);
		toolkit.adapt(btnTest, true, true);
		btnTest.setText("Test");

		final Button btnRefresh = new Button(loginGroup, SWT.NONE);
		btnRefresh.addMouseListener(new MouseAdapter() {
			public void mouseDown(final MouseEvent e) {
				addDataToComboProjectTeam(uriUser);
			}
		});
		final FormData fd_btnRefresh = new FormData();
		fd_btnRefresh.left = new FormAttachment(100, -70);
		fd_btnRefresh.bottom = new FormAttachment(0, 120);
		fd_btnRefresh.right = new FormAttachment(100, -10);
		fd_btnRefresh.top = new FormAttachment(0, 100);
		btnRefresh.setLayoutData(fd_btnRefresh);
		toolkit.adapt(btnRefresh, true, true);
		btnRefresh.setText("Refresh");

		final Group changePasswordGroup = new Group(composite, SWT.NONE);
		changePasswordGroup.setLayout(new FormLayout());
		final FormData fd_changePasswordGroup = new FormData();
		fd_changePasswordGroup.right = new FormAttachment(100, -50);
		fd_changePasswordGroup.bottom = new FormAttachment(0, 300);
		fd_changePasswordGroup.top = new FormAttachment(0, 150);
		fd_changePasswordGroup.left = new FormAttachment(0, 50);
		changePasswordGroup.setLayoutData(fd_changePasswordGroup);
		changePasswordGroup.setText("Change Password");
		toolkit.adapt(changePasswordGroup);

		final Label userNameLabel_2 = new Label(changePasswordGroup, SWT.NONE);
		final FormData fd_userNameLabel_2 = new FormData();
		fd_userNameLabel_2.right = new FormAttachment(0, 100);
		fd_userNameLabel_2.bottom = new FormAttachment(0, 30);
		fd_userNameLabel_2.top = new FormAttachment(0, 10);
		fd_userNameLabel_2.left = new FormAttachment(0, 10);
		userNameLabel_2.setLayoutData(fd_userNameLabel_2);
		toolkit.adapt(userNameLabel_2, true, true);
		userNameLabel_2.setText("User name");

		txtNameChange = new Text(changePasswordGroup, SWT.BORDER);
		final FormData fd_txtNameChange = new FormData();
		fd_txtNameChange.right = new FormAttachment(100, -10);
		fd_txtNameChange.bottom = new FormAttachment(0, 30);
		fd_txtNameChange.top = new FormAttachment(0, 10);
		fd_txtNameChange.left = new FormAttachment(0, 100);
		txtNameChange.setLayoutData(fd_txtNameChange);
		toolkit.adapt(txtNameChange, true, true);

		final Label currentPasswordLabel = new Label(changePasswordGroup, SWT.NONE);
		final FormData fd_currentPasswordLabel = new FormData();
		fd_currentPasswordLabel.bottom = new FormAttachment(0, 60);
		fd_currentPasswordLabel.top = new FormAttachment(0, 40);
		fd_currentPasswordLabel.right = new FormAttachment(0, 100);
		fd_currentPasswordLabel.left = new FormAttachment(0, 10);
		currentPasswordLabel.setLayoutData(fd_currentPasswordLabel);
		toolkit.adapt(currentPasswordLabel, true, true);
		currentPasswordLabel.setText("Current PW");

		txtPassCurChange = new Text(changePasswordGroup, SWT.PASSWORD | SWT.BORDER);
		final FormData fd_txtPassCurChange = new FormData();
		fd_txtPassCurChange.right = new FormAttachment(100, -10);
		fd_txtPassCurChange.bottom = new FormAttachment(0, 60);
		fd_txtPassCurChange.top = new FormAttachment(0, 40);
		fd_txtPassCurChange.left = new FormAttachment(0, 100);
		txtPassCurChange.setLayoutData(fd_txtPassCurChange);
		toolkit.adapt(txtPassCurChange, true, true);

		txtPassNewChange = new Text(changePasswordGroup, SWT.PASSWORD | SWT.BORDER);
		final FormData fd_txtPassNewChange = new FormData();
		fd_txtPassNewChange.right = new FormAttachment(100, -10);
		fd_txtPassNewChange.bottom = new FormAttachment(0, 90);
		fd_txtPassNewChange.top = new FormAttachment(0, 70);
		fd_txtPassNewChange.left = new FormAttachment(0, 100);
		txtPassNewChange.setLayoutData(fd_txtPassNewChange);
		toolkit.adapt(txtPassNewChange, true, true);

		final Label label = new Label(changePasswordGroup, SWT.NONE);
		final FormData fd_label = new FormData();
		fd_label.bottom = new FormAttachment(0, 90);
		fd_label.top = new FormAttachment(0, 70);
		fd_label.right = new FormAttachment(0, 100);
		fd_label.left = new FormAttachment(0, 10);
		label.setLayoutData(fd_label);
		toolkit.adapt(label, true, true);
		label.setText("New PW");

		final Button button_2 = new Button(changePasswordGroup, SWT.NONE);
		button_2.addMouseListener(new MouseAdapter() {
			public void mouseDown(final MouseEvent e) {
				if (Service.isLogin)
				{
					Message message = Service.webServiceDelegate.changePassword(txtNameChange.getText(), txtPassCurChange.getText(), txtPassNewChange.getText());
					String s = message.getMessage();
					if (message.isSuccess())
						MessageDialog.openInformation(cmbType.getShell(), "Message", s);
					else
						MessageDialog.openError(cmbType.getShell(), "Message", s);
				}
				else
					MessageDialog.openError(cmbType.getShell(), "Message", "You do not login yet");
				txtPassCurChange.setText("");
				txtPassNewChange.setText("");
			}
		});
		final FormData fd_button_2 = new FormData();
		fd_button_2.bottom = new FormAttachment(0, 120);
		fd_button_2.top = new FormAttachment(0, 100);
		fd_button_2.right = new FormAttachment(0, 160);
		fd_button_2.left = new FormAttachment(0, 100);
		button_2.setLayoutData(fd_button_2);
		toolkit.adapt(button_2, true, true);
		button_2.setText("Submit");

		final Label nullLabel1 = new Label(composite, SWT.NONE);
		final FormData fd_nullLabel1 = new FormData();
		fd_nullLabel1.bottom = new FormAttachment(0, 282);
		fd_nullLabel1.top = new FormAttachment(0, 269);
		fd_nullLabel1.right = new FormAttachment(0, 25);
		fd_nullLabel1.left = new FormAttachment(0, 0);
		nullLabel1.setLayoutData(fd_nullLabel1);
		toolkit.adapt(nullLabel1, true, true);

		final ExpandItem newItemExpandItem_1 = new ExpandItem(expandBar, SWT.NONE);
		newItemExpandItem_1.setText("Admin's Tasks");

		final Composite composite_1 = new Composite(expandBar, SWT.NONE);
		composite_1.setLayout(new FormLayout());
		toolkit.adapt(composite_1);
		newItemExpandItem_1.setHeight(270);
		newItemExpandItem_1.setControl(composite_1);

		Label userNameLabel;

		Label accountsTypeLabel;

		final Group createNewAccountGroup = new Group(composite_1, SWT.NONE);
		createNewAccountGroup.setLayout(new FormLayout());
		final FormData fd_createNewAccountGroup = new FormData();
		fd_createNewAccountGroup.right = new FormAttachment(100, -50);
		fd_createNewAccountGroup.bottom = new FormAttachment(0, 150);
		fd_createNewAccountGroup.top = new FormAttachment(0, 0);
		fd_createNewAccountGroup.left = new FormAttachment(0, 50);
		createNewAccountGroup.setLayoutData(fd_createNewAccountGroup);
		createNewAccountGroup.setText("Create new Account");
		toolkit.adapt(createNewAccountGroup);

		userNameLabel = new Label(createNewAccountGroup, SWT.NONE);
		final FormData fd_userNameLabel = new FormData();
		fd_userNameLabel.bottom = new FormAttachment(0, 30);
		fd_userNameLabel.top = new FormAttachment(0, 10);
		fd_userNameLabel.right = new FormAttachment(0, 100);
		fd_userNameLabel.left = new FormAttachment(0, 10);
		userNameLabel.setLayoutData(fd_userNameLabel);
		toolkit.adapt(userNameLabel, true, true);
		userNameLabel.setText("User name");

		txtNameCreat = new Text(createNewAccountGroup, SWT.BORDER);
		final FormData fd_txtNameCreat = new FormData();
		fd_txtNameCreat.right = new FormAttachment(100, -10);
		fd_txtNameCreat.bottom = new FormAttachment(0, 30);
		fd_txtNameCreat.top = new FormAttachment(0, 10);
		fd_txtNameCreat.left = new FormAttachment(0, 100);
		txtNameCreat.setLayoutData(fd_txtNameCreat);
		toolkit.adapt(txtNameCreat, true, true);

		final Label passwordLabel_1 = new Label(createNewAccountGroup, SWT.NONE);
		final FormData fd_passwordLabel_1 = new FormData();
		fd_passwordLabel_1.bottom = new FormAttachment(0, 60);
		fd_passwordLabel_1.top = new FormAttachment(0, 40);
		fd_passwordLabel_1.right = new FormAttachment(0, 100);
		fd_passwordLabel_1.left = new FormAttachment(0, 10);
		passwordLabel_1.setLayoutData(fd_passwordLabel_1);
		toolkit.adapt(passwordLabel_1, true, true);
		passwordLabel_1.setText("Password");

		txtPassCreat = new Text(createNewAccountGroup, SWT.PASSWORD | SWT.BORDER);
		final FormData fd_txtPassCreat = new FormData();
		fd_txtPassCreat.right = new FormAttachment(100, -10);
		fd_txtPassCreat.bottom = new FormAttachment(0, 60);
		fd_txtPassCreat.top = new FormAttachment(0, 40);
		fd_txtPassCreat.left = new FormAttachment(0, 100);
		txtPassCreat.setLayoutData(fd_txtPassCreat);
		toolkit.adapt(txtPassCreat, true, true);

		accountsTypeLabel = new Label(createNewAccountGroup, SWT.NONE);
		final FormData fd_accountsTypeLabel = new FormData();
		fd_accountsTypeLabel.bottom = new FormAttachment(0, 90);
		fd_accountsTypeLabel.top = new FormAttachment(0, 70);
		fd_accountsTypeLabel.right = new FormAttachment(0, 100);
		fd_accountsTypeLabel.left = new FormAttachment(0, 10);
		accountsTypeLabel.setLayoutData(fd_accountsTypeLabel);
		toolkit.adapt(accountsTypeLabel, true, true);
		accountsTypeLabel.setText("Account's Type");

		cmbType = new Combo(createNewAccountGroup, SWT.NONE);
		final FormData fd_cmbType = new FormData();
		fd_cmbType.right = new FormAttachment(100, -10);
		fd_cmbType.bottom = new FormAttachment(0, 100);
		fd_cmbType.top = new FormAttachment(0, 70);
		fd_cmbType.left = new FormAttachment(0, 100);
		cmbType.setLayoutData(fd_cmbType);
		toolkit.adapt(cmbType, true, true);

		final Button creatButton = new Button(createNewAccountGroup, SWT.NONE);
		creatButton.addMouseListener(new MouseAdapter() {
			public void mouseDown(final MouseEvent e) {
				if (Service.isLogin && Service.isAdmin)
				{
					String uri = cmbType.getData(cmbType.getText())+"_"+txtNameCreat.getText();
					Message message = Service.webServiceDelegate.addUser(txtNameCreat.getText(), txtPassCreat.getText(), uri);
					String s = message.getMessage();
					if (message.isSuccess())
					{
						MessageDialog.openInformation(cmbType.getShell(), "Message", s);
						Service.webServiceDelegate.createInstance(null, uri, (String)cmbType.getData(cmbType.getText()));
					}
					else
						MessageDialog.openError(cmbType.getShell(), "Message", s);
				}
				else
					MessageDialog.openError(cmbType.getShell(), "Message", "You are not admin or do not login yet");
				txtNameCreat.setText("");
				txtPassCreat.setText("");
			}
		});
		final FormData fd_creatButton = new FormData();
		fd_creatButton.bottom = new FormAttachment(0, 120);
		fd_creatButton.top = new FormAttachment(0, 100);
		fd_creatButton.right = new FormAttachment(0, 160);
		fd_creatButton.left = new FormAttachment(0, 100);
		creatButton.setLayoutData(fd_creatButton);
		toolkit.adapt(creatButton, true, true);
		creatButton.setText("Create");

		final Group removeAccountGroup = new Group(composite_1, SWT.NONE);
		removeAccountGroup.setLayout(new FormLayout());
		final FormData fd_removeAccountGroup = new FormData();
		fd_removeAccountGroup.right = new FormAttachment(100, -50);
		fd_removeAccountGroup.bottom = new FormAttachment(0, 240);
		fd_removeAccountGroup.top = new FormAttachment(0, 150);
		fd_removeAccountGroup.left = new FormAttachment(0, 50);
		removeAccountGroup.setLayoutData(fd_removeAccountGroup);
		removeAccountGroup.setText("Remove Account");
		toolkit.adapt(removeAccountGroup);

		final Label userNameLabel_1 = new Label(removeAccountGroup, SWT.NONE);
		final FormData fd_userNameLabel_1 = new FormData();
		fd_userNameLabel_1.bottom = new FormAttachment(0, 30);
		fd_userNameLabel_1.top = new FormAttachment(0, 10);
		fd_userNameLabel_1.right = new FormAttachment(0, 100);
		fd_userNameLabel_1.left = new FormAttachment(0, 10);
		userNameLabel_1.setLayoutData(fd_userNameLabel_1);
		toolkit.adapt(userNameLabel_1, true, true);
		userNameLabel_1.setText("User name");

		txtNameRemove = new Text(removeAccountGroup, SWT.BORDER);
		final FormData fd_txtNameRemove = new FormData();
		fd_txtNameRemove.right = new FormAttachment(100, -10);
		fd_txtNameRemove.bottom = new FormAttachment(0, 30);
		fd_txtNameRemove.top = new FormAttachment(0, 10);
		fd_txtNameRemove.left = new FormAttachment(0, 100);
		txtNameRemove.setLayoutData(fd_txtNameRemove);
		toolkit.adapt(txtNameRemove, true, true);

		final Button removeAccountButton = new Button(removeAccountGroup, SWT.NONE);
		removeAccountButton.addMouseListener(new MouseAdapter() {
			public void mouseDown(final MouseEvent e) {
				if (Service.isLogin && Service.isAdmin)
				{
					Message message = Service.webServiceDelegate.removeUser(txtNameRemove.getText());
					String s = message.getMessage();
					if (message.isSuccess())
						MessageDialog.openInformation(cmbType.getShell(), "Message", s);
					else
						MessageDialog.openError(cmbType.getShell(), "Message", s);
				}
				else
					MessageDialog.openError(cmbType.getShell(), "Message", "You are not admin or do not login yet");
				txtNameRemove.setText("");
			}
		});
		final FormData fd_removeAccountButton = new FormData();
		fd_removeAccountButton.bottom = new FormAttachment(0, 60);
		fd_removeAccountButton.top = new FormAttachment(0, 40);
		fd_removeAccountButton.right = new FormAttachment(0, 160);
		fd_removeAccountButton.left = new FormAttachment(0, 100);
		removeAccountButton.setLayoutData(fd_removeAccountButton);
		toolkit.adapt(removeAccountButton, true, true);
		removeAccountButton.setText("Remove");

		final Label nullLabel2 = new Label(composite_1, SWT.NONE);
		final FormData fd_nullLabel2 = new FormData();
		fd_nullLabel2.bottom = new FormAttachment(0, 263);
		fd_nullLabel2.top = new FormAttachment(0, 250);
		fd_nullLabel2.right = new FormAttachment(0, 25);
		fd_nullLabel2.left = new FormAttachment(0, 0);
		nullLabel2.setLayoutData(fd_nullLabel2);
		toolkit.adapt(nullLabel2, true, true);

		initialize();
	}

	private void initialize()
	{
		/*
		 * Lay ra gia tri cua server, luu trong preference cua Elcipse
		 */
		String s = Activator.getDefault().getPreferenceStore().getString(SERVER);
		if (s==null || s== "")
			Activator.getDefault().getPreferenceStore().setValue(SERVER, SERVER_URI);
		s = Activator.getDefault().getPreferenceStore().getString(SERVER);
		txtServer.setText(s);

		checkLogin();
	}

	private void checkLogin()
	{
		if (Service.isLogin)
		{
			btnLogout.setEnabled(true);
			btnLogin.setEnabled(false);
		}
		else
		{
			btnLogout.setEnabled(false);
			btnLogin.setEnabled(true);
		}
	}

	private void addDataToComboUserType()
	{
		cmbType.removeAll();
		for (ClassData cd:Service.webServiceDelegate.getSubClasses(null, ConsistentOntology.PERSON, true))
		{
			cmbType.add(cd.getClassName());
			cmbType.setData(cd.getClassName(), cd.getClassURI());
		}
		cmbType.select(0);
	}

	public ArrayList<String> getProjectTeam(String uriUser)
	{
		ArrayList<String> result = new ArrayList<String>();
		String query =
			ConsistentOntology.prefix+
			"\nSELECT DISTINCT ?t"+
			"\n{"+
			"\n?t rdf:type <" + ConsistentOntology.PROJECT_TEAM + ">."+
			"\n<"+uriUser+"> <" + ConsistentOntology.MEMBER_OF + "> ?t."+
			"\n}";
		System.out.println(query);
		List<ArrayListData> ad = Service.webServiceDelegate.sparqlResultList(null, query);
		for (int i=1; i<ad.size(); i++)
		{
			String s = ad.get(i).getData().get(0);
			if (s != null)
				result.add(s);
		}
		return result;
	}

	private void addDataToComboProjectTeam(String uriUser)
	{
		cmbProjectTeam.removeAll();
		if (uriUser == null || uriUser == "")
			return;

		for (String uri:getProjectTeam(uriUser))
		{
			List<String> lst = Service.webServiceDelegate.getValueOfSpecificPropertyForIndividual(null, uri, ConsistentOntology.HAS_NAME);
			String s = "";
			if (lst.size() == 0)
				s=uri;
			else
				s=lst.get(0);
			if (s =="")
				s=uri;
			//Neu co gia tri hasName add name dau tien, neu khong add uri
			cmbProjectTeam.add(s);
			cmbProjectTeam.setData(s,uri);
		}
	}
}
