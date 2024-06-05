Accepted JSON format for boolean expressions is based on json-logic-scala format (https://index.scala-lang.org/celadari/json-logic-scala).
I am using only their format for simplicity but the evaluator itself is implemented from scratch, I wanted to have some fun(:
Example valid JSON boolean expression:
```json
{
    "and": [{
            "==": [
                    true,
                    {"var": "valA"}
                  ]
        },
        {
            "!=": [
                    {"var": "colB"},
                    {"var": "colC", "type"}
                  ]
        }
        ]
}
```