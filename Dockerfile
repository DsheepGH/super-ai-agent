# 使用预装 Maven 和 JDK21 的镜像
FROM maven:3.9-amazoncorretto-21
WORKDIR /app

# 只复制必要的源代码和配置文件
COPY pom.xml .
COPY src ./src

# 使用 Maven 执行打包
RUN mvn clean package -DskipTests

# 暴露应用端口
EXPOSE 8123

# 小规格容器里 Spring Boot 启动慢是"部署失败"的常见原因，下面几个参数都是为了抢时间：
# -Djava.security.egd      容器熵不足时避免 SecureRandom 阻塞
# -XX:TieredStopAtLevel=1  只做 C1 编译，省掉 C2 预热，启动更快
# -XX:+UseSerialGC         小内存单核场景下 GC 线程更少、启动更快
# -XX:MaxRAMPercentage=75  按容器内存上限分配堆，避免默认值过小导致 OOM
# -Xlog:os+container=info  启动时打印识别到的容器 CPU/内存限制，方便核对规格
CMD ["java","-Djava.security.egd=file:/dev/./urandom","-XX:TieredStopAtLevel=1","-XX:+UseSerialGC","-XX:MaxRAMPercentage=75.0","-Xss512k","-Dfile.encoding=UTF-8","-Xlog:os+container=info","-jar","/app/target/super-ai-agent-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]
