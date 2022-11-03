package sprint.server.domain.member;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Provider {
    KAKAO, GOOGLE, APPLE;

    @JsonCreator
    public static Provider fromProvider(String input) {
        for (Provider provider : Provider.values()) {
            if(provider.name().equals(input)) {
                return provider;
            }
        }
        return null;
    }
}
