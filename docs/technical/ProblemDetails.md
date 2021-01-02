# REST Problem Detail

Couch2r used RFC 7087 ProblemDetails to inform clients about errors or invalid requests. Each ProblemDetail response
(within this document only "response") has the following skeleton:

```
{
    type: "urn:problem-type:{PROBLEM_TYPE}"
    title: "...",
    detail: "...", // Generic message or throwable message,
    status: 200 // or any other HTTP status code,
    instance: "urn:uuid:{RANDOM_UUID}" // can be used to find problem in logs
}
```

There may appear further informative attributes. This depends on the implementation and problem scenario/type.

# Types used in Couch2r

|type|status|additional attributes (not mandatory)|explanation|
|---|---|---|---|
| urn:problem-type:couch2r-unknown-problem | 500 |  | Returned if an error which can not be classified or handled occurred. |
| urn:problem-type:couch2r-too-many-path-variables | 400 |  | Returned if there are too many path variables (e.g. "/couch2r/entity/1 **/tooMuch/vars**") |
| urn:problem-type:couch2r-wrong-id-type | 400 |  | Returned if the ID type does not match the resource ID's type.  |
| urn:problem-type:couch2r-id-parsing-not-supported | 500 | `unsupportedType` | The Couch2r ID parser does not support parsing ID of entity's type. |
| urn:problem-type:couch2r-not-found | 404 |  | The requested mapping does not exist or the action on the resource is not available / was not found. |
| urn:problem-type:couch2r-invalid-data | 400 |  | Occurred most probable during a save/update (POST) action. The data can not be deserialized. |
|  |  |  |  |


