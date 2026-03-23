# RecoverAI

A satirical Spring Boot application that helps organizations recover from "AI psychosis" - the overenthusiastic and reckless adoption of AI technologies.

## How It Works

Run the `diagnose` command in the interactive shell to start a guided questionnaire. The application will:

1. **Intake**: Collect symptoms across three categories (Delusion of Velocity, Hallucinatory Resource Planning, Reality Detachment)
2. **Diagnosis**: Evaluate the stage of AI psychosis (Prodromal, Acute, or Residual)
3. **Recovery Planning**: Generate a recovery strategy tailored to the affected discipline
4. **Diplomatic Communication**: Produce a script to address the issue with leadership

## Symptom Categories

| Category | Description |
|----------|-------------|
| **Delusion of Velocity** | Obsession with AI-generated output metrics (PRs, lines of code) |
| **Hallucinatory Resource Planning** | Belief that AI replaces human expertise and capacity |
| **Reality Detachment** | Disconnection from actual costs, outcomes, and accountability |

## Stages of AI Psychosis

| Stage | Description |
|-------|-------------|
| **Prodromal** | Unusual interest, AI-first hype, mild detachment from reality |
| **Acute** | Hallucinations. Managers pushing AI code to prod without PRs |
| **Residual** | The crash. Tech debt, broken production, team burnout/cynicism |

## Supported Disciplines

Engineering, Product, Dev Management, Leadership, Marketing, Finance, and HR

## Requirements

- Java 21
- Maven

## Configuration

Set the following environment variables for LLM configuration:

```bash
OPENAI_CUSTOM_BASE_URL=https://api.example.com
OPENAI_CUSTOM_MODELS=model-name
OPENAI_CUSTOM_API_KEY=your_api_key
```

Optionally, set `OPENAI_CUSTOM_COMPLETIONS_PATH` if the completions endpoint differs from the default OpenAI path (`/v1/chat/completions`):

```bash
OPENAI_CUSTOM_COMPLETIONS_PATH=/custom/chat/completions
```

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

Once running, use the `diagnose` command to start the assessment.

### Available Commands

| Command | Description |
|---------|-------------|
| `diagnose` | Run the full diagnostic workflow |
| `memoryFetch <query>` | Search memory notes using vector similarity |
| `memoryWrite <text>` | Store text in memory for later retrieval |

## Technology Stack

- Spring Boot 3.5
- [Embabel Agent](https://github.com/embabel/embabel-agent) 0.3.4 - AI agent framework
- [Archiledger](https://github.com/thecookiezen/archiledger) - Knowledge graph builder using memory notes, where the LLM classifies AI psychosis analyses
- Spring Shell - Interactive CLI

## License

See [LICENSE](LICENSE) file for details.
