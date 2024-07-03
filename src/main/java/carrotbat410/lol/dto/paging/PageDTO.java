package carrotbat410.lol.dto.paging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PageDTO<T> {
    private List<T> content;
    private int totalPages;
    private long totalElements;
    private int pageNo;
    private int size;

    public PageDTO(Page<T> page) {
        this.content = page.getContent();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.pageNo = page.getNumber();
        this.size = page.getSize();
    }
}