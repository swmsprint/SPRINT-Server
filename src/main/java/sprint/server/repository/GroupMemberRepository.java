package sprint.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sprint.server.domain.Groups;
import sprint.server.domain.groupmember.GroupMember;
import sprint.server.domain.groupmember.GroupMemberId;
import sprint.server.domain.groupmember.GroupMemberState;
import sprint.server.domain.member.Member;

import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {
    Boolean existsByGroupMemberIdAndMemberState(GroupMemberId groupMemberId, GroupMemberState groupMemberState);
    Optional<GroupMember> findByGroupMemberIdAndMemberState(GroupMemberId groupMemberId, GroupMemberState groupMemberState);
}
