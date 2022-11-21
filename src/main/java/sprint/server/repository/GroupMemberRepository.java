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
    boolean existsByGroupMemberIdAndGroupMemberState(GroupMemberId groupMemberId, GroupMemberState groupMemberState);

    @Query("select gm from GroupMember gm where gm.groupMemberId.groupId = :groupId and " +
            "(gm.groupMemberState = sprint.server.domain.groupmember.GroupMemberState.ACCEPT or " +
            "gm.groupMemberState = sprint.server.domain.groupmember.GroupMemberState.LEADER)")
    List<GroupMember> findAllMemberByGroupId(@Param("groupId") Integer groupId);

    @Query("select gm from GroupMember gm where gm.groupMemberId.groupId = :groupId and "+
            "gm.groupMemberState = sprint.server.domain.groupmember.GroupMemberState.REQUEST")
    List<GroupMember> findRequestGroupMemberByGroupId(@Param("groupId") Integer groupId);
    @Query("select gm from GroupMember gm where gm.groupMemberId.memberId = :memberId and " +
            "(gm.groupMemberState = sprint.server.domain.groupmember.GroupMemberState.ACCEPT or " +
            "gm.groupMemberState = sprint.server.domain.groupmember.GroupMemberState.LEADER)")
    List<GroupMember> findJoinedGroupByMemberId(@Param("memberId") Long memberId);

    @Query("select gm from GroupMember gm where gm.groupMemberId.memberId = :memberId and "+
            "gm.groupMemberState = sprint.server.domain.groupmember.GroupMemberState.REQUEST")
    List<GroupMember> findRequestGroupMemberByMemberId(@Param("memberId") Long memberId);
    @Query("select gm from GroupMember gm where gm.groupMemberId.groupId = :groupId and " +
            "gm.groupMemberState = sprint.server.domain.groupmember.GroupMemberState.LEADER")
    Optional<GroupMember> findGroupLeaderByGroupId(@Param("groupId") Integer groupId);

    @Query("select gm from GroupMember gm where gm.groupMemberId.groupId = :groupId and " +
            "gm.groupMemberState = sprint.server.domain.groupmember.GroupMemberState.REQUEST")
    List<GroupMember> findGroupRequestByGroupId(@Param("groupId") Integer groupId);

    @Query("select gm from GroupMember gm where gm.groupMemberId.memberId = :memberId and " +
            "gm.groupMemberState = :state")
    List<GroupMember> findByMemberIdAndState(@Param("memberId") Long memberId, @Param("state") GroupMemberState state);

    @Query("select gm from GroupMember gm where gm.groupMemberId.memberId = :memberId and " +
            "gm.groupMemberId.groupId = :groupId")
    Optional<GroupMember> findByGroupIdAndMemberId(@Param("groupId") Integer groupId, @Param("memberId") Long memberId);
}
