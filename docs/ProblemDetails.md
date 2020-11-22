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

|type|status|additional attributes|explanation|
|---|---|---|---|
| couch2r-unknown-problem | 500 |  | Returned if an error which can not be classified or handled occurred. |
| couch2r-too-many-path-variables | 400 |  | Returned if there are too many path variables (e.g. "/couch2r/entity/1 **/tooMuch/vars**") |
| couch2r-wrong-id-type | 400 |  | Returned if the ID type does not match the resource ID's type.  |
|  |  |  |  |
