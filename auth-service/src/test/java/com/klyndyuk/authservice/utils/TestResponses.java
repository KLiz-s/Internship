package com.klyndyuk.authservice.utils;

import com.klyndyuk.authservice.dto.response.TwoTokenResponse;

public final class TestResponses {

    private TestResponses() {
    }

    public static TwoTokenResponse createTwoTokenResponse() {
        return new TwoTokenResponse(
                TestConstants.ACCESS_TOKEN,
                TestConstants.REFRESH_TOKEN
        );
    }
}
