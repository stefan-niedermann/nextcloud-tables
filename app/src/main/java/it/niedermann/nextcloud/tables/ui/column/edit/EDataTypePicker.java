package it.niedermann.nextcloud.tables.ui.column.edit;

import static java.util.stream.Collectors.toUnmodifiableSet;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.tables.R;
import it.niedermann.nextcloud.tables.database.model.EDataType;
import it.niedermann.nextcloud.tables.databinding.ViewDatatypePickerBinding;

public class EDataTypePicker extends FrameLayout {

    private ViewDatatypePickerBinding binding;

    private final MutableLiveData<String> selectedType$ = new ReactiveLiveData<String>().distinctUntilChanged();
    private final MutableLiveData<String> selectedSubType$ = new ReactiveLiveData<String>().distinctUntilChanged();
    private final LiveData<EDataType> selectedDataType$ = new ReactiveLiveData<>(selectedType$)
            .combineWith(() -> selectedSubType$)
            .map(typePair -> {
                try {
                    return EDataType.findByType(typePair.first, typePair.second);
                } catch (Exception e) {
                    return EDataType.UNKNOWN;
                }
            })
            .filter(type -> type != EDataType.UNKNOWN)
            .distinctUntilChanged();

    public EDataTypePicker(Context context) {
        super(context);
        onCreate();
    }

    public EDataTypePicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        onCreate();
    }

    public EDataTypePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onCreate();
    }

    public EDataTypePicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        onCreate();
    }

    private void onCreate() {
        binding = ViewDatatypePickerBinding.inflate(LayoutInflater.from(getContext()));

        final var typeAdapter = new ArrayAdapter<String>(getContext(), R.layout.item_option);
        final var subTypeAdapter = new ArrayAdapter<String>(getContext(), R.layout.item_option);

        typeAdapter.addAll(EDataType.getTypes());
        binding.type.setAdapter(typeAdapter);
        binding.type.setOnItemClickListener((adapterView, view, position, l) -> {
            final var newType = typeAdapter.getItem(position);
            selectedType$.postValue(newType);
            subTypeAdapter.clear();
            if (newType != null) {
                final var typeVariants = EDataType.getTypeVariants(newType);

                if (typeVariants.isEmpty()) {
                    selectedSubType$.postValue(null);
                    binding.subTypeWrapper.setVisibility(View.GONE);

                } else if (typeVariants.size() == 1) {
                    final var subType = typeVariants
                            .stream()
                            .findAny()
                            .flatMap(EDataType::getSubType)
                            .orElseThrow();

                    subTypeAdapter.addAll(subType);
                    selectedSubType$.postValue(null);
                    binding.subTypeWrapper.setVisibility(View.GONE);

                } else {
                    final var subTypes = typeVariants
                            .stream()
                            .map(EDataType::getSubType)
                            .map(subType -> subType.orElse(""))
                            .collect(toUnmodifiableSet());

                    subTypeAdapter.addAll(subTypes);
                    binding.subTypeWrapper.setVisibility(View.VISIBLE);
                }
            }
            subTypeAdapter.notifyDataSetChanged();
        });

        binding.subType.setAdapter(subTypeAdapter);
        binding.subType.setOnItemClickListener((adapterView, view, position, l) -> selectedSubType$.postValue(subTypeAdapter.getItem(position)));

        addView(binding.getRoot());
    }

    @NonNull
    public LiveData<EDataType> getDataType$() {
        return this.selectedDataType$;
    }
}
