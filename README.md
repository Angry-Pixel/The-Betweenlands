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
        "id": "<event id>",
        "value": <true|false>,
        "remote_reset_ticks": <int> (Optional field)
      },
      ...
    ]
  },
  ...
]
```

* ```versions```: Which mod versions should pull the states from that entry. Can be used for compatibility
* ```overrides```: Specifies the environment event overrides
* ```id```: The environment event ID (ResourceLocation)
* ```value```: The active state of the event
* ```remote_reset_ticks```: Optional active state reset timeout ticks. If the client/server doesn't pull the data and after  
the timeout ticks run out the event resets its state to its default value. Default timeout ticks value is 9600 (8 min.). Should  
be longer than the check interval of 6000 ticks (5 min.)

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
        "value": true
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
