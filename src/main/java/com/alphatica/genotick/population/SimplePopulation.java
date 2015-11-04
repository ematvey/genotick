package com.alphatica.genotick.population;


import java.util.ArrayList;
import java.util.List;

public class SimplePopulation implements Population {
    private int desiredSize = 1024;
    private PopulationDAO dao;

    @Override
    public void setDesiredSize(int size) {
        desiredSize = size;
    }

    @Override
    public int getDesiredSize() {
        return desiredSize;
    }

    @Override
    public int getSize() {
        return dao.getAvailableProgramsCount();
    }

    @Override
    public void setDao(PopulationDAO dao) {
        this.dao = dao;
    }

    @Override
    public Iterable<Program> listPrograms() {
        return dao.getProgramList();
    }

    @Override
    public void saveProgram(Program program) {
        dao.saveProgram(program);
    }

    @Override
    public Program getProgram(ProgramName name) {
        return dao.getProgramByName(name);
    }

    @Override
    public void removeProgram(ProgramName programName) {
        dao.removeProgram(programName);
    }

    @Override
    public List<ProgramInfo> getProgramInfoList() {
        List<ProgramInfo> list = new ArrayList<>(dao.getAvailableProgramsCount());
        for(Program program: dao.getProgramList()) {
            ProgramInfo programInfo = new ProgramInfo(program);
            list.add(programInfo);
        }
        return list;
    }

    @Override
    public boolean haveSpaceToBreed() {
        return getSize() < getDesiredSize();
    }

    @Override
    public void savePopulation(String path) {
        PopulationDAO fs = new PopulationDAOFileSystem();
        fs.setSettings(path);
        for(Program program: dao.getProgramList()) {
            fs.saveProgram(program);
        }
    }

    @Override
    public ProgramName[] listProgramNames() {
        return dao.listProgramNames();
    }
}
