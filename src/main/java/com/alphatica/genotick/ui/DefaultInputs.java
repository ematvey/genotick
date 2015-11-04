package com.alphatica.genotick.ui;

import com.alphatica.genotick.data.MainAppData;
import com.alphatica.genotick.genotick.Application;
import com.alphatica.genotick.genotick.MainSettings;

@SuppressWarnings("unused")
class DefaultInputs implements UserInput {

    @Override
    public void show(Application application) {
        MainAppData data = application.createData(MainSettings.DEFAULT_DATA_DIR);
        MainSettings defaults = MainSettings.getSettings(data.getFirstTimePoint(),data.getLastTimePoint());
        application.start(defaults, data);
    }


}
