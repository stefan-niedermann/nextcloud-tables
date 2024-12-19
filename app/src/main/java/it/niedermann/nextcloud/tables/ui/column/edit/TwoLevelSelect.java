package it.niedermann.nextcloud.tables.ui.column.edit;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import it.niedermann.nextcloud.tables.databinding.ViewTwoLevelSelectBinding;

///
public abstract class TwoLevelSelect<FirstLevelType extends Serializable, SecondLevelType extends Serializable, ResultType> extends FrameLayout implements AdapterView.OnItemClickListener {

    private final ViewTwoLevelSelectBinding binding;
    private final ArrayAdapter<FirstLevelType> firstLevelAdapter;
    private final ArrayAdapter<SecondLevelType> secondLevelAdapter;

    @Nullable
    private Consumer<ResultType> listener = null;

    @Nullable
    private FirstLevelType firstLevel = null;

    @Nullable
    private SecondLevelType secondLevel = null;

    public TwoLevelSelect(@NonNull Context context,
                          @Nullable AttributeSet attrs,
                          @NonNull ArrayAdapter<FirstLevelType> firstLevelAdapter,
                          @NonNull ArrayAdapter<SecondLevelType> secondLevelAdapter) {
        super(context, attrs);

        this.firstLevelAdapter = firstLevelAdapter;
        this.secondLevelAdapter = secondLevelAdapter;

        binding = ViewTwoLevelSelectBinding.inflate(LayoutInflater.from(getContext()));

        binding.firstLevelWrapper.setHint(getFirstLevelHint());
        binding.secondLevelWrapper.setHint(getSecondLevelHint());

        binding.firstLevel.setAdapter(this.firstLevelAdapter);
        binding.firstLevel.setOnItemClickListener(this);

        binding.secondLevel.setAdapter(this.secondLevelAdapter);
        binding.secondLevel.setOnItemClickListener((adapterView, view, position, l) -> {

            if (firstLevel != null) {
                secondLevel = secondLevelAdapter.getItem(position);
                binding.secondLevel.setText(getSecondLevelLabel(secondLevel).orElse(null), false);
                Optional.ofNullable(listener)
                        .ifPresent(listener -> getResult(firstLevel, secondLevel).ifPresent(listener));
            }

        });

        addView(binding.getRoot());
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        final var parentState = super.onSaveInstanceState();
        final var args = new Bundle();
        args.putParcelable("parent", parentState);
        args.putSerializable("first", firstLevel);
        args.putSerializable("second", secondLevel);
        return parentState;
    }

    /// @noinspection unchecked
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle args) {
            super.onRestoreInstanceState(args.getParcelable("parent"));

            firstLevel = (FirstLevelType) args.getSerializable("first");
            secondLevel = (SecondLevelType) args.getSerializable("second");

            binding.firstLevel.setText(getFirstLevelLabel(firstLevel).orElse(null), false);
            binding.secondLevel.setText(getSecondLevelLabel(secondLevel).orElse(null), false);
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    public void setFirstLevelContent(@NonNull Collection<FirstLevelType> firstLevelContent) {
        firstLevelAdapter.clear();
        firstLevelAdapter.addAll(firstLevelContent);
        firstLevelAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        firstLevel = firstLevelAdapter.getItem(position);
        binding.firstLevel.setText(getFirstLevelLabel(firstLevel).orElse(null), false);

        secondLevelAdapter.clear();
        secondLevel = null;

        getSecondLevelContent(firstLevel).whenCompleteAsync((secondLevelContent, error) -> {
            if (firstLevel == null) {
                return;
            }

            if (secondLevelContent.isEmpty()) {
                binding.secondLevelWrapper.setVisibility(View.GONE);
                Optional.ofNullable(listener)
                        .ifPresent(listener -> getResult(firstLevel, secondLevel).ifPresent(listener));

            } else if (secondLevelContent.size() == 1) {
                binding.secondLevelWrapper.setVisibility(View.GONE);
                secondLevelAdapter.addAll(secondLevelContent);
                secondLevel = secondLevelAdapter.getItem(0);

                binding.secondLevel.setSelection(0);
                binding.secondLevel.setText(getSecondLevelLabel(secondLevel).orElse(null), false);
                Optional.ofNullable(listener)
                        .ifPresent(listener -> getResult(firstLevel, secondLevel).ifPresent(listener));

            } else {
                secondLevelAdapter.addAll(secondLevelContent);
                secondLevel = secondLevelAdapter.getItem(0);
                Optional.ofNullable(listener)
                        .ifPresent(listener -> getResult(firstLevel, secondLevel).ifPresent(listener));

                binding.secondLevel.setSelection(0);
                binding.secondLevel.setText(getSecondLevelLabel(secondLevel).orElse(null), false);
                binding.secondLevelWrapper.setVisibility(View.VISIBLE);
            }

            secondLevelAdapter.notifyDataSetChanged();
        }, ContextCompat.getMainExecutor(getContext()));
    }

    public void setOnChangeListener(@Nullable Consumer<ResultType> listener) {
        this.listener = listener;
    }

    @NonNull
    abstract protected CompletableFuture<Collection<SecondLevelType>> getSecondLevelContent(@Nullable FirstLevelType firstLevel);

    @NonNull
    abstract protected Optional<ResultType> getResult(@NonNull FirstLevelType firstLevel, @Nullable SecondLevelType secondLevel);

    @StringRes
    protected abstract int getFirstLevelHint();

    @StringRes
    protected abstract int getSecondLevelHint();

    @NonNull
    protected Optional<String> getFirstLevelLabel(@Nullable FirstLevelType firstLevel) {
        return Optional
                .ofNullable(firstLevel)
                .map(FirstLevelType::toString);
    }

    @NonNull
    protected Optional<String> getSecondLevelLabel(@Nullable SecondLevelType secondLevel) {
        return Optional
                .ofNullable(secondLevel)
                .map(SecondLevelType::toString);
    }
}
