package org.k8loud.executor.common.testdata;

public record ResourceDescriptionTestData() {
    public static final String RESOURCE_DESCRIPTION_WITH_NAMESPACE = """
            apiVersion: v1
            kind: Pod
            metadata:
              name: nginx
              namespace: namespace-from-resource-description
            spec:
              containers:
              - name: nginx
                image: nginx:1.14.2
            """;
    public static final String RESOURCE_DESCRIPTION_WITHOUT_KIND = """
            apiVersion: v1
            metadata:
              name: nginx
            spec:
              containers:
              - name: nginx
                image: nginx:1.14.2
            """;
    public static final String RESOURCE_DESCRIPTION_POD = """
            apiVersion: v1
            kind: Pod
            metadata:
              name: nginx
            spec:
              containers:
              - name: nginx
                image: nginx:1.14.2
                ports:
                - containerPort: 80
            """;
    public static final String RESOURCE_DESCRIPTION_CONFIG_MAP = """
            kind: ConfigMap
            metadata:
              creationTimestamp: 2022-02-18T18:52:05Z
              name: game-config
              namespace: default
              uid: b4952dc3-d670-11e5-8cd0-68f728db1985
            data:
              game.properties: |
                enemies=aliens
                lives=3
                enemies.cheat=true
                enemies.cheat.level=noGoodRotten
                secret.code.passphrase=UUDDLRLRBABAS
                secret.code.allowed=true
                secret.code.lives=30
              ui.properties: |
                color.good=purple
                color.bad=yellow
                allow.textmode=true
                how.nice.to.look=fairlyNice
            """;
    public static final String RESOURCE_DESCRIPTION_STATEFUL_SET = """
            apiVersion: apps/v1
            kind: StatefulSet
            metadata:
              name: web
            spec:
              selector:
                matchLabels:
                  app: nginx # has to match .spec.template.metadata.labels
              serviceName: "nginx"
              replicas: 3 # by default is 1
              minReadySeconds: 10 # by default is 0
              template:
                metadata:
                  labels:
                    app: nginx # has to match .spec.selector.matchLabels
                spec:
                  terminationGracePeriodSeconds: 10
                  containers:
                  - name: nginx
                    image: registry.k8s.io/nginx-slim:0.8
                    ports:
                    - containerPort: 80
                      name: web
                    volumeMounts:
                    - name: www
                      mountPath: /usr/share/nginx/html
              volumeClaimTemplates:
              - metadata:
                  name: www
                spec:
                  accessModes: [ "ReadWriteOnce" ]
                  storageClassName: "my-storage-class"
                  resources:
                    requests:
                      storage: 1Gi
            """;

    public static final String RESOURCE_DESCRIPTION_DEPLOYMENT = """
            apiVersion: apps/v1
            kind: Deployment
            metadata:
              name: nginx-deployment
              labels:
                app: nginx
            spec:
              replicas: 3
              selector:
                matchLabels:
                  app: nginx
              template:
                metadata:
                  labels:
                    app: nginx
                spec:
                  containers:
                  - name: nginx
                    image: nginx:1.14.2
                    ports:
                    - containerPort: 80        
            """;
}
