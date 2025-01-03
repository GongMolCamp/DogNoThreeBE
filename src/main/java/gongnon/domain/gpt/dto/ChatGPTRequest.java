package gongnon.domain.gpt.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChatGPTRequest {
    private String model;
    private List<Message> messages;

    public ChatGPTRequest(String model, String prompt) {
        this.model = model;
        this.messages =  new ArrayList<>();
        this.messages.add(new Message("user", prompt));
    }
}
//DTO들은 기본적으로 요청과 응답에 대한 객체를 담고만 있음
//사용같은 건 다른 계층에서 하는 듯


