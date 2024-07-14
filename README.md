# Product Trial API

## Description
Product Trial API is a backend application for managing products in a shop.

## Technologies Used
- Java 17
- Spring Boot 3.3.1
- PostgreSQL (as database)
- MapStruct (for object mapping)
- Lombok (for reducing boilerplate code)
- Specification Arg Resolver (for dynamic querying)
- Springdoc OpenAPI (for API documentation)

## Features
- **Listing Products**: Retrieve a list of products with filtering and pagination.
- **Fetching Product by ID**: Retrieve details of a specific product.
- **Updating Product**: Update all or partial fields of a product.
- **Creating Product**: Add a new product to the database.
- **Deleting Product**: Remove a product from the database.

## APIs
- **GET /api/v1/products**: Retrieve paginated list of products.
  - Query Parameters:
    - `name_startsWith`: Filter by product name starting with specified string.
    - `code_startsWith`: Filter by product code starting with specified string.
    - `price_equals`: Filter by product price equal to specified value.
    - `rating_equals`: Filter by product rating equal to specified value.
    - `quantity_equals`: Filter by product quantity equal to specified value.
    - `inventoryStatus_equals`: Filter by inventory status equal to specified value.
    - `category_equals`: Filter by product category equal to specified value.
    - `sort`: Sort criteria (default: 'name,asc').
    - `page`: Page number (default: 1).
    - `size`: Number of items per page (default: 20).

- **GET /api/v1/products/{id}**: Retrieve details of a specific product by ID.
- **PUT /api/v1/products/{id}**: Update all fields of a product.
- **PATCH /api/v1/products/{id}**: Update specific fields of a product.
- **POST /api/v1/products**: Create a new product.
- **DELETE /api/v1/products/{id}**: Delete a product by ID.
- **DELETE /api/v1/products/delete**: Delete a list of products by IDs.
