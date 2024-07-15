
### Cara Menjalankan Program

* Jalankan database migration tools
```bash
./mvnw clean flyway:migrate -Dflyway.configFiles=tools/db/migrations.conf
```


* Jalankan aplikasi
```bash
./mvnw spring-boot:run 
```

* Login admin terlebih dahulu untuk mendapatkan token
    - cara login dengan menginputkan :
    ```bash
    {
        "email":"charlie@example.com",
        "password":"12345678"
    }
    ```
    - Dengan menggunakan METHOD POST dan url:
    ```bash
    http://127.0.0.1:8080/login
    ```
* lakukan register seller untuk mendapatkan token seller
    - cara register seller dengan menginputkan:
    ```bash
    {
        "name":"song-khang",
        "email":"song@gmail.com",
        "password":"12345678"
    }
    ```
    - Tambahkan token dari role admin pada Auth
    - Gunakan METHOD POST dan url:
    ```bash
    http://127.0.0.1:8080/secured/user/register-seller
    ```
* lakukan register buyer untuk mendapatkan token buyer
    - cara register buyer dengan menginputkan:
    ```bash
    {
        "name":"hafizah",
        "email":"hafiz123@gmail.com",
        "password":"12345678"
    }
    ```
    - Tambahkan token dari role admin pada Auth
    - Gunakan METHOD POST dan url:
    ```bash
   http://127.0.0.1:8080/secured/user/register-buyer
    ```
* lalu login sebagai seller untuk mendapatkan token
    - cara login sebagai seller dengan menginputkan:
    ```bash
    {
        "email":"song@gmail.com",
        "password":"12345678"
    }
    ```
    - gunakan METHOD POST dan url:
    ```bash
     http://127.0.0.1:8080/login
    ```
* dan jika login sebagai buyer untuk mendapatkan token
    - cara login sebagai buyer dengan menginputkan:
    ```bash
    {
        "email":"hafiz123@gmail.com",
        "password":"12345678"
    }
    ```
    - gunakan METHOD POST dan url:
    ```bash
     http://127.0.0.1:8080/login
    ```
* lalu untuk mendapatkan list data pengguna, menggunakan role admin dengan METHOD GET dan url sebagai berikut:
  - http://127.0.0.1:8080/secured/user/list
  - lalu tambahkan token dari role admin pada Auth

* lalu pengguna dapat melakukan reset password dan mengambil token role masing - masing dan dimasukan pada Auth
  - cara reset password untuk pengguna dengan menginputkan:
    ```bash
    {
        "newPassword":"12345678"
    }
    ```
    - gunakan METHOD POST dan url:
    ```bash
     http://127.0.0.1:8080/secured/user/reset-password
    ```
* lalu pengguna dapat melakukan update profil dan mengambil token role masing - masing dan dimasukan pada Auth
  - cara update profile untuk pengguna dengan menginputkan:
    ```bash
    {
        "name":"song song"
    }
    ```
    - gunakan METHOD POST dan url:
    ```bash
    http://127.0.0.1:8080/secured/user/update-profile
    ```

 * lalu role admin dapat melakukan delete user
   - cara delete user dengan menginputkan :
    ```bash
    {
        "id":4
    }
    ```
    - gunakan METHOD DELETE dan url:
    ```bash
    http://127.0.0.1:8080/secured/user/delete-user
    ```

* lalu seller melakukan create auction dengan role seller
  - cara create auction dengan menginputkan:
    ```bash
    {
        "name": "shong-khong",
        "description": "testing aja",
        "minimumPrice": 1000,
        "startedAt": "2024-07-15T00:00:00Z",
        "endedAt": "2025-07-20T00:00:00Z"
    }
    ```
  - gunakan METHOD POST dan url:
    ```bash
    http://127.0.0.1:8080/secured/auction/create-auction
    ```

