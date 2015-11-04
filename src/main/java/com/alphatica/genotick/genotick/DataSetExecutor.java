package com.alphatica.genotick.genotick;

import com.alphatica.genotick.population.*;

public interface DataSetExecutor {

    ProgramResult execute(ProgramData programData, ProgramName programName, Population population);

    void setExecutorFactory(ProgramExecutorSettings settings);

}
