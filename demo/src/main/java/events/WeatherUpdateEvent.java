package events;

import types.Event;
import types.WeatherData;
import types.enums.EventType;

public class WeatherUpdateEvent extends Event {
    private final WeatherData weatherData;

    public WeatherUpdateEvent(WeatherData weatherData) {
        super(EventType.WEATHER_UPDATE);
        this.weatherData = weatherData;
    }

    public WeatherData getWeatherData() {
        return weatherData;
    }
}
