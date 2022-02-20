package com.falabella.assessment.cloud.functions;

import com.falabella.assessment.cloud.functions.pojos.GcsEvent;
import com.google.api.core.ApiFuture;
import com.google.cloud.functions.BackgroundFunction;
import com.google.cloud.functions.Context;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Trigger implements BackgroundFunction<GcsEvent> {

    private static final Logger logger = Logger.getLogger(Trigger.class.getName());

    @Override
    public void accept(GcsEvent event, Context context) throws IOException, InterruptedException, ExecutionException {
        String topicName = "projects/falabella-assessment/topics/input";
        logger.info(topicName);

        // Create PubSubMessage object
        ByteArrayOutputStream byteStream =  new ByteArrayOutputStream();
        input inputMsg = input.newBuilder()
                .setBody(event.getBucket().concat("/").concat(event.getName())).build();
        Encoder encoder = EncoderFactory.get().jsonEncoder(input.getClassSchema(), byteStream);
        inputMsg.customEncode(encoder);
        encoder.flush();
        ByteString data = ByteString.copyFrom(byteStream.toByteArray());
        PubsubMessage message = PubsubMessage.newBuilder().setData(data).build();

        logger.info(message.toString());

        Publisher publisher = null;
        try {
            publisher = Publisher.newBuilder(topicName).build();
            ApiFuture<String> future = publisher.publish(message);
            logger.info("Published message ID: " + future.get());
        } finally {
            if (publisher != null) {
                publisher.shutdown();
                publisher.awaitTermination(1, TimeUnit.MINUTES);
            }
        }

    }
}
