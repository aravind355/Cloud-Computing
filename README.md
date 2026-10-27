# Cloud Computing Lab — 6th Semester

**Registration Number:** 23MCCE11  
**Course:** Integrated M.Tech | Cloud Computing Lab

---

## Exercise Index

| Exercise | Topic | Source Code |
|---|---|---|
| [Ex1](./Ex1/) | AWS account setup — IAM users, EC2 console overview, S3 bucket creation via GUI | — |
| [Ex2](./Ex2/) | EC2 instance launch (Amazon Linux, eu-north-1), SSH access via WSL instead of PuTTY | — |
| [Ex3](./Ex3/) | EBS volume creation, partitioning (`nvme1n1`), mounting to `/var/www/html`, persistent storage across reboots | — |
| [Ex4](./Ex4/) | Multi-threaded Gutenberg word count on Windows (`wmic`, `systeminfo`); Monte Carlo Pi estimation; single-thread vs multi-thread vs thread-pool comparison | `gutenberg.java`, `gutenbergMT.java`, `gutenbergTP.java`, `gutenberg3name.java`, `montecarlopi.java` |
| [Ex5](./Ex5/) | Distributed master-worker system over TCP sockets — master distributes Project Gutenberg books to workers; RSA key-based SSH login for passwordless transfer; sequence diagram for master-worker flow | `q1/`, `q2/`, `q3/`, `Stream socket code/` |
| [Ex6](./Ex6/) | RMI-based distributed Pi calculation (multi-threaded server, remote method invocation); REST API client in Python (Flask/requests); socket-level Pi calculation client-server | `q1/client.java`, `q1/server.java`, `q1/piCal.java`, `rest_example.py` |
| [Ex7](./Ex7/) | Docker volumes — host directory as data volume (read-only vs read-write), data volume containers with shared mounts between containers | — |
| [Ex8](./Ex8/) | Docker Compose — multi-container setup with Nginx + PostgreSQL; volume persistence test by removing and re-creating containers; `docker compose up/down` | — |
| [Ex9](./Ex9/) | Docker networking and load balancing — Python script to fetch container home page (no curl available), multi-container request routing, load generator testing | — |
| [Ex10](./Ex10/) | Kubernetes with Helm — deploying `my-nginx` via Bitnami Helm chart, `kubectl logs`, rolling 10-request loop to test load distribution across pods | — |

---

## Folder Structure

Each exercise folder contains:
- `23MCCE11_EX*.pdf` — completed lab submission with screenshots and written explanation
- `Exercise_*.pdf` — original lab question paper
- `*.java` / `*.py` — source code (Ex4, Ex5, Ex6 only)

> Screenshots are taken during the lab, embedded in the Word document, and exported as the PDF submission.

---

## Tools & Technologies

| Area | Tools |
|---|---|
| Cloud Platform | AWS (EC2, EBS, S3, IAM) |
| Containerisation | Docker, Docker Compose |
| Orchestration | Kubernetes, Helm (Bitnami) |
| Programming | Java (JDK 8+), Python 3 |
| Distributed concepts | TCP Sockets, RMI, Master-Worker, Multi-threading |
| OS / Access | Amazon Linux, WSL (Windows Subsystem for Linux), SSH |

---

## Notes

- `.pem` key pair files are excluded via `.gitignore` — private keys are never committed.
- Compiled `.class` and `.pyc` files, `.docx` source documents, and raw `.png` screenshots are all excluded — the PDF submissions contain everything.
