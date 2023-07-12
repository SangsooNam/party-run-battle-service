package online.partyrun.partyrunbattleservice.domain.record.exception;

import online.partyrun.partyrunbattleservice.global.exception.BadRequestException;

public class RunnerIsNotRunningException extends BadRequestException {
    public RunnerIsNotRunningException(String runnerId) {
        super(String.format("Runner %s 는 RUNNING 상태가 아닙니다.", runnerId));
    }
}
