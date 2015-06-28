## GeoCode Processor

Simple processor to convert addresses to Lat/Lon.  Processor uses;
```
http://nominatim.openstreetmap.org/search?q=
```

Expects addresses in the following format:
```
Al Fahidi St, Bur Dubai, Dubai - UAE
```

and will return
```
{
	"address":"Al Fahidi St, Bur Dubai, Dubai - UAE",
	"latitude":"25.2582122",
	"updated_date":1435501523479,
	"longitude":"55.2974774",
	"status":"ok"
}
```

