
---

# ScoutController Documentation

The `ScoutController` class provides RESTful API endpoints for managing `Scout` entities using the `ScoutService`. This controller allows clients to perform various operations related to scouts, with structured responses for consistent client interaction.

## Endpoints

### 1. **Get Scouts by Country**
- **URL**: `/api/scouts/country/{country}`
- **Method**: `GET`
- **Response**: `200 OK` with a list of scouts from the specified country.

### 2. **Get Scouts by Specialization**
- **URL**: `/api/scouts/specialization/{specialization}`
- **Method**: `GET`
- **Response**: `200 OK` with a list of scouts specialized in the specified area.

### 3. **Get Scouts by Affiliated Organization**
- **URL**: `/api/scouts/organization/{organization}`
- **Method**: `GET`
- **Response**: `200 OK` with a list of scouts affiliated with the specified organization.

### 4. **Get Scouts by Experience Greater Than**
- **URL**: `/api/scouts/experience-greater-than/{years}`
- **Method**: `GET`
- **Response**: `200 OK` with scouts having experience greater than the specified years.

### 5. **Get Top Experienced Scouts**
- **URL**: `/api/scouts/top-experienced/{limit}`
- **Method**: `GET`
- **Response**: `200 OK` with a list of the top experienced scouts, limited by the specified number.

### 6. **Get Scouts by Preferred Region**
- **URL**: `/api/scouts/preferred-region/{region}`
- **Method**: `GET`
- **Response**: `200 OK` with scouts from the specified preferred region.

### 7. **Get Scouts by Certification**
- **URL**: `/api/scouts/certification/{certification}`
- **Method**: `GET`
- **Response**: `200 OK` with a list of scouts having the specified certification.

### 8. **Get Scouts by Country and Specialization**
- **URL**: `/api/scouts/country/{country}/specialization/{specialization}`
- **Method**: `GET`
- **Response**: `200 OK` with a list of scouts matching the specified country and specialization.

### 9. **Get Scouts by Notable Talent**
- **URL**: `/api/scouts/notable-talent/{talent}`
- **Method**: `GET`
- **Response**: `200 OK` with scouts noted for their notable talent.

### 10. **Count Scouts by Experience Greater Than**
- **URL**: `/api/scouts/count-experience-greater-than/{years}`
- **Method**: `GET`
- **Response**: `200 OK` with the count of scouts having experience greater than the specified years.

### 11. **Save Scout**
- **URL**: `/api/scouts`
- **Method**: `POST`
- **Request Body**: `ScoutDTO` object representing the scout to be created.
- **Response**: `201 Created` with a message confirming the scout creation.

### 12. **Find Scout by ID**
- **URL**: `/api/scouts/{id}`
- **Method**: `GET`
- **Response**: `200 OK` with the scout details if found; `404 Not Found` if not found.

### 13. **Update Scout**
- **URL**: `/api/scouts/{id}`
- **Method**: `PUT`
- **Request Body**: `ScoutDTO` object representing the updated scout information.
- **Response**: `200 OK` with the updated scout details; `404 Not Found` if the scout does not exist.

### 14. **Delete Scout**
- **URL**: `/api/scouts/{id}`
- **Method**: `DELETE`
- **Response**: `200 OK` with a message confirming the deletion; `404 Not Found` if the scout does not exist.

### 15. **Find All Scouts**
- **URL**: `/api/scouts`
- **Method**: `GET`
- **Response**: `200 OK` with a list of all scouts.

## Service Developer

- **Full Name**: Jesse Oyofo Dan-Amuda

---