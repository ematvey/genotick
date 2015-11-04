package com.alphatica.genotick.ui;

import com.alphatica.genotick.genotick.Debug;

class ConsoleOutput implements UserOutput {
    @Override
    public void errorMessage(String message) {
        Debug.d("Error:",message);
    }

    @Override
    public void warningMessage(String message) {
        Debug.d("Warning:",message);
    }
}
