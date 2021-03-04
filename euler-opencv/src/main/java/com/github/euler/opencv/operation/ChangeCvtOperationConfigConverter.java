package com.github.euler.opencv.operation;

import java.lang.reflect.Field;

import org.opencv.imgproc.Imgproc;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.opencv.AbstractMatOperationTypeConfigConverter;
import com.github.euler.opencv.MatOperation;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;

public class ChangeCvtOperationConfigConverter extends AbstractMatOperationTypeConfigConverter {

    private static final String COLOR_PREFIX = "COLOR_";

    @Override
    public String configType() {
        return "change-cvt";
    }

    @Override
    public MatOperation convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        int code;
        try {
            code = config.getInt("space-conversion-code");
        } catch (ConfigException.WrongType e) {
            String codeStr = config.getString("space-conversion-code");
            code = getCodeByColor(codeStr);
        }

        return new ChangeCvtOperation(code);
    }

    private int getCodeByColor(String codeStr) {
        try {
            Field field = Imgproc.class.getField(COLOR_PREFIX + codeStr);
            return field.getInt(null);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException("There is not such space coversion code:" + codeStr, e);
        }
    }

}
