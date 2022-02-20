package com.falabella.assessment;

import com.falabella.assessment.entities.*;
import com.falabella.assessment.ingestion.DataSource;
import com.falabella.assessment.processing.Batch;
import com.falabella.assessment.storing.GsGrain;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Base64;
import java.util.function.Function;


@RestController
public class ServiceController {
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity receiveMessage(@RequestBody Body body) {

        Body.Message message = body.getMessage();
        if (message == null) {
            String msg = "Bad Request: invalid Pub/Sub message format";
            System.out.println(msg);
            return new ResponseEntity(msg, HttpStatus.BAD_REQUEST);
        }

        String data = message.getData();
        String target =
                !StringUtils.isEmpty(data) ? new String(Base64.getDecoder().decode(data)) : "";

        System.out.println(data);
        System.out.println(target);

        Gson gson = new Gson();
        InputSchema inputMessage = gson.fromJson(target, InputSchema.class);
        String[] bucketName = inputMessage.getBody().split("/");

        final Function<String, TemperatureByCity> map = line -> {
            TemperatureByCity instance = new TemperatureByCity();
            String[] cols = line.split(",");
            if (cols.length < 7) return null;

            instance.setDate(cols[0]);
            instance.setAverageTemperature(!cols[1].equals("") ? Float.parseFloat(cols[1]) : 0.0f);
            instance.setAvgTempUncertainty(!cols[2].equals("") ? Float.parseFloat(cols[2]) : 0.0f);
            instance.setCity(cols[3]);
            instance.setCountry(cols[4]);
            instance.setLatitude(cols[5]);
            instance.setLongitude(cols[6]);
            return instance;
        };

        DataSource<TemperatureByCity> ds = new DataSource<>(bucketName[0], bucketName[1]);
        try {
            Batch batch = new Batch(ds.fetch(map));
            GsGrain.send(batch.splitByCountry());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ResponseEntity(target, HttpStatus.OK);
    }
}