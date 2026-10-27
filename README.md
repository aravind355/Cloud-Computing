# Cloud Computing Lab — 6th Semester

**Registration Number:** 23MCCE11

Lab exercises for the Cloud Computing (CC) course, 6th semester. Each exercise covers a core cloud and distributed systems concept using AWS, Java, and Python.

---

## Exercise Index

| Exercise | Topic | Code |
|---|---|---|
| [Ex1](./Ex1/) | Introduction to AWS — EC2, S3, IAM setup | — |
| [Ex2](./Ex2/) | AWS EC2 instance launch and SSH access | — |
| [Ex3](./Ex3/) | AWS S3 bucket operations | — |
| [Ex4](./Ex4/) | Multi-threading and parallel word count (Gutenberg MapReduce in Java) | `gutenberg.java`, `gutenbergMT.java`, `gutenbergTP.java`, `montecarlopi.java` |
| [Ex5](./Ex5/) | Distributed master-worker communication over sockets (Java RMI-style) | `q1/master.java`, `q2/`, `q3/`, `Stream socket code/` |
| [Ex6](./Ex6/) | REST API client (Python) and distributed Pi calculation over sockets | `rest_example.py`, `q1/client.java`, `q1/server.java` |
| [Ex7](./Ex7/) | AWS Lambda and serverless functions | — |
| [Ex8](./Ex8/) | AWS RDS and cloud databases | — |
| [Ex9](./Ex9/) | AWS ECS / containerization and deployment | — |
| [Ex10](./Ex10/) | AWS CloudFormation / infrastructure as code | — |

---

## Folder Structure

Each exercise folder contains:
- `23MCCE11_EX*.pdf` — completed lab submission (screenshots + written explanation)
- `Exercise_*.pdf` — original lab question paper
- `*.java` / `*.py` — source code (where applicable)

> Screenshots are taken during exercises and embedded in the Word document, which is then exported as the PDF submission.

---

## Requirements (for code exercises)

- **Java** JDK 8 or newer — for Ex4, Ex5, Ex6 Java programs
- **Python 3** — for Ex6 REST API script
- AWS account and CLI configured — for Ex1–Ex3, Ex7–Ex10

---

## Notes

- `.pem` key pair files are excluded via `.gitignore` and are **not** tracked in this repository.
- Compiled `.class` and `.pyc` files are excluded.
- `.docx` and `.png` files are excluded — screenshots are embedded in the Word doc which is exported as the PDF submission.
