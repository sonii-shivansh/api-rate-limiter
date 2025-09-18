# Contributing to the High-Throughput API Rate Limiter

First off, thank you for considering contributing! It's people like you that make the open-source community such a great place. We welcome contributions of all kinds, from fixing bugs and adding new features to improving documentation and reporting issues.

## Code of Conduct

To ensure a welcoming and inclusive environment, we expect all contributors to adhere to our [Code of Conduct](CODE_OF_CONDUCT.md). Please read it before contributing.

## How Can I Contribute?

There are many ways to contribute to this project. Here are a few ideas:

* **Reporting Bugs:** If you find a bug, please open an issue and provide as much detail as possible, including steps to reproduce it.
* **Suggesting Enhancements:** If you have an idea for a new feature or an improvement to an existing one, open an issue to discuss it.
* **Submitting a Pull Request:** If you're ready to contribute code or documentation, we'd love to see your pull request.

### Finding an Issue to Work On

We have a list of issues that are great for getting started. Look for issues labeled with ["good first issue"](https://github.com/sonii-shivansh/api-rate-limiter/labels/good%20first%20issue) to find tasks that are well-suited for new contributors.

### Development Setup

To get started with development, you'll need to set up the project on your local machine.

**Prerequisites:**

* Java 21
* Maven
* Docker and Docker Compose

**Steps:**

1.  **Fork the repository** on GitHub.
2.  **Clone your fork** to your local machine:
    ```bash
    git clone https://github.com/sonii-shivansh/api-rate-limiter.git
    cd api-rate-limiter
    ```
3.  **Build and run the services using Docker Compose:**
    ```bash
    docker-compose up --build -d
    ```
    This will build the Docker images for all services and start them in detached mode.
4.  **Run the tests** to ensure everything is set up correctly. In each service directory (`api-gateway`, `rate-limiter-service`, `product-service`), run:
    ```bash
    ./mvnw test
    ```

### Submitting a Pull Request

1.  Create a new branch for your changes:
    ```bash
    git checkout -b your-feature-branch
    ```
2.  Make your changes and commit them with a clear and descriptive commit message.
3.  Push your branch to your fork on GitHub:
    ```bash
    git push origin your-feature-branch
    ```
4.  Open a pull request from your fork to the `main` branch of the original repository.
5.  In your pull request description, provide a clear explanation of the changes you've made and reference any related issues.
6.  Ensure that all tests are passing in the CI/CD pipeline.

### Styleguides

* **Git Commit Messages:** Follow the [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) specification.
* **Java Styleguide:** Follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

We look forward to your contributions!
