package gongnon.domain.gpt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String role;    // "system", "user", "assistant"
    private String content; // 실제 메시지 내용
}
