package com.github.secretx33.sccfg.exception;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class MissingTypeOverrideOnAdapter extends ConfigException {

    public MissingTypeOverrideOnAdapter(final Class<?> clazz) {
        super("Could not create instance of typeAdapter class '" + checkNotNull(clazz, "clazz cannot be null").getName() + "' because you forgot to specify for what class is that adapter for. Please specify the class for which the adapter is expecting, or remove the annotation from it.");
    }
}
