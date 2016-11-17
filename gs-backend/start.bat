rem call pm2 start process.yml --env test-jacek

rem call pm2 show rest-api

call d:\MongoDB\Server\3.2\bin\mongod --dbpath ./mongodb/data

rem http://localhost:8081/ admin pass
call c:\Users\jacek\AppData\Roaming\npm\mongo-express.cmd 

pause;