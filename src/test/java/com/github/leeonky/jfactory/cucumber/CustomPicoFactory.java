package com.github.leeonky.jfactory.cucumber;

import io.cucumber.core.backend.ObjectFactory;
import io.cucumber.picocontainer.PicoFactory;

public class CustomPicoFactory implements ObjectFactory {
    private PicoFactory delegate = new PicoFactory();

    @Override
    public void start() {
        delegate.start();
    }

    @Override
    public void stop() {
        delegate.stop();
    }

    @Override
    public boolean addClass(Class<?> glueClass) {
        return delegate.addClass(glueClass);
    }

    @Override
    public <T> T getInstance(Class<T> glueClass) {
        if (glueClass.equals(JData.class))
            return (T) new JData(new EntityFactory());
        return delegate.getInstance(glueClass);
    }
}
