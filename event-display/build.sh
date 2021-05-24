#! /bin/sh

CONTRIBDIR=../../../eventing-contrib

rm -f main
rm -f $CONTRIBDIR/cmd/event_display/main

pushd $CONTRIBDIR/cmd/event_display
env GOOS=linux GOARCH=amd64 go build main.go
popd
mv $CONTRIBDIR/cmd/event_display/main .


