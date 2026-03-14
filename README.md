# RecoverAI

A satirical Spring Boot application that helps organizations recover from "AI psychosis" - the overenthusiastic and reckless adoption of AI technologies.

## Features

- **Assessment**: Evaluates the stage of AI psychosis (Prodromal, Acute, or Residual)
- **Recovery Planning**: Generates recovery strategies for affected teams
- **Multi-discipline Support**: Covers Engineering, Product, Dev Management, Leadership, Marketing, Finance, and HR

## Requirements

- Java 21
- Maven
- API key for one of the supported model providers (auto-detected via environment variables):
  - OpenAI (`OPENAI_API_KEY`)
  - Anthropic (`ANTHROPIC_API_KEY`)
  - [ZhipuAI](https://docs.spring.io/spring-ai/reference/api/chat/zhipuai-chat.html) via custom OpenAI (`OPENAI_CUSTOM_API_KEY`)

## Building

```bash
mvn clean package
```

## Running

```bash
java -jar target/RecoverAI-0.0.1-SNAPSHOT.jar
```

Or with Maven:

```bash
mvn spring-boot:run
```

## Technology Stack

- Spring Boot 3.5
- [Embabel Agent](https://github.com/embabel/embabel-agent) - AI agent framework
- Spring Shell - Interactive CLI

## License

See [LICENSE](LICENSE) file for details.
