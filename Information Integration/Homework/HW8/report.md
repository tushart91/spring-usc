## dataset_new.csv

### PyTransforms

### Semantic Types
| Column | Property | Class |
|  ----- | -------- | ----- |
| _artist - active_ | `dcterms:title` | `schema:Country2`|
| _artist - birthplace - country_ | `dcterms:title` | `schema:Country1`|
| _artist - birthplace - state_ | `schema:addressRegion` | `schema:PostalAddress1`|
| _artist - birthplace - town_ | `schema:addressLocality` | `schema:PostalAddress1`|
| _artist - dates - born_ | `schema:birthDate` | `schema:Person1`|
| _artist - dates - died_ | `schema:deathDate` | `schema:Person1`|
| _artist - link_ | `uri` | `schema:Person1`|
| _artist - name - first_ | `schema:givenName` | `schema:Person1`|
| _artist - name - last_ | `schema:familyName` | `schema:Person1`|
| _artist - name - middle_ | `schema:additionalName` | `schema:Person1`|
| _category_ | `dcterms:type` | `schema:Painting1`|
| _copyright_ | `dcterms:title` | `schema:Organization1`|
| _created_country_ | `dcterms:title` | `schema:Country3`|
| _created_year_ | `schema:dateCreated` | `schema:Painting1`|
| _dimensions - depth_ | `schema:value` | `schema:QuantitativeValue3`|
| _dimensions - height_ | `schema:value` | `schema:QuantitativeValue2`|
| _dimensions - unit_ | `schema:unitCode` | `schema:QuantitativeValue3`|
| _dimensions - width_ | `schema:value` | `schema:QuantitativeValue1`|
| _id_ | `dcterms:identifier` | `schema:Painting1`|
| _image_link_ | `schema:image` | `schema:Painting1`|
| _link_ | `uri` | `schema:Painting1`|
| _name_ | `dcterms:title` | `schema:Painting1`|


### Links
| From | Property | To |
|  --- | -------- | ---|
| `schema:Painting1` | `schema:author` | `schema:Person1`|
| `schema:Painting1` | `schema:contentLocation` | `schema:Place3`|
| `schema:Painting1` | `schema:copyrightHolder` | `schema:Organization1`|
| `schema:Painting1` | `schema:depth` | `schema:QuantitativeValue3`|
| `schema:Painting1` | `schema:height` | `schema:QuantitativeValue2`|
| `schema:Painting1` | `schema:width` | `schema:QuantitativeValue1`|
| `schema:Person1` | `schema:birthPlace` | `schema:Place1`|
| `schema:Person1` | `schema:workLocation` | `schema:Place2`|
| `schema:Place1` | `schema:address` | `schema:PostalAddress1`|
| `schema:Place2` | `schema:address` | `schema:PostalAddress2`|
| `schema:Place3` | `schema:address` | `schema:PostalAddress3`|
| `schema:PostalAddress1` | `schema:addressCountry` | `schema:Country1`|
| `schema:PostalAddress2` | `schema:addressCountry` | `schema:Country2`|
| `schema:PostalAddress3` | `schema:addressCountry` | `schema:Country3`|
