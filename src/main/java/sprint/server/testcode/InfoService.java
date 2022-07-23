package sprint.server.testcode;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InfoService {

    private final InfoRepository infoRepository;

    @Transactional
    public Long join(Info info){
        infoRepository.save(info);
        return info.getId();
    }
}

