package gongnon.domain.gpt.controller;

import gongnon.domain.gpt.service.SummarizeService;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bot")
public class SummarizeController {

    private final SummarizeService summarizeService;

    public SummarizeController(SummarizeService summarizeService) {
        this.summarizeService = summarizeService;
    }

    /**
     * 뉴스 요약용 엔드포인트
     * ex) GET /bot/summarize?title=뉴스제목&content=뉴스본문
     */
    @GetMapping("/summarize")
    public String summarize(@RequestParam(required = false) Long id,
                            @RequestParam(required = false) String title,
                            @RequestParam(required = false) String content) {
        if (id != null) {
            return summarizeService.summarize(id);
        }
        else if (title != null && content != null){
            return summarizeService.summarize(title, content);
        }
        throw new IllegalArgumentException("id 또는 title과 content 중 하나는 필수입니다.");


    }
}
