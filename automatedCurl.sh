#!/bin/bash
a=1
while [ $a -le $1 ]
do
curl -X POST http://localhost:8080/transactions --header "Content-Type: application/json" -d "{\"amount\": 12.3,\"timestamp\": `date +%s%3N`}"
echo $a
a=`expr $a + 1`
sleep $2
#Cause the loop to wait for 10 Seconds
done

