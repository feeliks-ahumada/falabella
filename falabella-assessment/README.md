## Shipping the code
```
gcloud auth configure-docker

# Compiling
mvn compile jib:build -Dimage=gcr.io/falabella-assessment/service

# Deploy
gcloud run deploy service --image gcr.io/falabella-assessment/service \
--update-env-vars GOOGLE_CLOUD_PROJECT=falabella-assessment --no-allow-unauthenticated
```

## Integrating with Pub/Sub

1. create a service account to subscription identity
```
gcloud iam service-accounts create falabella-invoker \
     --display-name "Cloud Run Pub/Sub Invoker"
```

2. Create a subscription with the service account
```
gcloud run services add-iam-policy-binding service \
   --member=serviceAccount:falabella-invoker@falabella-assessment.iam.gserviceaccount.com \
   --role=roles/run.invoker
```

3. allow pub/sub to create auth tokens 
```
gcloud projects add-iam-policy-binding falabella-assessment \
     --member=serviceAccount:service-762947278452@gcp-sa-pubsub.iam.gserviceaccount.com \
     --role=roles/iam.serviceAccountTokenCreator
```

4. Create a pub/sub subscription with the service account
```
gcloud pubsub subscriptions create falabella-serv-suscription --topic projects/falabella-assessment/topics/input \
   --push-endpoint=https://service-xkb6yicqea-uc.a.run.app \
   --push-auth-service-account=falabella-invoker@falabella-assessment.iam.gserviceaccount.com

```
