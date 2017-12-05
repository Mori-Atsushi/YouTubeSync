package com.cyder.atsushi.youtubesync;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.cyder.atsushi.youtubesync.animations.ResizeAnimation;

/**
 * Created by atsushi on 2017/11/27.
 */

public class ChatFormFragment extends Fragment {
    private View chatFormArea;
    private ChatFormFragment.ChatFormFragmentListener listener = null;
    private InputMethodManager manager;
    private int originalHeight;

    public interface ChatFormFragmentListener {
        void onSendChat(String message);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (ChatFormFragment.ChatFormFragmentListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.chat_form_fragment, container, false);
        chatFormArea = rootView.findViewById(R.id.chat_form_area);

        final EditText chatForm = rootView.findViewById(R.id.chat_form);
        final ImageButton chatSubmit = rootView.findViewById(R.id.chat_submit);

        chatSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = chatForm.getText().toString();
                if (!message.equals("")) {
                    listener.onSendChat(message);
                    chatForm.getEditableText().clear();
                }
            }
        });

        originalHeight = dpToPx(getResources().getInteger(R.integer.chat_form_height));
        manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        return rootView;
    }

    public void setChatFormArea(Fragment nowFragment) {
        if (chatFormArea != null) {
            final ResizeAnimation animation;
            if (nowFragment instanceof ChatFragment) {
                chatFormArea.setVisibility(View.VISIBLE);
                animation = new ResizeAnimation(chatFormArea, originalHeight, 0);
            } else {
                if (getView() != null) {
                    manager.hideSoftInputFromWindow(getView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                animation = new ResizeAnimation(chatFormArea, -chatFormArea.getHeight(), chatFormArea.getHeight());
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation arg0) {
                        chatFormArea.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }
            animation.setDuration(getResources().getInteger(R.integer.chat_form_animation_time));
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            chatFormArea.startAnimation(animation);
        }
    }

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
