package sprint.server.repository;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import sprint.server.testcode.Info;
import sprint.server.testcode.InfoRepository;


@RunWith(SpringRunner.class)
@SpringBootTest
@Rollback(value = false)
public class InfoRepositoryTest {
    @Autowired
    InfoRepository infoRepository;

    @Test
    @Transactional
    public void testInfo() throws Exception{
        Info info = new Info();
        info.setContent("hello worldffggf");

        Long saveId = infoRepository.save(info);
        Info findInfo = infoRepository.find(saveId);

        Assertions.assertThat(findInfo.getId()).isEqualTo(info.getId());
        Assertions.assertThat(findInfo.getContent()).isEqualTo(info.getContent());

    }
}
