package br.ufba.dcc.wiser.iot_reactive.bus;

import static br.ufba.dcc.wiser.iot_reactive.bus.util.TcclSwitch.executeWithTCCLSwitch;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class VertxActivator implements BundleActivator {

    private ServiceRegistration vertxRegistration;
    private ServiceRegistration ebRegistration;

    @Override
    public void start(BundleContext context) throws Exception {

        try {

            System.out.println("Creating Vert.x instance");

            Vertx vertx = executeWithTCCLSwitch(() -> Vertx.vertx());

            vertxRegistration = context.registerService(Vertx.class, vertx, null);
            System.out.println("Reactive IoT Message Channel: Vert.x service registered");
            ebRegistration = context.registerService(EventBus.class, vertx.eventBus(), null);
            System.out.println("Reactive IoT Message Channel: Vert.x Event Bus service registered");

        } catch (Exception c) {
            c.printStackTrace();
        }

    }

    @Override
    public void stop(BundleContext context) throws Exception {
        // TODO add deactivation code here
    }

}
