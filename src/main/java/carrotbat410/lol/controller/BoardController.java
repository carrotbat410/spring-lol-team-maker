package carrotbat410.lol.controller;

import carrotbat410.lol.dto.board.BoardDTO;
import carrotbat410.lol.dto.board.WriteBoardRequestDTO;
import carrotbat410.lol.dto.result.SuccessResult;
import carrotbat410.lol.service.BoardService;
import carrotbat410.lol.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BoardController {
    @Autowired
    private BoardService boardService;

    @GetMapping("/my/boards")
    public SuccessResult<List<BoardDTO>> getMyBoards(Pageable pageable) {
        Long userId = SecurityUtils.getCurrentUserIdFromAuthentication();

        List<BoardDTO> boards = boardService.getMyBoards(userId, pageable);
        return new SuccessResult<>("ok", boards);
    }

    @GetMapping("/boards")
    public SuccessResult<List<BoardDTO>> getAllBoards(Pageable pageable) {
        List<BoardDTO> boards = boardService.getAllBoards(pageable);
        return new SuccessResult<>("ok", boards);
    }

    @PostMapping("/board")
    public SuccessResult writeBoard(@RequestBody @Validated WriteBoardRequestDTO request) {
        Long userId = SecurityUtils.getCurrentUserIdFromAuthentication();
        boardService.writeBoard(userId, request);
        return new SuccessResult<>("ok");
    }

}
