# ============================================================
# Stage 1 — Build the JAR with Maven
# ============================================================
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy pom.xml first for layer caching — dependencies only re-download
# when pom.xml actually changes, not on every code edit.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Now copy sources and build.
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ============================================================
# Stage 2 — Run on a slim JRE-only image
# ============================================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# The pom.xml sets <finalName>app</finalName>, so the artifact is app.jar.
COPY --from=build /workspace/target/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
