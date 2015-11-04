package com.alphatica.genotick.killer;

import com.alphatica.genotick.genotick.Debug;
import com.alphatica.genotick.population.Population;
import com.alphatica.genotick.population.ProgramInfo;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


class SimpleProgramKiller implements ProgramKiller{
    private ProgramKillerSettings settings;
    private final Random random;

    public static ProgramKiller getInstance() {
        return new SimpleProgramKiller();
    }
    private SimpleProgramKiller() {
        random = new Random();
    }

    @Override
    public void killPrograms(Population population) {
        List<ProgramInfo> list = population.getProgramInfoList();
        killNonPredictingPrograms(population, list);
        killNonSymmetricalPrograms(population, list);
        removeProtectedPrograms(population,list);
        killProgramsByWeight(population, list);
        killProgramsByAge(population, list);
        Debug.d("Population average weight:",ProgramInfo.getAverageWeight(list));
    }

    private void killNonSymmetricalPrograms(Population population, List<ProgramInfo> list) {
        if(!settings.requireSymmetricalPrograms)
            return;
        Debug.d("Killing non symmetrical programs");
        Iterator<ProgramInfo> iterator = list.iterator();
        while(iterator.hasNext()) {
            ProgramInfo programInfo = iterator.next();
            if(programInfo.getBias() != 0) {
                iterator.remove();
                population.removeProgram(programInfo.getName());
            }
        }
        Debug.d("Finished killing non symmetrical programs");
    }

    private void killNonPredictingPrograms(Population population, List<ProgramInfo> list) {
        if(!settings.killNonPredictingPrograms)
            return;
        Debug.d("Killing non predicting programs");
        Iterator<ProgramInfo> iterator = list.iterator();
        while(iterator.hasNext()) {
            ProgramInfo programInfo = iterator.next();
            if(programInfo.getTotalPredictions() == 0) {
                iterator.remove();
                population.removeProgram(programInfo.getName());
            }
        }
        Debug.d("Finished killing non predicting programs");
    }

    private void removeProtectedPrograms(Population population, List<ProgramInfo> list) {
        protectUntilOutcomes(list);
        protectBest(list,population);
    }

    private void protectBest(List<ProgramInfo> list, Population population) {
        if(settings.protectBestPrograms > 0) {
            Collections.sort(list, ProgramInfo.comparatorByAbsoluteWeight);
            int i = (int)Math.round(settings.protectBestPrograms * population.getDesiredSize());
            while(i-- > 0) {
                ProgramInfo programInfo = getLastFromList(list);
                if(programInfo == null)
                    break;
            }
        }
    }

    private void protectUntilOutcomes(List<ProgramInfo> list) {
        for(int i = list.size()-1; i >= 0; i--) {
            ProgramInfo programInfo = list.get(i);
            if(programInfo.getTotalOutcomes() < settings.protectProgramUntilOutcomes)
                list.remove(i);
        }
    }

    @Override
    public void setSettings(ProgramKillerSettings killerSettings) {
        settings = killerSettings;
    }

    private void killProgramsByAge(Population population, List<ProgramInfo> list) {
        Collections.sort(list,ProgramInfo.comparatorByAge);
        int numberToKill = (int)Math.round(settings.maximumDeathByAge * list.size());
        Debug.d("Killing max",numberToKill,"by age.");
        killPrograms(list,numberToKill,population,settings.probabilityOfDeathByAge);
        Debug.d("Finished killing by age.");
    }

    private void killProgramsByWeight(Population population, List<ProgramInfo> list) {
        if(population.haveSpaceToBreed())
            return;
        Collections.sort(list, ProgramInfo.comparatorByAbsoluteWeight);
        Collections.reverse(list);
        int numberToKill = (int) Math.round(settings.maximumDeathByWeight * list.size());
        Debug.d("Killing max",numberToKill,"by weight");
        killPrograms(list,numberToKill,population,settings.probabilityOfDeathByWeight);
        Debug.d("Finished killing by weight");
    }

    private void killPrograms(List<ProgramInfo> list, int numberToKill, Population population, double probability) {
        while(numberToKill-- > 0) {
            ProgramInfo toKill = getLastFromList(list);
            if(toKill == null)
                return;
            if(random.nextDouble() < probability) {
                population.removeProgram(toKill.getName());
            }
        }
    }

    private ProgramInfo getLastFromList(List<ProgramInfo> list) {
        int size = list.size();
        if(size == 0)
            return null;
        return list.remove(size-1);
    }
}
