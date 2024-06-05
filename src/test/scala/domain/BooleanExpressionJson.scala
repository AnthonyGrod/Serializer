package domain

object BooleanExpressionJson {
  val jsonSimple =
    """{
            "type": "And",
            "e1": {
              "type": "False"
            },
            "e2": {
              "type": "Or",
              "e1": {
              "type": "Variable",
              "symbol": "x"
            },
              "e2": {
              "type": "True"
            }
            }
          }"""

  val jsonSimpleResponseDNF =
    """{
              "type": "Or",
              "e1": {
                "type": "And",
                "e1": {
                  "type": "Variable",
                  "symbol": "x"
                },
                "e2": {
                  "type": "False"
                }
              },
              "e2": {
                "type": "And",
                "e1": {
                  "type": "True"
                },
                "e2": {
                  "type": "False"
                }
              }
            }"""


  val jsonComplex =
    """{
          "type": "Or",
          "e1": {
            "type": "And",
            "e1": {"type": "Variable", "symbol": "colA"},
            "e2": {"type": "Not", "expression": {"type": "Variable", "symbol": "colB"}}
          },
          "e2": {
            "type": "And",
            "e1": {"type": "Variable", "symbol": "valA"},
            "e2": {"type": "Not", "expression": {"type": "Variable", "symbol": "colB"}}
          }
        }"""

  val jsonComplexResponseDNF =
    """
          {
            "type": "Or",
            "e1": {
              "type": "And",
              "e1": {"type": "Variable", "symbol": "colA"},
              "e2": {"type": "Not", "expression": {"type": "Variable", "symbol": "colB"}}
            },
            "e2": {
              "type": "And",
              "e1": {"type": "Variable", "symbol": "valA"},
              "e2": {"type": "Not", "expression": {"type": "Variable", "symbol": "colB"}}
            }
          }
          """

  val jsonMissingField =
    """{
    "type": "Variable"
  }"""

  val jsonIncorrectFieldName =
    """{
    "type": "Variable",
    "symbl": "colA"
  }"""

  val jsonUnknownType =
    """{
    "type": "UnknownType"
  }"""

  val jsonIncompleteNot =
    """{
    "type": "Not"
  }"""


  val jsonMalformed =
    """{
    "type": "Or",
    "e1": {"type": "Variable", "symbol": "colA",
    "e2": {"type": "Variable", "symbol": "colB"}
  }"""

  val emptyJson = "{}"
}
