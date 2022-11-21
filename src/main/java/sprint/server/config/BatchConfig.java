package sprint.server.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sprint.server.domain.member.Member;
import sprint.server.repository.MemberRepository;
import sprint.server.service.MemberService;

import java.util.List;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {
    public final JobBuilderFactory jobBuilderFactory;
    public final StepBuilderFactory stepBuilderFactory;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    @Bean
    public Job job() {
        return jobBuilderFactory.get("job")
                .start(step())
                .build();
    }

    @Bean
    public Step step() {
        return stepBuilderFactory.get("step")
                .tasklet((contribution, chunkContext) -> {
                    log.info("===== 삭제 배치 시작 =====");
                    List<Member> disabledMembers = memberRepository.findDisableMembers();
                    if (!disabledMembers.isEmpty() && disabledMembers != null) {
                        log.info("delete member size : " + disabledMembers.size());
                        for (Member member : disabledMembers) {
                            memberService.deleteMember(member);
                        }
                    } else {
                        log.info("there is nothing to remove from database");
                    }
                    log.info("===== 삭제 배치 완료 =====");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
