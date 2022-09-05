package sprint.server.domain.friends;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum FriendState {
    REQUEST, ACCEPT, REJECT, CANCELED, DELETED, NOT_FRIEND;

    @JsonCreator
    public static FriendState fromFriendState(String input) {
        for (FriendState friendState : FriendState.values()) {
            if(friendState.name().equals(input)) {
                return friendState;
            }
        }
        return null;
    }
}
