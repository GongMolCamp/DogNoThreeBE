package gongnon.domain.hotnews.model;

import gongnon.domain.hotnews.repository.PredefinedPressRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class PredefinedPressInitializer implements CommandLineRunner {

    private final PredefinedPressRepository predefinedPressRepository;

    public PredefinedPressInitializer(PredefinedPressRepository predefinedPressRepository) {
        this.predefinedPressRepository = predefinedPressRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (predefinedPressRepository.count() == 0) { // 데이터베이스에 데이터가 없는 경우 초기화
            predefinedPressRepository.saveAll(Arrays.asList(
                    new PredefinedPress("TV조선", "448"),
                    new PredefinedPress("매일신문", "088"),
                    new PredefinedPress("YTN", "052"),
                    new PredefinedPress("매일경제", "009"),
                    new PredefinedPress("노컷뉴스", "079"),
                    new PredefinedPress("JTBC", "437"),
                    new PredefinedPress("경향신문", "032"),
                    new PredefinedPress("한국경제", "015"),
                    new PredefinedPress("연합뉴스", "001"),
                    new PredefinedPress("서울신문", "081"),
                    new PredefinedPress("파이낸셜뉴스", "014"),
                    new PredefinedPress("뉴스1", "421"),
                    new PredefinedPress("아시아경제", "277"),
                    new PredefinedPress("국민일보", "005"),
                    new PredefinedPress("중앙일보", "025"),
                    new PredefinedPress("KBS", "056"),
                    new PredefinedPress("SBS", "055"),
                    new PredefinedPress("머니투데이", "008"),
                    new PredefinedPress("동아일보", "020"),
                    new PredefinedPress("조선일보", "023"),
                    new PredefinedPress("세계일보", "022"),
                    new PredefinedPress("조선비즈", "366"),
                    new PredefinedPress("서울경제", "011"),
                    new PredefinedPress("헤럴드경제", "016"),
                    new PredefinedPress("이데일리", "018"),
                    new PredefinedPress("부산일보", "082"),
                    new PredefinedPress("MBN", "057"),
                    new PredefinedPress("한겨레", "028"),
                    new PredefinedPress("한국일보", "469"),
                    new PredefinedPress("전자신문", "030"),
                    new PredefinedPress("한경비즈니스", "050"),
                    new PredefinedPress("한국경제TV", "215"),
                    new PredefinedPress("채널A", "449"),
                    new PredefinedPress("뉴시스", "003"),
                    new PredefinedPress("아이뉴스24", "031"),
                    new PredefinedPress("문화일보", "021"),
                    new PredefinedPress("MBC", "214"),
                    new PredefinedPress("연합뉴스TV", "422"),
                    new PredefinedPress("머니S", "417"),
                    new PredefinedPress("강원일보", "087"),
                    new PredefinedPress("오마이뉴스", "047"),
                    new PredefinedPress("동아사이언스", "584"),
                    new PredefinedPress("디지털타임스", "029"),
                    new PredefinedPress("프레시안", "002"),
                    new PredefinedPress("KBC광주방송", "660"),
                    new PredefinedPress("헬스조선", "346"),
                    new PredefinedPress("코메디닷컴", "296"),
                    new PredefinedPress("데일리안", "119"),
                    new PredefinedPress("미디어오늘", "006"),
                    new PredefinedPress("SBS biz", "374"),
                    new PredefinedPress("주간동아", "037"),
                    new PredefinedPress("비즈워치", "648"),
                    new PredefinedPress("월간 산", "094"),
                    new PredefinedPress("신동아", "262"),
                    new PredefinedPress("코리아헤럴드", "044"),
                    new PredefinedPress("코리아중앙데일리", "640"),
                    new PredefinedPress("더팩트", "629"),
                    new PredefinedPress("강원도민일보", "654"),
                    new PredefinedPress("시사저널", "586"),
                    new PredefinedPress("디지털데일리", "138"),
                    new PredefinedPress("블로터", "293"),
                    new PredefinedPress("경기일보", "666"),
                    new PredefinedPress("지디넷코리아", "092"),
                    new PredefinedPress("대전일보", "656"),
                    new PredefinedPress("농민신문", "662"),
                    new PredefinedPress("이코노미스트", "243"),
                    new PredefinedPress("매경이코노미", "024"),
                    new PredefinedPress("주간조선", "053"),
                    new PredefinedPress("대구MBC", "657"),
                    new PredefinedPress("주간경향", "033"),
                    new PredefinedPress("시사IN", "308"),
                    new PredefinedPress("CJB청주방송", "655"),
                    new PredefinedPress("전주MBC", "659"),
                    new PredefinedPress("한겨레21", "036"),
                    new PredefinedPress("기자협회보", "127"),
                    new PredefinedPress("조세일보", "123"),
                    new PredefinedPress("여성신문", "310"),
                    new PredefinedPress("국제신문", "658"),
                    new PredefinedPress("JIBS", "661"),
                    new PredefinedPress("더스쿠프", "665"),
                    new PredefinedPress("레이디경향", "145")
            ));
        }
    }
}