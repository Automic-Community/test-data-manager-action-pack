package com.automic.testdatamanager.client;


import com.automic.testdatamanager.actions.AbstractAction;
import com.automic.testdatamanager.cli.Cli;
import com.automic.testdatamanager.cli.CliOptions;
import com.automic.testdatamanager.constants.Constants;
import com.automic.testdatamanager.constants.ExceptionConstants;
import com.automic.testdatamanager.exception.AutomicException;
import com.automic.testdatamanager.util.ConsoleWriter;

public class ClientHelper {
	private static final String ABSOLUTEPATH = "com.automic.testdatamanager.actions";
	
	private ClientHelper() {
    }

    /**
     * Method to delegate parameters to an instance of {@link AbstractAction} based on the value of Action parameter.
     *
     * @param actionParameters
     *            of options with key as option name and value is option value
     * @throws AutomicException
     */

    public static void executeAction(String[] actionParameters) throws AutomicException {
        String actionName = new Cli(new CliOptions(), actionParameters).getOptionValue(Constants.ACTION);

        AbstractAction action = null;
        try {
            Class<?> classDefinition = Class.forName(getCanonicalName(actionName));
            action = (AbstractAction) classDefinition.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            ConsoleWriter.writeln(e);
            String msg = String.format(ExceptionConstants.INVALID_INPUT_PARAMETER, Constants.ACTION, actionName);
            throw new AutomicException(msg);
        }
        action.executeAction(actionParameters);
    }

    private static String getCanonicalName(String clsName) {
        return ABSOLUTEPATH + "." + clsName;
    }
}
