package online.partyrun.partyrunbattleservice.domain.record.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.data.annotation.Id;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RunnerRecord {

    @Id String id;
    String battleId;
    String runnerId;
    List<Record> records;
}
