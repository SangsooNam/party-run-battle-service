package online.partyrun.partyrunbattleservice.domain.battle.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleCreateRequest;
import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleMapper;
import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleResponse;
import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleStartTimeResponse;
import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.battle.entity.BattleStatus;
import online.partyrun.partyrunbattleservice.domain.battle.event.RunnerRunningEvent;
import online.partyrun.partyrunbattleservice.domain.battle.exception.BattleNotFoundException;
import online.partyrun.partyrunbattleservice.domain.battle.exception.ReadyBattleNotFoundException;
import online.partyrun.partyrunbattleservice.domain.battle.exception.RunnerAlreadyRunningInBattleException;
import online.partyrun.partyrunbattleservice.domain.battle.repository.BattleDao;
import online.partyrun.partyrunbattleservice.domain.battle.repository.BattleRepository;
import online.partyrun.partyrunbattleservice.domain.runner.entity.Runner;
import online.partyrun.partyrunbattleservice.domain.runner.entity.RunnerStatus;
import online.partyrun.partyrunbattleservice.domain.runner.service.RunnerService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BattleService {

    BattleMapper battleMapper;
    BattleRepository battleRepository;
    RunnerService runnerService;
    ApplicationEventPublisher eventPublisher;
    BattleDao battleDao;
    Clock clock;

    public BattleResponse createBattle(BattleCreateRequest request) {
        final List<String> runnerIds = request.getRunnerIds();
        final List<Runner> runners = runnerService.findAllById(runnerIds);

        validateRunnerInBattle(runners);
        final Battle battle = battleRepository.save(new Battle(request.getDistance(), runners));
        return battleMapper.toResponse(battle);
    }

    private void validateRunnerInBattle(List<Runner> runners) {
        final List<Battle> runningBattle =
                battleRepository.findByStatusInAndRunnersIn(
                        List.of(BattleStatus.READY, BattleStatus.RUNNING), runners);
        if (!runningBattle.isEmpty()) {
            throw new RunnerAlreadyRunningInBattleException(runners, runningBattle);
        }
    }

    public BattleResponse getReadyBattle(String runnerId) {
        final Battle battle =
                battleRepository
                        .findByStatusAndRunnersId(BattleStatus.READY, runnerId)
                        .orElseThrow(() -> new ReadyBattleNotFoundException(runnerId));

        return battleMapper.toResponse(battle);
    }

    public void setRunnerRunning(String battleId, String runnerId) {
        final Battle battle = findBattle(battleId);
        battle.changeRunnerStatus(runnerId, RunnerStatus.RUNNING);

        battleDao.updateRunnerStatus(battleId, runnerId, battle.getRunnerStatus(runnerId));
        eventPublisher.publishEvent(new RunnerRunningEvent(battleId, battle.getNumberOfRunners()));
    }

    private Battle findBattle(String battleId) {
        return battleRepository
                .findById(battleId)
                .orElseThrow(() -> new BattleNotFoundException(battleId));
    }

    public BattleStartTimeResponse setBattleRunning(String battleId) {
        final Battle battle = findBattle(battleId);
        battle.changeBattleStatus(BattleStatus.RUNNING);

        final LocalDateTime now = LocalDateTime.now(clock);
        battle.setStartTime(now);
        battleRepository.save(battle);

        return new BattleStartTimeResponse(battle.getStartTime());
    }
}
