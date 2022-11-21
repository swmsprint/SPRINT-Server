package sprint.server.domain.member;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class ProviderPK {
    @Enumerated(EnumType.STRING)
    private Provider provider;
    private String providerUID;
}
