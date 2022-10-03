package sprint.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.domain.usermatch.MatchStatus;
import sprint.server.domain.usermatch.UserMatch;
import sprint.server.domain.usermatch.UserMatchApply;
import sprint.server.domain.usermatch.UserMatchApplyId;
import sprint.server.repository.UserMatchApplyRepository;
import sprint.server.repository.UserMatchRepository;

import java.sql.Timestamp;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserMatchService {


    private final UserMatchApplyRepository userMatchApplyRepository;
    private final UserMatchRepository userMatchRepository;

    @Transactional
    public UserMatchApply saveUserApplyMatchInfo(Long memberId, Calendar calendar){

        Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());

        UserMatchApplyId applyId = new UserMatchApplyId(memberId,timestamp);
        //랜덤으로 스코어 값을 넣어준다
        long score = (long)(Math.random()* 1000_000_000);
        UserMatchApply userMatchApply = new UserMatchApply(applyId, MatchStatus.WAIT,score);

        return userMatchApplyRepository.save(userMatchApply);
    }


    public void matchingApplyUser(){

        List<UserMatchApply> applyList = userMatchApplyRepository
                .findAllByMatchStatusOrderByScoreDesc(MatchStatus.WAIT);

        Timestamp matchingTime = new Timestamp(System.currentTimeMillis());
        int applyMemberCount = applyList.size();
        int matchingNumber = 0;
        for(int i=0; i<applyMemberCount; i++){
            if(i%5 == 0){
                matchingNumber++;
            }
            UserMatch userMatch = new UserMatch();
            userMatch.setMemberId(applyList.get(i).getMatchId().getMemberId());
            userMatch.setMatchTime(matchingTime);
            userMatch.setTeamNumber(matchingNumber);
            userMatchRepository.save(userMatch);
        }


    }
}
