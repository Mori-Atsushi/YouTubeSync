package com.example.atsushi.youtubesync.server;

import com.example.atsushi.youtubesync.json_data.Response;

import java.util.HashMap;

/**
 * Created by atsushi on 2017/11/07.
 */

public class Room extends HttpRequestsHelper {
    private RoomInterface listener = null;

    public Room() {
        super();
    }

    public void setListener(RoomInterface listener) {
        this.listener = listener;
    }

    public void get(final String roomKey) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("room_key", roomKey);
        super.get(params, "rooms", new HttpRequestCallback() {
            @Override
            public void call(Response response) {
                listener.onReceived(response.room);
            }
        });
    }
}
