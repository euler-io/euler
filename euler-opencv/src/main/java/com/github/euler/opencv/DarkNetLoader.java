package com.github.euler.opencv;

import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;

public class DarkNetLoader implements DnnNetLoader {

    private final String cfgFile;
    private final String darknetModel;

    public DarkNetLoader(String cfgFile, String darknetModel) {
        super();
        this.cfgFile = cfgFile;
        this.darknetModel = darknetModel;
    }

    @Override
    public Net load() {
        return Dnn.readNetFromDarknet(cfgFile, darknetModel);
    }

}
