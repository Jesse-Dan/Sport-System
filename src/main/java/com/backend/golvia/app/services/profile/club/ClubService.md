# ClubService Documentation

The `ClubService` class provides methods for handling `Club` entities and managing their conversions to `ClubDTO` objects. This service communicates with the `ClubRepository` to perform CRUD operations and additional searches on club data.

## Dependencies

- `ClubRepository` – Repository for performing database operations on `Club` entities.

## Methods

### Private Methods

- **`convertToDTO(Club club): ClubDTO`**
    - Converts a `Club` entity to a `ClubDTO`.
    - Sets values for `id`, `clubName`, `country`, `city`, `competitionLevel`, `contactEmail`, and `website`.

### Public Methods

1. **`findByCountry(String country): List<ClubDTO>`**
    - Retrieves all clubs matching the specified `country`.

2. **`findByCompetitionLevel(String level): List<ClubDTO>`**
    - Retrieves all clubs matching the specified competition level.

3. **`findByRecruitmentArea(String area): List<ClubDTO>`**
    - Retrieves clubs based on their recruitment area.

4. **`findByClubNameContaining(String name): List<ClubDTO>`**
    - Retrieves clubs whose names contain the specified substring.

5. **`findMostRecentlyAdded(int limit): List<ClubDTO>`**
    - Retrieves the most recently added clubs, limited by the provided number.

6. **`findByCity(String city): List<ClubDTO>`**
    - Retrieves clubs based in the specified city.

7. **`findByContactEmail(String email): Optional<ClubDTO>`**
    - Finds a club by its contact email. Returns an empty result if no club matches.

8. **`countByCompetitionLevel(String level): int`**
    - Returns the number of clubs with the specified competition level.

9. **`findByExactName(String name): Optional<ClubDTO>`**
    - Finds a club by its exact name.

10. **`findByWebsiteContaining(String url): List<ClubDTO>`**
    - Finds clubs with websites containing the specified URL substring.

11. **`findAllClubs(): List<ClubDTO>`**
    - Retrieves all clubs.

12. **`saveClub(ClubDTO clubDto): ClubDTO`**
    - Saves a new club to the database by converting `ClubDTO` to `Club`.

13. **`updateClub(Long id, ClubDTO updatedDto): Optional<ClubDTO>`**
    - Updates an existing club with new information, if it exists.

14. **`deleteClub(Long id): void`**
    - Deletes a club by its ID if it exists in the database.

15. **`existsById(Long id): boolean`**
    - Checks if a club exists by its ID.

16. **`findClubsByCriteria(String country, String level): List<ClubDTO>`**
    - Finds clubs by multiple criteria, such as `country` and `competitionLevel`.

## Service developer

- `Full Name` - Jesse Oyofo Dan-Amuda