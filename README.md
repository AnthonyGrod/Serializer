# Boolean Expression Transformer

This project is a simple library for serializing, deserializing 
and transforming boolean expressions.

## JSON Format for Boolean Expressions

Accepted JSON format for boolean expressions is inspired by json-logic-scala format 
(https://index.scala-lang.org/celadari/json-logic-scala). Moreover, the format has to
follow grammar given below:

```
JsonExpr        ::= JsonBinaryExp | JsonUnaryExpr | JsonLiteral | JsonVariable 
JsonBinaryExp   ::= { "type": BinaryOperator, "e1": JsonExpr, "e2": JsonExpr } 
JsonUnaryExpr   ::= { "type": UnaryOperator, "expression": JsonExpr } |
                    { "type": JsonLiteral }
JsonVariable    ::= { "type": "Variable", "name": String }
BinaryOperator  ::= "And" | "Or"
UnaryOperator   ::= "Not"
JsonLiteral     ::= "True" | "False"
```

Example valid JSON boolean expression:
```json
{
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
}
```

## Project Overview

The project is made of two main logical parts. Both of them are resilient to user mistakes 
and operate on boolean expressions given by Scala's ADT: 
```scala
sealed trait BooleanExpression
case object True                                             extends BooleanExpression
case object False                                            extends BooleanExpression
case class Variable(symbol: String)                          extends BooleanExpression
case class Not(e: BooleanExpression)                         extends BooleanExpression
case class Or(e1: BooleanExpression, e2: BooleanExpression)  extends BooleanExpression
case class And(e1: BooleanExpression, e2: BooleanExpression) extends BooleanExpression
```
### Serialization and Deserialization
First part of the project is responsible for serializng and deserializing boolean expressions.
Rules for serialization and deserialization are written with help of `spray-json` library 
in `utils.BooleanExpressionJsonProtocol`. The serialization and deserialization
itself are performed by `serializer.Serializer` and `serializer.Deserializer`.

### Transformation
Second part of the project is a DNF transformer for boolean expressions. It is implemented as
a HTTP service with help of `akka-http` library. The service is able to transform boolean
expressions to disjunctive normal form (DNF). The service itself is made of three key parts:
- `transformer.routers.TransformerRouter` - responsible for handling HTTP requests
- `transformer.services.TransformerService` - responsible for transforming boolean expressions
- `transformer.client.TransformerClient` - an interactive client for the service
`TransformerLoader` is responsible for providing the main application with routes.

### Usage

### Testing