package it.niedermann.nextcloud.tables.types.descriptors.usergroup;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.tables.types.creators.ColumnCreator;
import it.niedermann.nextcloud.tables.types.creators.type.UserGroupCreator;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.defaults.supplier.usergroup.UserGroupDefaultSupplier;
import it.niedermann.nextcloud.tables.types.descriptors.DataTypeDescriptor;
import it.niedermann.nextcloud.tables.types.editor.factories.EditorFactory;
import it.niedermann.nextcloud.tables.types.editor.factories.unknown.UnknownEditorFactory;
import it.niedermann.nextcloud.tables.types.viewer.ViewHolderFactory;
import it.niedermann.nextcloud.tables.types.viewer.factories.usergroup.UserGroupFactory;

public class UserGroupDescriptor extends DataTypeDescriptor {

    public UserGroupDescriptor() {
        this(new UserGroupDefaultSupplier());
    }

    private UserGroupDescriptor(@NonNull DefaultValueSupplier defaultValueSupplier) {
        this(new UserGroupFactory(defaultValueSupplier),
                new UnknownEditorFactory(defaultValueSupplier),
                new UserGroupCreator());
    }

    private UserGroupDescriptor(
            @NonNull ViewHolderFactory viewHolderFactory,
            @NonNull EditorFactory editorFactory,
            @NonNull ColumnCreator columnCreator) {
        super(viewHolderFactory, editorFactory, columnCreator);
    }
}
