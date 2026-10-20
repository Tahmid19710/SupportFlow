# SupportFlow — Support Ticket Management System

**SupportFlow** is a web-based Support Ticket Management System built with Spring Boot. It helps customers submit support requests, track ticket status, and communicate with the support team through ticket conversations. Administrators can manage support tickets and respond to customer messages.

The project focuses on role-based access control, ticket management, and a user-friendly web interface.

## Key Features

### Authentication and Authorization
- User registration and login.
- Secure authentication using Spring Security.
- Role-based access control for different user roles.
- Separate dashboard experiences for customers and administrators.

### Customer Dashboard
- View an overview of support tickets.
- Create new support requests.
- View submitted tickets and their details.
- Track ticket status and priority.
- Cancel eligible tickets.
- Access ticket conversations and communicate with the support team.

### Admin Dashboard
- View an overview of ticket activity.
- Access and manage customer support tickets.
- Review customer information and ticket details.
- Update ticket status.
- Open customer conversations and reply to messages.

### Ticket Management
- Create support tickets with a title and description.
- Assign a priority level:
  - LOW
  - MEDIUM
  - HIGH
  - URGENT
- Track ticket status:
  - OPEN
  - IN_PROGRESS
  - RESOLVED
- View ticket creation information.
- Apply role-based access restrictions to ticket operations.

### Conversation System
- Open conversations associated with support tickets.
- Exchange messages between customers and administrators.
- Keep communication connected to the relevant ticket.

### User Interface
- Responsive Thymeleaf-based web pages.
- Dark-themed dashboard with purple gradient accents.
- Dedicated pages for registration, login, ticket creation, ticket details, messages, and conversations.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java | Backend programming language |
| Spring Boot | Application framework |
| Spring Security | Authentication and authorization |
| Spring Data JPA | Database persistence |
| Hibernate | ORM and entity management |
| PostgreSQL | Relational database |
| pgAdmin 4 | Database administration |
| Thymeleaf | Server-side HTML rendering |
| HTML5 | Web page structure |
| CSS3 | Styling and responsive layouts |
| Maven | Dependency and build management |
| Git | Version control |
| GitHub | Source code hosting |

## Project Structure

```text
support-ticket/
├── .mvn/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/tahmid/support_ticket/
│   │   │       ├── config/
│   │   │       ├── controller/
│   │   │       ├── entity/
│   │   │       ├── repository/
│   │   │       └── service/
│   │   └── resources/
│   │       ├── templates/
│   │       ├── static/
│   │       └── application.yml
│   └── test/
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

*Note: The package and directory listing above illustrates the expected organization. Actual files may vary as the project develops.*

## Prerequisites

Before running the application, install:

- Java Development Kit (JDK) compatible with the version configured in `pom.xml`.
- PostgreSQL.
- pgAdmin 4 (recommended for database management).
- Git.
- IntelliJ IDEA or another Java IDE.

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/Tahmid19710/SupportFlow.git
cd SupportFlow
```

If the repository contains the Spring Boot project in a nested directory, navigate to that directory before running the following commands.

### 2. Configure PostgreSQL

Create a PostgreSQL database for the application.

For example:

```sql
CREATE DATABASE support_ticket;
```

Configure the database connection in `src/main/resources/application.yml` using your own local database credentials.

Example configuration:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/support_ticket
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

  jpa:
    hibernate:
      ddl-auto: update
```

Use the settings appropriate to your existing application. If your current configuration differs, retain the settings required by your project.

**Security note:** Never commit real database passwords, API keys, or other secrets to GitHub.

### 3. Run the Application

On Windows, use the Maven wrapper:

```powershell
.\mvnw.cmd spring-boot:run
```

Alternatively, open the project in IntelliJ IDEA and run the main Spring Boot application class.

### 4. Open the Application

Once the application starts successfully, open:

```text
http://localhost:8080
```

Use the registration page to create a customer account, or sign in with an appropriately configured existing account.

Administrator access requires an account configured with the appropriate role. Do not assume that a newly registered account has administrator privileges.

## Application Workflow

1. A customer registers and signs in.
2. The customer creates a support request.
3. The request is saved and displayed in the customer's ticket list.
4. The customer views ticket details and tracks its status.
5. An administrator reviews the ticket and can update its status.
6. Customer and administrator communicate through the ticket conversation.
7. The customer checks the latest available ticket information from the dashboard.

## Security and Access Control

SupportFlow uses Spring Security to restrict access to application resources.

- Public pages include the routes configured for login, registration, and other public resources.
- Customer ticket routes are restricted according to the configured customer role.
- Administrator routes are restricted according to the configured administrator role.
- Authenticated users can access resources permitted by the security configuration.

Access rules should be reviewed whenever new controllers or endpoints are added.

## Development

To contribute to the project:

1. Fork or clone the repository.
2. Create a separate branch for your change.
3. Implement and test the change.
4. Commit your changes with a descriptive message.
5. Push the branch and open a pull request if working with collaborators.

Example:

```bash
git checkout -b feature/improve-ticket-management
git add .
git commit -m "Improve ticket management"
git push -u origin feature/improve-ticket-management
```

## Future Improvements

Potential future enhancements include:

- Automated email notifications.
- Ticket search, filtering, and pagination.
- More detailed activity history and audit logs.
- Improved automated test coverage.
- Deployment to a production hosting environment.
- Enhanced monitoring and operational logging.

These are potential improvements, not claims that the features are already implemented.

## Author

**Tahmid Anjum Mozumder**

GitHub: [Tahmid19710](https://github.com/Tahmid19710)

## License

No license has been specified yet. Unless a license is added to the repository, the source code should not be assumed to be available for unrestricted reuse or redistribution.
