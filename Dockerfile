FROM gcr.io/distroless/java17-debian11:latest
WORKDIR /opt/mirchi/
COPY target/google-cloud-*.jar /opt/mirchi/google-cloud.jar
CMD ["google-cloud.jar"]
EXPOSE 8080