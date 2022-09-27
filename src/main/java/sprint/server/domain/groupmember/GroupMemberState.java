package sprint.server.domain.groupmember;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum GroupMemberState {
    REQUEST, ACCEPT, LEAVE, LEADER, REJECT, CANCEL;

    @JsonCreator
    public static GroupMemberState fromGroupMemberState(String input) {
        for (GroupMemberState groupMemberState : GroupMemberState.values()) {
            if(groupMemberState.name().equals(input)) {
                return groupMemberState;
            }
        }
        return null;
    }
}
