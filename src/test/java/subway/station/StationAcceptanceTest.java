package subway.station;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class StationAcceptanceTest {
    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");

        ExtractableResponse<Response> response = requestCreateStation(params);

        // then
        successCreateStation(response);

        // then
        List<String> stationNames = requestGetStations().getList("name", String.class);
        assertThat(stationNames).containsAnyOf("강남역");
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역 목록을 조회한다")
    @Test
    void getStations() {
        // given
        String[] stationNames = {"강남역", "잠실역"};
        for (String stationName : stationNames) {
            Map<String, String> params = new HashMap<>();
            params.put("name", stationName);

            ExtractableResponse<Response> response = requestCreateStation(params);
            successCreateStation(response);
        }

        // when
        JsonPath allStations = requestGetStations();

        // then
        int stationCount = allStations.getList("name", String.class).size();
        assertThat(stationCount).isEqualTo(2);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 삭제한다.")
    @Test
    void deleteStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");

        ExtractableResponse<Response> response = requestCreateStation(params);
        successCreateStation(response);

        long id = response.body().jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> deleteResponse = requestDeleteStation(id);
        successDeleteStation(deleteResponse);

        // then
        List<String> stationNames = requestGetStations().getList("name", String.class);
        assertThat(stationNames).doesNotContain("강남역");
    }

    private static ExtractableResponse<Response> requestCreateStation(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    private static ExtractableResponse<Response> requestDeleteStation(long id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/stations/" + id)
                .then().log().all()
                .extract();
    }

    private static JsonPath requestGetStations() {
        return RestAssured.given().log().all()
                .when().get("/stations")
                .then().log().all()
                .extract().jsonPath();
    }

    private static void successCreateStation(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private static void successDeleteStation(ExtractableResponse<Response> deleteResponse) {
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}