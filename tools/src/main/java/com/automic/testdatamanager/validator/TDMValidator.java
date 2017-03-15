package com.automic.testdatamanager.validator;

import com.automic.testdatamanager.constants.ExceptionConstants;
import com.automic.testdatamanager.exception.AutomicException;
import com.automic.testdatamanager.util.CommonUtil;

/**
 * This class provides common validations as required by action(s).
 *
 */

public final class TDMValidator {

	private TDMValidator() {
	}

	public static final void checkNotEmpty(String parameter,
			String parameterName) throws AutomicException {
		if (!CommonUtil.checkNotEmpty(parameter)) {
			throw new AutomicException(String.format(
					ExceptionConstants.INVALID_INPUT_PARAMETER, parameterName,
					parameter));
		}
	}

	public static final void checkNotNull(Object parameter, String parameterName)
			throws AutomicException {
		if (parameter != null) {
			throw new AutomicException(String.format(
					ExceptionConstants.INVALID_INPUT_PARAMETER, parameterName,
					parameter));
		}
	}
	
	public static void lessThan(int value, int lessThan, String parameterName) throws AutomicException {
        if (value < lessThan) {
            String errMsg = String.format(ExceptionConstants.INVALID_INPUT_PARAMETER, parameterName, value);
            throw new AutomicException(errMsg);
        }
    }

}
