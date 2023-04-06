# sql-injection

This is a sandbox containing multiple flaws, including SQL injection.

## How to

### Run

This is a spring boot app using Maven, to run it :

```bash
mvn spring-boot:run
```

It will start a web sever listening to http://localhost:8080

### Break

To use the app normally, register a new user and use the home page to query the database in an authenticated manner.

**But**, the login form is vulnerable to SQL injection, try to break the app !

Hint: `toto'; DROP TABLE "user"; --` 💥
