package carrotbat410.lol.entity;

import carrotbat410.lol.dto.SummonerDTO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Getter
@Table(name = "summoners")
public class Summoner extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "summoner_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private String summonerName;
    private String tagLine;
    private String tier;
    private String rank1; //! rank 예약어여서 rank로 설정 불가능함. //TODO 안정화되면 필드명, DB컬럼명 rank로 바꾸기.
    private int mmr;
    private int level;
    private int wins;
    private int losses;
    private int iconId;

    public static Summoner of(Long userId, String summonerName, String tagLine, String tier, String rank,
                              int mmr, int level, int wins, int losses, int iconId){
        return new Summoner(null, userId, summonerName, tagLine, tier, rank, mmr, level, wins, losses, iconId);
    }
}
