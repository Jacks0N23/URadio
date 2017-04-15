package com.jassdev.apps.andrroider.uradio.Chat;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jassdev.apps.andrroider.uradio.databinding.ChatFragmentBinding;

/**
 * Created by Jackson on 15/01/2017.
 */

public class ChatFragment extends Fragment {

    private ChatFragmentBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mBinding = ChatFragmentBinding.inflate(inflater, container, false);
        mBinding.openChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent telegram = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/uradio_pro"));
                startActivity(telegram);
            }
        });
        return mBinding.getRoot();
    }

}
