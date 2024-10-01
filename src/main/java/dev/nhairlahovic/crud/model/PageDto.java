package dev.nhairlahovic.crud.model;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PageDto<T> {
    private List<T> content;
    private int totalPages;
    private int currentPage;
    private long totalElements;
    private boolean last;

    public static <T> PageDto<T> of(Page<T> page) {
        PageDto<T> dto = new PageDto<>();
        dto.setContent(page.getContent());
        dto.setTotalPages(page.getTotalPages());
        dto.setCurrentPage(page.getPageable().getPageNumber() + 1);
        dto.setTotalElements(page.getTotalElements());
        dto.setLast(page.isLast());

        return dto;
    }
}
