package online.partyrun.partyrunbattleservice.domain.battle.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import online.partyrun.partyrunbattleservice.domain.battle.exception.*;
import online.partyrun.partyrunbattleservice.domain.runner.entity.Runner;
import online.partyrun.partyrunbattleservice.domain.runner.entity.RunnerStatus;
import online.partyrun.partyrunbattleservice.domain.runner.exception.RunnerNotFoundException;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Battle {
    static int MIN_DISTANCE = 1;

    @Id String id;
    int distance;
    List<Runner> runners;
    BattleStatus status;
    LocalDateTime startTime;
    @CreatedDate LocalDateTime createdAt;

    public Battle(int distance, List<Runner> runners) {
        validateDistance(distance);
        validateRunners(runners);
        this.distance = distance;
        this.runners = runners;
        this.status = BattleStatus.READY;
    }

    private void validateDistance(int distance) {
        if (distance < MIN_DISTANCE) {
            throw new InvalidDistanceException(distance, MIN_DISTANCE);
        }
    }

    private void validateRunners(List<Runner> runners) {
        if (Objects.isNull(runners) || runners.isEmpty()) {
            throw new InvalidRunnerNumberInBattleException();
        }
    }

    public void changeRunnerStatus(String runnerId, RunnerStatus runnerStatus) {
        validateIsFinishedStatus();
        final Runner runner = findRunner(runnerId);
        runner.changeStatus(runnerStatus);
    }

    private void validateIsFinishedStatus() {
        if (this.status.isFinished()) {
            throw new BattleAlreadyFinishedException(this.id);
        }
    }

    private Runner findRunner(String runnerId) {
        return runners.stream()
                .filter(runner -> runner.hasId(runnerId))
                .findFirst()
                .orElseThrow(() -> new RunnerNotFoundException(runnerId));
    }

    public void changeBattleStatus(BattleStatus status) {
        validateIsFinishedStatus();
        validateStatus(status);

        this.status = status;
    }

    private void validateStatus(BattleStatus status) {
        if (Objects.isNull(status) || status.isReady() || this.status.equals(status)) {
            throw new BattleStatusCannotBeChangedException(status);
        }
    }

    public RunnerStatus getRunnerStatus(String runnerId) {
        final Runner runner = findRunner(runnerId);
        return runner.getStatus();
    }

    public void setStartTime(LocalDateTime now, LocalDateTime startTime) {
        validateStartTime(now, startTime);
        this.startTime = startTime;
    }

    private void validateStartTime(LocalDateTime now, LocalDateTime startTime) {
        if (!startTime.isAfter(now)) {
            throw new InvalidBattleStartTimeException(startTime, now);
        }
    }


    public boolean isAllRunnersRunningStatus() {
        return this.runners.stream()
                .allMatch(Runner::isRunning);
    }
}
