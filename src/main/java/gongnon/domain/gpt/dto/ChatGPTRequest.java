package gongnon.domain.gpt.dto;

import lombok.Data;
import java.util.List;

@Data
public class ChatGPTRequest {

    private String model;
    private List<Message> messages;

    public ChatGPTRequest() {
    }

    public ChatGPTRequest(String model, String prompt) {
        this.model = model;
        this.messages = List.of(new Message("user", prompt));
    }
}
