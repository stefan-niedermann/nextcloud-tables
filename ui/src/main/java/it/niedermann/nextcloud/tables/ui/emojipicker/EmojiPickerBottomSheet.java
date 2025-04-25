package it.niedermann.nextcloud.tables.ui.emojipicker;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.emoji2.emojipicker.EmojiViewItem;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import it.niedermann.nextcloud.tables.ui.databinding.ViewEmojipickerBinding;

public class EmojiPickerBottomSheet extends BottomSheetDialogFragment {

    private ViewEmojipickerBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (!(requireActivity() instanceof EmojiPickerListener listener)) {
            throw new IllegalArgumentException("Calling " + Activity.class.getSimpleName() + " must implement " + EmojiPickerListener.class.getSimpleName());
        }

        binding = ViewEmojipickerBinding.inflate(inflater, container, false);
        binding.emojiPicker.setOnEmojiPickedListener(emoji -> {
            listener.onEmojiPicked(emoji);
            dismiss();
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        this.binding = null;
        super.onDestroyView();
    }

    @FunctionalInterface
    public interface EmojiPickerListener {
        void onEmojiPicked(EmojiViewItem emoji);
    }
}
