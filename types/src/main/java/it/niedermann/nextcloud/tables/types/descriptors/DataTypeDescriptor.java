package it.niedermann.nextcloud.tables.types.descriptors;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.types.creators.ColumnCreator;
import it.niedermann.nextcloud.tables.types.editor.factories.EditorFactory;
import it.niedermann.nextcloud.tables.types.interceptors.Interceptor;
import it.niedermann.nextcloud.tables.types.interceptors.NoOpInterceptor;
import it.niedermann.nextcloud.tables.types.manager.factories.ManageFactory;
import it.niedermann.nextcloud.tables.types.manager.factories.unknown.UnknownManagerFactory;
import it.niedermann.nextcloud.tables.types.viewer.ViewHolderFactory;

public abstract class DataTypeDescriptor {

    @NonNull
    protected final ViewHolderFactory viewHolderFactory;
    @NonNull
    protected final EditorFactory editorFactory;
    @NonNull
    protected final Interceptor interceptor;
    @NonNull
    protected final ColumnCreator columnCreator;
    @NonNull
    protected final ManageFactory manageFactory;

    protected DataTypeDescriptor(
            @NonNull ViewHolderFactory viewHolderFactory,
            @NonNull EditorFactory editorFactory,
            @NonNull ColumnCreator columnCreator) {
        this(viewHolderFactory, editorFactory, columnCreator, new UnknownManagerFactory(), new NoOpInterceptor());
    }

    protected DataTypeDescriptor(
            @NonNull ViewHolderFactory viewHolderFactory,
            @NonNull EditorFactory editorFactory,
            @NonNull ColumnCreator columnCreator,
            @NonNull UnknownManagerFactory managerFactory) {
        this(viewHolderFactory, editorFactory, columnCreator, managerFactory, new NoOpInterceptor());
    }

    protected DataTypeDescriptor(
            @NonNull ViewHolderFactory viewHolderFactory,
            @NonNull EditorFactory editorFactory,
            @NonNull ColumnCreator columnCreator,
            @NonNull ManageFactory manageFactory,
            @NonNull Interceptor interceptor) {
        this.viewHolderFactory = viewHolderFactory;
        this.editorFactory = editorFactory;
        this.columnCreator = columnCreator;
        this.manageFactory = manageFactory;
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

    @NonNull
    public ColumnCreator getColumnCreator() {
        return this.columnCreator;
    }

    @NonNull
    public ManageFactory getManageFactory() {
        return this.manageFactory;
    }
}
