package sprint.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sprint.server.domain.groupmember.GroupMember;
import sprint.server.domain.groupmember.GroupMemberId;
import sprint.server.domain.groupmember.GroupMemberState;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {
    Boolean existsByGroupMemberIdAndMemberState(GroupMemberId groupMemberId, GroupMemberState groupMemberState);
    Optional<GroupMember> findByGroupMemberIdAndMemberState(GroupMemberId groupMemberId, GroupMemberState groupMemberState);

    @Query("select gm from GroupMember gm where gm.groupMemberId.groupId = :groupId and " +
            "(gm.memberState = sprint.server.domain.groupmember.GroupMemberState.ACCEPT or " +
            "gm.memberState = sprint.server.domain.groupmember.GroupMemberState.LEADER)")
    List<GroupMember> findGroupMemberByGroupId(@Param("groupId") Integer groupId);
}
