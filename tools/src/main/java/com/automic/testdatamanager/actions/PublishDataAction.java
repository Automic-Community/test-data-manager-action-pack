package com.automic.testdatamanager.actions;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import javax.ws.rs.core.MediaType;

import com.automic.testdatamanager.exception.AutomicException;
import com.automic.testdatamanager.util.ConsoleWriter;
import com.automic.testdatamanager.validator.TDMValidator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class PublishDataAction extends AbstractHttpAction {
	private static final String endpoint = "/TDMJobService/api/ca/v1/jobs";
	private String filePath;
	private File file;

	public PublishDataAction() {
		addOption("filePath", true, "Json File to publish data");

	}

	@Override
	protected void executeSpecific() throws AutomicException {

		String entity = prepareInput();
	
		WebResource webResource = getClient().path(endpoint);

		ConsoleWriter.writeln("Calling url " + webResource.getURI());

		ClientResponse response = webResource.entity(entity, MediaType.APPLICATION_JSON).header("Authorization", token)
				.post(ClientResponse.class);

		JsonObject responseJson = new JsonParser().parse(response.getEntity(String.class)).getAsJsonObject();
		ConsoleWriter.writeln(responseJson);
		ConsoleWriter.writeln("UC4RB_TDM_JOBID ::=" + responseJson.get("jobId"));

	}

	private String prepareInput() throws AutomicException {

		filePath = getOptionValue("filePath");
		file = new File(filePath);
		TDMValidator.checkFileExists(file);

		try {

			return (new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8));

		} catch (Exception e) {
			throw new AutomicException(String.format("System error occured", e.getMessage()));
		}

	}

}
