package com.alphatica.genotick.population;

public interface PopulationDAO {

    Iterable<Program> getProgramList();

    int getAvailableProgramsCount();

    Program getProgramByName(ProgramName name);

    void saveProgram(Program program);

    void removeProgram(ProgramName programName);

    void setSettings(String settings);

    ProgramName[] listProgramNames();
}
