package carrotbat410.lol.repository;

import carrotbat410.lol.entity.cache.BoardCache;
import org.springframework.data.repository.CrudRepository;

public interface BoardRedisRepository extends CrudRepository<BoardCache, String> {
}
