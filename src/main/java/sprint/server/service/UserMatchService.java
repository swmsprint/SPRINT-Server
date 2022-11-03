package sprint.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.controller.datatransferobject.response.LeagueInfo;
import sprint.server.domain.member.Member;
import sprint.server.domain.statistics.Statistics;
import sprint.server.domain.statistics.StatisticsType;
import sprint.server.domain.usermatch.MatchStatus;
import sprint.server.domain.usermatch.UserMatch;
import sprint.server.domain.usermatch.UserMatchApply;
import sprint.server.domain.usermatch.UserMatchApplyId;
import sprint.server.repository.MemberRepository;
import sprint.server.repository.StatisticsRepository;
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
    private final StatisticsRepository statisticsRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public UserMatchApply saveUserApplyMatchInfo(Long memberId, Calendar calendar){

        Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());

        UserMatchApplyId applyId = new UserMatchApplyId(memberId,timestamp);
        //랜덤으로 스코어 값을 넣어준다
        long score = (long)(Math.random()* 1000_000_000);
        UserMatchApply userMatchApply = new UserMatchApply(applyId, MatchStatus.WAIT,score);

        return userMatchApplyRepository.save(userMatchApply);
    }

    @Transactional
    public void matchingApplyUser(){

        List<UserMatchApply> applyList = userMatchApplyRepository
                .findAllByMatchStatusOrderByScoreDesc(MatchStatus.WAIT);

        Timestamp matchingTime = new Timestamp(System.currentTimeMillis());

        int matchingNumber = 0;

        for(UserMatchApply apply: applyList){
            if(applyList.indexOf(apply)%5 == 0){
                matchingNumber++;
            }
            UserMatch userMatch = new UserMatch();
            userMatch.setMemberId(apply.getMatchId().getMemberId());
            userMatch.setMatchTime(matchingTime);
            userMatch.setTeamNumber(matchingNumber);
            userMatchRepository.save(userMatch);
        }

    }

    @Transactional
    public void finishLeague(Timestamp startTime){


        List<UserMatch> findAllByMatchTimeAfter = userMatchRepository.findAllByMatchTimeAfterOrderByMatchIdDesc(startTime);

        Integer maxTeamNumber = findAllByMatchTimeAfter.get(0).getTeamNumber();

        for(int i=0; i<maxTeamNumber; i++) {
            List<LeagueInfo> userMatchList = getLeagueInfosByTeamNumber(startTime, i + 1);
            userMatchList.stream().forEach(leagueInfo -> {
                UserMatch userMatch = userMatchRepository.findById(leagueInfo.getMatchId()).get();
                userMatch.setRanking(leagueInfo.getRanking());
                userMatch.setTotalScore(leagueInfo.getTotalScore());
                userMatch.setTotalDistance(leagueInfo.getTotalDistance());
                userMatch.setTotalCount(leagueInfo.getTotalCount());
            });
        }

    }


    /**
     * 조회즉시 리그 정보와 순위를 반환하는 메소드
     * @param member
     * @param startTime (리그에 포함되는 시작시간)
     * @return
     */
    public List<LeagueInfo> viewLeagueInfo(Member member, Timestamp startTime) {


        //해당 멤버의 이번주 매칭 정보를 가져옴
        Optional<UserMatch> userMatch = userMatchRepository.findAllByMemberIdAndMatchTimeBetween(member.getId(),startTime,
                new Timestamp(Calendar.getInstance().getTimeInMillis()));
        //부여받은 팀넘버 확인
        Integer teamNumber = userMatch.get().getTeamNumber();

        return getLeagueInfosByTeamNumber(startTime, teamNumber);
    }

    private List<LeagueInfo> getLeagueInfosByTeamNumber(Timestamp startTime, Integer teamNumber) {
        //매칭에 참여하는 멤버 리스트를 가져옴
        List<Member> matchingMemberIdList = findAllLeagueMember(teamNumber, startTime);

        //해당 멤버들을 바탕으로 리그정보를 만들어 낸다
        List<LeagueInfo> leagueInfoList = makeLeagueInfoList(matchingMemberIdList, teamNumber, startTime);
        return rankingLeague(leagueInfoList);
    }

    public List<LeagueInfo> rankingLeague(List<LeagueInfo> leagueInfoList){
        PriorityQueue<LeagueInfo> priorityQueue = new PriorityQueue<>(new Comparator<LeagueInfo>() {
            @Override
            public int compare(LeagueInfo o1, LeagueInfo o2) {
                return (o2.getTotalScore() - o1.getTotalScore());
            }
        });
        for(LeagueInfo leagueInfo : leagueInfoList) priorityQueue.add(leagueInfo);
        List<LeagueInfo> rankedLeagueInfoList = new ArrayList<>();
        while(!priorityQueue.isEmpty()){
            rankedLeagueInfoList.add(priorityQueue.poll());
        }
        for(int ranking = 0; ranking<leagueInfoList.size(); ranking++) {
            if(ranking != 0) {
                if (rankedLeagueInfoList.get(ranking-1).getTotalScore() == rankedLeagueInfoList.get(ranking).getTotalScore()){
                    rankedLeagueInfoList.get(ranking).setRanking(rankedLeagueInfoList.get(ranking-1).getRanking());
                }else rankedLeagueInfoList.get(ranking).setRanking(ranking+1);
            }else rankedLeagueInfoList.get(ranking).setRanking(ranking+1);
        }

        return rankedLeagueInfoList;
    }

    /**
     * 특정 시간 이후에 특정 팀넘버에 매칭된 멤버 리스트를 반환함
     * @param teamNumber
     * @param startTime
     * @return
     */
    public List<Member> findAllLeagueMember(Integer teamNumber, Timestamp startTime){
        //해당 팀넘버에 해당하는(참여하는) 매칭 정보를 모두 가져와
        List<UserMatch> userMatches = userMatchRepository.findAllByTeamNumberAndMatchTimeBetween(teamNumber,startTime,
                new Timestamp(Calendar.getInstance().getTimeInMillis()));
        //매칭에 참여하는 멤버 리스트를 가져옴
        List<Member> matchingMemberIdList = new ArrayList<>();
        for(UserMatch match : userMatches){
            matchingMemberIdList.add(memberRepository.findByIdAndDisableDayIsNull(match.getMemberId()).get());
        }

        return matchingMemberIdList;
    }

    /**
     * 멤버 리스트를 입력받아 모든 멤버들의 리그정보 리스트를 만들어 반환한다
     * @param matchingMemberIdList
     * @param teamNumber
     * @param startTime
     * @return
     */
    public List<LeagueInfo> makeLeagueInfoList(List<Member> matchingMemberIdList, Integer teamNumber, Timestamp startTime){
        List<LeagueInfo> leagueInfoList = new ArrayList<>();
        for(Member matchMember : matchingMemberIdList){
            leagueInfoList.add(userMatchInfo(matchMember,teamNumber, startTime));
        }
        return leagueInfoList;
    }

    /**
     * 특정 회원의 러닝 정보를 조회하여 리그정보를 만들어준다
     * @param member
     * @param teamNumber
     * @param startTime
     * @return
     */
    public LeagueInfo userMatchInfo(Member member, Integer teamNumber, Timestamp startTime){

        //리그 시작시간은 해당주 수요일
        Calendar leagueStartTime = Calendar.getInstance();
        leagueStartTime.setTime(startTime);
        leagueStartTime.set(Calendar.DAY_OF_WEEK,Calendar.WEDNESDAY);

        //현재시간을 종료시간으로
        Timestamp endTime = new Timestamp(Calendar.getInstance().getTimeInMillis());
        List<Statistics> dailyStatistics = statisticsRepository.findAllByStatisticsTypeAndMemberIdAndTimeBetween(StatisticsType.Daily, member.getId(),
                new Timestamp(leagueStartTime.getTimeInMillis()), endTime);

        Optional<UserMatch> userMatchInfo = userMatchRepository.findAllByMemberIdAndMatchTimeBetween(member.getId(), startTime, endTime);
        double distance =dailyStatistics.stream().mapToDouble(Statistics::getDistance).sum();
        int totalCount = dailyStatistics.stream().mapToInt(Statistics::getCount).sum();
        int totalScore = (int)(distance + totalCount*1000)/100;

        return new LeagueInfo(teamNumber,member.getId(),member.getNickname(),distance,totalCount,totalScore,0, userMatchInfo.get().getMatchId());
    }

}
