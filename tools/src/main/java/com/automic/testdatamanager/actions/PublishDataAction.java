package com.automic.testdatamanager.actions;

import java.io.File;

import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;

import com.automic.testdatamanager.exception.AutomicException;
import com.automic.testdatamanager.util.CommonUtil;
import com.automic.testdatamanager.util.ConsoleWriter;
import com.automic.testdatamanager.validator.TDMValidator;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class PublishDataAction extends AbstractHttpAction {
	private static final String endpoint = "/TDMJobService/api/ca/v1/jobs";
	private String filePath;

	public PublishDataAction() {
		addOption("filePath", true, "Json File to publish data");

	}

	@Override
	protected void executeSpecific() throws AutomicException {

		filePath = getOptionValue("filePath");
		File file = new File(filePath);
		TDMValidator.checkFileExists(file);

		WebResource webResource = getClient().path(endpoint);

		ConsoleWriter.writeln("Calling url " + webResource.getURI());

		ClientResponse response = webResource.entity(file, MediaType.APPLICATION_JSON).header("Authorization", token)
				.post(ClientResponse.class);

		JsonObject jsonObjectResponse = CommonUtil.jsonObjectResponse(response.getEntityInputStream());

		ConsoleWriter.writeln(jsonObjectResponse);
		ConsoleWriter.writeln("UC4RB_TDM_JOBID ::=" + jsonObjectResponse.get("jobId"));

	}

}
