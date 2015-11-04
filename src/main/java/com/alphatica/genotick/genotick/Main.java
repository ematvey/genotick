package com.alphatica.genotick.genotick;

import com.alphatica.genotick.reversal.Reversal;
import com.alphatica.genotick.ui.Parameters;
import com.alphatica.genotick.ui.UserInput;
import com.alphatica.genotick.ui.UserInputOutputFactory;
import com.alphatica.genotick.ui.UserOutput;

class Main {
    private static UserInput input;
    private static UserOutput output;

    public static void main(String... args) {
        setupDebug();
        Parameters parameters = new Parameters(args);
        getUserIO(parameters);
        checkReverse(parameters);
        checkSimulation(parameters);
    }

    private static void setupDebug() {
        Debug.setShowTime(true);
        Debug.toFile("genotick.out");
    }

    private static void getUserIO(Parameters parameters) {
        input = UserInputOutputFactory.getUserInput(parameters);
        if(input == null) {
            exit(ERROR_CODES.NO_INPUT);
        }
        output = UserInputOutputFactory.getUserOutput();
        //noinspection ConstantConditions
        if(output == null) {
            exit(ERROR_CODES.NO_OUTPUT);
        }
    }

    private static void checkReverse(Parameters parameters) {
        String reverseValue = parameters.getValue("reverse");
        if(reverseValue == null)
            return;
        Reversal reversal = new Reversal(reverseValue,output);
        reversal.reverse();
        System.exit(0);
    }

    private static void checkSimulation(Parameters parameters) {
        if(!parameters.allConsumed()) {
            output.errorMessage("Not all program arguments processed: " + parameters.getUnconsumed());
            exit(ERROR_CODES.UNKNOWN_ARGUMENT);
        }
        Application application = new Application(output);
        input.show(application);
    }

    private static void exit(ERROR_CODES code) {
        System.exit(code.getValue());
    }
}

enum ERROR_CODES {
    NO_INPUT(1),
    UNKNOWN_ARGUMENT(2),
    NO_OUTPUT(3);

    private final int code;

    ERROR_CODES(int code) {
        this.code = code;
    }

    public int getValue() {
        return code;
    }
}
