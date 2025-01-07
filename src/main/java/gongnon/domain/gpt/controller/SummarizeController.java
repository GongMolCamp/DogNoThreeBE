package gongnon.domain.gpt.controller;

import gongnon.domain.gpt.service.SummarizeService;
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
    public String summarize(@RequestParam String title,
                            @RequestParam String content) {
        return summarizeService.summarize(title, content);
    }
}
