package sprint.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.domain.usermatch.MatchStatus;
import sprint.server.domain.usermatch.UserMatchApply;
import sprint.server.domain.usermatch.UserMatchApplyId;
import sprint.server.repository.UserMatchApplyRepository;

import java.sql.Timestamp;
import java.util.Calendar;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserMatchService {


    private final UserMatchApplyRepository userMatchApplyRepository;

    @Transactional
    public UserMatchApply saveUserApplyMatchInfo(Long memberId, Calendar calendar){

        Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());

        UserMatchApplyId applyId = new UserMatchApplyId(memberId,timestamp);
        UserMatchApply userMatchApply = new UserMatchApply(applyId, MatchStatus.WAIT);

        return userMatchApplyRepository.save(userMatchApply);
    }


    public void matchingApplyUser(){

    }
}
