package events.handlers;

import types.EventHandler;
import types.WeatherData;
import events.WeatherUpdateEvent;

public class WeatherUpdateEventHandler implements EventHandler<WeatherUpdateEvent> {
    private final WeatherData weatherData;

    public WeatherUpdateEventHandler(WeatherData weatherData) {
        this.weatherData = weatherData;
    }

    @Override
    public void handle(WeatherUpdateEvent event) {
        // Handle the weather update event
        WeatherData newWeatherData = event.getWeatherData();

        // Update the weather data in the system
        weatherData.update(newWeatherData);
    }

    public WeatherData getWeatherData() {
        return weatherData;
    }
}
