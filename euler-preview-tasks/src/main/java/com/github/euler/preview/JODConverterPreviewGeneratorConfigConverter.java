package com.github.euler.preview;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;

import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.office.LocalOfficeManager;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.EulerHooks;
import com.typesafe.config.Config;

public class JODConverterPreviewGeneratorConfigConverter extends AbstractPreviewGeneratorConfigConverter {

    @Override
    public String configType() {
        return "openoffice";
    }

    @Override
    public PreviewGenerator convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        JODConverterPreviewGenerator.Config jodConfig = new JODConverterPreviewGenerator.Config();
        jodConfig.setInitialPage(0);
        jodConfig.setFinalPage(1);

        OfficeManager officeManager = buildOfficeManager();
        EulerHooks hooks = configContext.getRequired(EulerHooks.class);
        hooks.registerInitializable(() -> {
            try {
                officeManager.start();
            } catch (OfficeException e) {
                throw new RuntimeException(e);
            }
        });

        hooks.registerCloseable(() -> {
            try {
                officeManager.stop();
            } catch (OfficeException e) {
                throw new RuntimeException(e);
            }
        });

        return new JODConverterPreviewGenerator(jodConfig, officeManager);
    }

    private OfficeManager buildOfficeManager() {
        try {
            return LocalOfficeManager.builder()
                    .workingDir(Files.createTempDirectory("ooffice").toFile())
                    .portNumbers(findFreePort())
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int findFreePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("Could not find an open port.", e);
        }
    }

}
