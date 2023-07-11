package online.partyrun.partyrunbattleservice.domain.battle.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

import java.time.LocalDateTime;

public class InvalidNowTimeException extends BadRequestException {
    public InvalidNowTimeException(LocalDateTime now) {
        super(String.format("현재 시각은 %s일 수 없습니다.", now));
    }
}
