package sprint.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.domain.Groups;
import sprint.server.repository.GroupRepository;


@Service
@RequiredArgsConstructor
public class GroupsService {

    private final GroupRepository groupRepository;

    @Transactional
    public int addGroup(Groups groups){
        groupRepository.save(groups);
        return groups.getGroupId();
    }
}
