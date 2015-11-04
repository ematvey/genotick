package com.alphatica.genotick.population;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class PopulationDAORAM implements PopulationDAO {
    private final Map<ProgramName,Program> map = new HashMap<>();
    private final Random random = new Random();

    @Override
    public Iterable<Program> getProgramList() {
        return new Iterable<Program> () {
            @Override
            public Iterator<Program> iterator() {
                return map.values().iterator();
            }
        };
    }

    @Override
    public int getAvailableProgramsCount() {
        return map.size();
    }

    @Override
    public Program getProgramByName(ProgramName name) {
        return map.get(name);
    }

    @Override
    public void saveProgram(Program program) {
        if(program.getName() == null) {
            program.setName(getAvailableProgramName());
        }
        map.put(program.getName(),program);
    }

    @Override
    public void removeProgram(ProgramName programName) {
        map.remove(programName);
    }


    private ProgramName getAvailableProgramName() {
        long l;
        ProgramName name;
        boolean nameExist;
        do {
            l = random.nextLong();
            if(l < 0)
                l = -l;
            name =  new ProgramName(l);
            nameExist = map.containsKey(name);
        } while(nameExist);
        return name;
    }

    @Override
    public void setSettings(String settings) {
        /* Empty, Ram dao doesn't need settings */
    }

    @Override
    public ProgramName[] listProgramNames() {
        return map.keySet().toArray(new ProgramName[map.size()]);
    }
}
