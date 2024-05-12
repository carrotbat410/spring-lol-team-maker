package carrotbat410.lol.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Table(name = "summoners")
public class Summoner extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "summoner_id")
    private Long id;
    private String summonerName;
    private String tagLine;

    @Column(name = "user_id")
    private Long userId;
}
