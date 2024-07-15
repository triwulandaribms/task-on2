
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
    - Gunakan METHOD POST dan url:
    ```bash
    http://127.0.0.1:8080/login
    ```
* Lakukan register seller untuk mendapatkan token seller
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
* Lakukan register buyer untuk mendapatkan token buyer
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
* Lalu jika ingin login sebagai seller supaya mendapatkan token, berikut dibawah ini
    - cara login sebagai seller dengan menginputkan:
    ```bash
    {
        "email":"song@gmail.com",
        "password":"12345678"
    }
    ```
    - Gunakan METHOD POST dan url:
    ```bash
     http://127.0.0.1:8080/login
    ```
* Lalu jika ingin login sebagai buyer supaya mendapatkan token, berikut dibawah ini
    - cara login sebagai buyer dengan menginputkan:
    ```bash
    {
        "email":"hafiz123@gmail.com",
        "password":"12345678"
    }
    ```
    - Gunakan METHOD POST dan url:
    ```bash
     http://127.0.0.1:8080/login
    ```
* Lalu jika ingin mendapatkan list data pengguna, gunakan role admin dengan METHOD GET dan url sebagai berikut:
  - http://127.0.0.1:8080/secured/user/list
  - lalu tambahkan token dari role admin pada Auth

* Lalu pengguna dapat melakukan reset password dan mengambil token role masing - masing dan dimasukan pada Auth
  - Cara reset password untuk pengguna dengan menginputkan:
    ```bash
    {
        "newPassword":"12345678"
    }
    ```
  - Gunakan METHOD POST dan url:
    ```bash
     http://127.0.0.1:8080/secured/user/reset-password
    ```
* Lalu pengguna dapat melakukan update profil dan mengambil token role masing - masing dan dimasukan pada Auth
  - Cara update profile untuk pengguna dengan menginputkan:
    ```bash
    {
        "name":"song song"
    }
    ```
    - Gunakan METHOD POST dan url:
    ```bash
    http://127.0.0.1:8080/secured/user/update-profile
    ```

 * Lalu role admin dapat melakukan delete user
   - Cara delete user dengan menginputkan :
    ```bash
    {
        "id":4
    }
    ```
    - Gunakan METHOD DELETE dan url:
    ```bash
    http://127.0.0.1:8080/secured/user/delete-user
    ```

* Selanjutnya seller melakukan create auction dengan role seller guna untuk data pelelangan
  - Cara create auction dengan menginputkan:
    ```bash
    {
        "name": "shong-khong",
        "description": "testing aja",
        "minimumPrice": 1000,
        "startedAt": "2024-07-15T00:00:00Z",
        "endedAt": "2025-07-20T00:00:00Z"
    }
    ```
  - Gunakan METHOD POST dan url:
    ```bash
    http://127.0.0.1:8080/secured/auction/create-auction
    ```

* Lalu role admin dapat melakukan list auction 
  - Gunakan METHOD GET dan url sebagai berikut :
  - url dibawah untuk GET mendapatkan status approved
    ```bash
    http://127.0.0.1:8080/secured/auction/list-auction
    ```
    - url dibawah untuk GET mendapatkan status rejected 
    ```bash
    http://127.0.0.1:8080/secured/auction/list-auction?status=REJECTED
    ```
    - url dibawah untuk GET mendapatkan status waiting for approval 
    ```bash
    http://127.0.0.1:8080/secured/auction/list-auction?status=WAITING_FOR_APPROVAL
    ```
* Selanjutnya role admin melakukan approve untuk memulai pelelangan
  - Sebelum melakukan approve admin harus login terlebih dahulu untuk mendapatkan token
  - Lalu token dimasukkan pada Auth 
  - Gunakan METHOD POST dan url:
     ```bash
        http://127.0.0.1:8080/secured/auction/6/approve
    ```
  - Di dalam url masukan id pengguna yang akan di approve
  - Ketika akan approve, id pengguna yang dipilih harus berstatus waiting for approval


* Lalu role admin dapat juga melakukan reject untuk penolakan saat melalukan pelelangan
  - Sebelum melakukan reject admin harus login terlebih dahulu untuk mendapatkan token
  - Lalu token dimasukkan pada Auth
  - Gunakan METHOD POST dan url:
     ```bash
        http://127.0.0.1:8080/secured/auction/reject/7
    ```
  - Di dalam url masukan id pengguna yang akan di reject
  - Ketika akan reject, id pengguna yang dipilih harus berstatus waiting for approval

* Lalu role buyer dapat melakukan bidding 
  - Gunakan METHOD POST dan url :
    ```bash
    http://127.0.0.1:8080/secured/auction/create-bid
    ```
  - cara membuat bidding dengan menginputkan 
    ```bash
    {
      "auctionId":6,
      "bid":15000000
    }
    ```


