package app.ruzi.service.app.referrer;

import app.ruzi.configuration.jwt.JwtUtils;
import app.ruzi.entity.app.CartSession;
import app.ruzi.entity.app.Referral;
import app.ruzi.entity.app.Referrer;
import app.ruzi.repository.app.ReferralRepository;
import app.ruzi.repository.app.ReferrerRepository;
import app.ruzi.service.mappers.ReferrerMapper;
import app.ruzi.service.payload.app.ReferrerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReferrerService {
    private final JwtUtils jwtUtils;

    private final ReferrerRepository referrerRepository;
    private final ReferralRepository referralRepository;

    /**
     * client boyicha barcha mijozlarni berish
     */
    @Transactional(readOnly = true)
    public List<Referrer> getAllReferrers() {
        return referrerRepository.findAll();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void createBonus(CartSession cart, int percent) {

        if (cart.getReferrer() == null) return;
        if (percent <= 0) return;

        BigDecimal bonus = cart.getTotalAmount()
                .multiply(BigDecimal.valueOf(percent))
                .divide(BigDecimal.valueOf(100));

        Referral rf = Referral.builder()
                .client(cart.getClient())
                .cartSession(cart)
                .referrer(cart.getReferrer())
                .bonusAmount(bonus)
                .bonusPercent(BigDecimal.valueOf(percent))
                .status(Referral.Status.PENDING)
                .build();

        referralRepository.save(rf);
    }

    @Transactional
    public void create(ReferrerDto referrerDto) {
        Referrer referrer = ReferrerMapper.INSTANCE.toEntity(referrerDto);
        referrer.setReferrerCode(generateEasyReferrerCode(referrerDto.getFullName()));
        referrerRepository.save(referrer);
    }

    private String generateEasyReferrerCode(String fullName) {
        String prefix = fullName
                .replaceAll("[^A-Za-zА-Яа-яЎўҚқҒғҲҳ]", "")
                .toUpperCase();
        if (prefix.length() >= 3) {
            prefix = prefix.substring(0, 3);
        }
        int number = (int) (Math.random() * 900) + 100; // 100–999
        return prefix + number;
    }
}
