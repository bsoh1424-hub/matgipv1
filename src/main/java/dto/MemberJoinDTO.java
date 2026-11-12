package dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberJoinDTO {
    private String email;
    private String password;   // 화면에서 입력받은 평문 비밀번호 (서비스단에서 암호화)
    private String nickname;
}
