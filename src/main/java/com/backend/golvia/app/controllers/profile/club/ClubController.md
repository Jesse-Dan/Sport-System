# ClubController Documentation

The `ClubController` class provides RESTful API endpoints for managing `Club` entities using the `ClubService` and the `ResponseHelper` utility. This controller allows clients to perform CRUD operations, and search clubs, with structured responses for consistent client interaction.

## Endpoints

### 1. **Get All Clubs**
- **URL**: `/api/clubs`
- **Method**: `GET`
- **Response**: `200 OK` with `ResponseHelper`-formatted success response.

### 2. **Get Clubs by Country**
- **URL**: `/api/clubs/country/{country}`
- **Method**: `GET`
- **Response**: `200 OK` with list of clubs from specified country.

### 3. **Get Clubs by Competition Level**
- **URL**: `/api/clubs/competition-level/{level}`
- **Method**: `GET`
- **Response**: `200 OK` with clubs matching the competition level.

### 4. **Get Clubs by Recruitment Area**
- **URL**: `/api/clubs/recruitment-area/{area}`
- **Method**: `GET`
- **Response**: `200 OK` with clubs from specified recruitment area.

### 5. **Get Clubs by Name Containing Substring**
- **URL**: `/api/clubs/search/{name}`
- **Method**: `GET`
- **Response**: `200 OK` with list of clubs whose names contain the substring.

### 6. **Get Most Recently Added Clubs**
- **URL**: `/api/clubs/recent`
- **Method**: `GET`
- **Response**: `200 OK` with list of most recent clubs, limited by query parameter.

### 7. **Get Clubs by City**
- **URL**: `/api/clubs/city/{city}`
- **Method**: `GET`
- **Response**: `200 OK` with clubs based in specified city.

### 8. **Get Club by Contact Email**
- **URL**: `/api/clubs/email/{email}`
- **Method**: `GET`
- **Response**: `200 OK` with club found by email, or `404 Not Found` if unavailable.

### 9. **Count Clubs by Competition Level**
- **URL**: `/api/clubs/competition-level/{level}/count`
- **Method**: `GET`
- **Response**: `200 OK` with count of clubs at competition level.

### 10. **Get Club by Exact Name**
- **URL**: `/api/clubs/name/{name}`
- **Method**: `GET`
- **Response**: `200 OK` with club found by name, or `404 Not Found` if unavailable.

### 11. **Get Clubs by Website Containing Substring**
- **URL**: `/api/clubs/website/search/{url}`
- **Method**: `GET`
- **Response**: `200 OK` with list of clubs matching website substring.

### 12. **Create a New Club**
- **URL**: `/api/clubs`
- **Method**: `POST`
- **Response**: `201 Created` with saved club details in response.

### 13. **Update an Existing Club**
- **URL**: `/api/clubs/{id}`
- **Method**: `PUT`
- **Response**: `200 OK` with updated club, or `404 Not Found` if unavailable.

### 14. **Delete a Club by ID**
- **URL**: `/api/clubs/{id}`
- **Method**: `DELETE`
- **Response**: `204 No Content` if deleted, `404 Not Found` if unavailable.

### 15. **Find Clubs by Criteria**
- **URL**: `/api/clubs/search`
- **Method**: `GET`
- **Response**: `200 OK` with clubs matching specified criteria.

---

The `ResponseHelper` class centralizes responses, ensuring status codes and JSON structures are consistent across endpoints.
