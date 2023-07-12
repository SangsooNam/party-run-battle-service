package online.partyrun.partyrunbattleservice.domain.record.repository;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.record.entity.Record;
import online.partyrun.partyrunbattleservice.domain.record.entity.RunnerRecord;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecordDao {

    MongoTemplate mongoTemplate;

    public Optional<Record> findLatestRunnerRecord(String battleId, String runnerId) {
        Aggregation aggregation =
                Aggregation.newAggregation(
                        RunnerRecord.class,
                        match(where("battleId").is(battleId).and("runnerId").is(runnerId)),
                        unwind("records"),
                        sort(Sort.by(Sort.Direction.DESC, "records.time")),
                        limit(1),
                        getRecordProjectionOperation());
        AggregationResults<Record> results =
                mongoTemplate.aggregate(aggregation, Battle.class, Record.class);

        return Optional.ofNullable(results.getUniqueMappedResult());
    }

    private ProjectionOperation getRecordProjectionOperation() {
        return project()
                .and("records.location.point")
                .as("location.point")
                .and("records.location.altitude")
                .as("location.altitude")
                .and("records.time")
                .as("time")
                .and("records.distance")
                .as("distance")
                .andExclude("_id");
    }

    public void pushNewRecords(String battleId, String runnerId, List<Record> records) {
        Query query =
                Query.query(Criteria.where("battleId").is(battleId).and("runnerId").is(runnerId));
        Update update = new Update().push("records").each(records.toArray());

        mongoTemplate.updateFirst(query, update, RunnerRecord.class);
    }
}
