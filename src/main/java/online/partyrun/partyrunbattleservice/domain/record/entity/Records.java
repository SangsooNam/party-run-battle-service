package online.partyrun.partyrunbattleservice.domain.record.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Records {

    List<Record> records;

    public Records(List<Record> records) {
        validateRecords(records);
        Collections.sort(records);

        this.records = records;
    }

    private void validateRecords(List<Record> records) {
        if (Objects.isNull(records) || records.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }

    public Records updateDistance() {
        return updateDistance(records.get(0));
    }

    public Records updateDistance(Record preRecord) {
        for (Record newRecord : this.records) {
            newRecord.updateDistance(preRecord);
            preRecord = newRecord;
        }

        return this;
    }

    public boolean hasBefore(LocalDateTime time) {
        return this.records.stream().anyMatch(record -> record.isBefore(time));
    }

    public double getDistance() {
        return this.records.get(this.records.size() - 1).getDistance();
    }
}
