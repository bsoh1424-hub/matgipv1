package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.data.domain.Pageable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor

public class PageRequestDTO {
    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 12;

    private String type;     // 검색 종류 (t, c, w 등)

    // 💡 화면(list.html)의 검색 필터와 일치하도록 필드 추가
    private String guName; // 지역구 (해운대구, 수영구 등)
    private String category; // 카테고리 (국밥, 밀면 등)
    private String keyword;  // 검색어

    public String[] getTypes() {
        if (type == null || type.isEmpty()) {
            return null;
        }
        return type.split("");
    }

    public Pageable getPageable(String... props) {
        // page 번호가 1보다 작으면 기본값 1로 고정
        int pageNum = this.page <= 0 ? 1 : this.page;
        return PageRequest.of(pageNum - 1, this.size, Sort.by(props).descending());
    }

    // URL 파라미터 조합 메서드 (캐싱 로직 제거하여 항상 최신 값 반영)
    public String getLink() {
        StringBuilder builder = new StringBuilder();

        builder.append("page=").append(this.page);
        builder.append("&size=").append(this.size);

        if (type != null && !type.isEmpty()) {
            builder.append("&type=").append(type);
        }

        if (guName != null && !guName.isEmpty()) {
            builder.append("&district=").append(URLEncoder.encode(guName, StandardCharsets.UTF_8));
        }

        if (category != null && !category.isEmpty()) {
            builder.append("&category=").append(URLEncoder.encode(category, StandardCharsets.UTF_8));
        }

        if (keyword != null && !keyword.isEmpty()) {
            builder.append("&keyword=").append(URLEncoder.encode(keyword, StandardCharsets.UTF_8));
        }

        return builder.toString();
    }
}
