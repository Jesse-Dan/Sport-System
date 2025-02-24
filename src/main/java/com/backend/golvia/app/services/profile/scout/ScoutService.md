The `ScoutService` class centralizes the business logic for managing scout data, ensuring that operations related to scouts are consistently handled across the application.

---

# Scout Service Documentation

The `ScoutService` class provides methods for handling `Scout` entities and managing their conversions to `ScoutDTO` objects. This service communicates with the `ScoutRepository` to perform CRUD operations and additional searches on scout data.

## Dependencies

- `ScoutRepository` – Repository for performing database operations on `Scout` entities.

## Methods

### Private Methods

- **`convertToDTO(Scout scout): ScoutDTO`**
    - Converts a `Scout` entity to a `ScoutDTO`.
    - Sets values for `id`, `name`, `country`, `specialization`, `yearsOfExperience`, `affiliatedOrganization`, and `certification`.

### Public Methods

1. **`findByCountry(String country): List<ScoutDTO>`**
    - Retrieves all scouts matching the specified `country`.

2. **`findBySpecialization(String specialization): List<ScoutDTO>`**
    - Retrieves all scouts with the specified specialization.

3. **`findByAffiliatedOrganization(String organization): List<ScoutDTO>`**
    - Retrieves scouts affiliated with the specified organization.

4. **`findByExperienceGreaterThan(int years): List<ScoutDTO>`**
    - Retrieves scouts with experience greater than the specified number of years.

5. **`findTopExperienced(int limit): List<ScoutDTO>`**
    - Retrieves the top experienced scouts, limited by the provided number.

6. **`findByPreferredRegion(String region): List<ScoutDTO>`**
    - Retrieves scouts based in the specified preferred region.

7. **`findByCertification(String certification): List<ScoutDTO>`**
    - Retrieves scouts with the specified certification.

8. **`findByCountryAndSpecialization(String country, String specialization): List<ScoutDTO>`**
    - Retrieves scouts matching the specified country and specialization.

9. **`findByNotableTalent(String talent): List<ScoutDTO>`**
    - Retrieves scouts noted for their notable talent.

10. **`countByExperienceGreaterThan(int years): int`**
    - Returns the number of scouts with experience greater than the specified years.

11. **`findAllScouts(): List<ScoutDTO>`**
    - Retrieves all scouts.

12. **`saveScout(ScoutDTO scoutDto): ScoutDTO`**
    - Saves a new scout to the database by converting `ScoutDTO` to `Scout`.

13. **`updateScout(Long id, ScoutDTO updatedDto): Optional<ScoutDTO>`**
    - Updates an existing scout with new information if it exists.

14. **`deleteScout(Long id): void`**
    - Deletes a scout by its ID if it exists in the database.

15. **`existsById(Long id): boolean`**
    - Checks if a scout exists by its ID.

16. **`findScoutsByCriteria(String country, String specialization): List<ScoutDTO>`**
    - Finds scouts by multiple criteria, such as `country` and `specialization`.

## Service Developer

- `Full Name` - Jesse Oyofo Dan-Amuda

--- 