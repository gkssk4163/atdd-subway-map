package line;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    @BeforeEach
    void setUp() {
        지하철역_생성("지하철역");
        지하철역_생성("새로운지하철역");
    }

    private void 지하철역_생성(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());
    }


    @Test
    void 지하철노선_생성() {
        String 신분당선 = "신분당선";

        // When
        지하철노선_생성(신분당선, "bg-red-600", 1, 2, 10);

        // Then
        List<String> lineNames = 지하철노선_전체목록_조회().getList("name", String.class);
        assertThat(lineNames).containsAnyOf(신분당선);
    }

    @Test
    void 지하철노선_목록_조회() {
        // Given
        지하철노선_생성("신분당선", "bg-red-600", 1, 2, 10);
        지하철노선_생성("분당선", "bg-green-600", 1, 3, 20);

        // When
        int lineCount = 지하철노선_전체목록_조회().getList("name", String.class).size();

        // Then
        assertThat(lineCount).isEqualTo(2);
    }

    private void 지하철노선_생성(String name, String color, long upStationId, long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        RestAssured.given().log().all()
                .body(params)
                .when().post("/lines")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());
    }

    private JsonPath 지하철노선_전체목록_조회() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().body().jsonPath();
    }
}
