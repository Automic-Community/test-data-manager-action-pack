package com.automic.testdatamanager.actions;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.MediaType;

import com.automic.testdatamanager.config.HttpClientConfig;
import com.automic.testdatamanager.constants.Constants;
import com.automic.testdatamanager.constants.ExceptionConstants;
import com.automic.testdatamanager.exception.AutomicException;
import com.automic.testdatamanager.filter.GenericResponseFilter;
import com.automic.testdatamanager.util.CommonUtil;
import com.automic.testdatamanager.util.ConsoleWriter;
import com.automic.testdatamanager.validator.TDMValidator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.Base64;

/**
 * This class defines the execution of any action.It provides some
 * initializations and validations on common inputs .The child actions will
 * implement its executeSpecific() method as per their own need.
 */
public abstract class AbstractHttpAction extends AbstractAction {

	/**
	 * Username for Login into TDM Portal
	 */
	private String username;

	/**
	 * Password for Login into TDM Portal
	 */
	private String password;

	/**
	 * Connection timeout in milliseconds
	 */
	private int connectionTimeOut;

	/**
	 * Read timeout in milliseconds
	 */
	private int readTimeOut;

	private Client client;

	/**
	 * Option to skip validation
	 */
	private boolean skipCertValidation;

	protected String token;

	/**
	 * Service end point
	 */
	protected URI baseUrl;

	public AbstractHttpAction() {
		addOption(Constants.READ_TIMEOUT, true, "Read timeout");
		addOption(Constants.CONN_TIMEOUT, true, "connection timeout");
		addOption(Constants.BASE_URL, true, "TDM Portal URL");
		addOption(Constants.USERNAME, true, "Username for Login into TDM Portal");
		addOption(Constants.PASSWORD, true, "Password for Login into TDM Portal");
		addOption(Constants.SKIP_CERT_VALIDATION, false, "Skip SSL validation");

	}

	/**
	 * This method initializes the arguments and calls the execute method.
	 *
	 * @throws AutomicException
	 *             exception while executing an action
	 */
	public final void execute() throws AutomicException {
		prepareCommonInputs();
		try {
			login();
			executeSpecific();
			logout();
		} finally {
			if (client != null) {
				client.destroy();
			}
		}
	}

	private void prepareCommonInputs() throws AutomicException {
		String temp = getOptionValue(Constants.BASE_URL);
		try {

			this.username = getOptionValue("username");
			TDMValidator.checkNotEmpty(username, "Username for Login into TDM Portal");

			this.password = getOptionValue("password");
			TDMValidator.checkNotEmpty(password, "Password for Login into TDM Portal");

			this.baseUrl = new URI(temp);

			this.connectionTimeOut = CommonUtil.parseStringValue(getOptionValue(Constants.CONN_TIMEOUT),
					Constants.MINUS_ONE);
			TDMValidator.lessThan(connectionTimeOut, Constants.ZERO, "Connect Timeout");

			this.readTimeOut = CommonUtil.parseStringValue(getOptionValue(Constants.READ_TIMEOUT), Constants.MINUS_ONE);
			TDMValidator.lessThan(readTimeOut, Constants.ZERO, "Read Timeout");

			this.skipCertValidation = CommonUtil.convert2Bool(getOptionValue(Constants.SKIP_CERT_VALIDATION));

		} catch (URISyntaxException e) {
			ConsoleWriter.writeln(e);
			String msg = String.format(ExceptionConstants.INVALID_INPUT_PARAMETER, "URL", temp);
			throw new AutomicException(msg);
		}
	}

	/**
	 * Method to execute the action.
	 *
	 * @throws AutomicException
	 */
	protected abstract void executeSpecific() throws AutomicException;

	/**
	 * Method to initialize Client instance.
	 *
	 * @throws AutomicException
	 *
	 */
	protected WebResource getClient() throws AutomicException {
		if (client == null) {
			client = HttpClientConfig.getClient(this.connectionTimeOut, this.readTimeOut, this.skipCertValidation);
			client.addFilter(new GenericResponseFilter());
		}
		return client.resource(baseUrl);
	}

	private void login() throws AutomicException {

		String endpoint = "/TestDataManager/user/login";

		// request to rest api
		WebResource webResource = getClient().path(endpoint);

		ConsoleWriter.writeln("Calling url " + webResource.getURI());

		ClientResponse response = webResource.header("Authorization", encodeUserNamePasswordToBase64())
				.post(ClientResponse.class);

		JsonObject responseJson = new JsonParser().parse(response.getEntity(String.class)).getAsJsonObject();

		this.token = (responseJson.get("token") == null ? null : "Bearer " + responseJson.get("token").getAsString());
		if (null == this.token) {
			throw new AutomicException("Error occured while login into the CA TDM portal,token is empty");
		}

	}

	private String encodeUserNamePasswordToBase64() {
		String usernamepassword = new StringBuffer().append(this.username).append(":").append(this.password).toString();
		String bytesEncoded = new String(Base64.encode(usernamepassword.getBytes()));

		return new StringBuffer().append("Basic ").append(bytesEncoded).toString();

	}

	private void logout() throws AutomicException {
		String endpoint = "/TestDataManager/user/logout";

		// request to rest api
		WebResource webResource = getClient().path(endpoint);

		ConsoleWriter.writeln("Calling url " + webResource.getURI());

		ClientResponse response = webResource.header("Authorization", this.token)
				.entity("{\"tokenTobeInvalidated\":null}", MediaType.APPLICATION_JSON).put(ClientResponse.class);

		ConsoleWriter.writeln(response);

	}

}