## Overrides for Environment Events

### Format:

```
[
  {
    "versions": [
      "<mod version string>",
      ...
    ],
    "overrides": [
      {
        "dimensions": ["<dimension name>", ...] (Optional field),
        "id": "<event id>",
        "value": <true|false>,
        "remote_reset_ticks": <int> (Optional field),
        "<additional data key>": <additional data>,
        ...
      },
      ...
    ]
  },
  ...
]
```

* ```versions```: Which mod versions should pull the states from that entry. Can be used for compatibility
* ```overrides```: Specifies the environment event overrides
* ```dimensions```: The dimensions in which this override is valid. If an empty array is specified the override is valid for any dimension. If no value is specified the override is only valid for the Betweenlands dimension
* ```id```: The environment event ID (ResourceLocation)
* ```value```: The active state of the event
* ```remote_reset_ticks```: Optional active state reset timeout ticks. If the client/server fails to pull the data more than two  
times the reset timeout ticks will start to count down. Once it runs out the events reset their state to their default values. Default timeout ticks value is 3600 (3 min.).
* Additional data can be specified in the overrides that the specified event can read.

All arrays can have any number of entries.

#### Example

```Json
[
  {
    "versions": [
      "3.0.0",
      "3.1.0",
      "3.2.0"
    ],
    "overrides": [
      {
        "id": "thebetweenlands:spook",
        "value": true
      },
      {
        "id": "thebetweenlands:winter",
        "value": true,
        "some_additional_data": {
          "some_additional_value": "hello"
        }
      }
    ]
  },
  {
    "versions": [
      "2.0.0"
    ],
    "overrides": [
      {
        "id": "thebetweenlands:dense_fog",
        "value": true
      }
    ]
  }
]
```
