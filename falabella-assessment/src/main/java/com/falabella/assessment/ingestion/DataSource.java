package com.falabella.assessment.ingestion;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.StorageOptions;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class DataSource<T> {
    private static final Logger logger = Logger.getLogger(DataSource.class.getName());
    private static final String COMMA = ",";
    private final String bucket;
    private final String file;

    public DataSource(String bucketName, String fileName) {
        this.bucket = bucketName;
        this.file = fileName;
    }

    public List<T> fetch(Function<String, T> mapFunction) throws IOException {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        Blob blob = storage.get(BlobId.of(this.bucket, this.file));

        Path dir = Files.createTempDirectory("input_");
        Path path = Paths.get(dir.toString(), this.file);
        blob.downloadTo(path);
        logger.info("Object download to...".concat(path.toString()));

        return getData(path, mapFunction);
    }

    private List<T> getData(Path path, Function<String, T> mapToTemp) {
        List<T> inputList = new ArrayList<>();
        File inputFile = path.toFile();
        try (
                InputStream inputStream = new FileInputStream(inputFile);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, UTF_8.name()))
                ) {

            inputList = reader.lines()
                    .skip(1).map(mapToTemp)
                    .filter(Objects::nonNull)
                    .parallel()
                    .collect(Collectors.toList());

        } catch (IOException ex){
            ex.printStackTrace();
        }

        return inputList;
    }
}
