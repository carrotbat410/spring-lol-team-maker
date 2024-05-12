package carrotbat410.lol.service;

import carrotbat410.lol.entity.Summoner;
import carrotbat410.lol.repository.SummonerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SummonerService {
    private final SummonerRepository summonerRepository;

    public List<Summoner> getSummoners(Long userId) {
        return summonerRepository.findByUserId(userId);
    }
}