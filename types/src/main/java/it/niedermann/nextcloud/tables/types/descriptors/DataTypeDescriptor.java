package it.niedermann.nextcloud.tables.types.descriptors;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.types.editor.EditorFactory;
import it.niedermann.nextcloud.tables.types.interceptors.Interceptor;
import it.niedermann.nextcloud.tables.types.interceptors.NoOpInterceptor;
import it.niedermann.nextcloud.tables.types.viewer.ViewHolderFactory;

public abstract class DataTypeDescriptor {

    @NonNull
    protected final ViewHolderFactory viewHolderFactory;
    @NonNull
    protected final EditorFactory editorFactory;
    @NonNull
    protected final Interceptor interceptor;

    protected DataTypeDescriptor(
            @NonNull ViewHolderFactory viewHolderFactory,
            @NonNull EditorFactory editorFactory) {
        this(viewHolderFactory, editorFactory, new NoOpInterceptor());
    }

    protected DataTypeDescriptor(
            @NonNull ViewHolderFactory viewHolderFactory,
            @NonNull EditorFactory editorFactory,
            @NonNull Interceptor interceptor) {
        this.viewHolderFactory = viewHolderFactory;
        this.editorFactory = editorFactory;
        this.interceptor = interceptor;
    }

    @NonNull
    public ViewHolderFactory getViewHolderFactory() {
        return viewHolderFactory;
    }

    @NonNull
    public EditorFactory getEditorFactory() {
        return editorFactory;
    }

    @NonNull
    public Interceptor getInterceptor() {
        return this.interceptor;
    }
}
