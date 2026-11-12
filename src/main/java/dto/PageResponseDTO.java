package dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

@Getter
@ToString

public class PageResponseDTO<E> {
    private int page;
    private int size;
    private int total;

    // 시작 페이지 번호, 끝 페이지 번호
    private int start;
    private int end;

    // 이전 페이지 존재 여부, 다음 페이지 존재 여부
    private boolean prev;
    private boolean next;

    private List<E> dtoList;

    @Builder(builderMethodName = "withAll")
    public PageResponseDTO(PageRequestDTO pageRequestDTO, List<E> dtoList, int total) {

        if (total <= 0) {
            this.page = pageRequestDTO != null ? pageRequestDTO.getPage() : 1;
            this.size = pageRequestDTO != null ? pageRequestDTO.getSize() : 10;
            this.total = 0;
            this.start = 0;
            this.end = 0;
            this.prev = false;
            this.next = false;
            this.dtoList = Collections.emptyList();
            return;
        }

        this.page = pageRequestDTO.getPage();
        this.size = pageRequestDTO.getSize();
        this.total = total;
        this.dtoList = dtoList != null ? dtoList : Collections.emptyList();

        // 10개씩 페이지 번호를 보여주는 경우 (1~10, 11~20...)
        this.end = (int) (Math.ceil(this.page / 10.0)) * 10;
        this.start = this.end - 9;

        // 실제 마지막 페이지 수 계산
        int last = (int) (Math.ceil((double) total / size));

        // end 값이 실제 마지막 페이지보다 크면 조절
        this.end = Math.min(end, last);

        // 이전/다음 버튼 활성화 여부
        this.prev = this.start > 1;
        this.next = total > this.end * this.size;
    }

}
