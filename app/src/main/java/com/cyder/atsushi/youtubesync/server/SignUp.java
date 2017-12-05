package com.cyder.atsushi.youtubesync.server;

import com.cyder.atsushi.youtubesync.json_data.BaseResponse;

/**
 * Created by atsushi on 2017/10/27.
 */

public class SignUp extends HttpRequestsHelper {
    private SignUpInterface listener = null;

    public void setListener(SignUpInterface _listener) {
        listener = _listener;
    }

    public SignUp() {
        super(BaseResponse.class);
    }

    public void post(final String email, final String name, final String password) {
        super.post(new com.cyder.atsushi.youtubesync.json_data.SignUp(email, name, password), "users", new HttpRequestCallback() {
            @Override
            public void success(Object response) {
                BaseResponse r = (BaseResponse)response;
                listener.onSignedUp(r.user);
            }

            @Override
            public void failure() {
                listener.onSignUpFailed();
            }
        });
    }
}
