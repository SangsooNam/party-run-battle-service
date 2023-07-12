package online.partyrun.partyrunbattleservice.domain.record.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import online.partyrun.partyrunbattleservice.domain.record.entity.Record;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecordRequest {

    double longitude;
    double latitude;
    double altitude;
    LocalDateTime gpsTime;

    public Record toEntity() {
        return Record.of(longitude, latitude, altitude, gpsTime);
    }

    @Override
    public String toString() {
        return "RecordRequest{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", altitude=" + altitude +
                ", gpsTime=" + gpsTime +
                '}';
    }
}
