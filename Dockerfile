# ==========================================
# Stage 1: Build the application
# ==========================================
FROM ubuntu:22.04 AS builder

ENV DEBIAN_FRONTEND=noninteractive

RUN mkdir -p /etc/apt/apt.conf.d && \
    echo 'Acquire::https::Verify-Peer "false";' > /etc/apt/apt.conf.d/99-insecure && \
    echo 'Acquire::https::Verify-Host "false";' >> /etc/apt/apt.conf.d/99-insecure && \
    sed -i 's/http:\/\/archive.ubuntu.com/https:\/\/mirrors.aliyun.com/g' /etc/apt/sources.list && \
    sed -i 's/http:\/\/security.ubuntu.com/https:\/\/mirrors.aliyun.com/g' /etc/apt/sources.list

RUN apt-get update

RUN apt-get install -y --fix-missing curl
RUN apt-get install -y --fix-missing openjdk-11-jdk
RUN apt-get install -y --fix-missing nodejs npm
RUN apt-get install -y --fix-missing maven

RUN npm config set registry https://registry.npmmirror.com && \
    npm install -g grunt-cli

WORKDIR /build

COPY . .

RUN mkdir -p /root/.m2 && \
    echo '<settings><mirrors><mirror><id>aliyunmaven</id><mirrorOf>central</mirrorOf><name>Aliyun Maven</name><url>https://maven.aliyun.com/repository/central</url></mirror></mirrors></settings>' > /root/.m2/settings.xml

RUN mvn clean package -Pprod -DskipTests

# ==========================================
# Stage 2: Run the application
# ==========================================
FROM ubuntu:22.04
LABEL maintainer="b.gamard@sismics.com"

ENV DEBIAN_FRONTEND=noninteractive
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8
ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64/
ENV JAVA_OPTIONS="-Dfile.encoding=UTF-8 -Xmx1g"
ENV JETTY_VERSION=11.0.20
ENV JETTY_HOME=/opt/jetty

RUN mkdir -p /etc/apt/apt.conf.d && \
    echo 'Acquire::https::Verify-Peer "false";' > /etc/apt/apt.conf.d/99-insecure && \
    echo 'Acquire::https::Verify-Host "false";' >> /etc/apt/apt.conf.d/99-insecure && \
    sed -i 's/http:\/\/archive.ubuntu.com/https:\/\/mirrors.aliyun.com/g' /etc/apt/sources.list && \
    sed -i 's/http:\/\/security.ubuntu.com/https:\/\/mirrors.aliyun.com/g' /etc/apt/sources.list

RUN apt-get update && \
    apt-get -y -q --no-install-recommends install \
    vim less procps unzip wget tzdata openjdk-11-jdk \
    ffmpeg mediainfo \
    tesseract-ocr tesseract-ocr-ara tesseract-ocr-ces \
    tesseract-ocr-chi-sim tesseract-ocr-chi-tra tesseract-ocr-dan \
    tesseract-ocr-deu tesseract-ocr-fin tesseract-ocr-fra \
    tesseract-ocr-heb tesseract-ocr-hin tesseract-ocr-hun \
    tesseract-ocr-ita tesseract-ocr-jpn tesseract-ocr-kor \
    tesseract-ocr-lav tesseract-ocr-nld tesseract-ocr-nor \
    tesseract-ocr-pol tesseract-ocr-por tesseract-ocr-rus \
    tesseract-ocr-spa tesseract-ocr-swe tesseract-ocr-tha \
    tesseract-ocr-tur tesseract-ocr-ukr tesseract-ocr-vie \
    tesseract-ocr-sqi \
    && apt-get clean && \
    rm -rf /var/lib/apt/lists/*

RUN dpkg-reconfigure -f noninteractive tzdata

RUN wget -U "Mozilla/5.0" -nv -O /tmp/jetty.tar.gz \
    "https://maven.aliyun.com/repository/central/org/eclipse/jetty/jetty-home/${JETTY_VERSION}/jetty-home-${JETTY_VERSION}.tar.gz" \
    && tar xzf /tmp/jetty.tar.gz -C /opt \
    && mv /opt/jetty* /opt/jetty \
    && useradd jetty -U -s /bin/false \
    && chown -R jetty:jetty /opt/jetty \
    && mkdir /opt/jetty/webapps \
    && chmod +x /opt/jetty/bin/jetty.sh

EXPOSE 8080

RUN mkdir /app && \
    cd /app && \
    java -jar /opt/jetty/start.jar --add-modules=server,http,webapp,deploy

COPY --from=builder /build/docs.xml /app/webapps/docs.xml
COPY --from=builder /build/docs-web/target/docs-web-*.war /app/webapps/docs.war

WORKDIR /app

CMD ["java", "-jar", "/opt/jetty/start.jar"]
