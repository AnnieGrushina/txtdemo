#!/usr/bin/env sh

echo "List all messages:"
echo "=================="
echo "1. Server:"
sqlite3 TextSaver/ServerMessages.db "select * from messages;" ".exit"
for i in TextSaver/Client-*_Messages.db;
do
  echo
  echo "Client $i:"
  sqlite3 $i "select * from messages order by timestamp;" ".exit"
done

echo
echo "Count messages:"
echo "==============="
echo "Server:" `sqlite3 TextSaver/ServerMessages.db "select count(*) from messages;" ".exit"`
total_client_delivered=0
for i in TextSaver/Client-*_Messages.db;
do
  delivered=`sqlite3 $i "select count(*) from messages where delivered=true;" ".exit"`
  not_delivered=`sqlite3 $i "select count(*) from messages where delivered=false;" ".exit"`
  total_client_delivered=$((total_client_delivered+delivered))

  echo "Client $i delivered: $delivered"
  echo "Client $i not delivered: $not_delivered"
  sqlite3 $i "select * from messages where delivered=false order by timestamp;" ".exit"
  echo
done
echo "Total delivered on clients: $total_client_delivered"
echo "Some messages may exist in server db, but marked as not delivered on client."