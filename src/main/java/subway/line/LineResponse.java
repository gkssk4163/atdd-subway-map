package subway.line;

import subway.station.Station;

import java.util.List;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<Station> stations;

    public LineResponse(Long id, String name, String color, Station upStation, Station downStation) {
        this.id = id;
        this.name = name;
        this.color = color;
        Station[] stations = {upStation, downStation};
        this.stations = List.of(stations);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Station> getStations() {
        return stations;
    }
}
