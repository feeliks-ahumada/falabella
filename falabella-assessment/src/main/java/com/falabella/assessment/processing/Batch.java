package com.falabella.assessment.processing;

import com.falabella.assessment.entities.TemperatureByCity;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class Batch {
    private final List<TemperatureByCity> data;
    public Batch(List<TemperatureByCity> ds) {
        data = ds;
    }

    public String splitByCountry() throws IOException {
        Map<String, List<TemperatureByCity>> temps = data.stream()
                .collect(Collectors.groupingBy(TemperatureByCity::getCountry, Collectors.toList()));

        Path dir = Files.createTempDirectory("output_");
        BiConsumer<String, List<TemperatureByCity>> splitByCountries= (country, listOfTemps) -> {
            Path textFile = Paths.get(dir.toString() ,"GLTBC_".concat(country).concat(".csv"));
            try (
                    BufferedWriter bw = Files.newBufferedWriter(textFile, StandardCharsets.UTF_8,
                            StandardOpenOption.CREATE, StandardOpenOption.WRITE)
            ) {
                bw.write("dt,AverageTemperature,AverageTemperatureUncertainty,City,Country,Latitude,Longitude");
                for(TemperatureByCity item : listOfTemps) {
                    bw.newLine();
                    String sb = item.getDate() + "," +
                            item.getAverageTemperature() + "," +
                            item.getAvgTempUncertainty() + "," +
                            item.getCity() + "," +
                            item.getCountry() + "," +
                            item.getLatitude() + "," +
                            item.getLongitude();
                    bw.write(sb);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        };

        temps.forEach(splitByCountries);

        return dir.toString();
    }
}
