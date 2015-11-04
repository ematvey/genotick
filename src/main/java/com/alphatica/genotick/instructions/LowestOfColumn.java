package com.alphatica.genotick.instructions;

import com.alphatica.genotick.processor.Processor;

import java.io.Serializable;

public class LowestOfColumn extends RegRegInstruction implements Serializable {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = -6945803435707758563L;

    @SuppressWarnings("unused")
    public LowestOfColumn() {
    }

    private LowestOfColumn(LowestOfColumn lowestOfColumn) {
        this.setRegister1(lowestOfColumn.getRegister1());
        this.setRegister2(lowestOfColumn.getRegister2());
    }

    @Override
    public void executeOn(Processor processor) {
        processor.execute(this);
    }

    @Override
    public Instruction copy() {
        return new LowestOfColumn(this);
    }
}
