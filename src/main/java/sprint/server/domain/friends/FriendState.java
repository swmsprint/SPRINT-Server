package sprint.server.domain.friends;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum FriendState {
    REQUEST, ACCEPT, REJECT, CANCEL, DELETE, NOT_FRIEND, RECEIVE;
    // NOT_FRIEND & RECEIVE is only for Api response
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
