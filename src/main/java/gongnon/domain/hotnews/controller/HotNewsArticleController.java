package gongnon.domain.hotnews.controller;

import gongnon.domain.hotnews.model.HotNewsArticle;
import gongnon.domain.hotnews.model.HotNewsArticleComment;
import gongnon.domain.hotnews.service.HotNewsArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/hotnews")
public class HotNewsArticleController {
    @Autowired
    private HotNewsArticleService hotNewsArticleService;

    // 최신 top 5 뉴스 조회
    @GetMapping("/top5/{userId}")
    public List<HotNewsArticle> getTop5News(
            @PathVariable Long userId,
            @RequestParam(required = false) String date) {
        LocalDate queryDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        return hotNewsArticleService.getTop5NewsByPress(userId, queryDate);
    }

    // 댓글 추가
    @PostMapping("/{articleId}/comments")
    public HotNewsArticleComment addComment(
            @PathVariable Long articleId,
            @RequestParam String author,
            @RequestParam String content) {
        return hotNewsArticleService.addCommentToArticle(articleId, author, content);
    }

    // 댓글 조회
    @GetMapping("/{articleId}/comments")
    public List<HotNewsArticleComment> getCommentsByArticleId(@PathVariable Long articleId) {
        return hotNewsArticleService.getCommentsByArticleId(articleId);
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        hotNewsArticleService.deleteComment(commentId);
    }
}
