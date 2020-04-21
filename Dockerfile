FROM gradle
COPY . ./covis
WORKDIR ./covis
RUN chmod +x gradlew
RUN ./gradlew build
ENTRYPOINT ./gradlew bootRun