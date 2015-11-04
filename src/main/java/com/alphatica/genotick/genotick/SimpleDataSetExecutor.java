package com.alphatica.genotick.genotick;

import com.alphatica.genotick.population.*;

public class  SimpleDataSetExecutor implements DataSetExecutor {

    private ProgramExecutorSettings settings;

    @Override
    public ProgramResult execute(ProgramData programData, ProgramName programName, Population population) {
        Program program = population.getProgram(programName);
        assert program != null;
        ProgramResult result = execute(programData,program);
        population.saveProgram(program);
        return result;
    }

    @Override
    public void setExecutorFactory(ProgramExecutorSettings settings) {
        this.settings = settings;
    }

    private ProgramResult execute(ProgramData programData, Program program) {
        ProgramExecutor executor = ProgramExecutorFactory.getDefaultProgramExecutor(settings);
        Prediction prediction = executor.executeProgram(programData,program);
        ProgramResult result = new ProgramResult(prediction, program, programData);
        program.recordPrediction(result.getPrediction());
        return result;
    }


}
