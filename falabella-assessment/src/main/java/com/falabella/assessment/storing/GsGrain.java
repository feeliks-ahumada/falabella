package com.falabella.assessment.storing;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GsGrain {
    private static final Logger logger = Logger.getLogger(GsGrain.class.getName());

    public static void send(String dir) {
        String bucket = "falabella_assessment_output";
        Path path = Paths.get(dir);

        Storage storage = StorageOptions.getDefaultInstance().getService();

        Consumer<Path> push = p -> {
            BlobId blobId = BlobId.of(bucket, p.getFileName().toString());
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
            try {
                logger.info(String.format("Sending object...%s", p.getFileName().toString()));
                storage.create(blobInfo, Files.readAllBytes(p));
                Files.deleteIfExists(p);
                logger.info(String.format("Deleting object...%s", p.getFileName().toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        List<Path> files;
        try {
            files = Files.walk(path, FileVisitOption.FOLLOW_LINKS)
                    .filter(f -> f.toString().endsWith(".csv"))
                    .collect(Collectors.toList());
            files.forEach(push);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
