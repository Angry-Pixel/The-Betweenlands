## Online Picture Gallery

This branch hosts all #the-gallery entries and the index file.

### Format:

* ```versions```: Which gallery protocol version the index array is made for
* ```sha256```: SHA256 hash of the picture file. Used for integrity check, e.g. to redownload picture if it has been corrupted or changed
* ```url```: URL of the picture file
* ```title```: Title of the picture
* ```author```: Author of the picture
* ```source_url```: Optional source URL
* ```description```: Description of the picture

#### Example
```Json
[{
	"versions": ["1.0.0"],
	"index": [{
			"sha256": "34A12DA47565A0D1E891BCE6A1E19304A5B9CA02A27BCE8E2EB3B47CA60DF08C",
			"url": "https://raw.githubusercontent.com/Angry-Pixel/The-Betweenlands/online_picture_gallery/test1.png",
			"title": "Test Picture 1",
			"author": "TheCyberBrick",
			"source_url": "This will contain an URL",
			"description": "A test picture\nfor testing purposes"
		},
		{
			"sha256": "0BB9899C03E974D7628C585497817251E32426B8D446A9E8D1BD075225B12DE8",
			"url": "https://raw.githubusercontent.com/Angry-Pixel/The-Betweenlands/online_picture_gallery/test2.png",
			"title": "Test Picture 2",
			"author": "TheCyberBrick",
			"source_url": "This will contain an URL",
			"description": "A test picture to test flexible picture size"
		},
		{
			"sha256": "39A967F6A9D0C07CF44CB650DE5D7FCC9EE26B22BB55BEB13CB24ADC81E11ED2",
			"url": "https://raw.githubusercontent.com/Angry-Pixel/The-Betweenlands/online_picture_gallery/test3.png",
			"title": "Test Picture 3",
			"author": "TheCyberBrick",
			"source_url": "This will contain an URL",
			"description": "A test picture to test\ntransparency"
		}
	]
}]

```
