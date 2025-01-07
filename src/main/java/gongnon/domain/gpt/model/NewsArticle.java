package gongnon.domain.gpt.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "news_article")
public class NewsArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title; // gpt에게 질문할 기사 제목 ex) 르세라핌 인기 입증한 베스트그룹 '2관왕 수상'

    @Column(length = 65535) // gpt에게 질문할 기사 내용 ex) 그룹 르세라핌이 4일 오후 일본 후쿠오카 페이페이돔에서 열린 어쩌구저쩌구
    private String content;

    @Column(length = 65535)
    private String summary;
}
