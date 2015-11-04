package com.alphatica.genotick.breeder;

import com.alphatica.genotick.genotick.Debug;
import com.alphatica.genotick.instructions.Instruction;
import com.alphatica.genotick.instructions.InstructionList;
import com.alphatica.genotick.mutator.Mutator;
import com.alphatica.genotick.population.Population;
import com.alphatica.genotick.population.Program;
import com.alphatica.genotick.population.ProgramInfo;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SimpleBreeder implements ProgramBreeder {
    private BreederSettings settings;
    private Mutator mutator;

    public static ProgramBreeder getInstance() {
        return new SimpleBreeder();
    }

    @Override
    public void breedPopulation(Population population) {
        Debug.d("Breeding population");
        Debug.d("Current population size", population.getSize());
        addRequiredRandomPrograms(population);
        if(population.haveSpaceToBreed()) {
            breedPopulationFromParents(population);
            addOptionalRandomPrograms(population);
        }
        Debug.d("Breeder exiting");
        Debug.d("Current population size", population.getSize());
    }

    private void addOptionalRandomPrograms(Population population) {
        int count = population.getDesiredSize() - population.getSize();
        if(count > 0) {
            fillWithPrograms(count, population);
        }
    }

    private void addRequiredRandomPrograms(Population population) {
        if(settings.randomPrograms > 0) {
            int count = (int)Math.round(settings.randomPrograms * population.getDesiredSize());
            fillWithPrograms(count,population);
        }
    }

    private void fillWithPrograms(int count, Population population) {
        for(int i = 0; i < count; i++) {
            createNewProgram(population);
        }
    }

    private void createNewProgram(Population population) {
        Program program = Program.createEmptyProgram(settings.dataMaximumOffset);
        int instructionsCount = mutator.getNextInt() % 1024;
        InstructionList main = program.getMainFunction();
        while(instructionsCount-- > 0) {
            addInstructionToMain(main, mutator);
        }
        population.saveProgram(program);
    }

    private void addInstructionToMain(InstructionList main, Mutator mutator) {
        Instruction instruction = mutator.getRandomInstruction();
        instruction.mutate(mutator);
        main.addInstruction(instruction);
    }
    private void breedPopulationFromParents(Population population) {
        List<ProgramInfo> programInfoList = population.getProgramInfoList();
        removeNotAllowedPrograms(programInfoList);
        Collections.sort(programInfoList, ProgramInfo.comparatorByAbsoluteWeight);
        breedPopulation(population, programInfoList);
    }

    private void removeNotAllowedPrograms(List<ProgramInfo> programInfoList) {
        Iterator<ProgramInfo> iterator = programInfoList.iterator();
        while(iterator.hasNext()) {
            ProgramInfo programInfo = iterator.next();
            if(!programInfo.canBeParent(settings.minimumOutcomesToAllowBreeding, settings.outcomesBetweenBreeding))
                iterator.remove();
        }
    }

    private void breedPopulation(Population population, List<ProgramInfo> list) {
        while(population.haveSpaceToBreed()) {
            Program parent1 = getPossibleParent(population, list);
            Program parent2 = getPossibleParent(population,list);
            if(parent1 == null || parent2 == null)
                break;
            Program child = Program.createEmptyProgram(settings.dataMaximumOffset);
            child.setFromParents();
            makeChild(parent1, parent2, child);
            population.saveProgram(child);
            parent1.increaseChildren();
            population.saveProgram(parent1);
            parent2.increaseChildren();
            population.saveProgram(parent2);
        }
    }

    private void makeChild(Program parent1, Program parent2, Program child) {
        double weight = getParentsWeight(parent1, parent2);
        child.setInheritedWeight(weight);
        InstructionList instructionList = mixMainInstructionLists(parent1,parent2);
        child.setMainInstructionList(instructionList);
    }

    private double getParentsWeight(Program parent1, Program parent2) {
        return settings.inheritedWeightPercent * (parent1.getWeight() + parent2.getWeight()) / 2;
    }

    private InstructionList mixMainInstructionLists(Program parent1, Program parent2) {
        InstructionList source1 = parent1.getMainFunction();
        InstructionList source2 = parent2.getMainFunction();
        return blendInstructionLists(source1,source2);
    }

    /*
    This potentially will make programs gradually shorter.
    Let's say that list1.size == 4 and list2.size == 2. Average length is 3.
    Then, break1 will be between <0,3> and break2 <0,1>
    All possible lengths for new InstructionList will be: 0,1,2,3,1,2,3,4 with equal probability.
    Average length is 2.
    For higher numbers this change isn't so dramatic but may add up after many populations.
     */
    private InstructionList blendInstructionLists(InstructionList list1, InstructionList list2) {
        InstructionList instructionList = InstructionList.createInstructionList();
        int break1 = getBreakPoint(list1);
        int break2 = getBreakPoint(list2);
        copyBlock(instructionList, list1,0,break1);
        copyBlock(instructionList, list2,break2, list2.getSize());
        return instructionList;
    }

    private int getBreakPoint(InstructionList list) {
        int size = list.getSize();
        if(size == 0)
            return 0;
        else
            return Math.abs(mutator.getNextInt() % size);
    }

    private void copyBlock(InstructionList destination, InstructionList source, int start, int stop) {
        assert start <= stop: "start > stop " + String.format("%d %d", start,stop);
        for(int i = start; i <= stop; i++) {
            Instruction instruction = source.getInstruction(i).copy();
            addInstructionToInstructionList(instruction,destination);
        }
    }

    private void addInstructionToInstructionList(Instruction instruction, InstructionList instructionList) {
        if(!mutator.skipNextInstruction()) {
            if (mutator.getAllowNewInstruction()) {
                instruction = mutator.getRandomInstruction();
                instructionList.addInstruction(instruction);
            }
            if (mutator.getAllowInstructionMutation()) {
                instruction.mutate(mutator);
            }
            instructionList.addInstruction(instruction);
        }
    }

    private Program getPossibleParent(Population population, List<ProgramInfo> list) {
        double totalWeight = sumTotalWeight(list);
        double target = totalWeight * mutator.getNextDouble();
        double weightSoFar = 0;
        Program parent = null;
        Iterator<ProgramInfo> iterator = list.iterator();
        while(iterator.hasNext()) {
            ProgramInfo programInfo = iterator.next();
            weightSoFar += Math.abs(programInfo.getWeight());
            if(weightSoFar >= target) {
                parent = population.getProgram(programInfo.getName());
                iterator.remove();
                break;
            }
        }
        return parent;
    }

    private double sumTotalWeight(List<ProgramInfo> list) {
        double weight = 0;
        for(ProgramInfo programInfo: list) {
            weight += Math.abs(programInfo.getWeight());
        }
        return weight;
    }

    @Override
    public void setSettings(BreederSettings breederSettings, Mutator mutator) {
        this.settings = breederSettings;
        this.mutator = mutator;
    }
}
