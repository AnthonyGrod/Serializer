# Boolean Expression Transformer

This project is a simple library for serializing, deserializing 
and transforming boolean expressions.

## JSON Format for Boolean Expressions

Accepted JSON format for boolean expressions is inspired by json-logic-scala format 
(https://index.scala-lang.org/celadari/json-logic-scala). Moreover, the format has to
follow the grammar given below:

```ebnf
JsonExpr        ::= JsonBinaryExp | JsonUnaryExpr | JsonLiteral | JsonVariable 
JsonBinaryExp   ::= { "type": BinaryOperator, "e1": JsonExpr, "e2": JsonExpr } 
JsonUnaryExpr   ::= { "type": UnaryOperator, "expression": JsonExpr } |
                    { "type": JsonLiteral }
JsonVariable    ::= { "type": "Variable", "symbol": String }
BinaryOperator  ::= "And" | "Or"
UnaryOperator   ::= "Not"
JsonLiteral     ::= "True" | "False"
```

Order of the operator inside binary expressions does not matter - for example
```json
{ "type": "And", "e1": { "type": "False" }, "e2": { "type": "True" } }
``` 
is equivalent to
```json
{ "e1": { "type": "False" }, "e2": { "type": "True" }, "type": "And" }
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
an HTTP service with help of the `akka-http` library. The service is able to transform boolean
expressions to disjunctive normal form (DNF). The service itself is made of three key parts:
- `transformer.routers.TransformerRouter` - responsible for handling HTTP requests
- `transformer.services.TransformerService` - responsible for transforming boolean expressions
- `transformer.server.Server` - server that runs the service
- `transformer.client.TransformerClient` - an interactive client that connects to the server

### Usage
#### Running transformations:
Open two terminal windows and type the following commands:
```shell
sbt "runMain server.Server"
```
```shell
sbt "runMain client.Client"
```

The client will ask you to provide a boolean expression in our JSON format. After providing the
expression and pressing Enter twice, the client will send it to the server and print the 
response in a user-friendly manner (you can check it out with te boolean expression in JSON 
format from the beginning of the `README`). The response will be a boolean expression in DNF. 
If the expression is invalid, the server will respond with an error message.

#### Using serialization and deserialization:
The project provides `Serializer` and `Deserializer` objects that expose, respectively,
```scala
serialize(expression: BooleanExpression): String
``` 
and
```scala 
deserialize(json: String): Either[String, BooleanExpression]
```
methods. Use cases are shown in tests in `serializer.SerializerSpec`


### Testing
The project is covered with unit tests. They test valid as well as invalid usage.
In order to run tests, type:
```shell
sbt test
```