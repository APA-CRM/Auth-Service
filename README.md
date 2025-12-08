# APA-CRM Auth-Service

A Spring Boot microservice providing authentication and authorization for APA-CRM. This service issues JWTs and manages users, roles, password security, sessions, and works as the central identity provider—integrated with Discovery-and-Gateway for service routing and authentication enforcement throughout the CRM backend.
---

## Features

- User registration and login
- JWT issuance (access & refresh tokens)
- Role-based access control (RBAC)
- Password reset via email
- Token refresh, logout
- Health, metrics, and audit logging
- Integration-ready with Spring Cloud Gateway/Discovery


---

## Contribution Guidelines

We welcome contributions! To get started:

### Development

- Fork this repository and clone your fork locally.
- Create a feature branch:
  ```sh
  git checkout -b feature/your-feature-name
  ```
- Write your code following [Google Java Styleguide](https://google.github.io/styleguide/javaguide.html) or project-specific conventions.
- Include meaningful commit messages and comments.

### Pull Requests

- Open a Pull Request (PR) against the `develop` branch.
- Fill out the PR template with context about your change.
- Ensure your PR passes CI/CD checks (see workflows in `.github/`).
- Respond to code review feedback promptly.

### Issues

- Use [GitHub Issues](https://github.com/APA-CRM/Auth-Service/issues) for bugs or feature requests.
- Please include reproduction steps and environment details for bugs.

### Code of Conduct

- Be respectful and constructive in issues, PRs, and discussions.
- Follow the [Contributor Covenant](https://www.contributor-covenant.org/) where applicable.

## License

This project is licensed under the terms found in the [LICENSE](./LICENSE) file.

## Contact & Support

For help or questions, please open an issue or contact the maintainers via GitHub.

---
