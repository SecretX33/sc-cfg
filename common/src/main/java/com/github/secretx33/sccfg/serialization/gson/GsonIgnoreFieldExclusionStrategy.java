package com.github.secretx33.sccfg.serialization.gson;

import com.github.secretx33.sccfg.api.annotation.IgnoreField;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public class GsonIgnoreFieldExclusionStrategy implements ExclusionStrategy {

    @Override
    public boolean shouldSkipField(final FieldAttributes fa) {
        checkNotNull(fa, "fa");
        return fa.getAnnotation(IgnoreField.class) != null;
    }

    @Override
    public boolean shouldSkipClass(final Class<?> clazz) {
        return false;
    }
}
