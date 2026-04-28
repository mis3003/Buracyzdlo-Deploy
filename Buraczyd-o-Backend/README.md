# Buraczydlo Backend

A Spring Boot application with user registration and login functionality using JWT authentication.

## Features

- User registration and login with JWT authentication
- Password encryption using BCrypt
- Role-based authorization with Spring Security
- Customized access denied handling
- H2 in-memory database for development
- CRUD operations for Users, Playlists, and Songs

## Technologies

- Spring Boot 3.4.5
- Spring Security
- JSON Web Tokens (JWT)
- BCrypt
- Maven
- H2 Database

## Getting Started

To get started with this project, you will need to have the following installed on your local machine:

- JDK 17+
- Maven 3+

### Build and Run

1. Clone the repository
2. Navigate to the project directory
3. Build the project: `mvn clean install`
4. Run the application: `mvn spring-boot:run`

The application will start running at http://localhost:8080.

## API Endpoints

### Authentication

| Method | Url | Description | Sample Valid Request Body |
| ------ | --- | ----------- | ------------------------- |
| POST   | /api/auth/signup | Sign up | [JSON](#signup) |
| POST   | /api/auth/signin | Sign in | [JSON](#signin) |

### User Endpoints

| Method | Url | Description | Access | Sample Request Body |
| ------ | --- | ----------- | ------ | ------------------ |
| GET    | /api/users | Get all users | ROLE_ADMIN | - |
| GET    | /api/users/{id} | Get user by ID | ROLE_USER, ROLE_ADMIN | - |
| POST   | /api/users | Create a new user | ROLE_ADMIN | [JSON](#user-create) |
| PUT    | /api/users/{id} | Update a user | ROLE_USER, ROLE_ADMIN | [JSON](#user-update) |
| DELETE | /api/users/{id} | Delete a user | ROLE_ADMIN | - |

### Playlist Endpoints

| Method | Url | Description | Access | Sample Request Body |
| ------ | --- | ----------- | ------ | ------------------ |
| GET    | /api/playlists | Get all playlists | ROLE_ADMIN | - |
| GET    | /api/playlists/user/{userId} | Get playlists by user ID | ROLE_USER, ROLE_ADMIN | - |
| GET    | /api/playlists/{id} | Get playlist by ID | ROLE_USER, ROLE_ADMIN | - |
| POST   | /api/playlists | Create a new playlist | ROLE_USER, ROLE_ADMIN | [JSON](#playlist-create) |
| PUT    | /api/playlists/{id} | Update a playlist | ROLE_USER, ROLE_ADMIN | [JSON](#playlist-update) |
| DELETE | /api/playlists/{id} | Delete a playlist | ROLE_USER, ROLE_ADMIN | - |

### Song Endpoints

| Method | Url | Description | Access | Sample Request Body |
| ------ | --- | ----------- | ------ | ------------------ |
| GET    | /api/songs | Get all songs | ROLE_ADMIN | - |
| GET    | /api/songs/playlist/{playlistId} | Get songs by playlist ID | ROLE_USER, ROLE_ADMIN | - |
| GET    | /api/songs/{id} | Get song by ID | ROLE_USER, ROLE_ADMIN | - |
| POST   | /api/songs | Create a new song | ROLE_USER, ROLE_ADMIN | [JSON](#song-create) |
| PUT    | /api/songs/{id} | Update a song | ROLE_USER, ROLE_ADMIN | [JSON](#song-update) |
| DELETE | /api/songs/{id} | Delete a song | ROLE_USER, ROLE_ADMIN | - |

### Test Endpoints

| Method | Url | Description | Access |
| ------ | --- | ----------- | ------ |
| GET    | /api/test/all | Get public content | Public |
| GET    | /api/test/user | Get user content | ROLE_USER, ROLE_ADMIN |
| GET    | /api/test/admin | Get admin content | ROLE_ADMIN |

## Sample Valid JSON Request Bodies

### Authentication

##### <a id="signup">Sign Up -> /api/auth/signup</a>
```json
{
  "username": "user",
  "email": "user@example.com",
  "password": "password",
  "roles": ["ROLE_USER"]
}
```

##### <a id="signin">Sign In -> /api/auth/signin</a>
```json
{
  "username": "user",
  "password": "password"
}
```

### User Management

##### <a id="user-create">Create User -> /api/users</a>
```json
{
  "login": "newuser",
  "email": "newuser@example.com",
  "password": "password"
}
```

##### <a id="user-update">Update User -> /api/users/{id}</a>
```json
{
  "login": "updateduser",
  "email": "updated@example.com",
  "password": "newpassword"
}
```

### Playlist Management

##### <a id="playlist-create">Create Playlist -> /api/playlists</a>
```json
{
  "userId": 1,
  "name": "My Favorite Songs"
}
```

##### <a id="playlist-update">Update Playlist -> /api/playlists/{id}</a>
```json
{
  "userId": 1,
  "name": "Updated Playlist Name"
}
```

### Song Management

##### <a id="song-create">Create Song -> /api/songs</a>
```json
{
  "songUrl": "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
  "platform": "YouTube",
  "playlistId": 1
}
```

##### <a id="song-update">Update Song -> /api/songs/{id}</a>
```json
{
  "songUrl": "https://open.spotify.com/track/4cOdK2wGLETKBW3PvgPWqT",
  "platform": "Spotify",
  "playlistId": 1
}
```

## Sample Valid JSON Response Bodies

### Authentication

##### <a id="signup-response">Sign Up Response</a>
```json
{
  "message": "User registered successfully!"
}
```

##### <a id="signin-response">Sign In Response</a>
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "user",
  "email": "user@example.com",
  "roles": ["ROLE_USER"]
}
```

### User Management

##### <a id="user-response">User Response</a>
```json
{
  "id": 1,
  "login": "user",
  "email": "user@example.com"
}
```

##### <a id="user-delete-response">User Delete Response</a>
```json
{
  "message": "User deleted successfully"
}
```

### Playlist Management

##### <a id="playlist-response">Playlist Response</a>
```json
{
  "playlistId": 1,
  "userId": 1,
  "name": "My Favorite Songs"
}
```

##### <a id="playlist-delete-response">Playlist Delete Response</a>
```json
{
  "message": "Playlist deleted successfully"
}
```

### Song Management

##### <a id="song-response">Song Response</a>
```json
{
  "songId": 1,
  "songUrl": "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
  "platform": "YouTube",
  "playlistId": 1
}
```

##### <a id="song-delete-response">Song Delete Response</a>
```json
{
  "message": "Song deleted successfully"
}
```

## Security

The application uses JWT for authentication and authorization. When a user signs in, a JWT token is generated and returned to the client. This token must be included in the Authorization header of subsequent requests to access protected resources.

Example:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```
