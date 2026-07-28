package com.klyndyuk.authservice.utils;

import com.klyndyuk.authservice.dto.request.RefreshRequest;

public final class TestRefreshRequests {

    private TestRefreshRequests() {
    }

    public static RefreshRequest createRefreshRequest() {
        RefreshRequest request = new RefreshRequest();
        request.setToken(TestConstants.REFRESH_TOKEN);
        return request;
    }
}
