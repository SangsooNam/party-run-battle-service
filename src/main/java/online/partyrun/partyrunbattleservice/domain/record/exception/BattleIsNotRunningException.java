package online.partyrun.partyrunbattleservice.domain.record.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

public class BattleIsNotRunningException extends BadRequestException {
    public BattleIsNotRunningException(String battleId) {
        super(String.format("Battle %s 는 RUNNING 상태가 아닙니다.", battleId));
    }
}
