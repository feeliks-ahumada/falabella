## Install instructions:

1. Run:
```
gcloud functions deploy trigger-function \
--entry-point cloud.functions.Trigger \
--runtime java11 \
--memory 512MB \
--trigger-resource gs://falabella_assessment_input \
--trigger-event google.storage.object.finalize
```