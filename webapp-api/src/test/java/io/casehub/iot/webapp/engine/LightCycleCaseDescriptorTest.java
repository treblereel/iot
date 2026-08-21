package io.casehub.iot.webapp.engine;

import io.casehub.iot.api.CommandResult;
import io.casehub.iot.api.DeviceClass;
import io.casehub.iot.api.DeviceCommand;
import io.casehub.iot.api.LightDevice;
import io.casehub.iot.api.spi.DeviceProvider;
import io.casehub.iot.api.spi.DeviceRegistry;
import io.casehub.iot.webapp.worker.DeviceCommandWorkerFunction;
import io.casehub.worker.api.WorkerOutcome;
import jakarta.enterprise.inject.Instance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LightCycleCaseDescriptorTest {

    private DeviceRegistry deviceRegistry;
    private DeviceProvider deviceProvider;
    private Instance<DeviceProvider> providers;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        deviceRegistry = mock(DeviceRegistry.class);
        deviceProvider = mock(DeviceProvider.class);
        providers = mock(Instance.class);
        when(providers.stream()).thenReturn(java.util.stream.Stream.of(deviceProvider));
        when(deviceProvider.providerId()).thenReturn("openhab");
    }

    @Test
    void workersHaveLightOnAndLightOff() {
        var descriptor = new LightCycleCaseDescriptor(providers, deviceRegistry);
        var names = descriptor.workers().stream().map(w -> w.name()).toList();
        assertThat(names).containsExactly("light-on", "light-off");
    }

    @Test
    void workersMapToCorrectCapabilities() {
        var descriptor = new LightCycleCaseDescriptor(providers, deviceRegistry);
        var workers = descriptor.workers();
        assertThat(workers.get(0).capabilityNames()).containsExactly("light-on");
        assertThat(workers.get(1).capabilityNames()).containsExactly("light-off");
    }

    @Test
    void turnOnDispatchesToDevice() {
        var device = lightDevice();
        when(deviceRegistry.findById("LIFX_Bulb")).thenReturn(Optional.of(device));

        List<DeviceCommand> dispatched = new ArrayList<>();
        when(deviceProvider.dispatch(any(DeviceCommand.class))).thenAnswer(inv -> {
            dispatched.add(inv.getArgument(0));
            return CommandResult.SENT;
        });

        var fn = new DeviceCommandWorkerFunction(providers, deviceRegistry);
        var result = fn.apply(Map.of(
                "targetDeviceId", "LIFX_Bulb",
                "action", "turn_on"
        ));

        assertThat(result.outcome()).isInstanceOf(WorkerOutcome.Success.class);
        assertThat(dispatched).hasSize(1);
        assertThat(dispatched.get(0).action()).isEqualTo("turn_on");
    }

    @Test
    void turnOffDispatchesToDevice() {
        var device = lightDevice();
        when(deviceRegistry.findById("LIFX_Bulb")).thenReturn(Optional.of(device));

        List<DeviceCommand> dispatched = new ArrayList<>();
        when(deviceProvider.dispatch(any(DeviceCommand.class))).thenAnswer(inv -> {
            dispatched.add(inv.getArgument(0));
            return CommandResult.SENT;
        });

        var fn = new DeviceCommandWorkerFunction(providers, deviceRegistry);
        var result = fn.apply(Map.of(
                "targetDeviceId", "LIFX_Bulb",
                "action", "turn_off"
        ));

        assertThat(result.outcome()).isInstanceOf(WorkerOutcome.Success.class);
        assertThat(dispatched).hasSize(1);
        assertThat(dispatched.get(0).action()).isEqualTo("turn_off");
    }

    private LightDevice lightDevice() {
        return new LightDevice.Builder()
                .deviceId("LIFX_Bulb")
                .deviceClass(DeviceClass.LIGHT)
                .label("LIFX Bulb")
                .providerId("openhab")
                .tenancyId("default-tenant")
                .available(true)
                .lastUpdated(Instant.now())
                .on(true)
                .build();
    }
}
