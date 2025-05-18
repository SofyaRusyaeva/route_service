# Микросервис для взаимодействия с маршрутами

---

## Аутентификация

### **Запрос:** `POST` `/auth`

**Тело запроса:**

Объект AuthDto

```json
{
  "login": "user",
  "password": "123"
}
```

### **Ответ:**

**Code** : `200 OK`

```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJYSXFiVkV1eEZPZ0FBNkltZm1EdVkwYUhudU5nSnhUYmZZYTQ0Uko4em53In0.eyJleHAiOjE3NDc1NjA0NTQsImlhdCI6MTc0NzU2MDE1NCwianRpIjoiZjc2ODk5YjAtN2E1NC00M2ZhLTgwYTUtNTcxYmZhZmEzMTQ4IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgxL3JlYWxtcy9teV9yZWFsbSIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiIzNjkwY2Y0Mi1iNDFmLTQ0YmEtODI2NC05ZjdkMjJkNmFmOGIiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJteV9jbGllbnQiLCJzaWQiOiI1ZGFjMDFjMy0zMDllLTRiMjctYWExNi01YjQwMzViYmM2YTEiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHA6Ly9sb2NhbGhvc3Q6ODA4MCJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy1teV9yZWFsbSIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIHByb2ZpbGUiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm5hbWUiOiJ1c2VyIHVzZXIiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ1c2VyIiwiZ2l2ZW5fbmFtZSI6InVzZXIiLCJmYW1pbHlfbmFtZSI6InVzZXIiLCJlbWFpbCI6InVzZXJAZ21haWwuY29tIn0.V4IEywpXuxc7odbxLwcYVZCeBRFNduBonLQJTWIt30gTi1UZOsEP4X3tbbZYTvZY2UnWorCADpzctOuh6HbvXdmG8-hfLnFPFOovKPris6I4MmbMQQjo_wT8woWxZEDV6RoH-uVBvuFxYbWYJfKgMe33nVPYSYqIta0sGIiCwhV9MezsWRtoHzRPzVHlXr8YaNFT45mt032heBxPQmp1Vd9ss4-AHnHlFvgm5yNLwJWpaIPSsDuhkfOQxgM7xzuwRubCRJyPPpG2tvsVm9gT_48tB4bmmTXxAH43gvb6eUh3wVSSYprHukiXvKHqDkBjhuqmRRC3NfyykGzsVfkWjw",
  "expires_in": 300,
  "refresh_expires_in": 1800,
  "refresh_token": "eyJhbGciOiJIUzUxMiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIyYTE2ZWRlZi0zMTgxLTRkNDUtOWFjYS1iMDJkYmYzNmJmMDIifQ.eyJleHAiOjE3NDc1NjE5NTQsImlhdCI6MTc0NzU2MDE1NCwianRpIjoiMmRkYjIyMGMtMTBkYi00NjQ1LWI4ZWMtMTQ3YjMyMDQ4MmRjIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgxL3JlYWxtcy9teV9yZWFsbSIsImF1ZCI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MS9yZWFsbXMvbXlfcmVhbG0iLCJzdWIiOiIzNjkwY2Y0Mi1iNDFmLTQ0YmEtODI2NC05ZjdkMjJkNmFmOGIiLCJ0eXAiOiJSZWZyZXNoIiwiYXpwIjoibXlfY2xpZW50Iiwic2lkIjoiNWRhYzAxYzMtMzA5ZS00YjI3LWFhMTYtNWI0MDM1YmJjNmExIiwic2NvcGUiOiJ3ZWItb3JpZ2lucyByb2xlcyBiYXNpYyBhY3IgZW1haWwgcHJvZmlsZSJ9.Fi2eVfFmW5hzjT5FC7ltTMrm9SMDEVt7GVxN8mO2zQPxYlJO10t8CwpXUuPWJZKyMVPKgplcd_MWub3SJmLV7w",
  "token_type": "Bearer",
  "not-before-policy": 0,
  "session_state": "5dac01c3-309e-4b27-aa16-5b4035bbc6a1",
  "scope": "email profile"
}
```

---

## Получить точку по ID

### **Запрос:** `GET` `/api/points/{pointId}`

### **Ответ:**

#### Возвращает объект PointDocument с данными точки

**Code** : `200 OK`

```json
{
  "pointId": "67fb759bb3ae645634f42568",
  "type": "Парк",
  "latitude": 53.227925,
  "longitude": 50.199265,
  "address": "парк культуры и отдыха имени Юрия Гагарина, Промышленный район, городской округ Самара",
  "locationData": {
    "name": "Парк культуры и отдыха имени Ю. А. Гагарина",
    "review": "Парк культуры и отдыха имени Ю. А. Гагарина — это популярное место для семейного отдыха в Самаре.",
    "attributions": {
      "opening_hours": "Круглосуточно",
      "website": "https://parki-samara.ru/park-im-yu-gagarina/"
    }
  }
}
```

#### Точка с указанным ID не найдена

**Code** : `404 Not Found`

```json
{
  "message": "Point with id 67fb759bb3ae645634f4256 not found",
  "status": 404
}
```

## Обновить существующую точку

### **Запрос:** `PUT` `/api/points/{pointId}`

**Тело запроса:**

Объект PointDto

```json
{
  "type": "Парк",
  "latitude": 53.227925,
  "longitude": 50.199265,
  "address": "парк культуры и отдыха имени Юрия Гагарина, Промышленный район, городской округ Самара",
  "locationData": {
    "name": "Парк культуры и отдыха имени Ю. А. Гагарина",
    "review": "Парк культуры и отдыха имени Ю. А. Гагарина — это популярное место для семейного отдыха в Самаре.",
    "attributions": {
      "opening_hours": "Круглосуточно",
      "website": "https://parki-samara.ru/park-im-yu-gagarina/"
    }
  }
}
```

### **Ответ:**

#### Возвращает обновленный объект PointDocument

**Code** : `200 OK`

```json
{
  "pointId": "67fb759bb3ae645634f42568",
  "type": "Парк",
  "latitude": 53.227925,
  "longitude": 50.199265,
  "address": "парк культуры и отдыха имени Юрия Гагарина, Промышленный район, городской округ Самара",
  "locationData": {
    "name": "Парк культуры и отдыха имени Ю. А. Гагарина",
    "review": "Парк культуры и отдыха имени Ю. А. Гагарина — это популярное место для семейного отдыха в Самаре.",
    "attributions": {
      "website": "https://parki-samara.ru/park-im-yu-gagarina/",
      "opening_hours": "Круглосуточно"
    }
  }
}
```

#### Точка с указанным ID не найдена

**Code** : `404 Not Found`

```json
{
  "message": "Point with id 67fb759bb3ae645634f4256 not found",
  "status": 404
}
```

#### Данные не прошли валидацию

**Code** : `400 Bad Request`

```json
{
  "message": "Type cannot be null",
  "status": 400
}
```

## Добавить новую точку

### **Запрос:** `POST` `/api/points`

**Тело запроса:**

Объект PointDto

```json
{
  "type": "Парк",
  "latitude": 53.227925,
  "longitude": 50.199265,
  "address": "парк культуры и отдыха имени Юрия Гагарина, Промышленный район, городской округ Самара",
  "locationData": {
    "name": "Парк культуры и отдыха имени Ю. А. Гагарина",
    "review": "Парк культуры и отдыха имени Ю. А. Гагарина — это популярное место для семейного отдыха в Самаре.",
    "attributions": {
      "website": "https://parki-samara.ru/park-im-yu-gagarina/",
      "opening_hours": "Круглосуточно"
    }
  }
}
```

### **Ответ:**

#### Возвращает созданный объект PointDocument

**Code** : `201 Created`

```json
{
  "pointId": "67fb759bb3ae645634f42568",
  "type": "Парк",
  "latitude": 53.227925,
  "longitude": 50.199265,
  "address": "парк культуры и отдыха имени Юрия Гагарина, Промышленный район, городской округ Самара",
  "locationData": {
    "name": "Парк культуры и отдыха имени Ю. А. Гагарина",
    "review": "Парк культуры и отдыха имени Ю. А. Гагарина — это популярное место для семейного отдыха в Самаре.",
    "attributions": {
      "website": "https://parki-samara.ru/park-im-yu-gagarina/",
      "opening_hours": "Круглосуточно"
    }
  }
}
```

#### Данные не прошли валидацию

**Code** : `400 Bad Request`

```json
{
  "message": "Type cannot be null",
  "status": 400
}
```

## Удалить точку по ID

### **Запрос:** `DELETE` `/api/points/{pointId}`

### **Ответ:**

#### Успешное удаление

**Code** : `204 No Content`

#### Точка с указанным ID не найдена

**Code** : `404 Not Found`

```json
{
  "message": "Point with id 67fb759bb3ae645634f4256 not found",
  "status": 404
}
```

---

## Получить маршруты текущего авторизованного пользователя

### **Запрос:** `GET` `/api/routes`

### **Ответ:**

#### Возвращает массив объектов RouteDocument

**Code** : `200 OK`

```json
[
  {
    "routeId": "67fb79ae88c2063bdf04b8f3",
    "userId": "9e9eca77-360e-4597-8429-87e7fc49a937",
    "pointsId": [
      "67fb759bb3ae645634f42567",
      "67fb759bb3ae645634f42568",
      "67fb759bb3ae645634f42569"
    ],
    "public": true
  },
  {
    "routeId": "67fb79ae88c2063bdf04b8f4",
    "userId": "9e9eca77-360e-4597-8429-87e7fc49a937",
    "pointsId": [
      "67fb759bb3ae645634f42568",
      "67fb759bb3ae645634f42569"
    ],
    "public": false
  },
  {
    "routeId": "67fb8a3ccbfebb283d8a1e84",
    "userId": "9e9eca77-360e-4597-8429-87e7fc49a937",
    "pointsId": [
      "67fb759bb3ae645634f42569"
    ],
    "public": false
  }
]
```

## Получить публичные маршруты указанного пользователя

### **Запрос:** `GET` `/api/routes/{userId}`

### **Ответ:**

#### Возвращает массив объектов RouteDocument

**Code** : `200 OK`

```json
[
  {
    "routeId": "67fb79ae88c2063bdf04b8f3",
    "userId": "9e9eca77-360e-4597-8429-87e7fc49a937",
    "pointsId": [
      "67fb759bb3ae645634f42567",
      "67fb759bb3ae645634f42568",
      "67fb759bb3ae645634f42569"
    ],
    "public": true
  }
]
```

## Создать новый маршрут

### **Запрос:** `POST` `/api/routes`

**Тело запроса:**

Объект RouteDto

```json
{
  "pointsId": [
    "67fb759bb3ae645634f42567",
    "67fb759bb3ae645634f42568",
    "67fb759bb3ae645634f42569"
  ],
  "public": true
}
```

### **Ответ:**

#### Возвращает созданный объект RouteDocument

**Code** : `201 Created`

```json
[
  {
    "routeId": "67fb79ae88c2063bdf04b8f3",
    "userId": "9e9eca77-360e-4597-8429-87e7fc49a937",
    "pointsId": [
      "67fb759bb3ae645634f42567",
      "67fb759bb3ae645634f42568",
      "67fb759bb3ae645634f42569"
    ],
    "public": true
  }
]
```

#### Данные не прошли валидацию

**Code** : `400 Bad Request`

```json
{
  "message": "Points list cannot be empty",
  "status": 400
}
```

## Обновить точки в конкретном маршруте

### **Запрос:** `PATCH` `/api/routes/{routeId}/points`

**Тело запроса:**

Объект RouteDto

```json
{
  "pointsId": [
    "67fb759bb3ae645634f42567",
    "67fb759bb3ae645634f42568",
    "67fb759bb3ae645634f42569"
  ]
}
```

### **Ответ:**

#### Возвращает обновленный объект RouteDocument

**Code** : `200 OK`

```json
[
  {
    "routeId": "67fb79ae88c2063bdf04b8f3",
    "userId": "9e9eca77-360e-4597-8429-87e7fc49a937",
    "pointsId": [
      "67fb759bb3ae645634f42567",
      "67fb759bb3ae645634f42568",
      "67fb759bb3ae645634f42569"
    ],
    "public": true
  }
]
```

#### Данные не прошли валидацию

**Code** : `400 Bad Request`

```json
{
  "message": "Points list cannot be empty",
  "status": 400
}
```

#### Маршрут с указанным ID не найден

**Code** : `404 Not Found`

```json
{
  "message": "Route 67fb79ae88c2063bdf04b8f not found",
  "status": 404
}
```

## Изменить видимость маршрута (публичный/приватный)

### **Запрос:** `PATCH` `/api/routes/{routeId}/visibility`

### **Ответ:**

#### Возвращает обновленный объект RouteDocument

**Code** : `200 OK`

```json
[
  {
    "routeId": "67fb79ae88c2063bdf04b8f3",
    "userId": "9e9eca77-360e-4597-8429-87e7fc49a937",
    "pointsId": [
      "67fb759bb3ae645634f42567",
      "67fb759bb3ae645634f42568",
      "67fb759bb3ae645634f42569"
    ],
    "public": true
  }
]
```

## Удалить маршрут по ID

### **Запрос:** `DELETE` `/api/routes/{routeId}`

### **Ответ:**

#### Успешное удаление

**Code** : `204 No Content`

#### Маршрут с указанным ID не найден

**Code** : `404 Not Found`

```json
{
  "message": "Route 67fb79ae88c2063bdf04b8f not found",
  "status": 404
}
```

---

# Documents

### `LocationData`

Содержит данные о местоположении точки маршрута

* `name` — строка (обязательно, не пустая)
* `review` — строка (необязательно)
* `attributions` — словарь {ключ: значение} (необязательно)

### `PointDocument`

Документ точки в базе данных

* `pointId` — строка (автоматически генерируется)
* `type` — строка (обязательно, не пустая)
* `latitude` — число (обязательно, от -90 до 90)
* `longitude` — число (обязательно, от -180 до 180)
* `address` — строка (необязательно)
* `locationData` — объект LocationData (необязательно, валидируется)

### `RouteDocument`

Документ маршрута в базе данных

* `routeId` — строка (автоматически генерируется)
* `userId` — строка (обязательно, не пустая)
* `pointsId` — список строк (обязательно, не пустой)
* `isPublic` — логическое значение (по умолчанию false)

---

# DTO

### `AuthDto`

* `login` — строка
* `password` — строка

### `PointDto`

* `type` — строка (обязательно)
* `latitude` — число (обязательно, от -90 до 90)
* `longitude` — число (обязательно, от -180 до 180)
* `address` — строка (необязательно)
* `locationData` — объект (необязательно)

### `RouteDto`

* `pointsId` — список ID точек (обязателен, не пустой)
* `isPublic` — логическое значение