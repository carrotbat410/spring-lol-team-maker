package carrotbat410.lol.service;

import carrotbat410.lol.dto.board.BoardDTO;
import carrotbat410.lol.dto.board.WriteBoardRequestDTO;
import carrotbat410.lol.entity.Board;
import carrotbat410.lol.entity.User;
import carrotbat410.lol.entity.cache.BoardCache;
import carrotbat410.lol.exhandler.exception.AccessDeniedException;
import carrotbat410.lol.repository.BoardRedisRepository;
import carrotbat410.lol.repository.BoardRepository;
import carrotbat410.lol.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BoardService {
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BoardRedisRepository boardCacheRepository;

    private static final Long CACHE_TTL = 10L; // 10초 TTL 설정

    public Page<BoardDTO> getMyBoards(Long userId, Pageable pageable) {
        String cacheKey = "myBoards_" + userId + "_" + pageable.getPageNumber() + "_" + pageable.getPageSize();
        Optional<BoardCache> cachedResult = boardCacheRepository.findById(cacheKey);

        if (cachedResult.isPresent()) {
            BoardCache cache = cachedResult.get();
            return new PageImpl<>(cache.getBoards(), pageable, cache.getTotalElements());
        } else {
            Page<Board> boardsPage = boardRepository.findByUserId(userId, pageable);
            List<BoardDTO> boardDTOs = boardsPage.stream().map(board -> new BoardDTO(
                    board.getId(),
                    board.getTitle(),
                    board.getContent(),
                    userId,
                    null
            )).collect(Collectors.toList());

            BoardCache boardCache = new BoardCache(cacheKey, boardDTOs, pageable.getPageNumber(), pageable.getPageSize(), boardsPage.getTotalElements(), CACHE_TTL);
            boardCacheRepository.save(boardCache);
            return boardsPage.map(board -> new BoardDTO(
                    board.getId(),
                    board.getTitle(),
                    board.getContent(),
                    userId,
                    null
            ));
        }
    }

    public Page<BoardDTO> getAllBoards(Pageable pageable) {
        String cacheKey = "allBoards_" + pageable.getPageNumber() + "_" + pageable.getPageSize();
        Optional<BoardCache> cachedResult = boardCacheRepository.findById(cacheKey);

        if (cachedResult.isPresent()) {
            BoardCache cache = cachedResult.get();
            return new PageImpl<>(cache.getBoards(), pageable, cache.getTotalElements());
        } else {
            Page<Board> boardsPage = boardRepository.findAll(pageable);
            List<BoardDTO> boardDTOs = boardsPage.stream().map(board -> new BoardDTO(
                    board.getId(),
                    board.getTitle(),
                    board.getContent(),
                    board.getUser().getId(),
                    board.getUser().getUsername()
            )).collect(Collectors.toList());

            BoardCache boardCache = new BoardCache(cacheKey, boardDTOs, pageable.getPageNumber(), pageable.getPageSize(), boardsPage.getTotalElements(), CACHE_TTL);
            boardCacheRepository.save(boardCache);
            return boardsPage.map(board -> new BoardDTO(
                    board.getId(),
                    board.getTitle(),
                    board.getContent(),
                    board.getUser().getId(),
                    board.getUser().getUsername()
            ));
        }
    }

    @Transactional
    public void writeBoard(Long userId, WriteBoardRequestDTO request) {
        User user = userRepository.getReferenceById(userId);
        Board newBoard = new Board(null, request.getTitle(), request.getContent(), request.getBoardCategory(), user);
        boardRepository.save(newBoard);

        // 캐시 무효화
//        boardCacheRepository.deleteAll();
    }
}
