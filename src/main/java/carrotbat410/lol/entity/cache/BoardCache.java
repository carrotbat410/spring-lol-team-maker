package carrotbat410.lol.entity.cache;

import carrotbat410.lol.dto.board.BoardDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@ToString
@RedisHash("BoardCache")
@Getter
@Setter
public class BoardCache implements Serializable {
    @Id
    private String id;

    @Indexed
    private List<BoardDTO> boards;
    private int pageNumber;
    private int pageSize;
    private long totalElements;

    @TimeToLive(unit = TimeUnit.SECONDS)
    private Long expiration;

    public BoardCache(String id, List<BoardDTO> boards, int pageNumber, int pageSize, long totalElements, Long expiration) {
        this.id = id;
        this.boards = (boards != null) ? boards : Collections.emptyList();
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.expiration = expiration;
    }
}