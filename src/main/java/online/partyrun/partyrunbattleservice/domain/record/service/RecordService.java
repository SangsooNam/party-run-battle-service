package online.partyrun.partyrunbattleservice.domain.record.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.battle.service.BattleService;
import online.partyrun.partyrunbattleservice.domain.record.dto.RecordRequest;
import online.partyrun.partyrunbattleservice.domain.record.dto.RecordRequests;
import online.partyrun.partyrunbattleservice.domain.record.dto.RunnerDistanceResponse;
import online.partyrun.partyrunbattleservice.domain.record.entity.Record;
import online.partyrun.partyrunbattleservice.domain.record.entity.Records;
import online.partyrun.partyrunbattleservice.domain.record.exception.BattleIsNotRunningException;
import online.partyrun.partyrunbattleservice.domain.record.exception.RunnerIsNotRunningException;
import online.partyrun.partyrunbattleservice.domain.record.repository.RecordDao;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecordService {

    BattleService battleService;
    RecordDao recordDao;

    public RunnerDistanceResponse calculateDistance(
            String battleId, String runnerId, RecordRequests request) {
        final Battle battle = battleService.findBy(battleId, runnerId);
        validateIsRunning(battle, runnerId);

        final Records records = createNewRecords(request);
        Records newRecords =
                recordDao
                        .findLatestRunnerRecord(battleId, runnerId)
                        .map(records::updateDistance)
                        .orElseGet(() -> newRunnerRecords(records, battle.getStartTime()));

        recordDao.pushNewRecords(battleId, runnerId, newRecords.getRecords());

        return new RunnerDistanceResponse(runnerId, false, records.getDistance());
    }

    private void validateIsRunning(Battle battle, String runnerId) {
        validateBattleRunning(battle);
        validateRunnerRunning(battle, runnerId);
    }

    private void validateBattleRunning(Battle battle) {
        if (!battle.isRunning()) {
            throw new BattleIsNotRunningException(battle.getId());
        }
    }

    private void validateRunnerRunning(Battle battle, String runnerId) {
        if (!battle.isRunningRunner(runnerId)) {
            throw new RunnerIsNotRunningException(battle.getId());
        }
    }

    private Records createNewRecords(RecordRequests request) {
        final List<Record> records =
                request.getGpsDatas().stream().map(RecordRequest::toEntity).sorted().toList();

        return new Records(records);
    }

    private Records newRunnerRecords(Records records, LocalDateTime startTime) {
        if (records.hasBefore(startTime)) {
            throw new IllegalArgumentException();
        }

        return records.updateDistance();
    }
}
