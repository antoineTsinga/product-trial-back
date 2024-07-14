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
## API Reference

#### Retrieve paginated list of products

```http
  GET /api/v1/products
```
| Query Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `name_startsWith`      | `string` | Filter by product name starting with specified string |
| `code_startsWith`      | `string` | Filter by product code starting with specified string. |
| `price_equals`      | `float` | Filter by product price equal to specified value.|
| `rating_equals`      | `float` |  Filter by product rating equal to specified value. |
| `quantity_equals`      | `integer` |   Filter by product quantity equal to specified value. |
| `inventoryStatus_equals`      | `string` | Filter by inventory status equal to specified value. |
| `category_equals`      | `string` |Filter by product category equal to specified value.|
| `sort`      | `string` | Sort criteria (default: 'name,asc'). |
| `page`      | `integer` |  Page number (default: 1). |
| `size`      | `integer` | Page number (default: 1). |


#### Retrieve details of a specific product by ID.

```http
  GET /api/v1/products/{id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `integer` | **Required**. Id of product to fetch |

####  Create a new product.

```http
  POST /api/v1/products
```

| Body | Type     |
| :-------- | :------- |
| `code`      | `string` |
| `name`      | `string` |
| `description`      | `string` |
| `image`      | `string` |
| `price`      | `float` |
| `category`      | `string` |
| `quantity`      | `integer` |
| `inventoryStatus`      | `string` |
| `rating`      | `float` |


#### Update specific fields of a product

```http
  PATCH /api/v1/products/{id}
```

| Body | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `code`      | `string` | **Optional**.  |
| `name`      | `string` | **Optional**.  |
| `description`      | `string` | **Optional**.  |
| `image`      | `string` | **Optional**.  |
| `price`      | `float` | **Optional**.  |
| `category`      | `string` | **Optional**.  |
| `quantity`      | `integer` | **Optional**.  |
| `inventoryStatus`      | `string` | **Optional**.  |
| `rating`      | `float` | **Optional**.  |


#### Update all fields of a product

```http
  PUT /api/v1/products/{id}
```

| Body | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `code`      | `string` | **Required**.  |
| `name`      | `string` | **Required**.  |
| `description`      | `string` | **Required**.  |
| `image`      | `string` | **Required**.  |
| `price`      | `float` | **Required**.  |
| `category`      | `string` | **Required**.  |
| `quantity`      | `integer` | **Required**.  |
| `inventoryStatus`      | `string` | **Required**.  |
| `rating`      | `float` | **Required**.  |

#### Delete a product by ID.
```http
  DELETE /api/v1/products/{id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `integer` | **Required**. Id of product to delete |


#### Delete a list of products by IDs.
```http
  DELETE /api/v1/products/delete
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `ids`      | `integer[]` | **Required**. List of ids to delete |

