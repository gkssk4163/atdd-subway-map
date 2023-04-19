package line;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
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
                .when().post("/stations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());
    }

    @DisplayName("지하철노선을 생성한다")
    @Test
    void createLine() {
        String 노선명 = "신분당선";

        // When
        지하철노선_생성(노선명, "bg-red-600", 1, 2, 10);

        // Then
        List<String> allLineNames = 지하철노선_목록_조회().getList("name", String.class);
        assertThat(allLineNames).containsAnyOf(노선명);
    }

    @DisplayName("지하철노선 목록을 조회한다")
    @Test
    void getLines() {
        // Given
        지하철노선_생성("신분당선", "bg-red-600", 1, 2, 10);
        지하철노선_생성("분당선", "bg-green-600", 1, 3, 20);

        // When
        int lineCount = 지하철노선_목록_조회().getList("name", String.class).size();

        // Then
        assertThat(lineCount).isEqualTo(2);
    }

    @DisplayName("지하철노선을 조회한다")
    @Test
    void getLine() {
        String 노선명 = "신분당선";

        // Given
        ExtractableResponse<Response> createResponse = 지하철노선_생성(노선명, "bg-red-600", 1, 2, 10);
        long id = createResponse.body().jsonPath().getLong("id");

        // When
        String lineName = 지하철노선_조회(id).body().jsonPath().getString("name");

        // Then
        assertThat(lineName).isEqualTo(노선명);
    }

    @DisplayName("지하철노선을 수정한다")
    @Test
    void updateLine() {
        String 변경전_노선명 = "신분당선";
        String 변경후_노선명 = "다른분당선";

        // Given
        ExtractableResponse<Response> createResponse = 지하철노선_생성(변경전_노선명, "bg-red-600", 1, 2, 10);
        long id = createResponse.body().jsonPath().getLong("id");

        // When
        지하철노선_수정(id, 변경후_노선명, "bg-red-600");

        // Then
        String lineName = 지하철노선_조회(id).body().jsonPath().getString("name");
        assertThat(lineName).isEqualTo(변경후_노선명);
    }

    @DisplayName("지하철노선을 삭제한다")
    @Test
    void deleteLine() {
        String 노선명 = "신분당선";

        // Given
        ExtractableResponse<Response> createResponse = 지하철노선_생성(노선명, "bg-red-600", 1, 2, 10);
        long id = createResponse.body().jsonPath().getLong("id");

        // When
        지하철노선_삭제(id);

        // Then
        List<String> allLineNames = 지하철노선_목록_조회().getList("name", String.class);
        assertThat(allLineNames).doesNotContain(노선명);
    }

    private ExtractableResponse<Response> 지하철노선_생성(String name, String color, long upStationId, long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .when().post("/lines")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
    }

    private JsonPath 지하철노선_목록_조회() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().body().jsonPath();
    }

    private ExtractableResponse<Response> 지하철노선_조회(long id) {
        return RestAssured.given().log().all()
                .when().get("/lines/" + id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철노선_수정(long id, String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured.given().log().all()
                .body(params)
                .when().patch("/lines/" + id)
                .then().log().all().statusCode(HttpStatus.OK.value())
                .extract();
    }

    private ExtractableResponse<Response> 지하철노선_삭제(long id) {
        return RestAssured.given().log().all()
                .when().delete("/lines/" + id)
                .then().log().all().statusCode(HttpStatus.NO_CONTENT.value())
                .extract();
    }

}
