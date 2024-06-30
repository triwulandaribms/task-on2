
* Ubah di application.yml
  * /home/unknown/Documents/sk/tools/db/data/sk.db disesuaikan dengan di komputer masing-masing
* Jalankan perintah
```bash
mvn clean flyway:migrate -Dflyway.configFiles=tools/migrations/migrations.conf
```
* Jalankan aplikasi
```bash
mvn spring-boot:run 
```