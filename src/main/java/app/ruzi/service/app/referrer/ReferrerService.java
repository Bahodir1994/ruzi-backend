package app.ruzi.service.app.referrer;

import app.ruzi.configuration.jwt.JwtUtils;
import app.ruzi.entity.app.Referrer;
import app.ruzi.repository.app.ReferrerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReferrerService {
    private final JwtUtils jwtUtils;

    private final ReferrerRepository referrerRepository;

    /**
     * client boyicha barcha mijozlarni berish
     */
    public List<Referrer> getAllReferrers() {
        return referrerRepository.findAll();
    }
}
