package io.casehub.iot.webapp.app.engine;

import io.casehub.api.engine.YamlCaseHub;
import io.casehub.api.model.CaseDefinition;
import io.casehub.iot.api.spi.DeviceProvider;
import io.casehub.iot.api.spi.DeviceRegistry;
import io.casehub.iot.webapp.engine.LightCycleCaseDescriptor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

@ApplicationScoped
public class LightCycleCaseHub extends YamlCaseHub {

    @Inject
    Instance<DeviceProvider> providers;

    @Inject
    DeviceRegistry registry;

    public LightCycleCaseHub() {
        super("iot/light-cycle-test.yaml");
    }

    @Override
    protected void augment(final CaseDefinition definition) {
        final var descriptor = new LightCycleCaseDescriptor(providers, registry);
        descriptor.workers().forEach(definition.getWorkers()::add);
    }
}
