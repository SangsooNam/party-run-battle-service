package online.partyrun.partyrunbattleservice.domain.record.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Record implements Comparable<Record> {
    Location location;
    LocalDateTime time;
    double distance;

    @Builder
    public Record(Location location, LocalDateTime time, double distance) {
        this.location = location;
        this.time = time;
        this.distance = distance;
    }

    public static Record of(
            double longitude, double latitude, double altitude, LocalDateTime time) {
        return Record.builder()
                .location(Location.of(longitude, latitude, altitude))
                .time(time)
                .build();
    }

    public void updateDistance(Record preRecord) {
        Objects.requireNonNull(preRecord);

        final double additionalDistance = this.location.calculateDistance(preRecord.location);
        this.distance = preRecord.getDistance() + additionalDistance;
    }

    public boolean isBefore(LocalDateTime startTime) {
        return this.time.isBefore(startTime);
    }

    @Override
    public int compareTo(Record o) {
        return this.time.compareTo(o.time);
    }
}
