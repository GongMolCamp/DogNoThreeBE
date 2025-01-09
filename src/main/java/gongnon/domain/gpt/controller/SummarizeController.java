package gongnon.domain.gpt.controller;

import gongnon.domain.gpt.service.SummarizeService;
import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/gpt")
@RequiredArgsConstructor
public class SummarizeController {
    private final SummarizeService summarizeService;

    /**
     * 뉴스 요약용 API
     * ex) GET /v1/gpt/summarize?title=뉴스제목&content=뉴스본문
     */
    @GetMapping("/summarize")
    public String summarize(@RequestParam Long id)
    {
        return summarizeService.summarize(id);
    }
}
