package com.chatting.chatting.websocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

@Service
@ServerEndpoint(value = "/api/ws/{channel}/{username}")
public class WebsoketEndpoint {
    private static Set<HashMap<String, Session>> CLIENTS = Collections.synchronizedSet(new HashSet<>());

    @OnOpen
    public void onOpen(Session session, @PathParam("channel") String channel, @PathParam("username") String username)
            throws IOException {
        HashMap<String, Session> users = new HashMap<>();
        users.put(channel, session);

        JSONObject json = new JSONObject();
        json.put("channel", channel);
        json.put("username", username);
        json.put("message", "님이 입장했습니다.");

        if (CLIENTS.contains(users)) {
            System.out.printf("channel : %s Session : %s  세션이 이미 존재 합니다.", channel, session);

        } else {
            CLIENTS.add(users);
            for (HashMap<String, Session> user : CLIENTS) {
                if (user.get(channel) != null) {
                    // System.out.println(json);
                    // user.get(channel).getAsyncRemote().sendObject(json);
                    user.get(channel).getAsyncRemote().sendText(json.toJSONString());
                }
            }
            System.out.printf("channel : %s Session : %s 세션을 추가합니다..", channel, session);

        }

    }

    @OnMessage
    public void OnMessage(Session session, String message)
            throws IOException, ParseException {
        System.out.println(message);

        JSONParser json = new JSONParser();
        JSONObject jsonobj = (JSONObject) json.parse(message);
        System.out.println(jsonobj.get("channel"));

        for (HashMap<String, Session> users : CLIENTS) {
            if (users.get(jsonobj.get("channel")) != null &&
                    users.get(jsonobj.get("channel")) != session) {

                users.get(jsonobj.get("channel")).getAsyncRemote().sendText(jsonobj.toJSONString());
            }

        }

    }

    @OnClose
    public void onClose(Session session, @PathParam("channel") String channel,
            @PathParam("username") String username)
            throws IOException {

        HashMap<String, Session> users = new HashMap<>();
        users.put(channel, session);
        CLIENTS.remove(users);

        JSONObject json = new JSONObject();
        json.put("channel", channel);
        json.put("username", username);
        json.put("message", "님이 퇴장했습니다.");

        for (HashMap<String, Session> user : CLIENTS) {
            if (user.get(channel) != null) {

                user.get(channel).getAsyncRemote().sendText(json.toJSONString());
            }
        }
        ;
        System.out.printf("channel : %s Session : %s  세션이 종료합니다", channel, session);
    }
}