package carrotbat410.lol.service;

import carrotbat410.lol.dto.board.BoardCategory;
import carrotbat410.lol.dto.board.BoardDTO;
import carrotbat410.lol.dto.board.WriteBoardRequestDTO;
import carrotbat410.lol.entity.Board;
import carrotbat410.lol.entity.User;
import carrotbat410.lol.repository.BoardRepository;
import carrotbat410.lol.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static carrotbat410.lol.dto.board.BoardCategory.*;
import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class BoardServiceTest {
    @Autowired
    BoardService boardService;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EntityManager em;

    @Test
    @DisplayName("본인의 게시글만 조회할 수 있다.")
    void getMyBoards() throws Exception {
        // given
        User user1 = new User(null, "test123", "testPassword", "ROLE_USER");
        User user2 = new User(null, "test456", "testPassword", "ROLE_USER");
        userRepository.saveAll(List.of(user1, user2));

        Board board1 = new Board(null, "title1", "content1", FREE, user1);
        Board board2 = new Board(null, "title2", "content2", MATCH, user2);
        boardRepository.saveAll(List.of(board1, board2));
        // when
        PageRequest pageRequest = PageRequest.of(0, 30);
        Page<BoardDTO> myBoards = boardService.getMyBoards(user1.getId(), pageRequest);

        // then
        assertThat(myBoards)
                .hasSize(1)
                .extracting("title", "content")
                .contains(
                        tuple("title1", "content1")
                );


        // 캐시에 저장된 결과 확인
        //TODO 캐싱 테스트메서드를 따로 구분하는게 가독성이 더 좋은거 같음.
        Board board3 = new Board(null, "title3", "content3", MATCH, user1);
        boardRepository.save(board3);

        Page<BoardDTO> cachedMyBoards = boardService.getMyBoards(user1.getId(), pageRequest);
        assertThat(cachedMyBoards)
                .hasSize(1)
                .extracting("title", "content")
                .contains(
                        tuple("title1", "content1")
                );
    }

    @Test
    @DisplayName("작성된 모든 게시글을 볼 수 있다.")
    void getAllBoards() throws Exception {
        // given
        User user1 = new User(null, "test123", "testPassword", "ROLE_USER");
        User user2 = new User(null, "test456", "testPassword", "ROLE_USER");
        userRepository.saveAll(List.of(user1, user2));

        Board board1 = new Board(null, "title1", "content1", FREE, user1);
        Board board2 = new Board(null, "title2", "content2", MATCH, user2);
        boardRepository.saveAll(List.of(board1, board2));
        // when
        PageRequest pageRequest = PageRequest.of(0, 30);
        Page<BoardDTO> allBoards = boardService.getAllBoards(pageRequest);

        // then
        assertThat(allBoards)
                .hasSize(2)
                .extracting("title", "content")
                .contains(
                        tuple("title1", "content1"),
                        tuple("title2", "content2")
                );

        // 캐시에 저장된 결과 확인
        //TODO 캐싱 테스트메서드를 따로 구분하는게 가독성이 더 좋은거 같음.
        Board board3 = new Board(null, "title3", "content3", MATCH, user1);
        boardRepository.save(board3);

        Page<BoardDTO> cachedAllBoards = boardService.getAllBoards(pageRequest);
        assertThat(cachedAllBoards)
                .hasSize(2)
                .extracting("title", "content")
                .contains(
                        tuple("title1", "content1"),
                        tuple("title2", "content2")
                );
    }

    //TODO 아래 상황 생각해보기.
    // getReferenceById사용하므로서, 존재하는 유저인지 검증하는 과정이 없어짐. =>이 테스트 필요없어짐.
    // 조회 쿼리시 inner join해서 상관없을려나?
//    @Test
//    @DisplayName("존재하지 않는 유저가 게시글 작성시 예외를 반환한다.")
//    void writeBoardWithNoExistingUser() throws Exception {
//        // given
//        User user = new User(1L, "test123", "testPassword", "ROLE_USER");
//
//        String title = "title1";
//        String content = "content1";
//        BoardCategory boardCategory = FREE;
//        WriteBoardRequestDTO request = new WriteBoardRequestDTO(title, content, boardCategory);
//
//        // when //then
//        assertThatThrownBy(() -> boardService.writeBoard(user.getId(), request))
//                .isInstanceOf(AccessDeniedException.class)
//                .hasMessage("존재하지 않는 유저입니다. 재 로그인후 다시 요청해주세요.");
//    }

    @Test
    @DisplayName("Body값 없이 데이터 요청시 예외가 발생한다.")
    void writeBoardWithNoBody() throws Exception {
        // given
        User user = new User(null, "test123", "testPassword", "ROLE_USER");
        userRepository.save(user);

        String title = "title1";
        String content = "content1";
        BoardCategory boardCategory = FREE;
        WriteBoardRequestDTO request = new WriteBoardRequestDTO(title, content, boardCategory);

        // when
        boardService.writeBoard(user.getId(), request);

        // then
        List<Board> savedBoards = boardRepository.findAll();

        assertThat(savedBoards).hasSize(1)
                .extracting("title", "content", "boardCategory")
                .contains(
                        Tuple.tuple(title, content, boardCategory)
                );
    }

    @Test
    @DisplayName("게시글을 작성할 수 있다.")
    void writeBoard() throws Exception {
        // given
        User user = new User(null, "test123", "testPassword", "ROLE_USER");
        userRepository.save(user);

        String title = "title1";
        String content = "content1";
        BoardCategory boardCategory = FREE;
        WriteBoardRequestDTO request = new WriteBoardRequestDTO(title, content, boardCategory);

        // when
        boardService.writeBoard(user.getId(), request);

        // then
        List<Board> savedBoards = boardRepository.findAll();

        assertThat(savedBoards).hasSize(1)
                .extracting("title", "content", "boardCategory")
                .contains(
                        Tuple.tuple(title, content, boardCategory)
                );
    }

}