# Pingo Family

## Overview
Pingo Family is a foreign language vocabulary learning application supporting English, German, and Spanish. The application is designed to optimize the learning process through adaptive training algorithms, spaced repetition, and AI-generated content, providing a personalized and data-driven learning experience.

---

## Core Features

### Vocabulary Management
Users can organize their vocabulary through a structured system of word groups and word lists. For every word added to the application, the system automatically generates supplementary linguistic data via the **OpenAI API**, including:
- Thesaurus entries
- Contextual example sentences (used directly in training exercises)
- Word conjugations
- Audio pronunciation of the word and its usage in context

### Training Module
The training module consists of **four distinct exercise types**, each targeting different aspects of language acquisition:

1. **Translation Input** — The user types the translation of a given word.
2. **Audio-Based Recognition** — The user transcribes a word after hearing its audio.
3. **Fill-in-the-Blank** — The user completes a missing word within a sentence.
4. **Phrase Block Assembly** — The user reconstructs a phrase from word blocks, supporting both target-to-native and native-to-target language directions.

Incorrect answers trigger immediate repetition of the problematic item within the same session, continuing until the user provides the correct response or the session's task limit is reached.

### Adaptive Training Type Selection
Training type selection is **algorithmically driven** and adapts dynamically based on multiple performance signals. The initial training session follows a fixed sequence; all subsequent sessions are generated according to the following weighted factors:
- Time elapsed since the word was last trained
- Total number of previous training sessions for the word
- Historical user performance on the specific word
- Intrinsic difficulty rating of the training type
- The user's overall assessed language proficiency level, which is continuously updated by the system after each training session

### Spaced Repetition & Word Selection
Word selection for upcoming training sessions is powered by an implementation of the **Half-Life Regression (HLR)** model, as described in the research paper:

> Settles, B. & Meeder, B. (2016). [*A Trainable Spaced Repetition Model for Language Learning*](https://research.duolingo.com/papers/settles.acl16.pdf). ACL 2016.

The model was trained using a linear regression approach on the publicly available [Duolingo Spaced Repetition Dataset](https://www.kaggle.com/datasets/aravinii/duolingo-spaced-repetition-data), enabling the system to predict the optimal timing for re-exposing the user to a given word based on their historical recall performance.

Upon each successful completion of a word's training threshold, a new, fully customized exercise for that word is generated via the **OpenAI API**, ensuring training content remains varied and contextually relevant across sessions.

---

## Architecture & Infrastructure

### Configuration Server with mTLS
The backend employs a **dedicated configuration server** that enables real-time configuration updates without service restarts. Inter-service communication is secured using **mutual TLS (mTLS)**, ensuring that both server and config server authenticate each other via certificates, preventing unauthorized access to internal services.

### Deployment & Containerization
The entire application was **containerized using Docker** and orchestrated with **Docker Compose**, enabling consistent, reproducible deployments across development, testing, and production environments.

### Real-Time Vocabulary Synchronization
The frontend maintains a persistent **WebSocket connection** to the backend, enabling:
- Real-time delivery of vocabulary updates as they are processed
- Access to predefined, curated vocabularies available to users immediately upon registration

---

## Technology Highlights

### Frontend
| Area              | Technology     |
|-------------------|----------------|
| UI Framework      | React          |
| State Management  | Redux          |
| Real-Time Updates | WebSocket      |

### Backend
| Area                    | Technology              |
|-------------------------|-------------------------|
| Framework               | Spring Boot             |
| Security                | Spring Security         |
| Primary Database        | PostgreSQL              |
| Caching Layer           | Redis                   |
| Inter-Service Security  | Mutual TLS (mTLS)       |
| Configuration Management| Dedicated Config Server |
| Deployment              | Docker, Docker Compose  |

### AI & Machine Learning
| Area                | Technology / Approach                          |
|---------------------|------------------------------------------------|
| Spaced Repetition   | Half-Life Regression (HLR) with Linear Regression |
| Content Generation  | OpenAI API (examples, conjugations, pronunciation, exercises) |

### General
| Area                | Details                                      |
|---------------------|----------------------------------------------|
| Supported Languages | English, German, Spanish, (partially) Romanian |
