package it.niedermann.nextcloud.tables.database.model;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class DataTypeServiceRegistry<T> {

    protected final Map<EDataType, T> cache = new HashMap<>();

    protected DataTypeServiceRegistry() {
        this(false);
    }

    /**
     * @see <a href="JEP 447">https://openjdk.org/jeps/447</a>
     * @param skipWarmUp can be skipped in case {@link #getService(EDataType)} requires to access
     *                   any other resource set or created in the constructor
     */
    protected DataTypeServiceRegistry(boolean skipWarmUp) {
        if (!skipWarmUp) {
            warmUp();
        }
    }

    /**
     * Calls {@link #getService(EDataType)} with each possible {@link EDataType#values()} to fill the {@link #cache}.
     */
    protected final void warmUp() {
        Arrays.stream(EDataType.values()).forEach(this::getService);
    }

    public abstract T getService(@NonNull EDataType dataType);
}
