# Analysis of Mapping OJP-XML to OJP-JSON

## Approach

Mapping of XML to JSON:

- `OJPPlaceInformation`to `/ojp-places` (or `/ojp-place-information`)
- `OJPTripInfo` to `/ojp-trips`

While the mapping feels quite straight forward, however some design
decision need to be taken. Thoughts on topics identified are described
below:

## Topics

- Mapping of `xs:complexType` elements with `xs:choice` *and* `xs:elements`, e.g.:

  ```xsd
    <xs:complexType name="TripLegStructure">
        <xs:annotation>
        <xs:documentation>
            a single stage of a TRIP that is made without change of MODE or service (ie: between each interchange)
        </xs:documentation>
        </xs:annotation>
        <xs:sequence>
        <xs:element name="LegId" type="xs:NMTOKEN">
            <xs:annotation>
            <xs:documentation>Id of this trip leg. Unique within trip result.</xs:documentation>
            </xs:annotation>
        </xs:element>
        <xs:element name="ParticipantRef" type="siri:ParticipantRefStructure" minOccurs="0">
            <xs:annotation>
            <xs:documentation>[equivalent of PARTICIPANT in SIRI] IT system that is participating in a communication with other participant(s)</xs:documentation>
            </xs:annotation>
        </xs:element>
        <xs:choice>
            <xs:annotation>
            <xs:documentation>Choice for the type of the trip leg.</xs:documentation>
            </xs:annotation>
            <xs:element name="TimedLeg" type="TimedLegStructure"/>
            <xs:element name="TransferLeg" type="TransferLegStructure"/>
            <xs:element name="ContinuousLeg" type="ContinuousLegStructure"/>
        </xs:choice>
        </xs:sequence>
    </xs:complexType>
  ```

  A straight forward mapping of XSD to YAML is:

  ```yaml
      TripLeg:
      description: trip leg
      required:
        - legs
      type: object
      properties:
        legId:
          description: Id of this trip leg. Unique within trip result.
          type: string
          format: uuid
        participantRef:
          description: IT system that is participating in a communication with other participant(s)
          type: string
        legs:
          type: array
          items:
            anyOf:
              - $ref: "#/components/schemas/TimedLeg"
              - $ref: "#/components/schemas/TransferLeg"
              - $ref: "#/components/schemas/ContinuousLeg"
  ```
  
  which leads to the following JSON:

  ```json
  {
      "legId": "a legId",
      "part": "participantRef",
      "legs:"
        [
            {"..."}
        ]
  }
  ```

  But, this leads to a `legs` attribute not part of the `XSD` specification.
  
  *Todo*: Determine whether this is a problem.

  The simplest approach would be to redesign the `XSD` accordingly.

- Granularity of resources
  
  - *Todo*
  
- Mapping of `Parameter`
  
  - Option 1: as `Parameter` in `body`

     `GET`with a body is undefined, so we would need a `POST`.

  - Option 2: as `parameter` of a `query`, e.g.,:

    ```yml
    numberOfResults:
      name: numberOfResults
      in: query
      description: >-
        The desired number of trip results before the given time (at origin or destination).
      required: true
      style: simple
      explode: false
      schema:
        type: string
   ```
   More lightweight and more REST, however can lead to many queries or even not doable in case of deeply nested structures.

- Modelling of `Extension`:

  - Option 1: as `Extension` in `body`
  - Option 2: as optional attributes, "ext:param"

- Modelling of `Attribute`:
  
  - Option 1: as `Attribute` in `Body`
  - Option 2: as optional attributes
