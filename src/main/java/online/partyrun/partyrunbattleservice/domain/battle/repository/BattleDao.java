package online.partyrun.partyrunbattleservice.domain.battle.repository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;
import online.partyrun.partyrunbattleservice.domain.runner.entity.RunnerStatus;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BattleDao {

    MongoTemplate mongoTemplate;

    public void updateRunnerStatus(String battleId, String runnerId, RunnerStatus runnerStatus) {
        Query query = Query.query(createBattleRunnerCriteria(battleId, runnerId));
        Update update = new Update().set("runners.$.status", runnerStatus);
        mongoTemplate.updateFirst(query, update, Battle.class);
    }

    private Criteria createBattleRunnerCriteria(String battleId, String runnerId) {
        return where("id").is(battleId).and("runners.id").is(runnerId);
    }
}
