package gongnon.domain.gpt.controller;

import gongnon.domain.gpt.service.SummarizeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "뉴스 요약", description = "GPT API를 이용한 뉴스 요약 API")
@RestController
@RequestMapping("/v1/gpt")
@RequiredArgsConstructor
public class SummarizeController {
    private final SummarizeService summarizeService;

    /**
     * 뉴스 요약용 API
     * ex) GET /v1/gpt/summarize?title=뉴스제목&content=뉴스본문
     */
    @Operation(summary = "뉴스 요약")
    @GetMapping("/summarize")
    public String summarize(
        @Parameter(description = "뉴스 아이디", required = true, example = "1")
        @RequestParam Long id
    )
    {
        return summarizeService.summarize(id);
    }
}
