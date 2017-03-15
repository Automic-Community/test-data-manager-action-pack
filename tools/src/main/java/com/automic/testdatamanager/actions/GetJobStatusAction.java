package com.automic.testdatamanager.actions;

import com.automic.testdatamanager.exception.AutomicException;
import com.automic.testdatamanager.util.ConsoleWriter;
import com.automic.testdatamanager.validator.TDMValidator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class GetJobStatusAction extends AbstractHttpAction {
	private static final String endpoint = "/TDMJobService/api/ca/v1/jobs";
	private String jobId;

	public GetJobStatusAction() {		
		addOption("jobId", true, "Id of the Job");
	}

	@Override
	protected void executeSpecific() throws AutomicException {
		// input parameter being prepared
		jobId = getOptionValue("jobId");
		TDMValidator.checkNotEmpty(jobId, "Id of the Job");

		// request to rest api
		WebResource webResource = getClient().path(endpoint).path(jobId);

		// encode username:password to Base64

		ConsoleWriter.writeln("Calling url " + webResource.getURI());

		ClientResponse response = webResource.header("Authorization",token).get(ClientResponse.class);

		JsonObject responseJson = new JsonParser().parse(response.getEntity(String.class)).getAsJsonObject();
		ConsoleWriter.writeln(responseJson);

	}

}
