package gongnon.domain.sms.model;

import gongnon.domain.user.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // 기본 생성자 추가
@Entity
@Table(name = "messages")
public class Message {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String phone;

	@Column(columnDefinition = "TEXT")
	private String content;
	private String sentAt;

	// Message와 User의 N:1 관계 매핑
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public Message(String phone, String content, String sentAt, User user) {
		this.phone = phone;
		this.content = content;
		this.sentAt = sentAt;
		this.user = user;
	}
}
