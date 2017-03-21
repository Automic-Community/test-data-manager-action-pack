package com.automic.testdatamanager.actions;

import javax.json.JsonObject;

import com.automic.testdatamanager.constants.Constants;
import com.automic.testdatamanager.exception.AutomicException;
import com.automic.testdatamanager.util.CommonUtil;
import com.automic.testdatamanager.util.ConsoleWriter;
import com.automic.testdatamanager.validator.TDMValidator;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class GetJobStatusAction extends AbstractHttpAction {
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

		String apiVersion = CommonUtil.getEnvParameter(Constants.ENV_API_VERSION, Constants.API_VERSION);
		WebResource webResource = getClient().path("TDMJobService").path("api").path("ca").path(apiVersion).path("jobs")
				.path(jobId);

		// encode username:password to Base64

		ConsoleWriter.writeln("Calling url " + webResource.getURI());

		ClientResponse response = webResource.header("Authorization", token).get(ClientResponse.class);

		JsonObject jsonObjectResponse = CommonUtil.jsonObjectResponse(response.getEntityInputStream());

		ConsoleWriter.writeln(jsonObjectResponse);
		ConsoleWriter.writeln("UC4RB_TDM_JOBSTATUS ::=" + jsonObjectResponse.getString("status"));

	}

}
