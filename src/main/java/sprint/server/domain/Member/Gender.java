package sprint.server.domain.Member;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Gender {
    MALE, FEMALE, X;

    @JsonCreator
    public static Gender fromGender(String input) {
        for (Gender gender : Gender.values()) {
            if(gender.name().equals(input)) {
                return gender;
            }
        }
        return null;
    }
}
