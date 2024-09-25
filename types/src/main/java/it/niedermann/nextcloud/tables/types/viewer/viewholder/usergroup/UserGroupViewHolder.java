package it.niedermann.nextcloud.tables.types.viewer.viewholder.usergroup;

import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.tables.database.entity.Column;
import it.niedermann.nextcloud.tables.types.databinding.TableviewCellRichBinding;
import it.niedermann.nextcloud.tables.types.defaults.DefaultValueSupplier;
import it.niedermann.nextcloud.tables.types.viewer.CellViewHolder;

public class UserGroupViewHolder extends CellViewHolder {

    protected final TableviewCellRichBinding binding;

    public UserGroupViewHolder(@NonNull TableviewCellRichBinding binding,
                               @NonNull DefaultValueSupplier defaultValueSupplier) {
        super(binding.getRoot(), defaultValueSupplier);
        this.binding = binding;
    }

    @Override
    public void bind(@NonNull JsonElement value, @NonNull Column column) {
        final var userNames = getUserNames(value)
                .stream()
                .map(userName -> "@" + userName)
                .collect(Collectors.joining(", "));

        // TODO
        // binding.rich.setCurrentSingleSignOnAccount();
        binding.rich.setMarkdownString(userNames);

        binding.rich.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.rich.requestLayout();

        binding.getRoot().getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
        binding.getRoot().requestLayout();
    }

    @NonNull
    private Collection<String> getUserNames(@NonNull JsonElement value) {
        if (!value.isJsonArray()) {
            return Collections.emptySet();
        }

        return value
                .getAsJsonArray()
                .asList()
                .stream()
                .filter(JsonElement::isJsonObject)
                .map(JsonElement::getAsJsonObject)
                .filter(user -> user.has("id"))
                .map(user -> user.get("id"))
                .filter(JsonElement::isJsonPrimitive)
                .map(JsonElement::getAsString)
                .collect(Collectors.toUnmodifiableSet());
    }
}
