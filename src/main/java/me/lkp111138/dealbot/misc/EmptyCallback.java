package me.lkp111138.dealbot.misc;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;

import java.io.IOException;

public class EmptyCallback<T extends BaseRequest<T, R>, R extends BaseResponse> implements Callback<T, R> {
    @Override
    public void onResponse(T request, R response) {

    }

    @Override
    public void onFailure(T request, IOException e) {

    }
}
