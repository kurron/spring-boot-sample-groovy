Feature: Hash ID Generation
  In order to identify cards between systems,
  the service will calculate a unique fingerprint for each card.

  Background:
    Given a valid request
    And an X-Correlation-Id header filled in with a unique request identifier
    And an Accept header filled in with the MIME type of the hypermedia control
    And a Content-Type header filled in with the media-type of the hypermedia control

  @required
  Scenario: Successful Fingerprint Generation
    When a PUT request is made to the resource
    Then a response with a 200 HTTP status code is returned
    And the hypermedia control contains the fingerprint
