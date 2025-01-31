package it.niedermann.nextcloud.tables.util;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Optional;

import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.entity.LinkValue;

public class UserGroupUtil {

    /// @noinspection DataFlowIssue
    @Nullable
    public static String getLinkAsDisplayValue(@NonNull Context context, @NonNull LinkValue linkValue) {
        final var value = Optional.of(linkValue)
                .map(LinkValue::getValue)
                .map(Uri::toString)
                .orElse(null);

        final var title = Optional.of(linkValue)
                .map(LinkValue::getTitle);

        return title
                .map(s -> context.getString(R.string.format_text_link, s, value))
                .orElse(value);
    }
}
