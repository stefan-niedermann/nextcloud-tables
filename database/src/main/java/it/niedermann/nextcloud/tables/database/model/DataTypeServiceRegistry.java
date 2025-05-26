package it.niedermann.nextcloud.tables.database.model;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class DataTypeServiceRegistry<T> {

    private static final Logger logger = Logger.getLogger(DataTypeServiceRegistry.class.getSimpleName());

    protected final Map<EDataType, T> cache = new HashMap<>();

    protected DataTypeServiceRegistry() {
        this(false);
    }

    /**
     * @param skipWarmUp can be skipped in case {@link #getService(EDataType)} requires to access
     *                   any other resource set or created in the constructor
     * @see <a href="JEP 447">https://openjdk.org/jeps/447</a>
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
        Arrays.stream(EDataType.values()).forEach(this::warmUp);
    }

    private void warmUp(@NonNull EDataType dataType) {
        try {
            getService(dataType);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Warming up " + dataType + " failed. Recommended Action: Call super(true) constructor to skip warming up in during construction if getting the service relies on external resources set during construction. Exception message: " + e.getMessage(), e);
        }
    }

    public abstract T getService(@NonNull EDataType dataType);
}
