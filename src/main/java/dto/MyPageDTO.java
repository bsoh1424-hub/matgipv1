package dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class MyPageDTO {
    private Long mno;

    private String email;

    private String nickname;

    private String profileImage;

    private String role;
}
