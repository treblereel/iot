package io.casehub.iot.webapp.engine;

import io.casehub.iot.api.spi.DeviceProvider;
import io.casehub.iot.api.spi.DeviceRegistry;
import io.casehub.iot.webapp.worker.DeviceCommandWorkerFunction;
import io.casehub.worker.api.Worker;
import jakarta.enterprise.inject.Instance;

import java.util.List;

public final class LightCycleCaseDescriptor {

    private final Instance<DeviceProvider> providers;
    private final DeviceRegistry deviceRegistry;

    public LightCycleCaseDescriptor(
            final Instance<DeviceProvider> providers,
            final DeviceRegistry deviceRegistry) {
        this.providers = providers;
        this.deviceRegistry = deviceRegistry;
    }

    public List<Worker> workers() {
        return List.of(lightOnWorker(), lightOffWorker());
    }

    private Worker lightOnWorker() {
        return Worker.builder()
                     .name("light-on")
                     .capabilityName("light-on")
                     .function(new DeviceCommandWorkerFunction(providers, deviceRegistry))
                     .build();
    }

    private Worker lightOffWorker() {
        return Worker.builder()
                     .name("light-off")
                     .capabilityName("light-off")
                     .function(new DeviceCommandWorkerFunction(providers, deviceRegistry))
                     .build();
    }
}
